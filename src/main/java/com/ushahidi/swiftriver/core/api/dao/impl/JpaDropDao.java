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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.api.dao.IdentityDao;
import com.ushahidi.swiftriver.core.api.dao.LinkDao;
import com.ushahidi.swiftriver.core.api.dao.MediaDao;
import com.ushahidi.swiftriver.core.api.dao.PlaceDao;
import com.ushahidi.swiftriver.core.api.dao.SequenceDao;
import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Sequence;
import com.ushahidi.swiftriver.core.util.MD5Util;

@Repository
public class JpaDropDao extends AbstractJpaDao<Drop> implements DropDao {

	final Logger logger = LoggerFactory.getLogger(JpaDropDao.class);

	@Autowired
	private SequenceDao sequenceDao;

	@Autowired
	private IdentityDao identityDao;

	@Autowired
	private TagDao tagDao;

	@Autowired
	private LinkDao linkDao;

	@Autowired
	private PlaceDao placeDao;

	@Autowired
	private MediaDao mediaDao;

	private NamedParameterJdbcTemplate namedJdbcTemplate;
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.DropDao#createDrops(java.util.List)
	 */
	public List<Drop> createDrops(List<Drop> drops) {

		// Get a lock on droplets
		Sequence seq = sequenceDao.findById("droplets");

		// Get identity IDs populated
		identityDao.getIdentities(drops);

		// newDropIndex maps a drop hash to an list index in the drops list.
		Map<String, List<Integer>> newDropIndex = getNewDropIndex(drops);

		// Insert new drops
		if (newDropIndex.size() > 0) {
			// Find drops that already exist
			updateNewDropIndex(newDropIndex, drops);

			// Insert new drops into the db
			batchInsert(newDropIndex, drops, seq);
		}

		// Add drops to their respective rivers
		insertRiverDrops(drops);
		
		// Add drops to their respective buckets
		insertBucketDrops(drops);
		
		// TODO Mark as read

		// Populate metadata
		tagDao.getTags(drops);
		linkDao.getLinks(drops);
		placeDao.getPlaces(drops);
		mediaDao.getMedia(drops);

		return drops;
	}

	/**
	 * Generates a mapping of drop hashes to list index for the given drops.
	 * Also populates a drop hash into the given list of drops.
	 * 
	 * @param drops
	 * @return
	 */
	private Map<String, List<Integer>> getNewDropIndex(List<Drop> drops) {
		Map<String, List<Integer>> newDropIndex = new HashMap<String, List<Integer>>();

		// Generate hashes for each new drops i.e. those without an id
		int i = 0;
		for (Drop drop : drops) {
			if (drop.getId() > 0)
				continue;

			String hash = MD5Util.md5Hex(drop.getIdentity().getOriginId()
					+ drop.getChannel() + drop.getOriginalId());
			drop.setHash(hash);

			// Keep a record of where this hash is in the drop list
			List<Integer> indexes;
			if (newDropIndex.containsKey(hash)) {
				indexes = newDropIndex.get(hash);
			} else {
				indexes = new ArrayList<Integer>();
				newDropIndex.put(hash, indexes);
			}
			indexes.add(i++);
		}

		return newDropIndex;
	}

	/**
	 * For the given list of new drops, find those that the hash already exist
	 * and update the drop entry with the existing id and remove the hash from
	 * the new drop index.
	 * 
	 * @param newDropIndex
	 * @param drops
	 */
	private void updateNewDropIndex(Map<String, List<Integer>> newDropIndex,
			List<Drop> drops) {
		// First find and update existing drops with their ids.
		String sql = "SELECT id, droplet_hash FROM droplets WHERE droplet_hash IN (:hashes)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("hashes", newDropIndex.keySet());

		List<Map<String, Object>> results = this.namedJdbcTemplate
				.queryForList(sql, params);

		// Update id for the drops that were found
		for (Map<String, Object> result : results) {
			String hash = (String) result.get("droplet_hash");
			Long id = ((Number) result.get("id")).longValue();

			List<Integer> indexes = newDropIndex.get(hash);
			for (Integer index : indexes) {
				drops.get(index).setId(id);
			}

			// Hash is not for a new drop so remove it
			newDropIndex.remove(hash);
		}
	}

	/**
	 * Insert new drops in a single batch statement
	 * 
	 * @param newDropIndex
	 * @param drops
	 */
	private void batchInsert(final Map<String, List<Integer>> newDropIndex,
			final List<Drop> drops, Sequence seq) {

		final List<String> hashes = new ArrayList<String>();
		hashes.addAll(newDropIndex.keySet());
		final long startKey = sequenceDao.getIds(seq, hashes.size());

		String sql = "INSERT INTO droplets (id, channel, droplet_hash, "
				+ "droplet_orig_id, droplet_title, "
				+ "droplet_content, droplet_date_pub, droplet_date_add, "
				+ "identity_id) VALUES (?,?,?,?,?,?,?,?,?)";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				String hash = hashes.get(i);

				// Update drops with the newly generated id
				for (int index : newDropIndex.get(hash)) {
					drops.get(index).setId(startKey + i);
				}

				Drop drop = drops.get(newDropIndex.get(hash).get(0));
				ps.setLong(1, drop.getId());
				ps.setString(2, drop.getChannel());
				ps.setString(3, drop.getHash());
				ps.setString(4, drop.getOriginalId());
				ps.setString(5, drop.getTitle());
				ps.setString(6, drop.getContent());
				ps.setTimestamp(7, new java.sql.Timestamp(drop
						.getDatePublished().getTime()));
				ps.setTimestamp(8,
						new java.sql.Timestamp((new Date()).getTime()));
				ps.setLong(9, drop.getIdentity().getId());
			}

			public int getBatchSize() {
				return hashes.size();
			}
		});

	}

	/**
	 * Populate the rivers_droplets table
	 * 
	 * @param drops
	 */
	private void insertRiverDrops(final List<Drop> drops) {

		// Get a lock on rivers_droplets
		Sequence seq = sequenceDao.findById("rivers_droplets");

		// Mapping of drop id to list index position
		final Map<Long, Integer> dropIndex = new HashMap<Long, Integer>();
		// List of rivers in a drop
		Map<Long, Set<Long>> dropRiversMap = new HashMap<Long, Set<Long>>();
		int i = 0;
		for (Drop drop : drops) {
			if (drop.getRiverIds() == null)
				continue;

			Set<Long> rivers = new HashSet<Long>();
			rivers.addAll(drop.getRiverIds());
			dropRiversMap.put(drop.getId(), rivers);

			dropIndex.put(drop.getId(), i++);
		}

		// No rivers found, exit
		if (dropIndex.size() == 0)
			return;

		// Find already existing rivers_droplets
		String sql = "SELECT droplet_id, river_id FROM rivers_droplets WHERE droplet_id in (:ids)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("ids", dropIndex.keySet());

		List<Map<String, Object>> results = this.namedJdbcTemplate
				.queryForList(sql, params);

		// Remove already existing rivers_droplets from our Set
		for (Map<String, Object> result : results) {
			long dropletId = ((Number) result.get("droplet_id"))
					.longValue();
			long riverId = ((Number) result.get("river_id")).longValue();

			Set<Long> riverSet = dropRiversMap.get(dropletId);
			if (riverSet != null) {
				riverSet.remove(riverId);
			}
		}

		// Insert the remaining items in the set into the db
		sql = "INSERT INTO rivers_droplets (id, droplet_id, river_id, droplet_date_pub, channel) VALUES (?,?,?,?,?)";

		final List<long[]> dropRiverList = new ArrayList<long[]>();
		for (Long dropletId : dropRiversMap.keySet()) {
			for (Long riverId : dropRiversMap.get(dropletId)) {
				long[] riverDrop = { dropletId, riverId };
				dropRiverList.add(riverDrop);
			}
		}
		
		if (dropRiverList.size() == 0)
			return;

		// Insert the remaining items in the set into the db
		sql = "INSERT INTO `rivers_droplets` (`id`, `droplet_id`, `river_id`, `droplet_date_pub`, `channel`) VALUES (?,?,?,?,?)";

		final long startKey = sequenceDao.getIds(seq, dropRiverList.size());
		// A map to hold the new max_drop_id and drop_count per river
		final Map<Long, long[]> riverDropsMap = new HashMap<Long, long[]>();
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				long[] riverDrop = dropRiverList.get(i);
				long id = startKey + i;
				Drop drop = drops.get(dropIndex.get(riverDrop[0]));
				ps.setLong(1, id);
				ps.setLong(2, riverDrop[0]);
				ps.setLong(3, riverDrop[1]);
				ps.setTimestamp(4, new java.sql.Timestamp(drop
						.getDatePublished().getTime()));
				ps.setString(5, drop.getChannel());

				// Get updated max_drop_id and drop_count for the rivers table
				long[] update = riverDropsMap.get(riverDrop[1]);
				if (update == null) {
					long[] u = { id, 1 };
					riverDropsMap.put(riverDrop[1], u);
				} else {
					update[0] = Math.max(update[0], id);
					update[1] = update[1] + 1;
				}
			}

			public int getBatchSize() {
				return dropRiverList.size();
			}
		});
		
		// Update river max_drop_id and drop_count
		sql = "UPDATE rivers SET max_drop_id = ?, drop_count = drop_count + ? WHERE id = ?";
		final List<Entry<Long, long[]>> riverUpdate = new ArrayList<Entry<Long, long[]>>();
		riverUpdate.addAll(riverDropsMap.entrySet());		
		this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
            	Entry<Long, long[]> entry = riverUpdate.get(i);
                ps.setLong(1, entry.getValue()[0]);
                ps.setLong(2, entry.getValue()[1]);
                ps.setLong(3, entry.getKey());
            }

            public int getBatchSize() {
                return riverUpdate.size();
            }
        } );
	}
	
	/**
	 * Populates the buckets_droplets table
	 * 
	 * @param drops
	 */
	private void insertBucketDrops(final List<Drop> drops) {
		// Stores the drop id against the destination bucket ids
		Map<Long, Set<Long>> dropBucketsMap = new HashMap<Long, Set<Long>>();

		// Stores the drop id against its index in the drops list
		final Map<Long, Integer> dropsIndex = new HashMap<Long, Integer>();
		int i = 0;
		for (Drop drop: drops) {
			if (drop.getBucketIds() == null)
				continue;

			Set<Long> bucketSet = new HashSet<Long>();
			bucketSet.addAll(drop.getBucketIds());
			dropBucketsMap.put(drop.getId(), bucketSet);
			dropsIndex.put(drop.getId(), i);
			i++;
		}

		if (dropsIndex.isEmpty())
			return;

		// Exclude existing drops
		String existsSQL = "SELECT `bucket_id`, `droplet_id` " +
				"FROM `buckets_droplets` WHERE `droplet_id` IN (:ids)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("ids", dropsIndex.keySet());

		for (Map<String, Object> row: namedJdbcTemplate.queryForList(existsSQL, params)) {
			Long dropId = ((Number) row.get("droplet_id")).longValue();
			Long bucketId = ((Number) row.get("bucket_id")).longValue();

			if (dropBucketsMap.containsKey(dropId)) {
				Set<Long> bucketIdSet = dropBucketsMap.get(dropId);
				bucketIdSet.remove(bucketId);
			}
		}

		// Stores each bucket id
		// List of arrays comprised of the drop id and bucket id
		final List<Long[]> bucketDropList = new ArrayList<Long[]>();
		for (Map.Entry<Long, Set<Long>> entry: dropBucketsMap.entrySet()) {
			for (Long bucketId: entry.getValue()) {
				Long[] bucketDrop = {bucketId, entry.getKey()};
				bucketDropList.add(bucketDrop);
			}
		}

		if (bucketDropList.isEmpty())
			return;

		// Store for the no. of drops inserted for each bucket
		final Map<Long, Integer> bucketDropCount = new HashMap<Long, Integer>();

		// Query for populating TABLE buckets_droplets
		String insertSQL = "INSERT INTO `buckets_droplets` (`bucket_id`, `droplet_id`, `droplet_date_added`) " +
				"VALUES (?, ?, ?)";

		jdbcTemplate.batchUpdate(insertSQL, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement statement, int index) throws SQLException {
				Long[] bucketDrop = bucketDropList.get(index);
				Long bucketId = bucketDrop[0];
				Drop drop = drops.get(dropsIndex.get(bucketDrop[1]));

				statement.setLong(1, bucketId);
				statement.setLong(2, bucketDrop[1]);
				statement.setTimestamp(3, 
						new java.sql.Timestamp(drop.getDateAdded().getTime()));

				Integer count = bucketDropCount.get(bucketId);
				count = (count == null) ? 0 : count + 1;
				bucketDropCount.put(bucketId, count);
			}

			@Override
			public int getBatchSize() {
				return bucketDropList.size();
			}
		});

		// Update the drop count for the populated buckets
		List<String> tempTableQuery = new ArrayList<String>();
		for (Map.Entry<Long, Integer> entry: bucketDropCount.entrySet()) {
			String sql = String.format("SELECT %d AS `id`, %d AS `drop_count`", 
					entry.getKey(), entry.getValue());
			tempTableQuery.add(sql);
		}

		String joinQuery = StringUtils.join(tempTableQuery, " UNION ALL ");
		String updateSQL = "UPDATE `buckets` JOIN(" + joinQuery + ") AS t " +
				"USING (`id`) " +
				"SET `buckets`.`drop_count` = `buckets`.`drop_count` + `t`.`drop_count` ";

		this.jdbcTemplate.update(updateSQL);
	}
}
