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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.util.DateUtil;

@Transactional(readOnly = true)
@Service
public class AccountService {

	final Logger logger = LoggerFactory.getLogger(AccountService.class);

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

		Object[][] accountData = {
				{ "id", account.getId() },
				{ "account_path", account.getAccountPath() },
				{ "active", account.isActive() },
				{ "public", !account.isAccountPrivate() },
				{ "date_added", DateUtil.formatRFC822(account.getDateAdded()) },
				{ "river_quota_remaining", account.getRiverQuotaRemaining() },
				{ "follower_count", account.getFollowers().size() },
				{ "following_count", account.getFollowing().size() },
				{ "is_owner", true }, { "is_collaborator", false },
				{ "is_following", false } };

		// Populate owner data
		Object[][] ownerData = {
				{ "name", account.getOwner().getName() },
				{ "email", account.getOwner().getEmail() },
				{ "username", account.getOwner().getUsername() },
				{
						"date_added",
						DateUtil.formatRFC822(account.getOwner()
								.getCreatedDate()) }, };

		Map<String, Object> accountMap = ArrayUtils.toMap(accountData);
		Map<String, Object> ownerMap = ArrayUtils.toMap(ownerData);
		accountMap.put("owner", ownerMap);

		// Populate Rivers
		List<Map<String, Object>> riverList = new ArrayList<Map<String, Object>>();
		if (account.getRivers() != null) {
			for (River river : account.getRivers()) {
				riverList.add(RiverService.getRiverMap(river, account));
			}
		}

		if (account.getCollaboratingRivers() != null) {
			for (River river : account.getCollaboratingRivers()) {
				riverList.add(RiverService.getRiverMap(river, account));
			}
		}

		if (account.getFollowingRivers() != null) {
			for (River river : account.getFollowingRivers()) {
				riverList.add(RiverService.getRiverMap(river, account));
			}
		}
		accountMap.put("rivers", riverList);

		// Populate Buckets
		List<Map<String, Object>> bucketList = new ArrayList<Map<String, Object>>();

		if (account.getBuckets() != null) {
			for (Bucket bucket : account.getBuckets()) {
				bucketList.add(BucketService.getBucketMap(bucket, account));
			}
		}

		if (account.getCollaboratingBuckets() != null) {
			for (Bucket bucket : account.getCollaboratingBuckets()) {
				bucketList.add(BucketService.getBucketMap(bucket, account));
			}
		}

		if (account.getFollowingBuckets() != null) {
			for (Bucket bucket : account.getFollowingBuckets()) {
				bucketList.add(BucketService.getBucketMap(bucket, account));
			}
		}
		accountMap.put("buckets", bucketList);

		return accountMap;
	}
}
