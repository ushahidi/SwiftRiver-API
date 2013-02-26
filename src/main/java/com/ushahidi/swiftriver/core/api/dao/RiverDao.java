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
	 * Get list of drops from the given river.
	 * 
	 * @param id
	 * @param maxId
	 * @param dropCount
	 * @param queryingAccount
	 * @return
	 */
	public List<Drop> getDrops(Long id, Long maxId, int page, int dropCount, List<String> channelList, List<Long> channelIds, Boolean isRead, Date dateFrom, Date dateTo, Account queryingAccount);
	
	/**
	 * Get list of drops from the given river with an id after the the given since_id
	 * 
	 * @param id
	 * @param maxId
	 * @param dropCount
	 * @param queryingAccount
	 * @return
	 */
	public List<Drop> getDropsSince(Long id, Long sinceId, int dropCount, List<String> channelList, List<Long> channelIds, Boolean isRead, Date dateFrom, Date dateTo, Account queryingAccount);
	
	/**
	 * Gets and returns a collaborator tied to the {@link Account} in <code>accountId</code>
	 * and the river specified by <code>riverId</code>
	 * 
	 * @param riverId
	 * @param accountId
	 * @return {@link RiverCollaborator}
	 */
	public RiverCollaborator findCollaborator(Long riverId, Long accountId);
	
	/**
	 * Adds a collaborator to a river
	 * @param river
	 * @param account
	 * @param readOnly
	 * @return {@link RiverCollaborator}
	 */
	public RiverCollaborator addCollaborator(River river, Account account, boolean readOnly);

	/**
	 * Updates a collaborator
	 * 
	 * @param collaborator
	 */
	public void updateCollaborator(RiverCollaborator collaborator);

	/**
	 * Deletes the drop specified by 
	 * @param id
	 * @param dropId
	 * @return
	 */
	public boolean removeDrop(Long id, Long dropId);
}
