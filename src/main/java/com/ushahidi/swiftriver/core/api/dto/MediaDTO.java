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

import com.ushahidi.swiftriver.core.model.Media;

/**
 * DTO mapping class for the Media model
 * @author ekala
 *
 */
public class MediaDTO extends EntityDTO<Media> {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> createDTO(Media entity) {
		Object[][] mediaData = {
				{"id", entity.getId()},
				{"url", entity.getUrl()},
				{"type", entity.getType()}
		};

		return ArrayUtils.toMap(mediaData);
	}

	@Override
	public Media createModel(Map<String, Object> entityDTO) {
		String url = (String) entityDTO.get("url");
		
		Media media = new Media();
		media.setUrl(url);
		media.setHash(EntityDTO.getMD5Hash(url));
		media.setType((String)entityDTO.get("type"));

		return media;
	}

}
