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
package com.ushahidi.swiftriver.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ushahidi.swiftriver.dao.MediaDAO;
import com.ushahidi.swiftriver.dao.SwiftRiverDAO;
import com.ushahidi.swiftriver.model.Media;
import com.ushahidi.swiftriver.service.MediaService;

/**
 * Service class for media
 * @author ekala
 *
 */
@Service
public class MediaServiceImpl extends AbstractServiceImpl<Media, Long> implements MediaService {

	@Autowired
	private MediaDAO mediaDAO;

	public void setMediaDAO(MediaDAO mediaDAO) {
		this.mediaDAO = mediaDAO;
	}

	public Media findByHash(String hash) {
		return mediaDAO.findByHash(hash);
	}

	public SwiftRiverDAO<Media, Long> getServiceDAO() {
		return mediaDAO;
	}

}
