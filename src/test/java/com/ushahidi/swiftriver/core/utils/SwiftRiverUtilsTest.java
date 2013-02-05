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
package com.ushahidi.swiftriver.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * SwiftRiverUtils unit tests 
 * @author ekala
 *
 */
public class SwiftRiverUtilsTest {
	
	/**
	 * @verifies generation of URL slug from a phrase
	 */
	@Test
	public void testGetURLSlug() {
		String phrase = "Test URL slUg GENERATION";
		
		String expectedSlug = "test-url-slug-generation";
		assertEquals(expectedSlug, SwiftRiverUtils.getURLSlug(phrase));
	}
	
	/**
	 * Verifies generation of an MD5 hash from a list of
	 * strings 
	 */
	@Test
	public void testGetMD5Hash() {
		String expectedHash = "b8d83f3ff6c3301d4c7c5580c0b8cdab";
		assertEquals(expectedHash, SwiftRiverUtils.getMD5Hash("Test ", "MD5 ", "Generation"));
	}
}
