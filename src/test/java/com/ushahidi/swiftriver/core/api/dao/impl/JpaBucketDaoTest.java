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
import static org.junit.Assert.assertFalse;
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
import com.ushahidi.swiftriver.core.api.dao.BucketCollaboratorDao;
import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.api.filter.DropFilter;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketCollaborator;
import com.ushahidi.swiftriver.core.model.BucketDropForm;
import com.ushahidi.swiftriver.core.model.BucketDropFormField;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Media;
import com.ushahidi.swiftriver.core.model.MediaThumbnail;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.Tag;

public class JpaBucketDaoTest extends AbstractJpaDaoTest {

	@Autowired
	private BucketDao bucketDao;

	@Autowired
	private BucketCollaboratorDao bucketCollaboratorDao;

	@Autowired
	private AccountDao accountDao;

	/**
	 * Tests that a bucket is successfully created and inserted in the database
	 */
	@Test
	public void createBucket() {
		Account account = accountDao.findByUsername("user1");
		Bucket bucket = new Bucket();

		bucket.setName("Test Bucket Number 2");
		bucket.setDescription("The Bucket's Description");
		bucket.setPublished(true);
		bucket.setAccount(account);

		bucketDao.create(bucket);

		assertNotNull(bucket.getId());

		String sql = "SELECT account_id, bucket_name, bucket_name_canonical, bucket_description, bucket_publish  FROM buckets WHERE id = ?";
		Map<String, Object> r = this.jdbcTemplate.queryForMap(sql,
				bucket.getId());

		assertEquals(3L, ((Number) r.get("account_id")).longValue());
		assertEquals("Test Bucket Number 2", (String) r.get("bucket_name"));
		assertEquals("test-bucket-number-2",
				(String) r.get("bucket_name_canonical"));
		assertEquals("The Bucket's Description",
				(String) r.get("bucket_description"));
		assertTrue((Boolean) r.get("bucket_publish"));
	}

	@Test
	public void updateBucket() {
		Bucket bucket = bucketDao.findById(1L);

		bucket.setName("Renamed Bucket");
		bucket.setDescription("Renamed Bucket's Description");
		bucket.setPublished(false);

		bucketDao.update(bucket);
		em.flush();

		String sql = "SELECT account_id, bucket_name, bucket_name_canonical, bucket_description, bucket_publish  FROM buckets WHERE id = ?";

		Map<String, Object> r = this.jdbcTemplate.queryForMap(sql,
				bucket.getId());
		assertEquals(3L, ((Number) r.get("account_id")).longValue());
		assertEquals("Renamed Bucket", (String) r.get("bucket_name"));
		assertEquals("renamed-bucket", (String) r.get("bucket_name_canonical"));
		assertEquals("Renamed Bucket's Description",
				(String) r.get("bucket_description"));
		assertFalse((Boolean) r.get("bucket_publish"));
	}

	@Test
	public void findCollaboratorByAccount() {
		BucketCollaborator rc = bucketDao.findCollaborator(1L, 3L);

		assertEquals(1L, (long) rc.getId());
		assertEquals(3L, rc.getAccount().getId());
	}

	@Test
	public void findNonExistentCollaboratorByAccount() {
		BucketCollaborator rc = bucketDao.findCollaborator(1L, 5L);

		assertNull(rc);
	}

	@Test
	public void testAddCollaborator() {
		Bucket bucket = bucketDao.findById(1L);
		Account account = accountDao.findByUsername("user3");

		bucketDao.addCollaborator(bucket, account, true);
		em.flush();

		String sql = "SELECT bucket_id, account_id, collaborator_active, read_only FROM bucket_collaborators WHERE bucket_id = ? AND account_id = ?";
		Map<String, Object> results = this.jdbcTemplate
				.queryForMap(sql, 1L, 5L);

		assertEquals(false, results.get("collaborator_active"));
		assertEquals(true, results.get("read_only"));
	}

	@Test
	public void testModifyCollaborator() {
		BucketCollaborator collaborator = bucketCollaboratorDao.findById(1L);
		collaborator.setActive(false);
		collaborator.setReadOnly(true);
		bucketDao.updateCollaborator(collaborator);
		em.flush();

		String sql = "SELECT bucket_id, account_id, collaborator_active, read_only FROM bucket_collaborators WHERE id = ?";
		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql, 1L);

		assertEquals(false, results.get("collaborator_active"));
		assertEquals(true, results.get("read_only"));
	}

	@Test
	public void getDrops() {
		Account account = accountDao.findById(1L);
		DropFilter filter = new DropFilter();
		List<Drop> drops = bucketDao.getDrops(1L, filter, 1, 10,
				account);

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
		
		BucketDropForm form = (BucketDropForm) drop.getForms().get(0);
		assertEquals(1, (long)form.getId());
		List<BucketDropFormField> values = form.getValues();
		assertEquals(3, values.size());
		assertEquals(1, (long)values.get(0).getField().getId());
		assertEquals("[\"English\"]", values.get(0).getValue());
		assertEquals(2, (long)values.get(1).getField().getId());
		assertEquals("\"Journalist\"", values.get(1).getValue());
		assertEquals(3, (long)values.get(2).getField().getId());
		assertEquals("\"Kenyans\"", values.get(2).getValue());
	}
	
	@Test
	public void getDropsSince() {
		Account account = accountDao.findById(1L);
		DropFilter filter = new DropFilter();
		filter.setSinceId(3L);
		List<Drop> drops = bucketDao.getDrops(1L, filter, 1, 1, account);

		assertEquals(1, drops.size());

		Drop drop = drops.get(0);
		assertEquals(4, drop.getId());
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
	public void getDropsFromDate() throws Exception {
		Account account = accountDao.findById(1L);

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DropFilter filter = new DropFilter();
		filter.setDateFrom(dateFormat.parse("01/01/2013"));
		List<Drop> drops = bucketDao.getDrops(1L, filter, 1, 10, account);

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
		List<Drop> drops = bucketDao.getDrops(1L, filter, 1, 10, account);

		assertEquals(1, drops.size());

		Drop drop = drops.get(0);
		assertEquals(1, drop.getId());
	}

	@Test
	public void getReadDrops() {
		Account account = accountDao.findById(3L);

		DropFilter filter = new DropFilter();
		filter.setRead(true);
		List<Drop> drops = bucketDao.getDrops(1L, filter, 1, 10, account);

		assertEquals(2, drops.size());

		Drop drop = drops.get(1);
		assertEquals(2, drop.getId());
	}

	@Test
	public void getReadDropsSince() {
		Account account = accountDao.findById(3L);

		DropFilter filter = new DropFilter();
		filter.setSinceId(3L);
		filter.setRead(true);
		List<Drop> drops = bucketDao.getDrops(1L, filter, 1, 10, account);

		assertEquals(1, drops.size());

		Drop drop = drops.get(0);
		assertEquals(4, drop.getId());
	}

	@Test
	public void getUnreadDrops() {
		Account account = accountDao.findById(3L);

		DropFilter filter = new DropFilter();
		filter.setRead(false);
		List<Drop> drops = bucketDao.getDrops(1L, filter, 1, 10, account);

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
		List<Drop> drops = bucketDao.getDrops(1L, filter, 1, 10, account);

		assertEquals(3, drops.size());

		Drop drop = drops.get(1);
		assertEquals(3, drop.getId());
	}

	@Test
	public void getDropsSinceForChannelName() {
		Account account = accountDao.findById(3L);

		List<String> channels = new ArrayList<String>();
		channels.add("twitter");
		DropFilter filter = new DropFilter();
		filter.setChannels(channels);
		filter.setSinceId(3L);
		List<Drop> drops = bucketDao.getDrops(1L, filter, 1, 10, account);

		assertEquals(1, drops.size());

		Drop drop = drops.get(0);
		assertEquals(4, drop.getId());
	}

}
