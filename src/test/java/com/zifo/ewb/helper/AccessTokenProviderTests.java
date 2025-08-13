package com.zifo.ewb.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.zifo.ewb.exceptions.InvalidTokenRequestException;
import com.zifo.ewb.testPropertiesHelper.AccessTokenTestProperties;
import com.zifo.ewb.testPropertiesHelper.TestProperties;
import com.zifo.ewb.webclient.service.AccessTokenWebClientService;
import com.zifo.gsk.barcodegeneration.constants.MessageConstants;

/**
 * 
 */
@ExtendWith(SpringExtension.class)
@DisplayName("Test Class for Access token provider")
class AccessTokenProviderTests {

	private AccessTokenWebClientService accessTokenWebClientService;
	private AccessTokenProvider accessTokenProvider;

	@BeforeEach
	void setup() {
		// ARRANGE: Initialize the service
		accessTokenWebClientService = Mockito.mock(AccessTokenWebClientService.class);
		accessTokenProvider = new AccessTokenProvider(accessTokenWebClientService);
	}

	@Test
	@DisplayName("Getting access token successfully")
	void testGetAccessToken_Success() throws InvalidTokenRequestException {

		// ARRANGE: Setting up the mocked services response
		doReturn(AccessTokenTestProperties.TEST_EWB_ACCESS_TOKEN_RESPONSE).when(accessTokenWebClientService)
				.executeTokenRequest((AccessTokenTestProperties.TEST_ACCESS_CODE));

		// ACT: Executing the method using mocked access code
		String accessToken = accessTokenProvider.requestAccessToken(AccessTokenTestProperties.TEST_ACCESS_CODE);

		// ASSERT: verify the response
		assertEquals(AccessTokenTestProperties.TEST_EWB_ACCESS_TOKEN, accessToken);

		// check for execution
		verify(accessTokenWebClientService).executeTokenRequest((AccessTokenTestProperties.TEST_ACCESS_CODE));
	}

	@Test
	@DisplayName("Get access token fail due to empty token")
	void testGetAccessToken_Fail_EmptyToken() throws InvalidTokenRequestException {

		// ARRANGE: Setting up the mocked services response
		doReturn(AccessTokenTestProperties.EMPTY_EWB_ACCESS_TOKEN_RESPONSE).when(accessTokenWebClientService)
				.executeTokenRequest((AccessTokenTestProperties.TEST_ACCESS_CODE));

		// ACT: Executing the method using mocked access code
		InvalidTokenRequestException invalidTokenRequestException = assertThrows(InvalidTokenRequestException.class,
				() -> accessTokenProvider.requestAccessToken(AccessTokenTestProperties.TEST_ACCESS_CODE));

		// ASSERT: verify the exception message
		assertEquals(MessageConstants.EWB_ACCESS_TOKEN_MISSING_IN_RESPONSE, invalidTokenRequestException.getMessage());

		// check for execution
		verify(accessTokenWebClientService).executeTokenRequest((AccessTokenTestProperties.TEST_ACCESS_CODE));
	}

	@Test
	@DisplayName("Get access token fail due to empty response")
	void testGetAccessToken_Fail_EmptyResponse() throws InvalidTokenRequestException {

		// ARRANGE: Setting up the mocked services response
		doReturn(TestProperties.getEmptyJSONNode()).when(accessTokenWebClientService)
				.executeTokenRequest((AccessTokenTestProperties.TEST_ACCESS_CODE));

		// ACT: Executing the method using mocked access code
		InvalidTokenRequestException invalidTokenRequestException = assertThrows(InvalidTokenRequestException.class,
				() -> accessTokenProvider.requestAccessToken(AccessTokenTestProperties.TEST_ACCESS_CODE));

		// ASSERT: verify the exception message
		assertEquals(MessageConstants.EWB_ACCESS_TOKEN_MISSING_IN_RESPONSE, invalidTokenRequestException.getMessage());

		// check for execution
		verify(accessTokenWebClientService).executeTokenRequest((AccessTokenTestProperties.TEST_ACCESS_CODE));
	}
}
