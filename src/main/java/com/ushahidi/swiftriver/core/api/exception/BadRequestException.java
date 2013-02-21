/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/agpl.html>
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.core.api.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a malformed request is received.
 *
 */
@ResponseStatus(value=HttpStatus.BAD_REQUEST, reason="Malformed request.")
public class BadRequestException extends RuntimeException {
	
	private static final long serialVersionUID = 7737582642439602394L;
	
	private List<ErrorField> errors;

	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException() {
		super();
	}

	public BadRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public BadRequestException(Throwable cause) {
		super(cause);
	}

	public List<ErrorField> getErrors() {
		return errors;
	}

	public void setErrors(List<ErrorField> errors) {
		this.errors = errors;
	}	
}
