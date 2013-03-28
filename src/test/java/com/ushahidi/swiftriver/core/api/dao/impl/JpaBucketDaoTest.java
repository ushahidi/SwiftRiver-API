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

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.BucketCollaboratorDao;
import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketCollaborator;
import com.ushahidi.swiftriver.core.model.Drop;

public class JpaBucketDaoTest extends AbstractDaoTest {

	@Autowired
	private BucketDao bucketDao;
	
	@Autowired
	private BucketCollaboratorDao bucketCollaboratorDao;
	
	@Autowired
	private AccountDao accountDao;
	
	@PersistenceContext
	protected EntityManager em;

	/**
	 * Tests that a bucket is successfully created and inserted
	 * in the database
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
		
		String sql = "SELECT account_id, bucket_name, bucket_name_canonical, bucket_description, bucket_publish  FROM `buckets` WHERE `id` = ?";
		Map<String, Object> r = this.jdbcTemplate.queryForMap(sql, bucket.getId());
		
		assertEquals(BigInteger.valueOf(3L), (BigInteger)r.get("account_id"));
		assertEquals("Test Bucket Number 2", (String)r.get("bucket_name"));
		assertEquals("test-bucket-number-2", (String)r.get("bucket_name_canonical"));
		assertEquals("The Bucket's Description", (String)r.get("bucket_description"));
		assertTrue((Boolean)r.get("bucket_publish"));
	}
	
	@Test
	public void updateBucket() {
		Bucket bucket = bucketDao.findById(1L);

		bucket.setName("Renamed Bucket");
		bucket.setDescription("Renamed Bucket's Description");
		bucket.setPublished(false);
		
		bucketDao.update(bucket);
		em.flush();
		
		String sql = "SELECT account_id, bucket_name, bucket_name_canonical, bucket_description, bucket_publish  FROM `buckets` WHERE `id` = ?";
		
		Map<String, Object> r = this.jdbcTemplate.queryForMap(sql, bucket.getId());
		assertEquals(BigInteger.valueOf(3L), (BigInteger)r.get("account_id"));
		assertEquals("Renamed Bucket", (String)r.get("bucket_name"));
		assertEquals("renamed-bucket", (String)r.get("bucket_name_canonical"));
		assertEquals("Renamed Bucket's Description", (String)r.get("bucket_description"));
		assertFalse((Boolean)r.get("bucket_publish"));
	}
	
	@Test
	public void findCollaboratorByAccount() {
		BucketCollaborator rc = bucketDao.findCollaborator(1L, 3L);
		
		assertEquals(1L, (long)rc.getId());
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
		
		String sql = "SELECT `bucket_id`, `account_id`, `collaborator_active`, `read_only` FROM `bucket_collaborators` WHERE `bucket_id` = ? AND `account_id` = ?";
		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql, 1L, 5L);
		
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
		
		String sql = "SELECT `bucket_id`, `account_id`, `collaborator_active`, `read_only` FROM `bucket_collaborators` WHERE `id` = ?";
		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql, 1L);
		
		assertEquals(false, results.get("collaborator_active"));
		assertEquals(true, results.get("read_only"));
	}
	
	@Test
	public void getDropsSinceId() {
		Account account = accountDao.findByUsername("user1");
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("page", 1);
		requestParams.put("count", 10);
		requestParams.put("sinceId", 4L);
		
		List<Drop> drops = bucketDao.getDrops(1L, account, requestParams);
		assertEquals(1, drops.size());
		assertEquals(5L, drops.get(0).getId());
	}
	
	@Test
	public void getDropsByChannel() {
		Account account = accountDao.findByUsername("user1");

		List<String> channels = new ArrayList<String>();
		channels.add("rss");

		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("page", 1);
		requestParams.put("count", 10);
		requestParams.put("channels", channels);
		
		List<Drop> drops = bucketDao.getDrops(1L, account, requestParams);
		assertEquals(3, drops.size());
	}
	
	@Test
	public void getDropsWithMaxId() {
		Account account = accountDao.findByUsername("user1");
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("page", 1);
		requestParams.put("count", 10);
		requestParams.put("maxId", 3L);
		
		List<Drop> drops = bucketDao.getDrops(1L, account, requestParams);
		assertEquals(3, drops.size());
	}
	
	@Test
	public void getDropsFromDate() throws Exception {
		Account account = accountDao.findByUsername("user1");
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("page", 1);
		requestParams.put("count", 10);
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		requestParams.put("dateFrom", dateFormat.parse("15-09-2012"));		
		
		List<Drop> drops = bucketDao.getDrops(1L, account, requestParams);
		assertEquals(3, drops.size());		
	}
	
	@Test
	public void getDropsToDate() throws Exception {
		Account account = accountDao.findByUsername("user1");
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("page", 1);
		requestParams.put("count", 10);
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
		requestParams.put("dateTo", dateFormat.parse("12-09-2012"));
		
		List<Drop> drops = bucketDao.getDrops(1L, account, requestParams);
		assertEquals(2, drops.size());		
		
	}

}
