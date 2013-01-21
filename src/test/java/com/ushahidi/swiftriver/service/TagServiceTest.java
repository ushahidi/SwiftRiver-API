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
package com.ushahidi.swiftriver.service;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.ushahidi.swiftriver.model.Tag;
import com.ushahidi.swiftriver.test.AbstractSwiftRiverTest;

/**
 * Integration test for Tags
 * @author ekala
 *
 */
public class TagServiceTest extends AbstractSwiftRiverTest {
	
	private TagService tagService;

	@Before
	public void beforeTest() {
		tagService = mock(TagService.class);
	}

	/**
	 * @verifies that a tag is successfully created in the database
	 */
	@Test
	public void testCreateTag() {
		Tag sampleTag  = createSampleTag();
		
		// Verify that the tag doesn't exist
		when(tagService.findById(sampleTag.getId())).thenReturn(null);
		
		// Create the tag
		tagService.create(sampleTag);

		// Verify that the tag has been saved
		when(tagService.findByHash("DKL4947A3084JLMXCYUIABUX")).thenReturn(sampleTag);		
	}
	
	/**
	 * Creates a sample tag object
	 * @return
	 */
	private Tag createSampleTag() {
		Tag tag = new Tag();

		tag.setId(Long.MAX_VALUE);
		tag.setTag("University of Nairobi");
		tag.setTagCanonical("university of nairobi");
		tag.setTagType("organization");
		tag.setHash("DKL4947A3084JLMXCYUIABUX");

		return tag;
	}
	
	
	/**
	 * @verifies that a tag is deleted from the database
	 */
	@Test
	public void testDeleteTag() {
		
		Tag tag = tagService.findById(Long.MAX_VALUE);
		tagService.delete(tag);

		when(tagService.findById(Long.MAX_VALUE)).thenReturn(null);
	}

}
