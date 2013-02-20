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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketCollaborator;
import com.ushahidi.swiftriver.core.model.BucketDrop;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Identity;
import com.ushahidi.swiftriver.core.model.Link;

/**
 * Repository class for buckets
 * @author ekala
 *
 */
@Repository
public class JpaBucketDao extends AbstractJpaDao implements BucketDao {

	final static Logger LOG = LoggerFactory.getLogger(JpaBucketDao.class);
	
	@Autowired
	private DropDao dropDao;
	
	/* (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDao#update(com.ushahidi.swiftriver.core.model.Bucket)
	 */
	public Bucket update(Bucket bucket) {
		return em.merge(bucket);
	}
	
	/* (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDao#save(com.ushahidi.swiftriver.core.model.Bucket)
	 */
	public Bucket save(Bucket bucket) {
		em.persist(bucket);
		return bucket;
	}
	
	/* (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDao#delete(com.ushahidi.swiftriver.core.model.Bucket)
	 */
	public void delete(Bucket bucket) {
		em.remove(bucket);
	}
	
	/* (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDao#findById(long)
	 */
	public Bucket findById(long id) {
		Bucket bucket = em.find(Bucket.class, id);
		return bucket;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDao#addDrop(com.ushahidi.swiftriver.core.model.Bucket, long)
	 */
	public boolean addDrop(Bucket bucket, long dropId) {
		Drop drop = dropDao.findById(dropId);
		if (drop == null) {
			return false;
		}
		
		BucketDrop bucketDrop = new BucketDrop();
		bucketDrop.setDrop(drop);
		bucketDrop.setBucket(bucket);
		bucketDrop.setDateAdded(new Date());
		
		this.em.persist(bucketDrop);

		return true;
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
		collaborator.setDateAdded(new Date());

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

		return (List<BucketCollaborator>)query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDao#findCollaborator(com.ushahidi.swiftriver.core.model.Bucket, com.ushahidi.swiftriver.core.model.Account)
	 */
	@SuppressWarnings("unchecked")
	public BucketCollaborator findCollaborator(Bucket bucket, Account account) {
		String sql = "FROM BucketCollaborator bc WHERE bc.bucket =:bucket AND bc.account = :account";
		
		Query query = em.createQuery(sql);
		query.setParameter("bucket", bucket);
		query.setParameter("account", account);

		List<BucketCollaborator> results = (List<BucketCollaborator>) query.getResultList();
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

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDao#getDrops(java.lang.Long, com.ushahidi.swiftriver.core.model.Account, java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public List<Drop> getDrops(Long bucketId, Account account, Map<String, Object> requestParams) {
		String sql = "SELECT `droplets`.`id` AS `id`, `buckets_droplets`.`id` AS `sort_id`, `droplet_title`, ";
		sql += "`droplet_content`, `droplets`.`channel`, `identities`.`id` AS `identity_id`, `identity_name`, ";
		sql += "`identity_avatar`, `droplets`.`droplet_date_pub`, `droplet_orig_id`, ";
		sql += "`user_scores`.`score` AS `user_score`, `links`.`id` AS `original_url_id`, ";
		sql += "`links`.`url` AS `original_url`, `comment_count` ";
		sql += "FROM `buckets_droplets` ";
		sql += "INNER JOIN `droplets` ON (`buckets_droplets`.`droplet_id` = `droplets`.`id`) ";
		sql += "INNER JOIN `identities` ON (droplets.identity_id = `identities`.`id`) ";
		sql += "LEFT JOIN `droplet_scores` AS `user_scores` ON (`user_scores`.`droplet_id` = `droplets`.`id` AND `user_scores`.`user_id` = :userId) ";
		sql += "LEFT JOIN `links` ON (`droplets`.`original_url` = `links`.`id`) ";
		sql += "WHERE `buckets_droplets`.`droplet_date_added` > '0000-00-00 00:00:00' ";
		sql += "AND `buckets_droplets`.`bucket_id` = :bucketId ";
		
		// Check for channel parameter
		if (requestParams.containsKey("channels")) {
			sql += "AND droplets.channel IN :channels ";
		}
		
		if (requestParams.containsKey("photos")) {
			sql += "AND droplets.droplet_image > 0 ";
		}

		sql += "ORDER BY `buckets_droplets`.`droplet_date_added` DESC ";
		
		Integer dropCount = (Integer) requestParams.get("count");

		Query query = this.em.createNativeQuery(sql);
		query.setParameter("bucketId", bucketId);
		query.setParameter("userId", account.getId());

		if (requestParams.containsKey("channels")) {
			List<String> channels = (List<String>) requestParams.get("channels");
			query.setParameter("channels", channels);
		}
		
		query.setMaxResults(dropCount);
		List<Drop> drops = new ArrayList<Drop>();

		for (Object row: query.getResultList()) {
			Object[] rowArray = (Object[]) row;

			Drop drop = new Drop();
			
			// Set the drop properties
			drop.setId(((BigInteger)rowArray[0]).longValue());
			drop.setTitle((String) rowArray[2]);
			drop.setContent((String) rowArray[3]);
			drop.setChannel((String) rowArray[4]);
			
			Identity identity = new Identity();
			identity.setId(((BigInteger) rowArray[5]).longValue());
			identity.setName((String) rowArray[6]);
			identity.setAvatar((String) rowArray[7]);

			drop.setIdentity(identity);

			drop.setDatePublished((Date)rowArray[8]);
			drop.setOriginalId((String) rowArray[9]);
			
			if (rowArray[11] != null) {
				Link originalUrl = new Link();
				originalUrl.setId(((BigInteger) rowArray[11]).longValue());
				originalUrl.setUrl((String) rowArray[12]);
			}

			drop.setCommentCount((Integer) rowArray[13]);

			drops.add(drop);
		}
		
		if (!drops.isEmpty()) {
			// Populate the metadata
			dropDao.populateMetadata(drops, account);
		}

		return drops;
	}

	/**
	 * @see {@link BucketDao#deleteDrop(Long, Long)}
	 */
	public boolean deleteDrop(Long id, Long dropId) {
		String sql = "DELETE FROM buckets_droplets " +
				"WHERE bucket_id = :bucketId " + 
				"AND droplet_id = :dropId";
		
		Query query = this.em.createNativeQuery(sql);
		query.setParameter("bucketId", id);
		query.setParameter("dropId", dropId);

		return query.executeUpdate() == 1;
	}

	/**
	 * @see {@link BucketDao#findBucketByName(Account, String)}
	 */
	@SuppressWarnings("unchecked")
	public Bucket findBucketByName(Account account, String bucketName) {
		String sql = "FROM Bucket b WHERE account = :account AND name = :name";
		Query query = this.em.createQuery(sql);
		query.setParameter("account", account);
		query.setParameter("name", bucketName);
		
		List<Bucket> results = (List<Bucket>) query.getResultList();
		return results.isEmpty() ? null : results.get(0);
	}

}
