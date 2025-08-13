package com.zifo.ewb.webclient.service;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zifo.ewb.exceptions.InvalidTokenRequestException;
import com.zifo.gsk.barcodegeneration.constants.MessageConstants;
import com.zifo.gsk.barcodegeneration.constants.PropConstants;
import com.zifo.gsk.barcodegeneration.constants.ServiceConstants;

import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

/**
 * Class that provide method for executing token request using WebClient
 */
@Service
public class AccessTokenWebClientService {

	/**
	 * Logger instance to write logs
	 */
	private static final Logger LOGGER = LogManager.getLogger(AccessTokenWebClientService.class);

	private final WebClient webClient;

	@Value("${tokenrequesturl}")
	private String tokenRequestURL;

	@Value("${grantType}")
	private String grantType;

	@Value("${redirectUrl}")
	private String redirectURI;

	@Value("${clientID}")
	private String clientID;

	/**
	 * Constructor that gets the required fields from .properties file and builds
	 * the WebClient for executing token request
	 * 
	 * @param scheme
	 * @param host
	 * @param port
	 * @param serviceBase
	 * @param password
	 */
	public AccessTokenWebClientService(@Value("${scheme}") final String scheme, @Value("${host}") final String host,
			@Value("${port}") final String port, @Value("${servicebase}") final String serviceBase,
			@Value("${authorization}") final String password,
			@Value("${connection.maxConnections}") final int maxConnections,
			@Value("${connection.durationInSeconds}") final int durationInSeconds,
			@Value("${connection.durationInMin}") final int durationInMin,
			@Value("${connection.durationInMillis}") final int durationInMillis) {

		// Builds the EWB base URL from properties
		String baseUrl = UriComponentsBuilder.newInstance().scheme(scheme).host(host).port(port).path(serviceBase)
				.build().toUriString();
		LOGGER.debug("Token Base URL {}", baseUrl);

		ConnectionProvider provider = ConnectionProvider.builder("EWB-Access-conn-provider")
				.maxConnections(maxConnections).maxIdleTime(Duration.ofSeconds(durationInSeconds))
				.maxLifeTime(Duration.ofMinutes(durationInMin))
				.pendingAcquireTimeout(Duration.ofSeconds(durationInSeconds))
				.evictInBackground(Duration.ofSeconds(durationInSeconds)).build();

		HttpClient httpClient = HttpClient.create(provider)
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, durationInMillis)
				.doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(durationInSeconds))
						.addHandlerLast(new WriteTimeoutHandler(durationInSeconds)))
				.responseTimeout(Duration.ofSeconds(durationInSeconds))
				.wiretap("reactor.netty.http.client.HttpClient", LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);

		// Builds the webclient with the base URL and the default headers
		webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).baseUrl(baseUrl)
				.defaultHeaders(headers -> {
					headers.set(ServiceConstants.AUTHORIZATION, ServiceConstants.BASIC + password);
					headers.set(ServiceConstants.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
					headers.set(ServiceConstants.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
				}).exchangeStrategies(ExchangeStrategies.builder() // Increasing size of the response accepted to 16MB
						.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)).build())
				.build();
	}

	/**
	 * Method that uses access code from IDBS to get the access token from token
	 * endpoint
	 * 
	 * @param accessCode - The access code from request JSON
	 * @return accessToken for processing
	 * @throws InvalidTokenRequestException - if the response is not success
	 *                                      (WebClientResponseException)
	 */

	public final JsonNode executeTokenRequest(final String accessCode) throws InvalidTokenRequestException {

		LOGGER.debug("Token Generation started");
		try {
			// Executes the request using WebClient
			ResponseEntity<JsonNode> response = webClient.post()
					.uri(uriBuilder -> uriBuilder.path(tokenRequestURL)
							.queryParam(PropConstants.REDIRECT_URI, redirectURI)
							.queryParam(PropConstants.CLIENT_ID, clientID)
							.queryParam(PropConstants.GRANT_TYPE, grantType)
							.queryParam(ServiceConstants.CODE, accessCode).build())
					.retrieve().toEntity(JsonNode.class).block();

			// Returning response if it has body, if not, returns a empty JSON Node
			return (response != null && response.hasBody()) ? response.getBody()
					: new ObjectMapper().createObjectNode();
		}
		// If the if the request is not success
		catch (WebClientResponseException webClientResponseException) {
			LOGGER.error(MessageConstants.getWebClientAPIErrorLog(MessageConstants.TOKEN_GENERATION_EWB_FAIL,
					webClientResponseException));
			throw new InvalidTokenRequestException(MessageConstants.TOKEN_GENERATION_EWB_FAIL,
					webClientResponseException);
		}
	}
}
