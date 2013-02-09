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
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverCollaborator;


public interface RiverDao extends JpaDao<River, Long>{
	
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
	 * Removes the {@link Account} with id <code>accountId</code>
	 * from the list of collaborators on river with id <code>id</code>
	 * 
	 * @param id
	 * @param accountId
	 */
	public void deleteCollaborator(Long id, Long accountId);

	/**
	 * Deletes the drop specified by 
	 * @param id
	 * @param dropId
	 * @return
	 */
	public boolean removeDrop(Long id, Long dropId);

	/**
	 * Gets and returns the drops for the river specified in <code>id</code>
	 * using the parameters in <code>requestParams</code>.
	 * 
	 * The valid parameter keys for <code>requestParams</code> are:
	 * <ul>
	 * <li>count</li>
	 * <li>max_id</li>
	 * <li>since_id</li>
	 * <li>date_from</li>
	 * <li>date_to</li>
	 * <li>keywords</li>
	 * <li>channels</li>
	 * <li>location</li>
	 * </ul>
	 *
	 * @param id ID of the river in the database
	 * @param requestParams <key,value> pairs of the parameters to use for fetching the drops
	 * @return {@link List}
	 */
	public List<Drop> getDrops(Long id, Map<String, Object> requestParams);
	
}
