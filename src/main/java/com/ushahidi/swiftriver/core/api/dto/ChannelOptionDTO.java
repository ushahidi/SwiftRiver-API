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

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import com.ushahidi.swiftriver.core.model.ChannelOption;

/**
 * DTO mapping class for the ChannelOption model
 * @author ekala
 *
 */
public class ChannelOptionDTO extends EntityDTO<ChannelOption> {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> createDTO(ChannelOption entity) {
		Object[][] channelOptionData = {
				{"id", entity.getId()},
				{"key", entity.getKey()},
				{"value", entity.getValue()}
		};

		return ArrayUtils.toMap(channelOptionData);
	}

	@Override
	public ChannelOption createModel(Map<String, Object> entityDTO) {

		ChannelOption channelOption = new ChannelOption();
		if (entityDTO.get("id") != null) {
			channelOption.setId(Integer.parseInt((String)entityDTO.get("id")));
		}

		channelOption.setChannel(Integer.parseInt((String) entityDTO.get("channel_id")));
		channelOption.setKey((String) entityDTO.get("key"));
		channelOption.setValue((String) entityDTO.get("value"));
		
		return channelOption;
	}

}
