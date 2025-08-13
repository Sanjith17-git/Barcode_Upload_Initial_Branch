package com.zifo.ewb.testPropertiesHelper;

import java.net.URI;
import java.util.function.Function;

import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.zifo.ewb.exceptions.InvalidRequestException;
import com.zifo.gsk.barcodegeneration.constants.JSONConstants;
import com.zifo.gsk.barcodegeneration.constants.MessageConstants;
import com.zifo.gsk.barcodegeneration.constants.ServiceConstants;

public class SpreadSheetServiceTestPropertiesHelper {

	private static final String TEST_SCHEME = TestProperties.getProperty("test.scheme");

	private static final Integer TEST_PORT = Integer.valueOf(TestProperties.getProperty("test.port"));

	private static final String TEST_HOST = TestProperties.getProperty("test.host");

	public static final org.springframework.web.util.UriBuilder getTestEWBURIBuilder() {
		return UriComponentsBuilder.newInstance().host(TEST_HOST).scheme(TEST_SCHEME).port(TEST_PORT);
	}

	private static final Function<UriBuilder, URI> SPREADSHEET_DATA_URI_FUNCTION = uriBuilder -> uriBuilder
			.path(ServiceConstants.SPREADSHEET_DATA)
			.queryParam(JSONConstants.VERSION_ID, TestProperties.TEST_VERSION_ID).build();

	public static final URI EXPECTED_URL = SPREADSHEET_DATA_URI_FUNCTION.apply(getTestEWBURIBuilder());

	public static final WebClientResponseException MOCK_WEB_CLIENT_RESP_EXP_401 = new WebClientResponseException(401,
			"Unauthorized", null, new byte[0], null);
	
	public static final InvalidRequestException MOCK_INVALID_REQUEST_EXC_GETDATA = new InvalidRequestException(
			MessageConstants.UNABLE_TO_READ_WRITE_TABLE_DATA);

	public static final ResponseEntity<JsonNode> NULL_RESPONSE_ENTITY = ResponseEntity.ok(null);

	public static final ResponseEntity<JsonNode> EMPTY_RESPONSE_ENTITY = ResponseEntity
			.ok(TestProperties.getEmptyJSONNode());

	public static final JsonNode GETDATA_SUCCESS_API_RESPONSE = TestProperties
			.getJsonNodeOfProperty("spreadsheet.getData.response.success");

	public static final ResponseEntity<JsonNode> GETDATA_SUCCESS_API_RESPONSE_ENTITY = ResponseEntity
			.ok(GETDATA_SUCCESS_API_RESPONSE);

	public static final JsonNode GETDATA_ACCEPTED_API_RESPONSE = TestProperties
			.getJsonNodeOfProperty("spreadsheet.getData.response.accepted");

	public static final JsonNode GETDATA_NOT_FOUND_API_RESPONSE = TestProperties
			.getJsonNodeOfProperty("spreadsheet.getData.response.notFound");

	public static final ResponseEntity<JsonNode> GETDATA_NOT_FOUND_API_RESPONSE_ENTITY = ResponseEntity
			.ok(GETDATA_NOT_FOUND_API_RESPONSE);

	public static final JsonNode GETDATA_UNAUTHORIZED_API_RESPONSE = TestProperties
			.getJsonNodeOfProperty("spreadsheet.getData.response.unauthorized");

	public static final JsonNode GETDATA_INT_ERR_API_RESPONSE = TestProperties
			.getJsonNodeOfProperty("spreadsheet.getData.response.internalServerError");
}
