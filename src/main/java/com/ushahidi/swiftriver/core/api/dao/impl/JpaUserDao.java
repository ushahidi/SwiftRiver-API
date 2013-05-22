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

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.UserDao;
import com.ushahidi.swiftriver.core.model.User;

@Repository
public class JpaUserDao extends AbstractJpaDao<User> implements UserDao {
	
	final Logger logger = LoggerFactory.getLogger(JpaUserDao.class);
	
	@Override
	public User create(User user) {
		// Set the date created
		user.setDateCreated(new Date());
		
		return super.create(user);
	}

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

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.UserDao#updateLastLogin(com.ushahidi.swiftriver.core.model.User)
	 */
	public void updateLastLogin(User user) {
		user.setLastLoginDate(new Date());
		this.update(user);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.UserDao#findByUserameOrEmail(java.lang.String)
	 */
	public User findByUsernameOrEmail(String search) {
		String qlString = "FROM User u WHERE email = :email OR username = :username";
		TypedQuery<User> query = em.createQuery(qlString, User.class);
		query.setParameter("email", search);
		query.setParameter("username", search);

		User user = null;
		try {
			user = query.getSingleResult(); 
		} catch (NoResultException e) {
			logger.info("No user with email {} or username {} exists", search, search);
		}

		return user;
	}

}
