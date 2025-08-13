package com.zifo.ewb.testPropertiesHelper;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.JsonNode;
import com.zifo.gsk.barcodegeneration.utils.JSONUtils;

public class BarcodeGeneratorTestProperties {

	public static final JsonNode GET_BARCODE_DATA_SUCCESS_API_RESPONSE = TestProperties
			.getJsonNodeOfProperty("barcode.getData.response");

	private static final Map<String, String> POPULATE_DETAILS_MAP = Stream
			.of(new AbstractMap.SimpleEntry<>("TESTSAMPLE1", "test://TESTSAMPLE1_2D.format"),
					new AbstractMap.SimpleEntry<>("TESTSAMPLE2", "test://TESTSAMPLE2_2D.format"))
			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (u, v) -> u, LinkedHashMap::new));

	public static final String MOCK_SAMPLES = "TEST_SAMPLES";
	
	public static final String MOCK_TEST_SAMPLE_1 = "TESTSAMPLE1";
	
	public static final String MOCK_TEST_SAMPLE_2 = "TESTSAMPLE2";

	public static final String MOCK_NON_DD = "B";

	public static final String MOCK_BARCODE = "TEST_BARCODE";

	public static final String MOCK_PREFIX = "test://";

	public static final String MOCK_FORMAT = "format";

	public static final String MOCK_DIRECTORY_PATH = "C:\\testing\\barcodes\\";

	public static final String MOCK_SAMPLE_ID_COL = "Sample ID";

	public static final String READ_TABLE_JSON = JSONUtils.createJSONToReadTableData(MOCK_SAMPLES);

	public static final String POPULATE_JSON = JSONUtils.getPopulateJson(POPULATE_DETAILS_MAP, MOCK_SAMPLES,
			MOCK_NON_DD, MOCK_BARCODE);

	public static final String IMAGE_1_PATH = "C:\\testing\\barcodes\\TESTSAMPLE1_2D.format";

	public static final String IMAGE_2_PATH = "C:\\testing\\barcodes\\TESTSAMPLE2_2D.format";

	public static final String IMAGE_1_ENCODED = "Encoded " + IMAGE_1_PATH;

	public static final String IMAGE_2_ENCODED = "Encoded " + IMAGE_2_PATH;
	
	public static final String IMAGE_ENCODED = "Encoded Image Bytes";

	public static final String ADD_IMAGE_1_JSON = JSONUtils.getJSONToAddImage("test://TESTSAMPLE1_2D.format", IMAGE_1_ENCODED);

	public static final String ADD_IMAGE_2_JSON = JSONUtils.getJSONToAddImage("test://TESTSAMPLE2_2D.format", IMAGE_2_ENCODED);
}
