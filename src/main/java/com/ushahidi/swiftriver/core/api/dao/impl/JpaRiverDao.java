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
package com.ushahidi.swiftriver.core.api.dao.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverCollaborator;


/**
 * Repository class for Rivers
 * @author ekala
 *
 */
@Repository
public class JpaRiverDao extends AbstractJpaDao<River, Long> implements RiverDao {

	public JpaRiverDao() {
		super(River.class);
	}

	/**
	 * @see RiverDao#addDrop(long, Drop)
	 */
	public void addDrop(long riverId, Drop drop) {
		findById(riverId).getDrops().add(drop);
	}

	/**
	 * @see RiverDao#addDrops(long, Collection)
	 */
	public void addDrops(long riverId, Collection<Drop> drops) {
		findById(riverId).getDrops().addAll(drops);
	}

	/**
	 * @see RiverDao#addCollaborator(River, Account, boolean)
	 */
	public RiverCollaborator addCollaborator(River river, Account account, boolean readOnly) {
		RiverCollaborator collaborator = new RiverCollaborator();
		collaborator.setRiver(river);
		collaborator.setAccount(account);
		collaborator.setReadOnly(readOnly);
		
		river.getCollaborators().add(collaborator);
		this.entityManager.persist(collaborator);

		return collaborator;
	}

	/**
	 * @see {@link RiverDao#deleteCollaborator(Long, Long)}
	 */
	public void deleteCollaborator(Long id, Long accountId) {
		// Retrieve the collaborator from the DB
		RiverCollaborator collaborator = findCollaborator(id, accountId);
		
		if (collaborator != null) {
			this.entityManager.remove(collaborator);
		}
	}

	/**
	 * @see {@link RiverDao#findCollaborator(Long, Long)}
	 */
	@SuppressWarnings("unchecked")
	public RiverCollaborator findCollaborator(Long riverId, Long accountId) {
		String sql = "FROM RiverCollaborator rc " +
				"WHERE rc.account.id = :accountId " + 
				"AND rc.river.id =:riverId";

		Query query = this.entityManager.createQuery(sql);
		query.setParameter("accountId", accountId);
		query.setParameter("riverId", riverId);

		List<RiverCollaborator> result = (List<RiverCollaborator>) query.getResultList();
		return result.isEmpty() ? null : result.get(0);
	}

	/**
	 * @see {@link RiverDao#updateCollaborator(RiverCollaborator)}
	 */
	public void updateCollaborator(RiverCollaborator collaborator) {
		this.entityManager.merge(collaborator);
	}

	/**
	 * @see {@link RiverDao#removeDrop(Long, Long)}
	 */
	public boolean removeDrop(Long id, Long dropId) {
		String sql = "DELETE FROM RiverDrop rd " +
				"WHERE rd.id = :dropId " + 
				"AND rd.river.id = :riverId";
		Query query = entityManager.createQuery(sql);
		query.setParameter("riverId", id);
		query.setParameter("dropId", dropId);
		
		return query.executeUpdate() == 1;
	}

	/**
	 * @see {@link RiverDao#getDrops(Long, Map)}
	 */
	public List<Drop> getDrops(Long id, Map<String, Object> requestParams) {
		// TODO Auto-generated method stub
		return null;
	}

}
