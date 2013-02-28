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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.model.Tag;

/**
 * {@link TagDao} unit tests 
 * @author ekala
 *
 */
public class JpaTagDaoTest extends AbstractDaoTest {

	@Autowired
	private TagDao tagDao;

	/**
	 * Test for {@link TagDao#create(Tag)} 
	 */
	@Test
	public void testCreate() {
		Tag tag = new Tag();

		tag.setTag("Ushahidi Inc");
		tag.setType("organization");
		
		tagDao.create(tag);		
		assertTrue(tag.getId() > 0);
		assertEquals("ushahidi inc", tag.getTagCanonical());
		assertEquals("75ff52b5876a910d6b1c0c273076f8de", tag.getHash());
	}
}
