package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.api.dao.AccountDao;
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
	
	/**
	 * Test for {@link AccountDao#populateAssets(Account, Account)} where
	 * the querying account does not have access to the private assets
	 * of the target account
	 */
	@Test	
	public void populateAssets() {
		Account targetAccount = accountDao.findByUsername("admin");
		Account queryingAccount = accountDao.findByUsername("user1");
		
		accountDao.populateAssets(targetAccount, queryingAccount);
		assertEquals(0, targetAccount.getBuckets().size());
	}
	
	/**
	 * Test for {@link AccountDao#populateAssets(Account, Account)} where
	 * the target account has some public assets or the querying user is
	 * collaborating on the private assets
	 */
	@Test
	public void populateVisibleAssets() {
		// User 1 querying user 2
		Account targetAccount = accountDao.findByUsername("user2");		
		Account queryingAccount = accountDao.findByUsername("user1");
		
		accountDao.populateAssets(targetAccount, queryingAccount);
		assertTrue(targetAccount.getBuckets().size() > 0);
	}
}
