/**
 * The contents of this file are subject to the Affero General
 * Public License (AGPL) Version 3; you may not use this file 
 * except in compliance with the License. You may obtain a copy
 * of the License at http://www.gnu.org/licenses/agpl.html
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.dao;

import java.util.Map;
import java.util.Set;

import com.ushahidi.swiftriver.model.Drop;
import com.ushahidi.swiftriver.model.Bucket;
import com.ushahidi.swiftriver.model.User;

/**
 * Database operations for a bucket
 * 
 * @author ekala
 *
 */
public interface BucketDAO {
	
	/**
	 * Creates a bucket
	 * 
	 * @param bucket
	 */
	public void create(Bucket bucket);
	
	/**
	 * Updates a bucket
	 * 
	 * @param bucket
	 */
	public void update(Bucket bucket);
	
	/**
	 * Deletes a bucket
	 * 
	 * @param bucket
	 */
	public void delete(Bucket bucket);
	
	/**
	 * Retrieves a bucket using its unique id in the database
	 * 
	 * @param id
	 * @return
	 */
	public Bucket getBucket(long id);
	
	/**
	 * Gets the drops from the bucket with the specified id and parameters
	 *  
	 * @param id
	 * @param params
	 * @return
	 */
	public Set<Drop> getDrops(long id, Map<Object, Object>...params);
	
	/**
	 * Adds a single drop to a bucket
	 * 
	 * @param bucket
	 * @param drop
	 */
	public void addDrop(Bucket bucket, Drop drop);
	
	/**
	 * Adds a group of drops to a bucket
	 * 
	 * @param bucket
	 * @param drops
	 */
	public void addDrops(Bucket bucket, Set<Drop> drops);
	
	/**
	 * Removes a single drop from a bucket
	 * 
	 * @param bucket
	 * @param drop
	 */
	public void removeDrop(Bucket bucket, Drop drop);

	/**
	 * Removes a group of drops from a bucket
	 * 
	 * @param bucket
	 * @param drops
	 */
	public void removeDrops(Bucket bucket, Set<Drop> drops);
	
	/**
	 * Adds a user as a collaborator to a bucket
	 * 
	 * @param bucket
	 * @param user
	 * @param readOnly
	 */
	public void addCollabotator(Bucket bucket, User user, boolean readOnly);
	
	/**
	 * Gets the users collaborating on the bucket
	 * 
	 * @param bucket
	 * @return
	 */
	public Set<User> getCollaborators(Bucket bucket);
	
	/**
	 * Removes a user from the list of users collaborating on a bucket
	 * 
	 * @param bucket
	 * @param user
	 */
	public void removeCollaborator(Bucket bucket, User user);
		
}
