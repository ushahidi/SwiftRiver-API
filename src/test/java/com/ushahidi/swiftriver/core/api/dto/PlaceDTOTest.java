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
package com.ushahidi.swiftriver.core.api.dto;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

import com.ushahidi.swiftriver.core.model.Place;

/**
 * Unit test for the Place DTO
 * @author ekala
 *
 */
public class PlaceDTOTest {
	
	/**
	 * @verifies the hash created when creating the model matches
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateModel() {
		Object[][] placeData = {
				{"name", "Marsabit"},
				{"coordinates", new Double[]{37.9899, 2.32839}}
		};
		
		PlaceDTO placeDTO = new PlaceDTO();
		Place place = placeDTO.createModel(ArrayUtils.toMap(placeData));

		String expectedHash = "2d67198dc71747308498e9685bdcebc3";
		
		assertEquals(expectedHash, place.getHash());
	}
}
