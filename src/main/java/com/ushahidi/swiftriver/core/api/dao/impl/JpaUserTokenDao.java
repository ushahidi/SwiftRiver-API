package com.ushahidi.swiftriver.core.api.dao.impl;

import javax.persistence.NoResultException;

import com.ushahidi.swiftriver.core.api.dao.UserTokenDao;
import com.ushahidi.swiftriver.core.model.UserToken;

public class JpaUserTokenDao extends AbstractJpaDao<UserToken> implements
		UserTokenDao {

	@Override
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

}
