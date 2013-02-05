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
 */package com.ushahidi.swiftriver.core.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Generates a 403 HTTP status code. This exception should be thrown
 * when access to the resource has been denied by virtue of insufficient
 * privileges. It signals the client to stop requesting for the resource
 * 
 * @author ekala
 *
 */
 @ResponseStatus(value = HttpStatus.FORBIDDEN)
public class ForbiddenRequestException extends RuntimeException {

	private static final long serialVersionUID = -5181429160292524717L;

	public ForbiddenRequestException(String message) {
		super(message);
	}
	
	public ForbiddenRequestException(String message, Throwable cause) {
		super(message, cause);
	}
}
