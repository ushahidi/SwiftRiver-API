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
 */package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import java.util.Map;

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

	/**
	 * Tests that a bucket is successfully created and inserted
	 * in the database
	 */
	@Test
	@Transactional
	public void testCreateBucket() {
		Account account = accountDao.findByUsername("user1");
		Bucket bucket = new Bucket();

		bucket.setName("Test Bucket Number 2");
		bucket.setPublished(true);
		bucket.setAccount(account);
		
		bucketDao.create(bucket);
		
		assertTrue(bucket.getId() > 0);
		assertEquals("Test Bucket Number 2", bucket.getName());
		
		assertNotNull(bucket.getId());
		String sql = "SELECT bucket_name, account_id FROM `buckets` WHERE `id` = ?";
		
		Map<String, Object> r = this.jdbcTemplate.queryForMap(sql, bucket.getId());
		assertEquals("Test Bucket Number 2", (String)r.get("bucket_name"));
		assertEquals(BigInteger.valueOf(3L), (BigInteger)r.get("account_id"));
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
