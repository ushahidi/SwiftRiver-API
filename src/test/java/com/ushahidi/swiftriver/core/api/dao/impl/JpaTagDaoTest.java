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

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.model.Tag;
import com.ushahidi.swiftriver.core.util.HashUtil;

/**
 * {@link TagDao} unit tests 
 * @author ekala
 *
 */
public class JpaTagDaoTest extends AbstractDaoTest {

	@Autowired
	private TagDao tagDao;

	/**
	 * Test for {@link TagDao#save(Tag)} 
	 */
	@Test
	public void testSave() {
		Tag tag = new Tag();

		String tagName = "Ushahidi Inc";
		String hash = HashUtil.md5(tagName + "oragnization");

		tag.setTag(tagName);
		tag.setType(tagName.toLowerCase());
		tag.setType("organization");
		tag.setHash(hash);
		
		tagDao.create(tag);
		
		assertTrue(tag.getId() > 0);
	}
}
