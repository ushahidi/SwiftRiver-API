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
package com.ushahidi.swiftriver.dao.hibernate;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.dao.RiverDAO;
import com.ushahidi.swiftriver.model.Channel;
import com.ushahidi.swiftriver.model.Drop;
import com.ushahidi.swiftriver.model.River;
import com.ushahidi.swiftriver.model.User;


/**
 * Hibernate class for Rivers
 * @author ekala
 *
 */
@Repository("riverDAO")
@Transactional
public class HibernateRiverDAO extends AbstractHibernateDAO<River, Long> implements RiverDAO {

	public HibernateRiverDAO() {
		super(River.class);
	}

	/**
	 * @see RiverDAO#getDrops(long, Object...)
	 */	
	@SuppressWarnings("unchecked")
	public List<Drop> getDrops(long id, Object... params) {
		String hql = "Select r.drops from River r where r.id = ?";
		return (List<Drop>) hibernateTemplate.find(hql, new Long(id));
	}

	/**
	 * @see RiverDAO#getCollaborators(River)
	 */
	@SuppressWarnings("unchecked")
	public List<User> getCollaborators(River river) {
		String query = "Select r.collaborators from River r where r = ?";
		return (List<User>) hibernateTemplate.find(query, river);
	}

	/**
	 * @see RiverDAO#addCollaborator(long, User, boolean)
	 */
	@Override
	public void addCollaborator(long riverId, User user, boolean readOnly) {
		// TODO Auto-generated method stub	
	}

	/**
	 * @see RiverDAO#removeCollaborator(long, User)
	 */
	@Override
	public void removeCollaborator(long riverId, User user) {
		getById(riverId).getDrops().remove(user);
	}

	/**
	 * @see RiverDAO#removeDrop(long, Drop)
	 */
	public void removeDrop(long riverId, Drop drop) {
		getById(riverId).getDrops().remove(drop);
	}

	/**
	 * @see RiverDAO#addDrop(long, Drop)
	 */
	public void addDrop(long riverId, Drop drop) {
		getById(riverId).getDrops().add(drop);
	}

	/**
	 * @see RiverDAO#addDrops(long, Collection)
	 */
	public void addDrops(long riverId, Collection<Drop> drops) {
		getById(riverId).getDrops().addAll(drops);
	}

	/**
	 * @see RiverDAO#addChannel(long, Channel)
	 */
	public void addChannel(long riverId, Channel channel) {
		getById(riverId).getChannels().add(channel);
	}

	/**
	 * @see RiverDAO#removeChannel(long, Channel)
	 */
	public void removeChannel(long riverId, Channel channel) {
		getById(riverId).getChannels().remove(channel);
	}

	/**
	 * @see RiverDAO#getChannels(long)
	 */
	@SuppressWarnings("unchecked")
	public List<Channel> getChannels(long riverId) {
		// TODO Auto-generated method stub
		String query = "from Channel c where c.riverId = ?";
		return (List<Channel>) hibernateTemplate.find(query, new Long(riverId));
	}

}
