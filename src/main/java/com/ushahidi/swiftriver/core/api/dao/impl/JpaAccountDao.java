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
package com.ushahidi.swiftriver.core.api.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.model.Account;

@Repository
public class JpaAccountDao extends AbstractJpaDao implements AccountDao {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ushahidi.swiftriver.core.data.dao.AccountDao#findById(long)
	 */
	public Account findById(long id) {
		Account account = em.find(Account.class, id);
		return account;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.data.dao.AccountDao#findByUsername(java.
	 * lang.String)
	 */
	public Account findByUsername(String username) {
		String query = "SELECT a FROM Account a JOIN a.owner o WHERE o.username = :username";
		return (Account) em.createQuery(query)
				.setParameter("username", username)
				.getSingleResult();
	}

	/* (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.AccountDao#findByName(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Account findByName(String accountPath) {
		String sql = "SELECT a FROM Account a WHERE a.accountPath = :account_path";
		Query query = em.createQuery(sql);
		query.setParameter("account_path", accountPath);
		List<Account> accounts = (List<Account>) query.getResultList();
		
		return accounts.isEmpty() ? null : accounts.get(0);
	}
}
