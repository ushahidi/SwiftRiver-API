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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import com.ushahidi.swiftriver.core.api.dao.RiverDao;

/**
 * Tests for the River service
 * @author ekala
 *
 */
public class RiverServiceTest{

	@Test
	public void testGetRiver() {
		RiverDao riverDao = mock(RiverDao.class);
		RiverService service = new RiverService();
		service.setRiverDao(riverDao);
		
		service.getRiver(2L);
		verify(riverDao).findById(2L);
	}

}
