/**
 * The contents of this file are subject to the Affero General
 * Public License (AGPL) Version 3; you may not use this file 
 * except in compliance with the License. You may obtain a copy
 * of the License at http://www.gnu.org/licenses/agpl.html
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.dao;

import java.util.Set;

import com.ushahidi.swiftriver.model.Channel;
import com.ushahidi.swiftriver.model.Drop;
import com.ushahidi.swiftriver.model.River;
import com.ushahidi.swiftriver.model.User;


public interface RiverDAO {
	
	/**
	 * Creates a new river
	 * @param river
	 */
	public void create(River river);
	
	/**
	 * Updates an existing river
	 * @param river
	 */
	public void update(River river);
	
	/**
	 * Deletes a river
	 * @param river
	 */
	public void delete(River river);
	
	/**
	 * Find a river using its unique id in the database
	 * 
	 * @param id
	 * @return
	 */
	public River getRiver(long id);
	
	/**
	 * Gets the drops in a river using the specified id and parameters
	 * 
	 * @param id Unique ID of the river
	 * @param params Additional(optional) parameters for filtering the drops listing
	 * @return
	 */
	public Set<Drop> getDrops(long id, Object ...params);
	
	/**
	 * Gets users collaborating on the specified river
	 * 
	 * @param river
	 * @return
	 */
	public Set<User> getCollaborators(River river);
	
	/**
	 * Adds a user to the list of river collaborators
	 * @param river
	 * @param user
	 * @param readOnly
	 */
	public void addCollaborator(River river, User user, boolean readOnly);
	
	/**
	 * Removes a user from the list of river collaborators
	 * @param river
	 * @param user
	 */
	public void removeCollaborator(River river, User user);
	
	/**
	 * Removes a drop from the river
	 * 
	 * @param river River containing the drop to be removed
	 * @param drop Drop to be removed from the river
	 */
	public void removeDrop(River river, Drop drop);
	
	/**
	 * Adds a drop to a river
	 * 
	 * @param river
	 * @param drop
	 */
	public void addDrop(River river, Drop drop);
	
	/**
	 * Adds a collection of drops to a river
	 * 
	 * @param river
	 * @param drops
	 */
	public void addDrops(River river, Set<Drop> drops);
	
	/**
	 * Adds a channel to a river
	 * 
	 * @param river
	 * @param channel
	 */
	public void addChannel(River river, Channel channel);
	
	/**
	 * Gets the channels in a river
	 * @param river
	 */
	public Set<Channel> getChannels(River river);
	
}
