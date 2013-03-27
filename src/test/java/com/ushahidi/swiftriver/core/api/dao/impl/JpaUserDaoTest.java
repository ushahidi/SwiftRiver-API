package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.UserDao;
import com.ushahidi.swiftriver.core.model.User;

public class JpaUserDaoTest extends AbstractJpaDaoTest {
	
	@Autowired
	UserDao userDao;
	
	@Test
	public void findByUsername() {
		User user = userDao.findByUsername("user2");

		assertNotNull(user);
		assertEquals(4, user.getId());
	}
}
