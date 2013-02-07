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
package com.ushahidi.swiftriver.core.api.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import com.ushahidi.swiftriver.core.model.Channel;
import com.ushahidi.swiftriver.core.model.ChannelOption;

/**
 * DTO mapping class for channels
 * @author ekala
 *
 */
public class ChannelDTO extends AbstractDTO<Channel> {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> createMapFromEntity(Channel entity) {
		List<Map<String, Object>> channelOptions = new ArrayList<Map<String,Object>>();
		ChannelOptionDTO channelOptionDTO = new ChannelOptionDTO();

		for (ChannelOption channelOption: entity.getChannelOptions()) {
			channelOptions.add(channelOptionDTO.createMapFromEntity(channelOption));
		}

		Object[][] channelData = {
				{"id", entity.getId()},
				{"name", entity.getChannel()},
				{"options", channelOptions}
		};

		return ArrayUtils.toMap(channelData);
	}

	@Override
	public Channel createEntityFromMap(Map<String, Object> map) {
		Channel channel = new Channel();
		
		channel.setChannel((String) map.get("name"));

		return channel;
	}

	@Override
	protected String[] getValidationKeys() {
		return new String[]{"name", "options"};
	}

	@Override
	protected void copyFromMap(Channel target, Map<String, Object> source) {
		// TODO Auto-generated method stub
		
	}

}
