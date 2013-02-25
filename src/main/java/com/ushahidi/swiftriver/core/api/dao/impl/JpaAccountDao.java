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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.AccountFollower;

@Repository
public class JpaAccountDao extends AbstractJpaDao<Account> implements AccountDao {

	@Autowired
	private BucketDao bucketDao;
	
	@Autowired
	private RiverDao riverDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.data.dao.AccountDao#findByUsername(java.
	 * lang.String)
	 */
	public Account findByUsername(String username) {
		String query = "SELECT a FROM Account a JOIN a.owner o WHERE o.username = :username";
		
		Account account = null;
		try {
			account = (Account) em.createQuery(query)
					.setParameter("username", username).getSingleResult();
		} catch (NoResultException e) {
			// Do nothing
		}
		return account;
	}

	/* (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.AccountDao#findByName(java.lang.String)
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
	
	/* (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.AccountDao#decreaseRiverQuota(com.ushahidi.swiftriver.core.model.Account, int)
	 */
	public void decreaseRiverQuota(Account account, int decrement) {
		account.setRiverQuotaRemaining(account.getRiverQuotaRemaining() - decrement);
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
	 * @see com.ushahidi.swiftriver.core.api.dao.AccountDao#populateAssets(com.ushahidi.swiftriver.core.model.Account, com.ushahidi.swiftriver.core.model.Account)
	 */
	public void populateAssets(Account account, Account queryingAccount) {
		populateRivers(account, queryingAccount);
		populateBuckets(account, queryingAccount);
		
	}

	@SuppressWarnings("unchecked")
	private void populateBuckets(Account account, Account queryingAccount) {
		// Query to fetch the bucket ids
		String sql = "SELECT `buckets`.`id` AS `id` ";
		sql += "FROM `buckets` " ;
		sql += "WHERE `bucket_publish` = 1 ";
		sql += "AND `account_id` = :account_id ";
		sql += "UNION ALL ";
		sql += "SELECT `bucket_collaborators`.`bucket_id` AS id ";
		sql += "FROM `bucket_collaborators` ";
		sql += "INNER JOIN `buckets` ON (`bucket_collaborators`.`bucket_id` = `buckets`.`id`) ";
		sql += "WHERE buckets.`bucket_publish` = 0 ";
		sql += "AND buckets.account_id = :account_id ";
		sql += "AND `bucket_collaborators`.`account_id` = :collaborator_account_id ";
		
		Query query = em.createNativeQuery(sql);
		query.setParameter("account_id", account.getId());
		query.setParameter("collaborator_account_id", queryingAccount.getId());
		
		List<Long> bucketIds = new ArrayList<Long>();
		for (Object row: query.getResultList()) {
			Long bucketId = ((BigInteger) row).longValue();
			bucketIds.add(bucketId);
		}
		
		if (!bucketIds.isEmpty()) {
			account.setBuckets(bucketDao.findAll(bucketIds));
		}
	}

	private void populateRivers(Account account, Account queryingAccount) {
		// Query to fetch the river ids
		String sql = "SELECT `rivers`.`id` AS `id` ";
		sql += "FROM `rivers` " ;
		sql += "WHERE `river_public` = 1 ";
		sql += "AND `account_id` = :account_id ";
		sql += "UNION ALL ";
		sql += "SELECT `river_collaborators`.`river_id` AS `id` ";
		sql += "FROM `river_collaborators` ";
		sql += "INNER JOIN `rivers` ON (`river_collaborators`.`river_id` = `rivers`.`id`) ";
		sql += "WHERE rivers.`river_public` = 0 ";
		sql += "AND rivers.account_id = :account_id ";
		sql += "AND `river_collaborators`.`account_id` = :collaborator_account_id ";
		
		Query query = em.createNativeQuery(sql);
		query.setParameter("account_id", account.getId());
		query.setParameter("collaborator_account_id", queryingAccount.getId());
		
		List<Long> riverIds = new ArrayList<Long>();
		for (Object row: query.getResultList()) {
			Long riverId = ((BigInteger) row).longValue();
			riverIds.add(riverId);
		}
		
		if (!riverIds.isEmpty()) {
			account.setRivers(riverDao.findAll(riverIds));
		}
	}
}
