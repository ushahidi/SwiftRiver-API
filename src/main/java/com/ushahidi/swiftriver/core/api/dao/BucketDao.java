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

import java.util.Collection;

import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.User;

/**
 * Database operations for a bucket
 * 
 * @author ekala
 *
 */
public interface BucketDao extends SwiftRiverDao<Bucket, Long>{
		
	/**
	 * Gets a collection of drops from a bucket using the specified parameters
	 *  
	 * @param bucketId
	 * @param dropCount TODO
	 * @return
	 */
	public Collection<Drop> getDrops(Long bucketId, int dropCount);
	
	/**
	 * Adds a single drop to a bucket
	 * 
	 * @param bucketId
	 * @param drop
	 */
	public void addDrop(Long bucketId, Drop drop);
	
	/**
	 * Adds a collection of drops to a buckets
	 * 
	 * @param bucketId
	 * @param drops
	 */
	public void addDrops(Long bucketId, Collection<Drop> drops);
	
	/**
	 * Removes a single drop from a bucket
	 * 
	 * @param bucketId
	 * @param drop
	 */
	public void removeDrop(Long bucketId, Drop drop);

	/**
	 * Removes a group of drops from a bucket
	 * 
	 * @param bucketId
	 * @param drops
	 */
	public void removeDrops(Long bucketId, Collection<Drop> drops);
	
	/**
	 * Adds a user as a collaborator to a bucket
	 * 
	 * @param bucketId
	 * @param user
	 * @param readOnly
	 */
	public void addCollaborator(Long bucketId, User user, boolean readOnly);
	
	/**
	 * Gets the users collaborating on the bucket
	 * 
	 * @param bucketId Database ID of the bucket
	 * @return
	 */
	public Collection<User> getCollaborators(Long bucketId);
	
	/**
	 * Removes a user from the list of users collaborating on a bucket
	 * 
	 * @param bucketId
	 * @param user
	 */
	public void removeCollaborator(Long bucketId, User user);
		
}
