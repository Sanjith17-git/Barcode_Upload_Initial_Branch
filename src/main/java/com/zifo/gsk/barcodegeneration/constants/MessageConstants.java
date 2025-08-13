package com.zifo.gsk.barcodegeneration.constants;

import java.text.MessageFormat;

import org.springframework.http.HttpRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.experimental.UtilityClass;

/**
 * 
 * Message constants are present in this class
 * 
 * @author zifo
 *
 */
@UtilityClass
public class MessageConstants {
	
	/* Strings used for building some message Strings internally in this class */
	private static final String FAILED_LOWER_CASE = "failed";
	/** Field Error 404 */
	public static final String ERROR_404_MSG = "Unable to communicate with the Study Director dependent Web Service";
	/** Field Error 401 */
	public static final String ERROR_401_MSG = "Authorization has been denied for this request.";
	/** Field Internal Server Error */
	public static final String INTL_SERVER_ERROR = "Internal server error occurred. Please contact your Administrator";
	/** Field Success */
	public static final String SUCCESS = "SUCCESS";
	/** Field Failre */
	public static final String FAILURE = "FAILURE";
	/** Field Ok */
	public static final String OK_DIALOG = "notification";
	/** Field Study Num Invalid */
	public static final String STUDY_NO_INVALID = "Please select a valid Study Number and try again";
	/** Field Tbale Not Found */
	public static final String TABLE_NOT_FOUND = "Table not found";
	/** Field Invalid Template Message */
	public static final String INVALID_TEMPLATE_MSG = "Please make sure a valid template is used. The following table(s) are missing : ";
	/** Field Export Word */
	public static final String EXPORT_WORD="Export to Word failed";
	/** Field Word Report */	
	public static final String WORD_REPORT = "Data Pushed";
	public static final String TOKEN_GENERATION_STARTED = "Generating the access token from IDBS EWB";

	public static final String TOKEN_GENERATION_EWB_FAIL = MessageFormat
			.format("Generating the access token from IDBS EWB {0}", FAILED_LOWER_CASE);

	public static final String GOT_TOKEN_RESPONSE = "Got token response";

	public static final String EWB_ACCESS_TOKEN_MISSING_IN_RESPONSE = "Access token is missing in EWB token response";

	public static final String EWB_ACCESS_TOKEN_SUCCESS = "EWB Access token is generated successfully";

	/**
	 * Method that returns a String to log the details of webclient API exception
	 * 
	 * @param message            - Message related to the API triggered
	 * @param webClientException - Exception object
	 * @return String - Log message
	 */
	public static final String getWebClientAPIErrorLog(String message, WebClientResponseException webClientException) {

		final String apiErrorLogPattern = "Message : {0}, Request URI : {1}, Status Code : {2}, Response : {3}";

		HttpRequest request = webClientException.getRequest();

		return MessageFormat.format(apiErrorLogPattern, message, request == null ? "Unknown URI" : request.getURI(),
				webClientException.getStatusCode(), webClientException.getResponseBodyAsString());
	}

	/**
	 * Method that returns a log message with the exception class name, exception
	 * message, and its cause. This is useful for logging exceptions when dealing
	 * with parent-child relationships where a child exception might be wrapped
	 * inside a parent exception.
	 *
	 * @param exception - The exception object to log
	 * @return String - Formatted log message
	 */
	public static final String getExceptionLog(Exception exception) {
		// Define the log message format, including exception class, message, and cause
		final String apiErrorLogPattern = "Exception : {0}, Message : {1}, Cause : {2}";

		return MessageFormat.format(apiErrorLogPattern, exception.getClass().getSimpleName(), exception.getMessage(),
				exception.getCause() != null ? exception.getCause().toString() : "No known cause");
	}
	
	public static final String NULL_OR_EMPTY_RESPONSE_BODY = "Response body is null/empty while getting the table data";

	public static final String NULL_RESPONSE = "Response is null while getting the table data!";

	public static final String UNABLE_TO_READ_WRITE_TABLE_DATA = "Unable to read/write spreadsheet table data";


}
