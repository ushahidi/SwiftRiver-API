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
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverCollaborator;

public interface RiverDao extends GenericDao<River> {

	/**
	 * Get a River by its name
	 * 
	 * @param id
	 * @return
	 */
	public River findByName(String name);

	/**
	 * Get list of drops from the river with the ID specified in <code>id</code>
	 * using the parameters specified in <code>params</code>.
	 * 
	 * @param id
	 * @param maxId
	 * @param dropCount
	 * @param queryingAccount
	 * @return
	 */
	public List<Drop> getDrops(Long riverId, Long maxId, int page,
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
	public List<Drop> getDropsSince(Long riverId, Long sinceId, int dropCount,
			DropFilter filter, Account queryingAccount);


	/**
	 * Gets and returns a collaborator tied to the {@link Account} in
	 * <code>accountId</code> and the river specified by <code>riverId</code>
	 * 
	 * @param riverId
	 * @param accountId
	 * @return {@link RiverCollaborator}
	 */
	public RiverCollaborator findCollaborator(Long riverId, Long accountId);

	/**
	 * Adds a collaborator to a river
	 * 
	 * @param river
	 * @param account
	 * @param readOnly
	 * @return {@link RiverCollaborator}
	 */
	public RiverCollaborator addCollaborator(River river, Account account,
			boolean readOnly);

	/**
	 * Updates a collaborator
	 * 
	 * @param collaborator
	 */
	public void updateCollaborator(RiverCollaborator collaborator);

	/**
	 * Deletes the drop specified by
	 * 
	 * @param id
	 * @param dropId
	 * @return
	 */
	public boolean removeDrop(Long id, Long dropId);

	/**
	 * Gets and returns a {@link List} of all {@link River} entities whose id is
	 * in the {@link List} specified by <code><riverIds/code>
	 * 
	 * @param roverIds
	 * @return
	 */
	public List<River> findAll(List<Long> riverIds);

	/**
	 * Helper Class for holding filter parameters when getting drops.
	 * 
	 */
	public static class DropFilter {
		List<String> channelList;

		List<Long> channelIds;

		Boolean read;

		Date dateFrom;

		Date dateTo;
		
		Boolean photos;

		/**
		 * @return the channelList
		 */
		public List<String> getChannelList() {
			return channelList;
		}

		/**
		 * @param channelList
		 *            the channelList to set
		 */
		public void setChannelList(List<String> channelList) {
			this.channelList = channelList;
		}

		/**
		 * @return the channelIds
		 */
		public List<Long> getChannelIds() {
			return channelIds;
		}

		/**
		 * @param channelIds
		 *            the channelIds to set
		 */
		public void setChannelIds(List<Long> channelIds) {
			this.channelIds = channelIds;
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

		public Boolean getPhotos() {
			return photos;
		}

		public void setPhotos(Boolean photos) {
			this.photos = photos;
		}
	}

	/**
	 * Finds and returns a {@link List} of {@link River} entities
	 * that contain the phrase in <code>searchTerm</code> in their
	 * <code>name</code> or <code>description</code> fields
	 * 
	 * @param searchTerm
	 * @param page
	 * @param count
	 * @return
	 */
	public List<River> findAll(String searchTerm, int page, int count);
}
