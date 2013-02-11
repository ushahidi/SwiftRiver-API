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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Identity;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.River;


@Repository
@Transactional
public class JpaRiverDao  implements RiverDao {
	
	final Logger logger = LoggerFactory.getLogger(JpaRiverDao.class);

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private DropDao dropsDao;

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Override
	public River findById(long id) {
		River river = em.find(River.class, id);
		return river;
	}

	public List<Drop> getDrops(Long riverId, Long maxId, int dropCount, Account queryingAccount) {
		String sql = "SELECT `droplets`.`id` AS `id`, `rivers_droplets`.`id` AS `sort_id`, `droplet_title`, `droplet_content`, `droplets`.`channel`, ";
		sql += "`identities`.`id` AS `identity_id`, `identity_name`, `identity_avatar`, `droplets`.`droplet_date_pub`, `droplet_orig_id`, ";
		sql += "`user_scores`.`score` AS `user_score`, `links`.`id` as `original_url_id`, `links`.`url` AS `original_url`, `comment_count` ";
		sql += "FROM `rivers_droplets` ";
		sql += "INNER JOIN `droplets` ON (`rivers_droplets`.`droplet_id` = `droplets`.`id`) ";
		sql += "INNER JOIN `identities` ON (`droplets`.`identity_id` = `identities`.`id`) ";
		sql += "LEFT JOIN `droplet_scores` AS `user_scores` ON (`user_scores`.`droplet_id` = droplets.id AND user_scores.user_id = 3) ";
		sql += "LEFT JOIN `links` ON (`links`.`id` = `droplets`.`original_url`) ";
		sql += "WHERE `rivers_droplets`.`droplet_date_pub` > '0000-00-00 00:00:00' "; 
		sql += "AND `rivers_droplets`.`river_id` = :riverId ";
		sql += "AND `rivers_droplets`.`id` <= :maxId ";
		sql += "ORDER BY `rivers_droplets`.`droplet_date_pub` DESC";
		
		Query query = em.createNativeQuery(sql);
		query.setParameter("riverId", riverId);
		query.setParameter("maxId", maxId);
		query.setMaxResults(dropCount);
		
		List<Drop> drops = new ArrayList<Drop>();
		for (Object oRow : query.getResultList()) {
			Object[] r = (Object[]) oRow;
			
			Drop drop = new Drop();
			
			// Set drop details
			drop.setId(((BigInteger)r[0]).longValue());
			drop.setChannel((String)r[4]);
			drop.setTitle((String)r[2]);
			drop.setContent((String)r[3]);
			drop.setDatePublished((Date)r[8]);
			drop.setOriginalId((String)r[9]);
			drop.setCommentCount((Integer)r[13]);
			drops.add(drop);

			if (r[11] != null) {
				Link originalUrl = new Link();
				originalUrl.setId(((BigInteger)r[11]).longValue());
				originalUrl.setUrl((String)r[12]);
				drop.setOriginalUrl(originalUrl);
			}
			
			// Set identity
			Identity identity = new Identity();
			identity.setId(((BigInteger)r[5]).longValue());
			identity.setName((String)r[6]);
			identity.setAvatar((String)r[7]);
			drop.setIdentity(identity);
		}
		
		// Populate metadata
		dropsDao.populateMetadata(drops, queryingAccount);

		return drops;
	}

}
