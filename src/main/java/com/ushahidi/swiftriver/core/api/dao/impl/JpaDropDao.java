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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Query;
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
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.DropSource;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Media;
import com.ushahidi.swiftriver.core.model.MediaThumbnail;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.Sequence;
import com.ushahidi.swiftriver.core.model.Tag;
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
		String sql = "SELECT `id`, `droplet_hash` FROM `droplets` WHERE `droplet_hash` IN (:hashes)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("hashes", newDropIndex.keySet());

		List<Map<String, Object>> results = this.namedJdbcTemplate
				.queryForList(sql, params);

		// Update id for the drops that were found
		for (Map<String, Object> result : results) {
			String hash = (String) result.get("droplet_hash");
			Long id = ((BigInteger) result.get("id")).longValue();

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

		String sql = "INSERT INTO `droplets` (`id`, `channel`, `droplet_hash`, "
				+ "`droplet_orig_id`, `droplet_title`, "
				+ "`droplet_content`, `droplet_date_pub`, `droplet_date_add`, "
				+ "`identity_id`) VALUES (?,?,?,?,?,?,?,?,?)";

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
		String sql = "SELECT `droplet_id`, `river_id` FROM `rivers_droplets` WHERE `droplet_id` in (:ids)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("ids", dropIndex.keySet());

		List<Map<String, Object>> results = this.namedJdbcTemplate
				.queryForList(sql, params);

		// Remove already existing rivers_droplets from our Set
		for (Map<String, Object> result : results) {
			long dropletId = ((BigInteger) result.get("droplet_id"))
					.longValue();
			long riverId = ((BigInteger) result.get("river_id")).longValue();

			Set<Long> riverSet = dropRiversMap.get(dropletId);
			if (riverSet != null) {
				riverSet.remove(riverId);
			}
		}

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

		// Create a temp table sql for updating the rivers table
		String riverUpdateSql = "";
		for (Entry<Long, long[]> entry : riverDropsMap.entrySet()) {
			long[] update = entry.getValue();

			if (riverUpdateSql.length() > 0) {
				riverUpdateSql += " UNION ALL ";
			}

			riverUpdateSql += String.format("SELECT %d id, %d max_id, %d cnt",
					entry.getKey(), update[0], update[1]);
		}

		// Update max_drop_id using the temp table
		sql = "UPDATE `rivers` " + "JOIN (" + riverUpdateSql + ") a "
				+ "USING (`id`) "
				+ "SET `rivers`.`max_drop_id` = `a`.`max_id` "
				+ "WHERE `rivers`.`max_drop_id` < `a`.`max_id`";
		this.jdbcTemplate.update(sql);

		// Update drop_count using the temp table
		sql = "UPDATE `rivers` "
				+ "JOIN ("
				+ riverUpdateSql
				+ ") a "
				+ "USING (`id`) "
				+ "SET `rivers`.`drop_count` = `rivers`.`drop_count` + `a`.`cnt`";
		this.jdbcTemplate.update(sql);
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
			Long dropId = ((BigInteger) row.get("droplet_id")).longValue();
			Long bucketId = ((BigInteger) row.get("bucket_id")).longValue();
			
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
				statement.setTimestamp(2, 
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

	/**
	 * @see DropDao#findDropsByHash(ArrayList)
	 */
	@SuppressWarnings("unchecked")
	public List<Drop> findDropsByHash(ArrayList<String> dropHashes) {
		String sql = "FROM Drop d WHERE d.dropletHash in (?1)";

		Query query = em.createQuery(sql);
		query.setParameter(1, dropHashes);

		return (List<Drop>) query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.DropDao#populateMetadata(java.util
	 * .List, com.ushahidi.swiftriver.core.model.Account)
	 */
	public void populateMetadata(List<Drop> drops, DropSource dropSource,
			Account queryingAccount) {
		if (drops.size() == 0) {
			return;
		}

		populateTags(drops, dropSource);
		populateLinks(drops, dropSource);
		populateMedia(drops, dropSource);
		populatePlaces(drops, dropSource);
		populateBuckets(drops, queryingAccount, dropSource);
	}

	/**
	 * Populate tag metadata into the given drops.
	 * 
	 * @param drops
	 * @param dropSource
	 */
	public void populateTags(List<Drop> drops, DropSource dropSource) {

		List<Long> dropIds = new ArrayList<Long>();
		for (Drop drop : drops) {
			dropIds.add(drop.getId());
		}

		String sql = null;
		switch (dropSource) {
		case RIVER:
			sql = getRiverTagsQuery();
			break;

		case BUCKET:
			sql = getBucketTagsQuery();
			break;
		}

		Query query = em.createNativeQuery(sql);
		query.setParameter("drop_ids", dropIds);

		// Group the tags by drop id
		Map<Long, List<Tag>> tags = new HashMap<Long, List<Tag>>();
		for (Object oRow : query.getResultList()) {
			Object[] r = (Object[]) oRow;

			Long dropId = ((BigInteger) r[0]).longValue();
			Tag tag = new Tag();
			tag.setId(((BigInteger) r[1]).longValue());
			tag.setTag((String) r[2]);
			tag.setType((String) r[4]);

			List<Tag> t = tags.get(dropId);
			if (t == null) {
				t = new ArrayList<Tag>();
				tags.put(dropId, t);
			}

			t.add(tag);
		}

		for (Drop drop : drops) {
			List<Tag> t = tags.get(drop.getId());

			if (t != null) {
				drop.setTags(t);
			} else {
				drop.setTags(new ArrayList<Tag>());
			}
		}

	}

	/**
	 * Builds and returns the query for fetching tags for the drops in a
	 * <code>com.ushahidi.swiftriver.core.model.Bucket</code>
	 * 
	 * @return
	 */
	private String getBucketTagsQuery() {
		String sql = "SELECT `buckets_droplets`.`id` AS `droplet_id`, `tag_id` AS `id`, `tag`, `tag_canonical`, `tag_type` ";
		sql += "FROM `droplets_tags`  ";
		sql += "INNER JOIN `tags` ON (`tags`.`id` = `tag_id`)  ";
		sql += "INNER JOIN buckets_droplets ON (`buckets_droplets`.`droplet_id` = `droplets_tags`.`droplet_id`)";
		sql += "WHERE `buckets_droplets`.`id` IN :drop_ids  ";
		sql += "AND `tags`.`id` NOT IN ( ";
		sql += "	SELECT `tag_id` FROM `bucket_droplet_tags`  ";
		sql += "	WHERE `buckets_droplets_id` IN :drop_ids  ";
		sql += "	AND `deleted` = 1) ";
		sql += "UNION ALL  ";
		sql += "SELECT `buckets_droplets_id` AS `droplet_id`, `tag_id` AS `id`, `tag`, `tag_canonical`, `tag_type`  ";
		sql += "FROM `bucket_droplet_tags` ";
		sql += "INNER JOIN `tags` ON (`tags`.`id` = `tag_id`)  ";
		sql += "WHERE `buckets_droplets_id` IN :drop_ids  ";
		sql += "AND `deleted` = 0 ";

		return sql;
	}

	/**
	 * Builds and returns the query for fetching tags for the drops in a
	 * <code>com.ushahidi.swiftriver.core.model.River</code>
	 * 
	 * @return
	 */
	private String getRiverTagsQuery() {
		String sql = "SELECT `rivers_droplets`.`id` AS `droplet_id`, `tag_id` AS `id`, `tag`, `tag_canonical`, `tag_type` ";
		sql += "FROM `droplets_tags`  ";
		sql += "INNER JOIN `tags` ON (`tags`.`id` = `tag_id`)  ";
		sql += "INNER JOIN rivers_droplets ON (`rivers_droplets`.`droplet_id` = `droplets_tags`.`droplet_id`)";
		sql += "WHERE `rivers_droplets`.`id` IN :drop_ids  ";
		sql += "AND `tags`.`id` NOT IN ( ";
		sql += "	SELECT `tag_id` FROM `river_droplet_tags`  ";
		sql += "	WHERE `rivers_droplets_id` IN :drop_ids  ";
		sql += "	AND `deleted` = 1) ";
		sql += "UNION ALL  ";
		sql += "SELECT `rivers_droplets_id` AS `droplet_id`, `tag_id` AS `id`, `tag`, `tag_canonical`, `tag_type`  ";
		sql += "FROM `river_droplet_tags` ";
		sql += "INNER JOIN `tags` ON (`tags`.`id` = `tag_id`)  ";
		sql += "WHERE `rivers_droplets_id` IN :drop_ids  ";
		sql += "AND `deleted` = 0 ";

		return sql;
	}

	/**
	 * Populate link metadata into the given drops array.
	 * 
	 * @param drops
	 * @param dropSource
	 */
	public void populateLinks(List<Drop> drops, DropSource dropSource) {

		List<Long> dropIds = new ArrayList<Long>();
		for (Drop drop : drops) {
			dropIds.add(drop.getId());
		}

		String sql = null;
		switch (dropSource) {
		case RIVER:
			sql = getRiverLinksQuery();
			break;

		case BUCKET:
			sql = getBucketLinksQuery();
			break;
		}

		Query query = em.createNativeQuery(sql);
		query.setParameter("drop_ids", dropIds);

		// Group the links by drop id
		Map<Long, List<Link>> links = new HashMap<Long, List<Link>>();
		for (Object oRow : query.getResultList()) {
			Object[] r = (Object[]) oRow;

			Long dropId = ((BigInteger) r[0]).longValue();
			Link link = new Link();
			link.setId(((BigInteger) r[1]).longValue());
			link.setUrl((String) r[2]);

			List<Link> l = links.get(dropId);
			if (l == null) {
				l = new ArrayList<Link>();
				links.put(dropId, l);
			}

			l.add(link);
		}

		for (Drop drop : drops) {
			List<Link> l = links.get(drop.getId());

			if (l != null) {
				drop.setLinks(l);
			} else {
				drop.setLinks(new ArrayList<Link>());
			}
		}
	}

	/**
	 * Builds and returns the query for fetching links for the drops in a
	 * <code>com.ushahidi.swiftriver.core.model.Bucket</code>
	 * 
	 * @return
	 */
	private String getBucketLinksQuery() {
		String sql = "SELECT `buckets_droplets`.`id` AS `droplet_id`, `link_id` AS `id`, `url` ";
		sql += "FROM `droplets_links`  ";
		sql += "INNER JOIN `links` ON (`links`.`id` = `link_id`)  ";
		sql += "INNER JOIN buckets_droplets ON (`buckets_droplets`.`droplet_id` = `droplets_links`.`droplet_id`)";
		sql += "WHERE `buckets_droplets`.`id` IN :drop_ids  ";
		sql += "AND `links`.`id` NOT IN ( ";
		sql += "	SELECT `link_id` FROM `bucket_droplet_links`  ";
		sql += "	WHERE `buckets_droplets_id` IN :drop_ids  ";
		sql += "	AND `deleted` = 1) ";
		sql += "UNION ALL  ";
		sql += "SELECT `buckets_droplets_id` AS `droplet_id`, `link_id` AS `id`, `url`  ";
		sql += "FROM `bucket_droplet_links`  ";
		sql += "INNER JOIN `links` ON (`links`.`id` = `link_id`)  ";
		sql += "WHERE `buckets_droplets_id` IN :drop_ids  ";
		sql += "AND `deleted` = 0 ";

		return sql;
	}

	/**
	 * Builds and returns the query for fetching links for the drops in a
	 * <code>com.ushahidi.swiftriver.core.model.River</code>
	 * 
	 * @return
	 */
	private String getRiverLinksQuery() {
		String sql = "SELECT `rivers_droplets`.`id` AS `droplet_id`, `link_id` AS `id`, `url` ";
		sql += "FROM `droplets_links`  ";
		sql += "INNER JOIN `links` ON (`links`.`id` = `link_id`)  ";
		sql += "INNER JOIN rivers_droplets ON (`rivers_droplets`.`droplet_id` = `droplets_links`.`droplet_id`)";
		sql += "WHERE `rivers_droplets`.`id` IN :drop_ids  ";
		sql += "AND `links`.`id` NOT IN ( ";
		sql += "	SELECT `link_id` FROM `river_droplet_links`  ";
		sql += "	WHERE `rivers_droplets_id` IN :drop_ids  ";
		sql += "	AND `deleted` = 1) ";
		sql += "UNION ALL  ";
		sql += "SELECT `rivers_droplets_id` AS `droplet_id`, `link_id` AS `id`, `url`  ";
		sql += "FROM `river_droplet_links`  ";
		sql += "INNER JOIN `links` ON (`links`.`id` = `link_id`)  ";
		sql += "WHERE `rivers_droplets_id` IN :drop_ids  ";
		sql += "AND `deleted` = 0 ";

		return sql;
	}

	/**
	 * Populate media metadata into the given drops array.
	 * 
	 * @param drops
	 * @param dropSource
	 */
	public void populateMedia(List<Drop> drops, DropSource dropSource) {

		Map<Long, Integer> dropIndex = new HashMap<Long, Integer>();
		int i = 0;
		for (Drop drop : drops) {
			dropIndex.put(drop.getId(), i);
			i++;
		}

		// Generate a map for drop images
		String sql = null;
		switch (dropSource) {
		case BUCKET:
			sql = "SELECT `buckets_droplets`.`id`, `droplet_image` FROM `droplets` ";
			sql += "INNER JOIN `buckets_droplets` ON (`buckets_droplets`.`droplet_id` = `droplets`.`id`) ";
			sql += "WHERE `buckets_droplets`.`id` IN :drop_ids ";
			break;

		case RIVER:
			sql = "SELECT `rivers_droplets`.`id`, `droplet_image` FROM `droplets` ";
			sql += "INNER JOIN `rivers_droplets` ON (`rivers_droplets`.`droplet_id` = `droplets`.`id`) ";
			sql += "WHERE `rivers_droplets`.`id` IN :drop_ids ";
			break;
		}

		sql += "AND `droplets`.`droplet_image` > 0";

		Query query = em.createNativeQuery(sql);
		query.setParameter("drop_ids", dropIndex.keySet());

		Map<Long, Long> dropImagesMap = new HashMap<Long, Long>();
		for (Object oRow2 : query.getResultList()) {
			Object[] r2 = (Object[]) oRow2;
			dropImagesMap.put(((BigInteger) r2[0]).longValue(),
					((BigInteger) r2[1]).longValue());
		}

		// Get the query to fetch the drop media
		switch (dropSource) {
		case RIVER:
			sql = getRiverMediaQuery();
			break;

		case BUCKET:
			sql = getBucketMediaQuery();
			break;
		}

		query = em.createNativeQuery(sql);
		query.setParameter("drop_ids", dropIndex.keySet());

		// Group the media by drop id
		for (Object oRow : query.getResultList()) {
			Object[] r = (Object[]) oRow;

			Long dropId = ((BigInteger) r[0]).longValue();
			Drop drop = drops.get(dropIndex.get(dropId));
			if (drop.getMedia() == null) {
				drop.setMedia(new ArrayList<Media>());
			}

			Long mediaId = ((BigInteger) r[1]).longValue();
			Media m = null;
			for (Media x : drop.getMedia()) {
				if (x.getId() == mediaId) {
					m = x;
				}
			}

			if (m == null) {
				m = new Media();
				m.setId(mediaId);
				m.setUrl((String) r[2]);
				m.setType((String) r[3]);
			}

			// Add thumbnails
			if (r[4] != null) {
				MediaThumbnail mt = new MediaThumbnail();
				mt.setMedia(m);
				mt.setSize((Integer) r[4]);
				mt.setUrl((String) r[5]);

				List<MediaThumbnail> thumbnails = m.getThumbnails();
				if (thumbnails == null) {
					thumbnails = new ArrayList<MediaThumbnail>();
					m.setThumbnails(thumbnails);
				}
				thumbnails.add(mt);
			}

			if (!drop.getMedia().contains(m)) {
				drop.getMedia().add(m);

				// Set the droplet image if any
				Long dropImageId = dropImagesMap.get(drop.getId());
				if (dropImageId != null && dropImageId == m.getId()) {
					drop.setImage(m);
				}
			}
		}
	}

	/**
	 * Builds and returns the query for fetching media for the drops in a
	 * <code>com.ushahidi.swiftriver.core.model.Bucket</code>
	 * 
	 * @return
	 */
	private String getBucketMediaQuery() {
		String sql = "SELECT `buckets_droplets`.`id` AS `droplet_id`, `media`.`id` AS `id`, `media`.`url` AS `url`, `type`, `media_thumbnails`.`size` AS `thumbnail_size`, ";
		sql += "`media_thumbnails`.`url` AS `thumbnail_url` ";
		sql += "FROM `droplets_media` ";
		sql += "INNER JOIN `media` ON (`media`.`id` = `media_id`) ";
		sql += "INNER JOIN `buckets_droplets` ON (`buckets_droplets`.`droplet_id` = `droplets_media`.`droplet_id`) ";
		sql += "LEFT JOIN `media_thumbnails` ON (`media_thumbnails`.`media_id` = `media`.`id`) ";
		sql += "WHERE `buckets_droplets`.`id` IN :drop_ids ";
		sql += "AND `media`.`id` NOT IN ( ";
		sql += "	SELECT `media_id` ";
		sql += "	FROM `bucket_droplet_media` ";
		sql += "	WHERE `buckets_droplets_id` IN :drop_ids ";
		sql += "	AND `deleted` = 1) ";
		sql += "UNION ALL ";
		sql += "SELECT `buckets_droplets_id` AS `droplet_id`, `media`.`id` AS `id`, `media`.`url` AS `url`, `type`, `media_thumbnails`.`size` AS `thumbnail_size`, `media_thumbnails`.`url` AS `thumbnail_url` ";
		sql += "FROM `bucket_droplet_media` ";
		sql += "INNER JOIN `media` ON (`media`.`id` = `media_id`) ";
		sql += "LEFT JOIN `media_thumbnails` ON (`media_thumbnails`.`media_id` = `media`.`id`) ";
		sql += "WHERE `buckets_droplets_id` IN :drop_ids ";
		sql += "AND `deleted` = 0; ";

		return sql;
	}

	/**
	 * Builds and returns the query for fetching media for the drops in a
	 * <code>com.ushahidi.swiftriver.core.model.River</code>
	 * 
	 * @return
	 */
	private String getRiverMediaQuery() {
		String sql = "SELECT `rivers_droplets`.`id` AS `droplet_id`, `media`.`id` AS `id`, `media`.`url` AS `url`, `type`, `media_thumbnails`.`size` AS `thumbnail_size`, ";
		sql += "`media_thumbnails`.`url` AS `thumbnail_url` ";
		sql += "FROM `droplets_media` ";
		sql += "INNER JOIN `media` ON (`media`.`id` = `media_id`) ";
		sql += "INNER JOIN `rivers_droplets` ON (`rivers_droplets`.`droplet_id` = `droplets_media`.`droplet_id`) ";
		sql += "LEFT JOIN `media_thumbnails` ON (`media_thumbnails`.`media_id` = `media`.`id`) ";
		sql += "WHERE `rivers_droplets`.`id` IN :drop_ids ";
		sql += "AND `media`.`id` NOT IN ( ";
		sql += "	SELECT `media_id` ";
		sql += "	FROM `river_droplet_media` ";
		sql += "	WHERE `rivers_droplets_id` IN :drop_ids ";
		sql += "	AND `deleted` = 1) ";
		sql += "UNION ALL ";
		sql += "SELECT `rivers_droplets_id` AS `droplet_id`, `media`.`id` AS `id`, `media`.`url` AS `url`, `type`, `media_thumbnails`.`size` AS `thumbnail_size`, `media_thumbnails`.`url` AS `thumbnail_url` ";
		sql += "FROM `river_droplet_media` ";
		sql += "INNER JOIN `media` ON (`media`.`id` = `media_id`) ";
		sql += "LEFT JOIN `media_thumbnails` ON (`media_thumbnails`.`media_id` = `media`.`id`) ";
		sql += "WHERE `rivers_droplets_id` IN :drop_ids ";
		sql += "AND `deleted` = 0; ";

		return sql;
	}

	/**
	 * Populate geo metadata into the given drops array.
	 * 
	 * @param drops
	 * @param dropSource
	 */
	public void populatePlaces(List<Drop> drops, DropSource dropSource) {

		Map<Long, Integer> dropIndex = new HashMap<Long, Integer>();
		int i = 0;
		for (Drop drop : drops) {
			dropIndex.put(drop.getId(), i);
			i++;
		}

		String sql = null;
		// Get the query to fetch the drop media
		switch (dropSource) {
		case RIVER:
			sql = getRiverPlacesQuery();
			break;

		case BUCKET:
			sql = getBucketPlacesQuery();
			break;
		}

		Query query = em.createNativeQuery(sql);
		query.setParameter("drop_ids", dropIndex.keySet());

		// Group the media by drop id
		Map<Long, Place> places = new HashMap<Long, Place>();
		for (Object oRow : query.getResultList()) {
			Object[] r = (Object[]) oRow;

			Long dropId = ((BigInteger) r[0]).longValue();
			Drop drop = drops.get(dropIndex.get(dropId));
			if (drop.getPlaces() == null) {
				drop.setPlaces(new ArrayList<Place>());
			}

			Long placeId = ((BigInteger) r[1]).longValue();
			Place p = places.get(placeId);

			if (p == null) {
				p = new Place();
				p.setId(placeId);
				p.setPlaceName((String) r[2]);
				p.setLatitude((Float) r[5]);
				p.setLongitude((Float) r[6]);

				places.put(placeId, p);
			}

			// Add place to drop
			if (!drop.getPlaces().contains(p)) {
				drop.getPlaces().add(p);
			}
		}
	}

	/**
	 * Builds and returns the query for fetching places for the drops in a
	 * <code>com.ushahidi.swiftriver.core.model.Bucket</code>
	 * 
	 * @return
	 */
	private String getBucketPlacesQuery() {
		String sql = "SELECT `buckets_droplets`.`id` AS `droplet_id`, `place_id` AS `id`, `place_name`, `place_name_canonical`, ";
		sql += "`places`.`hash` AS `place_hash`, `latitude`, `longitude` ";
		sql += "FROM `droplets_places` ";
		sql += "INNER JOIN `places` ON (`places`.`id` = `place_id`) ";
		sql += "INNER JOIN `buckets_droplets` ON (`buckets_droplets`.`droplet_id` = `droplets_places`.`droplet_id`) ";
		sql += "WHERE `buckets_droplets`.`id` IN :drop_ids ";
		sql += "AND `places`.`id` NOT IN ( ";
		sql += "	SELECT `place_id` ";
		sql += "	FROM `bucket_droplet_places` ";
		sql += "	WHERE `buckets_droplets_id` IN :drop_ids ";
		sql += "	AND `deleted` = 1) ";
		sql += "UNION ALL ";
		sql += "SELECT `buckets_droplets_id` AS `droplet_id`, `place_id` AS `id`, `place_name`, `place_name_canonical`, `places`.`hash` AS `place_hash`, `latitude`, `longitude` ";
		sql += "FROM `bucket_droplet_places` ";
		sql += "INNER JOIN `places` ON (`places`.`id` = `place_id`) ";
		sql += "WHERE `buckets_droplets_id` IN :drop_ids ";
		sql += "AND `deleted` = 0 ";

		return sql;
	}

	/**
	 * Builds and returns the query for fetching places for the drops in a
	 * <code>com.ushahidi.swiftriver.core.model.River</code>
	 * 
	 * @return
	 */
	private String getRiverPlacesQuery() {
		String sql = "SELECT `rivers_droplets`.`id` AS `droplet_id`, `place_id` AS `id`, `place_name`, `place_name_canonical`, ";
		sql += "`places`.`hash` AS `place_hash`, `latitude`, `longitude` ";
		sql += "FROM `droplets_places` ";
		sql += "INNER JOIN `places` ON (`places`.`id` = `place_id`) ";
		sql += "INNER JOIN `rivers_droplets` ON (`rivers_droplets`.`droplet_id` = `droplets_places`.`droplet_id`) ";
		sql += "WHERE `rivers_droplets`.`id` IN :drop_ids ";
		sql += "AND `places`.`id` NOT IN ( ";
		sql += "	SELECT `place_id` ";
		sql += "	FROM `river_droplet_places` ";
		sql += "	WHERE `rivers_droplets_id` IN :drop_ids ";
		sql += "	AND `deleted` = 1) ";
		sql += "UNION ALL ";
		sql += "SELECT `rivers_droplets_id` AS `droplet_id`, `place_id` AS `id`, `place_name`, `place_name_canonical`, `places`.`hash` AS `place_hash`, `latitude`, `longitude` ";
		sql += "FROM `river_droplet_places` ";
		sql += "INNER JOIN `places` ON (`places`.`id` = `place_id`) ";
		sql += "WHERE `rivers_droplets_id` IN :drop_ids ";
		sql += "AND `deleted` = 0 ";

		return sql;
	}

	/**
	 * Populates the buckets for each of the {@link Drop} in <code>drops</code>
	 * 
	 * @param drops
	 * @param queryingAccount
	 * @param dropSource
	 */
	public void populateBuckets(List<Drop> drops, Account queryingAccount,
			DropSource dropSource) {
		Map<Long, Integer> dropsIndex = new HashMap<Long, Integer>();
		int i = 0;
		for (Drop drop : drops) {
			dropsIndex.put(drop.getId(), i);
			i++;
		}

		Map<Long, Long> bucketDropsIndex = getBucketDropsIndex(dropsIndex.keySet(), dropSource);

		// Query to fetch the buckets
		String sql = "SELECT `buckets_droplets`.`droplet_id` AS `id`, `buckets`.`id` AS `bucket_id`, ";
		sql += "`buckets`.`bucket_name` ";
		sql += "FROM `buckets` ";
		sql += "INNER JOIN `buckets_droplets` ON (`buckets`.`id` = `buckets_droplets`.`bucket_id`) ";
		sql += "WHERE `buckets_droplets`.`droplet_id` IN (:dropletIds) ";
		sql += "AND `buckets`.`bucket_publish` = 1 ";
		sql += "UNION ALL ";
		sql += "SELECT `buckets_droplets`.`droplet_id` AS `id`, `buckets`.`id` AS `bucket_id`, ";
		sql += "`buckets`.`bucket_name` ";
		sql += "FROM `buckets` ";
		sql += "INNER JOIN `buckets_droplets` ON (`buckets`.`id` = `buckets_droplets`.`bucket_id`) ";
		sql += "LEFT JOIN `accounts` ON (`buckets`.`account_id` = `accounts`.`id` AND `buckets`.`account_id` = :accountId) ";
		sql += "LEFT JOIN `bucket_collaborators` ON (`bucket_collaborators`.`bucket_id` = `buckets`.`id` AND `bucket_collaborators`.`account_id` = :accountId) ";
		sql += "WHERE `buckets_droplets`.`droplet_id` IN (:dropletIds) ";
		sql += "AND `buckets`.`bucket_publish` = 0 ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("dropletIds", bucketDropsIndex.keySet());
		params.addValue("accountId", (Long)queryingAccount.getId());
		List<Map<String, Object>> results = this.namedJdbcTemplate.queryForList(sql, params);
	
		// Group the buckets per drop 
		Map<Long, List<Bucket>> dropBucketsMap = new HashMap<Long, List<Bucket>>();
		for (Map<String, Object> row: results) {
			
			Long dropId = ((BigInteger)row.get("id")).longValue();
			List<Bucket> dropBuckets = dropBucketsMap.get(dropId);
			if (dropBuckets == null) {
				dropBuckets = new ArrayList<Bucket>();
			}

			// Create the bucket
			Bucket bucket = new Bucket();
			bucket.setId(((BigInteger) row.get("bucket_id")).longValue());
			bucket.setName((String)row.get("bucket_name"));

			// Add to the list of buckets for the current drop
			dropBuckets.add(bucket);			
			dropBucketsMap.put(dropId, dropBuckets);				
		}
		
		// Populate the buckets for the submitted drops
		for (Map.Entry<Long, List<Bucket>> entry: dropBucketsMap.entrySet()) {
			Long dropId = bucketDropsIndex.get(entry.getKey());

			// Retrieve the drop
			Drop drop = drops.get(dropsIndex.get(dropId));
			drop.setBuckets(entry.getValue());
		}
	}
	
	private Map<Long, Long> getBucketDropsIndex(Set<Long> dropIds,
			DropSource dropSource) {
		String sql = null;

		switch(dropSource) {
			case BUCKET:
				sql = "SELECT `id`, `droplet_id` FROM `buckets_droplets` WHERE `id` IN :dropIds";
			break;
			case RIVER:
				sql = "SELECT `id`, `droplet_id` FROM `rivers_droplets` WHERE `id` IN :dropIds";
			break;
		}
		
		Query query = em.createNativeQuery(sql);
		query.setParameter("dropIds", dropIds);
		
		Map<Long, Long> bucketDropsIndex = new HashMap<Long, Long>();
		for (Object row: query.getResultList()) {
			Object[] rowArray = (Object[]) row;

			Long dropId = ((BigInteger) rowArray[0]).longValue();
			Long indexId = ((BigInteger)rowArray[1]).longValue();
			
			bucketDropsIndex.put(indexId, dropId);
		}
		
		return bucketDropsIndex;
	}
}
