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
import com.ushahidi.swiftriver.core.model.Tag;

public interface TagDao extends GenericDao<Tag> {

	/**
	 * Populate tag IDs in the given list of drops while create any that are
	 * missing.
	 * 
	 * @param drops
	 */
	public void getTags(List<Drop> drops);

	/**
	 * Gets and returns the {@link Tag} record with the specified
	 * <code>hash</code>
	 * 
	 * @param hash
	 * @return
	 */
	public Tag findByHash(String hash);

}
