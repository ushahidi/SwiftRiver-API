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

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.ChannelDao;
import com.ushahidi.swiftriver.core.model.Channel;
import com.ushahidi.swiftriver.core.model.ChannelOption;

/**
 * Hibernate class for channels
 * @author ekala
 *
 */

@Repository
@Transactional
public class JpaChannelDao extends AbstractJpaDao<Channel, Integer> implements ChannelDao {

	public JpaChannelDao() {
		super(Channel.class);
	}

	/**
	 * @see ChannelDao#getChannelOptions(Integer)
	 */
	@SuppressWarnings("unchecked")
	public Collection<ChannelOption> getChannelOptions(Integer channelId) {
		String hql = "SELECT c.channelOptions FROM Channel c WHERE c.id = ?";

		Query query = entityManager.createQuery(hql).setParameter(0, channelId);
		return (List<ChannelOption>) query.getResultList();
	}

}
