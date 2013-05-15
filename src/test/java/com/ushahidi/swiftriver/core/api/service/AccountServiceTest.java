/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/agpl.html>
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.core.api.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

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
import com.ushahidi.swiftriver.core.api.dao.ActivityDao;
import com.ushahidi.swiftriver.core.api.dao.ClientDao;
import com.ushahidi.swiftriver.core.api.dao.RoleDao;
import com.ushahidi.swiftriver.core.api.dao.UserDao;
import com.ushahidi.swiftriver.core.api.dao.UserTokenDao;
import com.ushahidi.swiftriver.core.api.dto.CreateAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateClientDTO;
import com.ushahidi.swiftriver.core.api.dto.GetAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.GetActivityDTO;
import com.ushahidi.swiftriver.core.api.dto.GetClientDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyAccountDTO;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.AccountFollower;
import com.ushahidi.swiftriver.core.model.Activity;
import com.ushahidi.swiftriver.core.model.ActivityType;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketActivity;
import com.ushahidi.swiftriver.core.model.Client;
import com.ushahidi.swiftriver.core.model.Form;
import com.ushahidi.swiftriver.core.model.FormActivity;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverActivity;
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

	private ActivityDao mockActivityDao;

	private Mapper mockMapper;

	private Mapper mapper;

	private AccountService accountService;

	private RiverService mockRiverService;

	private BucketService mockBucketService;

	private PasswordEncoder passwordEncoder;

	@Before
	public void setup() {
		account = new Account();
		account.setId(13);
		account.setOwner(new User());
		account.setFollowers(new ArrayList<AccountFollower>());
		account.setFollowing(new ArrayList<AccountFollower>());

		getAccountDTO = new GetAccountDTO();

		mockAccountDao = mock(AccountDao.class);
		mockUserDao = mock(UserDao.class);
		mockUserTokenDao = mock(UserTokenDao.class);
		mockClientDao = mock(ClientDao.class);
		mockRoleDao = mock(RoleDao.class);
		mockActivityDao = mock(ActivityDao.class);
		mockMapper = mock(Mapper.class);
		mapper = new DozerBeanMapper();
		passwordEncoder = new BCryptPasswordEncoder();
		when(mockMapper.map(account, GetAccountDTO.class)).thenReturn(
				getAccountDTO);
		mockRiverService = mock(RiverService.class);
		mockBucketService = mock(BucketService.class);

		accountService = new AccountService();
		accountService.setRiverService(mockRiverService);
		accountService.setBucketService(mockBucketService);
		accountService.setAccountDao(mockAccountDao);
		accountService.setMapper(mockMapper);
		accountService.setUserDao(mockUserDao);
		accountService.setUserTokenDao(mockUserTokenDao);
		accountService.setClientDao(mockClientDao);
		accountService.setRoleDao(mockRoleDao);
		accountService.setActivityDao(mockActivityDao);
		accountService.setPasswordEncoder(passwordEncoder);
		accountService.setEncryptionKey("2344228477#97{7&6>82");
	}

	@Test
	public void findById() throws NotFoundException {
		when(mockAccountDao.findById(anyLong())).thenReturn(account);

		GetAccountDTO actualGetAccountDTO = accountService.getAccountById(13L,
				"admin");

		verify(mockAccountDao).findById(13L);
		assertEquals(getAccountDTO, actualGetAccountDTO);
	}

	@Test
	public void findByUsername() throws NotFoundException {
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(account);

		GetAccountDTO actualGetAccountDTO = accountService
				.getAccountByUsername("admin");

		verify(mockAccountDao).findByUsernameOrEmail("admin");
		assertEquals(getAccountDTO, actualGetAccountDTO);
	}

	@Test
	public void findByName() throws NotFoundException {
		when(mockAccountDao.findByAccountPath(anyString())).thenReturn(account);

		GetAccountDTO actualGetAccountDTO = accountService
				.getAccountByAccountPath("default", false, "user1");

		verify(mockAccountDao).findByAccountPath("default");
		assertEquals(getAccountDTO, actualGetAccountDTO);
	}

	@Test
	public void findByEmail() throws NotFoundException {
		when(mockAccountDao.findByEmail(anyString())).thenReturn(account);

		GetAccountDTO actualGetAccountDTO = accountService.getAccountByEmail(
				"email", false, "user1");

		verify(mockAccountDao).findByEmail("email");
		assertEquals(getAccountDTO, actualGetAccountDTO);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void mapGetAccountDTO() {
		List<River> filteredRivers = new ArrayList<River>();
		filteredRivers.add(new River());
		when(mockRiverService.filterVisible(anyList(), (Account) anyObject()))
				.thenReturn(filteredRivers);
		List<Bucket> filteredBuckets = new ArrayList<Bucket>();
		filteredBuckets.add(new Bucket());
		when(mockBucketService.filterVisible(anyList(), (Account) anyObject()))
				.thenReturn(filteredBuckets);

		GetAccountDTO actualGetAccountDTO = accountService.mapGetAccountDTO(
				account, account);

		assertEquals(getAccountDTO, actualGetAccountDTO);

		ArgumentCaptor<Account> argument = ArgumentCaptor
				.forClass(Account.class);
		verify(mockMapper).map(argument.capture(), any(Class.class));
		Account modifiedAccount = argument.getValue();
		assertEquals(filteredRivers, modifiedAccount.getRivers());
		assertEquals(filteredRivers, modifiedAccount.getCollaboratingRivers());
		assertEquals(filteredRivers, modifiedAccount.getFollowingRivers());
		assertEquals(filteredBuckets, modifiedAccount.getBuckets());
		assertEquals(filteredBuckets, modifiedAccount.getCollaboratingBuckets());
		assertEquals(filteredBuckets, modifiedAccount.getFollowingBuckets());
	}

	@Test
	public void search() {
		AccountDao mockAccountDao = mock(AccountDao.class);
		List<Account> accounts = new ArrayList<Account>();
		accounts.add(account);
		when(mockAccountDao.search(anyString())).thenReturn(accounts);
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(account);
		accountService.setAccountDao(mockAccountDao);

		List<GetAccountDTO> getAccountDTOs = accountService.searchAccounts(
				"abcd", "user1");

		verify(mockAccountDao).search("abcd");
		assertEquals(1, getAccountDTOs.size());
	}

	@SuppressWarnings("unchecked")
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

		User user = new User();
		user.setActive(false);
		user.setExpired(true);
		user.setLocked(true);
		user.setId(1);
		Account account = new Account();
		account.setActive(false);
		account.setOwner(user);
		account.setFollowers(new ArrayList<AccountFollower>());
		account.setFollowing(new ArrayList<AccountFollower>());
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
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(account);

		accountService.modifyAccount(1L, modifyAccount, "admin");

		ArgumentCaptor<Account> accountArgument = ArgumentCaptor
				.forClass(Account.class);
		verify(mockAccountDao).update(accountArgument.capture());
		Account modifiedAccount = accountArgument.getValue();

		assertEquals("new account path", modifiedAccount.getAccountPath());
		assertTrue(modifiedAccount.isAccountPrivate());
		assertEquals(999, modifiedAccount.getRiverQuotaRemaining());
		assertTrue(modifiedAccount.isActive());
		assertTrue(modifiedAccount.getOwner().getActive());
		assertFalse(modifiedAccount.getOwner().getExpired());
		assertFalse(modifiedAccount.getOwner().getLocked());
		assertEquals("email@example.com", modifiedAccount.getOwner().getEmail());
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
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(account);

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
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(account);
		when(mockRoleDao.findByName(anyString())).thenReturn(role);

		accountService.setMapper(mapper);
		GetClientDTO getClientDTO = accountService.createClient(1L,
				createClientDTO, "admin");

		ArgumentCaptor<Client> argument = ArgumentCaptor.forClass(Client.class);
		verify(mockClientDao).create(argument.capture());
		Client client = argument.getValue();
		assertEquals("new app's name", client.getName());
		assertEquals("new app's description", client.getDescription());
		assertEquals("http://example.com", client.getHomepage());
		assertEquals("http://example.com/redirect", client.getRedirectUri());
		assertTrue(client.getActive());
		assertTrue(client.getRoles().contains(role));
		assertNotNull(client.getClientId());
		assertNotNull(client.getClientSecret());

		TextEncryptor encryptor = Encryptors.text(
				TextUtil.convertStringToHex(accountService.getEncryptionKey()),
				TextUtil.convertStringToHex(client.getClientId()));
		assertEquals(getClientDTO.getClientSecret(),
				encryptor.decrypt(client.getClientSecret()));
	}

	@Test
	public void deleteClient() {
		Account account = new Account();
		Client client = new Client();

		when(mockAccountDao.findById(anyLong())).thenReturn(account);
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(account);
		when(mockClientDao.findById(anyLong())).thenReturn(client);

		accountService.deleteApp(1L, 1L, "admin");

		verify(mockClientDao).delete(client);
	}

	@Test
	public void getActivities() {
		accountService.setMapper(mapper);

		List<Activity> activities = new ArrayList<Activity>();

		// The authenticated account
		Account account = new Account();

		// Add a river activity the authenticated user owns
		Activity activity = new RiverActivity();
		activity.setId(1L);
		River river = new River();
		river.setAccount(account);
		river.setRiverPublic(true);
		((RiverActivity) activity).setActionOnObj(river);
		activities.add(activity);

		// Add a river activity the authenticated user *does not* own
		activity = new RiverActivity();
		activity.setId(2L);
		river = new River();
		river.setAccount(account);
		river.setRiverPublic(false);
		((RiverActivity) activity).setActionOnObj(river);
		activities.add(activity);

		// Add a bucket activity the authenticated user owns
		activity = new BucketActivity();
		activity.setId(3L);
		Bucket bucket = new Bucket();
		bucket.setAccount(account);
		bucket.setPublished(true);
		((BucketActivity) activity).setActionOnObj(bucket);
		activities.add(activity);

		// Add a bucket activity the authenticated user *does not* own
		activity = new BucketActivity();
		activity.setId(4L);
		bucket = new Bucket();
		bucket.setAccount(account);
		bucket.setPublished(false);
		((BucketActivity) activity).setActionOnObj(bucket);
		activities.add(activity);

		// Add a form activity the authenticated user owns
		activity = new FormActivity();
		activity.setId(5L);
		Form form = new Form();
		form.setAccount(account);
		((FormActivity) activity).setActionOnObj(form);
		activities.add(activity);

		when(mockActivityDao.find(1L, 2, 3L, true, false)).thenReturn(activities);

		List<GetActivityDTO> ret = accountService.getActivities(1L, 2, 3L,
				true, false, account);
		assertEquals(3, ret.size());
		assertEquals("1", ret.get(0).getId());
		assertEquals("3", ret.get(1).getId());
		assertEquals("5", ret.get(2).getId());
	}

	/**
	 * Check that getActivities will continue searching for results when all
	 * activities are removed due to the permissions check.
	 */
	@Test(expected = NotFoundException.class)
	public void getActivitiesRecursively() {
		accountService.setMapper(mapper);

		List<Activity> activities = new ArrayList<Activity>();

		// The authenticated account
		Account account = new Account();

		// Add a river activity the authenticated user *does not* own
		Activity activity = new RiverActivity();
		activity.setId(2L);
		River river = new River();
		river.setAccount(account);
		river.setRiverPublic(false);
		((RiverActivity) activity).setActionOnObj(river);
		activities.add(activity);

		when(mockActivityDao.find(1L, 2, 1L, true, false)).thenReturn(activities);
		when(mockActivityDao.find(1L, 2, 2L, true, false)).thenReturn(null);

		accountService.getActivities(1L, 2, 1L, true, false, account);

	}

	@Test
	public void logRiverActivity() {
		Account account = new Account();
		River river = new River();

		accountService.logActivity(account, ActivityType.CREATE, river);

		ArgumentCaptor<Activity> argument = ArgumentCaptor
				.forClass(Activity.class);
		verify(mockActivityDao).create(argument.capture());
		Activity activity = argument.getValue();
		
		assertTrue(activity instanceof RiverActivity);
		assertEquals(river, ((RiverActivity)activity).getActionOnObj());
		assertEquals(account, activity.getAccount());
	}
	
	@Test
	public void logBucketActivity() {
		Account account = new Account();
		Bucket bucket = new Bucket();

		accountService.logActivity(account, ActivityType.CREATE, bucket);

		ArgumentCaptor<Activity> argument = ArgumentCaptor
				.forClass(Activity.class);
		verify(mockActivityDao).create(argument.capture());
		Activity activity = argument.getValue();
		
		assertTrue(activity instanceof BucketActivity);
		assertEquals(bucket, ((BucketActivity)activity).getActionOnObj());
		assertEquals(account, activity.getAccount());
	}
	
	@Test
	public void logFormActivity() {
		Account account = new Account();
		Form form = new Form();

		accountService.logActivity(account, ActivityType.CREATE, form);

		ArgumentCaptor<Activity> argument = ArgumentCaptor
				.forClass(Activity.class);
		verify(mockActivityDao).create(argument.capture());
		Activity activity = argument.getValue();
		
		assertTrue(activity instanceof FormActivity);
		assertEquals(form, ((FormActivity)activity).getActionOnObj());
		assertEquals(account, activity.getAccount());
	}
	
	@Test
	public void logActivityCheckActionOn() {
		Account account = new Account();
		River river = new River();

		accountService.logActivity(account, ActivityType.CREATE, river);
		accountService.logActivity(account, ActivityType.FOLLOW, river);
		accountService.logActivity(account, ActivityType.INVITE, river);
		accountService.logActivity(account, ActivityType.COMMENT, river);

		ArgumentCaptor<Activity> argument = ArgumentCaptor
				.forClass(Activity.class);
		verify(mockActivityDao, times(4)).create(argument.capture());
		
		List<Activity> activities = argument.getAllValues();
		assertEquals("create", activities.get(0).getAction());
		assertEquals("follow", activities.get(1).getAction());
		assertEquals("invite", activities.get(2).getAction());
		assertEquals("comment", activities.get(3).getAction());
		
	}
	
	@Test(expected = RuntimeException.class)
	public void logActivityOnUnknownObject() {
		Account account = new Account();
		
		accountService.logActivity(account, ActivityType.CREATE, new Object());
	}
	
	@Test
	public void addFollower() {
		AccountService spy = spy(accountService);
		
		Account account = new Account();
		Account follower = new Account();
		
		when(mockAccountDao.findById(1L)).thenReturn(account);
		when(mockAccountDao.findById(2L)).thenReturn(follower);
		
		spy.addFollower(1L, 2L);
		
		verify(mockAccountDao).addFollower(account, follower);
		verify(spy).logActivity(follower, ActivityType.FOLLOW, account);
	}
}
