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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ushahidi.swiftriver.dao.BucketDAO;
import com.ushahidi.swiftriver.dao.SwiftRiverDAO;
import com.ushahidi.swiftriver.model.Bucket;
import com.ushahidi.swiftriver.model.Drop;
import com.ushahidi.swiftriver.model.User;
import com.ushahidi.swiftriver.service.BucketService;

/**
 * Service class for buckets
 * @author ekala
 *
 */
@Service
public class BucketServiceImpl extends AbstractServiceImpl<Bucket, Long> implements BucketService {
	
	@Autowired
	private BucketDAO bucketDAO;

	public void setBucketDAO(BucketDAO bucketDAO) {
		this.bucketDAO = bucketDAO;
	}

	public SwiftRiverDAO<Bucket, Long> getServiceDAO() {
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

	public ArrayList<String> getDrops(Long bucketId, Map<Object, Object>... params) {
		List<Drop> drops = (List<Drop>) bucketDAO.getDrops(bucketId, params);
		
		ArrayList<String> dropsList = new ArrayList<String>();
		for (Drop drop: drops) {
			dropsList.add(drop.toString());
		}
		return dropsList;
	}

	@Override
	public void addDrop(Long bucketId, Drop drop) {
		bucketDAO.addDrop(bucketId, drop);
	}

	@Override
	public void addDrops(Long bucketId, Collection<Drop> drops) {
		bucketDAO.addDrops(bucketId, drops);
	}

	@Override
	public void removeDrop(Long bucketId, Drop drop) {
		bucketDAO.removeDrop(bucketId, drop);
	}

	@Override
	public void removeDrops(Long bucketId, Collection<Drop> drops) {
		bucketDAO.removeDrops(bucketId, drops);
	}

	@Override
	public void addCollaborator(Long bucketId, User user, boolean readOnly) {
		bucketDAO.addCollaborator(bucketId, user, readOnly);
	}

	@Override
	public Map<String, Object> getCollaborators(Bucket bucket) {
		List<User> collaborators = (List<User>) bucketDAO.getCollaborators(bucket);
		return null;
	}

	@Override
	public void removeCollaborator(Long bucketId, User user) {
		bucketDAO.removeCollaborator(bucketId, user);
	}

}
