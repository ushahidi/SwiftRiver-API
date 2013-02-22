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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dto.GetAccountDTO;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.User;

public class AccountServiceTest {
	
	private Account account;
	
	private GetAccountDTO getAccountDTO;

	private AccountDao mockedAccountDao;
	
	private Mapper mockedMapper;
	
	private AccountService accountService;

	@Before
	public void setup() {
		account = new Account();
		account.setId(13);
		account.setOwner(new User());
		account.setFollowers(new ArrayList<Account>());
		account.setFollowing(new ArrayList<Account>());
		
		getAccountDTO = new GetAccountDTO();
		
		mockedAccountDao = mock(AccountDao.class);		
		when(mockedAccountDao.findById(anyLong())).thenReturn(account);
		when(mockedAccountDao.findByUsername(anyString())).thenReturn(account);
		when(mockedAccountDao.findByName(anyString())).thenReturn(account);
		
		mockedMapper = mock(Mapper.class);
		when(mockedMapper.map(account, GetAccountDTO.class)).thenReturn(getAccountDTO);
		
		accountService = new AccountService();
		accountService.setAccountDao(mockedAccountDao);
		accountService.setMapper(mockedMapper);
	}

	@Test
	public void findById() throws NotFoundException {
		GetAccountDTO actualGetAccountDTO = accountService.getAccountById(13L);

		verify(mockedAccountDao).findById(13L);
		assertEquals(getAccountDTO, actualGetAccountDTO);
	}

	@Test
	public void findByUsername() throws NotFoundException {
		GetAccountDTO actualGetAccountDTO = accountService.getAccountByUsername("admin");

		verify(mockedAccountDao).findByUsername("admin");
		assertEquals(getAccountDTO, actualGetAccountDTO);
	}
	
	@Test
	public void findByName() throws NotFoundException {
		GetAccountDTO actualGetAccountDTO = accountService.getAccountByName("default");

		verify(mockedAccountDao).findByName("default");
		assertEquals(getAccountDTO, actualGetAccountDTO);
	}

	@Test
	public void mapGetAccountDTO() {
		GetAccountDTO actualGetAccountDTO = accountService
				.mapGetAccountDTO(account);

		verify(mockedMapper).map(account, GetAccountDTO.class);
		assertEquals(getAccountDTO, actualGetAccountDTO);
	}
	
	@Test
	public void search() {
		AccountDao mockAccountDao = mock(AccountDao.class);
		List<Account> accounts = new ArrayList<Account>();
		accounts.add(account);
		when(mockAccountDao.search(anyString())).thenReturn(accounts);
		
		AccountService accountService = new AccountService();
		accountService.setAccountDao(mockAccountDao);
		accountService.setMapper(mockedMapper);
		List<GetAccountDTO> getAccountDTOs = accountService.searchAccounts("abcd");
		
		verify(mockAccountDao).search("abcd");
		assertEquals(1, getAccountDTOs.size());
	}
}
