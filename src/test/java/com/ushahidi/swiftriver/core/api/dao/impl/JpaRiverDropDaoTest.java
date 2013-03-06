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
package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.api.dao.LinkDao;
import com.ushahidi.swiftriver.core.api.dao.PlaceDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDropDao;
import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.RiverDrop;
import com.ushahidi.swiftriver.core.model.Tag;

public class JpaRiverDropDaoTest extends AbstractDaoTest {

	@Autowired
	private RiverDropDao riverDropDao;
	
	@Autowired
	private PlaceDao placeDao;
	
	@Autowired
	private LinkDao linkDao;
	
	@Autowired
	private TagDao tagDao;
	
	@Test
	public void addTag() {
		RiverDrop riverDrop = riverDropDao.findById(1L);

		Tag tag = new Tag();
		tag.setTag("Eric Wainaina");
		tag.setType("person");
		
		tagDao.create(tag);

		riverDropDao.addTag(riverDrop, tag);
		assertNotNull(riverDropDao.findTag(riverDrop, tag));
	}
	
	@Test
	public void deleteTag() {
		RiverDrop riverDrop = riverDropDao.findById(1L);
		Tag tag = tagDao.findById(2L);

		assertTrue(riverDropDao.deleteTag(riverDrop, tag));		
	}
	
	@Test
	public void addPlace() {
		RiverDrop riverDrop = riverDropDao.findById(1L);
		
		Place place = new Place();
		place.setPlaceName("Naivasha");
		place.setLatitude(-0.71667f);
		place.setLongitude(36.4359f);
		
		placeDao.create(place);
		
		riverDropDao.addPlace(riverDrop, place);
		assertNotNull(riverDropDao.findPlace(riverDrop, place));
	}

	@Test
	public void deletePlace() {
		RiverDrop riverDrop = riverDropDao.findById(1L);
		Place place = placeDao.findById(5L);
		
		assertTrue(riverDropDao.deletePlace(riverDrop, place));
	}

	@Test
	public void addLink() {
		RiverDrop riverDrop = riverDropDao.findById(1L);
		
		Link link = new Link();
		link.setUrl("http://www.ihub.co.ke");
		linkDao.create(link);
		
		riverDropDao.addLink(riverDrop, link);
		assertNotNull(riverDropDao.findLink(riverDrop, link));
	}
	
	@Test
	public void deleteLink() {
		RiverDrop riverDrop = riverDropDao.findById(1L);
		Link link = linkDao.findById(2L);
		
		assertTrue(riverDropDao.deleteLink(riverDrop, link));
	}
}
