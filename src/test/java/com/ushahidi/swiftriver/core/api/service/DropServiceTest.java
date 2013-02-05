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

import org.junit.Before;
import org.junit.Test;

import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.api.service.DropService;
import com.ushahidi.swiftriver.test.AbstractTransactionalTest;

/**
 * Integration tests for the Drop service
 * @author ekala
 *
 */
public class DropServiceTest extends AbstractTransactionalTest {

	/** Service interfaces under test */	
	private DropService dropService = new DropService();

	/** DAO interfaces under test */
	private DropDao dropDao;

	/* Drop id to be used for the test */
	private Long dropId = new Long(18);
	
	@Before
	public void beforeTest() {
		dropDao = mock(DropDao.class);
		dropService.setDropDao(dropDao);
	}

	@Test
	public void testGetDrop() {
		dropService.getDrop(dropId);
		verify(dropDao).findById(dropId);
	}
	
}
