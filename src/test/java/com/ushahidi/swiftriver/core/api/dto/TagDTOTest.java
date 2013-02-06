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

import com.ushahidi.swiftriver.core.model.Tag;

/**
 * Unit test for the TagDTO class
 * @author ekala
 *
 */
public class TagDTOTest {
	
	/**
	 * @verifies the hash computed when creating the model
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateModel() {
		TagDTO tagDTO = new TagDTO();

		Object[][] tagData = { {"tag_name", "Uhuru Kenyatta"}, {"tag_type", "person"} };
		Tag tag = tagDTO.createEntityFromMap(ArrayUtils.toMap(tagData));
		
		// Hash value to be expected from the Tag entity created from the DTO
		String expectedHash = "a767beb96688d9807a181202929b456e";
		
		assertEquals(expectedHash, tag.getHash());
	}

}
