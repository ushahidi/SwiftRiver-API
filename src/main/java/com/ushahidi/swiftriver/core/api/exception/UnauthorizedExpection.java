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
 * Thrown when a client is not authorized 
 */
@ResponseStatus(value=HttpStatus.UNAUTHORIZED, reason="You are not authorized to access the resource")
public class UnauthorizedExpection extends RuntimeException {

	private static final long serialVersionUID = -1793327149823107162L;
	
	public UnauthorizedExpection() {
		super();
	}
	
	public UnauthorizedExpection(String message) {
		super(message);
	}
	
	public UnauthorizedExpection(String message, Throwable t) {
		super(message, t);
	}

}
