package com.zifo.ewb.pojo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JSON containing Oauth related data
 * 
 * @author zifo
 */
@Getter
@Setter
@NoArgsConstructor
public class Oauth {

	/**
	 * access code
	 */
	private String accessCode;

}
