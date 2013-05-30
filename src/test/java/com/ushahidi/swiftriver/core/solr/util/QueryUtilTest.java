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
package com.ushahidi.swiftriver.core.solr.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit test for the {@link QueryUtil} class
 * 
 * @author ekala
 */
public class QueryUtilTest {

	/**
	 * Where a single query string has been specified
	 */
	@Test
	public void singleQueryString() {
		String queryString = QueryUtil.getQueryString("ushahidi");
		
		assertEquals("ushahidi", queryString);
	}
	
	/**
	 * Where no query string has been specified
	 */
	@Test
	public void nullQueryString() {
		String nullString = QueryUtil.getQueryString(null);
		assertEquals("*:*", nullString);
		
		String emptyString = QueryUtil.getQueryString("");
		assertEquals(emptyString, nullString);
	}
	
	/**
	 * Where multiple (comma separated) query strings
	 * have been specified
	 */
	@Test
	public void multipleQueryStrings() {
		String queryString = QueryUtil.getQueryString("ushahidi,elections,kenya");
		
		String expected = "ushahidi AND elections AND kenya";
		assertEquals(expected, queryString);
	}
}
