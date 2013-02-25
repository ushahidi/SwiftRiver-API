package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.api.dao.impl.JpaAccountDao;
import com.ushahidi.swiftriver.core.model.Account;

public class JpaAccountDaoTest extends AbstractDaoTest {

	@Autowired
	JpaAccountDao accountDao;
	
	@PersistenceContext
	protected EntityManager em;

	@Test
	public void findById() {
		Account account = accountDao.findById(5L);

		assertNotNull(account);
		assertEquals("user3", account.getAccountPath());
	}

	@Test
	public void findByUsername() {
		Account account = accountDao.findByUsername("user2");

		assertNotNull(account);
		assertEquals(4, account.getId());
	}

	@Test
	public void findByNonExistentUsername() {
		Account account = accountDao.findByUsername("chris");

		assertNull(account);
	}

	@Test
	public void findByName() {
		Account account = accountDao.findByAccountPath("default");

		assertNotNull(account);
		assertEquals(1, account.getId());
	}

	@Test
	public void findByNonExistentName() {
		Account account = accountDao.findByAccountPath("chris");

		assertNull(account);
	}
	
	@Test
	public void decreaseRiverQuota() {
		Account account = accountDao.findById(1L);
		accountDao.decreaseRiverQuota(account, 3);
		em.flush();
		String sql = "SELECT river_quota_remaining FROM `accounts` WHERE `id` = 1";
		int quotaAfter = this.jdbcTemplate.queryForInt(sql);
		
		assertEquals(7, quotaAfter);
	}
}
