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

import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Sequence;
import com.ushahidi.swiftriver.core.model.Tag;
import com.ushahidi.swiftriver.core.util.MD5Util;

@Repository
public class JpaTagDao extends AbstractJpaDao<Tag> implements TagDao {

	@Autowired
	private JpaSequenceDao sequenceDao;

	private NamedParameterJdbcTemplate namedJdbcTemplate;
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public Tag create(Tag t) {
		t = formatTag(t);
		t.setHash(getHash(t));
		return super.create(t);
	}

	/**
	 * Generate a hash for the given tag.
	 * 
	 * @param t
	 * @return
	 */
	public String getHash(Tag t) {
		return MD5Util.md5Hex(t.getTag() + t.getType());
	}

	/**
	 * Trim the given tag and make the tag type lower case.
	 * 
	 * @param tag
	 */
	private Tag formatTag(Tag tag) {
		tag.setTag(tag.getTag().trim());
		tag.setTagCanonical(tag.getTag().toLowerCase());
		tag.setType(tag.getType().trim().toLowerCase());
		return tag;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.TagDao#findByHash(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public Tag findByHash(String hash) {
		String sql = "FROM Tag WHERE hash = :hash";
		List<Tag> tags = (List<Tag>) em.createQuery(sql)
				.setParameter("hash", hash).getResultList();
		return tags.isEmpty() ? null : tags.get(0);
	}

	@Override
	public void getTags(List<Drop> drops) {
		// Get a lock
		Sequence seq = sequenceDao.findById("tags");

		Map<String, List<int[]>> newTagIndex = getNewTagIndex(drops);

		if (newTagIndex.size() > 0) {
			// Find tags that already exist in the db
			updateNewTagIndex(newTagIndex, drops);

			// Insert new tag into the db
			batchInsert(newTagIndex, drops, seq);
		}

	}

	/**
	 * Generates a mapping of tag hashes to list index for the given drops. Also
	 * populates an tag hash into the given list of drops.
	 * 
	 * @param drops
	 * @return
	 */
	private Map<String, List<int[]>> getNewTagIndex(List<Drop> drops) {
		Map<String, List<int[]>> newTagIndex = new HashMap<String, List<int[]>>();

		// Generate hashes for each new drops i.e. those without an id
		for (int i = 0; i < drops.size(); i++) {
			Drop drop = drops.get(i);
			
			if (drop.getTags() == null)
				continue;

			List<Tag> tags = drop.getTags();
			for (int j = 0; j < tags.size(); j++) {
				Tag tag = tags.get(j);
			
				// Cleanup the tag
				tag = formatTag(tag);

				String hash = getHash(tag);
				tag.setHash(hash);

				// Keep a record of where this hash is in the drop list
				List<int[]> indexes;
				if (newTagIndex.containsKey(hash)) {
					indexes = newTagIndex.get(hash);
				} else {
					indexes = new ArrayList<int[]>();
					newTagIndex.put(hash, indexes);
				}

				// Location of the tag in the drops array is an i,j tuple
				int[] loc = { i, j };
				indexes.add(loc);
			}
		}

		return newTagIndex;
	}

	/**
	 * For the given list of new drops, find those that the tag hash already
	 * exists in the db and update the drop entry with the existing id. Also
	 * remove the hash from the new tag index for those that already exist.
	 * 
	 * @param newTagIndex
	 * @param drops
	 */
	private void updateNewTagIndex(Map<String, List<int[]>> newTagIndex,
			List<Drop> drops) {
		// First find and update existing drops with their ids.
		String sql = "SELECT id, hash FROM tags WHERE hash IN (:hashes)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("hashes", newTagIndex.keySet());

		List<Map<String, Object>> results = this.namedJdbcTemplate
				.queryForList(sql, params);

		// Update id for the drops that were found
		for (Map<String, Object> result : results) {
			String hash = (String) result.get("hash");
			Long id = ((Number) result.get("id")).longValue();

			List<int[]> indexes = newTagIndex.get(hash);
			for (int[] index : indexes) {
				drops.get(index[0]).getTags().get(index[1]).setId(id);
			}

			// Hash is not for a new drop so remove it
			newTagIndex.remove(hash);
		}
	}

	/**
	 * Insert new tags in a single batch statement
	 * 
	 * @param newTagIndex
	 * @param drops
	 */
	private void batchInsert(final Map<String, List<int[]>> newTagIndex,
			final List<Drop> drops, Sequence seq) {

		final List<String> hashes = new ArrayList<String>();
		hashes.addAll(newTagIndex.keySet());
		final long startKey = sequenceDao.getIds(seq, hashes.size());

		String sql = "INSERT INTO tags (id, hash, tag, "
				+ "tag_canonical, tag_type) " + "VALUES (?,?,?,?,?)";

		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				String hash = hashes.get(i);

				// Update drops with the newly generated id
				for (int[] index : newTagIndex.get(hash)) {
					drops.get(index[0]).getTags().get(index[1])
							.setId(startKey + i);
				}

				int[] index = newTagIndex.get(hash).get(0);
				Tag tag = drops.get(index[0]).getTags().get(index[1]);
				ps.setLong(1, tag.getId());
				ps.setString(2, tag.getHash());
				ps.setString(3, tag.getTag());
				ps.setString(4, tag.getTagCanonical());
				ps.setString(5, tag.getType());
			}

			public int getBatchSize() {
				return hashes.size();
			}
		});
		
		// Update the droplet_tags table
		insertDropletTags(drops);

	}

	/**
	 * Populate the droplet tags table.
	 * 
	 * @param drops
	 */
	private void insertDropletTags(List<Drop> drops) {

		// List of drop IDs in the drops list
		List<Long> dropIds = new ArrayList<Long>();
		// List of tags in a drop
		Map<Long, Set<Long>> dropletTagsMap = new HashMap<Long, Set<Long>>(); 
		for (Drop drop : drops) {
			
			if (drop.getTags() == null)
				continue;
			
			dropIds.add(drop.getId());
			
			for (Tag tag : drop.getTags()) {
				Set<Long> tags = null;
				if(dropletTagsMap.containsKey(drop.getId())) {
					tags = dropletTagsMap.get(drop.getId());
				} else {
					tags = new HashSet<Long>();
					dropletTagsMap.put(drop.getId(), tags);
				}
				
				tags.add(tag.getId());
			}
		}

		// Find droplet tags that already exist in the db
		String sql = "SELECT droplet_id, tag_id FROM droplets_tags WHERE droplet_id in (:ids)";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("ids", dropIds);

		List<Map<String, Object>> results = this.namedJdbcTemplate
				.queryForList(sql, params);
		
		// Remove already existing droplet_tags from our Set
		for (Map<String, Object> result : results) {
			long dropletId = ((Number) result.get("droplet_id")).longValue();
			long tagId = ((Number) result.get("tag_id")).longValue();
			
			Set<Long> tagSet = dropletTagsMap.get(dropletId);
			if (tagSet != null) {
				tagSet.remove(tagId);	
			}
		}
		
		// Insert the remaining items in the set into the db
		sql = "INSERT INTO droplets_tags (droplet_id, tag_id) VALUES (?,?)";
		
		final List<long[]> dropletTagsList = new ArrayList<long[]>();
		for (Long dropletId : dropletTagsMap.keySet()) {
			for (Long tagId : dropletTagsMap.get(dropletId)) {
				long[] dropletTag = {dropletId, tagId};
				dropletTagsList.add(dropletTag);
			}
		}
		jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i)
					throws SQLException {
				long[] dropletTag = dropletTagsList.get(i);
				ps.setLong(1, dropletTag[0]);
				ps.setLong(2, dropletTag[1]);
			}

			public int getBatchSize() {
				return dropletTagsList.size();
			}
		});
	}

}
