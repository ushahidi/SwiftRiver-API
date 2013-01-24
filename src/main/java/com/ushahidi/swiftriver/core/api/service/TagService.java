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
package com.ushahidi.swiftriver.core.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ushahidi.swiftriver.core.api.dao.JpaDao;
import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.model.Tag;

/**
 * Service class for tags
 * @author ekala
 *
 */
@Service
public class TagService extends AbstractServiceImpl<Tag, Long> {

	@Autowired
	private TagDao tagDAO;

	public void setTagDAO(TagDao tagDAO) {
		this.tagDAO = tagDAO;
	}

	public JpaDao<Tag, Long> getServiceDao() {
		return tagDAO;
	}

	public Tag findByHash(String hash) {
		return tagDAO.findByHash(hash);
	}

	public Tag getTag(Long id) {
		return tagDAO.findById(id);
	}

}
