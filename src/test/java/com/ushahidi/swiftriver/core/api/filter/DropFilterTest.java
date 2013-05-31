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
package com.ushahidi.swiftriver.core.api.filter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ushahidi.swiftriver.core.api.exception.InvalidFilterException;
import com.ushahidi.swiftriver.core.api.filter.DropFilter.BoundingBox;

/**
 * Unit test for the {@link DropFilter} class
 * 
 * @author ekala
 */
public class DropFilterTest {

	/**
	 * Verifies that the <code>location</code> property of
	 * the filter is valid 
	 */
	@Test
	public void setValidLocation() {
		DropFilter dropFilter = new DropFilter();
		dropFilter.setBoundingBox("36.8,-122.75,37.8,-121.75");
		
		BoundingBox boundingBox = dropFilter.getBoundingBox();

		assertEquals("36.8", boundingBox.getLatFrom().toString());
		assertEquals("-122.75", boundingBox.getLngFrom().toString());
		assertEquals("37.8", boundingBox.getLatTo().toString());
		assertEquals("-121.75", boundingBox.getLngTo().toString());
	}
	
	@Test(expected = InvalidFilterException.class)
	public void setInvalidLocation() {
		DropFilter dropFilter = new DropFilter();
		dropFilter.setBoundingBox("-122.75,36.8,-121.75,37.8");
	}
	
	@Test(expected = InvalidFilterException.class)
	public void setInvalidBoundingBox() {
		DropFilter dropFilter = new DropFilter();
		dropFilter.setBoundingBox("37.8,-121.75,36.8,-122.75");
	}
}
