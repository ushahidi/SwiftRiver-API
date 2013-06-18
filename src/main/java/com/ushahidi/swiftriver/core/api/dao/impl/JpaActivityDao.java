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

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.ActivityDao;
import com.ushahidi.swiftriver.core.model.Activity;

@Repository
public class JpaActivityDao extends AbstractJpaDao<Activity> implements ActivityDao {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ushahidi.swiftriver.core.api.dao.ActivityDao#find(long,
	 * java.lang.Integer, java.lang.Long, java.lang.Boolean)
	 */
	public List<Activity> find(long accountId, Integer count, Long lastId,
			Boolean newer, Boolean followers) {
		String qlString = "SELECT ac " +
				"FROM Activity ac " +
				"JOIN ac.account a ";

		qlString += (followers) 
				? "JOIN a.followers f JOIN f.follower a2 WHERE a2.id = :accountId " 
				: "WHERE a.id = :accountId ";

		if (newer != null && newer) {
			if (lastId != null) {
				qlString += "AND ac.id > :lastId ";
			}
			qlString += "ORDER BY ac.id ASC ";
		} else {
			if (lastId != null) {
				qlString += "AND ac.id < :lastId ";
			}
			qlString += "ORDER BY ac.id DESC ";
		}

		TypedQuery<Activity> query = em.createQuery(qlString, Activity.class);
		query.setParameter("accountId", accountId);

		if (lastId != null) {
			query.setParameter("lastId", lastId).setMaxResults(count);
		}
		
		List<Activity> activities = query.getResultList();
		
		if (activities.size() == 0)
			return null;
		
		return activities;

	}

}
