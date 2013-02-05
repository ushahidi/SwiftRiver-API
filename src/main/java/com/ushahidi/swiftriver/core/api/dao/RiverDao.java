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

import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Channel;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.River;


public interface RiverDao extends JpaDao<River, Long>{
	
	/**
	 * Returns drops from the specified river where the oldest ID is
	 * specified by @param sinceId
	 * 
	 * @param id Unique ID of the river
	 * @param sinceId Oldest dropId
	 * @param dropCount No. of drops to return
	 * @return
	 */
	public List<Drop> getDrops(Long id, Long sinceId, int dropCount);
	
	/**
	 * Adds a collaborator to a river
	 * @param riverId
	 * @param account
	 * @param readOnly
	 */
	public void addCollaborator(long riverId, Account account, boolean readOnly);

	/**
	 * Removes a drop from the river
	 * 
	 * @param riverId River containing the drop to be removed
	 * @param drop Drop to be removed from the river
	 */
	public void removeDrop(long riverId, Drop drop);
	
	/**
	 * Adds a drop to a river
	 * 
	 * @param riverId
	 * @param drop
	 */
	public void addDrop(long riverId, Drop drop);
	
	/**
	 * Adds a collection of drops to a river
	 * 
	 * @param riverId
	 * @param drops
	 */
	public void addDrops(long riverId, Collection<Drop> drops);
	
	/**
	 * Adds a channel to a river
	 * 
	 * @param riverId
	 * @param channel
	 */
	public void addChannel(long riverId, Channel channel);
	
	/**
	 * Removes a channel from a river
	 * @param riverId
	 * @param channel
	 */
	public void removeChannel(long riverId, Channel channel);
	
}
