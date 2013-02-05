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
public abstract class EntityDTO<T> {	

	/**
	 * Given an entity, creates and returns a Map representation
	 *  
	 * @param entity
	 * @return
	 */
	public abstract Map<String, Object> createDTO(T entity);	
	
	
	/**
	 * Given a Map<String, Object>, creates an entity of type T
	 * This convenience method should be used when creating a
	 * new entity from a DTO
	 * 
	 * @param entityDTO
	 * @return
	 */
	public abstract T createModel(Map<String, Object> entityDTO);
	

}
