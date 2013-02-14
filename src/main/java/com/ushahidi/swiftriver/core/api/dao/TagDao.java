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

import com.ushahidi.swiftriver.core.model.Tag;

public interface TagDao  {
	
	/**
	 * Given a list of hashes, finds and returns all tags whose
	 * hash is contained in the list
	 * 
	 * @param tagHashes
	 * @return
	 */
	public List<Tag> findAllByHash(ArrayList<String> tagHashes);

	/**
	 * Gets and returns the {@link Tag} record with the specified
	 * <code>hash</code>
	 * @param hash
	 * @return
	 */
	public Tag findByHash(String hash);
	
	/**
	 * Creates a new {@link Tag} record in the database
	 * 
	 * @param tag
	 */
	public void save(Tag tag);

	/**
	 * Retrieves a {@link Tag} record using its unique <code>tagId</code>
	 *  
	 * @param tagId
	 * @return
	 */
	public Tag findById(long tagId);
}
