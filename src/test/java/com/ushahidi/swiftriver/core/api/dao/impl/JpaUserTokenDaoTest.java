package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.model.UserToken;

public class JpaUserTokenDaoTest extends AbstractDaoTest {

	@Autowired
	JpaUserTokenDao userTokenDao;

	@Test
	public void findByToken() {
		UserToken userToken = userTokenDao
				.findByToken("18012e9d-0e26-47f5-848f-ad81c96fc3f4");

		assertNotNull(userToken);
		assertEquals(1, userToken.getId());
	}
}
