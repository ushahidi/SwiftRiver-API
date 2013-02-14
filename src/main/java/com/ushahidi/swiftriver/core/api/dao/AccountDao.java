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

public interface AccountDao {
	
	/**
	 * Modify an account
	 * 
	 * @param river
	 */
	public Account update(Account account);
	
	/**
	 * Create an account
	 * 
	 * @param river
	 * @return
	 */
	public Account save(Account account);

	/**
	 * Get an account by its ID
	 * 
	 * @param id
	 * @return
	 */
	public Account findById(long id);
	
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
	public Account findByName(String accountPath);
	
	/**
	 * Reduce an account's quota by the given decrement
	 * 
	 * @param account
	 * @param decrement
	 */
	public void decreaseRiverQuota(Account account, int decrement);
}
