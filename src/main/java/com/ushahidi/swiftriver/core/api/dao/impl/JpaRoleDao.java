package com.ushahidi.swiftriver.core.api.dao.impl;

import javax.persistence.NoResultException;

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.RoleDao;
import com.ushahidi.swiftriver.core.model.Role;

@Repository
public class JpaRoleDao extends AbstractJpaDao<Role> implements RoleDao {

	@Override
	public Role findByName(String name) {
		String query = "SELECT r FROM Role r WHERE r.name = :name";

		Role role = null;
		try {
			role = (Role) em.createQuery(query)
					.setParameter("name", name).getSingleResult();
		} catch (NoResultException e) {
			// Do nothing
		}
		return role;
	}

	
}
