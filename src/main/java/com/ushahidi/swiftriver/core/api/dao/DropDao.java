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
import java.util.Collection;
import java.util.List;

import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.DropSource;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Media;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.Tag;

public interface DropDao extends GenericDao<Drop> {
	
	/**
	 * Adds a collection of links to a drop
	 * 
	 * @param dropId
	 * @param links
	 */
	public void addLinks(long dropId, Collection<Link> links);
	
	/**
	 * Adds a collection of places to a drop
	 * @param dropId
	 * @param places
	 */
	public void addPlaces(long dropId, Collection<Place> places);
	
	/**
	 * Adds a collection of media to a drop
	 * @param dropId
	 * @param media
	 */
	public void addMultipleMedia(long dropId, Collection<Media> media);
		
	/**
	 * Adds a collection of tags to the drop
	 * @param dropId
	 * @param tags
	 */
	public void addTags(long dropId, Collection<Tag> tags);
	
	/**
	 * Returns all drops with a hash in @param dropHashes
	 *  
	 * @param dropHashes Hashes of drops
	 * @return
	 */
	public List<Drop> findDropsByHash(ArrayList<String> dropHashes);
	
	
	/**
	 * Populate the metadata into the drops in the given array.
	 * 
	 * @param drops
	 * @param dropSource
	 * @param queryingAccount
	 * @return
	 */
	public void populateMetadata(List<Drop> drops, DropSource dropSource, Account queryingAccount);
}
