package com.zifo.ewb.pojo;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JSON containing User related data
 * 
 * @author zifo
 */
@Getter
@Setter
@NoArgsConstructor
public class Spreadsheet {
	/**
	 * version id
	 */
	private String versionId;

	/**
	 * 
	 * @return
	 */
	public String getVersionId() {
		return versionId;
	}

	/**
	 * 
	 * @param versionId
	 */
	public void setVersionId(final String versionId) {
		this.versionId = versionId;
	}

	/**
	 * 
	 */
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<>();

	/**
	 * 
	 * @return
	 */
	@JsonAnyGetter
	public Map<String, Object> getAdditionalProperties() {
		return this.additionalProperties;
	}

	/**
	 * 
	 * @param name
	 * @param value
	 */
	@JsonAnySetter
	public void setAdditionalProperty(final String name, final Object value) {
		this.additionalProperties.put(name, value);
	}

}
