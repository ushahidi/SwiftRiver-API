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

import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketCollaborator;
import com.ushahidi.swiftriver.core.model.Drop;

/**
 * Repository class for buckets
 * @author ekala
 *
 */
@Repository
public class JpaBucketDao extends AbstractJpaDao<Bucket, Long> implements BucketDao {

	public JpaBucketDao() {
		super(Bucket.class);
	}

	/**
	 * @see BucketDao#getDrops(Long, int)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Drop> getDrops(Long bucketId, int dropCount) {
		String sqlQuery = "SELECT b.drops FROM Bucket b WHERE b.id = ?1";

		Query query = entityManager.createQuery(sqlQuery);
		query.setParameter(1, bucketId);
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
	 * @see BucketDao#addCollaborator(Bucket, Account, boolean)
	 */
	public BucketCollaborator addCollaborator(Bucket bucket, Account account, boolean readOnly) {
		BucketCollaborator collaborator = new BucketCollaborator();
		collaborator.setBucket(bucket);
		collaborator.setAccount(account);
		collaborator.setReadOnly(readOnly);

		bucket.getCollaborators().add(collaborator);

		this.entityManager.persist(collaborator);
		
		return collaborator;
	}

	/**
	 * @see BucketDao#getCollaborators(long)
	 */
	@SuppressWarnings("unchecked")
	public List<BucketCollaborator> getCollaborators(long bucketId) {
		String hql = "SELECT b.collaborators FROM Bucket b WHERE b.id = ?1";
		Query query = entityManager.createQuery(hql);
		query.setParameter(1, bucketId);

		return (List<BucketCollaborator>)query.getResultList();
	}

	/**
	 * @see {@link BucketDao#findCollaborator(Long, Long)}
	 */
	@SuppressWarnings("unchecked")
	public BucketCollaborator findCollaborator(Long id, Long accountId) {
		String sql = "FROM BucketCollaborator bc WHERE bc.bucket.id =:bucketId AND bc.account.id = :accountId";
		
		Query query = entityManager.createQuery(sql);
		query.setParameter("bucketId", id);
		query.setParameter("accountId", accountId);

		List<BucketCollaborator> results = (List<BucketCollaborator>) query.getResultList();
		return results.isEmpty() ? null : results.get(0);
	}

	/**
	 * @see {@link BucketDao#updateCollaborator(BucketCollaborator)}
	 */
	public void updateCollaborator(BucketCollaborator collaborator) {
		this.entityManager.merge(collaborator);
	}

	/**
	 * @see {@link BucketDao#deleteCollaborator(BucketCollaborator)}
	 */
	public void deleteCollaborator(BucketCollaborator collaborator) {
		this.entityManager.remove(collaborator);
	}

}
