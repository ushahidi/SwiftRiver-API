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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.api.dao.JpaDao;
import com.ushahidi.swiftriver.core.api.dto.BucketDTO;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.Drop;

/**
 * Service class for buckets
 * @author ekala
 *
 */
@Service
public class BucketService {
	
	@Autowired
	private BucketDao bucketDao;
	
	/* Logger */
	final static Logger logger = LoggerFactory.getLogger(BucketService.class);

	public void setBucketDao(BucketDao bucketDao) {
		this.bucketDao = bucketDao;
	}

	public JpaDao<Bucket, Long> getServiceDao() {
		return bucketDao;
	}
	
	/**
	 * Creates and returns a new bucket
	 * @param bucketData
	 * @return
	 */
	public Map<String, Object> createBucket(Map<String, Object> bucketData) {
		BucketDTO bucketDTO = new BucketDTO();
		
		Bucket bucket = bucketDao.save(bucketDTO.createEntityFromMap(bucketData));
		return bucketDTO.createMapFromEntity(bucket);
	}

	/**
	 * Gets and returns a single bucket
	 * 
	 * @param id
	 * @return
	 */
	public Map<String, Object> getBucket(long id) {
		Bucket bucket = bucketDao.findById(id);
		
		// Verify that the bucket exists
		if (bucket == null) 
			return null;
		
		return new BucketDTO().createMapFromEntity(bucket);
	}

	public ArrayList<Map<String, Object>> getDrops(Long bucketId, int dropCount) {
		ArrayList<Map<String, Object>> dropsList = new ArrayList<Map<String,Object>>();

		
		return dropsList;
	}

	public void addDrop(long bucketId, Drop drop) {
		throw new UnsupportedOperationException();
	}

	public void addDrops(long bucketId, Collection<Drop> drops) {
		throw new UnsupportedOperationException();
	}

	public void removeDrop(Long bucketId, Drop drop) {
		throw new UnsupportedOperationException();
	}

	public void removeDrops(long bucketId, Collection<Drop> drops) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets and returns the list of users collaborating on the bucket specified
	 * by bucketId
	 * 
	 * @param bucketId
	 * @return
	 */
	@Transactional
	public List<Map<String, Object>> getCollaborators(long bucketId) {
		throw new UnsupportedOperationException();
	}

}
