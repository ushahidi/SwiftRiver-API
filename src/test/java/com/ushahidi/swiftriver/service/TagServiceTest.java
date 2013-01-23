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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.api.service.TagService;
import com.ushahidi.swiftriver.core.model.Tag;
import com.ushahidi.swiftriver.test.AbstractSwiftRiverTest;

/**
 * Integration test for Tags
 * @author ekala
 *
 */
public class TagServiceTest extends AbstractSwiftRiverTest {
	
	private TagDao tagDAO;
	
	private TagService tagService;

	@Before
	public void beforeTest() {
		tagDAO = mock(TagDao.class);
		
		tagService = new TagService();
		tagService.setTagDAO(tagDAO);
	}

	/**
	 * @verifies that a tag is successfully created in the database
	 */
	@Test
	public void testCreateTag() {
		Tag sampleTag  = createSampleTag();
		
		// Verify that the tag doesn't exist
		when(tagDAO.findById(sampleTag.getId())).thenReturn(null);
		
		// Create the tag
		tagService.create(sampleTag);

		verify(tagDAO).create(sampleTag);
	}
	
	/**
	 * @verifies that a tag can be looked up using its hash
	 */
	@Test
	public void testFindTagByHash() {
		String hash = "DKL4947A3084JLMXCYUIABUX";

		tagService.findByHash(hash);
		verify(tagDAO).findByHash(hash);
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
		
		Tag tag = tagService.getTag(Long.MAX_VALUE);
		tagService.delete(tag);

		// Verify that tagDAO.delete was called
		verify(tagDAO).delete(tag);

		when(tagDAO.findById(Long.MAX_VALUE)).thenReturn(null);
	}

}