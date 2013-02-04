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

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.JpaDao;

/**
 * Base DAO implementation class for all DAO implementations
 * @author ekala
 *
 * @param <T>
 * @param <ID>
 */
public abstract class AbstractJpaDao<T, ID extends Serializable> implements JpaDao<T, ID> {
	
	protected EntityManager entityManager;
	
	private Class<T> entityClass;
	
	public AbstractJpaDao(Class<T> clazz) {
		this.entityClass = clazz;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	@Transactional(readOnly = false)
	public T create(T entity) {
		entityManager.persist(entity);
		return entity;
	}

	@Transactional(readOnly = false)
	public T update(T entity) {
		return entityManager.merge(entity);
	}

	@Transactional(readOnly = false)
	public void delete(T entity) {
		entityManager.remove(entity);
	}
	
	public T findById(ID id) {
		return (T) entityManager.find(entityClass, id);
	}

	/**
	 * @see JpaDao#getSequenceNumber(String, int)
	 */
	public Long getSequenceNumber(String sequenceName, int increment) {
		String sql = "SELECT NEXTVAL(:sequenceName, :increment)";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("sequenceName", sequenceName);
		query.setParameter("increment", increment);

		Integer epochSequenceNo =  (Integer) query.getSingleResult();
		
		return new Long(epochSequenceNo.toString());
	}
		 
}
