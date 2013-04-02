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


import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.BucketDropDao;
import com.ushahidi.swiftriver.core.api.dao.LinkDao;
import com.ushahidi.swiftriver.core.api.dao.PlaceDao;
import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.model.BucketDrop;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.Tag;

public class JpaBucketDropDaoTest extends AbstractJpaDaoTest {
	
	@Autowired
	private BucketDropDao bucketDropDao;
	
	@Autowired
	private PlaceDao placeDao;

	@Autowired
	private TagDao tagDao;

	@Autowired
	private LinkDao linkDao;

	/**
	 * Test for {@link BucketDropDao#addPlace(BucketDrop, Place)}
	 */
	@Test
	public void testAddPlace() {
  		BucketDrop bucketDrop = bucketDropDao.findById(1L);		
		
		Place place = new Place();
		place.setPlaceName("Nyeri");
		place.setLatitude(-0.42013f);
		place.setLongitude(36.9476f);
		
		placeDao.create(place);

		bucketDropDao.addPlace(bucketDrop, place);
		assertNotNull(bucketDropDao.findPlace(bucketDrop, place));
	}
	
	/**
	 * Test for {@link BucketDropDao#addPlace(BucketDrop, Place)}
	 */
	@Test
	public void testAddTag() {
		BucketDrop bucketDrop = bucketDropDao.findById(1L);
		
		Tag tag = new Tag();
		tag.setTag("Ushahidi Inc");
		tag.setType("organization");
		
		tagDao.create(tag);
		
		bucketDropDao.addTag(bucketDrop, tag);
		assertNotNull(bucketDropDao.findTag(bucketDrop, tag));
	}
	
	/**
	 * Test for {@link BucketDropDao#addLink(BucketDrop, Link)}
	 */
	@Test
	public void testAddLink() {
		BucketDrop bucketDrop = bucketDropDao.findById(1L);
		
		Link link = new Link();
		link.setUrl("http://www.ihub.co.ke");
		linkDao.create(link);
		
		bucketDropDao.addLink(bucketDrop, link);
		assertNotNull(bucketDropDao.findLink(bucketDrop, link));
	}
	
	@Test
	public void findForm() {
		assertNotNull(bucketDropDao.findForm(2L, 1L));
	}
}
