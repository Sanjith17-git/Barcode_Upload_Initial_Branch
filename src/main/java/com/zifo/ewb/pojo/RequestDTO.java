package com.zifo.ewb.pojo;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * DTO for request
 * 
 * @author Zifo
 *
 */
public class RequestDTO {

	/**
	 * spreadsheet to be accessed
	 */
	private Spreadsheet spreadsheet;

	/**
	 * Parent of the entity
	 */
	private Parent parent;

	/**
	 * Object of class Action
	 */
	private Action action;

	/**
	 * Object of class Oauth
	 */
	private Oauth oauth;

	/**
	 * 
	 */
	@JsonIgnore
	private Map<String, Object> additionalProperties = new HashMap<>();

	/**
	 * non-parameterized constructor
	 */
	public RequestDTO() {
		super();
	}

	public Spreadsheet getSpreadsheet() {
		return spreadsheet;
	}

	public void setSpreadsheet(final Spreadsheet spreadsheet) {

		this.spreadsheet = spreadsheet;
	}

	public Parent getParent() {

		return parent;
	}

	public void setParent(final Parent parent) {
		this.parent = parent;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(final Action action) {
		this.action = action;
	}

	public Oauth getOauth() {
		return oauth;
	}

	public void setOauth(final Oauth oauth) {
		this.oauth = oauth;
	}

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
