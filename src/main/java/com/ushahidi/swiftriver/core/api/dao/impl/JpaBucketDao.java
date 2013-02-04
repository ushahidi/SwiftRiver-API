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
package com.ushahidi.swiftriver.core.api.dao.impl;

import java.util.Collection;
import java.util.List;

import javax.persistence.Query;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.User;

/**
 * Hibernate class for buckets
 * @author ekala
 *
 */
@Repository
@Transactional
public class JpaBucketDao extends AbstractJpaDao<Bucket, Long> implements BucketDao {

	public JpaBucketDao() {
		super(Bucket.class);
	}

	/**
	 * @see BucketDao#getDrops(Long, int)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Drop> getDrops(Long bucketId, int dropCount) {
		String sqlQuery = "SELECT b.drops FROM Bucket b WHERE b.id = :bucketId";

		Query query = entityManager.createQuery(sqlQuery);
		query.setParameter("bucketId", bucketId);
		query.setMaxResults(dropCount);

		return (List<Drop>) query.getResultList();
	}

	/**
	 * @see BucketDao#addDrop(Long, Drop)
	 */
	public void addDrop(Long bucketId, Drop drop) {
		findById(bucketId).getDrops().add(drop);
	}

	/**
	 * @see BucketDao#addDrops(Long, Collection)
	 */
	public void addDrops(Long bucketId, Collection<Drop> drops) {
		findById(bucketId).getDrops().addAll(drops);
	}

	/**
	 * @see BucketDao#removeDrop(Long, Drop)
	 */
	public void removeDrop(Long bucketId, Drop drop) {		
		findById(bucketId).getDrops().remove(drop);
	}

	/**
	 * @see BucketDao#removeDrops(Long, Collection)
	 */
	public void removeDrops(Long bucketId, Collection<Drop> drops) {
		findById(bucketId).getDrops().removeAll(drops);
	}

	/**
	 * @see {@link BucketDao#addCollaborator(Long, User, boolean)}
	 */
	public void addCollaborator(Long bucketId, User user, boolean readOnly) {
		// TODO Auto-generated method stub

	}

	/**
	 * @see BucketDao#getCollaborators(Long)
	 */
	@SuppressWarnings("unchecked")
	public Collection<User> getCollaborators(Long bucketId) {
		String hql = "SELECT b.collaborators FROM Bucket b WHERE b.id = ?1";
		return (List<User>) entityManager.createQuery(hql).setParameter(1, bucketId).getResultList();
	}

	/**
	 * @see BucketDao#removeCollaborator(Long, User)
	 */
	public void removeCollaborator(Long bucketId, User user) {
		findById(bucketId).getCollaborators().remove(user);
	}

}
