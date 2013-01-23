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
package com.ushahidi.swiftriver.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.api.dao.impl.JpaRiverDao;
import com.ushahidi.swiftriver.core.api.service.RiverService;
import com.ushahidi.swiftriver.test.AbstractSwiftRiverTest;

/**
 * Tests for the River service
 * @author ekala
 *
 */
public class RiverServiceTest extends AbstractSwiftRiverTest{
	
	private RiverService riverService;
	
	private RiverDao riverDAO;
	
	@Override
	@Before
	public void beforeTest() {
		riverDAO = mock(RiverDao.class);
		riverService = new RiverService();
		riverService.setRiverDAO(riverDAO);
	}


	/**
	 * @see JpaRiverDao#getRiver(long)
	 * @verifies The river exists
	 */
	@Test
	public void testGetRiver() {
		riverService.getRiver(new Long(1));
		verify(riverDAO).findById(new Long(1));
	}
	
	
	@Test
	public void testGetDrops() {
		riverService.getDropsSinceId(new Long(2), 1000L, 50);
		verify(riverDAO).getDrops(new Long(2), 1000L, 50);
	}
	
	@Test
	public void testGetChannels() {
		riverService.getChannels(1);
		verify(riverDAO).getChannels(1);
	}
}
