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
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Identity;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverCollaborator;
import com.ushahidi.swiftriver.core.util.TextUtil;

@Repository
@Transactional(readOnly = true)
public class JpaRiverDao extends AbstractJpaDao<River> implements RiverDao {

	final Logger logger = LoggerFactory.getLogger(JpaRiverDao.class);

	@Autowired
	private DropDao dropsDao;

	private NamedParameterJdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.impl.AbstractJpaDao#create(java.
	 * lang.Object)
	 */
	@Override
	public River create(River river) {
		river.setRiverNameCanonical(TextUtil.getURLSlug(river.getRiverName()));
		return super.create(river);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.impl.AbstractJpaDao#update(java.
	 * lang.Object)
	 */
	@Override
	public River update(River river) {
		river.setRiverNameCanonical(TextUtil.getURLSlug(river.getRiverName()));
		return super.update(river);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.RiverDao#findByName(java.lang.String
	 * )
	 */
	@Override
	public River findByName(String name) {
		String canonicalRiverName = TextUtil.getURLSlug(name);
		String query = "SELECT r FROM River r WHERE r.riverNameCanonical = :river_name_canonical";

		River river = null;
		try {
			river = (River) em.createQuery(query)
					.setParameter("river_name_canonical", canonicalRiverName)
					.getSingleResult();
		} catch (NoResultException e) {
			// Do nothing
		}

		return river;
	}

	public List<Drop> getDrops(Long riverId, Long maxId, int page,
			int dropCount, List<String> channelList, List<Long> channelIds, Boolean isRead, Date dateFrom, Date dateTo, Account queryingAccount) {
		String sql = "SELECT `rivers_droplets`.`id` AS `id`, `droplet_title`, `droplet_content`, `droplets`.`channel`, ";
		sql += "`identities`.`id` AS `identity_id`, `identity_name`, `identity_avatar`, `rivers_droplets`.`droplet_date_pub`, `droplet_orig_id`, ";
		sql += "`user_scores`.`score` AS `user_score`, `links`.`id` as `original_url_id`, `links`.`url` AS `original_url`, `comment_count` ";
		sql += "FROM `rivers_droplets` ";
		sql += "INNER JOIN `droplets` ON (`rivers_droplets`.`droplet_id` = `droplets`.`id`) ";
		sql += "INNER JOIN `identities` ON (`droplets`.`identity_id` = `identities`.`id`) ";

		if (channelIds.size() > 0) {
			sql += "INNER JOIN `river_channels` ON (`rivers_droplets`.`channel_id` = `river_channels`.`id`) ";
		}

		sql += "LEFT JOIN `droplet_scores` AS `user_scores` ON (`user_scores`.`droplet_id` = droplets.id AND user_scores.user_id = :userId) ";
		sql += "LEFT JOIN `links` ON (`links`.`id` = `droplets`.`original_url`) ";
		
		if (isRead != null) {
			sql += "LEFT JOIN `account_read_drops` ON (`account_read_drops`.`droplet_id` = `rivers_droplets`.`droplet_id` AND `account_read_drops`.`account_id` = :accountId) ";
		}
		
		sql += "WHERE `rivers_droplets`.`droplet_date_pub` > '0000-00-00 00:00:00' ";
		sql += "AND `rivers_droplets`.`river_id` = :riverId ";
		sql += "AND `rivers_droplets`.`id` <= :maxId ";
		
		if (channelList.size() > 0) {
			sql += "AND `rivers_droplets`.`channel` IN (:channels) ";
		}

		if (channelIds.size() > 0) {
			sql += "AND `rivers_droplets`.`channel_id` IN (:channel_ids) ";
		}
		
		if (isRead != null) {
			if (isRead) {
				sql += "AND `account_read_drops`.`droplet_id` IS NOT NULL ";
			} else {
				sql += "AND `account_read_drops`.`droplet_id` IS NULL ";
			}
		}
		
		if (dateFrom != null) {
			sql += "AND `rivers_droplets`.`droplet_date_pub` >= :date_from ";
		}
		
		if (dateTo != null) {
			sql += "AND `rivers_droplets`.`droplet_date_pub` <= :date_to ";
		}

		sql += "ORDER BY `rivers_droplets`.`droplet_date_pub` DESC ";
		sql += "LIMIT " + dropCount + " OFFSET " + dropCount * (page - 1);

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", queryingAccount.getOwner().getId());
		params.addValue("accountId", queryingAccount.getId());
		params.addValue("riverId", riverId);
		params.addValue("maxId", maxId);
		
		if (channelList.size() > 0) {
			params.addValue("channels", channelList);
		}

		if (channelIds.size() > 0) {
			params.addValue("channel_ids", channelIds);
		}
		
		if (dateFrom != null)
		{
			params.addValue("date_from", dateFrom);
		}
		
		if (dateTo != null)
		{
			params.addValue("date_to", dateTo);
		}
		
		List<Map<String, Object>> results = this.jdbcTemplate.queryForList(sql, params);

		return formatDrops(results, queryingAccount);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.RiverDao#getDropsSince(java.lang
	 * .Long, java.lang.Long, int, com.ushahidi.swiftriver.core.model.Account)
	 */
	@Override
	public List<Drop> getDropsSince(Long riverId, Long sinceId, int dropCount,
			List<String> channelList, List<Long> channelIds, Boolean isRead, Date dateFrom, Date dateTo, Account queryingAccount) {
		String sql = "SELECT `rivers_droplets`.`id` AS `id`, `droplet_title`, `droplet_content`, ";
		sql += "`droplets`.`channel`, `identities`.`id` `identity_id`, `identity_name`, ";
		sql += "`identity_avatar`, `rivers_droplets`.`droplet_date_pub`, `droplet_orig_id`, ";
		sql += "`user_scores`.`score` AS `user_score`, `links`.`id` AS `original_url_id`, ";
		sql += "`links`.`url` AS `original_url`, `comment_count` ";
		sql += "FROM `droplets` ";
		sql += "INNER JOIN `rivers_droplets` ON (`rivers_droplets`.`droplet_id` = `droplets`.`id`) ";
		sql += "INNER JOIN `identities` ON (`droplets`.`identity_id` = `identities`.`id`) ";
		
		if (channelIds.size() > 0) {
			sql += "INNER JOIN `river_channels` ON (`rivers_droplets`.`channel_id` = `river_channels`.`id`) ";
		}
		
		sql += "LEFT JOIN `droplet_scores` AS `user_scores` ON (`user_scores`.`droplet_id` = droplets.id AND user_scores.user_id = :userId) ";
		sql += "LEFT JOIN `links` ON (`links`.`id` = `droplets`.`original_url`) ";
		
		if (isRead != null) {
			sql += "LEFT JOIN `account_read_drops` ON (`account_read_drops`.`droplet_id` = `rivers_droplets`.`droplet_id` AND `account_read_drops`.`account_id` = :accountId) ";
		}
		
		sql += "WHERE `rivers_droplets`.`river_id` = :riverId AND `rivers_droplets`.`id` > :sinceId ";
		
		if (channelList.size() > 0) {
			sql += "AND `rivers_droplets`.`channel` IN (:channels) ";
		}
		
		if (channelIds.size() > 0) {
			sql += "AND `rivers_droplets`.`channel_id` IN (:channels) ";
		}
		
		if (isRead != null) {
			if (isRead) {
				sql += "AND `account_read_drops`.`droplet_id` IS NOT NULL ";
			} else {
				sql += "AND `account_read_drops`.`droplet_id` IS NULL ";
			}
		}
		
		sql += "ORDER BY `rivers_droplets`.`id` ASC LIMIT " + dropCount;

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("userId", queryingAccount.getOwner().getId());
		params.addValue("accountId", queryingAccount.getId());
		params.addValue("riverId", riverId);
		params.addValue("sinceId", sinceId);
		
		if (channelList.size() > 0) {
			params.addValue("channels", channelList);
		}
		
		if (channelIds.size() > 0) {
			params.addValue("channels", channelIds);
		}
		
		List<Map<String, Object>> results = this.jdbcTemplate.queryForList(sql, params);

		return formatDrops(results, queryingAccount);
	}

	/**
	 * Generate a Drop entity list for the given drop result map.
	 * 
	 * @param results
	 * @return
	 */
	private List<Drop> formatDrops(List<Map<String, Object>> results,
			Account queryingAccount) {
		List<Drop> drops = new ArrayList<Drop>();
		for (Map<String, Object> result : results) {
			Drop drop = new Drop();

			// Set drop details
			drop.setId(((BigInteger) result.get("id")).longValue());
			drop.setChannel((String) result.get("channel"));
			drop.setTitle((String) result.get("droplet_title"));
			drop.setContent((String) result.get("droplet_content"));
			drop.setDatePublished((Date) result.get("droplet_date_pub"));
			drop.setOriginalId((String) result.get("droplet_orig_id"));
			drop.setCommentCount((Integer) result.get("comment_count"));
			drops.add(drop);

			if (result.get("original_url_id") != null) {
				Link originalUrl = new Link();
				originalUrl.setId(((BigInteger) result.get("original_url_id"))
						.longValue());
				originalUrl.setUrl((String) result.get("original_url"));
				drop.setOriginalUrl(originalUrl);
			}

			// Set identity
			Identity identity = new Identity();
			identity.setId(((BigInteger) result.get("identity_id")).longValue());
			identity.setName((String) result.get("identity_name"));
			identity.setAvatar((String) result.get("identity_avatar"));
			drop.setIdentity(identity);
		}

		// Populate metadata
		dropsDao.populateMetadata(drops, queryingAccount);

		return drops;
	}

	/**
	 * @see RiverDao#addCollaborator(River, Account, boolean)
	 */
	public RiverCollaborator addCollaborator(River river, Account account,
			boolean readOnly) {
		RiverCollaborator collaborator = new RiverCollaborator();
		collaborator.setRiver(river);
		collaborator.setAccount(account);
		collaborator.setReadOnly(readOnly);

		river.getCollaborators().add(collaborator);
		this.em.persist(collaborator);

		return collaborator;
	}

	/**
	 * @see {@link RiverDao#deleteCollaborator(Long, Long)}
	 */
	public void deleteCollaborator(Long id, Long accountId) {
		// Retrieve the collaborator from the DB
		RiverCollaborator collaborator = findCollaborator(id, accountId);

		if (collaborator != null) {
			this.em.remove(collaborator);
		}
	}

	/**
	 * @see {@link RiverDao#findCollaborator(Long, Long)}
	 */
	@SuppressWarnings("unchecked")
	public RiverCollaborator findCollaborator(Long riverId, Long accountId) {
		String sql = "FROM RiverCollaborator rc "
				+ "WHERE rc.account.id = :accountId "
				+ "AND rc.river.id =:riverId";

		Query query = this.em.createQuery(sql);
		query.setParameter("accountId", accountId);
		query.setParameter("riverId", riverId);

		List<RiverCollaborator> result = (List<RiverCollaborator>) query
				.getResultList();
		return result.isEmpty() ? null : result.get(0);
	}

	/**
	 * @see {@link RiverDao#updateCollaborator(RiverCollaborator)}
	 */
	public void updateCollaborator(RiverCollaborator collaborator) {
		this.em.merge(collaborator);
	}

	/**
	 * @see {@link RiverDao#removeDrop(Long, Long)}
	 */
	public boolean removeDrop(Long id, Long dropId) {
		String sql = "DELETE FROM RiverDrop rd " + "WHERE rd.id = :dropId "
				+ "AND rd.river.id = :riverId";
		Query query = em.createQuery(sql);
		query.setParameter("riverId", id);
		query.setParameter("dropId", dropId);

		return query.executeUpdate() == 1;
	}

}
