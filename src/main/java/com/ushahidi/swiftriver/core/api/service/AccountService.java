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
import com.ushahidi.swiftriver.core.api.dto.GetAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.GetClientDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyAccountDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ErrorField;
import com.ushahidi.swiftriver.core.api.exception.ForbiddenException;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
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
	public GetAccountDTO getAccountById(Long id) throws NotFoundException {
		Account account = accountDao.findById(id);

		if (account == null) {
			throw new NotFoundException(String.format("Account not found", id));
		}

		return mapGetAccountDTO(account);
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

		return mapGetAccountDTO(account);
	}

	/**
	 * Get an account by account_path
	 * 
	 * @param username
	 * @return
	 * @throws NotFoundException
	 */
	public GetAccountDTO getAccountByName(String accountPath, boolean getToken)
			throws NotFoundException {
		Account account = accountDao.findByName(accountPath);

		if (account == null) {
			throw new NotFoundException("Account not found");
		}

		GetAccountDTO getAccountDTO = mapGetAccountDTO(account);
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
	public GetAccountDTO getAccountByEmail(String email, boolean getToken)
			throws NotFoundException {
		Account account = accountDao.findByEmail(email);

		if (account == null) {
			throw new NotFoundException("Account not found");
		}

		GetAccountDTO getAccountDTO = mapGetAccountDTO(account);
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
	public List<GetAccountDTO> searchAccounts(String query)
			throws NotFoundException {
		List<Account> accounts = accountDao.search(query);

		if (accounts == null) {
			throw new NotFoundException("No accounts found");
		}

		List<GetAccountDTO> getAccountTOs = new ArrayList<GetAccountDTO>();
		for (Account account : accounts) {
			getAccountTOs.add(mapGetAccountDTO(account));
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
		if (accountDao.findByName(createAccountTO.getAccountPath()) != null) {
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
	 * @return
	 */
	@Transactional(readOnly = false)
	public GetAccountDTO modifyAccount(Long accountId,
			ModifyAccountDTO modifyAccountTO) {

		Account account = accountDao.findById(accountId);

		if (account == null)
			throw new NotFoundException("Account not found.");

		if (modifyAccountTO.getAccountPath() != null) {
			if (accountDao.findByName(modifyAccountTO.getAccountPath()) != null) {
				throw ErrorUtil.getBadRequestException("account_path",
						"duplicate");
			}
		}

		if (modifyAccountTO.getOwner() != null
				&& modifyAccountTO.getOwner().getEmail() != null) {
			String email = modifyAccountTO.getOwner().getEmail();
			if (accountDao.findByEmail(email) != null) {
				throw ErrorUtil.getBadRequestException("owner.email",
						"duplicate");
			}
			account.getOwner().setUsername(email);
		}

		if (modifyAccountTO.getOwner() != null
				&& modifyAccountTO.getOwner().getPassword() != null
				&& account.getOwner().getActive()) {

			if (modifyAccountTO.getToken() == null)
				throw ErrorUtil.getBadRequestException("token", "missing");

			if (!isTokenValid(modifyAccountTO.getToken(), account.getOwner()))
				throw ErrorUtil.getBadRequestException("token", "invalid");

			String password = passwordEncoder.encode(modifyAccountTO.getOwner()
					.getPassword());
			modifyAccountTO.getOwner().setPassword(password);
		}

		if (modifyAccountTO.getToken() != null
				&& !account.getOwner().getActive()) {
			// Activate account is matching user token found
			if (!isTokenValid(modifyAccountTO.getToken(), account.getOwner()))
				throw ErrorUtil.getBadRequestException("token", "invalid");

			account.getOwner().setActive(true);
		}

		mapper.map(modifyAccountTO, account);
		accountDao.update(account);

		return mapper.map(account, GetAccountDTO.class);
	}

	/**
	 * Convert the given account into a GetAccountDTO
	 * 
	 * @param account
	 * @return
	 */
	public GetAccountDTO mapGetAccountDTO(Account account) {
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

			// Decrypt client secret
			TextEncryptor encryptor = Encryptors.text(
					TextUtil.convertStringToHex(key),
					TextUtil.convertStringToHex(client.getClientId()));
			GetClientDTO dto = mapper.map(client, GetClientDTO.class);
			dto.setClientSecret(encryptor.decrypt(client.getClientSecret()));

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
		String clientId = UUID.randomUUID().toString();
		String secret = UUID.randomUUID().toString();

		// Encrypt the secret
		TextEncryptor encryptor = Encryptors.text(
				TextUtil.convertStringToHex(key),
				TextUtil.convertStringToHex(clientId));
		client.setClientSecret(encryptor.encrypt(secret));
		client.setClientId(clientId);

		client.setAccount(account);
		client.setRoles(new HashSet<Role>());
		client.getRoles().add(roleDao.findByName("client"));
		clientDao.create(client);

		GetClientDTO dto = mapper.map(client, GetClientDTO.class);
		dto.setClientSecret(secret);
		return dto;
	}

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
}
