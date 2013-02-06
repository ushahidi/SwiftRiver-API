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

import com.ushahidi.swiftriver.core.model.RiverCollaborator;

/**
 * DTO mapping class for river collaborators
 * @author ekala
 *
 */
public class RiverCollaboratorDTO extends AbstractDTO<RiverCollaborator> {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> createMapFromEntity(RiverCollaborator entity) {
		Map<String, Object> accountMap = new AccountDTO().createMapFromEntity(entity.getAccount());

		Object[][] collaboratorData = {
				{"id", entity.getId()},
				{"active", entity.isActive()},
				{"read_only", entity.isReadOnly()},
				{"account", accountMap}
		};

		return ArrayUtils.toMap(collaboratorData);
	}

	@Override
	public RiverCollaborator createEntityFromMap(Map<String, Object> map) {
		RiverCollaborator collaborator = new RiverCollaborator();

		collaborator.setActive((Boolean) map.get("active"));
		collaborator.setReadOnly((Boolean) map.get("read_only"));

		return collaborator;
	}

	@Override
	protected String[] getValidationKeys() {
		return new String[]{"active", "read_only"};
	}

	@Override
	protected void copyFromMap(RiverCollaborator target,
			Map<String, Object> source) {
		
		RiverCollaborator dummy = createEntityFromMap(source);
		
		target.setActive(dummy.isActive());
		target.setReadOnly(dummy.isReadOnly());
	}

}
