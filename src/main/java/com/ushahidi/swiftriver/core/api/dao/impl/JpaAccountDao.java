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
package com.ushahidi.swiftriver.core.api.dao.impl;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.AccountFollower;

@Repository
public class JpaAccountDao extends AbstractJpaDao<Account> implements AccountDao {

	private final Logger logger = LoggerFactory.getLogger(JpaAccountDao.class);


	@Override
	public Account update(Account t) {
		t.setDateModified(new Date());
		return super.update(t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.data.dao.AccountDao#findByUsername(java.
	 * lang.String)
	 */
	public Account findByUsernameOrEmail(String username) {
		String qlString = "SELECT a FROM Account a JOIN a.owner o " +
				"WHERE o.username = :username OR o.email = :email";

		Account account = null;
		try {
			TypedQuery<Account> query = em.createQuery(qlString, Account.class);
			query.setParameter("username", username);
			query.setParameter("email", username);
			account = query.getSingleResult();
		} catch (NoResultException e) {
			logger.info("Account associated with {} not found", username);
		}
		return account;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.AccountDao#findByName(java.lang.
	 * String)
	 */
	public Account findByAccountPath(String accountPath) {
		String query = "SELECT a FROM Account a WHERE a.accountPath = :account_path";

		Account account = null;
		try {
			account = (Account) em.createQuery(query)
					.setParameter("account_path", accountPath)
					.getSingleResult();
		} catch (NoResultException e) {
			// Do nothing;
		}
		return account;
	}

	@Override
	public Account findByEmail(String email) {
		String query = "SELECT a FROM Account a WHERE a.owner.email = :email";

		Account account = null;
		try {
			account = (Account) em.createQuery(query)
					.setParameter("email", email)
					.getSingleResult();
		} catch (NoResultException e) {
			// Do nothing;
		}
		return account;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.AccountDao#decreaseRiverQuota(com
	 * .ushahidi.swiftriver.core.model.Account, int)
	 */
	public void decreaseRiverQuota(Account account, int decrement) {
		account.setRiverQuotaRemaining(account.getRiverQuotaRemaining()
				- decrement);
		update(account);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.AccountDao#getFollower(com.ushahidi.swiftriver.core.model.Account, java.lang.Long)
	 */
	@SuppressWarnings("unchecked")
	public Account getFollower(Account account, Long accountId) {
		String sql = "FROM AccountFollower WHERE account = :account AND follower.id = :follower";
		Query query = this.em.createQuery(sql);
		query.setParameter("account", account);
		query.setParameter("follower", accountId);
		
		List<AccountFollower> followers = (List<AccountFollower>) query.getResultList();
		
		return followers.isEmpty() ? null : followers.get(0).getFollower();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.AccountDao#addFollower(com.ushahidi.swiftriver.core.model.Account, com.ushahidi.swiftriver.core.model.Account)
	 */
	public void addFollower(Account account, Account follower) {
		AccountFollower accountFollower = new AccountFollower();

		accountFollower.setAccount(account);
		accountFollower.setFollower(follower);
		accountFollower.setDateAdded(new Date());
		
		this.em.persist(accountFollower);		
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.AccountDao#deleteFollower(com.ushahidi.swiftriver.core.model.Account, com.ushahidi.swiftriver.core.model.Account)
	 */
	public boolean deleteFollower(Account account, Account follower) {
		String sql = "DELETE FROM AccountFollower WHERE account = :account AND follower = :follower";

		Query query = em.createQuery(sql);
		query.setParameter("account", account);
		query.setParameter("follower", follower);
		
		return query.executeUpdate() == 1;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.AccountDao#search(java.lang.String)
	 */
	public List<Account> search(String query) {
		return getSearchResultList(query + "%", 10, 0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.AccountDao#search(java.lang.String, int, int)
	 */
	public List<Account> search(String searchTerm, int count, int page) {
		// Calculate the offset
		int offset = count * (page - 1);
		
		return getSearchResultList("%" + searchTerm + "%", count, offset);
	}
	
	
	private List<Account> getSearchResultList(String searchTerm, int count, int offset) {
		String qlString = "SELECT a FROM Account a WHERE a.accountPath like :q " +
				"OR a.owner.name like :q OR a.owner.email like :q";

		List<Account> accounts = null;
		try {
			TypedQuery<Account> query = em.createQuery(qlString, Account.class);
			query.setParameter("q", searchTerm);
			query.setMaxResults(count);
			query.setFirstResult(offset);

			accounts = query.getResultList();
		} catch (NoResultException e) {
			// Do nothing;
		}
		return accounts;
		
	}
}
