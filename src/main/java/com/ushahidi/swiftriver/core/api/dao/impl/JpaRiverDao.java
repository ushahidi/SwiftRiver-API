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

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Channel;
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
	 * @see RiverDao#getDrops(Long, Long, int)
	 */	
	@SuppressWarnings("unchecked")
	public List<Drop> getDrops(Long id, Long sinceId, int dropCount) {
		String sql = "SELECT r.drops FROM River r, IN(r.drops) d WHERE r.id = :riverId AND d.id > :sinceId";

		Query query = entityManager.createQuery(sql);
		query.setParameter("riverId", id);
		query.setParameter("sinceId", sinceId);
		query.setMaxResults(dropCount);

		return (List<Drop>) query.getResultList();
	}

	/**
	 * @see RiverDao#removeDrop(long, Drop)
	 */
	public void removeDrop(long riverId, Drop drop) {
		findById(riverId).getDrops().remove(drop);
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
	 * @see RiverDao#addChannel(long, Channel)
	 */
	public void addChannel(long riverId, Channel channel) {
		findById(riverId).getChannels().add(channel);
	}

	/**
	 * @see RiverDao#removeChannel(long, Channel)
	 */
	public void removeChannel(long riverId, Channel channel) {
		findById(riverId).getChannels().remove(channel);
	}

	/**
	 * @see RiverDao#addCollaborator(long, Account, boolean)
	 */
	public RiverCollaborator addCollaborator(long riverId, Account account, boolean readOnly) {
		River river = findById(new Long(riverId));
	
		RiverCollaborator collaborator = new RiverCollaborator();
		collaborator.setAccount(account);
		collaborator.setRiver(river);
		collaborator.setReadOnly(readOnly);
		
		river.getCollaborators().add(collaborator);
		this.entityManager.persist(collaborator);
		
		return collaborator;
	}

	/**
	 * @see {@link RiverDao#deleteCollaborator(Long, Long)}
	 */
	public void deleteCollaborator(Long id, Long collaboratorId) {
		// Retrieve the collaborator from the DB
		RiverCollaborator collaborator = this.entityManager.find(RiverCollaborator.class, 
				collaboratorId);
		
		if (collaborator.getRiver() != null && collaborator.getRiver().getId().equals(id)) {
			this.entityManager.remove(collaborator);
		}
	}

	/**
	 * @see {@link RiverDao#findCollaborator(Long, Long)}
	 */
	public RiverCollaborator findCollaborator(Long riverId, Long collaboratorId) {
		String sql = "FROM RiverCollaborator rc WHERE rc.id = :collaboratorId AND rc.river.id =:riverId";

		Query query = this.entityManager.createQuery(sql);
		query.setParameter("collaboratorId", collaboratorId);
		query.setParameter("riverId", riverId);

		return (RiverCollaborator) query.getSingleResult();
	}

	/**
	 * @see {@link RiverDao#updateCollaborator(RiverCollaborator)}
	 */
	public void updateCollaborator(RiverCollaborator collaborator) {
		this.entityManager.merge(collaborator);
	}

}
