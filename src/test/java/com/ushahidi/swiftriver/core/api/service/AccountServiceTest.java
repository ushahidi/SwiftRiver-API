/**
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ushahidi.swiftriver.core.api.service;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.service.AccountService;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.User;

import static org.junit.Assert.*;
import static org.junit.matchers.JUnitMatchers.*;
import static org.mockito.Mockito.*;

public class AccountServiceTest {
	
	private Account account;

	private AccountDao mockedAccountDao;
	
	private AccountService accountService;

	@Before
	public void setup() {
		account = new Account();
		account.setId(13);
		account.setOwner(new User());
		account.setFollowers(new ArrayList<Account>());
		account.setFollowing(new ArrayList<Account>());
		
		mockedAccountDao = mock(AccountDao.class);		
		when(mockedAccountDao.findById(anyInt())).thenReturn(account);
		when(mockedAccountDao.findByUsername(anyString())).thenReturn(account);
		
		accountService = new AccountService();
		accountService.setAccountDao(mockedAccountDao);
	}

	@Test
	public void findById() {
		Map<String, Object> accountMap = accountService.getAccount(13);

		verify(mockedAccountDao).findById(13);
		assertEquals(13L, accountMap.get("id"));
	}

	@Test
	public void findByUsername() {
		Map<String, Object> accountMap = accountService.getAccount("admin");

		verify(mockedAccountDao).findByUsername("admin");
		assertEquals(13L, accountMap.get("id"));
	}

	@Test
	public void getAccountMap() {
		Map<String, Object> accountMap = accountService
				.getAccountMap(account);

		assertThat(
				accountMap.keySet(),
				hasItems("is_following", "buckets", "is_collaborator",
						"rivers", "following_count", "id", "is_owner",
						"date_added", "owner", "follower_count",
						"river_quota_remaining", "account_path", "active",
						"public"));
	}
}
