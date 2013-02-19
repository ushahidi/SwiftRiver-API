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
import java.util.Map;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketCollaborator;
import com.ushahidi.swiftriver.core.model.Drop;

/**
 * Repository class for buckets
 * 
 * @author ekala
 * 
 */
@Repository
public class JpaBucketDao extends AbstractJpaDao<Bucket> implements BucketDao {

	final static Logger LOG = LoggerFactory.getLogger(JpaBucketDao.class);

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
	public BucketCollaborator addCollaborator(Bucket bucket, Account account,
			boolean readOnly) {
		BucketCollaborator collaborator = new BucketCollaborator();
		collaborator.setBucket(bucket);
		collaborator.setAccount(account);
		collaborator.setReadOnly(readOnly);

		bucket.getCollaborators().add(collaborator);

		this.em.persist(collaborator);

		return collaborator;
	}

	/**
	 * @see BucketDao#getCollaborators(long)
	 */
	@SuppressWarnings("unchecked")
	public List<BucketCollaborator> getCollaborators(long bucketId) {
		String hql = "SELECT b.collaborators FROM Bucket b WHERE b.id = ?1";
		Query query = em.createQuery(hql);
		query.setParameter(1, bucketId);

		return (List<BucketCollaborator>) query.getResultList();
	}

	/**
	 * @see {@link BucketDao#findCollaborator(Long, Long)}
	 */
	@SuppressWarnings("unchecked")
	public BucketCollaborator findCollaborator(Long id, Long accountId) {
		String sql = "FROM BucketCollaborator bc WHERE bc.bucket.id =:bucketId AND bc.account.id = :accountId";

		Query query = em.createQuery(sql);
		query.setParameter("bucketId", id);
		query.setParameter("accountId", accountId);

		List<BucketCollaborator> results = (List<BucketCollaborator>) query
				.getResultList();
		return results.isEmpty() ? null : results.get(0);
	}

	/**
	 * @see {@link BucketDao#updateCollaborator(BucketCollaborator)}
	 */
	public void updateCollaborator(BucketCollaborator collaborator) {
		this.em.merge(collaborator);
	}

	/**
	 * @see {@link BucketDao#deleteCollaborator(BucketCollaborator)}
	 */
	public void deleteCollaborator(BucketCollaborator collaborator) {
		this.em.remove(collaborator);
	}

	/**
	 * @see {@link BucketDao#getDrops(Long, Map)}
	 */
	@SuppressWarnings("unchecked")
	public List<Drop> getDrops(Long bucketId, Map<String, Object> requestParams) {
		CriteriaBuilder cb = this.em.getCriteriaBuilder();
		CriteriaQuery<Drop> dropsQuery = cb.createQuery(Drop.class);

		Root<Drop> dropRoot = dropsQuery.from(Drop.class);
		Path<Long> dropId = dropRoot.get("id");

		// Join drops and buckets
		Root<Bucket> bucketRoot = dropsQuery.from(Bucket.class);
		ListJoin<Bucket, Drop> bucketDrops = bucketRoot.joinList("drops",
				JoinType.INNER);

		CriteriaQuery<Drop> bucketDropsQuery = dropsQuery.select(bucketDrops);

		// Apply the query parameters
		Predicate filterPredicates = cb.and(
				cb.equal(bucketDrops.get("id"), dropId),
				cb.equal(bucketRoot.get("id"), bucketId));

		// Check for since_id parameter
		if (requestParams.containsKey("since_id")) {
			Long sinceId = (Long) requestParams.get("since_id");
			filterPredicates = cb.and(filterPredicates, cb.gt(dropId, sinceId));
		}

		// Check for max_id parameter
		if (requestParams.containsKey("max_id")) {
			Long maxId = (Long) requestParams.get("max_id");
			filterPredicates = cb.and(filterPredicates, cb.le(dropId, maxId));
		}

		// Check for channels parameter
		if (requestParams.containsKey("channels")) {
			List<String> channelsList = (List<String>) requestParams
					.get("channels");
			filterPredicates = cb.and(filterPredicates,
					cb.in(dropRoot.get("channel")).value(channelsList));
		}

		// Apply the predicates and order the results by drop id in descending
		// order
		bucketDropsQuery.where(filterPredicates);
		bucketDropsQuery.orderBy(cb.desc(dropId));
		TypedQuery<Drop> resultsQuery = this.em.createQuery(bucketDropsQuery);

		Integer dropCount = (Integer) requestParams.get("count");
		resultsQuery.setMaxResults(dropCount);

		return resultsQuery.getResultList();
	}

	/**
	 * @see {@link BucketDao#deleteDrop(Long, Long)}
	 */
	public boolean deleteDrop(Long id, Long dropId) {
		String sql = "DELETE FROM buckets_droplets "
				+ "WHERE bucket_id = :bucketId " + "AND droplet_id = :dropId";

		Query query = this.em.createNativeQuery(sql);
		query.setParameter("bucketId", id);
		query.setParameter("dropId", dropId);

		return query.executeUpdate() > 0;
	}

	/**
	 * @see {@link BucketDao#findBucketByName(Account, String)}
	 */
	@SuppressWarnings("unchecked")
	public Bucket findBucketByName(Account account, String bucketName) {
		String jPQL = "FROM Bucket b WHERE account = :account AND name = :name";
		Query query = this.em.createQuery(jPQL);
		query.setParameter("account", account);
		query.setParameter("name", bucketName);

		List<Bucket> results = (List<Bucket>) query.getResultList();
		return results.isEmpty() ? null : results.get(0);
	}

}
