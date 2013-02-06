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
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.utils.SwiftRiverUtils;
import com.ushahidi.swiftriver.test.AbstractTransactionalTest;

public class JpaBucketDaoTest extends AbstractTransactionalTest {

	@Autowired
	private BucketDao bucketDao;
	
	@Autowired
	private AccountDao accountDao;

	@Override
	@Before
	public void beforeTest() {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Tests that a bucket is successfully created and inserted
	 * in the database
	 */
	@Test
	@Rollback(false)
	public void testCreateBucket() {
		Account account = accountDao.findByUsername("admin5");
		Bucket bucket = new Bucket();

		bucket.setName("Test Bucket Number 2");
		bucket.setUrl(SwiftRiverUtils.getURLSlug(bucket.getBucketName()));
		bucket.setPublished(true);
		bucket.setAccount(account);
		
		bucketDao.save(bucket);
		
		assertTrue(bucket.getId() > 0);
		assertEquals("test-bucket-number-2", bucket.getUrl());
	}
	
	/**
	 * Tests that a collaborator is successfully created and inserted
	 * into the database
	 */
	@Test
	@Transactional
	@Rollback(false)
	public void testAddBucketCollaborator() {
		long bucketId = 1;
		Bucket bucket = bucketDao.findById(bucketId);
		int collaboratorCount = bucket.getCollaborators().size();
		
		Account account = accountDao.findByUsername("admin2");
		bucketDao.addCollaborator(bucketId, account, true);
		assertEquals(collaboratorCount+1, bucket.getCollaborators().size());
	}

}
