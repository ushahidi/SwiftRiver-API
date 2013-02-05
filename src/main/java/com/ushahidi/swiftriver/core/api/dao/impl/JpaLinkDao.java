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

import com.ushahidi.swiftriver.core.api.dao.LinkDao;
import com.ushahidi.swiftriver.core.model.Link;

@Repository
public class JpaLinkDao extends AbstractJpaDao<Link, Long> implements LinkDao {

	public JpaLinkDao() {
		super(Link.class);
	}

	/**
	 * @see LinkDao#findByHash(ArrayList)
	 */
	@SuppressWarnings("unchecked")
	public List<Link> findByHash(ArrayList<String> linkHashes) {
		String sql = "FROM Link WHERE hash IN (?1)";
		
		return (List<Link>) entityManager.createQuery(sql).setParameter(1, linkHashes).getResultList();
	}

}
