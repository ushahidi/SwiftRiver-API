/**
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ushahidi.swiftriver.core.api.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.ushahidi.swiftriver.core.api.dao.ContextDropDao;
import com.ushahidi.swiftriver.core.api.dao.GenericDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketDrop;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Media;
import com.ushahidi.swiftriver.core.model.MediaThumbnail;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.Tag;

public abstract class AbstractJpaContextDropDao<T> extends AbstractJpaDao<T>
		implements ContextDropDao, GenericDao<T> {

	/**
	 * Query to be used for retrieving tag metadata for drops in a River/Bucket
	 */
	protected String tagsQuery = null;

	/**
	 * Query to be used for retrieving link metadata for drops in a River/Bucket
	 */
	protected String linksQuery = null;

	/**
	 * Query to be used for retrieving the drop image drops in a River/Bucket
	 */
	protected String dropImageQuery = null;

	/**
	 * Query to be used for retrieving media metadata for drops in a River/Bucket
	 */
	protected String mediaQuery = null;
	
	/**
	 * Query to be used for retrieving place metadata for drops in a River/Bucket
	 */
	protected String placesQuery = null;

	/**
	 * Query to be used for retrieving the context id for drops in a River/Bucket
	 */
	protected String contextDropQuery = null;

	@PersistenceContext
	protected EntityManager em;

	protected NamedParameterJdbcTemplate namedJdbcTemplate;

	public EntityManager getEm() {
		return em;
	}

	public void setEm(EntityManager em) {
		this.em = em;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.ContextDropDao#populateMetadata(
	 * java.util.List, com.ushahidi.swiftriver.core.model.Account)
	 */
	@Override
	public void populateMetadata(List<Drop> drops, Account queryingAccount) {
		if (drops.size() == 0) {
			return;
		}

		populateTags(drops);
		populateLinks(drops);
		populateMedia(drops);
		populatePlaces(drops);
		populateBuckets(drops, queryingAccount);
		populateForms(drops);
	}

	/**
	 * Populate tag metadata into the given drops.
	 * 
	 * @param drops
	 * @param dropSource
	 */
	public void populateTags(List<Drop> drops) {

		List<Long> dropIds = new ArrayList<Long>();
		for (Drop drop : drops) {
			dropIds.add(drop.getId());
		}

		Query query = em.createNativeQuery(tagsQuery);
		query.setParameter("drop_ids", dropIds);

		// Group the tags by drop id
		Map<Long, List<Tag>> tags = new HashMap<Long, List<Tag>>();
		for (Object oRow : query.getResultList()) {
			Object[] r = (Object[]) oRow;

			Long dropId = ((Number) r[0]).longValue();
			Tag tag = new Tag();
			tag.setId(((Number) r[1]).longValue());
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
	 * Populate link metadata into the given drops array.
	 * 
	 * @param drops
	 * @param dropSource
	 */
	public void populateLinks(List<Drop> drops) {

		List<Long> dropIds = new ArrayList<Long>();
		for (Drop drop : drops) {
			dropIds.add(drop.getId());
		}

		Query query = em.createNativeQuery(linksQuery);
		query.setParameter("drop_ids", dropIds);

		// Group the links by drop id
		Map<Long, List<Link>> links = new HashMap<Long, List<Link>>();
		for (Object oRow : query.getResultList()) {
			Object[] r = (Object[]) oRow;

			Long dropId = ((Number) r[0]).longValue();
			Link link = new Link();
			link.setId(((Number) r[1]).longValue());
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
	 * Populate media metadata into the given drops array.
	 * 
	 * @param drops
	 * @param dropSource
	 */
	public void populateMedia(List<Drop> drops) {

		Map<Long, Integer> dropIndex = new HashMap<Long, Integer>();
		int i = 0;
		for (Drop drop : drops) {
			dropIndex.put(drop.getId(), i);
			i++;
		}

		Query query = em.createNativeQuery(dropImageQuery);
		query.setParameter("drop_ids", dropIndex.keySet());

		Map<Long, Long> dropImagesMap = new HashMap<Long, Long>();
		for (Object oRow2 : query.getResultList()) {
			Object[] r2 = (Object[]) oRow2;
			dropImagesMap.put(((Number) r2[0]).longValue(),
					((Number) r2[1]).longValue());
		}

		query = em.createNativeQuery(mediaQuery);
		query.setParameter("drop_ids", dropIndex.keySet());

		// Group the media by drop id
		for (Object oRow : query.getResultList()) {
			Object[] r = (Object[]) oRow;

			Long dropId = ((Number) r[0]).longValue();
			Drop drop = drops.get(dropIndex.get(dropId));
			if (drop.getMedia() == null) {
				drop.setMedia(new ArrayList<Media>());
			}

			Long mediaId = ((Number) r[1]).longValue();
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
	 * Populate geo metadata into the given drops array.
	 * 
	 * @param drops
	 * @param dropSource
	 */
	public void populatePlaces(List<Drop> drops) {

		Map<Long, Integer> dropIndex = new HashMap<Long, Integer>();
		int i = 0;
		for (Drop drop : drops) {
			dropIndex.put(drop.getId(), i);
			i++;
		}

		Query query = em.createNativeQuery(placesQuery);
		query.setParameter("drop_ids", dropIndex.keySet());

		// Group the media by drop id
		Map<Long, Place> places = new HashMap<Long, Place>();
		for (Object oRow : query.getResultList()) {
			Object[] r = (Object[]) oRow;

			Long dropId = ((Number) r[0]).longValue();
			Drop drop = drops.get(dropIndex.get(dropId));
			if (drop.getPlaces() == null) {
				drop.setPlaces(new ArrayList<Place>());
			}

			Long placeId = ((Number) r[1]).longValue();
			Place p = places.get(placeId);

			if (p == null) {
				p = new Place();
				p.setId(placeId);
				p.setPlaceName((String) r[2]);
				p.setLatitude(((Number) r[5]).floatValue());
				p.setLongitude(((Number) r[6]).floatValue());

				places.put(placeId, p);
			}

			// Add place to drop
			if (!drop.getPlaces().contains(p)) {
				drop.getPlaces().add(p);
			}
		}
	}

	/**
	 * Populates the buckets for each of the {@link Drop} in <code>drops</code>
	 * 
	 * @param drops
	 * @param queryingAccount
	 * @param dropSource
	 */
	public void populateBuckets(List<Drop> drops, Account queryingAccount) {
		Map<Long, Integer> dropsIndex = new HashMap<Long, Integer>();
		int i = 0;
		for (Drop drop : drops) {
			dropsIndex.put(drop.getId(), i);
			i++;
		}

		Map<Long, Long> bucketDropsIndex = getBucketDropsIndex(dropsIndex.keySet());

		// Query to fetch the buckets
		String sql = "SELECT `buckets_droplets`.`droplet_id` AS `id`, `buckets_droplets`.`id` AS `bucket_drop_id`, ";
		sql += "`buckets`.`id` AS `bucket_id`, `buckets`.`bucket_name` ";
		sql += "FROM `buckets` ";
		sql += "INNER JOIN `buckets_droplets` ON (`buckets`.`id` = `buckets_droplets`.`bucket_id`) ";
		sql += "WHERE `buckets_droplets`.`droplet_id` IN (:dropletIds) ";
		sql += "AND `buckets`.`bucket_publish` = 1 ";
		sql += "UNION ALL ";
		sql += "SELECT `buckets_droplets`.`droplet_id` AS `id`, `buckets_droplets`.`id` AS `bucket_drop_iid`, ";
		sql += "`buckets`.`id` AS `bucket_id`, `buckets`.`bucket_name` ";
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
		Map<Long, List<BucketDrop>> dropBucketsMap = new HashMap<Long, List<BucketDrop>>();
		for (Map<String, Object> row: results) {
			
			Long dropId = ((Number)row.get("id")).longValue();
			List<BucketDrop> dropBuckets = dropBucketsMap.get(dropId);
			if (dropBuckets == null) {
				dropBuckets = new ArrayList<BucketDrop>();
			}

			// Create the bucket
			Bucket bucket = new Bucket();
			bucket.setId(((Number) row.get("bucket_id")).longValue());
			bucket.setName((String)row.get("bucket_name"));

			// Create the bucket drop
			BucketDrop bucketDrop = new BucketDrop();
			bucketDrop.setId(((Number)row.get("bucket_drop_id")).longValue());
			bucketDrop.setBucket(bucket);

			// Add to the list of buckets for the current drop
			dropBuckets.add(bucketDrop);			
			dropBucketsMap.put(dropId, dropBuckets);				
		}
		
		// Populate the buckets for the submitted drops
		for (Map.Entry<Long, List<BucketDrop>> entry: dropBucketsMap.entrySet()) {
			Long dropId = bucketDropsIndex.get(entry.getKey());

			// Retrieve the drop
			Drop drop = drops.get(dropsIndex.get(dropId));
			drop.setBucketDrops(entry.getValue());
		}
	}

	private Map<Long, Long> getBucketDropsIndex(Set<Long> dropIds) {
		Query query = em.createNativeQuery(contextDropQuery);
		query.setParameter("dropIds", dropIds);

		Map<Long, Long> bucketDropsIndex = new HashMap<Long, Long>();
		for (Object row : query.getResultList()) {
			Object[] rowArray = (Object[]) row;

			Long dropId = ((Number) rowArray[0]).longValue();
			Long indexId = ((Number) rowArray[1]).longValue();

			bucketDropsIndex.put(indexId, dropId);
		}

		return bucketDropsIndex;
	}
	
	/**
	 * Populate custom field for the given drops.
	 * 
	 * @param drops
	 */
	public abstract void populateForms(List<Drop> drops);
	
}
