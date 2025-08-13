package com.zifo.gsk.barcodegeneration.controller;

import static com.zifo.ewb.testPropertiesHelper.AccessTokenTestProperties.TEST_ACCESS_CODE;
import static com.zifo.ewb.testPropertiesHelper.AccessTokenTestProperties.TEST_EWB_ACCESS_TOKEN;
import static com.zifo.ewb.testPropertiesHelper.TestProperties.getRequestDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.zifo.ewb.exceptions.InvalidTokenRequestException;
import com.zifo.ewb.helper.AccessTokenProvider;
import com.zifo.ewb.pojo.RequestDTO;
import com.zifo.ewb.pojo.ResponseDTO;
import com.zifo.gsk.barcodegeneration.constants.MessageConstants;
import com.zifo.gsk.barcodegeneration.helper.BarcodeGenerator;
import com.zifo.gsk.barcodegeneration.utils.ResponseUtils;

/**
 * Class that contains methods to test the Bioreg Pull method in Mosaic class
 * 
 * @author Zifo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Barcode Generator Controller Tests")
class BarcodeGeneratorControllerTests {

	/**
	 * Dependency objects
	 */
	@Mock
	private AccessTokenProvider accessTokenProvider;

	@Mock
	private BarcodeGenerator barcodeGenerator;

	@InjectMocks
	private BarcodeGeneratorController barcodeGeneratorController;

	private final RequestDTO testRequestDTO = getRequestDTO();

	/**
	 * Mocking necessary dependency class objects
	 */
	@BeforeEach
	void setup() {
		// ARRANGE: Initialize the services and injecting the properties fields
	}

	@Test
	@DisplayName("Method to test generateBarcode method when barcode is generated successfully")
	void testGenerateBarcode_Success() {

		doReturn(TEST_EWB_ACCESS_TOKEN).when(accessTokenProvider).requestAccessToken(TEST_ACCESS_CODE);

		doReturn(ResponseUtils.generateResponse(Boolean.TRUE, 1)).when(barcodeGenerator)
				.generate(TEST_EWB_ACCESS_TOKEN, testRequestDTO);

		// ACT: Executing the method using mocked request DTO
		ResponseDTO responseDTO = barcodeGeneratorController.generateBarcode(testRequestDTO);

		// ASSERT: verify the response
		assertNotNull(responseDTO);
		assertEquals("Barcode Generated Successfully", responseDTO.getLongMessage());
		assertEquals("Barcode Generated", responseDTO.getShortMessage());
		assertEquals(MessageConstants.OK_DIALOG, responseDTO.getOptions().getMessageType());
		assertEquals(MessageConstants.SUCCESS, responseDTO.getStatus());

		// INORDER : check order of execution
		InOrder inOrder = inOrder(accessTokenProvider, barcodeGenerator);
		inOrder.verify(accessTokenProvider).requestAccessToken(TEST_ACCESS_CODE);
		inOrder.verify(barcodeGenerator).generate(TEST_EWB_ACCESS_TOKEN, testRequestDTO);
	}

	@Test
	@DisplayName("Method to test generateBarcode method when barcode is generated successfully")
	void testGenerateBarcode_InvalidTokenRequestException() {

		doThrow(new InvalidTokenRequestException(MessageConstants.EWB_ACCESS_TOKEN_MISSING_IN_RESPONSE))
				.when(accessTokenProvider).requestAccessToken(TEST_ACCESS_CODE);

		// ACT: Executing the method using mocked request DTO
		ResponseDTO responseDTO = barcodeGeneratorController.generateBarcode(testRequestDTO);

		// ASSERT: verify the response
		assertNotNull(responseDTO);
		assertEquals("Error while generating the barcode", responseDTO.getLongMessage());
		assertEquals("Error Occured", responseDTO.getShortMessage());
		assertEquals(MessageConstants.OK_DIALOG, responseDTO.getOptions().getMessageType());
		assertEquals(MessageConstants.FAILURE, responseDTO.getStatus());

		// VERIFY : verify method execution
		verify(accessTokenProvider).requestAccessToken(TEST_ACCESS_CODE);
	}
}
