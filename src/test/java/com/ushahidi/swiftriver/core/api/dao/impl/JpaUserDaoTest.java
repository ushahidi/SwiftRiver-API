package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.model.User;
import com.ushahidi.swiftriver.core.support.AbstractIntegrationTest;

public class JpaUserDaoTest extends AbstractIntegrationTest {
	
	@Autowired
	JpaUserDao userDao;
	
	@Test
	public void findByUsername() {
		User user = userDao.findByUsername("user2");

		assertNotNull(user);
		assertEquals(4, user.getId());
	}
}
