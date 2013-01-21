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

import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.dao.ChannelDAO;
import com.ushahidi.swiftriver.model.Channel;
import com.ushahidi.swiftriver.model.ChannelOption;

/**
 * Hibernate class for channels
 * @author ekala
 *
 */

@Repository("channelDAO")
@Transactional
public class HibernateChannelDAO extends AbstractHibernateDAO<Channel, Integer> implements ChannelDAO {

	public HibernateChannelDAO() {
		super(Channel.class);
	}

	/**
	 * @see ChannelDAO#getChannelOptions(Integer)
	 */
	@SuppressWarnings("unchecked")
	public Collection<ChannelOption> getChannelOptions(Integer channelId) {
		String hql = "Select c.channelOptions from Channel c where c.id = ?";
		return (List<ChannelOption>) hibernateTemplate.find(hql, channelId);
	}

}
