package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.model.Client;

public class JpaClientDaoTest extends AbstractDaoTest {
	
	@Autowired
	JpaClientDao clientDao;
	
	@Test
	public void findByClientId() {
		Client client = clientDao.findByClientId("trusted-client");

		assertNotNull(client);
		assertEquals(1, client.getId());
	}
}
