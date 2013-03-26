package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.RoleDao;
import com.ushahidi.swiftriver.core.model.Role;
import com.ushahidi.swiftriver.core.support.AbstractIntegrationTest;

public class JpaRoleDaoTest extends AbstractIntegrationTest {

	@Autowired
	RoleDao roleDao;
	
	@Test
	public void findByClientId() {
		Role role = roleDao.findByName("client");

		assertNotNull(role);
		assertEquals(3, role.getId());
	}
}
