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
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.utils.SwiftRiverUtils;

/**
 * DTO mapping class for Rivers
 * @author ekala
 *
 */
public class RiverDTO extends EntityDTO<River> {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> createDTO(River entity) {
		List<Map<String, Object>> channelsList = new ArrayList<Map<String,Object>>();
		ChannelDTO channelDTO = new ChannelDTO();

		for (Channel channel: entity.getChannels()) {
			channelsList.add(channelDTO.createDTO(channel));
		}

		Object[][] riverData = {
				{"id", entity.getId()},
				{"name", entity.getRiverName()},
				{"url", entity.getRiverNameUrl()},
				{"public", entity.isRiverPublic()},
				{"active", entity.isActive()},
				{"full", entity.isRiverFull()},
				{"drop_count", entity.getDropCount()},
				{"drop_quota", entity.getDropQuota()},
				{"date_added", entity.getDateAdded()},
				{"expiry_date", entity.getExpiryDate()},
				{"extension_count", entity.getExtensionCount()},
				{"channels", channelsList}
		};

		return ArrayUtils.toMap(riverData);
	}

	@Override
	public River createModel(Map<String, Object> entityDTO) {
		River river = new River();
		if (entityDTO.get("id") == null) {
			river.setId(Long.parseLong((String) entityDTO.get("id")));
		}

		String riverName = (String) entityDTO.get("name");

		river.setRiverName(riverName);
		river.setRiverNameUrl(SwiftRiverUtils.getURLSlug(riverName));
		river.setActive(Boolean.parseBoolean((String) entityDTO.get("active")));
		
		if (entityDTO.get("layout") != null) {
			river.setDefaultLayout((String) entityDTO.get("layout"));
		}
		return river;
	}

}
