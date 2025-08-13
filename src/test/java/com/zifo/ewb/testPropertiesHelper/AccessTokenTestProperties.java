package com.zifo.ewb.testPropertiesHelper;

import java.net.URI;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zifo.ewb.exceptions.InvalidTokenRequestException;
import com.zifo.gsk.barcodegeneration.constants.MessageConstants;
import com.zifo.gsk.barcodegeneration.constants.PropConstants;
import com.zifo.gsk.barcodegeneration.constants.ServiceConstants;

public class AccessTokenTestProperties {

	// Access Token related properties
	public static final String TEST_ACCESS_CODE = TestProperties.getProperty("test.access.code");

	public static final String TEST_EWB_ACCESS_TOKEN = TestProperties.getProperty("test.ewb.access.token");

	public static final JsonNode TEST_EWB_ACCESS_TOKEN_RESPONSE = TestProperties
			.getJsonNodeOfProperty("test.ewb.access.token.response");

	public static final ResponseEntity<JsonNode> TEST_EWB_ACCESS_TOKEN_RESPONSE_ENTITY = new ResponseEntity<JsonNode>(
			TEST_EWB_ACCESS_TOKEN_RESPONSE, HttpStatus.OK);

	public static final JsonNode EMPTY_EWB_ACCESS_TOKEN_RESPONSE = TestProperties
			.getJsonNodeOfProperty("test.ewb.access.token.response.emptyToken");

	public static final ResponseEntity<JsonNode> TEST_EMPTY_EWB_ACCESS_TOKEN_RESPONSE_ENTITY = ResponseEntity
			.ok(EMPTY_EWB_ACCESS_TOKEN_RESPONSE);

	public static final ResponseEntity<JsonNode> TEST_EWB_ACCESS_TOKEN_EMPTY_RESPONSE_ENTITY = ResponseEntity
			.ok(new ObjectMapper().createObjectNode());

	public static final ResponseEntity<JsonNode> TEST_EWB_ACCESS_TOKEN_NULL_RESPONSE_ENTITY = ResponseEntity.ok(null);

	public static final String TEST_TOKEN_REQUEST_URL = TestProperties.getProperty("test.token_request_url");

	public static final String TEST_REDIRECT_URI = TestProperties.getProperty("test.redirect_uri");

	public static final String TEST_CLIENT_ID = TestProperties.getProperty("test.client_id");

	public static final String TEST_GRANT_TYPE = TestProperties.getProperty("test.grant_type");

	public static final String TEST_SCHEME = TestProperties.getProperty("test.scheme");

	public static final String TEST_PORT = TestProperties.getProperty("test.port");

	public static final String TEST_HOST = TestProperties.getProperty("test.host");

	public static final String TEST_SERVICE_BASE = TestProperties.getProperty("test.servicebase");

	public static final String TEST_AUTHORIZATION = TestProperties.getProperty("test.authorization");

	public static final String TEST_CONTENT_TYPE_FORM = TestProperties.getProperty("test.content_type_form");

	public static final InvalidTokenRequestException INVALID_TOKEN_REQUEST_EXCEPTION = new InvalidTokenRequestException(
			MessageConstants.TOKEN_GENERATION_EWB_FAIL);

	private static final Function<UriBuilder, URI> TOKEN_REQUEST_URI_FUNCTION = uriBuilder -> uriBuilder
			.path(TEST_TOKEN_REQUEST_URL).queryParam(PropConstants.REDIRECT_URI, TEST_REDIRECT_URI)
			.queryParam(PropConstants.CLIENT_ID, TEST_CLIENT_ID).queryParam(PropConstants.GRANT_TYPE, TEST_GRANT_TYPE)
			.queryParam(ServiceConstants.CODE, TEST_ACCESS_CODE).build();

	public static final org.springframework.web.util.UriBuilder GET_TEST_EWB_URI_BUILDER() {
		return UriComponentsBuilder.newInstance().host(TEST_HOST).scheme(TEST_SCHEME).port(TEST_PORT);
	}

	public static final URI EXPECTED_URL = TOKEN_REQUEST_URI_FUNCTION.apply(GET_TEST_EWB_URI_BUILDER());

}
