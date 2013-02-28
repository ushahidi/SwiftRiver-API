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
import java.util.List;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dto.FollowerDTO;
import com.ushahidi.swiftriver.core.api.dto.GetAccountDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.AccountFollower;

@Transactional(readOnly = true)
@Service
public class AccountService {

	final Logger logger = LoggerFactory.getLogger(AccountService.class);

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private Mapper mapper;

	public AccountDao getAccountDao() {
		return accountDao;
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
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
	public GetAccountDTO getAccountById(Long id, String queryingUsername) throws NotFoundException {
		Account account = getAccount(id);
		
		Account queryingAccount = accountDao.findByUsername(queryingUsername);
		accountDao.populateAssets(account, queryingAccount);
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
	 * @param accountPath
	 * @return
	 * @throws NotFoundException
	 */
	public GetAccountDTO getAccountByAccountPath(String accountPath, 
			String queryingUsername)  throws NotFoundException {
		Account account = accountDao.findByAccountPath(accountPath);
		
		if (account == null) {
			throw new NotFoundException("Account not found");
		}
		
		Account queryingAccount = accountDao.findByUsername(queryingUsername);
		accountDao.populateAssets(account, queryingAccount);
		return mapGetAccountDTO(account);
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
	 * Gets and returns the list of {@link Account} entities that are
	 * following the {@Account} specified in <code>id</code>. <code>accountId</code>
	 * is an optional parameter and when specified, verifies whether the {@link Account}
	 * with that id (<code>accountId</code>) is a follower
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
			for (AccountFollower accountFollower: account.getFollowers()) {
				followers.add(mapFollowerDTO(accountFollower.getFollower()));
			}
		} else {
			Account follower = accountDao.getFollower(account, accountId);
			if (follower == null) {
				throw new NotFoundException(String.format("Account %d does not follow %d",
						accountId, id));
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
	 * Adds the {@link Account} with the specified <code>accountId</code> to
	 * the list of followers for the {@link Account} specified in <code>id</code>
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
			throw new BadRequestException(String.format("Account %d is already following account %d",
					accountId, id));
		}
		
		Account follower = getAccount(accountId);
		accountDao.addFollower(account, follower);
	}

	/**
	 * Internal helper method for retrieving a {@link Account} entity
	 * using its  unique database ID
	 * 
	 * @param accountId
	 * @return
	 */
	private Account getAccount(Long accountId) {
		Account account = accountDao.findById(accountId);
		if (account == null) {
			throw new NotFoundException(String.format("Account %d does not exist", accountId));
		}
		
		return account;
	}

	/**
	 * Deletes the {@link Account} specified in <code>accountId</code> from
	 * the list of followers for the {@link Account} specified in <code>id</code>
	 * 
	 * @param id
	 * @param accountId
	 */
	@Transactional
	public void deleteFollower(Long id, Long accountId) {
		Account account = getAccount(id);
		if (!accountDao.deleteFollower(account, getAccount(accountId))) {
			throw new NotFoundException(String.format("Account %d does not follow account %d",
					accountId, id));
		}
	}

}
