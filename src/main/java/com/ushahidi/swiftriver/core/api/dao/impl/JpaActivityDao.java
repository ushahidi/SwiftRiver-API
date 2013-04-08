package com.ushahidi.swiftriver.core.api.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.ActivityDao;
import com.ushahidi.swiftriver.core.model.Activity;

@Repository
public class JpaActivityDao extends AbstractJpaDao<Activity> implements
		ActivityDao {

	@SuppressWarnings("unchecked")
	@Override
	public List<Activity> find(long accountId, Integer count, Long lastId,
			Boolean newer) {
		String sql = "SELECT ac ";
		sql += "FROM Activity ac JOIN ac.account a ";
		sql += "WHERE a.id = :accountId ";

		if (newer != null && newer) {
			if (lastId != null) {
				sql += "AND ac.id > :lastId ";
			}
			sql += "ORDER BY ac.id ASC ";
		} else {
			if (lastId != null) {
				sql += "AND ac.id < :lastId ";
			}
			sql += "ORDER BY ac.id DESC ";
		}

		Query query = em.createQuery(sql).setParameter("accountId", accountId);

		if (lastId != null) {
			query.setParameter("lastId", lastId).setMaxResults(count);
		}

		List<Activity> activities = (List<Activity>) query.getResultList();
		
		if (activities.size() == 0)
			return null;
		
		return activities;

	}

}
