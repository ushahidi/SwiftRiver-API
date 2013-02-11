package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.api.dao.impl.JpaAccountDao;
import com.ushahidi.swiftriver.core.model.Account;

public class JpaAccountDaoTest extends AbstractDaoTest {
	
	@Autowired
	JpaAccountDao accountDao;
	
	@Test
	public void findById()
	{
		Account account = accountDao.findById(5);
		
		assertNotNull(account);
		assertEquals("user3", account.getAccountPath());
	}
	
	@Test
	public void findByUsername()
	{
		Account account = accountDao.findByUsername("user2");
		
		assertNotNull(account);
		assertEquals(4, account.getId());
	}
	
	@Test
	public void findByName()
	{
		Account account = accountDao.findByName("default");
		
		assertNotNull(account);
		assertEquals(1, account.getId());
	}
}
