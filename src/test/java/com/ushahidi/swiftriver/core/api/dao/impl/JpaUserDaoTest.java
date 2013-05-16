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
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.UserDao;
import com.ushahidi.swiftriver.core.model.User;

/**
 * Tests for {@link JpaUserDao}
 * @author ekala
 *
 */
public class JpaUserDaoTest extends AbstractJpaDaoTest {
	
	@Autowired
	UserDao userDao;
	
	@Test
	public void findByUsername() {
		User user = userDao.findByUsername("user2");

		assertNotNull(user);
		assertEquals(4, user.getId());
	}

	/**
	 * Tests {@link UserDao#create(User)}
	 */
	@Test
	public void create() {
		User user = new User();
		user.setName("New User");
		user.setUsername("newuser");
		user.setEmail("newuser@myswiftriver.com");
		user.setPassword("new-user-pa55w0rd");

		userDao.create(user);

		assertNotNull(user.getDateCreated());
	}
}
