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
package com.ushahidi.swiftriver.dao.hibernate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.dao.BucketDAO;
import com.ushahidi.swiftriver.model.Bucket;
import com.ushahidi.swiftriver.model.Drop;
import com.ushahidi.swiftriver.model.User;

/**
 * Hibernate class for buckets
 * @author ekala
 *
 */
@Repository("bucketDAO")
@Transactional
public class HibernateBucketDAO extends AbstractHibernateDAO<Bucket, Long> implements BucketDAO {

	public HibernateBucketDAO() {
		super(Bucket.class);
	}

	/**
	 * @see BucketDAO#getDrops(Long, Map...)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Drop> getDrops(Long bucketId, Map<Object, Object>... params) {
		String hql = "Select b.drops from Bucket b where b.id = ?";
		return (List<Drop>) hibernateTemplate.find(hql, bucketId);
	}

	/**
	 * @see BucketDAO#addDrop(Long, Drop)
	 */
	public void addDrop(Long bucketId, Drop drop) {
		getById(bucketId).getDrops().add(drop);
	}

	/**
	 * @see BucketDAO#addDrops(Long, Collection)
	 */
	public void addDrops(Long bucketId, Collection<Drop> drops) {
		getById(bucketId).getDrops().addAll(drops);
	}

	/**
	 * @see BucketDAO#removeDrop(Long, Drop)
	 */
	public void removeDrop(Long bucketId, Drop drop) {		
		getById(bucketId).getDrops().remove(drop);
	}

	/**
	 * @see BucketDAO#removeDrops(Long, Collection)
	 */
	public void removeDrops(Long bucketId, Collection<Drop> drops) {
		getById(bucketId).getDrops().removeAll(drops);
	}

	/**
	 * @see {@link BucketDAO#addCollaborator(Long, User, boolean)}
	 */
	public void addCollaborator(Long bucketId, User user, boolean readOnly) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see BucketDAO#getCollaborators(Bucket)
	 */
	@SuppressWarnings("unchecked")
	public Collection<User> getCollaborators(Bucket bucket) {
		// TODO Auto-generated method stub
		String hql = "Select b.collaborators from Bucket b where b = ?";
		return (List<User>)hibernateTemplate.find(hql, bucket);
	}

	/**
	 * @see BucketDAO#removeCollaborator(Long, User)
	 */
	public void removeCollaborator(Long bucketId, User user) {
		getById(bucketId).getCollaborators().remove(user);
	}

}
