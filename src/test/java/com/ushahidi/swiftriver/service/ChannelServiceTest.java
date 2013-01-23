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
package com.ushahidi.swiftriver.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import com.ushahidi.swiftriver.core.api.dao.ChannelDao;
import com.ushahidi.swiftriver.core.api.service.ChannelService;
import com.ushahidi.swiftriver.core.model.Channel;
import com.ushahidi.swiftriver.test.AbstractSwiftRiverTest;

/**
 * Integration tests for channels
 * @author ekala
 *
 */
public class ChannelServiceTest extends AbstractSwiftRiverTest {
	
	private ChannelService channelService;
	private ChannelDao channelDAO;
	
	@Override
	public void beforeTest() {
		channelService = new ChannelService();
		channelDAO = mock(ChannelDao.class);
		
		channelService.setChannelDAO(channelDAO);
	}

	/**
	 * @verifies that a channel is successfully created
	 */
	@Test
	public void testCreateChannel() {
		Channel channel = new Channel();
		channel.setChannel("instagram");
		channel.setRiver(new Long(1));
		
		channelService.create(channel);

		verify(channelDAO).create(channel);
	}
	
}
