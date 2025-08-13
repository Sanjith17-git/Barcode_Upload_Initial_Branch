package com.zifo.gsk.barcodegeneration.utils;

import com.zifo.ewb.pojo.Options;
import com.zifo.ewb.pojo.ResponseDTO;
import com.zifo.gsk.barcodegeneration.constants.MessageConstants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ResponseUtils {
	public static ResponseDTO generateResponse(final boolean status, final int code) {
		final ResponseDTO response = new ResponseDTO();
		if (status && code == 1) {
			response.setLongMessage("Barcode Generated Successfully");
			response.setShortMessage("Barcode Generated");
			response.setStatus(MessageConstants.SUCCESS);
			final Options options = new Options();
			options.setMessageType(MessageConstants.OK_DIALOG);
			response.setOptions(options);
		} else if (Boolean.FALSE.equals(status) && code == 0) {
			response.setLongMessage("Sample Ids cannot be Empty");
			response.setShortMessage("Sample Ids are Empty");
			response.setStatus(MessageConstants.FAILURE);
			final Options options = new Options();
			options.setMessageType(MessageConstants.OK_DIALOG);
			response.setOptions(options);
		} else {
			response.setLongMessage("Error while generating the barcode");
			response.setShortMessage("Error Occured");
			response.setStatus(MessageConstants.FAILURE);
			final Options options = new Options();
			options.setMessageType(MessageConstants.OK_DIALOG);
			response.setOptions(options);
		}
		return response;
	}
}
