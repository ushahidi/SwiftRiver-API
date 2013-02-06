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

import com.ushahidi.swiftriver.core.model.Identity;
import com.ushahidi.swiftriver.core.utils.SwiftRiverUtils;

public class IdentityDTO extends AbstractDTO<Identity> {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> createMapFromEntity(Identity entity) {
		Object[][] identityData= { 
				{"id", entity.getId()},
				{"name", entity.getName()},
				{"avatar", entity.getAvatar()}
		};

		return ArrayUtils.toMap(identityData);
	}

	@Override
	public Identity createEntityFromMap(Map<String, Object> map) {
		Identity identity = new Identity();
		if (map.get("id") != null) {
			identity.setId(Long.parseLong((String) map.get("id")));
		}
		
		// Get the origin ID and channel
		String originId = (String) map.get("identity_orig_id");
		String channel = (String) map.get("channel");
		
		// Generate the hash for the identity
		String identityHash = SwiftRiverUtils.getMD5Hash(channel, originId);

		identity.setHash(identityHash);
		identity.setChannel(channel);
		identity.setOriginId(originId);
		identity.setUsername((String) map.get("identity_username"));
		identity.setName((String) map.get("identity_name"));
		identity.setAvatar((String) map.get("identity_avatar"));

		return identity;
	}

	@Override
	protected String[] getValidationKeys() {
		// TODO Auto-generated method stub
		return new String[]{};
	}

	@Override
	protected void copyFromMap(Identity target, Map<String, Object> source) {
		// TODO Auto-generated method stub
		
	}

}
