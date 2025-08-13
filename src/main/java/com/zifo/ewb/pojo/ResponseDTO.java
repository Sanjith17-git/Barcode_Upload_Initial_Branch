package com.zifo.ewb.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JSON containing All Output data given by IDBS EWB
 * 
 * @author zifo
 */
@Getter
@Setter
@NoArgsConstructor
public class ResponseDTO {
	/**
	 * status
	 */
	private String status;

	/**
	 * short message
	 */
	private String shortMessage;

	/**
	 * long message
	 */
	private String longMessage;

	/**
	 * options
	 */
	private Options options;

}
