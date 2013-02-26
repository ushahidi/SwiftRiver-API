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

import java.math.BigInteger;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;

public class JpaBucketDaoTest extends AbstractDaoTest {

	@Autowired
	private BucketDao bucketDao;
	
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
	
	/**
	 * Tests that a collaborator is successfully created and inserted
	 * into the database
	 */
	@Test
	@Transactional
	public void testAddBucketCollaborator() {
		long bucketId = 1;
		Bucket bucket = bucketDao.findById(bucketId);
		int collaboratorCount = bucket.getCollaborators().size();
		
		Account account = accountDao.findByUsername("user3");
		bucketDao.addCollaborator(bucket, account, true);
		assertEquals(collaboratorCount+1, bucket.getCollaborators().size());
	}

}
