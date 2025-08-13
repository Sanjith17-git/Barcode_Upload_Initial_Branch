package com.zifo.gsk.barcodegeneration.helper;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.zxing.WriterException;
import com.zifo.ewb.exceptions.InvalidRequestException;
import com.zifo.ewb.pojo.RequestDTO;
import com.zifo.ewb.pojo.ResponseDTO;
import com.zifo.ewb.webclient.service.SpreadSheetService;
import com.zifo.gsk.barcodegeneration.constants.MessageConstants;
import com.zifo.gsk.barcodegeneration.utils.BarcodeGeneratorUtils;
import com.zifo.gsk.barcodegeneration.utils.DataReader;
import com.zifo.gsk.barcodegeneration.utils.JSONUtils;
import com.zifo.gsk.barcodegeneration.utils.ResponseUtils;

import lombok.RequiredArgsConstructor;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;

/**
 * @author Zifo
 */
@Service
@RequiredArgsConstructor
public class BarcodeGenerator {

	/** LOGGER instance to write logs */
	private static final Logger LOGGER = LogManager.getLogger(BarcodeGenerator.class);

	/**
	 * Dependency injection via constructor
	 */
	private final SpreadSheetService spreadSheetService;


	@Value("${namePrefix}")
	private String prefix;

	@Value("${format}")
	private String format;

	@Value("${samples}")
	private String samples;

	@Value("${sampleIdColumn}")
	private String sampleIdColumn;

	@Value("${nonDD}")
	private String nonDD;

	@Value("${barcode}")
	private String barcode;

	/**
	 * 
	 * @param accessToken
	 * @param requestDTO
	 * @return ResponseDTO
	 */
	public ResponseDTO generate(final String accessToken, final RequestDTO requestDTO) {
		ResponseDTO response;
		final String versionId = requestDTO.getSpreadsheet().getVersionId();
		final JsonNode responseNode = spreadSheetService.getData(versionId,
				JSONUtils.createJSONToReadTableData(samples), accessToken);
		final List<String> dimensionColumn = DataReader.readDataFromJson(responseNode, "Barcode Dimension");
		final Map<String, String> sampleDetails = DataReader.readDataAsMap(responseNode, nonDD, sampleIdColumn);
		final String dimension = dimensionColumn.get(0);

		if (sampleDetails.isEmpty()) {
			response = ResponseUtils.generateResponse(Boolean.FALSE, 0);
		} else {
			Map<String, String> populateDetails = sampleDetails.entrySet().stream()
					.filter((Map.Entry<String, String> entry) -> !StringUtils.isEmpty(entry.getValue()))
					.map((Map.Entry<String, String> entry) -> {
						final String value = entry.getValue();
						// build file path
						String name = StringUtils.EMPTY;
						try {
							String encodedString;
							if ("2D".equals(dimension)) {
								encodedString = BarcodeGeneratorUtils.generateQRCodeBase64(value, format);
							} else {
								encodedString = BarcodeGeneratorUtils.generate1DBarcodeBase64(value, format);
							}
							// build name and call service
							name = prefix + value + "_" + dimension + "." + format;
							final String payload = JSONUtils.getJSONToAddImage(name, encodedString);
							spreadSheetService.getData(versionId, payload, accessToken);

						} catch (WriterException | IOException | BarcodeException | OutputException
								| InvalidRequestException exception) {
							LOGGER.error("Unable to generate barcode / unable to update barcode in IDBS EWB");
							LOGGER.error(MessageConstants.getExceptionLog(exception));
						}
						return Map.entry(entry.getKey(), name);
					}).filter((Map.Entry<String, String> entry) -> !StringUtils.isEmpty(entry.getValue()))
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> newVal,
							LinkedHashMap::new));

			if (!populateDetails.isEmpty()) {
				LOGGER.info(populateDetails);
				String populateJson = JSONUtils.getPopulateJson(populateDetails, samples, nonDD, barcode);
				LOGGER.info(populateJson);
				JsonNode populateApiResponse = spreadSheetService.getData(versionId, populateJson, accessToken);
				boolean status = spreadSheetService.is200or201(populateApiResponse);
				response = ResponseUtils.generateResponse(status, 1);
			} else {
				response = ResponseUtils.generateResponse(Boolean.FALSE, 0);
			}
		}
		return response;
	}
}
