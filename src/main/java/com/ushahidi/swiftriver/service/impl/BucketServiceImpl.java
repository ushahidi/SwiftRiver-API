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

import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
	@Override
	public SwiftRiverDAO<Bucket, Long> getDAO() {
		return bucketDAO;
	}

	@Override
	public Collection<Drop> getDrops(Long bucketId, Map<Object, Object>... params) {
		return bucketDAO.getDrops(bucketId, params);
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
	public Collection<User> getCollaborators(Bucket bucket) {
		return bucketDAO.getCollaborators(bucket);
	}

	@Override
	public void removeCollaborator(Long bucketId, User user) {
		bucketDAO.removeCollaborator(bucketId, user);
	}

}
