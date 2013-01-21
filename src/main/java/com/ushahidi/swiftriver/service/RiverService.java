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
package com.ushahidi.swiftriver.service;

import java.util.Collection;
import java.util.List;

import com.ushahidi.swiftriver.dao.RiverDAO;
import com.ushahidi.swiftriver.model.Channel;
import com.ushahidi.swiftriver.model.Drop;
import com.ushahidi.swiftriver.model.River;
import com.ushahidi.swiftriver.model.User;

public interface RiverService extends SwiftRiverService<River, Long> {

	public void setRiverDAO(RiverDAO riverDAO);

	/**
	 * Gets the drops in a river using the specified id and parameters
	 * 
	 * @param id Unique ID of the river
	 * @param params Additional(optional) parameters for filtering the drops listing
	 * @return
	 */
	public List<Drop> getDrops(long id, Object ...params);
	
	/**
	 * Gets users collaborating on the specified river
	 * 
	 * @param river
	 * @return
	 */
	public List<User> getCollaborators(River river);
	
	/**
	 * Adds a user to the list of river collaborators
	 * @param riverId
	 * @param user
	 * @param readOnly
	 */
	public void addCollaborator(long riverId, User user, boolean readOnly);
	
	/**
	 * Removes a user from the list of river collaborators
	 * @param riverId
	 * @param user
	 */
	public void removeCollaborator(long riverId, User user);
	
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
	 * Gets the channels in a river
	 * @param riverId
	 */
	public List<Channel> getChannels(long riverId);

	/**
	 * Removes a channel from a river
	 * @param riverId
	 * @param channel
	 */
	public void removeChannel(long riverId, Channel channel);

}
