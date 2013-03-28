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
import javax.persistence.TypedQuery;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketCollaborator;
import com.ushahidi.swiftriver.core.model.BucketComment;
import com.ushahidi.swiftriver.core.model.BucketDrop;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.DropSource;
import com.ushahidi.swiftriver.core.model.Identity;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.util.TextUtil;

/**
 * Repository class for buckets
 * 
 * @author ekala
 * 
 */
@Repository
public class JpaBucketDao extends AbstractJpaDao<Bucket> implements BucketDao {

	final static Logger LOG = LoggerFactory.getLogger(JpaBucketDao.class);
	
	@Autowired
	private DropDao dropDao;
	
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.impl.AbstractJpaDao#create(java.
	 * lang.Object)
	 */
	@Override
	public Bucket create(Bucket bucket) {
		bucket.setBucketNameCanonical(TextUtil.getURLSlug(bucket.getName()));
		return super.create(bucket);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.impl.AbstractJpaDao#update(java.
	 * lang.Object)
	 */
	@Override
	public Bucket update(Bucket bucket) {
		bucket.setBucketNameCanonical(TextUtil.getURLSlug(bucket.getName()));
		return super.update(bucket);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDao#addDrop(com.ushahidi.swiftriver.core.model.Bucket, long)
	 */
	public boolean addDrop(Bucket bucket, Drop drop) {		
		BucketDrop bucketDrop = new BucketDrop();
		bucketDrop.setDrop(drop);
		bucketDrop.setBucket(bucket);
		bucketDrop.setDateAdded(new Date());
		bucketDrop.setVeracity(1L);
		
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
	 * @see BucketDao#addCollaborator(Bucket, Account, boolean)
	 */
	public BucketCollaborator addCollaborator(Bucket bucket, Account account,
			boolean readOnly) {
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

		return (List<BucketCollaborator>) query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDao#findCollaborator(com.ushahidi.swiftriver.core.model.Bucket, com.ushahidi.swiftriver.core.model.Account)
	 */
	public BucketCollaborator findCollaborator(Long bucketId, Long accountId) {
		String sql = "FROM BucketCollaborator bc "
				+ "WHERE bc.account.id = :accountId "
				+ "AND bc.bucket.id =:riverId";

		Query query = this.em.createQuery(sql);
		query.setParameter("accountId", accountId);
		query.setParameter("riverId", bucketId);

		BucketCollaborator collaborator = null;
		try {
			collaborator = (BucketCollaborator) query.getSingleResult();
		} catch (Exception e) {
			// Do nothing;
		}
		
		return collaborator;
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
	public List<Drop> getDrops(Long bucketId, Account account, Map<String, Object> requestParams) {
		String sql = "SELECT `buckets_droplets`.`id` AS `id`, `droplet_title`, ";
		sql += "`droplet_content`, `droplets`.`channel`, `identities`.`id` AS `identity_id`, `identity_name`, ";
		sql += "`identity_avatar`, `droplets`.`droplet_date_pub`, `droplet_orig_id`, ";
		sql += "`user_scores`.`score` AS `user_score`, `links`.`id` AS `original_url_id`, ";
		sql += "`links`.`url` AS `original_url`, `comment_count`, `bucket_droplets_read`.`buckets_droplets_id` AS `drop_read` ";
		sql += "FROM `buckets_droplets` ";
		sql += "INNER JOIN `droplets` ON (`buckets_droplets`.`droplet_id` = `droplets`.`id`) ";
		sql += "INNER JOIN `identities` ON (droplets.identity_id = `identities`.`id`) ";
		sql += "LEFT JOIN `droplet_scores` AS `user_scores` ON (`user_scores`.`droplet_id` = `droplets`.`id` AND `user_scores`.`user_id` = :userId) ";
		sql += "LEFT JOIN `links` ON (`droplets`.`original_url` = `links`.`id`) ";
		sql += "LEFT JOIN `bucket_droplets_read` ON (`bucket_droplets_read`.`buckets_droplets_id` = `buckets_droplets`.`id` AND `bucket_droplets_read`.`account_id` = :accountId) ";
		sql += "WHERE `buckets_droplets`.`droplet_date_added` > '0000-00-00 00:00:00' ";
		sql += "AND `buckets_droplets`.`bucket_id` = :bucketId ";
		
		// Check for channel parameter
		if (requestParams.containsKey("channels")) {
			sql += "AND `droplets`.`channel` IN (:channels) ";
		}
		
		if (requestParams.containsKey("photos")) {
			sql += "AND `droplets`.`droplet_image` > 0";
		}
		
		// Check for sinceId
		if (requestParams.containsKey("sinceId")) {
			sql += " AND `buckets_droplets`.`id` > :sinceId";
		}
		
		// Check for maxId
		if (requestParams.containsKey("maxId")) {
			sql += " AND `buckets_droplets`.`id` <= :maxId";
		}

		// dateFrom and dateTo
		if (requestParams.containsKey("dateFrom")) {
			sql += " AND `buckets_droplets`.`droplet_date_added` >= :dateFrom";
		}
		
		if (requestParams.containsKey("dateTo")) {
			sql += " AND `buckets_droplets`.`droplet_date_added` <= :dateTo";
		}
		

		Integer dropCount = (Integer) requestParams.get("count");
		Integer page = (Integer) requestParams.get("page");

		sql += " ORDER BY `buckets_droplets`.`droplet_date_added` DESC ";
		sql += String.format("LIMIT %d OFFSET %d", dropCount, ((page - 1) * dropCount));

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("bucketId", bucketId);
		params.addValue("userId", account.getOwner().getId());
		params.addValue("accountId", account.getId());
		
		String[] paramKeys = {"channels", "maxId", "sinceId", "dateFrom", "dateTo"};
		for (String key: paramKeys) {
			if (requestParams.containsKey(key)) {
				params.addValue(key, requestParams.get(key));
			}
		}

		List<Drop> drops = new ArrayList<Drop>();

		for (Map<String, Object> row: jdbcTemplate.queryForList(sql, params)) {
			Drop drop = new Drop();
			
			// Set the drop properties
			drop.setId(((BigInteger) row.get("id")).longValue());
			drop.setTitle((String) row.get("droplet_title"));
			drop.setContent((String) row.get("droplet_content"));
			drop.setChannel((String) row.get("channel"));
			
			Identity identity = new Identity();
			identity.setId(((BigInteger) row.get("identity_id")).longValue());
			identity.setName((String) row.get("identity_name"));
			identity.setAvatar((String) row.get("identity_avatar"));

			drop.setIdentity(identity);

			drop.setDatePublished((Date) row.get("droplet_date_pub"));
			drop.setOriginalId((String) row.get("droplet_orig_id"));
			
			if (row.get("original_url_id") != null) {
				Link originalUrl = new Link();
				originalUrl.setId(((BigInteger) row.get("original_url_id")).longValue());
				originalUrl.setUrl((String) row.get("original_url"));
			}

			drop.setCommentCount((Integer) row.get("comment_count"));
			drop.setRead((Long) row.get("drop_read") != null);

			drops.add(drop);
		}
		
		if (!drops.isEmpty()) {
			// Populate the metadata
			dropDao.populateMetadata(drops, DropSource.BUCKET, account);
		}

		return drops;
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

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDao#findAll(java.util.List)
	 */
	@SuppressWarnings("unchecked")
	public List<Bucket> findAll(List<Long> bucketIds) {
		return (List<Bucket>) em.createQuery("FROM Bucket WHERE id IN :bucketIds")
				.setParameter("bucketIds", bucketIds)
				.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDao#findDrop(java.lang.Long, java.lang.Long)
	 */
	public BucketDrop findDrop(Long bucketId, Long dropId) {
		String sql = "FROM BucketDrop WHERE bucket.id = :bucketId AND drop.id = :dropId";
		
		TypedQuery<BucketDrop> query = em.createQuery(sql, BucketDrop.class);
		query.setParameter("bucketId", bucketId);
		query.setParameter("dropId", dropId);
		
		List<BucketDrop> bucketDrops = query.getResultList();
		
		return bucketDrops.isEmpty() ? null : bucketDrops.get(0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDao#addComment(com.ushahidi.swiftriver.core.model.Bucket, java.lang.String, com.ushahidi.swiftriver.core.model.Account)
	 */
	public BucketComment addComment(Bucket bucket, String commentText,
			Account account) {
		BucketComment bucketComment = new BucketComment();
		
		bucketComment.setBucket(bucket);
		bucketComment.setAccount(account);
		bucketComment.setCommentText(commentText);
		bucketComment.setDateAdded(new Date());
		
		this.em.persist(bucketComment);
		
		return bucketComment;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDao#decreaseDropCount(com.ushahidi.swiftriver.core.model.Bucket)
	 */
	public void decreaseDropCount(Bucket bucket) {
		int dropCount = bucket.getDropCount() - 1;
		bucket.setDropCount(dropCount);
		this.update(bucket);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDao#increaseDropCount(com.ushahidi.swiftriver.core.model.Bucket)
	 */
	public void increaseDropCount(Bucket bucket) {
		int dropCount = bucket.getDropCount() + 1;
		bucket.setDropCount(dropCount);
		this.update(bucket);
	}

}
