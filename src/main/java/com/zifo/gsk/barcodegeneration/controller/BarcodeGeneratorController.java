package com.zifo.gsk.barcodegeneration.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zifo.ewb.helper.AccessTokenProvider;
import com.zifo.ewb.pojo.RequestDTO;
import com.zifo.ewb.pojo.ResponseDTO;
import com.zifo.gsk.barcodegeneration.constants.MessageConstants;
import com.zifo.gsk.barcodegeneration.helper.BarcodeGenerator;
import com.zifo.gsk.barcodegeneration.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * @author Zifo
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Barcode Generator API", description = "Generates barcode for each sample IDs")
@RequestMapping("/BarcodeGenerator/v1")
public class BarcodeGeneratorController {

	/** Field LOGGER to write logs */
	private static final Logger LOGGER = LogManager.getLogger(BarcodeGeneratorController.class);

	private final AccessTokenProvider accessTokenProvider;

	private final BarcodeGenerator barcodeGenerator;

	/**
	 * @param requestDTO
	 * @return ResponseDTO
	 */
	@PostMapping(path = "/generateBarcode", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseDTO generateBarcode(@Valid @RequestBody final RequestDTO requestDTO) {
		LOGGER.info("Barcode generation starts");
		ResponseDTO responseDTO;
		try {
			// Get access token using the access code from IDBS
			final String accessToken = accessTokenProvider.requestAccessToken(requestDTO.getOauth().getAccessCode());
			responseDTO = barcodeGenerator.generate(accessToken, requestDTO);
			LOGGER.info("Barcode generation process ends");
		} catch (Exception exception) {
			LOGGER.error(MessageConstants.getExceptionLog(exception));
			responseDTO = ResponseUtils.generateResponse(false, 2);
		}
		return responseDTO;
	}
}
