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
import java.util.List;
import java.util.Map;

import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketCollaborator;
import com.ushahidi.swiftriver.core.model.Drop;

public interface BucketDao extends GenericDao<Bucket> {
	
	/**
	 * Gets a collection of drops from a bucket using the specified parameters
	 *  
	 * @param bucketId
	 * @param requestParams 
	 * @return {@link LinkDTO}
	 */
	public List<Drop> getDrops(Long bucketId, Map<String, Object> requestParams);
	
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
	 * Adds a collaborator to the bucket with the specified id
	 * 
	 * @param bucket
	 * @param account
	 * @param readOnly
	 * @return TODO
	 */
	public BucketCollaborator addCollaborator(Bucket bucket, Account account, boolean readOnly);

	/**
	 * Retrieves a {@link BucketCollaborator} record using the bucket id (<code>id</code>
	 * and the account id (<code>accountId</code>) of the collaborator
	 * 
	 * @param id
	 * @param accountId
	 * @return {@link BucketCollaborator} on success, null otherwise
	 */
	public BucketCollaborator findCollaborator(Long id, Long accountId);

	/**
	 * Modifies a bucket collaborator record 
	 * @param collaborator
	 */
	public void updateCollaborator(BucketCollaborator collaborator);

	/**
	 * Deletes a {@link BucketCollaborator} record from the database
	 * 
	 * @param collaborator
	 */
	public void deleteCollaborator(BucketCollaborator collaborator);

	/**
	 * Deletes the {@link Drop} with the id in <code>dropId</code> from the list of 
	 * drops for the {@link Bucket} with the id specified in <code>id</code>
	 * 
	 * @param id
	 * @param dropId
	 * @return boolean
	 */
	public boolean deleteDrop(Long id, Long dropId);

	/**
	 * Gets and returns the bucket with the name specified in <code>bucketName</code> 
	 * and is owned by the {@link Account} in <code>account</account>
	 * 
	 * @param account
	 * @param bucketName
	 * @return {@link Bucket}
	 */
	public Bucket findBucketByName(Account account, String bucketName);

}
