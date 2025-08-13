/**
 * 
 */
package com.zifo.ewb.exceptions;

/**
 * Custom exception class for throwing when the API request gives
 * invalid/failure response
 */
public class InvalidRequestException extends RuntimeException {

	/**
	 * random generated serialVersion UID for Compatibility during serialization and
	 * deserialization
	 */

	private static final long serialVersionUID = 7804753005230229129L;

	public InvalidRequestException(final String exceptionMessage) {
		super(exceptionMessage);
	}

	public InvalidRequestException(final String exceptionMessage, Throwable cause) {
		super(exceptionMessage, cause);
	}
}
