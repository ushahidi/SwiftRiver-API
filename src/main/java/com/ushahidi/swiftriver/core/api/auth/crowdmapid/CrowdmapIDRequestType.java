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
package com.ushahidi.swiftriver.core.api.auth.crowdmapid;

public enum CrowdmapIDRequestType {

	/**
	 * Login request
	 */
	SIGNIN,
	
	/**
	 * Request to register for a new account
	 */
	REGISTER,
	
	/**
	 * Change password request
	 */
	CHANGEPASSWORD,
	
	/**
	 * Reset password request; for use where a user
	 * has forgotten their password
	 */
	REQUESTPASSWORD,
	
	/**
	 * Set password request; for a user who has already
	 * sent a <code>REQUESTPASSWORD</code> request and
	 * received a password reset token 
	 */
	SETPASSWORD;
	
	
}
