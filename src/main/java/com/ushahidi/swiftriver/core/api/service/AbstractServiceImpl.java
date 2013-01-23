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
package com.ushahidi.swiftriver.core.api.service;

import java.io.Serializable;

import com.ushahidi.swiftriver.core.api.dao.SwiftRiverDao;

/**
 * Base class for all SwiftRiver service classes.
 * @author ekala
 *
 * @param <T>
 * @param <ID>
 */
public abstract class AbstractServiceImpl<T, ID extends Serializable> {

	/**
	 * Gets the DAO interface to be used for database operations. This
	 * method MUST be implemented by all classes that extend this class.
	 * @return
	 */
	public abstract SwiftRiverDao<T, ID> getServiceDAO();
	
	/**
	 * @see SwiftRiverDao#create(Object)
	 */
	public void create(T entity) {
		getServiceDAO().create(entity);
	}

	/**
	 * @see SwiftRiverDao#update(Object)
	 */
	public void update(T entity) {
		getServiceDAO().update(entity);
	}

	/**
	 * @see SwiftRiverDao#delete(Object)
	 */
	public void delete(T entity) {
		getServiceDAO().delete(entity);
	}
	
}
