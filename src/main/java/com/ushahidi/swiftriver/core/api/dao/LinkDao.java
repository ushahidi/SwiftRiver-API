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

import java.util.ArrayList;
import java.util.List;

import com.ushahidi.swiftriver.core.model.Link;

public interface LinkDao  {
	
	/**
	 * Gets and returns the {@link Link} record with the specified
	 * <code>id</code>
	 * 
	 * @param id
	 * @return {@Link} on success, null otherwise
	 */
	public Link findById(long id);

	/**
	 * Gets and returns a list of {@link Link} entities with the
	 * specified hash values
	 * 
	 * @param linkHashes
	 */
	public List<Link> findAllByHash(ArrayList<String> linkHashes);
	
	/**
	 * Gets the {@Link} record with the specified <code>hash</code>
	 * @param hash
	 * @return
	 */
	public Link findByHash(String hash);

	/**
	 * Creates a new {@link Link} record in the database
	 * 
	 * @param link
	 */
	public void save(Link link);
}
