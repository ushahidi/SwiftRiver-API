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
package com.ushahidi.swiftriver.core.mail;

public enum EmailType {

	/**
	 * Account activation email - for newly created accounts 
	 */
	ACTIVATE_ACCOUNT,
	
	/**
	 * Reset password email - where a user has forgotten
	 * their password
	 */
	RESET_PASSWORD; 
	
	/**
	 * Returns the path for the Velocity template to be used
	 * for the {@link EmailType} specified in <code>emailType</code>
	 *  
	 * @param emailType
	 * @return
	 */
	public static String getTemplateLocation(EmailType emailType) {
		switch(emailType) {
		case ACTIVATE_ACCOUNT:
			return "templates/velocity/activate-account.vm";

		case RESET_PASSWORD:
			return "templates/velocity/reset-password.vm";
		}
		return null;
	}
}
