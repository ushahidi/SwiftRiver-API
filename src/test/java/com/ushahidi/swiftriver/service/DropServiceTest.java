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

import org.junit.Before;
import org.junit.Test;

import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.api.dao.PlaceDao;
import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.Tag;
import com.ushahidi.swiftriver.test.AbstractSwiftRiverTest;

public class DropServiceTest extends AbstractSwiftRiverTest {

	/** Service interfaces under test */	
	private DropService dropService;
	
	private TagService tagService;

	private PlaceService placeService;

	/** DAO interfaces under test */
	private DropDao dropDAO;

	private TagDao tagDAO;
	
	private PlaceDao placeDAO;

	/* Drop id to be used for the test */
	private Long dropId = new Long(18);
	
	@Override
	@Before
	public void beforeTest() {
		// Drop servoce
		dropService = new DropService();
		dropDAO = mock(DropDao.class);
		dropService.setDropDAO(dropDAO);

		// Tag service
		tagService = new TagService();
		tagDAO = mock(TagDao.class);
		tagService.setTagDAO(tagDAO);
		
		// Places
		placeService = new PlaceService();
		placeDAO = mock(PlaceDao.class);
		placeService.setPlaceDAO(placeDAO);
	}

	@Test
	public void testGetDrop() {
		dropService.getDrop(dropId);
		verify(dropDAO).findById(dropId);
	}

	/**
	 * @verifies that a tag is added to a drop
	 */
	@Test
	public void testAddTag() {
		Tag tag = tagService.getTag(new Long(12));		
		verify(tagDAO).findById(new Long(12));

		dropService.addTag(dropId, tag);
		verify(dropDAO).addTag(dropId, tag);
	}
	
	/**
	 * @verifies the added tag has been removed from the drop
	 */
	@Test
	public void testRemoveTag() {
		Tag tag =  tagService.getTag(new Long(12));
		verify(tagDAO).findById(new Long(12));

		dropService.removeTag(dropId, tag);
		verify(dropDAO).removeTag(dropId, tag);
	}
	
	/**
	 * @verifies that a place is successfully added to a drop
	 */
	@Test
	public void testAddPlace() {
		Place place = placeService.getPlace(new Long(99));
		verify(placeDAO).findById(new Long(99));
		
		dropService.addPlace(dropId, place);
		verify(dropDAO).addPlace(dropId, place);
	}
	
	/**
	 * @verifies a place is successfully removed from a drop
	 */
	@Test
	public void testRemovePlace() {
		Place place = placeService.getPlace(new Long(99));
		verify(placeDAO).findById(new Long(99));
		
		dropService.removePlace(dropId, place);
		verify(dropDAO).removePlace(dropId, place);
	}
	
	 
}
