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
package com.ushahidi.swiftriver.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ushahidi.swiftriver.dao.ChannelDAO;
import com.ushahidi.swiftriver.dao.SwiftRiverDAO;
import com.ushahidi.swiftriver.model.Channel;
import com.ushahidi.swiftriver.model.ChannelOption;
import com.ushahidi.swiftriver.service.ChannelService;

/**
 * Service class for channels
 * @author ekala
 *
 */
@Service
public class ChannelServiceImpl extends AbstractServiceImpl<Channel, Integer> implements ChannelService {

	@Autowired
	private ChannelDAO channelDAO;

	
	@Override
	public SwiftRiverDAO<Channel, Integer> getDAO() {
		return channelDAO;
	}

	@Override
	public Collection<ChannelOption> getChannelOptions(Integer channelId) {
		return channelDAO.getChannelOptions(channelId);
	}

}
