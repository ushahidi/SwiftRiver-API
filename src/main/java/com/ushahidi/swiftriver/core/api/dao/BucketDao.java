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
import com.ushahidi.swiftriver.core.model.BucketDrop;
import com.ushahidi.swiftriver.core.model.Drop;

public interface BucketDao extends GenericDao<Bucket> {

	/**
	 * Gets and returns a list of drops for the bucket specified in <code>bucketId</code>
	 * using the request parameters in <code>requestParams</code>. The {@link Account}
	 * in <code>account</code> is used to fetch account-specific metadata (tags, links, places etc)
	 *  
	 * @param bucketId
	 * @param account
	 * @param requestParams 
	 * @return {@link LinkDTO}
	 */
	public List<Drop> getDrops(Long bucketId, Account account, Map<String, Object> requestParams);
	
	/**
	 * Adds the {@link Drop} specified in <code>drop</code> to the
	 * {@link Bucket} in <code>bucket</code>
	 * 
	 * @param bucket
	 * @param drop
	 * @return 
	 */
	public boolean addDrop(Bucket bucket, Drop drop);
	
	/**
	 * Adds a collection of drops to a buckets
	 * 
	 * @param bucketId
	 * @param drops
	 */
	public void addDrops(Long bucketId, Collection<Drop> drops);
	
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
	 * Retrieves the {@link BucketCollaborator} record associated with the
	 * {@link Account} in <code>account</code> for the {@link Bucket} specified
	 * in <code>bucket</code>
	 * 
	 * @param bucket
	 * @param account
	 * @return {@link BucketCollaborator} on success, null otherwise
	 */
	public BucketCollaborator findCollaborator(Long bucketId, Long accountId);

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

	/**
	 * Gets and returns a {@link List} of all {@link Bucket} entities
	 * whose id is in the {@link List} specified by <code><bucketIds/code>
	 * 
	 * @param bucketIds
	 * @return
	 */
	public List<Bucket> findAll(List<Long> bucketIds);

	/**
	 * Gets and returns the {@link BucketDrop} record with
	 * the specified <code>bucketId</code> and <code>dropId</code>
	 * 
	 * @param bucketId
	 * @param dropId
	 * @return
	 */
	public BucketDrop findDrop(Long bucketId, Long dropId);

}
