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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.model.Drop;
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

		tag.setTag(" Ushahidi Inc ");
		tag.setType(" organization ");
		
		tagDao.create(tag);		
		assertTrue(tag.getId() > 0);
		assertEquals("ushahidi inc", tag.getTagCanonical());
		assertEquals("75ff52b5876a910d6b1c0c273076f8de", tag.getHash());
	}
	
	@Test
	public void getTags() {
		List<Tag> tags = new ArrayList<Tag>();
		Tag existingTag = new Tag();
		existingTag.setTag("NHS");
		existingTag.setType("organization");
		tags.add(existingTag);
		Tag newTag = new Tag();
		newTag.setTag(" Test tag ");
		newTag.setType(" Just a test ");
		tags.add(newTag);
		
		List<Drop> drops = new ArrayList<Drop>();
		Drop drop = new Drop();
		drop.setId(5);
		drop.setTags(tags);
		drops.add(drop);
		
		tagDao.getTags(drops);
		
		assertEquals(2, existingTag.getId());
		
		String sql = "SELECT `hash`, `tag`, `tag_canonical`, `tag_type` " +
				"FROM `tags` WHERE `id` = ?";

		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql,
				newTag.getId());
		
		assertEquals("a47f130478358335467f0d1e91ff044a", results.get("hash"));
		assertEquals("Test tag", results.get("tag"));
		assertEquals("test tag", results.get("tag_canonical"));
		assertEquals("just a test", results.get("tag_type"));
		
		sql = "SELECT `tag_id` FROM `droplets_tags` WHERE `droplet_id` = 5";
		
		List<Long> dropletTags = this.jdbcTemplate.queryForList(sql, Long.class);
		assertEquals(3, dropletTags.size());
		assertTrue(dropletTags.contains(newTag.getId()));
	}
	
	/**
	 * A drop with null tags should not cause any exception
	 */
	@Test
	public void getTagsWithDropInListMissingTags() {
		List<Tag> tags = new ArrayList<Tag>();
		Tag existingTag = new Tag();
		existingTag.setTag("NHS");
		existingTag.setType("organization");
		tags.add(existingTag);
		Tag newTag = new Tag();
		newTag.setTag(" Test tag ");
		newTag.setType(" Just a test ");
		tags.add(newTag);
		
		List<Drop> drops = new ArrayList<Drop>();
		Drop dropWithoutTags = new Drop();
		dropWithoutTags.setId(1);
		drops.add(dropWithoutTags);
		Drop drop = new Drop();
		drop.setId(5);
		drop.setTags(tags);
		drops.add(drop);
		
		tagDao.getTags(drops);
	}
}
