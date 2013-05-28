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

import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.UserTokenDao;
import com.ushahidi.swiftriver.core.model.UserToken;

@Repository
public class JpaUserTokenDao extends AbstractJpaDao<UserToken> implements
		UserTokenDao {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.UserTokenDao#findByToken(java.lang
	 * .String)
	 */
	public UserToken findByToken(String token) {
		String query = "SELECT u FROM UserToken u WHERE u.token = :token";

		UserToken userToken = null;
		try {
			userToken = (UserToken) em.createQuery(query)
					.setParameter("token", token).getSingleResult();
		} catch (NoResultException e) {
			// Do nothing
		}
		return userToken;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.UserTokenDao#delete(java.lang.String
	 * )
	 */
	public void delete(String token) {
		em.createQuery("DELETE FROM UserToken WHERE token = :token")
			.setParameter("token", token)
			.executeUpdate();
	}

}
