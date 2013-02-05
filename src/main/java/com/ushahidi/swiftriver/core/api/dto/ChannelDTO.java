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
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import com.ushahidi.swiftriver.core.model.Channel;
import com.ushahidi.swiftriver.core.model.ChannelOption;

/**
 * DTO mapping class for channels
 * @author ekala
 *
 */
public class ChannelDTO extends EntityDTO<Channel> {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> createDTO(Channel entity) {
		ArrayList<Map<String, Object>> channelOptions = new ArrayList<Map<String,Object>>();
		ChannelOptionDTO channelOptionDTO = new ChannelOptionDTO();

		for (ChannelOption channelOption: entity.getChannelOptions()) {
			channelOptions.add(channelOptionDTO.createDTO(channelOption));
		}

		Object[][] channelData = {
				{"id", entity.getId()},
				{"channel", entity.getChannel()},
				{"options", channelOptions}
		};

		return ArrayUtils.toMap(channelData);
	}

	@Override
	public Channel createModel(Map<String, Object> entityDTO) {
		Channel channel = new Channel();
		
		if (entityDTO.get("id") != null) {
			channel.setId(Integer.parseInt((String) entityDTO.get("id")));
		}
		channel.setChannel((String) entityDTO.get("channel"));
//		channel.setRiver(Long.parseLong((String) entityDTO.get("river_id")));

		return channel;
	}

}
