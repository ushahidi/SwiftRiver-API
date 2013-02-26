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
package com.ushahidi.swiftriver.core.api.dao;

import com.ushahidi.swiftriver.core.model.Account;

public interface AccountDao extends GenericDao<Account> {
		
	/**
	 * Get an account by its username
	 * 
	 * @param username
	 * @return
	 */
	public Account findByUsername(String username);
	
	/**
	 * Get an account by its account path
	 * 
	 * @param accountPath
	 * @return
	 */
	public Account findByAccountPath(String accountPath);
	
	/**
	 * Reduce an account's quota by the given decrement
	 * 
	 * @param account
	 * @param decrement
	 */
	public void decreaseRiverQuota(Account account, int decrement);

	/**
	 * Gets the {@link Account} record with the id in <code>accountId</code>
	 * from the list of followers from the {@link Account} specified in
	 * <code>account</code>
	 * 
	 * @param account
	 * @param accountId
	 * @return {@link Account}
	 */
	public Account getFollower(Account account, Long accountId);
	
	/**
	 * Adds the {@link Account} specified in <code>follower</code>
	 * to the list of followers for the {@link Account} specified
	 * in <code>account</code>
	 * 
	 * @param account
	 * @param follower
	 */
	public void addFollower(Account account, Account follower);

	/**
	 * Removes the {@link Account} specified in <code>follower</code>
	 * from the list of followers for {@link Account} specified in
	 * <code>account</code>
	 * 
	 * @param account
	 * @param follower
	 */
	public boolean deleteFollower(Account account, Account follower);

	/**
	 * Populates the list of assets for the {@link Account} specified in
	 * <code>targetAccount</code> that the {@link Account} specified in
	 * <code>queryingAccount</code> has access to.
	 * 
	 * @param targetAccount
	 * @param queryingAccount
	 */
	public void populateAssets(Account targetAccount, Account queryingAccount);
}
