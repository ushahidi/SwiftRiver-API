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
package com.ushahidi.swiftriver.core.api.dao;

import java.io.Serializable;

/**
 * Generic DAO interface for all DAO Objects
 * @author ekala
 *
 */
public interface JpaDao <T, ID extends Serializable> {

	/**
	 * Creates a new entity
	 * @param entity
	 * @return TODO
	 */
	public T create(T entity);
	
	/**
	 * Modifies an existing entity
	 * @param entity
	 * @return TODO
	 */
	public T update(T entity);
	
	/**
	 * Deletes an existing entity
	 * @param entity
	 */
	public void delete(T entity);
	
	/**
	 * Retrives an entity using its unique database ID
	 * @param id
	 * @return
	 */
	public T findById(ID id);

	/**
	 * Allocates a range of IDs - specified by  @parm increment - for a given 
	 * sequence and returns the last ID (of the sequence) before the allocation. 
	 *    
	 * @param sequenceName
	 * @param increment
	 * @return
	 */
	
	public Long getSequenceNumber(String sequenceName, int increment);
}
