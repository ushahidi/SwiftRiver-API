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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.RiverCollaboratorDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Media;
import com.ushahidi.swiftriver.core.model.MediaThumbnail;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverCollaborator;
import com.ushahidi.swiftriver.core.model.RiverDropForm;
import com.ushahidi.swiftriver.core.model.RiverDropFormField;
import com.ushahidi.swiftriver.core.model.Tag;
import com.ushahidi.swiftriver.core.model.drop.DropFilter;
import com.ushahidi.swiftriver.core.util.TextUtil;

public class JpaRiverDaoTest extends AbstractJpaDaoTest {

	@Autowired
	RiverDao riverDao;

	@Autowired
	RiverCollaboratorDao riverCollaboratorDao;

	@Autowired
	AccountDao accountDao;

	@Test
	public void findById() {
		River r = riverDao.findById(1L);

		assertEquals("Public River 1", r.getRiverName());
	}

	@Test
	public void findByName() {
		River r = riverDao.findByName("Private River 1");

		assertEquals(2, (long) r.getId());
	}

	@Test
	public void findNonExistentByName() {
		River r = riverDao.findByName("River That Doesn't Exist");

		assertNull(r);
	}

	@Test
	public void getDrops() {
		Account account = accountDao.findById(1L);
		DropFilter filter = new DropFilter();
		List<Drop> drops = riverDao.getDrops(1L, filter, 1, 10, account);

		assertEquals(5, drops.size());

		Drop drop = drops.get(0);
		assertEquals(5, drop.getId());
		assertEquals(false, drop.getRead());
		assertEquals("rss", drop.getChannel());
		assertEquals("droplet_5_title", drop.getTitle());
		assertEquals("droplet_5_content", drop.getContent());
		assertEquals("5", drop.getOriginalId());
		assertEquals(30, drop.getCommentCount());
		assertEquals(1, drop.getIdentity().getId());
		assertEquals("identity1_name", drop.getIdentity().getName());
		assertEquals("identity1_avatar", drop.getIdentity().getAvatar());
		assertNotNull(drop.getOriginalUrl());
		assertEquals(3, drop.getOriginalUrl().getId());
		assertEquals("http://www.bbc.co.uk/nature/20273855", drop
				.getOriginalUrl().getUrl());

		assertEquals(2, drop.getTags().size());
		Tag tag = drop.getTags().get(0);
		assertEquals(1, tag.getId());
		assertEquals("Jeremy Hunt", tag.getTag());
		assertEquals("person", tag.getType());

		assertEquals(2, drop.getLinks().size());
		Link link = drop.getLinks().get(0);
		assertEquals(2, link.getId());
		assertEquals(
				"http://news.bbc.co.uk/democracylive/hi/house_of_commons/newsid_9769000/9769109.stm#sa-ns_mchannel=rss&amp;ns_source=PublicRSS20-sa&quot;",
				link.getUrl());

		assertEquals(2, drop.getMedia().size());
		Media media = drop.getMedia().get(0);
		assertEquals(1, media.getId());
		assertEquals(
				"http://gigaom2.files.wordpress.com/2012/10/datacapspercentage.jpeg",
				media.getUrl());
		assertEquals("image", media.getType());

		assertEquals(2, media.getThumbnails().size());
		MediaThumbnail thumbnail = media.getThumbnails().get(0);
		assertEquals(
				"https://2bcbd22fbb0a02d76141-1680e9dfed1be27cdc47787ec5d4ef89.ssl.cf1.rackcdn.com/625dd7cb656d258b4effb325253e880631699d80345016e9e755b4a04341cda1.peg",
				thumbnail.getUrl());
		assertEquals(80, thumbnail.getSize());

		assertEquals(2, drop.getPlaces().size());
		Place place = drop.getPlaces().get(0);
		assertEquals(1, place.getId());
		assertEquals("Wales", place.getPlaceName());
		assertEquals(new Float(146.11), place.getLongitude());
		assertEquals(new Float(-33), place.getLatitude());

		drop = drops.get(3);
		assertEquals(2, drop.getId());
		assertEquals(2, drop.getForms().size());
		
		RiverDropForm form = (RiverDropForm) drop.getForms().get(0);
		assertEquals(1, (long)form.getId());
		List<RiverDropFormField> values = form.getValues();
		assertEquals(3, values.size());
		assertEquals(1, (long)values.get(0).getField().getId());
		assertEquals("[\"English\"]", values.get(0).getValue());
		assertEquals(2, (long)values.get(1).getField().getId());
		assertEquals("\"Journalist\"", values.get(1).getValue());
		assertEquals(3, (long)values.get(2).getField().getId());
		assertEquals("\"Kenyans\"", values.get(2).getValue());
	}

	@Test
	public void getDropsForChannelId() {
		Account account = accountDao.findById(1L);

		ArrayList<Long> channels = new ArrayList<Long>();
		channels.add(1L);
		DropFilter filter = new DropFilter();
		filter.setChannelIds(channels);
		List<Drop> drops = riverDao.getDrops(1L, filter, 1, 10, account);

		assertEquals(2, drops.size());

		Drop drop = drops.get(0);
		assertEquals(3, drop.getId());
	}

	@Test
	public void getDropsSince() {
		Account account = accountDao.findById(1L);
		DropFilter filter = new DropFilter();
		filter.setSinceId(3L);
		List<Drop> drops = riverDao.getDrops(1L, filter, 1, 1, account);

		assertEquals(1, drops.size());

		Drop drop = drops.get(0);
		assertTrue(drop.getId() > 3);
		assertEquals(false, drop.getRead());
		assertEquals("twitter", drop.getChannel());
		assertEquals("droplet_4_title", drop.getTitle());
		assertEquals("droplet_4_content", drop.getContent());
		assertEquals("4", drop.getOriginalId());
		assertEquals(25, drop.getCommentCount());
		assertEquals(2, drop.getIdentity().getId());
		assertEquals("identity2_name", drop.getIdentity().getName());
		assertEquals("identity2_avatar", drop.getIdentity().getAvatar());
		assertNotNull(drop.getOriginalUrl());
		assertEquals(3, drop.getOriginalUrl().getId());
		assertEquals("http://www.bbc.co.uk/nature/20273855", drop
				.getOriginalUrl().getUrl());

		assertEquals(1, drop.getTags().size());
		Tag tag = drop.getTags().get(0);
		assertEquals(1, tag.getId());
		assertEquals("Jeremy Hunt", tag.getTag());
		assertEquals("person", tag.getType());

		assertEquals(1, drop.getLinks().size());
		Link link = drop.getLinks().get(0);
		assertEquals(10, link.getId());
		assertEquals("http://www.bbc.co.uk/sport/0/football/20319573",
				link.getUrl());

		assertEquals(1, drop.getMedia().size());
		Media media = drop.getMedia().get(0);
		assertEquals(1, media.getId());
		assertEquals(
				"http://gigaom2.files.wordpress.com/2012/10/datacapspercentage.jpeg",
				media.getUrl());
		assertEquals("image", media.getType());

		assertEquals(2, media.getThumbnails().size());
		MediaThumbnail thumbnail = media.getThumbnails().get(0);
		assertEquals(
				"https://2bcbd22fbb0a02d76141-1680e9dfed1be27cdc47787ec5d4ef89.ssl.cf1.rackcdn.com/625dd7cb656d258b4effb325253e880631699d80345016e9e755b4a04341cda1.peg",
				thumbnail.getUrl());
		assertEquals(80, thumbnail.getSize());

		assertEquals(1, drop.getPlaces().size());
		Place place = drop.getPlaces().get(0);
		assertEquals(1, place.getId());
		assertEquals("Wales", place.getPlaceName());
		assertEquals(new Float(146.11), place.getLongitude());
		assertEquals(new Float(-33), place.getLatitude());

	}

	@Test
	public void getDropsSinceForChannelId() {
		Account account = accountDao.findById(1L);

		ArrayList<Long> channels = new ArrayList<Long>();
		channels.add(2L);
		DropFilter filter = new DropFilter();
		filter.setChannelIds(channels);
		filter.setSinceId(4L);
		List<Drop> drops = riverDao.getDrops(1L, filter, 1, 10, account);

		assertEquals(1, drops.size());

		Drop drop = drops.get(0);
		assertEquals(5, drop.getId());
	}

	@Test
	public void getDropsFromDate() throws Exception {
		Account account = accountDao.findById(1L);

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DropFilter filter = new DropFilter();
		filter.setDateFrom(dateFormat.parse("01/01/2013"));
		List<Drop> drops = riverDao.getDrops(1L, filter, 1, 10, account);

		assertEquals(1, drops.size());

		Drop drop = drops.get(0);
		assertEquals(5, drop.getId());
	}

	@Test
	public void getDropsToDate() throws Exception {
		Account account = accountDao.findById(1L);

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DropFilter filter = new DropFilter();
		filter.setDateTo(dateFormat.parse("01/01/2012"));
		List<Drop> drops = riverDao.getDrops(1L, filter, 1, 10, account);

		assertEquals(1, drops.size());

		Drop drop = drops.get(0);
		assertEquals(1, drop.getId());
	}

	@Test
	public void getReadDrops() {
		Account account = accountDao.findById(3L);

		DropFilter filter = new DropFilter();
		filter.setRead(true);
		List<Drop> drops = riverDao.getDrops(1L, filter, 1, 10, account);

		assertEquals(2, drops.size());

		Drop drop = drops.get(1);
		assertEquals(2, drop.getId());
	}

	@Test
	public void getReadDropsSince() {
		Account account = accountDao.findById(3L);

		DropFilter filter = new DropFilter();
		filter.setRead(true);
		filter.setSinceId(3L);
		List<Drop> drops = riverDao.getDrops(1L, filter, 1, 10, account);

		assertEquals(1, drops.size());

		Drop drop = drops.get(0);
		assertEquals(5, drop.getId());
	}

	@Test
	public void getUnreadDrops() {
		Account account = accountDao.findById(3L);

		DropFilter filter = new DropFilter();
		filter.setRead(false);
		List<Drop> drops = riverDao.getDrops(1L, filter, 1, 10, account);

		assertEquals(3, drops.size());

		Drop drop = drops.get(1);
		assertEquals(3, drop.getId());
	}

	@Test
	public void getDropsForChannelName() {
		Account account = accountDao.findById(3L);

		List<String> channels = new ArrayList<String>();
		channels.add("rss");
		DropFilter filter = new DropFilter();
		filter.setChannels(channels);
		List<Drop> drops = riverDao.getDrops(1L, filter, 1, 10, account);

		assertEquals(3, drops.size());
	}

	@Test
	public void getDropsSinceForChannelName() {
		Account account = accountDao.findById(3L);

		List<String> channels = new ArrayList<String>();
		channels.add("twitter");
		DropFilter filter = new DropFilter();
		filter.setChannels(channels);
		filter.setSinceId(3L);
		List<Drop> drops = riverDao.getDrops(1L, filter, 1, 10, account);

		assertEquals(1, drops.size());

		Drop drop = drops.get(0);
		assertTrue(drop.getId() > 3);
	}

	@Test
	public void testCreateRiver() {
		River river = new River();
		Account account = accountDao.findByUsername("user1");

		river.setRiverName("Test river");
		river.setDescription("test description");
		river.setAccount(account);
		river.setRiverPublic(false);

		riverDao.create(river);

		assertNotNull(river.getId());
		String sql = "SELECT account_id, river_name, description, river_public, river_name_canonical FROM rivers WHERE id = ?";

		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql,
				river.getId());
		assertEquals("Test river", (String) results.get("river_name"));
		assertEquals(3L, ((Number) results.get("account_id")).longValue());
		assertEquals(TextUtil.getURLSlug("Test river"),
				(String) results.get("river_name_canonical"));
		assertEquals("test description", (String) results.get("description"));
		assertEquals(false, results.get("river_public"));
	}

	@Test
	public void updateRiver() {
		River river = riverDao.findById(2L);
		river.setRiverName("updated river name");
		river.setDescription("updated description");
		river.setRiverPublic(true);

		riverDao.update(river);
		em.flush();

		String sql = "SELECT river_name, river_name_canonical, description, river_public FROM rivers WHERE id = ?";
		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql, 2);
		assertEquals("updated river name", (String) results.get("river_name"));
		assertEquals(TextUtil.getURLSlug("updated river name"),
				(String) results.get("river_name_canonical"));
		assertEquals("updated description", (String) results.get("description"));
		assertEquals(true, results.get("river_public"));
	}

	@Test
	public void findCollaboratorByAccount() {
		RiverCollaborator rc = riverDao.findCollaborator(1L, 3L);

		assertEquals(1L, (long) rc.getId());
		assertEquals(3L, rc.getAccount().getId());
	}

	@Test
	public void findNonExistentCollaboratorByAccount() {
		RiverCollaborator rc = riverDao.findCollaborator(1L, 5L);

		assertNull(rc);
	}

	@Test
	public void testAddCollaborator() {
		River river = riverDao.findById(1L);
		Account account = accountDao.findByUsername("user3");

		riverDao.addCollaborator(river, account, true);
		em.flush();

		String sql = "SELECT river_id, account_id, collaborator_active, read_only FROM river_collaborators WHERE river_id = ? AND account_id = ?";
		Map<String, Object> results = this.jdbcTemplate
				.queryForMap(sql, 1L, 5L);

		assertEquals(false, results.get("collaborator_active"));
		assertEquals(true, results.get("read_only"));
	}

	@Test
	public void testModifyCollaborator() {
		RiverCollaborator collaborator = riverCollaboratorDao.findById(1L);
		collaborator.setActive(false);
		collaborator.setReadOnly(true);
		riverDao.updateCollaborator(collaborator);
		em.flush();

		String sql = "SELECT river_id, account_id, collaborator_active, read_only FROM river_collaborators WHERE id = ?";
		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql, 1L);

		assertEquals(false, results.get("collaborator_active"));
		assertEquals(true, results.get("read_only"));
	}
}
