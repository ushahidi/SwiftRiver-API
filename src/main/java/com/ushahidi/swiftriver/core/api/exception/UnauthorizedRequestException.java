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

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Generates a 401 HTTP status code. This exception should be thrown
 * when an un-authenticated request is made to a resource that requires
 * authentication; it signals the client to re-authenticate
 * 
 * @author ekala
 *
 */
@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedRequestException extends RuntimeException {

	private static final long serialVersionUID = -6741600228177740936L;
	
	public UnauthorizedRequestException() { }

	public UnauthorizedRequestException(String message) {
		super(message);
	}
	
	public UnauthorizedRequestException(String message, Throwable cause) {
		super(message, cause);
	}

}
