package com.zifo.ewb.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.zifo.ewb.exceptions.InvalidTokenRequestException;
import com.zifo.ewb.webclient.service.AccessTokenWebClientService;
import com.zifo.gsk.barcodegeneration.constants.JSONConstants;
import com.zifo.gsk.barcodegeneration.constants.MessageConstants;

import lombok.RequiredArgsConstructor;

/**
 * Class that provides the Access Token
 * 
 * @author zifo
 */
@Service
@RequiredArgsConstructor
public final class AccessTokenProvider {
	/**
	 * Logger instance to write logs
	 */
	
	private static final Logger LOGGER = LogManager.getLogger(AccessTokenProvider.class);

	private final AccessTokenWebClientService accessTokenWebClientService;

	/**
	 * This method is responsible for requesting access token using access code
	 * 
	 * @param accessCode
	 * @return Access Token
	 * @throws InvalidTokenRequestException If there is exception during execution
	 *                                      of Web Request or If the token is
	 *                                      missing in response
	 */
	public String requestAccessToken(final String accessCode) throws InvalidTokenRequestException {

		// Get token API response as JsonNode
		final JsonNode tokenResponse = accessTokenWebClientService.executeTokenRequest(accessCode);
		LOGGER.debug(MessageConstants.GOT_TOKEN_RESPONSE);

		// Retrieves Access token JsonNode and returns the field from the JsonNode
		final JsonNode accessTokenJson = tokenResponse.path(JSONConstants.ACCESS_TOKEN);

		if (!accessTokenJson.isMissingNode() && !accessTokenJson.asText().isEmpty()) {
			return accessTokenJson.asText();
		}
		throw new InvalidTokenRequestException(MessageConstants.EWB_ACCESS_TOKEN_MISSING_IN_RESPONSE);
	}
}