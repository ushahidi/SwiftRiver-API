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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.ClientDao;
import com.ushahidi.swiftriver.core.api.dao.RoleDao;
import com.ushahidi.swiftriver.core.api.dao.UserDao;
import com.ushahidi.swiftriver.core.api.dao.UserTokenDao;
import com.ushahidi.swiftriver.core.api.dto.CreateAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateClientDTO;
import com.ushahidi.swiftriver.core.api.dto.GetAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.GetClientDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyAccountDTO;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Client;
import com.ushahidi.swiftriver.core.model.Role;
import com.ushahidi.swiftriver.core.model.User;
import com.ushahidi.swiftriver.core.model.UserToken;
import com.ushahidi.swiftriver.core.util.TextUtil;

public class AccountServiceTest {

	private Account account;

	private GetAccountDTO getAccountDTO;

	private AccountDao mockAccountDao;

	private UserDao mockUserDao;

	private UserTokenDao mockUserTokenDao;
	
	private ClientDao mockClientDao;
	
	private RoleDao mockRoleDao;

	private Mapper mockMapper;

	private Mapper mapper;

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
		mockClientDao = mock(ClientDao.class);
		mockRoleDao = mock(RoleDao.class);
		mockMapper = mock(Mapper.class);
		mapper = new DozerBeanMapper();
		passwordEncoder = new BCryptPasswordEncoder();
		when(mockMapper.map(account, GetAccountDTO.class)).thenReturn(
				getAccountDTO);

		accountService = new AccountService();
		accountService.setAccountDao(mockAccountDao);
		accountService.setMapper(mockMapper);
		accountService.setUserDao(mockUserDao);
		accountService.setUserTokenDao(mockUserTokenDao);
		accountService.setClientDao(mockClientDao);
		accountService.setRoleDao(mockRoleDao);
		accountService.setPasswordEncoder(passwordEncoder);
		accountService.setKey("2344228477#97{7&6>82");
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

		GetAccountDTO actualGetAccountDTO = accountService
				.getAccountByUsername("admin");

		verify(mockAccountDao).findByUsername("admin");
		assertEquals(getAccountDTO, actualGetAccountDTO);
	}

	@Test
	public void findByName() throws NotFoundException {
		when(mockAccountDao.findByName(anyString())).thenReturn(account);

		GetAccountDTO actualGetAccountDTO = accountService.getAccountByName(
				"default", false);

		verify(mockAccountDao).findByName("default");
		assertEquals(getAccountDTO, actualGetAccountDTO);
	}

	@Test
	public void findByEmail() throws NotFoundException {
		when(mockAccountDao.findByEmail(anyString())).thenReturn(account);

		GetAccountDTO actualGetAccountDTO = accountService.getAccountByEmail(
				"email", false);

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
		List<GetAccountDTO> getAccountDTOs = accountService
				.searchAccounts("abcd");

		verify(mockAccountDao).search("abcd");
		assertEquals(1, getAccountDTOs.size());
	}

	@Test
	public void createAccount() {
		when(mockMapper.map(any(Account.class), any(Class.class))).thenReturn(
				getAccountDTO);

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
		assertTrue(passwordEncoder
				.matches("totally secret", user.getPassword()));

		ArgumentCaptor<UserToken> tokenArgument = ArgumentCaptor
				.forClass(UserToken.class);
		verify(mockUserTokenDao).create(tokenArgument.capture());
		UserToken token = tokenArgument.getValue();
		assertEquals(user, token.getUser());
		assertNotNull(token.getToken());

		ArgumentCaptor<Account> accountArgument = ArgumentCaptor
				.forClass(Account.class);
		verify(mockAccountDao).create(accountArgument.capture());
		Account account = accountArgument.getValue();
		assertEquals("account_path", account.getAccountPath());
		assertTrue(account.isAccountPrivate());
		assertEquals(user, account.getOwner());

		assertEquals(getAccountDTO, actual);
	}

	@Test
	public void modifyAccount() {
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

		ArgumentCaptor<Account> accountArgument = ArgumentCaptor
				.forClass(Account.class);
		verify(mockAccountDao).update(accountArgument.capture());
		Account modifiedAccount = accountArgument.getValue();

		assertEquals("new account path", modifiedAccount.getAccountPath());
		assertTrue(modifiedAccount.isAccountPrivate());
		assertEquals(999, modifiedAccount.getRiverQuotaRemaining());
		assertTrue(modifiedAccount.getOwner().getActive());
		assertEquals("email@example.com", modifiedAccount.getOwner().getEmail());
		assertEquals("email@example.com", modifiedAccount.getOwner()
				.getUsername());
		assertEquals("owner's new name", modifiedAccount.getOwner().getName());
		assertTrue(passwordEncoder.matches("new password", modifiedAccount
				.getOwner().getPassword()));
	}

	@Test
	public void createUserToken() {
		User user = new User();

		accountService.createUserToken(user);

		ArgumentCaptor<UserToken> userTokenArgument = ArgumentCaptor
				.forClass(UserToken.class);
		verify(mockUserTokenDao).create(userTokenArgument.capture());
		User tokenUser = userTokenArgument.getValue().getUser();

		assertEquals(user, tokenUser);
	}

	@Test
	public void getClients() {
		Account account = new Account();
		account.setClients(new HashSet<Client>());
		Client client = new Client();
		client.setId(1L);
		client.setClientId("trusted-client");
		client.setClientSecret("8b22f281afd911c3dfc59270af43db1995d5968a3447c780ba3e152e603fd9a0");
		client.setDescription("the description");
		client.setHomepage("the homepage");
		client.setRedirectUri("the redirect uri");
		account.getClients().add(client);

		when(mockAccountDao.findById(anyLong())).thenReturn(account);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(account);

		accountService.setMapper(mapper);
		List<GetClientDTO> clients = accountService.getClients(1L, "username");
		
		assertEquals(1, clients.size());
		
		GetClientDTO dto = clients.get(0);
		assertEquals(1, dto.getId());
		assertEquals("trusted-client", dto.getClientId());
		assertEquals("somesecret", dto.getClientSecret());
		assertEquals("the description", dto.getDescription());
		assertEquals("the homepage", dto.getHomepage());
		assertEquals("the redirect uri", dto.getRedirectUri());
	}
	
	@Test
	public void createClient() {
		Account account = new Account();
		Role role = new Role();
		CreateClientDTO createClientDTO = new CreateClientDTO();
		createClientDTO.setName("new app's name");
		createClientDTO.setDescription("new app's description");
		createClientDTO.setHomepage("http://example.com");
		createClientDTO.setRedirectUri("http://example.com/redirect");
		
		when(mockAccountDao.findById(anyLong())).thenReturn(account);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(account);
		when(mockRoleDao.findByName(anyString())).thenReturn(role);
		
		accountService.setMapper(mapper);
		GetClientDTO getClientDTO = accountService.createClient(1L, createClientDTO, "admin");
		
		ArgumentCaptor<Client> argument = ArgumentCaptor
				.forClass(Client.class);
		verify(mockClientDao).create(argument.capture());
		Client client = argument.getValue();
		assertEquals("new app's name", client.getName());
		assertEquals("new app's description", client.getDescription());
		assertEquals("http://example.com", client.getHomepage());
		assertEquals("http://example.com/redirect", client.getRedirectUri());
		assertTrue(client.getRoles().contains(role));
		assertNotNull(client.getClientId());
		assertNotNull(client.getClientSecret());
		
		TextEncryptor encryptor = Encryptors.text(
				TextUtil.convertStringToHex(accountService.getKey()),
				TextUtil.convertStringToHex(client.getClientId()));
		assertEquals(getClientDTO.getClientSecret(), encryptor.decrypt(client.getClientSecret()));
	}
	
	@Test
	public void deleteClient() {
		Account account = new Account();
		Client client = new Client();
		
		when(mockAccountDao.findById(anyLong())).thenReturn(account);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(account);
		when(mockClientDao.findById(anyLong())).thenReturn(client);
		
		accountService.deleteApp(1L, 1L, "admin");
		
		verify(mockClientDao).delete(client);
	}
}
