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
public class RiverDTO extends AbstractDTO<River> {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> createMapFromEntity(River entity) {
		List<Map<String, Object>> channelsList = new ArrayList<Map<String,Object>>();
		ChannelDTO channelDTO = new ChannelDTO();

		for (Channel channel: entity.getChannels()) {
			channelsList.add(channelDTO.createMapFromEntity(channel));
		}

		Object[][] riverData = {
				{"id", entity.getId()},
				{"name", entity.getName()},
				{"url", entity.getUrl()},
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
	public River createEntityFromMap(Map<String, Object> map) {
		River river = new River();
		if (map.get("id") != null) {
			String riverId = ((Integer) map.get("id")).toString();
			river.setId(Long.parseLong(riverId));
		}

		String riverName = (String) map.get("name");

		river.setName(riverName);
		river.setUrl(SwiftRiverUtils.getURLSlug(riverName));
		river.setActive((Boolean) map.get("active"));
		river.setRiverPublic((Boolean) map.get("public"));
		
		if (map.get("layout") != null) {
			river.setDefaultLayout((String) map.get("layout"));
		}
		return river;
	}

	@Override
	protected String[] getValidationKeys() {
		return new String[]{"name", "public"};
	}

	@Override
	protected void copyFromMap(River target, Map<String, Object> source) {
		River dummy = createEntityFromMap(source);
		
		target.setName(dummy.getName());
		target.setUrl(dummy.getUrl());
		target.setActive(dummy.isActive());
		target.setRiverPublic(dummy.isRiverPublic());
	}

}
