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
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.model.Account;

@Transactional(readOnly = true)
@Service
public class AccountService {

	@Autowired
	private AccountDao accountDao;

	public AccountDao getAccountDao() {
		return accountDao;
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	/**
	 * Get an account using its id
	 * 
	 * @param id
	 * @return
	 */
	public Map<String, Object> getAccount(long id) {
		Account account = accountDao.findById(id);

		return getAccountMap(account);
	}

	/**
	 * Get an account by username
	 * 
	 * @param username
	 * @return
	 */
	public Map<String, Object> getAccount(String username) {
		Account account = accountDao.findByUsername(username);
		return getAccountMap(account);
	}

	public Map<String, Object> getAccountMap(Account account) {
		Object[][] accountData = { { "id", account.getId() },
				{ "account_path", account.getAccountPath() },
				{ "active", account.isActive() },
				{ "public", !account.isAccountPrivate() },
				{ "date_added", account.getDateAdded() },
				{ "river_quota_remaining", account.getRiverQuotaRemaining() },
				{ "follower_count", 0 }, { "following_count", 0 },
				{ "is_owner", false }, { "is_collaborator", false },
				{ "is_following", false }, { "rivers", new ArrayList() },
				{ "buckets", new ArrayList() } };

		Object[][] ownerData = { { "name", account.getOwner().getName() },
				{ "email", account.getOwner().getEmail() },
				{ "username", account.getOwner().getUsername() },
				{ "date_added", account.getOwner().getCreatedDate() }, };

		Map<String, Object> accountMap = ArrayUtils.toMap(accountData);
		Map<String, Object> ownerMap = ArrayUtils.toMap(ownerData);
		accountMap.put("owner", ownerMap);

		return accountMap;
	}
}
