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

import java.util.Date;
import java.util.List;

import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketCollaborator;
import com.ushahidi.swiftriver.core.model.BucketComment;
import com.ushahidi.swiftriver.core.model.BucketDrop;
import com.ushahidi.swiftriver.core.model.Drop;

public interface BucketDao extends GenericDao<Bucket> {

	/**
	 * Get list of drops from the given river.
	 * 
	 * @param id
	 * @param maxId
	 * @param dropCount
	 * @param queryingAccount
	 * @return
	 */
	public List<Drop> getDrops(Long bucketId, Long maxId, int page,
			int dropCount, DropFilter filter, Account queryingAccount);

	/**
	 * Get drops from the river newer than the given id.
	 * 
	 * @param riverId
	 * @param sinceId
	 * @param dropCount
	 * @param filter
	 * @param queryingAccount
	 * @return
	 */
	public List<Drop> getDropsSince(Long bucketId, Long sinceId, int dropCount,
			DropFilter filter, Account queryingAccount);

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
	 * Adds a collaborator to the bucket with the specified id
	 * 
	 * @param bucket
	 * @param account
	 * @param readOnly
	 * @return
	 */
	public BucketCollaborator addCollaborator(Bucket bucket, Account account,
			boolean readOnly);

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
	 * 
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
	 * Deletes the {@link Drop} with the id in <code>dropId</code> from the list
	 * of drops for the {@link Bucket} with the id specified in <code>id</code>
	 * 
	 * @param id
	 * @param dropId
	 * @return boolean
	 */
	public boolean deleteDrop(Long id, Long dropId);

	/**
	 * Gets and returns the bucket with the name specified in
	 * <code>bucketName</code> and is owned by the {@link Account} in
	 * <code>account</account>
	 * 
	 * @param account
	 * @param bucketName
	 * @return {@link Bucket}
	 */
	public Bucket findBucketByName(Account account, String bucketName);

	/**
	 * Gets and returns a {@link List} of all {@link Bucket} entities whose id
	 * is in the {@link List} specified by <code><bucketIds/code>
	 * 
	 * @param bucketIds
	 * @return
	 */
	public List<Bucket> findAll(List<Long> bucketIds);

	/**
	 * Gets and returns the {@link BucketDrop} record with the specified
	 * <code>bucketId</code> and <code>dropId</code>
	 * 
	 * @param bucketId
	 * @param dropId
	 * @return
	 */
	public BucketDrop findDrop(Long bucketId, Long dropId);

	/**
	 * Adds a new comment by the {@link Account} specified in
	 * <code>account</code> to the {@link Bucket} specified in
	 * <code>bucket</code>
	 * 
	 * @param bucket
	 * @param commentText
	 * @param account
	 * @return
	 */
	public BucketComment addComment(Bucket bucket, String commentText,
			Account account);

	/**
	 * Helper Class for holding filter parameters when getting drops.
	 * 
	 */
	public static class DropFilter {
		List<String> channels;

		Boolean read;

		Boolean photos;

		Date dateFrom;

		Date dateTo;

		/**
		 * @return the channels
		 */
		public List<String> getChannels() {
			return channels;
		}

		/**
		 * @param channels
		 *            the channels to set
		 */
		public void setChannels(List<String> channels) {
			this.channels = channels;
		}

		/**
		 * @return the read
		 */
		public Boolean getRead() {
			return read;
		}

		/**
		 * @param read
		 *            the read to set
		 */
		public void setRead(Boolean read) {
			this.read = read;
		}

		/**
		 * @return the photos
		 */
		public Boolean getPhotos() {
			return photos;
		}

		/**
		 * @param photos
		 *            the photos to set
		 */
		public void setPhotos(Boolean photos) {
			this.photos = photos;
		}

		/**
		 * @return the dateFrom
		 */
		public Date getDateFrom() {
			return dateFrom;
		}

		/**
		 * @param dateFrom
		 *            the dateFrom to set
		 */
		public void setDateFrom(Date dateFrom) {
			this.dateFrom = dateFrom;
		}

		/**
		 * @return the dateTo
		 */
		public Date getDateTo() {
			return dateTo;
		}

		/**
		 * @param dateTo
		 *            the dateTo to set
		 */
		public void setDateTo(Date dateTo) {
			this.dateTo = dateTo;
		}
	}

	/**
	 * Reduces the drop count of the {@link Bucket} specified in
	 * <code>bucket</code>
	 * 
	 * @param bucket
	 */
	public void decreaseDropCount(Bucket bucket);

	/**
	 * Increases the drop count for the {@link Bucket} specified in the
	 * <code>bucket</code> parameter
	 * 
	 * @param bucket
	 */
	public void increaseDropCount(Bucket bucket);

	
	/**
	 * Finds and returns a {@link List} of {@link Bucket} entities
	 * that contain the phrase in <code>searchTerm</code> in their
	 * <code>name</code> or <code>description</code> fields
	 * 
	 * @param searchTerm
	 * @param count
	 * @param page
	 * @return
	 */
	public List<Bucket> findAll(String searchTerm, int count, int page);

}
