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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.AccountDropLink;
import com.ushahidi.swiftriver.core.model.AccountDropPlace;
import com.ushahidi.swiftriver.core.model.AccountDropTag;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.DropComment;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Media;
import com.ushahidi.swiftriver.core.model.MediaThumbnail;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.Tag;

/**
 * @author ekala
 *
 */
@Repository
public class JpaDropDao extends AbstractJpaDao<Drop> implements DropDao {

	final Logger logger = LoggerFactory.getLogger(JpaDropDao.class);

	
	@Override
	public Drop create(Drop t) {
		t.setId(this.getSequenceNumber("droplets", 1));
		return this.em.merge(t);
	}

	/**
	 * @see DropDao#createDrops(Collection)
	 */
	public void createDrops(Collection<Drop> drops) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see DropDao#addLinks(long, Collection)
	 */
	public void addLinks(long dropId, Collection<Link> links) {
		findById(dropId).getLinks().addAll(links);
	}

	/**
	 * @see DropDao#addPlaces(long, Collection)
	 */
	public void addPlaces(long dropId, Collection<Place> places) {
		findById(dropId).getPlaces().addAll(places);
	}

	/**
	 * @see DropDao#addMultipleMedia(long, Collection)
	 */
	public void addMultipleMedia(long dropId, Collection<Media> media) {
		findById(dropId).getMedia().addAll(media);
	}

	/**
	 * @see DropDao#addTags(long, Collection)
	 */
	public void addTags(long dropId, Collection<Tag> tags) {
		findById(dropId).getTags().addAll(tags);
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
	public void populateMetadata(List<Drop> drops, Account queryingAccount) {
		if (drops.size() == 0) {
			return;
		}

		populateTags(drops, queryingAccount);
		populateLinks(drops, queryingAccount);
		populateMedia(drops, queryingAccount);
		populatePlaces(drops, queryingAccount);
		populateBuckets(drops);
	}

	/**
	 * Populate tag metadata into the given drops.
	 * 
	 * @param drops
	 * @param queryingAccount
	 */
	public void populateTags(List<Drop> drops, Account queryingAccount) {

		List<Long> dropIds = new ArrayList<Long>();
		for (Drop drop : drops) {
			dropIds.add(drop.getId());
		}

		String sql = "SELECT `droplet_id`, `tag_id` AS `id`, `tag`, `tag_canonical`, `tag_type` ";
		sql += "FROM `droplets_tags`  ";
		sql += "INNER JOIN `tags` ON (`tags`.`id` = `tag_id`)  ";
		sql += "WHERE `droplet_id` IN :drop_ids  ";
		sql += "AND `tags`.`id` NOT IN ( ";
		sql += "	SELECT `tag_id` FROM `account_droplet_tags`  ";
		sql += "	WHERE `account_id` = :account_id  ";
		sql += "	AND `droplet_id` IN :drop_ids  ";
		sql += "	AND `deleted` = 1) ";
		sql += "UNION ALL  ";
		sql += "SELECT `droplet_id`, `tag_id` AS `id`, `tag`, `tag_canonical`, `tag_type`  ";
		sql += "FROM `account_droplet_tags`  ";
		sql += "INNER JOIN `tags` ON (`tags`.`id` = `tag_id`)  ";
		sql += "WHERE `droplet_id` IN :drop_ids  ";
		sql += "AND `account_id` = :account_id  ";
		sql += "AND `deleted` = 0 ";

		Query query = em.createNativeQuery(sql);
		query.setParameter("drop_ids", dropIds);
		query.setParameter("account_id", queryingAccount.getId());

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
	 * Populate link metadata into the given drops array.
	 * 
	 * @param drops
	 * @param queryingAccount
	 */
	public void populateLinks(List<Drop> drops, Account queryingAccount) {

		List<Long> dropIds = new ArrayList<Long>();
		for (Drop drop : drops) {
			dropIds.add(drop.getId());
		}

		String sql = "SELECT `droplet_id`, `link_id` AS `id`, `url` ";
		sql += "FROM `droplets_links` ";
		sql += "INNER JOIN `links` ON (`links`.`id` = `link_id`) ";
		sql += "WHERE `droplet_id` IN :drop_ids ";
		sql += "AND `links`.`id` NOT IN ( ";
		sql += "SELECT `link_id` ";
		sql += "FROM `account_droplet_links` ";
		sql += "WHERE `account_id` = :account_id ";
		sql += "AND `droplet_id` IN :drop_ids ";
		sql += "AND `deleted` = 1) ";
		sql += "UNION ALL ";
		sql += "SELECT `droplet_id`, `link_id` AS `id`, `url` ";
		sql += "FROM `account_droplet_links` ";
		sql += "INNER JOIN `links` ON (`links`.`id` = `link_id`) ";
		sql += "WHERE `droplet_id` IN :drop_ids ";
		sql += "AND `account_id` = :account_id ";
		sql += "AND `deleted` = 0 ";

		Query query = em.createNativeQuery(sql);
		query.setParameter("drop_ids", dropIds);
		query.setParameter("account_id", queryingAccount.getId());

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
	 * Populate media metadata into the given drops array.
	 * 
	 * @param drops
	 * @param queryingAccount
	 */
	public void populateMedia(List<Drop> drops, Account queryingAccount) {

		Map<Long, Integer> dropIndex = new HashMap<Long, Integer>();
		int i = 0;
		for (Drop drop : drops) {
			dropIndex.put(drop.getId(), i);
			i++;
		}
		
		// Generate a map for drop images
		String sql = "SELECT `id`, `droplet_image` ";
		sql += "FROM `droplets` ";
		sql += "WHERE `droplet_image` > 0 ";
		sql += "AND `id` IN :drop_ids ";

		Query query = em.createNativeQuery(sql);
		query.setParameter("drop_ids", dropIndex.keySet());

		Map<Long, Long> dropImagesMap = new HashMap<Long, Long>();
		for (Object oRow2 : query.getResultList()) {
			Object[] r2 = (Object[]) oRow2;
			dropImagesMap.put(((BigInteger) r2[0]).longValue(),
					((BigInteger) r2[1]).longValue());
		}

		sql = "SELECT `droplet_id`, `media`.`id` AS `id`, `media`.`url` AS `url`, `type`, `media_thumbnails`.`size` AS `thumbnail_size`, `media_thumbnails`.`url` AS `thumbnail_url` ";
		sql += "FROM `droplets_media` ";
		sql += "INNER JOIN `media` ON (`media`.`id` = `media_id`) ";
		sql += "LEFT JOIN `media_thumbnails` ON (`media_thumbnails`.`media_id` = `media`.`id`) ";
		sql += "WHERE `droplet_id` IN :drop_ids ";
		sql += "AND `media`.`id` NOT IN ( ";
		sql += "SELECT `media_id` ";
		sql += "FROM `account_droplet_media` ";
		sql += "WHERE `account_id` = :account_id ";
		sql += "AND `droplet_id` IN :drop_ids ";
		sql += "AND `deleted` = 1) ";
		sql += "UNION ALL ";
		sql += "SELECT `droplet_id`, `media`.`id` AS `id`, `media`.`url` AS `url`, `type`, `media_thumbnails`.`size` AS `thumbnail_size`, `media_thumbnails`.`url` AS `thumbnail_url` ";
		sql += "FROM `account_droplet_media` ";
		sql += "INNER JOIN `media` ON (`media`.`id` = `media_id`) ";
		sql += "LEFT JOIN `media_thumbnails` ON (`media_thumbnails`.`media_id` = `media`.`id`) ";
		sql += "WHERE `droplet_id` IN :drop_ids ";
		sql += "AND `account_id` = :account_id ";
		sql += "AND `deleted` = 0; ";

		query = em.createNativeQuery(sql);
		query.setParameter("drop_ids", dropIndex.keySet());
		query.setParameter("account_id", queryingAccount.getId());

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
				mt.setSize((Integer)r[4]);
				mt.setUrl((String)r[5]);
				
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
	 * @param queryingAccount
	 */
	public void populatePlaces(List<Drop> drops, Account queryingAccount) {
		
		Map<Long, Integer> dropIndex = new HashMap<Long, Integer>();
		int i = 0;
		for (Drop drop : drops) {
			dropIndex.put(drop.getId(), i);
			i++;
		}

		String sql = "SELECT `droplet_id`, `place_id` AS `id`, `place_name`, `place_name_canonical`, `places`.`hash` AS `place_hash`, `latitude`, `longitude` ";
		sql += "FROM `droplets_places` ";
		sql += "INNER JOIN `places` ON (`places`.`id` = `place_id`) ";
		sql += "WHERE `droplet_id` IN :drop_ids ";
		sql += "AND `places`.`id` NOT IN ( ";
		sql += "SELECT `place_id` ";
		sql += "FROM `account_droplet_places` ";
		sql += "WHERE `account_id` = :account_id ";
		sql += "AND `droplet_id` IN :drop_ids ";
		sql += "AND `deleted` = 1) ";
		sql += "UNION ALL ";
		sql += "SELECT `droplet_id`, `place_id` AS `id`, `place_name`, `place_name_canonical`, `places`.`hash` AS `place_hash`, `latitude`, `longitude` ";
		sql += "FROM `account_droplet_places` ";
		sql += "INNER JOIN `places` ON (`places`.`id` = `place_id`) ";
		sql += "WHERE `droplet_id` IN :drop_ids ";
		sql += "AND `account_id` = :account_id ";
		sql += "AND `deleted` = 0 ";
		
		Query query = em.createNativeQuery(sql);
		query.setParameter("drop_ids", dropIndex.keySet());
		query.setParameter("account_id", queryingAccount.getId());
		
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
				p.setLatitude((Float)r[5]);
				p.setLongitude((Float)r[6]);

				places.put(placeId, p);
			} 

			// Add place to drop
			if (!drop.getPlaces().contains(p)) {
				drop.getPlaces().add(p);
			}
		}
	}
	
	/**
	 * Populates the buckets for each of the {@link Drop} 
	 * in <code>drops</code>
	 * 
	 * @param drops
	 */
	public void populateBuckets(List<Drop> drops) {
		Map<Long, Integer> dropsIndex = new HashMap<Long, Integer>();
		int i = 0;
		for (Drop drop: drops) {
			dropsIndex.put(drop.getId(), i);
			i++;
		}

		// Query to fetch the buckets
		String sql = "SELECT `buckets_droplets`.`droplet_id`, `buckets`.`id`, `buckets`.`bucket_name` ";
		sql += "FROM `buckets` ";
		sql += "INNER JOIN `buckets_droplets` ON (`buckets`.`id` = `buckets_droplets`.`bucket_id`) ";
		sql += "WHERE `buckets_droplets`.`droplet_id` IN :dropIds ";
		
		Query query = this.em.createNativeQuery(sql);
		query.setParameter("dropIds", dropsIndex.keySet());
		
		// Group the buckets by bucket id
		Map<Long, Bucket> buckets = new HashMap<Long, Bucket>();
		for (Object row: query.getResultList()) {
			Object[] rowArray = (Object[]) row;
			
			Long dropId = ((BigInteger)rowArray[0]).longValue();
			Drop drop = drops.get(dropsIndex.get(dropId));
			if (drop.getBuckets() == null) {
				drop.setBuckets(new ArrayList<Bucket>());
			}

			Long bucketId = ((BigInteger) rowArray[1]).longValue();
			Bucket bucket = buckets.get(bucketId);
			if (bucket == null) {
				bucket = new Bucket();
				bucket.setId(bucketId);
				bucket.setName((String) rowArray[2]);
				
				buckets.put(bucketId, bucket);
			}
			
			// Add bucket to the list of buckets
			if (!drop.getBuckets().contains(bucket)) {
				drop.getBuckets().add(bucket);
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.DropDao#findCommentById(java.lang.Long)
	 */
	public DropComment findCommentById(Long commentId) {
		return this.em.find(DropComment.class, commentId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.DropDao#deleteComment(com.ushahidi.swiftriver.core.model.DropComment)
	 */
	public void deleteComment(DropComment dropComment) {
		this.em.remove(dropComment);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.DropDao#addComment(com.ushahidi.swiftriver.core.model.Drop, com.ushahidi.swiftriver.core.model.Account, java.lang.String)
	 */
	public DropComment addComment(Drop drop, Account account, String commentText) {
		DropComment dropComment = new DropComment();
		
		dropComment.setDrop(drop);
		dropComment.setAccount(account);
		dropComment.setDeleted(false);
		dropComment.setCommentText(commentText);
		dropComment.setDateAdded(new Date());

		this.em.persist(dropComment);

		return dropComment;
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.DropDao#addLink(com.ushahidi.swiftriver.core.model.Drop, com.ushahidi.swiftriver.core.model.Account, java.lang.String)
	 */
	public void addLink(Drop drop, Account account, Link link) {
		// Add the link to the account
		AccountDropLink accountDropLink = new AccountDropLink();
		accountDropLink.setLink(link);
		accountDropLink.setAccount(account);
		accountDropLink.setDrop(drop);
		accountDropLink.setDeleted(false);

		this.em.persist(accountDropLink);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.DropDao#removeLink(com.ushahidi.swiftriver.core.model.Drop, com.ushahidi.swiftriver.core.model.Link, com.ushahidi.swiftriver.core.model.Account)
	 */
	@SuppressWarnings("unchecked")
	public void removeLink(Drop drop, Link link, Account account) {
		String sql = "FROM AccountDropLink WHERE account = :account ";
		sql += "AND drop = :drop AND link = :link";
		
		Query query = em.createQuery(sql);
		query.setParameter("link", link);
		query.setParameter("drop", drop);
		query.setParameter("account", account);
		
		List<AccountDropLink> links = (List<AccountDropLink>) query.getResultList();
		AccountDropLink accountDropLink = links.isEmpty() ? null : links.get(0); 
		
		if (accountDropLink != null && !accountDropLink.isDeleted()) {
			accountDropLink.setDeleted(true);
			this.em.merge(accountDropLink);
		} else if (accountDropLink == null) {
			// No records found
			accountDropLink = new AccountDropLink();
			accountDropLink.setAccount(account);
			accountDropLink.setDrop(drop);
			accountDropLink.setLink(link);
			accountDropLink.setDeleted(true);
			
			this.em.persist(accountDropLink);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.DropDao#addPlace(com.ushahidi.swiftriver.core.model.Drop, com.ushahidi.swiftriver.core.model.Account, com.ushahidi.swiftriver.core.model.Place)
	 */
	public void addPlace(Drop drop, Account account, Place place) {
		AccountDropPlace accountDropPlace = new AccountDropPlace();
		accountDropPlace.setDrop(drop);
		accountDropPlace.setPlace(place);
		accountDropPlace.setAccount(account);
		
		this.em.persist(accountDropPlace);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.DropDao#removePlace(com.ushahidi.swiftriver.core.model.Drop, com.ushahidi.swiftriver.core.model.Place, com.ushahidi.swiftriver.core.model.Account)
	 */
	@SuppressWarnings("unchecked")
	public void removePlace(Drop drop, Place place, Account account) {
		String sql = "FROM AccountDropPlace WHERE account = :account ";
		sql += "AND drop = :drop and place = :place";
		
		Query query = em.createQuery(sql);
		query.setParameter("place", place);
		query.setParameter("drop", drop);
		query.setParameter("account", account);

		List<AccountDropPlace> places = (List<AccountDropPlace>) query.getResultList();
		AccountDropPlace accountDropPlace = (places.isEmpty()) ? null : places.get(0);

		if (accountDropPlace != null && !accountDropPlace.isDeleted()) {
			accountDropPlace.setDeleted(true);
			this.em.merge(accountDropPlace);
		} else if (accountDropPlace == null) {
			// No records found
			accountDropPlace = new AccountDropPlace();
			accountDropPlace.setAccount(account);
			accountDropPlace.setDrop(drop);
			accountDropPlace.setPlace(place);
			accountDropPlace.setDeleted(true);
			
			this.em.persist(accountDropPlace);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.DropDao#addTag(com.ushahidi.swiftriver.core.model.Drop, com.ushahidi.swiftriver.core.model.Tag, com.ushahidi.swiftriver.core.model.Account)
	 */
	public void addTag(Drop drop, Tag tag, Account account) {
		AccountDropTag accountDropTag = new AccountDropTag();
		accountDropTag.setDrop(drop);
		accountDropTag.setAccount(account);
		accountDropTag.setTag(tag);
		accountDropTag.setDeleted(false);
		
		this.em.persist(accountDropTag);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.DropDao#removeTag(com.ushahidi.swiftriver.core.model.Drop, com.ushahidi.swiftriver.core.model.Tag, com.ushahidi.swiftriver.core.model.Account)
	 */
	@SuppressWarnings("unchecked")
	public void removeTag(Drop drop, Tag tag, Account account) {
		String sql = "FROM AccountDropTag a WHERE a.account = :account ";
		sql += "AND a.drop = :drop AND a.tag = :tag";

		Query query = em.createQuery(sql);
		query.setParameter("account", account);
		query.setParameter("drop", drop);
		query.setParameter("tag", tag);

		List<AccountDropTag> tags = (List<AccountDropTag>)query.getResultList();
		AccountDropTag accountDropTag = tags.isEmpty() ? null : tags.get(0); 
		
		if (accountDropTag != null && !accountDropTag.isDeleted()) {
			accountDropTag.setDeleted(true);
			this.em.merge(accountDropTag);
		} else if (accountDropTag == null) {
			// No records found
			accountDropTag = new AccountDropTag();
			accountDropTag.setAccount(account);
			accountDropTag.setDrop(drop);
			accountDropTag.setTag(tag);
			accountDropTag.setDeleted(true);
			
			this.em.persist(accountDropTag);
		}
		
	}
}
