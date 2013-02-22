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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.model.Tag;

/**
 * Repository class for tags
 * @author ekala
 *
 */
@Repository
public class JpaTagDao extends AbstractJpaDao<Tag> implements TagDao {

	/**
	 * @see TagDao#findAllByHash(ArrayList)
	 */
	@SuppressWarnings("unchecked")
	public List<Tag> findAllByHash(ArrayList<String> tagHashes) {
		String sql = "FROM Tag WHERE hash IN (?1)";

		Query query = em.createQuery(sql);
		query.setParameter(1, tagHashes);
		
		return (List<Tag>) query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.TagDao#findByHash(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Tag findByHash(String hash) {
		String sql = "FROM Tag WHERE hash = :hash";
		List<Tag> tags = (List<Tag>)em.createQuery(sql).setParameter("hash", hash).getResultList();
		return tags.isEmpty() ? null : tags.get(0);
	}

	/**
	 * 
	 */
	public Tag findById(long tagId) {
		return this.em.find(Tag.class, tagId);
	}

}
