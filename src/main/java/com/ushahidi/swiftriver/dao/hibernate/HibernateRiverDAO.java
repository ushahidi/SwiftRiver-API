/**
 * The contents of this file are subject to the Affero General
 * Public License (AGPL) Version 3; you may not use this file 
 * except in compliance with the License. You may obtain a copy
 * of the License at http://www.gnu.org/licenses/agpl.html
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.dao.hibernate;

import java.util.Set;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

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
public class HibernateRiverDAO extends HibernateDaoSupport implements RiverDAO {

	/**
	 * @see RiverDAO#create(River)
	 */
	public void create(River river) {
		getHibernateTemplate().save(river);
	}

	/**
	 * @see RiverDAO#update(River)
	 */
	public void update(River river) {
		getHibernateTemplate().update(river);
	}

	/**
	 * @see RiverDAO#delete(River)
	 */
	public void delete(River river) {
		getHibernateTemplate().delete(river);
	}

	/**
	 * @see RiverDAO#getRiver(long)
	 */
	public River getRiver(long id) {
		return (River) getHibernateTemplate().get(River.class, new Long(id)); 
	}

	/**
	 * @see RiverDAO#getDrops(long, Object...)
	 */
	public Set<Drop> getDrops(long id, Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see RiverDAO#getCollaborators(River)
	 */
	public Set<User> getCollaborators(River river) {
		// TODO Auto-generated method stub		
		return null;
	}

	/**
	 * @see RiverDAO#addCollaborator(River, User, boolean)
	 */
	@Override
	public void addCollaborator(River river, User user, boolean readOnly) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see RiverDAO#removeCollaborator(River, User)
	 */
	@Override
	public void removeCollaborator(River river, User user) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @see RiverDAO#removeDrop(River, Drop)
	 */
	public void removeDrop(River river, Drop drop) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see RiverDAO#addDrop(River, Drop)
	 */
	public void addDrop(River river, Drop drop) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see RiverDAO#addDrops(River, Set)
	 */
	public void addDrops(River river, Set<Drop> drops) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see RiverDAO#addChannel(River, Channel)
	 */
	public void addChannel(River river, Channel channel) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see RiverDAO#getChannels(River)
	 */
	public Set<Channel> getChannels(River river) {
		// TODO Auto-generated method stub
		return null;
	}

}
