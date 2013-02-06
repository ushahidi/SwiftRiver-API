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

import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.utils.SwiftRiverUtils;

public class LinkDTO extends AbstractDTO<Link> {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> createMapFromEntity(Link entity) {
		Object[][] linkData = {
				{"id", entity.getId()},
				{"url", entity.getUrl()}
		};

		return ArrayUtils.toMap(linkData);
	}

	@Override
	public Link createEntityFromMap(Map<String, Object> map) {
		// Get the URL for the link
		String url = (String) map.get("url");

		Link link = new Link();
		link.setUrl(url);
		link.setHash(SwiftRiverUtils.getMD5Hash(url));
		
		return link;
	}

	@Override
	protected String[] getValidationKeys() {
		// TODO Auto-generated method stub
		return new String[]{};
	}

	@Override
	protected void copyFromMap(Link target, Map<String, Object> source) {
		// TODO Auto-generated method stub
		
	}

}
