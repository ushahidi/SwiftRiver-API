package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDropDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.RiverDrop;

public class JpaAccountDaoTest extends AbstractJpaDaoTest {

	@Autowired
	AccountDao accountDao;
	
	@Autowired
	private RiverDropDao riverDropDao;

	@Test
	public void findById() {
		Account account = accountDao.findById(5L);

		assertNotNull(account);
		assertEquals("user3", account.getAccountPath());
	}

	@Test
	public void findByUsername() {
		Account account = accountDao.findByUsernameOrEmail("user2");

		assertNotNull(account);
		assertEquals(4, account.getId());
	}

	@Test
	public void findByNonExistentUsername() {
		Account account = accountDao.findByUsernameOrEmail("chris");

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
	public void findByEmail() {
		Account account = accountDao.findByEmail("user2@myswiftriver.com");
		
		assertNotNull(account);
		assertEquals(4, account.getId());
	}
	
	@Test
	public void decreaseRiverQuota() {
		Account account = accountDao.findById(1L);
		accountDao.decreaseRiverQuota(account, 3);
		em.flush();
		String sql = "SELECT river_quota_remaining FROM accounts WHERE id = 1";
		int quotaAfter = this.jdbcTemplate.queryForInt(sql);
		
		assertEquals(7, quotaAfter);
	}
	
	@Test
	public void searchAccountPath() {
		List<Account> accounts = accountDao.search("def");
		
		assertNotNull(accounts);
		assertEquals(1, accounts.size());
		assertEquals(1, accounts.get(0).getId());
	}
	
	@Test
	public void searchName() {
		List<Account> accounts = accountDao.search("Admi");
		
		assertNotNull(accounts);
		assertEquals(1, accounts.size());
		assertEquals(1, accounts.get(0).getId());
	}
	
	@Test
	public void searchEmail() {
		List<Account> accounts = accountDao.search("my");
		
		assertNotNull(accounts);
		assertEquals(1, accounts.size());
		assertEquals(1, accounts.get(0).getId());
	}
	
	@Test
	public void addReadRiverDrop() {
		Account account = accountDao.findByUsernameOrEmail("user1");		
		RiverDrop riverDrop = riverDropDao.findById(4L);
		
		account.getReadRiverDrops().add(riverDrop);
		accountDao.update(account);
		
		assertEquals(3, accountDao.findByUsernameOrEmail("user1").getReadRiverDrops().size());
	}
}
