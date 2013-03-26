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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.PlaceDao;
import com.ushahidi.swiftriver.core.api.dao.SequenceDao;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.Sequence;
import com.ushahidi.swiftriver.core.util.MD5Util;

@Repository
public class JpaPlaceDao extends AbstractJpaDao<Place> implements PlaceDao {

	@Autowired
	private SequenceDao sequenceDao;

	private NamedParameterJdbcTemplate namedJdbcTemplate;
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Place create(Place place) {
		place = formatPlace(place);
		place.setHash(getHash(place));

		return super.create(place);
	}

	/**
	 * Generate a hash for the given place.
	 * 
	 * @param t
	 * @return
	 */
	public String getHash(Place p) {
		String hash = MD5Util.md5Hex(p.getPlaceName()
				+ p.getLongitude().toString() + p.getLatitude().toString());

		return hash;
	}

	private Place formatPlace(Place place) {
		place.setPlaceName(place.getPlaceName().trim());
		place.setPlaceNameCanonical(place.getPlaceName().toLowerCase());
		return place;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.PlaceDao#findByHash(java.lang.String
	 * )
	 */
	@SuppressWarnings("unchecked")
	public Place findByHash(String hash) {
		String sql = "FROM Place WHERE hash = :hash";
		List<Place> places = (List<Place>) em.createQuery(sql)
				.setParameter("hash", hash).getResultList();
		return places.isEmpty() ? null : places.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ushahidi.swiftriver.core.api.dao.PlaceDao#findById(long)
	 */
	public Place findById(long placeId) {
		return this.em.find(Place.class, placeId);
	}

	@Override
	public void getPlaces(List<Drop> drops) {
		// Get a lock
		Sequence seq = sequenceDao.findById("places");

		Map<String, List<int[]>> newPlaceIndex = getNewPlaceIndex(drops);

		if (newPlaceIndex.size() > 0) {
			// Find places that already exist in the db
			updateNewPlaceIndex(newPlaceIndex, drops);

			// Insert new place into the db
			batchInsert(newPlaceIndex, drops, seq);
		}

	}

	/**
	 * Generates a mapping of place hashes to list index for the given drops.
	 * Also populates an place hash into the given list of drops.
	 * 
	 * @param drops
	 * @return
	 */
	private Map<String, List<int[]>> getNewPlaceIndex(List<Drop> drops) {
		Map<String, List<int[]>> newPlaceIndex = new HashMap<String, List<int[]>>();

		// Generate hashes for each new drops i.e. those without an id
		for (int i = 0; i < drops.size(); i++) {
			Drop drop = drops.get(i);
		
			List<Place> places = drop.getPlaces();
			
			if (places == null)
				continue;

			for (int j = 0; j < places.size(); j++) {
				Place place = places.get(j);
			
				// Cleanup the place
				place = formatPlace(place);

				String hash = getHash(place);
				place.setHash(hash);

				// Keep a record of where this hash is in the drop list
				List<int[]> indexes;
				if (newPlaceIndex.containsKey(hash)) {
					indexes = newPlaceIndex.get(hash);
				} else {
					indexes = new ArrayList<int[]>();
					newPlaceIndex.put(hash, indexes);
				}

				// Location of the place in the drops array is an i,j tuple
				int[] loc = { i, j };
				indexes.add(loc);
			}
		}

		return newPlaceIndex;
	}

	/**
	 * For the given list of new drops, find those that the place hash already
	 * exists in the db and update the drop entry with the existing id. Also
	 * remove the hash from the new place index for those that already exist.
	 * 
	 * @param newPlaceIndex
	 * @param drops
	 */
	private void updateNewPlaceIndex(Map<String, List<int[]>> newPlaceIndex,
			List<Drop> drops) {
		// First find and update existing drops with their ids.
		String sql = "SELECT id, hash FROM places WHERE hash IN (:hashes)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("hashes", newPlaceIndex.keySet());

		List<Map<String, Object>> results = this.namedJdbcTemplate
				.queryForList(sql, params);

		// Update id for the drops that were found
		for (Map<String, Object> result : results) {
			String hash = (String) result.get("hash");
			Long id = ((Number) result.get("id")).longValue();

			List<int[]> indexes = newPlaceIndex.get(hash);
			for (int[] index : indexes) {
				drops.get(index[0]).getPlaces().get(index[1]).setId(id);
			}

			// Hash is not for a new drop so remove it
			newPlaceIndex.remove(hash);
		}
	}

	/**
	 * Insert new places in a single batch statement
	 * 
	 * @param newPlaceIndex
	 * @param drops
	 */
	private void batchInsert(final Map<String, List<int[]>> newPlaceIndex,
			final List<Drop> drops, Sequence seq) {

		final List<String> hashes = new ArrayList<String>();
		hashes.addAll(newPlaceIndex.keySet());
		final long startKey = sequenceDao.getIds(seq, hashes.size());

		String sql = "INSERT INTO places (id, hash, place_name, "
				+ "place_name_canonical, longitude, latitude) "
				+ "VALUES (?,?,?,?,?,?)";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				String hash = hashes.get(i);

				// Update drops with the newly generated id
				for (int[] index : newPlaceIndex.get(hash)) {
					drops.get(index[0]).getPlaces().get(index[1])
							.setId(startKey + i);
				}

				int[] index = newPlaceIndex.get(hash).get(0);
				Place place = drops.get(index[0]).getPlaces().get(index[1]);
				ps.setLong(1, place.getId());
				ps.setString(2, place.getHash());
				ps.setString(3, place.getPlaceName());
				ps.setString(4, place.getPlaceNameCanonical());
				ps.setFloat(5, place.getLongitude());
				ps.setFloat(6, place.getLatitude());
			}

			public int getBatchSize() {
				return hashes.size();
			}
		});

		// Update the droplet_places table
		insertDropletPlaces(drops);

	}

	/**
	 * Populate the droplet places table.
	 * 
	 * @param drops
	 */
	private void insertDropletPlaces(List<Drop> drops) {

		// List of drop IDs in the drops list
		List<Long> dropIds = new ArrayList<Long>();
		// List of places in a drop
		Map<Long, Set<Long>> dropletPlacesMap = new HashMap<Long, Set<Long>>();
		for (Drop drop : drops) {
			
			if (drop.getPlaces() == null)
				continue;
			
			dropIds.add(drop.getId());

			for (Place place : drop.getPlaces()) {
				Set<Long> places = null;
				if (dropletPlacesMap.containsKey(drop.getId())) {
					places = dropletPlacesMap.get(drop.getId());
				} else {
					places = new HashSet<Long>();
					dropletPlacesMap.put(drop.getId(), places);
				}

				places.add(place.getId());
			}
		}

		// Find droplet places that already exist in the db
		String sql = "SELECT droplet_id, place_id FROM droplets_places WHERE droplet_id in (:ids)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("ids", dropIds);

		List<Map<String, Object>> results = this.namedJdbcTemplate
				.queryForList(sql, params);

		// Remove already existing droplet_places from our Set
		for (Map<String, Object> result : results) {
			long dropletId = ((Number) result.get("droplet_id"))
					.longValue();
			long placeId = ((Number) result.get("place_id")).longValue();

			Set<Long> placeSet = dropletPlacesMap.get(dropletId);
			if (placeSet != null) {
				placeSet.remove(placeId);
			}
		}

		// Insert the remaining items in the set into the db
		sql = "INSERT INTO droplets_places (droplet_id, place_id) VALUES (?,?)";

		final List<long[]> dropletPlacesList = new ArrayList<long[]>();
		for (Long dropletId : dropletPlacesMap.keySet()) {
			for (Long placeId : dropletPlacesMap.get(dropletId)) {
				long[] dropletPlace = { dropletId, placeId };
				dropletPlacesList.add(dropletPlace);
			}
		}
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				long[] dropletPlace = dropletPlacesList.get(i);
				ps.setLong(1, dropletPlace[0]);
				ps.setLong(2, dropletPlace[1]);
			}

			public int getBatchSize() {
				return dropletPlacesList.size();
			}
		});
	}

}