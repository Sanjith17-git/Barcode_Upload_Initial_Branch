package com.zifo.gsk.barcodegeneration.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.zifo.gsk.barcodegeneration.constants.JSONConstants;

import lombok.experimental.UtilityClass;

/**
 * Utility class to provide methods that helps to read the data from JSON
 *
 * @author zifo
 *
 */
@UtilityClass
public class DataReader {

	/**
	 * Method to read data from given column in table
	 *
	 * @param response
	 * @param columnName
	 * @return List of values in specific column
	 */
	public static final List<String> readDataFromJson(final JsonNode response, final String columnName) {
		final ArrayList<String> outputData = new ArrayList<>();
		StreamSupport
				.stream(response.path(JSONConstants.BATCH_RESPONSE).path(JSONConstants.API_RESPONSES).spliterator(),
						false)
				.flatMap(apiResponse -> StreamSupport.stream(apiResponse.path(JSONConstants.TABLES).spliterator(),
						false))
				.flatMap(table -> StreamSupport.stream(table.path(JSONConstants.RANGES).spliterator(), false))
				.flatMap(range -> StreamSupport.stream(range.path(JSONConstants.DATA).spliterator(), false))
				.filter(dataIterator -> !dataIterator.path(columnName).isMissingNode())
				.map(dataIterator -> readJson(dataIterator, columnName)).filter(value -> !value.isEmpty())
				.forEach(outputData::add);

		outputData.removeAll(Arrays.asList(StringUtils.EMPTY, "No Matches"));
		return outputData;
	}

	/**
	 * Method to read key–value pairs from two columns in the JSON tables.
	 *
	 * @param response    the root JsonNode containing BATCH_RESPONSE →
	 *                    API_RESPONSES → TABLES → RANGES → DATA
	 * @param keyColumn   the JSON field to use as map key
	 * @param valueColumn the JSON field to use as map value
	 * @return a LinkedHashMap preserving insertion order of non-empty key/value
	 *         pairs
	 */
	public static Map<String, String> readDataAsMap(final JsonNode response, final String keyColumn,
			final String valueColumn) {
		return StreamSupport
				.stream(response.path(JSONConstants.BATCH_RESPONSE).path(JSONConstants.API_RESPONSES).spliterator(),
						false)
				.flatMap(apiResp -> StreamSupport.stream(apiResp.path(JSONConstants.TABLES).spliterator(), false))
				.flatMap(table -> StreamSupport.stream(table.path(JSONConstants.RANGES).spliterator(), false))
				.flatMap(range -> StreamSupport.stream(range.path(JSONConstants.DATA).spliterator(), false))
				.filter(node -> !node.path(keyColumn).isMissingNode() && !node.path(valueColumn).isMissingNode())
				.map(node -> Map.entry(readJson(node, keyColumn), readJson(node, valueColumn)))
				.filter(entry -> !entry.getKey().isEmpty() && !entry.getValue().isEmpty()).collect(Collectors
						.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> newVal, LinkedHashMap::new));
	}

	/**
	 * Method that read data from JSON node
	 *
	 * @param node
	 * @param value
	 * @return
	 */
	public static final String readJson(final JsonNode node, final String value) {

		JsonNode path = node.path(value);
		String conc;
		if (!path.path(JSONConstants.NUMBER).isMissingNode()) {
			final int num = path.path(JSONConstants.NUMBER).asInt();
			conc = Integer.toString(num);
		} else if (!path.path(JSONConstants.DATE_TIME).isMissingNode()) {
			conc = path.path(JSONConstants.DATE_TIME).asText();
		} else if (!path.path(JSONConstants.INDEX).isMissingNode()) {
			conc = path.path(JSONConstants.INDEX).asText();
		} else if (!path.path(JSONConstants.STRING).isMissingNode()) {
			conc = path.path(JSONConstants.STRING).asText();
		} else {
			conc = StringUtils.EMPTY;
		}
		return conc;

	}
}
