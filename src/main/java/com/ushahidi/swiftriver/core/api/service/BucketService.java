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
import com.ushahidi.swiftriver.core.api.dao.SwiftRiverDao;
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
	private BucketDao bucketDAO;
	
	/* Logger */
	private static Logger logger = Logger.getLogger(BucketService.class);

	public void setBucketDAO(BucketDao bucketDAO) {
		this.bucketDAO = bucketDAO;
	}

	public SwiftRiverDao<Bucket, Long> getServiceDAO() {
		return bucketDAO;
	}

	public Map<String, Object> getBucket(Long id) {
		Bucket bucket = bucketDAO.findById(id);
		
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

		// TODO: Fetch drops and covert them to a map

		return dropsList;
	}

	public void addDrop(Long bucketId, Drop drop) {
		bucketDAO.addDrop(bucketId, drop);
	}

	public void addDrops(Long bucketId, Collection<Drop> drops) {
		bucketDAO.addDrops(bucketId, drops);
	}

	public void removeDrop(Long bucketId, Drop drop) {
		bucketDAO.removeDrop(bucketId, drop);
	}

	public void removeDrops(Long bucketId, Collection<Drop> drops) {
		bucketDAO.removeDrops(bucketId, drops);
	}

	public void addCollaborator(Long bucketId, User user, boolean readOnly) {
		bucketDAO.addCollaborator(bucketId, user, readOnly);
	}

	public ArrayList<Map<String, Object>> getCollaborators(Bucket bucket) {
		ArrayList<Map<String, Object>> collaborators = new ArrayList<Map<String,Object>>();

		return collaborators;
	}

	public void removeCollaborator(Long bucketId, User user) {
		bucketDAO.removeCollaborator(bucketId, user);
	}

}
