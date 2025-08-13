/**
 * 
 */
package com.zifo.ewb.exceptions;

/**
 * Custom exception class for throwing when the token request is invalid
 */
public class InvalidTokenRequestException extends InvalidRequestException {

	/**
	 * random generated serialVersion UID for Compatibility during serialization and
	 * deserialization
	 */
	private static final long serialVersionUID = -640475844985117163L;

	public InvalidTokenRequestException(final String exceptionMessage) {
		super(exceptionMessage);
	}

	public InvalidTokenRequestException(String exceptionMessage, Throwable cause) {
		super(exceptionMessage, cause);
	}
}
