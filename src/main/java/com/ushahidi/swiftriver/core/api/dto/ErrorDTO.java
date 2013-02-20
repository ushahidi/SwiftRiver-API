package com.ushahidi.swiftriver.core.api.dto;

import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.ushahidi.swiftriver.core.api.exception.ErrorField;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class ErrorDTO {
	
	private String message;
	
	private List<ErrorField> errors;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
	public List<ErrorField> getErrors() {
		return errors;
	}

	public void setErrors(List<ErrorField> errors) {
		this.errors = errors;
	}

}
