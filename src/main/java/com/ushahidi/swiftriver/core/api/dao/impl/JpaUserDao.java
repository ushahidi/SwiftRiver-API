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

import javax.persistence.NoResultException;

import com.ushahidi.swiftriver.core.api.dao.UserDao;
import com.ushahidi.swiftriver.core.model.User;

public class JpaUserDao extends AbstractJpaDao<User> implements UserDao {

	/* (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.UserDao#findByUsername(java.lang.String)
	 */
	@Override
	public User findByUsername(String username) {
		String query = "SELECT u FROM User u WHERE u.username = :username";

		User user = null;
		try {
			user = (User) em.createQuery(query)
					.setParameter("username", username).getSingleResult();
		} catch (NoResultException e) {
			// Do nothing
		}
		return user;
	}

}
