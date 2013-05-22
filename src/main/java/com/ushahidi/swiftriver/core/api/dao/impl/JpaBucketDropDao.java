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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.BucketDropDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketDrop;
import com.ushahidi.swiftriver.core.model.BucketDropComment;
import com.ushahidi.swiftriver.core.model.BucketDropForm;
import com.ushahidi.swiftriver.core.model.BucketDropFormField;
import com.ushahidi.swiftriver.core.model.BucketDropLink;
import com.ushahidi.swiftriver.core.model.BucketDropPlace;
import com.ushahidi.swiftriver.core.model.BucketDropTag;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.DropForm;
import com.ushahidi.swiftriver.core.model.Form;
import com.ushahidi.swiftriver.core.model.FormField;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.RiverDrop;
import com.ushahidi.swiftriver.core.model.RiverDropComment;
import com.ushahidi.swiftriver.core.model.RiverDropLink;
import com.ushahidi.swiftriver.core.model.RiverDropPlace;
import com.ushahidi.swiftriver.core.model.RiverDropTag;
import com.ushahidi.swiftriver.core.model.Tag;

@Repository
public class JpaBucketDropDao extends AbstractJpaContextDropDao<BucketDrop>
		implements BucketDropDao {

	public JpaBucketDropDao() {
		// Query for retrieving tag metadata
		tagsQuery = "SELECT buckets_droplets.id AS droplet_id, tag_id AS id, tag, tag_canonical, tag_type ";
		tagsQuery += "FROM droplets_tags  ";
		tagsQuery += "INNER JOIN tags ON (tags.id = tag_id)  ";
		tagsQuery += "INNER JOIN buckets_droplets ON (buckets_droplets.droplet_id = droplets_tags.droplet_id)";
		tagsQuery += "WHERE buckets_droplets.id IN :drop_ids  ";
		tagsQuery += "AND tags.id NOT IN ( ";
		tagsQuery += "	SELECT tag_id FROM bucket_droplet_tags  ";
		tagsQuery += "	WHERE buckets_droplets_id IN :drop_ids  ";
		tagsQuery += "	AND deleted = 1) ";
		tagsQuery += "UNION ALL  ";
		tagsQuery += "SELECT buckets_droplets_id AS droplet_id, tag_id AS id, tag, tag_canonical, tag_type  ";
		tagsQuery += "FROM bucket_droplet_tags ";
		tagsQuery += "INNER JOIN tags ON (tags.id = tag_id)  ";
		tagsQuery += "WHERE buckets_droplets_id IN :drop_ids  ";
		tagsQuery += "AND deleted = 0 ";

		// Query for retrieving link metadata
		linksQuery = "SELECT buckets_droplets.id AS droplet_id, link_id AS id, url ";
		linksQuery += "FROM droplets_links  ";
		linksQuery += "INNER JOIN links ON (links.id = link_id)  ";
		linksQuery += "INNER JOIN buckets_droplets ON (buckets_droplets.droplet_id = droplets_links.droplet_id)";
		linksQuery += "WHERE buckets_droplets.id IN :drop_ids  ";
		linksQuery += "AND links.id NOT IN ( ";
		linksQuery += "	SELECT link_id FROM bucket_droplet_links  ";
		linksQuery += "	WHERE buckets_droplets_id IN :drop_ids  ";
		linksQuery += "	AND deleted = 1) ";
		linksQuery += "UNION ALL  ";
		linksQuery += "SELECT buckets_droplets_id AS droplet_id, link_id AS id, url  ";
		linksQuery += "FROM bucket_droplet_links  ";
		linksQuery += "INNER JOIN links ON (links.id = link_id)  ";
		linksQuery += "WHERE buckets_droplets_id IN :drop_ids  ";
		linksQuery += "AND deleted = 0 ";

		// Query for retrieving the drop image
		dropImageQuery = "SELECT buckets_droplets.id, droplet_image FROM droplets ";
		dropImageQuery += "INNER JOIN buckets_droplets ON (buckets_droplets.droplet_id = droplets.id) ";
		dropImageQuery += "WHERE buckets_droplets.id IN :drop_ids ";
		dropImageQuery += "AND droplets.droplet_image > 0";

		// Query for retrieving media metadata
		mediaQuery = "SELECT buckets_droplets.id AS droplet_id, media.id AS id, media.url AS url, type, media_thumbnails.size AS thumbnail_size, ";
		mediaQuery += "media_thumbnails.url AS thumbnail_url ";
		mediaQuery += "FROM droplets_media ";
		mediaQuery += "INNER JOIN media ON (media.id = droplets_media.media_id) ";
		mediaQuery += "INNER JOIN buckets_droplets ON (buckets_droplets.droplet_id = droplets_media.droplet_id) ";
		mediaQuery += "LEFT JOIN media_thumbnails ON (media_thumbnails.media_id = media.id) ";
		mediaQuery += "WHERE buckets_droplets.id IN :drop_ids ";
		mediaQuery += "AND media.id NOT IN ( ";
		mediaQuery += "	SELECT media_id ";
		mediaQuery += "	FROM bucket_droplet_media ";
		mediaQuery += "	WHERE buckets_droplets_id IN :drop_ids ";
		mediaQuery += "	AND deleted = 1) ";
		mediaQuery += "UNION ALL ";
		mediaQuery += "SELECT buckets_droplets_id AS droplet_id, media.id AS id, media.url AS url, type, media_thumbnails.size AS thumbnail_size, media_thumbnails.url AS thumbnail_url ";
		mediaQuery += "FROM bucket_droplet_media ";
		mediaQuery += "INNER JOIN media ON (media.id = bucket_droplet_media.media_id) ";
		mediaQuery += "LEFT JOIN media_thumbnails ON (media_thumbnails.media_id = media.id) ";
		mediaQuery += "WHERE buckets_droplets_id IN :drop_ids ";
		mediaQuery += "AND deleted = 0; ";

		// Query for retrieving place metadata
		placesQuery = "SELECT buckets_droplets.id AS droplet_id, place_id AS id, place_name, place_name_canonical, ";
		placesQuery += "places.hash AS place_hash, latitude, longitude ";
		placesQuery += "FROM droplets_places ";
		placesQuery += "INNER JOIN places ON (places.id = place_id) ";
		placesQuery += "INNER JOIN buckets_droplets ON (buckets_droplets.droplet_id = droplets_places.droplet_id) ";
		placesQuery += "WHERE buckets_droplets.id IN :drop_ids ";
		placesQuery += "AND places.id NOT IN ( ";
		placesQuery += "	SELECT place_id ";
		placesQuery += "	FROM bucket_droplet_places ";
		placesQuery += "	WHERE buckets_droplets_id IN :drop_ids ";
		placesQuery += "	AND deleted = 1) ";
		placesQuery += "UNION ALL ";
		placesQuery += "SELECT buckets_droplets_id AS droplet_id, place_id AS id, place_name, place_name_canonical, places.hash AS place_hash, latitude, longitude ";
		placesQuery += "FROM bucket_droplet_places ";
		placesQuery += "INNER JOIN places ON (places.id = place_id) ";
		placesQuery += "WHERE buckets_droplets_id IN :drop_ids ";
		placesQuery += "AND deleted = 0 ";

		// Query for retrieving the BucketDrop id for the given drops
		contextDropQuery = "SELECT id, droplet_id FROM buckets_droplets WHERE id IN :dropIds";

	}

	@Override
	public BucketDrop create(BucketDrop t) {
		t.setVeracity(1L);
		t.setDateAdded(new Date());
		return super.create(t);
	}
	

	@Override
	public void delete(BucketDrop t) {
		String sql = String.format(
				"DELETE FROM `bucket_droplets_read` WHERE `buckets_droplets_id` = %d",
				t.getId());
		em.createNativeQuery(sql).executeUpdate();
		super.delete(t);
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.BucketDropDao#addTag(com.ushahidi
	 * .swiftriver.core.model.BucketDrop,
	 * com.ushahidi.swiftriver.core.model.Tag)
	 */
	public void addTag(BucketDrop bucketDrop, Tag tag) {
		BucketDropTag bucketDropTag = new BucketDropTag();

		bucketDropTag.setBucketDrop(bucketDrop);
		bucketDropTag.setTag(tag);
		bucketDropTag.setDeleted(false);

		this.em.persist(bucketDropTag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.BucketDropDao#findTag(com.ushahidi
	 * .swiftriver.core.model.BucketDrop,
	 * com.ushahidi.swiftriver.core.model.Tag)
	 */
	public BucketDropTag findTag(BucketDrop bucketDrop, Tag tag) {
		String sql = "FROM BucketDropTag WHERE bucketDrop = :bucketDrop AND tag = :tag";

		TypedQuery<BucketDropTag> query = em.createQuery(sql,
				BucketDropTag.class);
		query.setParameter("bucketDrop", bucketDrop);
		query.setParameter("tag", tag);

		List<BucketDropTag> dropTags = query.getResultList();
		return dropTags.isEmpty() ? null : dropTags.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.BucketDropDao#deleteTag(com.ushahidi
	 * .swiftriver.core.model.BucketDrop,
	 * com.ushahidi.swiftriver.core.model.Tag)
	 */
	public boolean deleteTag(BucketDrop bucketDrop, Tag tag) {
		BucketDropTag bucketDropTag = findTag(bucketDrop, tag);

		if (bucketDropTag != null && bucketDropTag.isDeleted()) {
			return false;
		} else if (bucketDropTag != null && !bucketDropTag.isDeleted()) {
			bucketDropTag.setDeleted(true);
			this.em.merge(bucketDropTag);
		} else {
			bucketDropTag = new BucketDropTag();
			bucketDropTag.setBucketDrop(bucketDrop);
			bucketDropTag.setTag(tag);
			bucketDropTag.setDeleted(true);

			this.em.persist(bucketDropTag);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.BucketDropDao#addLink(com.ushahidi
	 * .swiftriver.core.model.BucketDrop,
	 * com.ushahidi.swiftriver.core.model.Link)
	 */
	public void addLink(BucketDrop bucketDrop, Link link) {
		BucketDropLink bucketDropLink = new BucketDropLink();
		bucketDropLink.setBucketDrop(bucketDrop);
		bucketDropLink.setLink(link);
		bucketDropLink.setDeleted(false);

		this.em.persist(bucketDropLink);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.BucketDropDao#findLink(com.ushahidi
	 * .swiftriver.core.model.BucketDrop,
	 * com.ushahidi.swiftriver.core.model.Link)
	 */
	public BucketDropLink findLink(BucketDrop bucketDrop, Link link) {
		String sql = "FROM BucketDropLink WHERE bucketDrop = :bucketDrop AND link = :link";

		TypedQuery<BucketDropLink> query = em.createQuery(sql,
				BucketDropLink.class);
		query.setParameter("bucketDrop", bucketDrop);
		query.setParameter("link", link);

		List<BucketDropLink> dropLinks = query.getResultList();
		return dropLinks.isEmpty() ? null : dropLinks.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.BucketDropDao#deleteLink(com.ushahidi
	 * .swiftriver.core.model.BucketDrop,
	 * com.ushahidi.swiftriver.core.model.Link)
	 */
	public boolean deleteLink(BucketDrop bucketDrop, Link link) {
		BucketDropLink bucketDropLink = findLink(bucketDrop, link);

		if (bucketDropLink != null && bucketDropLink.isDeleted()) {
			return false;
		} else if (bucketDropLink != null && !bucketDropLink.isDeleted()) {
			bucketDropLink.setDeleted(true);
			this.em.merge(bucketDropLink);
		} else {
			bucketDropLink = new BucketDropLink();
			bucketDropLink.setBucketDrop(bucketDrop);
			bucketDropLink.setLink(link);
			bucketDropLink.setDeleted(true);

			this.em.persist(bucketDropLink);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.BucketDropDao#findPlace(com.ushahidi
	 * .swiftriver.core.model.BucketDrop,
	 * com.ushahidi.swiftriver.core.model.Place)
	 */
	public BucketDropPlace findPlace(BucketDrop bucketDrop, Place place) {
		String sql = "FROM BucketDropPlace WHERE bucketDrop = :bucketDrop AND place = :place";

		TypedQuery<BucketDropPlace> query = em.createQuery(sql,
				BucketDropPlace.class);
		query.setParameter("bucketDrop", bucketDrop);
		query.setParameter("place", place);

		List<BucketDropPlace> dropPlaces = query.getResultList();
		return dropPlaces.isEmpty() ? null : dropPlaces.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.BucketDropDao#addPlace(com.ushahidi
	 * .swiftriver.core.model.BucketDrop,
	 * com.ushahidi.swiftriver.core.model.Place)
	 */
	public void addPlace(BucketDrop bucketDrop, Place place) {
		BucketDropPlace bucketDropPlace = new BucketDropPlace();

		bucketDropPlace.setBucketDrop(bucketDrop);
		bucketDropPlace.setPlace(place);
		bucketDropPlace.setDeleted(false);

		this.em.persist(bucketDropPlace);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.BucketDropDao#deletePlace(com.ushahidi
	 * .swiftriver.core.model.BucketDrop,
	 * com.ushahidi.swiftriver.core.model.Place)
	 */
	public boolean deletePlace(BucketDrop bucketDrop, Place place) {
		BucketDropPlace bucketDropPlace = findPlace(bucketDrop, place);

		if (bucketDropPlace != null && bucketDropPlace.isDeleted()) {
			return false;
		} else if (bucketDropPlace != null && !bucketDropPlace.isDeleted()) {
			bucketDropPlace.setDeleted(true);
			this.em.merge(bucketDropPlace);
		} else {
			bucketDropPlace = new BucketDropPlace();
			bucketDropPlace.setBucketDrop(bucketDrop);
			bucketDropPlace.setPlace(place);
			bucketDropPlace.setDeleted(true);

			this.em.persist(bucketDropPlace);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.BucketDropDao#increaseVeracity(com
	 * .ushahidi.swiftriver.core.model.BucketDrop)
	 */
	public void increaseVeracity(BucketDrop bucketDrop) {
		long veracity = bucketDrop.getVeracity();
		veracity += 1;

		bucketDrop.setVeracity(veracity);
		this.em.merge(bucketDrop);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.BucketDropDao#createFromRiverDrop
	 * (com.ushahidi.swiftriver.core.model.RiverDrop,
	 * com.ushahidi.swiftriver.core.model.Bucket)
	 */
	public void createFromRiverDrop(RiverDrop riverDrop, Bucket bucket) {
		BucketDrop bucketDrop = new BucketDrop();

		bucketDrop.setBucket(bucket);
		bucketDrop.setDrop(riverDrop.getDrop());

		// Links
		List<BucketDropLink> links = new ArrayList<BucketDropLink>();
		for (RiverDropLink dropLink : riverDrop.getLinks()) {
			BucketDropLink bucketDropLink = new BucketDropLink();
			bucketDropLink.setBucketDrop(bucketDrop);
			bucketDropLink.setLink(dropLink.getLink());
			links.add(bucketDropLink);
		}
		bucketDrop.setLinks(links);

		// Places
		List<BucketDropPlace> places = new ArrayList<BucketDropPlace>();
		for (RiverDropPlace riverDropPlace : riverDrop.getPlaces()) {
			BucketDropPlace bucketDropPlace = new BucketDropPlace();
			bucketDropPlace.setBucketDrop(bucketDrop);
			bucketDropPlace.setPlace(riverDropPlace.getPlace());
			places.add(bucketDropPlace);
		}
		bucketDrop.setPlaces(places);

		// Tags
		List<BucketDropTag> tags = new ArrayList<BucketDropTag>();
		for (RiverDropTag riverDropTag : riverDrop.getTags()) {
			BucketDropTag bucketDropTag = new BucketDropTag();
			bucketDropTag.setBucketDrop(bucketDrop);
			bucketDropTag.setTag(riverDropTag.getTag());
			tags.add(bucketDropTag);
		}
		bucketDrop.setTags(tags);

		// Comments
		List<BucketDropComment> comments = new ArrayList<BucketDropComment>();
		for (RiverDropComment comment : riverDrop.getComments()) {
			BucketDropComment dropComment = new BucketDropComment();
			dropComment.setBucketDrop(bucketDrop);
			dropComment.setAccount(comment.getAccount());
			dropComment.setCommentText(comment.getCommentText());
			dropComment.setDateAdded(comment.getDateAdded());

			comments.add(dropComment);
		}
		bucketDrop.setComments(comments);

		this.create(bucketDrop);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.BucketDropDao#createFromExisting
	 * (com.ushahidi.swiftriver.core.model.BucketDrop,
	 * com.ushahidi.swiftriver.core.model.Bucket)
	 */
	public void createFromExisting(BucketDrop sourceBucketDrop, Bucket bucket) {
		BucketDrop bucketDrop = new BucketDrop();

		bucketDrop.setBucket(bucket);
		bucketDrop.setDrop(sourceBucketDrop.getDrop());

		// Links
		List<BucketDropLink> links = new ArrayList<BucketDropLink>();
		for (BucketDropLink link : sourceBucketDrop.getLinks()) {
			BucketDropLink bucketDropLink = new BucketDropLink();
			bucketDropLink.setBucketDrop(bucketDrop);
			bucketDropLink.setLink(link.getLink());
			links.add(bucketDropLink);
		}
		bucketDrop.setLinks(links);

		// Places
		List<BucketDropPlace> places = new ArrayList<BucketDropPlace>();
		for (BucketDropPlace place : sourceBucketDrop.getPlaces()) {
			BucketDropPlace bucketDropPlace = new BucketDropPlace();
			bucketDropPlace.setBucketDrop(bucketDrop);
			bucketDropPlace.setPlace(place.getPlace());
			places.add(bucketDropPlace);
		}
		bucketDrop.setPlaces(places);

		// Tags
		List<BucketDropTag> tags = new ArrayList<BucketDropTag>();
		for (BucketDropTag tag : sourceBucketDrop.getTags()) {
			BucketDropTag bucketDropTag = new BucketDropTag();
			bucketDropTag.setBucketDrop(bucketDrop);
			bucketDropTag.setTag(tag.getTag());
			tags.add(bucketDropTag);
		}
		bucketDrop.setTags(tags);

		// Comments
		List<BucketDropComment> comments = new ArrayList<BucketDropComment>();
		for (BucketDropComment comment : sourceBucketDrop.getComments()) {
			BucketDropComment dropComment = new BucketDropComment();
			dropComment.setBucketDrop(bucketDrop);
			dropComment.setAccount(comment.getAccount());
			dropComment.setCommentText(comment.getCommentText());
			dropComment.setDateAdded(comment.getDateAdded());

			comments.add(dropComment);
		}
		bucketDrop.setComments(comments);

		this.create(bucketDrop);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.BucketDropDao#addComment(com.ushahidi
	 * .swiftriver.core.model.BucketDrop,
	 * com.ushahidi.swiftriver.core.model.Account, java.lang.String)
	 */
	public BucketDropComment addComment(BucketDrop bucketDrop, Account account,
			String commentText) {
		BucketDropComment dropComment = new BucketDropComment();

		dropComment.setBucketDrop(bucketDrop);
		dropComment.setAccount(account);
		dropComment.setCommentText(commentText);
		dropComment.setDateAdded(new Date());

		this.em.persist(dropComment);
		return dropComment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.BucketDropDao#deleteComment(com.
	 * ushahidi.swiftriver.core.model.BucketDrop, java.lang.Long)
	 */
	public boolean deleteComment(Long commentId) {
		String sql = "DELETE FROM BucketDropComment WHERE id = ?1";
		return em.createQuery(sql).setParameter(1, commentId).executeUpdate() == 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.BucketDropDao#findForm(com.ushahidi
	 * .swiftriver.core.model.BucketDrop, java.lang.Long)
	 */
	@Override
	public BucketDropForm findForm(Long dropId, Long formId) {
		String query = "SELECT bdf ";
		query += "FROM BucketDropForm bdf ";
		query += "JOIN bdf.drop d ";
		query += "JOIN bdf.form f ";
		query += "WHERE d.id = :dropId ";
		query += "AND f.id = :formId";

		BucketDropForm dropForm = null;
		try {
			dropForm = (BucketDropForm) em.createQuery(query)
					.setParameter("dropId", dropId)
					.setParameter("formId", formId).getSingleResult();
		} catch (NoResultException e) {
			// Do nothing
		}
		return dropForm;
	}

	/**
	 * Populate custom field for the given drops.
	 * 
	 * @param drops
	 */
	@SuppressWarnings("rawtypes")
	public void populateForms(List<Drop> drops) {

		Map<Long, Integer> dropIndex = new HashMap<Long, Integer>();
		int i = 0;
		for (Drop drop : drops) {
			dropIndex.put(drop.getId(), i);
			i++;
		}

		String sql = "SELECT form.id, form.drop_id, form_id, field.field_id, field.value ";
		sql += "FROM bucket_droplet_form form, bucket_droplet_form_field field ";
		sql += "WHERE form.id = field.droplet_form_id ";
		sql += "AND drop_id IN (:drop_ids)  ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("drop_ids", dropIndex.keySet());
		List<Map<String, Object>> results = this.namedJdbcTemplate
				.queryForList(sql, params);

		// Add form values to their respective drops.
		Map<Long, BucketDropForm> dropForms = new HashMap<Long, BucketDropForm>();
		for (Map<String, Object> result : results) {

			Long dropId = ((Number) result.get("drop_id")).longValue();
			Drop drop = drops.get(dropIndex.get(dropId));
			if (drop.getForms() == null) {
				drop.setForms(new ArrayList<DropForm>());
			}

			Long dropFormId = ((Number) result.get("id")).longValue();
			BucketDropForm dropForm = dropForms.get(dropFormId);

			if (dropForm == null) {
				dropForm = new BucketDropForm();
				dropForm.setId(dropFormId);
				Form form = new Form();
				form.setId(((Number) result.get("form_id")).longValue());
				dropForm.setForm(form);
				dropForm.setValues(new ArrayList<BucketDropFormField>());

				dropForms.put(dropFormId, dropForm);
			}

			BucketDropFormField value = new BucketDropFormField();
			FormField field = new FormField();
			field.setId(((Number) result.get("field_id")).longValue());
			value.setField(field);
			value.setValue((String) result.get("value"));

			List<BucketDropFormField> values = dropForm.getValues();
			values.add(value);

			if (!drop.getForms().contains(dropForm)) {
				drop.getForms().add(dropForm);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDropDao#isRead(com.ushahidi.swiftriver.core.model.BucketDrop, com.ushahidi.swiftriver.core.model.Account)
	 */
	public boolean isRead(BucketDrop bucketDrop, Account account) {
		String sql = "SELECT * FROM `bucket_droplets_read` " +
				"WHERE `buckets_droplets_id` = :bucketDropId " +
				"AND account_id = :accountId";
		
		Query query = em.createNativeQuery(sql);
		query.setParameter("bucketDropId", bucketDrop.getId());
		query.setParameter("accountId", account.getId());

		return query.getResultList().size() == 1;
	}

}
