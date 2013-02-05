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

import com.ushahidi.swiftriver.core.model.Tag;
import com.ushahidi.swiftriver.core.utils.SwiftRiverUtils;

/**
 * DTO mapping class for the Tag model
 * @author ekala
 *
 */
public class TagDTO extends EntityDTO<Tag> {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> createDTO(Tag entity) {
		Object[][] tagData = {
				{"id", entity.getId()},
				{"tag", entity.getTag()},
				{"type", entity.getTagType()}
		};

		return ArrayUtils.toMap(tagData);
	}

	@Override
	public Tag createModel(Map<String, Object> entityDTO) {
		String tagName = (String) entityDTO.get("tag_name");
		String tagType = (String) entityDTO.get("tag_type");

		// Compute the hash for the tag
		String tagHash = SwiftRiverUtils.getMD5Hash(tagName, tagType);

		Tag tag = new Tag();
		tag.setTag(tagName);
		tag.setTagCanonical(tagName.toLowerCase());
		tag.setTagType(tagType);
		tag.setHash(tagHash);

		return tag;
	}

}
