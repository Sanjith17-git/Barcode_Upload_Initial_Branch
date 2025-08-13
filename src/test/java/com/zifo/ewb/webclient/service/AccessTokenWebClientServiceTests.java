package com.zifo.ewb.webclient.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;

import java.net.URI;
import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.util.UriBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.zifo.ewb.exceptions.InvalidTokenRequestException;
import com.zifo.ewb.testPropertiesHelper.AccessTokenTestProperties;
import com.zifo.ewb.testPropertiesHelper.TestProperties;
import com.zifo.gsk.barcodegeneration.constants.JSONConstants;
import com.zifo.gsk.barcodegeneration.constants.MessageConstants;

import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@DisplayName("Access Token WebClient Service tests")
class AccessTokenWebClientServiceTests {
	@Mock
	private WebClient webClient;

	@Mock
	private RequestBodyUriSpec requestBodyUriSpec;

	@Mock
	private RequestBodySpec requestBodySpec;

	@Mock
	private WebClient.ResponseSpec responseSpec;

	@Mock
	private Mono<ResponseEntity<JsonNode>> responseEntity;

	private AccessTokenWebClientService accessTokenWebClientService;

	@BeforeEach
	void setUp() {
		accessTokenWebClientService = new AccessTokenWebClientService(AccessTokenTestProperties.TEST_SCHEME,
				AccessTokenTestProperties.TEST_HOST, AccessTokenTestProperties.TEST_PORT,
				AccessTokenTestProperties.TEST_SERVICE_BASE, AccessTokenTestProperties.TEST_AUTHORIZATION, 10, 10, 10,
				10);
		ReflectionTestUtils.setField(accessTokenWebClientService, "webClient", webClient);
		ReflectionTestUtils.setField(accessTokenWebClientService, "webClient", webClient);
		ReflectionTestUtils.setField(accessTokenWebClientService, "tokenRequestURL",
				AccessTokenTestProperties.TEST_TOKEN_REQUEST_URL);
		ReflectionTestUtils.setField(accessTokenWebClientService, "grantType",
				AccessTokenTestProperties.TEST_GRANT_TYPE);
		ReflectionTestUtils.setField(accessTokenWebClientService, "redirectURI",
				AccessTokenTestProperties.TEST_REDIRECT_URI);
		ReflectionTestUtils.setField(accessTokenWebClientService, "clientID", AccessTokenTestProperties.TEST_CLIENT_ID);
	}

	@Test
	@DisplayName("Method to test the success response from executeTokenRequest method")
	void testExecuteTokenRequest_Success() throws InvalidTokenRequestException {

		// Mock successful response
		doReturn(requestBodyUriSpec).when(webClient).post();
		doReturn(requestBodySpec).when(requestBodyUriSpec).uri(Mockito.<Function<UriBuilder, URI>>any());
		doReturn(responseSpec).when(requestBodySpec).retrieve();
		doReturn(Mono.just(AccessTokenTestProperties.TEST_EWB_ACCESS_TOKEN_RESPONSE_ENTITY)).when(responseSpec)
				.toEntity((JsonNode.class));

		// Call method
		JsonNode response = accessTokenWebClientService.executeTokenRequest(AccessTokenTestProperties.TEST_ACCESS_CODE);

		// Validate response
		assertEquals(AccessTokenTestProperties.TEST_EWB_ACCESS_TOKEN, response.get("access_token").asText());

		InOrder inOrder = inOrder(webClient, requestBodyUriSpec, requestBodySpec, responseSpec);
		inOrder.verify(webClient).post();
		inOrder.verify(requestBodyUriSpec).uri(Mockito.<Function<UriBuilder, URI>>any());
		inOrder.verify(requestBodySpec).retrieve();
		inOrder.verify(responseSpec).toEntity((JsonNode.class));
	}

	@Test
	@DisplayName("Method to test the InvalidTokenRequestException from executeTokenRequest method")
	void testExecuteTokenRequest_InvalidTokenRequestException() {

		doReturn(requestBodyUriSpec).when(webClient).post();
		doReturn(requestBodySpec).when(requestBodyUriSpec).uri(Mockito.<Function<UriBuilder, URI>>any());
		doReturn(responseSpec).when(requestBodySpec).retrieve();
		doThrow(TestProperties.MOCK_WEB_CLIENT_RESP_EXP_401).when(responseSpec).toEntity(JsonNode.class);

		// Call method and expect exception
		InvalidTokenRequestException invalidTokenRequestException = assertThrows(InvalidTokenRequestException.class,
				() -> accessTokenWebClientService.executeTokenRequest(AccessTokenTestProperties.TEST_ACCESS_CODE));

		// ASSERT: verify the exception
		assertEquals(MessageConstants.TOKEN_GENERATION_EWB_FAIL, invalidTokenRequestException.getMessage());
		assertEquals(InvalidTokenRequestException.class.getSimpleName(),
				invalidTokenRequestException.getClass().getSimpleName());

		InOrder inOrder = inOrder(webClient, requestBodyUriSpec, requestBodySpec, responseSpec);
		inOrder.verify(webClient).post();
		inOrder.verify(requestBodyUriSpec).uri(Mockito.<Function<UriBuilder, URI>>any());
		inOrder.verify(requestBodySpec).retrieve();
		inOrder.verify(responseSpec).toEntity((JsonNode.class));
	}

	@Test
	@DisplayName("Method to test executeTokenRequest method if access token is empty")
	void testExecuteTokenRequest_EmptyToken() throws InvalidTokenRequestException {

		// Mock successful response
		doReturn(requestBodyUriSpec).when(webClient).post();
		doReturn(requestBodySpec).when(requestBodyUriSpec).uri(Mockito.<Function<UriBuilder, URI>>any());
		doReturn(responseSpec).when(requestBodySpec).retrieve();
		doReturn(Mono.just(AccessTokenTestProperties.TEST_EMPTY_EWB_ACCESS_TOKEN_RESPONSE_ENTITY)).when(responseSpec)
				.toEntity((JsonNode.class));

		// Call method
		JsonNode response = accessTokenWebClientService.executeTokenRequest(AccessTokenTestProperties.TEST_ACCESS_CODE);

		// Validate response
		assertEquals(JSONConstants.EMPTY_STRING, response.get("access_token").asText());

		InOrder inOrder = inOrder(webClient, requestBodyUriSpec, requestBodySpec, responseSpec);
		inOrder.verify(webClient).post();
		inOrder.verify(requestBodyUriSpec).uri(Mockito.<Function<UriBuilder, URI>>any());
		inOrder.verify(requestBodySpec).retrieve();
		inOrder.verify(responseSpec).toEntity((JsonNode.class));
	}

	@Test
	@DisplayName("Method to test executeTokenRequest method if access token response is empty")
	void testExecuteTokenRequest_EmptyBody() throws InvalidTokenRequestException {

		// Mock successful response
		doReturn(requestBodyUriSpec).when(webClient).post();
		doReturn(requestBodySpec).when(requestBodyUriSpec).uri(Mockito.<Function<UriBuilder, URI>>any());
		doReturn(responseSpec).when(requestBodySpec).retrieve();
		doReturn(Mono.just(AccessTokenTestProperties.TEST_EWB_ACCESS_TOKEN_EMPTY_RESPONSE_ENTITY)).when(responseSpec)
				.toEntity((JsonNode.class));

		// Call method
		JsonNode response = accessTokenWebClientService.executeTokenRequest(AccessTokenTestProperties.TEST_ACCESS_CODE);

		// Validate response
		assertTrue(response.path("access_token").isMissingNode());

		InOrder inOrder = inOrder(webClient, requestBodyUriSpec, requestBodySpec, responseSpec);
		inOrder.verify(webClient).post();
		inOrder.verify(requestBodyUriSpec).uri(Mockito.<Function<UriBuilder, URI>>any());
		inOrder.verify(requestBodySpec).retrieve();
		inOrder.verify(responseSpec).toEntity((JsonNode.class));
	}

	@Test
	@DisplayName("Method to test executeTokenRequest method if access token response is null")
	void testExecuteTokenRequest_nullBody() throws InvalidTokenRequestException {

		// Mock successful response
		doReturn(requestBodyUriSpec).when(webClient).post();
		doReturn(requestBodySpec).when(requestBodyUriSpec).uri(Mockito.<Function<UriBuilder, URI>>any());
		doReturn(responseSpec).when(requestBodySpec).retrieve();
		doReturn(Mono.just(AccessTokenTestProperties.TEST_EWB_ACCESS_TOKEN_NULL_RESPONSE_ENTITY)).when(responseSpec)
				.toEntity((JsonNode.class));

		// Call method
		JsonNode response = accessTokenWebClientService.executeTokenRequest(AccessTokenTestProperties.TEST_ACCESS_CODE);

		// Validate response
		assertTrue(response.path("access_token").isMissingNode());

		InOrder inOrder = inOrder(webClient, requestBodyUriSpec, requestBodySpec, responseSpec);
		inOrder.verify(webClient).post();
		inOrder.verify(requestBodyUriSpec).uri(Mockito.<Function<UriBuilder, URI>>any());
		inOrder.verify(requestBodySpec).retrieve();
		inOrder.verify(responseSpec).toEntity((JsonNode.class));
	}
}
