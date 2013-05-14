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
package com.ushahidi.swiftriver.core.api.auth;

public enum AuthenticationScheme {

	/**
	 * Use the application's DB to authenticate users 
	 */
	DEFAULT,
	
	/**
	 * User CrowdmapID to authenticate users
	 */
	CROWDMAPID;
	
	/**
	 * Gets the {@link AuthenticationScheme} given it's canonical
	 * name. 
	 * 
	 * {@link AuthenticationScheme#DEFAULT} is the default
	 * scheme
	 * 
	 * @param schemeName
	 * @return
	 */
	public static AuthenticationScheme getScheme(String schemeName) {
		if (schemeName.toLowerCase().equals("crowdmapid")) {
			return CROWDMAPID;
		}
		
		return DEFAULT;
	}
}
