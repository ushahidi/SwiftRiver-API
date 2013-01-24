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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.api.dao.JpaDao;
import com.ushahidi.swiftriver.core.api.dto.DropDTO;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.User;

/**
 * Service class for buckets
 * @author ekala
 *
 */
@Service
public class BucketService extends AbstractServiceImpl<Bucket, Long> {
	
	@Autowired
	private BucketDao bucketDao;
	
	/* Logger */
	private static Logger logger = Logger.getLogger(BucketService.class);

	public void setBucketDAO(BucketDao bucketDAO) {
		this.bucketDao = bucketDAO;
	}

	public JpaDao<Bucket, Long> getServiceDao() {
		return bucketDao;
	}

	public Map<String, Object> getBucket(Long id) {
		Bucket bucket = bucketDao.findById(id);
		
		// Verify that the bucket exists
		if (bucket == null) {
			logger.debug("Could not find bucket with id " + id);
			return null;
		}
		
		// Deserialize the bucket data
		Gson gson = new Gson();
		Type type = new TypeToken<Map<String, Object>>(){}.getType();
		
		Map<String, Object> bucketDataMap = gson.fromJson(bucket.toString(), type);
		
		// Add the drops
		return bucketDataMap;
	}

	public ArrayList<Map<String, Object>> getDrops(Long bucketId, int dropCount) {
		ArrayList<Map<String, Object>> dropsList = new ArrayList<Map<String,Object>>();

		DropDTO dropDTO = new DropDTO();
		for (Drop drop: bucketDao.getDrops(bucketId, dropCount)) {
			dropsList.add(dropDTO.createDTO(drop));
		}

		return dropsList;
	}

	public void addDrop(Long bucketId, Drop drop) {
		bucketDao.addDrop(bucketId, drop);
	}

	public void addDrops(Long bucketId, Collection<Drop> drops) {
		bucketDao.addDrops(bucketId, drops);
	}

	public void removeDrop(Long bucketId, Drop drop) {
		bucketDao.removeDrop(bucketId, drop);
	}

	public void removeDrops(Long bucketId, Collection<Drop> drops) {
		bucketDao.removeDrops(bucketId, drops);
	}

	public void addCollaborator(Long bucketId, User user, boolean readOnly) {
		bucketDao.addCollaborator(bucketId, user, readOnly);
	}

	public ArrayList<Map<String, Object>> getCollaborators(Bucket bucket) {
		ArrayList<Map<String, Object>> collaborators = new ArrayList<Map<String,Object>>();

		return collaborators;
	}

	public void removeCollaborator(Long bucketId, User user) {
		bucketDao.removeCollaborator(bucketId, user);
	}

}
