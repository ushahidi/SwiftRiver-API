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

import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketDrop;
import com.ushahidi.swiftriver.core.model.BucketDropComment;
import com.ushahidi.swiftriver.core.model.BucketDropForm;
import com.ushahidi.swiftriver.core.model.BucketDropLink;
import com.ushahidi.swiftriver.core.model.BucketDropPlace;
import com.ushahidi.swiftriver.core.model.BucketDropTag;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.RiverDrop;
import com.ushahidi.swiftriver.core.model.Tag;

public interface BucketDropDao extends GenericDao<BucketDrop>, ContextDropDao {
	
	/**
	 * Adds the {@link Tag} specified in <code>tag</code> to the 
	 * {@link BucketDrop} specified in <code>bucketDrop</code>
	 *  
	 * @param bucketDrop
	 * @param tag
	 */
	public void addTag(BucketDrop bucketDrop, Tag tag);

	/**
	 * Checks whether the {@link Tag} specified in <code>tag</code>
	 * has been added to the {@link BucketDrop} specified in <code>bucketDrop</code>
	 * 
	 * @param bucketDrop
	 * @param tag
	 * @return
	 */
	public BucketDropTag findTag(BucketDrop bucketDrop, Tag tag);

	/**
	 * Removes the {@link Tag} specified in <code>tag</code> from
	 * the {@link BucketDrop} specified in <code>bucketDrop</code>
	 * 
	 * @param bucketDrop
	 * @param tag
	 * @return
	 */
	public boolean deleteTag(BucketDrop bucketDrop, Tag tag);

	/**
	 * Adds the {@link Link} specified in <code>link</code> to the 
	 * {@link BucketDrop} specified in <code>bucketDrop</code>
	 * 
	 * @param bucketDrop
	 * @param link
	 */
	public void addLink(BucketDrop bucketDrop, Link link);

	/**
	 * Checks whether the {@link Link} specified in <code>link</code>
	 * has been added to the {@link BucketDrop} specified in <code>bucketDrop</code>
	 * 
	 * @param bucketDrop
	 * @param link
	 * @return
	 */
	public BucketDropLink findLink(BucketDrop bucketDrop, Link link);

	/**
	 * Removes the {@link Link} specified in <code>link</code> from
	 * the {@link BucketDrop} specified in <code>bucketDrop</code>
	 * 
	 * @param bucketDrop
	 * @param link
	 * @return
	 */
	public boolean deleteLink(BucketDrop bucketDrop, Link link);

	/**
	 * Checks whether the {@link Place} specified in <code>place</code>
	 * has been added to the {@link BucketDrop} specified in <code>bucketDrop</code>
	 * 
	 * @param bucketDrop
	 * @param place
	 * @return
	 */
	public BucketDropPlace findPlace(BucketDrop bucketDrop, Place place);

	/**
	 * Adds the {@link Place} specified in <code>place</code> to the 
	 * {@link BucketDrop} specified in <code>bucketDrop</code>
	 * 
	 * @param bucketDrop
	 * @param place
	 */
	public void addPlace(BucketDrop bucketDrop, Place place);

	/**
	 * Removes the {@link Place} specified in <code>place</code> from
	 * the {@link BucketDrop} specified in <code>bucketDrop</code>
	 * 
	 * @param bucketDrop
	 * @param place
	 * @return
	 */
	public boolean deletePlace(BucketDrop bucketDrop, Place place);

	/**
	 * Increments the veracity of the {@link BucketDrop} specified
	 * in <code>bucketDrop</code> by 1
	 * 
	 * @param bucketDrop
	 */
	public void increaseVeracity(BucketDrop bucketDrop);

	/**
	 * Creates a {@link BucketDrop} entity from the {@link RiverDrop}
	 * specified in <code>riverDrop> for the {@link Bucket} specified
	 * in <code>bucket</code>
	 * 
	 * @param riverDrop
	 * @param bucket
	 */
	public void createFromRiverDrop(RiverDrop riverDrop, Bucket bucket);

	/**
	 * Creates a {@link BucketDrop} entity from an existing {@link BucketDrop},
	 * entity that is specified in <code>sourceBucketDrop</code>.
	 * 
	 * The newly created entity belongs to the bucket specified in
	 * <code>bucket</code>
	 * 
	 * @param sourceBucketDrop
	 * @param bucket
	 */
	public void createFromExisting(BucketDrop sourceBucketDrop, Bucket bucket);

	/**
	 * Create a new {@link BucketDropComment} entity for the {@link Bucket}
	 * entity specified in <code>bucket</code>. 
	 * 
	 * @param bucketDrop
	 * @param account
	 * @param commentText
	 * @return
	 */
	public BucketDropComment addComment(BucketDrop bucketDrop, Account account,
			String commentText);

	/**
	 * Deletes the {@link BucketDropComment} entity with the ID specified 
	 * in <code>commentId</code>
	 * 
	 * @param commentId
	 * @return
	 */
	public boolean deleteComment(Long commentId);
	
	/**
	 * Return the form with the specified id in the drop
	 * 
	 * @param dropId
	 * @param formId
	 * @return
	 */
	public BucketDropForm findForm(Long dropId, Long formId);

	/**
	 * Checks whether the {@link BucketDrop} specified in <code>bucketDrop</code>
	 * has been added to the list of read drops for the {@link Account}
	 * specified in <code>account</code>
	 * 
	 * @param bucketDrop
	 * @param account
	 * @return
	 */
	public boolean isRead(BucketDrop bucketDrop, Account account);

}
