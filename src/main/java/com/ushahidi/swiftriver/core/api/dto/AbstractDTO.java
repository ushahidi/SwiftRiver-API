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
package com.ushahidi.swiftriver.core.api.dto;

import java.util.Map;

/**
 * Base class for mapping domain objects to DTOs (Data Transfer Objects).
 * The DTOs are used to pass data between the services and entities
 * 
 * @author ekala
 *
 */
public abstract class AbstractDTO<T> {	

	/**
	 * Given an entity, creates and returns a Map representation
	 *  
	 * @param entity
	 * @return
	 */
	public abstract Map<String, Object> createMapFromEntity(T entity);	
	
	
	/**
	 * Given a Map<String, Object>, creates an entity of type T
	 * This convenience method should be used when creating a
	 * new entity from a DTO
	 * 
	 * @param map
	 * @return
	 */
	public abstract T createEntityFromMap(Map<String, Object> map);
	
	
	/**
	 * Gets and returns the set of keys for validating a map
	 * before it can be used to update an entity
	 * 
	 * @return
	 */
	protected abstract String[] getValidationKeys();

	/**
	 * Verifies whether the entity represented by this DTO can be
	 * updated from the supplied {@link Map} object
	 * 
	 * @param map
	 * @return
	 */
	protected boolean isEntityUpdatableFromMap(Map<String, Object> map) {
		for (String key: getValidationKeys()) {
			if (!map.containsKey(key)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Updates the properties of the target entity from the supplied
	 * {@link Map} object.
	 * 
	 * @param target
	 * @param source
	 */
	public boolean updateEntityFromMap(T target, Map<String, Object> source) {
		if (!isEntityUpdatableFromMap(source))
				return false;
		
		copyFromMap(target, source);

		return true;
	}

	
	/**
	 * Copies the properties from the source {@link Map} to the 
	 * target entity reference
	 * 
	 * @param target
	 * @param source
	 */
	protected abstract void copyFromMap(T target, Map<String, Object> source);

}
