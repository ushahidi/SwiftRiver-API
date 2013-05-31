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

import java.util.List;

import com.ushahidi.swiftriver.core.model.Drop;

public interface DropDao extends GenericDao<Drop> {
	
	/**
	 * Add new drops
	 * 
	 * @param drops
	 * @return
	 */
	public List<Drop> createDrops(List<Drop> drops);

	/**
	 * Gets and returns the {@link List} of all {@link Drop} entities 
	 * with the IDs specified in <code>dropIds</code>
	 * 
	 * @param dropIds
	 * @return
	 */
	public List<Drop> findAll(List<Long> dropIds);

	/**
	 * Returns <code>batchSize</code> {@link Drop} entities with an 
	 * ID greater than the value specified in <code>sinceId</code>
	 *  
	 * @param sinceId
	 * @param batchSize
	 * @return
	 */
	public List<Drop> findAll(long sinceId, int batchSize);

}
