package com.zifo.ewb.webclient.service;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.zifo.ewb.exceptions.InvalidRequestException;
import com.zifo.gsk.barcodegeneration.constants.JSONConstants;
import com.zifo.gsk.barcodegeneration.constants.MessageConstants;
import com.zifo.gsk.barcodegeneration.constants.ServiceConstants;

import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

/**
 * The SpreadSheet Service is responsible for managing all non-cached entity
 * related requests
 * 
 * @author zifo
 */
@Service
public class SpreadSheetService {

	/**
	 * Logger instance to write logs
	 */
	private static final Logger LOGGER = LogManager.getLogger(SpreadSheetService.class);

	/**
	 * WebClient object to execute the API requests
	 */
	private final WebClient webClient;

	/**
	 * Constructor that gets the required fields from .properties file and builds
	 * the WebClient for executing request
	 * 
	 * @param scheme
	 * @param host
	 * @param port
	 * @param serviceBase
	 */
	public SpreadSheetService(@Value("${scheme}") final String scheme, @Value("${host}") final String host,
			@Value("${port}") final String port, @Value("${servicebase}") final String serviceBase,@Value("${connection.maxConnections}") final int maxConnections,
			@Value("${connection.durationInSeconds}") final int durationInSeconds,
			@Value("${connection.durationInMin}") final int durationInMin,
			@Value("${connection.durationInMillis}") final int durationInMillis) {
		// Builds the EWB base URL from properties
		String baseUrl = UriComponentsBuilder.newInstance().scheme(scheme).host(host).port(port).path(serviceBase)
				.build().toUriString();
		LOGGER.debug("Base URL {}", baseUrl);

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

		webClient = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).baseUrl(baseUrl)
				.defaultHeader(ServiceConstants.CONTENT_TYPE, ServiceConstants.CONTENT_TYPE_VALUE)
				.exchangeStrategies(ExchangeStrategies.builder() // Increasing size of the response accepted to 16MB
						.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)).build())
				.build();
	}

	/**
	 * Method that handles the get data API of spreadsheet
	 * 
	 * @param versionId
	 * @param inputJson
	 * @param accessToken
	 * @return JSON response
	 * @throws InvalidRequestException
	 */
	public final JsonNode getData(final String versionId, final String inputJson, final String accessToken)
			throws InvalidRequestException {

		try {
			// Executes the request using WebClient
			ResponseEntity<JsonNode> response = webClient.post()
					.uri(uriBuilder -> uriBuilder.path(ServiceConstants.SPREADSHEET_DATA)
							.queryParam(JSONConstants.VERSION_ID, versionId).build())
					.header(ServiceConstants.AUTHORIZATION, ServiceConstants.BEARER + accessToken).bodyValue(inputJson)
					.retrieve().toEntity(JsonNode.class).block();

			// Returning response if it has body and empty JSON if it is null or empty
			return checkResponseStatus(response);
		}
		// If the if the request is not success
		catch (WebClientResponseException webClientResponseException) {
			LOGGER.error(MessageConstants.getWebClientAPIErrorLog(MessageConstants.UNABLE_TO_READ_WRITE_TABLE_DATA,
					webClientResponseException));
			throw new InvalidRequestException(MessageConstants.UNABLE_TO_READ_WRITE_TABLE_DATA,
					webClientResponseException);
		}
	}

	/**
	 * Checks if the provided HTTP status code string represents a successful (2xx)
	 * response.
	 * 
	 * @param response         which contains status String
	 * @param exceptionMessage
	 * @return JsonNode response Body
	 * @throws InvalidRequestException if it's not a 2xx status
	 */
	private JsonNode checkResponseStatus(ResponseEntity<JsonNode> response) throws InvalidRequestException {
		if (response == null || !response.hasBody()) {
			throw new InvalidRequestException(MessageConstants.NULL_RESPONSE);
		}
		JsonNode body = response.getBody();
		if (body == null || body.isEmpty()) {
			throw new InvalidRequestException(MessageConstants.NULL_OR_EMPTY_RESPONSE_BODY);
		}

		JsonNode statusCodeNode = body.path(JSONConstants.BATCH_RESPONSE).path(JSONConstants.STATUS)
				.path(JSONConstants.STATUS_CODE);

		HttpStatusCode statusCode = HttpStatusCode.valueOf(statusCodeNode.asInt());

		if (!statusCode.is2xxSuccessful()) {
			throw new InvalidRequestException(MessageConstants.UNABLE_TO_READ_WRITE_TABLE_DATA);
		}
		return body;
	}

	/**
	 * Checks whether the given JSON response carries a 200 (OK) or 201 (Created)
	 * status code.
	 *
	 * @param response the parsed JSON containing BATCH_RESPONSE → STATUS →
	 *                 STATUS_CODE
	 * @return true if status code is 200 or 201; false otherwise (including
	 *         missing/invalid JSON)
	 */
	public boolean is200or201(JsonNode response) {
		JsonNode codeNode = response.path(JSONConstants.BATCH_RESPONSE).path(JSONConstants.STATUS)
				.path(JSONConstants.STATUS_CODE);

		if (!codeNode.isInt()) {
			return Boolean.FALSE;
		}

		int code = codeNode.asInt();
		return code == HttpStatus.OK.value() || code == HttpStatus.CREATED.value();
	}
}
