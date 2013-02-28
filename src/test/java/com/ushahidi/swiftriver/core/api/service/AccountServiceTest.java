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

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.UserDao;
import com.ushahidi.swiftriver.core.api.dao.UserTokenDao;
import com.ushahidi.swiftriver.core.api.dto.CreateAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.GetAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyAccountDTO;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.User;
import com.ushahidi.swiftriver.core.model.UserToken;

public class AccountServiceTest {
	
	private Account account;
	
	private GetAccountDTO getAccountDTO;

	private AccountDao mockAccountDao;
	
	private UserDao mockUserDao;
	
	private UserTokenDao mockUserTokenDao;
	
	private Mapper mockMapper;
	
	private AccountService accountService;
	
	private PasswordEncoder passwordEncoder;

	@Before
	public void setup() {
		account = new Account();
		account.setId(13);
		account.setOwner(new User());
		account.setFollowers(new ArrayList<Account>());
		account.setFollowing(new ArrayList<Account>());
		
		getAccountDTO = new GetAccountDTO();
		
		mockAccountDao = mock(AccountDao.class);
		mockUserDao = mock(UserDao.class);
		mockUserTokenDao = mock(UserTokenDao.class);
		mockMapper = mock(Mapper.class);
		passwordEncoder = new BCryptPasswordEncoder();
		when(mockMapper.map(account, GetAccountDTO.class)).thenReturn(getAccountDTO);
		
		accountService = new AccountService();
		accountService.setAccountDao(mockAccountDao);
		accountService.setMapper(mockMapper);
		accountService.setUserDao(mockUserDao);
		accountService.setUserTokenDao(mockUserTokenDao);
		accountService.setPasswordEncoder(passwordEncoder);
	}

	@Test
	public void findById() throws NotFoundException {
		when(mockAccountDao.findById(anyLong())).thenReturn(account);
		
		GetAccountDTO actualGetAccountDTO = accountService.getAccountById(13L);

		verify(mockAccountDao).findById(13L);
		assertEquals(getAccountDTO, actualGetAccountDTO);
	}

	@Test
	public void findByUsername() throws NotFoundException {
		when(mockAccountDao.findByUsername(anyString())).thenReturn(account);
		
		GetAccountDTO actualGetAccountDTO = accountService.getAccountByUsername("admin");

		verify(mockAccountDao).findByUsername("admin");
		assertEquals(getAccountDTO, actualGetAccountDTO);
	}
	
	@Test
	public void findByName() throws NotFoundException {
		when(mockAccountDao.findByName(anyString())).thenReturn(account);
		
		GetAccountDTO actualGetAccountDTO = accountService.getAccountByName("default", false);

		verify(mockAccountDao).findByName("default");
		assertEquals(getAccountDTO, actualGetAccountDTO);
	}
	
	@Test
	public void findByEmail() throws NotFoundException {
		when(mockAccountDao.findByEmail(anyString())).thenReturn(account);
		
		GetAccountDTO actualGetAccountDTO = accountService.getAccountByEmail("email", false);

		verify(mockAccountDao).findByEmail("email");
		assertEquals(getAccountDTO, actualGetAccountDTO);
	}

	@Test
	public void mapGetAccountDTO() {
		GetAccountDTO actualGetAccountDTO = accountService
				.mapGetAccountDTO(account);

		verify(mockMapper).map(account, GetAccountDTO.class);
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
		accountService.setMapper(mockMapper);
		List<GetAccountDTO> getAccountDTOs = accountService.searchAccounts("abcd");
		
		verify(mockAccountDao).search("abcd");
		assertEquals(1, getAccountDTOs.size());
	}
	
	@Test
	public void createAccount() {
		when(mockMapper.map(any(Account.class), any(Class.class))).thenReturn(getAccountDTO);
		
		CreateAccountDTO createAccount = new CreateAccountDTO();
		createAccount.setAccountPath("account_path");
		createAccount.setAccountPrivate(true);
		createAccount.setEmail("email@example.com");
		createAccount.setName("account name");
		createAccount.setPassword("totally secret");
		
		GetAccountDTO actual = accountService.createAccount(createAccount);
		
		ArgumentCaptor<User> userArgument = ArgumentCaptor.forClass(User.class);
		verify(mockUserDao).create(userArgument.capture());
		User user = userArgument.getValue();
		assertEquals("email@example.com", user.getEmail());
		assertEquals("account name", user.getName());
		assertEquals("email@example.com", user.getUsername());
		assertTrue(passwordEncoder.matches("totally secret", user.getPassword()));
		
		ArgumentCaptor<UserToken> tokenArgument = ArgumentCaptor.forClass(UserToken.class);
		verify(mockUserTokenDao).create(tokenArgument.capture());
		UserToken token = tokenArgument.getValue();
		assertEquals(user, token.getUser());
		assertNotNull(token.getToken());
		
		ArgumentCaptor<Account> accountArgument = ArgumentCaptor.forClass(Account.class);
		verify(mockAccountDao).create(accountArgument.capture());
		Account account = accountArgument.getValue();
		assertEquals("account_path", account.getAccountPath());
		assertTrue(account.isAccountPrivate());
		assertEquals(user, account.getOwner());
		
		assertEquals(getAccountDTO, actual);
	}
	
	@Test
	public void modifyAccount() {
		DozerBeanMapper mapper = new DozerBeanMapper();
		accountService.setMapper(mapper);
		
		Account account = new Account();
		account.setOwner(new User());
		account.getOwner().setActive(true);
		account.getOwner().setId(1);
		UserToken userToken = new UserToken();
		userToken.setUser(account.getOwner());
		userToken.setExpires(new Date((new Date()).getTime() + 86400000L));
		
		ModifyAccountDTO modifyAccount = new ModifyAccountDTO();		
		modifyAccount.setAccountPath("new account path");
		modifyAccount.setAccountPrivate(true);
		modifyAccount.setRiverQuotaRemaining(999);
		modifyAccount.setToken("this is a token");
		
		ModifyAccountDTO.User owner = new ModifyAccountDTO.User();
		modifyAccount.setOwner(owner);
		owner.setEmail("email@example.com");
		owner.setName("owner's new name");
		owner.setPassword("new password");
		modifyAccount.setOwner(owner);
		
		when(mockAccountDao.findById(anyLong())).thenReturn(account);
		when(mockUserTokenDao.findByToken(anyString())).thenReturn(userToken);
		
		accountService.modifyAccount(1L, modifyAccount);
		
		ArgumentCaptor<Account> accountArgument = ArgumentCaptor.forClass(Account.class);
		verify(mockAccountDao).update(accountArgument.capture());
		Account modifiedAccount = accountArgument.getValue();
		
		assertEquals("new account path", modifiedAccount.getAccountPath());
		assertTrue(modifiedAccount.isAccountPrivate());
		assertEquals(999, modifiedAccount.getRiverQuotaRemaining());
		assertTrue(modifiedAccount.getOwner().getActive());
		assertEquals("email@example.com", modifiedAccount.getOwner().getEmail());
		assertEquals("email@example.com", modifiedAccount.getOwner().getUsername());
		assertEquals("owner's new name", modifiedAccount.getOwner().getName());
		assertTrue(passwordEncoder.matches("new password", modifiedAccount.getOwner().getPassword()));
	}
	
	@Test
	public void createUserToken() {
		User user = new User();
		
		accountService.createUserToken(user);
		
		ArgumentCaptor<UserToken> userTokenArgument = ArgumentCaptor.forClass(UserToken.class);
		verify(mockUserTokenDao).create(userTokenArgument.capture());
		User tokenUser = userTokenArgument.getValue().getUser();
		
		assertEquals(user, tokenUser);
	}
}
