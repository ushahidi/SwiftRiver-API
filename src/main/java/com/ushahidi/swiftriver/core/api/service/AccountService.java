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
import java.util.List;
import java.util.UUID;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.UserDao;
import com.ushahidi.swiftriver.core.api.dao.UserTokenDao;
import com.ushahidi.swiftriver.core.api.dto.CreateAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.GetAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyAccountDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ErrorField;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.User;
import com.ushahidi.swiftriver.core.model.UserToken;
import com.ushahidi.swiftriver.core.util.ErrorUtil;

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
	private PasswordEncoder passwordEncoder;

	@Autowired
	private Mapper mapper;

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
}
