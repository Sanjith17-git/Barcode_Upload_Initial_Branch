package com.zifo.gsk.barcodegeneration.utils;

import java.util.Map;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;

/**
 * Utility class provide common JSON related functions
 * 
 * @author zifo
 *
 */
@UtilityClass
public final class JSONUtils {
	/**
	 * Method that creates JSON to query the table data
	 * 
	 * @param tableToBeQuery
	 * @return Query to get details of required table
	 */
	public static final String createJSONToReadTableData(final String tableToBeQuery) {
		final String template = "{\"batch-request\":[{\"version\":\"1.0\"},[{\"api-id\":\"table.data\",\"api-version\":\"1.0\"},{\"data\":{\"queries\":[{\"table\":\"%s\",\"range\":\"\"}]}}]]}";
		return String.format(template, tableToBeQuery);
	}

	/**
	 * @param tableToBeQuery
	 * @return JSON to read table structure
	 */
	public static final String createJSONToReadTableStructure(String tableToBeQuery) {
		final String template = "{\"batch-request\":[{\"version\":\"1.0\",\"options\":{\"auditEvent\":{\"type\":\"Update Samples\",\"description\":\"Updated samples with latest location information\",\"location\":\"%s\"}}},[{\"api-id\":\"table.structure\",\"api-version\":\"1.0\"},{\"data\":{\"tables\":[\"%s\"],\"options\":{\"itemNames\":\"dataDimension\"}}}]]}";
		return String.format(template, tableToBeQuery, tableToBeQuery);
	}

	/**
	 * 
	 * @param imageName
	 * @param encodedString
	 * @return JSON to add images
	 */
	public static final String getJSONToAddImage(final String imageName, final String encodedString) {
		final String template = "{\"batch-request\":[{\"version\":\"1.0\"},[{\"api-id\":\"image.add\",\"api-version\":\"1.0\"},{\"data\":{\"image\":{\"name\":\"%s\",\"base64\":\"%s\"}}}]]}";
		return String.format(template, imageName, encodedString);
	}

	/**
	 * Builds the batch‐request JSON for setting table data in one go.
	 *
	 * @param populateDetails  barcode key → fileName map
	 * @param tableName        the target table name
	 * @param nonDataDimension the JSON field name for the sample key
	 * @param barcode 
	 * @return a single‐line JSON string
	 */
	public static String getPopulateJson(Map<String, String> populateDetails, String tableName,
			String nonDataDimension, String barcode) {
		// template with tableName and the array of row‐objects placeholders
		final String template = "{\"batch-request\":[{\"version\":\"1.0\"},[{\"api-id\":\"table.data.set\",\"api-version\":\"1.0\"},{\"data\":{\"tables\":[{\"name\":\"%s\"},[%s]]}}]]}";

		// build the array of row objects
		String rows = populateDetails
				.entrySet().stream().map(e -> String.format("{\"%s\":{\"string\":\"%s\"},\"%s\":{\"image\":\"%s\"}}",
						nonDataDimension, e.getKey(), barcode, e.getValue()))
				.collect(Collectors.joining(","));

		// inject the table name and the rows array
		return String.format(template, tableName, rows);
	}
}
