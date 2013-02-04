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
package com.ushahidi.swiftriver.core.api.service;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.River;

public class BucketServiceTest {

	private BucketService bucketService;

	private BucketDao bucketDAO;

	private Long bucketId = new Long(1);

	@Before
	public void beforeTest() {
		bucketDAO = mock(BucketDao.class);
		bucketService = new BucketService();
		bucketService.setBucketDAO(bucketDAO);
	}

	@Test
	public void testGetBucket() {
		bucketService.getBucket(bucketId);
		verify(bucketDAO).findById(bucketId);
	}

	@Test
	public void getBucketMap() {

		Bucket bucket = new Bucket();
		bucket.setFollowers(new ArrayList());
		bucket.setCollaborators(new ArrayList());

		Map<String, Object> riverMap = BucketService.getBucketMap(bucket,
				new Account());

		assertThat(
				riverMap.keySet(),
				hasItems("is_following", "is_collaborating", "id", "category",
						"date_added", "description", "name", "follower_count",
						"public", "drop_count"));
	}

}
