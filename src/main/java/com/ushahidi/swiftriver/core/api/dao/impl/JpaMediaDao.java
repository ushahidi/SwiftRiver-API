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

import com.ushahidi.swiftriver.core.api.dao.MediaDao;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Media;
import com.ushahidi.swiftriver.core.model.Sequence;
import com.ushahidi.swiftriver.core.util.MD5Util;

/**
 * Repository class for Media
 * 
 * @author ekala
 * 
 */
@Repository
public class JpaMediaDao extends AbstractJpaDao<Media> implements MediaDao {

	@Autowired
	private JpaSequenceDao sequenceDao;

	private NamedParameterJdbcTemplate namedJdbcTemplate;
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/**
	 * @see MediaDao#findByHash(ArrayList)
	 */
	@SuppressWarnings("unchecked")
	public List<Media> findByHash(ArrayList<String> mediaHashes) {
		String sql = "FROM Media WHERE hash IN (?1)";

		return (List<Media>) em.createQuery(sql).setParameter(1, sql)
				.getResultList();
	}

	/**
	 * Generate a hash for the given media.
	 * 
	 * @param t
	 * @return
	 */
	public String getHash(Media m) {
		return MD5Util.md5Hex(m.getUrl());
	}

	@Override
	public void getMedia(List<Drop> drops) {
		// Get a lock
		Sequence seq = sequenceDao.findById("media");

		Map<String, List<int[]>> newMediaIndex = getNewMediaIndex(drops);

		if (newMediaIndex.size() > 0) {
			// Find media that already exist in the db
			updateNewMediaIndex(newMediaIndex, drops);

			// Insert new media into the db
			batchInsert(newMediaIndex, drops, seq);
		}

	}

	/**
	 * Generates a mapping of media hashes to list index for the given drops.
	 * Also populates an media hash into the given list of drops.
	 * 
	 * @param drops
	 * @return
	 */
	private Map<String, List<int[]>> getNewMediaIndex(List<Drop> drops) {
		Map<String, List<int[]>> newMediaIndex = new HashMap<String, List<int[]>>();

		// Generate hashes for each new drops i.e. those without an id
		for (int i = 0; i < drops.size(); i++) {
			Drop drop = drops.get(i);
		
			if (drop.getMedia() == null)
				continue;

			for (int j = 0; j < drop.getMedia().size(); j++) {
				Media media = drop.getMedia().get(j);
				
				// Cleanup the media
				media.setUrl(media.getUrl().trim());

				String hash = getHash(media);
				media.setHash(hash);

				// Keep a record of where this hash is in the drop list
				List<int[]> indexes;
				if (newMediaIndex.containsKey(hash)) {
					indexes = newMediaIndex.get(hash);
				} else {
					indexes = new ArrayList<int[]>();
					newMediaIndex.put(hash, indexes);
				}

				// Location of the media in the drops array is an i,j tuple
				int[] loc = { i, j };
				indexes.add(loc);
			}
		}

		return newMediaIndex;
	}

	/**
	 * For the given list of new drops, find those that the media hash already
	 * exists in the db and update the drop entry with the existing id. Also
	 * remove the hash from the new media index for those that already exist.
	 * 
	 * @param newMediaIndex
	 * @param drops
	 */
	private void updateNewMediaIndex(Map<String, List<int[]>> newMediaIndex,
			List<Drop> drops) {
		// First find and update existing drops with their ids.
		String sql = "SELECT `id`, `hash` FROM `media` WHERE `hash` IN (:hashes)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("hashes", newMediaIndex.keySet());

		List<Map<String, Object>> results = this.namedJdbcTemplate
				.queryForList(sql, params);

		// Update id for the drops that were found
		for (Map<String, Object> result : results) {
			String hash = (String) result.get("hash");
			Long id = ((BigInteger) result.get("id")).longValue();

			List<int[]> indexes = newMediaIndex.get(hash);
			for (int[] index : indexes) {
				drops.get(index[0]).getMedia().get(index[1]).setId(id);
			}

			// Hash is not for a new drop so remove it
			newMediaIndex.remove(hash);
		}
	}

	/**
	 * Insert new media in a single batch statement
	 * 
	 * @param newMediaIndex
	 * @param drops
	 */
	private void batchInsert(final Map<String, List<int[]>> newMediaIndex,
			final List<Drop> drops, Sequence seq) {

		final List<String> hashes = new ArrayList<String>();
		hashes.addAll(newMediaIndex.keySet());
		final long startKey = sequenceDao.getIds(seq, hashes.size());

		String sql = "INSERT INTO `media` (`id`, `hash`, `url`) "
				+ "VALUES (?,?,?)";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				String hash = hashes.get(i);

				// Update drops with the newly generated id
				for (int[] index : newMediaIndex.get(hash)) {
					drops.get(index[0]).getMedia().get(index[1])
							.setId(startKey + i);
				}

				int[] index = newMediaIndex.get(hash).get(0);
				Media media = drops.get(index[0]).getMedia().get(index[1]);
				ps.setLong(1, media.getId());
				ps.setString(2, media.getHash());
				ps.setString(3, media.getUrl());
			}

			public int getBatchSize() {
				return hashes.size();
			}
		});

		// Update the droplet_media table
		insertDropletMedia(drops);

	}

	/**
	 * Populate the droplet media table.
	 * 
	 * @param drops
	 */
	private void insertDropletMedia(List<Drop> drops) {

		// List of drop IDs in the drops list
		List<Long> dropIds = new ArrayList<Long>();
		// List of media in a drop
		Map<Long, Set<Long>> dropletMediaMap = new HashMap<Long, Set<Long>>();
		for (Drop drop : drops) {
			
			if (drop.getMedia() == null)
				continue;
			
			dropIds.add(drop.getId());

			for (Media media : drop.getMedia()) {
				Set<Long> m = null;
				if (dropletMediaMap.containsKey(drop.getId())) {
					m = dropletMediaMap.get(drop.getId());
				} else {
					m = new HashSet<Long>();
					dropletMediaMap.put(drop.getId(), m);
				}

				m.add(media.getId());
			}
		}

		// Find droplet media that already exist in the db
		String sql = "SELECT `droplet_id`, `media_id` FROM `droplets_media` WHERE `droplet_id` in (:ids)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("ids", dropIds);

		List<Map<String, Object>> results = this.namedJdbcTemplate
				.queryForList(sql, params);

		// Remove already existing droplet_media from our Set
		for (Map<String, Object> result : results) {
			long dropletId = ((BigInteger) result.get("droplet_id"))
					.longValue();
			long mediaId = ((BigInteger) result.get("media_id")).longValue();

			Set<Long> mediaSet = dropletMediaMap.get(dropletId);
			if (mediaSet != null) {
				mediaSet.remove(mediaId);
			}
		}

		// Insert the remaining items in the set into the db
		sql = "INSERT INTO `droplets_media` (`droplet_id`, `media_id`) VALUES (?,?)";

		final List<long[]> dropletMediaList = new ArrayList<long[]>();
		for (Long dropletId : dropletMediaMap.keySet()) {
			for (Long mediaId : dropletMediaMap.get(dropletId)) {
				long[] dropletMedia = { dropletId, mediaId };
				dropletMediaList.add(dropletMedia);
			}
		}
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				long[] dropletMedia = dropletMediaList.get(i);
				ps.setLong(1, dropletMedia[0]);
				ps.setLong(2, dropletMedia[1]);
			}

			public int getBatchSize() {
				return dropletMediaList.size();
			}
		});
	}

}
