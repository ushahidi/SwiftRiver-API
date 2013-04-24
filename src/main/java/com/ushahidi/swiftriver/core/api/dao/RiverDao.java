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

import java.util.List;

import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverCollaborator;
import com.ushahidi.swiftriver.core.model.drop.DropFilter;

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
	 * @param dropCount
	 * @param queryingAccount
	 * @param id
	 * 
	 * @return
	 */
	public List<Drop> getDrops(Long riverId, DropFilter filter, int page,
			int dropCount, Account queryingAccount);

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
	 * Finds and returns a {@link List} of {@link River} entities
	 * that contain the phrase in <code>searchTerm</code> in their
	 * <code>name</code> or <code>description</code> fields
	 * 
	 * @param searchTerm
	 * @param count
	 * @param page
	 * @return
	 */
	public List<River> findAll(String searchTerm, int count, int page);
}
