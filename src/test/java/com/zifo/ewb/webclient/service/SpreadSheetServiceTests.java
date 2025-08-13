package com.zifo.ewb.webclient.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.util.UriBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.zifo.ewb.exceptions.InvalidRequestException;
import com.zifo.ewb.testPropertiesHelper.AccessTokenTestProperties;
import com.zifo.ewb.testPropertiesHelper.SpreadSheetServiceTestPropertiesHelper;
import com.zifo.ewb.testPropertiesHelper.TestProperties;
import com.zifo.gsk.barcodegeneration.constants.MessageConstants;
import com.zifo.gsk.barcodegeneration.constants.ServiceConstants;
import com.zifo.gsk.barcodegeneration.utils.JSONUtils;

import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@DisplayName("EWB Spreadsheet Service tests")
class SpreadSheetServiceTests {

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

	private SpreadSheetService spreadSheetService;

	private String requestBody;

	private ArgumentCaptor<Function<UriBuilder, URI>> lambdaCaptor;

	@SuppressWarnings("unchecked")
	@BeforeEach
	void setUp() {
		// ARRANGE: Initialize the services and injecting the properties fields
		spreadSheetService = new SpreadSheetService(requestBody, requestBody, requestBody, requestBody, 10, 10, 10, 10);
		ReflectionTestUtils.setField(spreadSheetService, "webClient", webClient);
		requestBody = JSONUtils.createJSONToReadTableData(TestProperties.MOCK_REQUIRED_TABLES_STRING);
		lambdaCaptor = ArgumentCaptor.forClass(Function.class);
	}

	@Test
	@DisplayName("Method to test the success response from getData method")
	void testGetData_Success() throws InvalidRequestException {

		// ARRANGE: Mock required return objects of mocked methods
		doReturn(requestBodyUriSpec).when(webClient).post();
		doReturn(requestBodySpec).when(requestBodyUriSpec).uri(Mockito.<Function<UriBuilder, URI>>any());
		doReturn(requestBodySpec).when(requestBodySpec).header((ServiceConstants.AUTHORIZATION),
				(ServiceConstants.BEARER + AccessTokenTestProperties.TEST_ACCESS_CODE));
		doReturn(requestBodySpec).when(requestBodySpec).bodyValue(requestBody);
		doReturn(responseSpec).when(requestBodySpec).retrieve();
		doReturn(Mono.just(SpreadSheetServiceTestPropertiesHelper.GETDATA_SUCCESS_API_RESPONSE_ENTITY))
				.when(responseSpec).toEntity((JsonNode.class));

		// ACT: Executing the method using mocked inputs
		JsonNode response = spreadSheetService.getData(TestProperties.TEST_VERSION_ID, requestBody,
				AccessTokenTestProperties.TEST_ACCESS_CODE);

		// ASSERT: verify the response
		assertEquals(SpreadSheetServiceTestPropertiesHelper.GETDATA_SUCCESS_API_RESPONSE, response);

		// INORDER : check order of execution
		InOrder inOrder = inOrder(webClient, requestBodyUriSpec, requestBodySpec, responseSpec);
		inOrder.verify(webClient).post();
		inOrder.verify(requestBodyUriSpec).uri(lambdaCaptor.capture());
		inOrder.verify(requestBodySpec).header((ServiceConstants.AUTHORIZATION),
				(ServiceConstants.BEARER + AccessTokenTestProperties.TEST_ACCESS_CODE));

		inOrder.verify(requestBodySpec).retrieve();
		inOrder.verify(responseSpec).toEntity((JsonNode.class));

		Function<UriBuilder, URI> capturedLambda = lambdaCaptor.getValue();
		assertNotNull(capturedLambda);

		// Apply the lambda to the builder to see what URI it generates.
		URI generatedUri = capturedLambda.apply(SpreadSheetServiceTestPropertiesHelper.getTestEWBURIBuilder());
		assertEquals(SpreadSheetServiceTestPropertiesHelper.EXPECTED_URL, generatedUri,
				"The lambda did not generate the expected URI.");
	}

	@Test
	@DisplayName("Method to test the InvalidRequestException due WebClientResponseException to  from getData method")
	void testGetData_InvalidRequestException_WebClientResponseException() {

		// ARRANGE: Mock required return objects of mocked methods
		doReturn(requestBodyUriSpec).when(webClient).post();
		doReturn(requestBodySpec).when(requestBodyUriSpec).uri(Mockito.<Function<UriBuilder, URI>>any());
		doReturn(requestBodySpec).when(requestBodySpec).header((ServiceConstants.AUTHORIZATION),
				(ServiceConstants.BEARER + AccessTokenTestProperties.TEST_ACCESS_CODE));
		doReturn(requestBodySpec).when(requestBodySpec).bodyValue(requestBody);
		doReturn(responseSpec).when(requestBodySpec).retrieve();
		doThrow(SpreadSheetServiceTestPropertiesHelper.MOCK_WEB_CLIENT_RESP_EXP_401).when(responseSpec)
				.toEntity(JsonNode.class);

		// ACT: Executing the method using mocked inputs
		InvalidRequestException invalidRequestException = assertThrows(InvalidRequestException.class,
				() -> spreadSheetService.getData(TestProperties.TEST_VERSION_ID, requestBody,
						AccessTokenTestProperties.TEST_ACCESS_CODE));

		// ASSERT: verify the exception
		assertEquals(MessageConstants.UNABLE_TO_READ_WRITE_TABLE_DATA, invalidRequestException.getMessage());

		// INORDER : check order of execution
		InOrder inOrder = inOrder(webClient, requestBodyUriSpec, requestBodySpec, responseSpec);
		inOrder.verify(webClient).post();
		inOrder.verify(requestBodyUriSpec).uri(lambdaCaptor.capture());
		inOrder.verify(requestBodySpec).header((ServiceConstants.AUTHORIZATION),
				(ServiceConstants.BEARER + AccessTokenTestProperties.TEST_ACCESS_CODE));

		inOrder.verify(requestBodySpec).retrieve();
		inOrder.verify(responseSpec).toEntity((JsonNode.class));

		Function<UriBuilder, URI> capturedLambda = lambdaCaptor.getValue();
		assertNotNull(capturedLambda);

		// Apply the lambda to the builder to see what URI it generates.
		URI generatedUri = capturedLambda.apply(SpreadSheetServiceTestPropertiesHelper.getTestEWBURIBuilder());
		assertEquals(SpreadSheetServiceTestPropertiesHelper.EXPECTED_URL, generatedUri,
				"The lambda did not generate the expected URI.");
	}

	@Test
	@DisplayName("Method to test getData method for InvalidRequestException if response body is empty")
	void testGetData_InvalidRequestException_EmptyBody() {

		// ARRANGE: Mock required return objects of mocked methods
		doReturn(requestBodyUriSpec).when(webClient).post();
		doReturn(requestBodySpec).when(requestBodyUriSpec).uri(Mockito.<Function<UriBuilder, URI>>any());
		doReturn(requestBodySpec).when(requestBodySpec).header((ServiceConstants.AUTHORIZATION),
				(ServiceConstants.BEARER + AccessTokenTestProperties.TEST_ACCESS_CODE));
		doReturn(requestBodySpec).when(requestBodySpec).bodyValue(requestBody);
		doReturn(responseSpec).when(requestBodySpec).retrieve();
		doReturn(Mono.just(SpreadSheetServiceTestPropertiesHelper.EMPTY_RESPONSE_ENTITY)).when(responseSpec)
				.toEntity((JsonNode.class));

		// ACT: Executing the method using mocked inputs
		InvalidRequestException invalidRequestException = assertThrows(InvalidRequestException.class,
				() -> spreadSheetService.getData(TestProperties.TEST_VERSION_ID, requestBody,
						AccessTokenTestProperties.TEST_ACCESS_CODE));

		// ASSERT: verify the exception message
		assertEquals(MessageConstants.NULL_OR_EMPTY_RESPONSE_BODY, invalidRequestException.getMessage());

		// INORDER : check order of execution
		InOrder inOrder = inOrder(webClient, requestBodyUriSpec, requestBodySpec, responseSpec);
		inOrder.verify(webClient).post();
		inOrder.verify(requestBodyUriSpec).uri(lambdaCaptor.capture());
		inOrder.verify(requestBodySpec).header((ServiceConstants.AUTHORIZATION),
				(ServiceConstants.BEARER + AccessTokenTestProperties.TEST_ACCESS_CODE));

		inOrder.verify(requestBodySpec).retrieve();
		inOrder.verify(responseSpec).toEntity((JsonNode.class));

		Function<UriBuilder, URI> capturedLambda = lambdaCaptor.getValue();
		assertNotNull(capturedLambda);

		// Apply the lambda to the builder to see what URI it generates.
		URI generatedUri = capturedLambda.apply(SpreadSheetServiceTestPropertiesHelper.getTestEWBURIBuilder());
		assertEquals(SpreadSheetServiceTestPropertiesHelper.EXPECTED_URL, generatedUri,
				"The lambda did not generate the expected URI.");
	}

	@Test
	@DisplayName("Method to test getData method for InvalidRequestException if response body is null")
	void testGetData_InvalidRequestException_nullBody() throws InvalidRequestException {

		// ARRANGE: Mock required return objects of mocked methods
		doReturn(requestBodyUriSpec).when(webClient).post();
		doReturn(requestBodySpec).when(requestBodyUriSpec).uri(Mockito.<Function<UriBuilder, URI>>any());
		doReturn(requestBodySpec).when(requestBodySpec).header((ServiceConstants.AUTHORIZATION),
				(ServiceConstants.BEARER + AccessTokenTestProperties.TEST_ACCESS_CODE));
		doReturn(requestBodySpec).when(requestBodySpec).bodyValue(requestBody);
		doReturn(responseSpec).when(requestBodySpec).retrieve();
		doReturn(Mono.just(SpreadSheetServiceTestPropertiesHelper.NULL_RESPONSE_ENTITY)).when(responseSpec)
				.toEntity((JsonNode.class));

		// ACT: Executing the method using mocked inputs
		InvalidRequestException invalidRequestException = assertThrows(InvalidRequestException.class,
				() -> spreadSheetService.getData(TestProperties.TEST_VERSION_ID, requestBody,
						AccessTokenTestProperties.TEST_ACCESS_CODE));

		// ASSERT: verify the exception message
		assertEquals(MessageConstants.NULL_RESPONSE, invalidRequestException.getMessage());

		// INORDER : check order of execution
		InOrder inOrder = inOrder(webClient, requestBodyUriSpec, requestBodySpec, responseSpec);
		inOrder.verify(webClient).post();
		inOrder.verify(requestBodyUriSpec).uri(lambdaCaptor.capture());
		inOrder.verify(requestBodySpec).header((ServiceConstants.AUTHORIZATION),
				(ServiceConstants.BEARER + AccessTokenTestProperties.TEST_ACCESS_CODE));

		inOrder.verify(requestBodySpec).retrieve();
		inOrder.verify(responseSpec).toEntity((JsonNode.class));

		Function<UriBuilder, URI> capturedLambda = lambdaCaptor.getValue();
		assertNotNull(capturedLambda);

		// Apply the lambda to the builder to see what URI it generates.
		URI generatedUri = capturedLambda.apply(SpreadSheetServiceTestPropertiesHelper.getTestEWBURIBuilder());
		assertEquals(SpreadSheetServiceTestPropertiesHelper.EXPECTED_URL, generatedUri,
				"The lambda did not generate the expected URI.");
	}

	@Test
	@DisplayName("Method to test getData method for InvalidRequestException if response body is null")
	void testGetData_InvalidRequestException_404_Response() throws InvalidRequestException {

		// ARRANGE: Mock required return objects of mocked methods
		doReturn(requestBodyUriSpec).when(webClient).post();
		doReturn(requestBodySpec).when(requestBodyUriSpec).uri(Mockito.<Function<UriBuilder, URI>>any());
		doReturn(requestBodySpec).when(requestBodySpec).header((ServiceConstants.AUTHORIZATION),
				(ServiceConstants.BEARER + AccessTokenTestProperties.TEST_ACCESS_CODE));
		doReturn(requestBodySpec).when(requestBodySpec).bodyValue(requestBody);
		doReturn(responseSpec).when(requestBodySpec).retrieve();
		doReturn(Mono.just(SpreadSheetServiceTestPropertiesHelper.GETDATA_NOT_FOUND_API_RESPONSE_ENTITY))
				.when(responseSpec).toEntity((JsonNode.class));

		// ACT: Executing the method using mocked inputs
		InvalidRequestException invalidRequestException = assertThrows(InvalidRequestException.class,
				() -> spreadSheetService.getData(TestProperties.TEST_VERSION_ID, requestBody,
						AccessTokenTestProperties.TEST_ACCESS_CODE));

		// ASSERT: verify the exception message
		assertEquals(MessageConstants.UNABLE_TO_READ_WRITE_TABLE_DATA, invalidRequestException.getMessage());

		// INORDER : check order of execution
		InOrder inOrder = inOrder(webClient, requestBodyUriSpec, requestBodySpec, responseSpec);
		inOrder.verify(webClient).post();
		inOrder.verify(requestBodyUriSpec).uri(lambdaCaptor.capture());
		inOrder.verify(requestBodySpec).header((ServiceConstants.AUTHORIZATION),
				(ServiceConstants.BEARER + AccessTokenTestProperties.TEST_ACCESS_CODE));

		inOrder.verify(requestBodySpec).retrieve();
		inOrder.verify(responseSpec).toEntity((JsonNode.class));

		Function<UriBuilder, URI> capturedLambda = lambdaCaptor.getValue();
		assertNotNull(capturedLambda);

		// Apply the lambda to the builder to see what URI it generates.
		URI generatedUri = capturedLambda.apply(SpreadSheetServiceTestPropertiesHelper.getTestEWBURIBuilder());
		assertEquals(SpreadSheetServiceTestPropertiesHelper.EXPECTED_URL, generatedUri,
				"The lambda did not generate the expected URI.");
	}

	@Test
	@DisplayName("Method to test is200or201 method when the response is 202 accepted")
	void testIs200or201_AcceptedResp() {
		// ACT: Executing the method using mocked inputs
		Boolean output = spreadSheetService
				.is200or201(SpreadSheetServiceTestPropertiesHelper.GETDATA_ACCEPTED_API_RESPONSE);

		// ASSERT: verify the exception message
		assertFalse(output);
	}

	@Test
	@DisplayName("Method to test is200or201 method when the response is 200 success")
	void testIs200or201_SuccessResp() {
		// ACT: Executing the method using mocked inputs
		Boolean output = spreadSheetService
				.is200or201(SpreadSheetServiceTestPropertiesHelper.GETDATA_SUCCESS_API_RESPONSE);

		// ASSERT: verify the exception message
		assertTrue(output);
	}
}
