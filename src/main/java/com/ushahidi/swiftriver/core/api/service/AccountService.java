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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

import com.ushahidi.swiftriver.core.api.auth.AuthenticationScheme;
import com.ushahidi.swiftriver.core.api.auth.crowdmapid.CrowdmapIDClient;
import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.ActivityDao;
import com.ushahidi.swiftriver.core.api.dao.ClientDao;
import com.ushahidi.swiftriver.core.api.dao.RoleDao;
import com.ushahidi.swiftriver.core.api.dao.UserDao;
import com.ushahidi.swiftriver.core.api.dao.UserTokenDao;
import com.ushahidi.swiftriver.core.api.dto.ActivateAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateClientDTO;
import com.ushahidi.swiftriver.core.api.dto.FollowerDTO;
import com.ushahidi.swiftriver.core.api.dto.GetAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.GetActivityDTO;
import com.ushahidi.swiftriver.core.api.dto.GetClientDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyClientDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ErrorField;
import com.ushahidi.swiftriver.core.api.exception.ForbiddenException;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.mail.EmailHelper;
import com.ushahidi.swiftriver.core.mail.EmailType;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.AccountActivity;
import com.ushahidi.swiftriver.core.model.AccountFollower;
import com.ushahidi.swiftriver.core.model.Activity;
import com.ushahidi.swiftriver.core.model.ActivityType;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketActivity;
import com.ushahidi.swiftriver.core.model.BucketCollaborator;
import com.ushahidi.swiftriver.core.model.BucketCollaboratorActivity;
import com.ushahidi.swiftriver.core.model.Client;
import com.ushahidi.swiftriver.core.model.Form;
import com.ushahidi.swiftriver.core.model.FormActivity;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverActivity;
import com.ushahidi.swiftriver.core.model.RiverCollaborator;
import com.ushahidi.swiftriver.core.model.RiverCollaboratorActivity;
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
	private ActivityDao activityDao;

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

	private String encryptionKey;
	
	private AuthenticationScheme authenticationScheme;

	private CrowdmapIDClient crowdmapIDClient;

	private EmailHelper emailHelper;

	public void setRiverService(RiverService riverService) {
		this.riverService = riverService;
	}

	public void setBucketService(BucketService bucketService) {
		this.bucketService = bucketService;
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public void setActivityDao(ActivityDao activityDao) {
		this.activityDao = activityDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setUserTokenDao(UserTokenDao userTokenDao) {
		this.userTokenDao = userTokenDao;
	}

	public void setClientDao(ClientDao clientDao) {
		this.clientDao = clientDao;
	}

	public void setRoleDao(RoleDao roleDao) {
		this.roleDao = roleDao;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	public String getEncryptionKey() {
		return encryptionKey;
	}

	public void setEncryptionKey(String key) {
		this.encryptionKey = key;
	}

	public AuthenticationScheme getAuthenticationScheme() {
		return authenticationScheme;
	}

	public void setAuthenticationScheme(AuthenticationScheme authenticationScheme) {
		this.authenticationScheme = authenticationScheme;
	}

	public void setCrowdmapIDClient(CrowdmapIDClient crowdmapIDClient) {
		this.crowdmapIDClient = crowdmapIDClient;
	}

	public void setEmailHelper(EmailHelper emailHelper) {
		this.emailHelper = emailHelper;
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

		return mapGetAccountDTO(account, accountDao.findByUsernameOrEmail(authUser));
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
		Account account = accountDao.findByUsernameOrEmail(username);

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
			String authUser) throws NotFoundException {
		Account account = accountDao.findByAccountPath(accountPath);

		if (account == null) {
			throw new NotFoundException("Account not found");
		}

		GetAccountDTO getAccountDTO = mapGetAccountDTO(account,
				accountDao.findByUsernameOrEmail(authUser));
		return getAccountDTO;
	}

	/**
	 * Get an account by email
	 * @param username
	 * 
	 * @return
	 * @throws NotFoundException
	 */
	public GetAccountDTO getAccountByEmail(String email, String authUser) throws NotFoundException {
		Account account = accountDao.findByEmail(email);

		if (account == null) {
			throw new NotFoundException("Account not found");
		}

		GetAccountDTO getAccountDTO = mapGetAccountDTO(account,
				accountDao.findByUsernameOrEmail(authUser));
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

		Account queryingAccount = accountDao.findByUsernameOrEmail(authUser);
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

		String password = createAccountTO.getPassword();

		// Check the authentication scheme
		switch (this.authenticationScheme) {
		case DEFAULT:
			user.setPassword(passwordEncoder.encode(password));
			break;

		case CROWDMAPID:
			createCrowdmapIDAccount(user, password);
			break;
		}
		userDao.create(user);

		Account account = new Account();
		account.setAccountPath(createAccountTO.getAccountPath());
		account.setAccountPrivate(createAccountTO.isAccountPrivate());
		account.setOwner(user);
		accountDao.create(account);

		UserToken userToken = createUserToken(user);

		// Send activation email
		emailHelper.sendAccountActivationEmail(user, userToken);

		return mapper.map(account, GetAccountDTO.class);
	}

	/**
	 * Creates a user on CrowdmapID
	 * 
	 * @param user
	 * @param password
	 */
	private void createCrowdmapIDAccount(User user, String password) {
		// Check if the user exists
		String email = user.getEmail();
		
		// Is the email registered on CrowdmapID?
		if (crowdmapIDClient.isRegistered(email)) {
			throw new BadRequestException(String.format(
					"'%s' is already registered to another user", email));
		}
		
		String crowdmapUUID = crowdmapIDClient.register(email, password);
		if (crowdmapUUID == null) {
			throw new BadRequestException(String.format(
					"An error occurred while registering '%s'",email));
		}
		
		logger.debug("{} successfully created with unique ID {}", email, crowdmapUUID);
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
	public GetAccountDTO modifyAccount(Long accountId,
			ModifyAccountDTO modifyAccountTO, String authUser) {

		Account account = accountDao.findById(accountId);
		Account queryingAccount = accountDao.findByUsernameOrEmail(authUser);

		if (account == null)
			throw new NotFoundException("Account not found.");

		// If the querying account is not the the same as the account being
		// modified
		// raise an error
		if (!account.equals(queryingAccount)) {
			throw new ForbiddenException("Permission denied");
		}

		ModifyAccountDTO.User modifyAcOwner = modifyAccountTO.getOwner();

		// If another account already has the specified account path, raise an
		// error
		if (modifyAccountTO.getAccountPath() != null) {
			String accountPath = modifyAccountTO.getAccountPath();
			Account otherAccount = accountDao.findByAccountPath(accountPath);
			if (otherAccount != null && !otherAccount.equals(account)) {
				throw ErrorUtil.getBadRequestException("account_path",
						"duplicate");
			}
		}

		// > Account Owner properties
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

			// Check for new password
			if (modifyAcOwner.getPassword() == null) {
				throw ErrorUtil.getBadRequestException("password", "missing");
			}

			String newPassword = modifyAcOwner.getPassword();
			String currentPassword = modifyAcOwner.getCurrentPassword();
			
			// Check the authentication scheme
			switch (this.authenticationScheme) {
			case CROWDMAPID:
				crowdmapIDClient.changePassword(account.getOwner().getEmail(),
						currentPassword, newPassword);
				break;

				default:
					// Current password
					if (!passwordEncoder.matches(currentPassword,
							account.getOwner().getPassword())) {
						throw ErrorUtil.getBadRequestException("password", "invalid");
					}
					modifyAcOwner.setPassword(passwordEncoder.encode(newPassword));
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

		if (!account.equals(queryingAccount)) {
			logger.debug("Getting list of accessible rivers");

			account.setRivers(riverService.filterVisible(account.getRivers(),
					queryingAccount));
			account.setCollaboratingRivers(riverService.filterVisible(
					account.getCollaboratingRivers(), queryingAccount));
			account.setFollowingRivers(riverService.filterVisible(
					account.getFollowingRivers(), queryingAccount));

			// Filter out private buckets queryingAccount has no access to
			logger.debug("Getting the list of accessible buckets");
			account.setBuckets(bucketService.filterVisible(account.getBuckets(),
					queryingAccount));
			account.setCollaboratingBuckets(bucketService.filterVisible(
					account.getCollaboratingBuckets(), queryingAccount));
			account.setFollowingBuckets(bucketService.filterVisible(
					account.getFollowingBuckets(), queryingAccount));
		}

		GetAccountDTO accountDTO = mapper.map(account, GetAccountDTO.class);

		logger.debug("Setting the follow counts");
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
	public boolean isValidToken(String tokenString, User user) {
		// Activate account is matching user token found
		UserToken token = userTokenDao.findByToken(tokenString);

		if (token == null || token.getUser().getId() != user.getId()
				|| token.getExpires().getTime() < (new Date()).getTime())
			return false;

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
		Date dateCreated = new Date();
		long expiryDate = dateCreated.getTime() + 86400000L;
		token.setExpires(new Date(expiryDate));
		token.setUser(user);
		token.setCreated(dateCreated);

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

		Account authAccount = accountDao.findByUsernameOrEmail(authUser);

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

		if (!account.equals(accountDao.findByUsernameOrEmail(name)))
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

		if (!account.equals(accountDao.findByUsernameOrEmail(authUser)))
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

		if (!account.equals(accountDao.findByUsernameOrEmail(authUser)))
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
				TextUtil.convertStringToHex(encryptionKey),
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
				TextUtil.convertStringToHex(encryptionKey),
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
		
		logActivity(follower, ActivityType.FOLLOW, account);
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

	/**
	 * Search accounts
	 * 
	 * @param searchTerm
	 * @param count
	 * @param page
	 * @return
	 */
	@Transactional
	public List<GetAccountDTO> searchAccounts(String searchTerm, int count, int page) {
		List<Account> accounts = accountDao.search(searchTerm, count, page);
		
		for (Account account: accounts) {
			if (account.isAccountPrivate()) {
				account.setRivers(new ArrayList<River>());
				account.setCollaboratingRivers(new ArrayList<River>());
				account.setBuckets(new ArrayList<Bucket>());
				account.setCollaboratingBuckets(new ArrayList<Bucket>());				
				continue;
			}
			// Remove private buckets (owned and collaborating)
			List<Bucket> privateOwnerBuckets = new ArrayList<Bucket>();
			for (Bucket bucket: account.getBuckets()) {
				if (!bucket.isPublished()) {
					privateOwnerBuckets.add(bucket);
				}
			}
			account.getBuckets().removeAll(privateOwnerBuckets);
			
			// Private buckets the current account is collaborating on
			List<Bucket> privateCollaboratingBuckets = new ArrayList<Bucket>(); 
			for (Bucket bucket: account.getCollaboratingBuckets()) {
				if (!bucket.isPublished()) {
					privateCollaboratingBuckets.add(bucket);
				}
			}
			account.getCollaboratingBuckets().removeAll(privateCollaboratingBuckets);

			// Remove private rivers (owned and collaborating)
			List<River> privateOwnerRivers = new ArrayList<River>();
			for (River river: account.getRivers()) {
				if (!river.getRiverPublic()) {
					privateOwnerRivers.add(river);
				}
			}
			account.getRivers().removeAll(privateOwnerRivers);
			
			// Private rivers the current account is collaborating on
			List<River> privateCollaboratingRivers = new ArrayList<River>(); 
			for (River river: account.getCollaboratingRivers()) {
				if (!river.getRiverPublic()) {
					privateCollaboratingRivers.add(river);
				}
			}
			account.getCollaboratingRivers().removeAll(privateCollaboratingRivers);
		}
		
		List<GetAccountDTO> getAccountDTOs = new ArrayList<GetAccountDTO>();
		for (Account account: accounts) {
			getAccountDTOs.add(mapper.map(account, GetAccountDTO.class));
		}

		return getAccountDTOs;
	}

	/**
	 * Create an activity for the given account action.
	 * 
	 * @param account
	 * @param action
	 * @param actionOn
	 */
	@Transactional(readOnly = true)
	public void logActivity(Account account, ActivityType action,
			Object actionOn) {

		Activity activity = null;
		if (actionOn instanceof River) {
			activity = new RiverActivity();
			((RiverActivity) activity).setActionOnObj((River) actionOn);
		} else if (actionOn instanceof Bucket) {
			activity = new BucketActivity();
			((BucketActivity) activity).setActionOnObj((Bucket) actionOn);
		} else if (actionOn instanceof Form) {
			activity = new FormActivity();
			((FormActivity) activity).setActionOnObj((Form) actionOn);
		} else if (actionOn instanceof Account) {
			activity = new AccountActivity();
			((AccountActivity) activity).setActionOnObj((Account)actionOn);
		} else if (actionOn instanceof RiverCollaborator) {
			activity = new RiverCollaboratorActivity();
			((RiverCollaboratorActivity) activity).setActionOnObj((RiverCollaborator)actionOn);
		} else if (actionOn instanceof BucketCollaborator) {
			activity = new BucketCollaboratorActivity();
			((BucketCollaboratorActivity) activity).setActionOnObj((BucketCollaborator)actionOn);
		}
		
		if (activity == null)
			throw new RuntimeException("Unknown actionOn obj provided");

		activity.setAccount(account);
		activity.setActionDateAdd(new Date());

		switch (action) {
		case CREATE:
			activity.setAction("create");
			break;
		case FOLLOW:
			activity.setAction("follow");
			break;
		case INVITE:
			activity.setAction("invite");
			break;
		case COMMENT:
			activity.setAction("comment");
			break;
		}
		
		activityDao.create(activity);
	}

	/**
	 * Get an activity stream
	 * 
	 * When the followers parameter is true, get activities for 
	 * the accounts <code>accountId</code> is following otherwise 
	 * get <code>accountId</code>'s own activities.
	 * 
	 * @param accountId
	 * @param count
	 * @param lastId
	 * @param newer
	 * @param followers
	 * @param authUser
	 * @return
	 */
	public List<GetActivityDTO> getActivities(Long accountId, Integer count,
			Long lastId, Boolean newer, Boolean followers, Account authAccount) {
	
		List<Activity> activities = activityDao.find(accountId, count, lastId,
				newer, followers);
	
		if (activities == null)
			throw new NotFoundException("No activities found.");
	
		List<GetActivityDTO> activityDtos = new ArrayList<GetActivityDTO>();
		for (Activity activity : activities) {
	
			// Update last id
			if (lastId != null) {
				if (newer != null && newer) {
					lastId = Math.max(lastId, activity.getId());
				} else {
					lastId = Math.min(lastId, activity.getId());
				}
			} else {
				lastId = activity.getId();
			}
	
			// Filter out activities on objects the authenticated user has
			// no access to
			if (activity instanceof RiverActivity) {
				River river = ((RiverActivity) activity).getActionOnObj();
	
				if (!river.getRiverPublic()
						&& !riverService.isOwner(river, authAccount))
					continue;
			} else if (activity instanceof BucketActivity) {
				Bucket bucket = ((BucketActivity) activity).getActionOnObj();
	
				if (!bucket.isPublished()
						&& !bucketService.isOwner(bucket, authAccount))
					continue;
			}
	
			activityDtos.add(mapper.map(activity, GetActivityDTO.class));
		}
	
		// If all activities removed due to permissions, repeat
		if (activityDtos.size() == 0)
			return getActivities(accountId, count, lastId, newer, followers, authAccount);
	
		return activityDtos;
	}

	/**
	 * Get Activities for the given account.
	 * 
	 * @param accountId
	 * @param authUser
	 * @return
	 */
	public List<GetActivityDTO> getActivities(Long accountId, String authUser) {
		Account account = accountDao.findByUsernameOrEmail(authUser);
		return getActivities(accountId, 50, Long.MAX_VALUE, false, false, account);
	}

	/**
	 * Get Activities by the Account that authUser is following.
	 * 
	 * @param count
	 * @param lastId
	 * @param newer
	 * @param authUser
	 * @return
	 */
	public List<GetActivityDTO> getTimeline(Integer count, Long lastId,
			Boolean newer, String authUser) {
		Account account = accountDao.findByUsernameOrEmail(authUser);
		return getActivities(account.getId(), count, lastId, newer, true, account);
	}

	/**
	 * Activates a newly created account.
	 * 
	 * @param activateAccountDTO
	 */
	@Transactional(readOnly = false)
	public void activateAccount(ActivateAccountDTO activateAccountDTO) {
		String email = activateAccountDTO.getEmail();
		String tokenString = activateAccountDTO.getToken();
		Account account = accountDao.findByEmail(email);

		if (account == null) {
			throw new NotFoundException(String.format("Account %s does not exist", email));
		}
		
		// If the user is active, return early
		User user = account.getOwner();
		if (user.getActive()) {
			logger.info("Ignoring account activation request. '{}' is already active.", email);
			return;
		}

		// Validate the token
		if (!isValidToken(tokenString, user)) {
			logger.debug("Invalid token {}", tokenString);
			throw new BadRequestException(String.format(
					"Invalid activation token specified - %s", activateAccountDTO.getToken()));
		}
		
		account.setActive(true);
		user.setActive(true);
		user.setExpired(false);
		user.setLocked(false);
		user.setRoles(new HashSet<Role>());
		user.getRoles().add(roleDao.findByName("user"));

		accountDao.update(account);
		userDao.update(user);

		userTokenDao.delete(tokenString);
	}

	/**
	 * Sets a new password for the user associated with the email
	 * specified in <code>email</code>  
	 * @param resetToken
	 * @param email
	 * @param password
	 */
	@Transactional(readOnly = false)
	public void resetPassword(String resetToken, String email, String password) {
		Account account = accountDao.findByEmail(email);
		if (account == null) {
			throw new NotFoundException(String.format("Account '%s' does not exist", email));
		}

		// Reset the password
		switch (authenticationScheme) {
		case CROWDMAPID:
			boolean success = crowdmapIDClient.setPassword(
					resetToken, email, password);
			if (!success) {
				throw new BadRequestException("Password reset failed");
			}
			break;

		default:
			if (!isValidToken(resetToken, account.getOwner())) {
				throw new BadRequestException(String.format(
						"Invalid token specified - '%s'", resetToken));
			}
			User user = account.getOwner();
			user.setPassword(passwordEncoder.encode(password));
			userDao.update(user);
			
			// Delete the token
			userTokenDao.delete(resetToken);
		}

	}

	/**
	 * Creates and sends a password reset token to the user
	 * with the email address specified in <code>email</code>
	 * 
	 * @param email
	 */
	public void forgotPassword(String email) {
		Account account = accountDao.findByEmail(email);
		if (account == null) {
			throw new NotFoundException(String.format(
					"'%s' does not belong to a registered account", email));
		}
		
		// Check the configured authentication scheme
		User user = account.getOwner();
		switch(authenticationScheme) {
		case CROWDMAPID:
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("email", email);
			params.put("token", "%token%");
			String mailBody = emailHelper.getEmailBody(
					EmailType.RESET_PASSWORD, params, user.getName());
			crowdmapIDClient.requestPassword(email, mailBody);
			break;
			
		case DEFAULT:
			UserToken userToken = createUserToken(user);
			emailHelper.sendPasswordResetEmail(user, userToken);
		}
	}
	
}
