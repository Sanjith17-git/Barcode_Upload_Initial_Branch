package com.zifo.gsk.barcodegeneration.helper;

import static com.zifo.ewb.testPropertiesHelper.BarcodeGeneratorTestProperties.ADD_IMAGE_1_JSON;
import static com.zifo.ewb.testPropertiesHelper.BarcodeGeneratorTestProperties.ADD_IMAGE_2_JSON;
import static com.zifo.ewb.testPropertiesHelper.BarcodeGeneratorTestProperties.GET_BARCODE_DATA_SUCCESS_API_RESPONSE;
import static com.zifo.ewb.testPropertiesHelper.BarcodeGeneratorTestProperties.IMAGE_1_ENCODED;
import static com.zifo.ewb.testPropertiesHelper.BarcodeGeneratorTestProperties.IMAGE_2_ENCODED;
import static com.zifo.ewb.testPropertiesHelper.BarcodeGeneratorTestProperties.MOCK_BARCODE;
import static com.zifo.ewb.testPropertiesHelper.BarcodeGeneratorTestProperties.MOCK_FORMAT;
import static com.zifo.ewb.testPropertiesHelper.BarcodeGeneratorTestProperties.MOCK_NON_DD;
import static com.zifo.ewb.testPropertiesHelper.BarcodeGeneratorTestProperties.MOCK_PREFIX;
import static com.zifo.ewb.testPropertiesHelper.BarcodeGeneratorTestProperties.MOCK_SAMPLES;
import static com.zifo.ewb.testPropertiesHelper.BarcodeGeneratorTestProperties.MOCK_SAMPLE_ID_COL;
import static com.zifo.ewb.testPropertiesHelper.BarcodeGeneratorTestProperties.MOCK_TEST_SAMPLE_1;
import static com.zifo.ewb.testPropertiesHelper.BarcodeGeneratorTestProperties.MOCK_TEST_SAMPLE_2;
import static com.zifo.ewb.testPropertiesHelper.BarcodeGeneratorTestProperties.POPULATE_JSON;
import static com.zifo.ewb.testPropertiesHelper.BarcodeGeneratorTestProperties.READ_TABLE_JSON;
import static com.zifo.ewb.testPropertiesHelper.SpreadSheetServiceTestPropertiesHelper.GETDATA_SUCCESS_API_RESPONSE;
import static com.zifo.ewb.testPropertiesHelper.TestProperties.getRequestDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.zifo.ewb.exceptions.InvalidRequestException;
import com.zifo.ewb.pojo.RequestDTO;
import com.zifo.ewb.pojo.ResponseDTO;
import com.zifo.ewb.testPropertiesHelper.AccessTokenTestProperties;
import com.zifo.ewb.testPropertiesHelper.TestProperties;
import com.zifo.ewb.webclient.service.SpreadSheetService;
import com.zifo.gsk.barcodegeneration.constants.MessageConstants;
import com.zifo.gsk.barcodegeneration.utils.BarcodeGeneratorUtils;

/**
 * Class that contains methods to test the PDM Helper methods in PDMHelperTests
 * class
 * 
 * @author Zifo
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Barcode Generator Tests")
class BarcodeGeneratorTests {
	@Mock
	private SpreadSheetService spreadSheetService;

	private final RequestDTO testRequestDTO = getRequestDTO();

	@InjectMocks
	private BarcodeGenerator barcodeGenerator;

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(barcodeGenerator, "samples", MOCK_SAMPLES);
		ReflectionTestUtils.setField(barcodeGenerator, "nonDD", MOCK_NON_DD);
		ReflectionTestUtils.setField(barcodeGenerator, "barcode", MOCK_BARCODE);
		ReflectionTestUtils.setField(barcodeGenerator, "prefix", MOCK_PREFIX);
		ReflectionTestUtils.setField(barcodeGenerator, "format", MOCK_FORMAT);
		ReflectionTestUtils.setField(barcodeGenerator, "sampleIdColumn", MOCK_SAMPLE_ID_COL);
	}

	@Test
	@DisplayName("Test method that tests generate method to generate and upload barcodes")
	void testGenerate_Success() throws InvalidRequestException {

		try (MockedStatic<BarcodeGeneratorUtils> mockedBase64EncoderUtils = mockStatic(BarcodeGeneratorUtils.class)) {
			doReturn(GET_BARCODE_DATA_SUCCESS_API_RESPONSE).when(spreadSheetService).getData(
					TestProperties.TEST_VERSION_ID, READ_TABLE_JSON, AccessTokenTestProperties.TEST_EWB_ACCESS_TOKEN);

			mockedBase64EncoderUtils
					.when(() -> BarcodeGeneratorUtils.generateQRCodeBase64(MOCK_TEST_SAMPLE_1, MOCK_FORMAT))
					.thenReturn(IMAGE_1_ENCODED);

			doReturn(GETDATA_SUCCESS_API_RESPONSE).when(spreadSheetService).getData(TestProperties.TEST_VERSION_ID,
					ADD_IMAGE_1_JSON, AccessTokenTestProperties.TEST_EWB_ACCESS_TOKEN);

			mockedBase64EncoderUtils
					.when(() -> BarcodeGeneratorUtils.generateQRCodeBase64(MOCK_TEST_SAMPLE_2, MOCK_FORMAT))
					.thenReturn(IMAGE_2_ENCODED);

			doReturn(GETDATA_SUCCESS_API_RESPONSE).when(spreadSheetService).getData(TestProperties.TEST_VERSION_ID,
					ADD_IMAGE_2_JSON, AccessTokenTestProperties.TEST_EWB_ACCESS_TOKEN);

			doReturn(GETDATA_SUCCESS_API_RESPONSE).when(spreadSheetService).getData(TestProperties.TEST_VERSION_ID,
					POPULATE_JSON, AccessTokenTestProperties.TEST_EWB_ACCESS_TOKEN);

			doCallRealMethod().when(spreadSheetService).is200or201(GETDATA_SUCCESS_API_RESPONSE);

			// ACT: Executing the method
			ResponseDTO response = barcodeGenerator.generate(AccessTokenTestProperties.TEST_EWB_ACCESS_TOKEN,
					testRequestDTO);

			// ASSERT: verify the response
			assertNotNull(response);
			assertEquals("Barcode Generated Successfully", response.getLongMessage());
			assertEquals("Barcode Generated", response.getShortMessage());
			assertEquals(MessageConstants.OK_DIALOG, response.getOptions().getMessageType());
			assertEquals(MessageConstants.SUCCESS, response.getStatus());

			// INORDER : check order of execution
			InOrder inOrder = inOrder(spreadSheetService);
			inOrder.verify(spreadSheetService).getData(TestProperties.TEST_VERSION_ID, READ_TABLE_JSON,
					AccessTokenTestProperties.TEST_EWB_ACCESS_TOKEN);
			inOrder.verify(spreadSheetService).getData(TestProperties.TEST_VERSION_ID, ADD_IMAGE_1_JSON,
					AccessTokenTestProperties.TEST_EWB_ACCESS_TOKEN);
			inOrder.verify(spreadSheetService).getData(TestProperties.TEST_VERSION_ID, ADD_IMAGE_2_JSON,
					AccessTokenTestProperties.TEST_EWB_ACCESS_TOKEN);
			inOrder.verify(spreadSheetService).getData(TestProperties.TEST_VERSION_ID, POPULATE_JSON,
					AccessTokenTestProperties.TEST_EWB_ACCESS_TOKEN);
		}

	}
}
