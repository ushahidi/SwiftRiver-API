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
package com.ushahidi.swiftriver.dao.hibernate;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.dao.SwiftRiverDAO;

/**
 * Base DAO implementation class for all DAO implementations
 * @author ekala
 *
 * @param <T>
 * @param <ID>
 */
public abstract class AbstractHibernateDAO<T, ID extends Serializable> implements SwiftRiverDAO<T, ID> {
	
	protected HibernateTemplate hibernateTemplate;
	
	private Class<T> entityClass;
	
	public AbstractHibernateDAO(Class<T> clazz) {
		this.entityClass = clazz;
	}

	@Autowired
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}

	@Transactional(readOnly = false)
	public void create(T entity) {
		hibernateTemplate.save(entity);
	}

	@Transactional(readOnly = false)
	public void update(T entity) {
		hibernateTemplate.update(entity);
	}

	@Transactional(readOnly = false)
	public void delete(T entity) {
		hibernateTemplate.delete(entity);
	}
	
	public T getById(ID id) {
		return hibernateTemplate.get(entityClass, id);
	}
	
	 
}
