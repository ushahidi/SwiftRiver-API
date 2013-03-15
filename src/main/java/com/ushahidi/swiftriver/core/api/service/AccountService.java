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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.ClientDao;
import com.ushahidi.swiftriver.core.api.dao.RoleDao;
import com.ushahidi.swiftriver.core.api.dao.UserDao;
import com.ushahidi.swiftriver.core.api.dao.UserTokenDao;
import com.ushahidi.swiftriver.core.api.dto.CreateAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateClientDTO;
import com.ushahidi.swiftriver.core.api.dto.FollowerDTO;
import com.ushahidi.swiftriver.core.api.dto.GetAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.GetClientDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyClientDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ErrorField;
import com.ushahidi.swiftriver.core.api.exception.ForbiddenException;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.AccountFollower;
import com.ushahidi.swiftriver.core.model.Client;
import com.ushahidi.swiftriver.core.model.Role;
import com.ushahidi.swiftriver.core.model.User;
import com.ushahidi.swiftriver.core.model.UserToken;
import com.ushahidi.swiftriver.core.util.ErrorUtil;
import com.ushahidi.swiftriver.core.util.TextUtil;

@Transactional(readOnly = true)
@Service
public class AccountService {

	final Logger logger = LoggerFactory.getLogger(AccountService.class);

	@Autowired
	private RiverService riverService;

	@Autowired
	private BucketService bucketService;

	@Autowired
	private UserDao userDao;

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private UserTokenDao userTokenDao;

	@Autowired
	private ClientDao clientDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private Mapper mapper;

	private String key;

	public RiverService getRiverService() {
		return riverService;
	}

	public void setRiverService(RiverService riverService) {
		this.riverService = riverService;
	}

	public BucketService getBucketService() {
		return bucketService;
	}

	public void setBucketService(BucketService bucketService) {
		this.bucketService = bucketService;
	}

	public AccountDao getAccountDao() {
		return accountDao;
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public UserTokenDao getUserTokenDao() {
		return userTokenDao;
	}

	public void setUserTokenDao(UserTokenDao userTokenDao) {
		this.userTokenDao = userTokenDao;
	}

	public ClientDao getClientDao() {
		return clientDao;
	}

	public void setClientDao(ClientDao clientDao) {
		this.clientDao = clientDao;
	}

	public RoleDao getRoleDao() {
		return roleDao;
	}

	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public Mapper getMapper() {
		return mapper;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * Get an account using its id
	 * 
	 * @param id
	 * @return
	 * @throws NotFoundException
	 */
	public GetAccountDTO getAccountById(Long id, String authUser)
			throws NotFoundException {
		Account account = getAccount(id);

		return mapGetAccountDTO(account, accountDao.findByUsername(authUser));
	}

	/**
	 * Get an account by username
	 * 
	 * @param username
	 * @return
	 * @throws NotFoundException
	 */
	public GetAccountDTO getAccountByUsername(String username)
			throws NotFoundException {
		Account account = accountDao.findByUsername(username);

		if (account == null) {
			throw new NotFoundException("Account not found");
		}

		return mapGetAccountDTO(account, account);
	}

	/**
	 * Get an account by account_path
	 * 
	 * @param accountPath
	 * @return
	 * @throws NotFoundException
	 */
	public GetAccountDTO getAccountByAccountPath(String accountPath,
			boolean getToken, String authUser) throws NotFoundException {
		Account account = accountDao.findByAccountPath(accountPath);

		if (account == null) {
			throw new NotFoundException("Account not found");
		}

		GetAccountDTO getAccountDTO = mapGetAccountDTO(account,
				accountDao.findByUsername(authUser));
		if (getToken) {
			getAccountDTO.setToken(createUserToken(account.getOwner())
					.getToken());
		}
		return getAccountDTO;
	}

	/**
	 * Get an account by email
	 * 
	 * @param username
	 * @return
	 * @throws NotFoundException
	 */
	public GetAccountDTO getAccountByEmail(String email, boolean getToken,
			String authUser) throws NotFoundException {
		Account account = accountDao.findByEmail(email);

		if (account == null) {
			throw new NotFoundException("Account not found");
		}

		GetAccountDTO getAccountDTO = mapGetAccountDTO(account,
				accountDao.findByUsername(authUser));
		if (getToken) {
			getAccountDTO.setToken(createUserToken(account.getOwner())
					.getToken());
		}
		return getAccountDTO;
	}

	/**
	 * Search accounts
	 * 
	 * @param query
	 * @return
	 * @throws NotFoundException
	 */
	public List<GetAccountDTO> searchAccounts(String query, String authUser)
			throws NotFoundException {
		List<Account> accounts = accountDao.search(query);

		if (accounts == null) {
			throw new NotFoundException("No accounts found");
		}

		Account queryingAccount = accountDao.findByUsername(authUser);
		List<GetAccountDTO> getAccountTOs = new ArrayList<GetAccountDTO>();
		for (Account account : accounts) {
			getAccountTOs.add(mapGetAccountDTO(account, queryingAccount));
		}

		return getAccountTOs;
	}

	/**
	 * Create an account
	 * 
	 * @param createAccountTO
	 * @return
	 */
	@Transactional(readOnly = false)
	public GetAccountDTO createAccount(CreateAccountDTO createAccountTO) {
		if (accountDao.findByAccountPath(createAccountTO.getAccountPath()) != null) {
			BadRequestException ex = new BadRequestException(
					"Account already exists");
			List<ErrorField> errors = new ArrayList<ErrorField>();
			errors.add(new ErrorField("account_path", "duplicate"));
			ex.setErrors(errors);
			throw ex;
		}

		if (accountDao.findByEmail(createAccountTO.getEmail()) != null) {
			BadRequestException ex = new BadRequestException(
					"Account already exists");
			List<ErrorField> errors = new ArrayList<ErrorField>();
			errors.add(new ErrorField("email", "duplicate"));
			ex.setErrors(errors);
			throw ex;
		}

		User user = new User();
		user.setActive(false);
		user.setName(createAccountTO.getName());
		user.setEmail(createAccountTO.getEmail());
		user.setUsername(createAccountTO.getEmail());
		user.setPassword(passwordEncoder.encode(createAccountTO.getPassword()));
		userDao.create(user);

		Account account = new Account();
		account.setAccountPath(createAccountTO.getAccountPath());
		account.setAccountPrivate(createAccountTO.isAccountPrivate());
		account.setOwner(user);
		accountDao.create(account);

		GetAccountDTO getAccountTo = mapper.map(account, GetAccountDTO.class);

		UserToken token = createUserToken(user);
		getAccountTo.setToken(token.getToken());
		return getAccountTo;
	}

	/**
	 * Modify an existing account
	 * 
	 * @param accountId
	 * @param modifyAccountTO
	 * @param authUser
	 * @return
	 */
	@Transactional(readOnly = false)
	public GetAccountDTO modifyAccount(Long accountId, ModifyAccountDTO modifyAccountTO,
			String authUser) {

		Account account = accountDao.findById(accountId);
		Account queryingAccount = accountDao.findByUsername(authUser);

		if (account == null)
			throw new NotFoundException("Account not found.");

		// If the querying account is not the the same as the account being modified
		// raise an error
		if (!account.equals(queryingAccount)) {
			throw new ForbiddenException("You do not have sufficient privileges to modify this account");
		}

		ModifyAccountDTO.User modifyAcOwner = modifyAccountTO.getOwner();

		// If another account already has the specified account path, raise an error
		if (modifyAccountTO.getAccountPath() != null) {
			String accountPath = modifyAccountTO.getAccountPath();
			Account otherAccount = accountDao.findByAccountPath(accountPath); 
			if (otherAccount != null && !otherAccount.equals(account)) {
				throw ErrorUtil.getBadRequestException("account_path",
						"duplicate");
			}
		}
		
		// If modifying password without a token, raise an error
		if (modifyAccountTO.getToken() == null && modifyAcOwner != null
				&& modifyAcOwner.getPassword() != null && modifyAcOwner.getCurrentPassword() == null)
			throw ErrorUtil.getBadRequestException("token", "missing");

		//> Account Owner properties
		if (modifyAcOwner != null && modifyAcOwner.getEmail() != null) {
			String email = modifyAcOwner.getEmail();
			Account otherAccount = accountDao.findByEmail(email);
			if (otherAccount != null && !otherAccount.equals(account)) {
				throw ErrorUtil.getBadRequestException("owner.email",
						"duplicate");
			}
		}

		// Password change
		if (modifyAcOwner != null && modifyAcOwner.getCurrentPassword() != null) {
			// No token required for a change password 
			if (modifyAccountTO.getToken() != null) {
				throw ErrorUtil.getBadRequestException("token", "Invalid parameter");
			}
			
			// Check for new password 
			if (modifyAcOwner.getPassword() == null) {
				throw ErrorUtil.getBadRequestException("password", "missing");
			}
			
			// Current password
			if (!passwordEncoder.matches(modifyAcOwner.getCurrentPassword(), 
					account.getOwner().getPassword())) {
				throw ErrorUtil.getBadRequestException("password", "invalid");
			}
			
			String password = passwordEncoder.encode(modifyAcOwner.getPassword());
			modifyAcOwner.setPassword(password);
		}

		// Account activation or password reset
		if (modifyAccountTO.getToken() != null) {
			if (!isTokenValid(modifyAccountTO.getToken(), account.getOwner()))
				throw ErrorUtil.getBadRequestException("token", "invalid");

			// If a token is present, the account is active and no password,
			// then raise an error. Probably a password reset but password
			// missing.
			if (account.getOwner().getActive()
					&& (modifyAcOwner == null || (modifyAcOwner != null && modifyAcOwner
							.getPassword() == null)))
				throw ErrorUtil.getBadRequestException("password", "missing");

			// Activate account if inactive
			if (!account.getOwner().getActive()) {
				account.setActive(true);
				User user = account.getOwner();
				user.setActive(true);
				user.setExpired(false);
				user.setLocked(false);
				user.setRoles(new HashSet<Role>());
				user.getRoles().add(roleDao.findByName("user"));
			}

			// Encode and set the new password
			if (modifyAccountTO.getToken() != null && 
					modifyAcOwner != null && modifyAcOwner.getPassword() != null) {
				String password = passwordEncoder.encode(modifyAcOwner.getPassword());
				modifyAcOwner.setPassword(password);
			}
		}
		
		mapper.map(modifyAccountTO, account);
		accountDao.update(account);
		return mapGetAccountDTO(account, account);
	}

	/**
	 * Convert the given account into a GetAccountDTO and filter out rivers that
	 * queryingAccount has no access to.
	 * 
	 * @param account
	 * @param queryingAccount
	 * @return
	 */
	public GetAccountDTO mapGetAccountDTO(Account account,
			Account queryingAccount) {

		// Filter out private rivers queryingAccount has no access to
		account.setRivers(riverService.filterVisible(account.getRivers(),
				queryingAccount));
		account.setCollaboratingRivers(riverService.filterVisible(
				account.getCollaboratingRivers(), queryingAccount));
		account.setFollowingRivers(riverService.filterVisible(
				account.getFollowingRivers(), queryingAccount));

		// Filter out private buckets queryingAccount has no access to
		account.setBuckets(bucketService.filterVisible(account.getBuckets(),
				queryingAccount));
		account.setCollaboratingBuckets(bucketService.filterVisible(
				account.getCollaboratingBuckets(), queryingAccount));
		account.setFollowingBuckets(bucketService.filterVisible(
				account.getFollowingBuckets(), queryingAccount));

		GetAccountDTO accountDTO = mapper.map(account, GetAccountDTO.class);

		accountDTO.setFollowerCount(account.getFollowers().size());
		accountDTO.setFollowingCount(account.getFollowing().size());

		return accountDTO;
	}

	/**
	 * Returns true if the given token is valid for the user.
	 * 
	 * @param tokenString
	 * @param user
	 * @return
	 */
	@Transactional(readOnly = false)
	public boolean isTokenValid(String tokenString, User user) {
		// Activate account is matching user token found
		UserToken token = userTokenDao.findByToken(tokenString);

		if (token == null || token.getUser().getId() != user.getId()
				|| token.getExpires().getTime() < (new Date()).getTime())
			return false;

		userTokenDao.delete(token);
		return true;
	}

	/**
	 * Create a token for the given user.
	 * 
	 * @param user
	 * @return
	 */
	@Transactional(readOnly = false)
	public UserToken createUserToken(User user) {
		UserToken token = new UserToken();
		token.setToken(UUID.randomUUID().toString());
		long expiryDate = (new Date()).getTime() + 86400000L;
		token.setExpires(new Date(expiryDate));
		token.setUser(user);

		userTokenDao.create(token);

		return token;
	}

	/**
	 * Get clients in the given account
	 * 
	 * @param accountId
	 * @param authUser
	 * @return
	 */
	public List<GetClientDTO> getClients(Long accountId, String authUser) {

		Account account = accountDao.findById(accountId);

		if (account == null)
			throw new NotFoundException("Account not found");

		Account authAccount = accountDao.findByUsername(authUser);

		if (!authAccount.equals(account))
			throw new ForbiddenException("Permission Denied");

		List<GetClientDTO> clients = new ArrayList<GetClientDTO>();
		for (Client client : account.getClients()) {
			GetClientDTO dto = mapper.map(client, GetClientDTO.class);
			dto.setClientSecret(decryptClientSecret(client));

			clients.add(dto);
		}

		return clients;
	}

	/**
	 * Create a new client under the given account.
	 * 
	 * @param accountId
	 * @param createClientTO
	 * @param name
	 * @return
	 */
	@Transactional(readOnly = false)
	public GetClientDTO createClient(Long accountId,
			CreateClientDTO createClientTO, String name) {
		Account account = accountDao.findById(accountId);

		if (account == null)
			throw new NotFoundException("Account not found");

		if (!account.equals(accountDao.findByUsername(name)))
			throw new ForbiddenException("Permission Denied");

		Client client = mapper.map(createClientTO, Client.class);
		String secret = resetClientCredentials(client);
		client.setAccount(account);
		client.setRoles(new HashSet<Role>());
		client.getRoles().add(roleDao.findByName("client"));
		client.setActive(true);
		clientDao.create(client);

		GetClientDTO dto = mapper.map(client, GetClientDTO.class);
		dto.setClientSecret(secret);
		return dto;
	}

	@Transactional(readOnly = false)
	public void deleteApp(Long accountId, Long clientId, String authUser) {
		Account account = accountDao.findById(accountId);

		if (account == null)
			throw new NotFoundException("Account not found");

		if (!account.equals(accountDao.findByUsername(authUser)))
			throw new ForbiddenException("Permission Denied");

		Client client = clientDao.findById(clientId);
		if (client == null)
			throw new NotFoundException("Client not found");

		clientDao.delete(client);
	}

	/**
	 * Mofify the given client app.
	 * 
	 * @param accountId
	 * @param dbClientId
	 * @param modifyClientDTO
	 * @param authUser
	 * @return
	 */
	@Transactional(readOnly = false)
	public GetClientDTO modifyClient(Long accountId, Long dbClientId,
			ModifyClientDTO modifyClientDTO, String authUser) {

		Account account = accountDao.findById(accountId);

		if (account == null)
			throw new NotFoundException("Account not found");

		if (!account.equals(accountDao.findByUsername(authUser)))
			throw new ForbiddenException("Permission Denied");

		Client client = clientDao.findById(dbClientId);
		if (client == null)
			throw new NotFoundException("Client not found");

		mapper.map(modifyClientDTO, client);

		String secret = null;
		if (modifyClientDTO.getClientId() != null) {
			secret = resetClientCredentials(client);
		}

		clientDao.update(client);

		GetClientDTO getClientDTO = mapper.map(client, GetClientDTO.class);
		if (secret != null) {
			// We present the plain text secret
			getClientDTO.setClientSecret(secret);
		} else {
			getClientDTO.setClientSecret(decryptClientSecret(client));
		}

		return getClientDTO;
	}

	/**
	 * Sets a random client_id and client_secret in the given Client object.
	 * 
	 * @param client
	 * @return The plain text secret.
	 */
	private String resetClientCredentials(Client client) {
		// Reset the client credentials
		String clientId = UUID.randomUUID().toString();
		String secret = UUID.randomUUID().toString();

		// Encrypt the secret
		TextEncryptor encryptor = Encryptors.text(
				TextUtil.convertStringToHex(key),
				TextUtil.convertStringToHex(clientId));
		client.setClientSecret(encryptor.encrypt(secret));
		client.setClientId(clientId);

		return secret;
	}

	/**
	 * Get the plain text client_secret for the given client.
	 * 
	 * @param client
	 * @return
	 */
	private String decryptClientSecret(Client client) {
		TextEncryptor encryptor = Encryptors.text(
				TextUtil.convertStringToHex(key),
				TextUtil.convertStringToHex(client.getClientId()));
		return encryptor.decrypt(client.getClientSecret());
	}

	/**
	 * Gets and returns the list of {@link Account} entities that are following
	 * the {@Account} specified in <code>id</code>.
	 * <code>accountId</code> is an optional parameter and when specified,
	 * verifies whether the {@link Account} with that id (<code>accountId</code>
	 * ) is a follower
	 * 
	 * @param id
	 * @param accountId
	 * @return {@link java.util.List}
	 */
	@Transactional
	public List<FollowerDTO> getFollowers(Long id, Long accountId) {
		Account account = getAccount(id);

		List<FollowerDTO> followers = new ArrayList<FollowerDTO>();
		if (accountId == null) {
			for (AccountFollower accountFollower : account.getFollowers()) {
				followers.add(mapFollowerDTO(accountFollower.getFollower()));
			}
		} else {
			Account follower = accountDao.getFollower(account, accountId);
			if (follower == null) {
				throw new NotFoundException(String.format(
						"Account %d does not follow %d", accountId, id));
			}

			followers.add(mapFollowerDTO(follower));
		}

		return followers;
	}

	/**
	 * Converts the given {@link Account} to a {@link FollowerDTO}
	 * 
	 * @param account
	 * @return
	 */
	private FollowerDTO mapFollowerDTO(Account account) {
		FollowerDTO followerDTO = new FollowerDTO();

		followerDTO.setId(account.getId());
		followerDTO.setAccountPath(account.getAccountPath());
		followerDTO.setEmail(account.getOwner().getEmail());
		followerDTO.setName(account.getOwner().getName());
		followerDTO.setFollowerCount(account.getFollowers().size());
		followerDTO.setFollowingCount(account.getFollowing().size());

		return followerDTO;
	}

	/**
	 * Adds the {@link Account} with the specified <code>accountId</code> to the
	 * list of followers for the {@link Account} specified in <code>id</code>
	 * 
	 * @param id
	 * @param accountId
	 */
	@Transactional
	public void addFollower(Long id, Long accountId) {
		if (id.equals(accountId)) {
			throw new BadRequestException("An account cannot follow itself");
		}

		Account account = getAccount(id);

		if (accountDao.getFollower(account, accountId) != null) {
			throw new BadRequestException(
					String.format("Account %d is already following account %d",
							accountId, id));
		}

		Account follower = getAccount(accountId);
		accountDao.addFollower(account, follower);
	}

	/**
	 * Internal helper method for retrieving a {@link Account} entity using its
	 * unique database ID
	 * 
	 * @param accountId
	 * @return
	 */
	private Account getAccount(Long accountId) {
		Account account = accountDao.findById(accountId);
		if (account == null) {
			throw new NotFoundException(String.format(
					"Account %d does not exist", accountId));
		}

		return account;
	}

	/**
	 * Deletes the {@link Account} specified in <code>accountId</code> from the
	 * list of followers for the {@link Account} specified in <code>id</code>
	 * 
	 * @param id
	 * @param accountId
	 */
	@Transactional
	public void deleteFollower(Long id, Long accountId) {
		Account account = getAccount(id);
		if (!accountDao.deleteFollower(account, getAccount(accountId))) {
			throw new NotFoundException(String.format(
					"Account %d does not follow account %d", accountId, id));
		}
	}

}
