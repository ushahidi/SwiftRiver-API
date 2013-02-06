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

import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.api.dao.impl.JpaRiverDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.River;

/**
 * Tests for the River service
 * 
 * @author ekala
 * 
 */
public class RiverServiceTest {

	private RiverService riverService;

	private RiverDao riverDao;

	@Before
	public void setup() {
		riverDao = mock(RiverDao.class);
		riverService = new RiverService();
		riverService.setRiverDAO(riverDao);
	}

	/**
	 * @see JpaRiverDao#getRiver(long)
	 * @verifies The river exists
	 */
	@Test
	public void testGetRiver() {
		riverService.getRiver(new Long(1));
		verify(riverDao).findById(new Long(1));
	}

	@Test
	public void testGetDrops() {
		riverService.getDropsSinceId(new Long(2), 1000L, 50);
		verify(riverDao).getDrops(new Long(2), 1000L, 50);
	}

	@Test
	public void testGetChannels() {
		riverService.getChannels(1);
		verify(riverDao).getChannels(1);
	}

	/**
	 * @verfies that {@link RiverService#getDropsSinceId(Long, Long, int)}
	 *          exhibits the desired behaviour
	 */
	@Test
	public void testGetDropsSinceId() {
		riverService.getDropsSinceId(1L, 2000L, 50);
		verify(riverDao).getDrops(1L, 2000L, 50);
	}

	@Test
	public void getRiverMap() {

		River river = new River();
		river.setFollowers(new ArrayList());
		river.setCollaborators(new ArrayList());

		Map<String, Object> riverMap = RiverService.getRiverMap(river,
				new Account());

		assertThat(
				riverMap.keySet(),
				hasItems("extension_count", "is_following", "drop_quota",
						"is_collaborating", "id", "category", "date_added",
						"full", "description", "name", "follower_count",
						"active", "expiry_date", "public", "drop_count"));
	}
}