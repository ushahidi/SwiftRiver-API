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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.TypedQuery;
import javax.sql.DataSource;

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
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketDrop;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverTagTrend;
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

		// Populate metadata
		tagDao.getTags(drops);
		linkDao.getLinks(drops);
		placeDao.getPlaces(drops);
		mediaDao.getMedia(drops);

		// Add drops to their respective rivers
		insertRiverDrops(drops);
		
		// Add drops to their respective buckets
		insertBucketDrops(drops);
		
		// TODO Mark as read


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
		Map<Long, Set<Long>> dropChannelsMap = new HashMap<Long, Set<Long>>();

		// Registry for all channels and rivers
		Set<Long> allChannelIds = new HashSet<Long>();

		int i = 0;
		for (Drop drop : drops) {
			if (drop.getRiverIds() == null || drop.getChannelIds() == null) {
				logger.debug("No rivers or channels for drop {}", drop.getId());
				continue;
			}

			Set<Long> rivers = new HashSet<Long>();
			Set<Long> channels = new HashSet<Long>();

			rivers.addAll(drop.getRiverIds());
			channels.addAll(drop.getChannelIds());

			dropRiversMap.put(drop.getId(), rivers);
			dropChannelsMap.put(drop.getId(), channels);
			
			allChannelIds.addAll(channels);

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

		// Remove existing rivers_droplets entries from our Set
		for (Map<String, Object> row : results) {
			Long dropletId = ((Number) row.get("droplet_id")).longValue();
			Long riverId = ((Number) row.get("river_id")).longValue();

			Set<Long> riverSet = dropRiversMap.remove(dropletId);
			if (riverSet != null) {
				riverSet.remove(riverId);

				// Only add back the destination rivers if the set is non empty
				if (!riverSet.isEmpty()) {
					dropRiversMap.put(dropletId, riverSet);
				}
			}
		}

		// If all drops are duplicates, return early
		if (dropRiversMap.isEmpty()) {
			logger.info("No drops to add to the rivers");
			return;
		}

		// Associate the channels with rivers
		sql = "SELECT id, river_id FROM river_channels WHERE id IN (:channelIds)";
		MapSqlParameterSource channelParams = new MapSqlParameterSource();
		channelParams.addValue("channelIds", allChannelIds);
		
		Map<Long, Long> riverChannelsMap = new HashMap<Long, Long>();
		for (Map<String, Object> row: namedJdbcTemplate.queryForList(sql,
				channelParams)) {

			Long channelId = ((Number) row.get("id")).longValue();
			Long riverId = ((Number) row.get("river_id")).longValue();
			
			riverChannelsMap.put(channelId, riverId);
		}
		
		// Map to hold the association between a drop, river and channel
		// During the association, we verify that the river is in the drop's
		// destination river list
		final List<Map<String, Long>> riverDropChannelList = new ArrayList<Map<String,Long>>();
		for (Long dropletId: dropChannelsMap.keySet()) {
			for (Long channelId: dropChannelsMap.get(dropletId)) {
				if (riverChannelsMap.containsKey(channelId)) {
					Long riverId = riverChannelsMap.get(channelId);

					if (dropRiversMap.containsKey(dropletId) && 
							dropRiversMap.get(dropletId).contains(riverId)) {
						Map<String, Long> entry = new HashMap<String, Long>();
						entry.put("dropletId", dropletId);
						entry.put("channelId", channelId);
						entry.put("riverId", riverId);
						riverDropChannelList.add(entry);
					}
				}
			}
		}

		// Insert the remaining items in the set into the DB
		sql = "INSERT INTO `rivers_droplets` (`id`, `droplet_id`, `river_id`, " +
				"`river_channel_id`, `droplet_date_pub`) " +
				"VALUES (?, ?, ?, ?, ?)";

		final long startKey = sequenceDao.getIds(seq, riverDropChannelList.size());

		// Map to hold to hold the no. of drops created per channel
		final Map<Long, Long> channelDropCountMap = new HashMap<Long, Long>();

		// A map to hold the new max_drop_id and drop_count per river
		final Map<Long, long[]> riverDropsMap = new HashMap<Long, long[]>();
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Map<String, Long> dropEntry = riverDropChannelList.get(i);
				long id = startKey + i;

				Long dropletId = dropEntry.get("dropletId");
				Long riverId = dropEntry.get("riverId");
				Long channelId = dropEntry.get("channelId");
				Drop drop = drops.get(dropIndex.get(dropletId));

				ps.setLong(1, id);
				ps.setLong(2, dropletId);
				ps.setLong(3, riverId);
				ps.setLong(4, channelId);
				ps.setTimestamp(5, new java.sql.Timestamp(drop
						.getDatePublished().getTime()));

				// Get updated max_drop_id and drop_count for the rivers table
				long[] update = riverDropsMap.get(riverId);
				if (update == null) {
					long[] u = { id, 1 };
					riverDropsMap.put(riverId, u);
				} else {
					update[0] = Math.max(update[0], id);
					update[1] = update[1] + 1;
				}
				
				// Update the drop count for the channel
				Long channelDropCount = channelDropCountMap.remove(channelId);
				channelDropCount = (channelDropCount == null) 
						? 1L : Long.valueOf(channelDropCount.longValue() + 1);
				channelDropCountMap.put(channelId, channelDropCount);
			}

			public int getBatchSize() {
				return riverDropChannelList.size();
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
		});
		
		// Update the drop_count in TABLE `river_channels`
		sql = "UPDATE river_channels SET drop_count = drop_count + ? WHERE id = ?";
		final List<Entry<Long, Long>> riverChannelUpdate = new ArrayList<Entry<Long,Long>>();
		riverChannelUpdate.addAll(channelDropCountMap.entrySet());

		this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Entry<Long, Long> entry = riverChannelUpdate.get(i);
				ps.setLong(1, entry.getValue());
				ps.setLong(2, entry.getKey());
			}
			
			@Override
			public int getBatchSize() {
				return riverChannelUpdate.size();
			}
		});

		// Insert the trend data
		try {
			insertRiverTagTrends(drops, dropIndex, riverDropChannelList);
		} catch (Exception e) {
			logger.error("An error occurred while inserting the trend data", e);
		}
		
	}
	
	/**
	 * Populates the river_tag_trends table
	 * 
	 * @param drops
	 * @param dropIndex
	 * @param riverDropChannelList
	 * @throws Exception
	 */
	private void insertRiverTagTrends(List<Drop> drops, Map<Long, Integer> dropIndex,
			List<Map<String, Long>> riverDropChannelList) throws Exception {

		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:00:00");
		Map<String,RiverTagTrend> trendsData = new HashMap<String, RiverTagTrend>();

		for(Map<String, Long> entry: riverDropChannelList) {
			Long dropletId = entry.get("dropletId");
			Long riverId = entry.get("riverId");
			
			River river = new River();
			river.setId(riverId);

			Drop drop = drops.get(dropIndex.get(dropletId));
			String datePublishedStr = dateFormat.format(drop.getDatePublished());
			Date datePublished = dateFormat.parse(datePublishedStr);

			// Tags
			if (drop.getTags() != null) {
				for (Tag tag: drop.getTags()) {
					String hash = MD5Util.md5Hex(riverId.toString(),
							datePublishedStr, tag.getTag(), tag.getType());

					RiverTagTrend tagTrend = trendsData.remove(hash);
					if (tagTrend == null) {
						tagTrend = new RiverTagTrend();
						tagTrend.setRiver(river);
						tagTrend.setDatePublished(datePublished);
						tagTrend.setTag(tag.getTag());
						tagTrend.setTagType(tag.getType());
						tagTrend.setHash(hash);
						tagTrend.setCount(1L);
					} else {
						Long count = new Long(tagTrend.getCount() + 1L);
						tagTrend.setCount(count);
					}

					trendsData.put(hash, tagTrend);
				}
			}

			// Places
			if (drop.getPlaces() != null) {
				for (Place place: drop.getPlaces()) {
					String hash = MD5Util.md5Hex(riverId.toString(),
							datePublishedStr,  place.getPlaceName(), "place");

					RiverTagTrend tagTrend = trendsData.remove(hash);
					if (tagTrend == null) {
						tagTrend = new RiverTagTrend();
						tagTrend.setRiver(river);
						tagTrend.setDatePublished(datePublished);
						tagTrend.setTag(place.getPlaceName());
						tagTrend.setTagType("place");
						tagTrend.setHash(hash);
						tagTrend.setCount(1L);
					} else {
						Long count = new Long(tagTrend.getCount() + 1L);
						tagTrend.setCount(count);
					}

					trendsData.put(hash, tagTrend);
				}
			}
		}

		if (trendsData.keySet().isEmpty())
			return;

		// Check for existing trends
		String sql = "SELECT `id`, `hash` FROM `river_tag_trends` WHERE `hash` IN (:hashes)";
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("hashes", trendsData.keySet());

		// List of trend IDs whose count is to be updated
		final List<long[]> trendCountUpdate = new ArrayList<long[]>();
		for(Map<String, Object> row: namedJdbcTemplate.queryForList(sql, params)) {
			String hash = (String) row.get("hash");
			long trendId = ((Number) row.get("id")).longValue();

			RiverTagTrend tagTrend = trendsData.remove(hash);

			long[] counters = {trendId, tagTrend.getCount()};
			trendCountUpdate.add(counters);
		}

		// Update existing counters
		if (!trendCountUpdate.isEmpty()) {
			sql  = "UPDATE `river_tag_trends` SET `count` = `count` + ? WHERE `id` = ?";

			jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					long[] updateIndex = trendCountUpdate.get(i);
					ps.setLong(1, updateIndex[1]);
					ps.setLong(2, updateIndex[0]);
				}
				
				public int getBatchSize() {
					return trendCountUpdate.size();
				}
			});
		}
		
		if (trendsData.isEmpty()) {
			return;
		}

		Sequence sequence = sequenceDao.findById("river_tag_trends");
		final long startKey = sequenceDao.getIds(sequence, trendsData.size());

		// SQL to update the river_tag_trends table
		sql = "INSERT INTO river_tag_trends(`id`, `hash`, `river_id`, `date_pub`, `tag`, " +
				"`tag_type`, `count`) VALUES(?, ?, ?, ?, ?, ?, ?)";

		final List<Entry<String, RiverTagTrend>> tagTrendsList = new ArrayList<Entry<String,RiverTagTrend>>();
		tagTrendsList.addAll(trendsData.entrySet());

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				long id = startKey + i;

				Entry<String, RiverTagTrend> entry = tagTrendsList.get(i);
				RiverTagTrend tagTrend = entry.getValue();

				ps.setLong(1, id);
				ps.setString(2, entry.getKey());
				ps.setLong(3, tagTrend.getRiver().getId());
				ps.setTimestamp(4, new java.sql.Timestamp(tagTrend.getDatePublished().getTime()));
				ps.setString(5, tagTrend.getTag());
				ps.setString(6, tagTrend.getTagType());
				ps.setLong(7, tagTrend.getCount());
			}
			
			public int getBatchSize() {
				return tagTrendsList.size();
			}
		});
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

		// Store for the no. of drops inserted per bucket
		final Map<Long, Integer> bucketDropCount = new HashMap<Long, Integer>();

		// Query for populating TABLE buckets_droplets
		String insertSQL = "INSERT INTO `buckets_droplets` (`bucket_id`, `droplet_id`, `droplet_date_added`) " +
				"VALUES (?, ?, ?)";

		jdbcTemplate.batchUpdate(insertSQL, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int index) throws SQLException {
				Long[] bucketDrop = bucketDropList.get(index);
				Long bucketId = bucketDrop[0];

				ps.setLong(1, bucketId);
				ps.setLong(2, bucketDrop[1]);
				ps.setTimestamp(3, 
						new java.sql.Timestamp(new Date().getTime()));

				Integer count = bucketDropCount.remove(bucketId);
				count = (count == null) ? 1 : new Integer(count.intValue() + 1);
				bucketDropCount.put(bucketId, count);
			}

			@Override
			public int getBatchSize() {
				return bucketDropList.size();
			}
		});

		// Update the drop count for the updated buckets
		final List<Entry<Long, Integer>> bucketDropCountList = new ArrayList<Map.Entry<Long, Integer>>();
		bucketDropCountList.addAll(bucketDropCount.entrySet());

		String updateSQL = "UPDATE `buckets` SET `drop_count` = `drop_count` + ? WHERE `id` = ?";
		jdbcTemplate.batchUpdate(updateSQL, new BatchPreparedStatementSetter() {
			
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Entry<Long, Integer> entry = bucketDropCountList.get(i);
				ps.setLong(1, entry.getValue());
				ps.setLong(2, entry.getKey());
			}
			
			public int getBatchSize() {
				return bucketDropCountList.size();
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.DropDao#findAll(java.util.List)
	 */
	public List<Drop> findAll(List<Long> dropIds) {
		// JPQL query string
		String qlString = "FROM Drop WHERE id IN :dropIds";

		TypedQuery<Drop> query = em.createQuery(qlString, Drop.class);
		query.setParameter("dropIds", dropIds);

		List<Drop> drops = query.getResultList();

		// Store the ID of each drop against its index in the drops list
		Map<Long, Integer> dropsIndex = new HashMap<Long, Integer>();
		int i = 0;
		for (Drop drop: drops) {
			dropsIndex.put(drop.getId(), i);
			i++;
		}
		
		// Fetch the bucket drops
		String qlBucketDrops = "FROM BucketDrop b WHERE b.drop.id IN :dropIds AND b.bucket.published = 1";
		TypedQuery<BucketDrop> bucketDropQuery = em.createQuery(qlBucketDrops, BucketDrop.class);
		bucketDropQuery.setParameter("dropIds", dropIds);

		for (BucketDrop bucketDrop: bucketDropQuery.getResultList()) {
			int dropIndex = dropsIndex.get(bucketDrop.getDrop().getId());
			Drop drop =  drops.get(dropIndex);
			if (drop.getBuckets() == null) {
				drop.setBuckets(new ArrayList<Bucket>());
			}
			drop.getBuckets().add(bucketDrop.getBucket());
		}

		// Get the list of buckets
		return drops;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.DropDao#findAll(long, int)
	 */
	public List<Drop> findAll(long sinceId, int batchSize) {
		TypedQuery<Drop> query = em.createQuery("FROM Drop d WHERE d.id > :sinceId ORDER BY d.id ASC", 
				Drop.class);
		query.setParameter("sinceId", sinceId);
		query.setMaxResults(batchSize);

		return query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.DropDao#populateRiverIds(java.util.List)
	 */
	public void populateRiverIds(List<Drop> drops) {
		// Store the drop index
		Map<Long, Integer> dropIndex = new HashMap<Long, Integer>();
		List<Long> dropIds = new ArrayList<Long>();
		int index = 0;
		for (Drop drop: drops) {
			dropIds.add(drop.getId());
			dropIndex.put(drop.getId(), index);
			index++;
		}

		String sql = "SELECT `droplet_id`, `river_id` " +
				"FROM `rivers_droplets` WHERE `droplet_id` IN (:dropIds)";
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("dropIds", dropIds);

		for(Map<String, Object> row: namedJdbcTemplate.queryForList(sql, paramMap)) {
			Long dropId = ((Number) row.get("droplet_id")).longValue();
			Long riverId = ((Number) row.get("river_id")).longValue();
			
			Drop drop = drops.get(dropIndex.get(dropId));
			if (drop.getRiverIds() == null) {
				drop.setRiverIds(new ArrayList<Long>());
			}
			drop.getRiverIds().add(riverId);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.DropDao#populateBucketIds(java.util.List)
	 */
	public void populateBucketIds(List<Drop> drops) {
		// Store the drop index
		Map<Long, Integer> dropIndex = new HashMap<Long, Integer>();
		List<Long> dropIds = new ArrayList<Long>();
		int index = 0;
		for (Drop drop: drops) {
			dropIds.add(drop.getId());
			dropIndex.put(drop.getId(), index);
			index++;
		}
		
		String sql = "SELECT `droplet_id`, `bucket_id` " +
				"FROM `buckets_droplets` WHERE `droplet_id` IN (:dropIds)";

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("dropIds", dropIds);

		for(Map<String, Object> row: namedJdbcTemplate.queryForList(sql, paramMap)) {
			Long dropId = ((Number) row.get("droplet_id")).longValue();
			Long bucketId = ((Number) row.get("bucket_id")).longValue();
			
			Drop drop = drops.get(dropIndex.get(dropId));
			if (drop.getBucketIds() == null) {
				drop.setBucketIds(new ArrayList<Long>());
			}
			drop.getBucketIds().add(bucketId);
		}
	}
}
