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

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.MediaDao;
import com.ushahidi.swiftriver.core.model.Media;

/**
 * Repository class for Media
 * @author ekala
 *
 */
@Repository
public class JpaMediaDao extends AbstractJpaDao<Media> implements MediaDao {

	/**
	 * @see MediaDao#findByHash(ArrayList) 
	 */
	@SuppressWarnings("unchecked")
	public List<Media> findByHash(ArrayList<String> mediaHashes) {
		String sql = "FROM Media WHERE hash IN (?1)";
		
		return (List<Media>) em.createQuery(sql).setParameter(1, sql).getResultList();
	}

}
