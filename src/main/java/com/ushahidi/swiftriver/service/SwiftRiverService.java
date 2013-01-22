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
package com.ushahidi.swiftriver.service;

import java.io.Serializable;
import java.util.Map;

/**
 * Base interface for all SwiftRiver service interfaces. This interface
 * defines the signatures for the methods used for CRUD operations
 *  
 * @author ekala
 *
 * @param <T>
 * @param <ID>
 */
public interface SwiftRiverService<T, ID extends Serializable> {
	
	/**
	 * Creates a new entity in the database
	 * 
	 * @param entity
	 */
	public void create(T entity);
	
	/**
	 * Updates an existing entity
	 * 
	 * @param entity
	 */
	public void update(T entity);
	
	/**
	 * Deletes an entity from the database
	 * 
	 * @param entity
	 */
	public void delete(T entity);
	
}
