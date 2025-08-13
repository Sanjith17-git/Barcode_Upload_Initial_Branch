package com.zifo.ewb.testPropertiesHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zifo.ewb.exceptions.InvalidRequestException;
import com.zifo.ewb.pojo.RequestDTO;
import com.zifo.gsk.barcodegeneration.constants.MessageConstants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TestProperties {

	private static final Logger LOGGER = LogManager.getLogger(TestProperties.class);

	private static final Properties properties = new Properties();

	private static final String PROPERTIES_FILE = "test-constants.properties";

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
//			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
			;

	static {
		try (InputStream input = TestProperties.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
			if (input == null) {
				LOGGER.error("Unable to find properties file: {}", PROPERTIES_FILE);
				throw new RuntimeException("Unable to find " + PROPERTIES_FILE);
			}
			properties.load(input);
			LOGGER.info("Successfully loaded properties from: {}", PROPERTIES_FILE);

			// Log all loaded properties for debugging
			properties.forEach((key, value) -> LOGGER.debug("Loaded property - {}: {}", key, value));
		} catch (IOException e) {
			LOGGER.error("Error loading properties file: {}", PROPERTIES_FILE, e);
			throw new RuntimeException("Error loading " + PROPERTIES_FILE, e);
		}
	}

	public static final RequestDTO getRequestDTO() {
		String json = getProperty("test.request");
		RequestDTO dto;
		try {
			dto = OBJECT_MAPPER.readValue(json, RequestDTO.class);
		} catch (JsonProcessingException e) {
			dto = new RequestDTO();
		}
		return dto;
	}

	public static final String TEST_ERROR_REQUEST = getProperty("test.error.request");

	public static final String TEST_VERSION_ID = getProperty("test.version.id");

	public static final String MOCK_REQUIRED_TABLES_STRING = getProperty("studydirector.table.mock");

	public static final List<String> MOCK_REQUIRED_TABLES = List.of(MOCK_REQUIRED_TABLES_STRING);

	public static final InvalidRequestException MOCK_INVALID_REQUEST_EXC_GETDATA = new InvalidRequestException(
			MessageConstants.UNABLE_TO_READ_WRITE_TABLE_DATA);

	public static final WebClientResponseException MOCK_WEB_CLIENT_RESP_EXP_401 = new WebClientResponseException(401,
			"Unauthorized", null, new byte[0], null);

	public static final ResponseEntity<JsonNode> NULL_RESPONSE_ENTITY = ResponseEntity.ok(null);

	public static final ResponseEntity<JsonNode> EMPTY_RESPONSE_ENTITY = ResponseEntity
			.ok(TestProperties.getEmptyJSONNode());

	public static final JsonNode GETDATA_SUCCESS_API_RESPONSE = TestProperties
			.getJsonNodeOfProperty("spreadsheet.getData.response.success");

	public static final JsonNode GETDATA_NOT_FOUND_API_RESPONSE = TestProperties
			.getJsonNodeOfProperty("spreadsheet.getData.response.notFound");

	public static final JsonNode GETDATA_UNAUTHORIZED_API_RESPONSE = TestProperties
			.getJsonNodeOfProperty("spreadsheet.getData.response.unauthorized");

	public static final JsonNode GETDATA_INT_ERR_API_RESPONSE = TestProperties
			.getJsonNodeOfProperty("spreadsheet.getData.response.internalServerError");

	public static final String MOCK_CREATE_JSON_RESULT = "{\"batch-request\":[{\"version\":\"1.0\"},[{\"api-version\":\"1.0\",\"api-id\":\"table.data.set\"},{\"MockObject\":{\"Data\":{\"TUBELIST\":[\"TUBE1\",\"TUBE2\"]}}}]]}";

	/*
	 * Method that returns value of given property key
	 * 
	 * @param key
	 * 
	 * @return value in .properties file
	 */
	public static String getProperty(String key) {
		String value = properties.getProperty(key);
		if (value == null) {
			LOGGER.error("Property not found: {}", key);
			throw new RuntimeException("Required property not found: " + key);
		}
		return value;
	}

	/**
	 * Gets the API response of String and returns as Json Node Object
	 * 
	 * @param propertyKey
	 * @return Response of type JsonNode
	 */
	public static JsonNode getJsonNodeOfProperty(String propertyKey) {
		try {
			return OBJECT_MAPPER.readTree(getProperty(propertyKey));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Unable to convert into JsonNode: " + propertyKey);
		}
	}

	/**
	 * Creates an empty JsonNode
	 * 
	 * @return
	 */
	public static JsonNode getEmptyJSONNode() {
		return OBJECT_MAPPER.createObjectNode();
	}
}
