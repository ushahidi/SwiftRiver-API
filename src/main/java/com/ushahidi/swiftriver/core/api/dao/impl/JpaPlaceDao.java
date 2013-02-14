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

import com.ushahidi.swiftriver.core.api.dao.PlaceDao;
import com.ushahidi.swiftriver.core.model.Place;

/**
 * Repository class for places
 * @author ekala
 *
 */
@Repository
public class JpaPlaceDao extends AbstractJpaDao implements PlaceDao{

	/**
	 * @see PlaceDao#findAllByHash(ArrayList)
	 */
	@SuppressWarnings("unchecked")
	public List<Place> findAllByHash(ArrayList<String> placeHashes) {
		String sql = "FROM Place WHERE hash IN (?1)";
		
		return (List<Place>) em.createQuery(sql).setParameter(1, placeHashes).getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.PlaceDao#findByHash(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Place findByHash(String hash) {
		String sql = "FROM Place WHERE hash = :hash";
		List<Place> links = (List<Place>)em.createQuery(sql).setParameter("hash", hash).getResultList();
		return links.isEmpty() ? null : links.get(0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.PlaceDao#save(com.ushahidi.swiftriver.core.model.Place)
	 */
	public void save(Place place) {
		this.em.persist(place);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.PlaceDao#findById(long)
	 */
	public Place findById(long placeId) {
		return this.em.find(Place.class, placeId);
	}

}