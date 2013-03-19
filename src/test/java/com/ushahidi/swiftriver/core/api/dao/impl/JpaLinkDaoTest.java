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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.api.dao.LinkDao;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Link;

public class JpaLinkDaoTest extends AbstractDaoTest {

	@Autowired
	private LinkDao linkDao;

	/**
	 * Test for {@link LinkDao#save(Link)}
	 */
	@Test
	public void testCreate() {
		Link link = new Link();
		link.setUrl("http://www.ushahidi.com");
		linkDao.create(link);

		assertTrue(link.getId() > 0);
		assertEquals("242dc4c581d755a10217eea313038209", link.getHash());
	}

	@Test
	public void getLinks() {
		List<Link> links = new ArrayList<Link>();
		Link existingLink = new Link();
		existingLink.setUrl("http://www.bbc.co.uk/sport/0/football/20319573");
		links.add(existingLink);
		Link newLink = new Link();
		newLink.setUrl("http://example.com/new ");
		links.add(newLink);

		List<Drop> drops = new ArrayList<Drop>();
		Drop drop = new Drop();
		drop.setId(5);
		drop.setLinks(links);
		drop.setOriginalUrl(newLink);
		drops.add(drop);

		linkDao.getLinks(drops);

		assertEquals(10, existingLink.getId());

		String sql = "SELECT hash, url " + "FROM links WHERE id = ?";

		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql,
				newLink.getId());

		assertEquals("44b764d6f4dab845f031ba9e52f61d95", results.get("hash"));
		assertEquals("http://example.com/new", results.get("url"));

		sql = "SELECT link_id FROM droplets_links WHERE droplet_id = 5";

		List<Long> dropletLinks = this.jdbcTemplate
				.queryForList(sql, Long.class);
		assertEquals(3, dropletLinks.size());
		assertTrue(dropletLinks.contains(newLink.getId()));
		
		sql = "SELECT original_url FROM droplets WHERE id = 5";
		assertEquals(newLink.getId(), this.jdbcTemplate.queryForLong(sql));
	}
	
	/**
	 * Drop with null links in list should not cause exception
	 */
	@Test
	public void getLinksWithDropInListMissingLinks() {
		List<Link> links = new ArrayList<Link>();
		Link existingLink = new Link();
		existingLink.setUrl("http://www.bbc.co.uk/sport/0/football/20319573");
		links.add(existingLink);
		Link newLink = new Link();
		newLink.setUrl("http://example.com/new ");
		links.add(newLink);

		List<Drop> drops = new ArrayList<Drop>();
		Drop dropWithoutLinks = new Drop();
		dropWithoutLinks.setId(5);
		drops.add(dropWithoutLinks);
		Drop drop = new Drop();
		drop.setId(5);
		drop.setLinks(links);
		drops.add(drop);

		linkDao.getLinks(drops);

		assertEquals(10, existingLink.getId());

		String sql = "SELECT hash, url " + "FROM links WHERE id = ?";

		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql,
				newLink.getId());

		assertEquals("44b764d6f4dab845f031ba9e52f61d95", results.get("hash"));
		assertEquals("http://example.com/new", results.get("url"));

		sql = "SELECT link_id FROM droplets_links WHERE droplet_id = 5";

		List<Long> dropletLinks = this.jdbcTemplate
				.queryForList(sql, Long.class);
		assertEquals(3, dropletLinks.size());
		assertTrue(dropletLinks.contains(newLink.getId()));
	}
}
