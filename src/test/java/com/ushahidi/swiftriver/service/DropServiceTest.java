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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.test.AbstractSwiftRiverTest;

public class DropServiceTest extends AbstractSwiftRiverTest {
	
	@Autowired
	private DropService dropService;
	
	@Autowired
	private TagService tagService;

	@Autowired
	private PlaceService placeService;

	private Long dropId = new Long(18);
	
	@Override
	@Before
	public void beforeTest() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @verifies that a tag is added to a drop
	 */
	@Test
	public void testAddTag() {		
		int tagCount = dropService.getTags(dropId).size();
		dropService.addTag(dropId, tagService.findById(new Long(12)));
		
		Assert.assertEquals((tagCount + 1), dropService.getTags(dropId).size());
	}
	
	/**
	 * @verifies the added tag has been removed from the drop
	 */
	@Test
	public void testRemoveTag() {
		int tagCount = dropService.getTags(dropId).size();
		int expectedTagCount = tagCount > 0 ? (tagCount - 1) : tagCount;

		dropService.removeTag(dropId, tagService.findById(new Long(12)));
		Assert.assertEquals(expectedTagCount, dropService.getTags(dropId).size());
	}
	
	/**
	 * @verifies that a place is successfully added to a drop
	 */
	@Test
	public void testAddPlace() {
		int placeCount = dropService.getPlaces(dropId).size();
		
		dropService.addPlace(dropId, placeService.findById(new Long(99)));
		Assert.assertEquals((placeCount + 1), dropService.getPlaces(dropId).size());
	}
	
	/**
	 * @verifies a place is successfully removed from a drop
	 */
	@Test
	public void testRemovePlace() {
		int placeCount = dropService.getPlaces(dropId).size();
		int expectedPlaceCount = placeCount > 0 ? (placeCount - 1) : placeCount;

		dropService.removePlace(dropId, placeService.findById(new Long(99)));
		Assert.assertEquals(expectedPlaceCount, dropService.getPlaces(dropId).size());
	}
	
	 
}
