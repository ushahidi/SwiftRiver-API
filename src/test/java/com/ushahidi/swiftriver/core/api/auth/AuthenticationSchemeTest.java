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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for {@link AuthenticationScheme}
 * 
 * @author ekala
 */
public class AuthenticationSchemeTest {

	/**
	 * Verifies that given a scheme name, an 
	 * <code>com.ushahidi.swiftriver.core.api.auth.AuthenticationScheme</code>
	 * object is returned
	 */
	@Test
	public void getAuthenticationScheme() {
		// When the crowdmapid auth scheme is specified
		AuthenticationScheme authScheme = AuthenticationScheme.getScheme("crowdmapid");
		assertEquals(AuthenticationScheme.CROWDMAPID, authScheme);
		
		// When an invalid authentication scheme name is specified
		AuthenticationScheme defaultScheme = AuthenticationScheme.getScheme("unknown-scheme");
		assertEquals(AuthenticationScheme.DEFAULT, defaultScheme);
	}
}
