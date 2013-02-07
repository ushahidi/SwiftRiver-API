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

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.ChannelDao;
import com.ushahidi.swiftriver.core.model.Channel;
import com.ushahidi.swiftriver.core.model.ChannelOption;

/**
 * Repository class for channels
 * @author ekala
 *
 */

@Repository
public class JpaChannelDao extends AbstractJpaDao<Channel, Integer> implements ChannelDao {

	public JpaChannelDao() {
		super(Channel.class);
	}

	/**
	 * @see {@link ChannelDao#addChannelOptions(Channel, List)}
	 */
	public void addChannelOptions(Channel channel, List<ChannelOption> channelOptions) {
		for (ChannelOption option: channelOptions) {
			option.setChannel(channel);
			this.entityManager.persist(option);
		}

		// Refresh the state of the channel
		this.entityManager.refresh(channel);
	}

	/**
	 * @see {@link ChannelDao#deleteAllChannelOptions(Channel)}
	 */
	public void deleteAllChannelOptions(Channel channel) {
		// Bulk delete
		this.entityManager.createQuery("DELETE FROM ChannelOption WHERE channel = ?1")
			.setParameter(1, channel)
			.executeUpdate();
	}

}
