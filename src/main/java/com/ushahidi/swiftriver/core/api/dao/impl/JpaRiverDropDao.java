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

import com.ushahidi.swiftriver.core.api.dao.RiverDropDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.DropForm;
import com.ushahidi.swiftriver.core.model.Form;
import com.ushahidi.swiftriver.core.model.FormField;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.RiverDrop;
import com.ushahidi.swiftriver.core.model.RiverDropComment;
import com.ushahidi.swiftriver.core.model.RiverDropForm;
import com.ushahidi.swiftriver.core.model.RiverDropFormField;
import com.ushahidi.swiftriver.core.model.RiverDropLink;
import com.ushahidi.swiftriver.core.model.RiverDropPlace;
import com.ushahidi.swiftriver.core.model.RiverDropTag;
import com.ushahidi.swiftriver.core.model.Tag;

@Repository
public class JpaRiverDropDao extends AbstractJpaContextDropDao<RiverDrop>
		implements RiverDropDao {

	public JpaRiverDropDao() {

		// Query for retrieving tag metadata
		tagsQuery = "SELECT rivers_droplets.droplet_id, tag_id AS id, tag, tag_canonical, tag_type ";
		tagsQuery += "FROM droplets_tags  ";
		tagsQuery += "INNER JOIN tags ON (tags.id = tag_id)  ";
		tagsQuery += "INNER JOIN rivers_droplets ON (rivers_droplets.droplet_id = droplets_tags.droplet_id) ";
		tagsQuery += "WHERE rivers_droplets.droplet_id IN :drop_ids  ";
		tagsQuery += "AND tags.id NOT IN ( ";
		tagsQuery += "	SELECT tag_id FROM river_droplet_tags ";
		tagsQuery += "  INNER JOIN rivers_droplets ON (rivers_droplets.id = river_droplet_tags.rivers_droplets_id) ";
		tagsQuery += "	WHERE rivers_droplets.droplet_id IN :drop_ids ";
		tagsQuery += "	AND deleted = 1) ";
		tagsQuery += "UNION ALL  ";
		tagsQuery += "SELECT rivers_droplets.droplet_id, tag_id AS id, tag, tag_canonical, tag_type  ";
		tagsQuery += "FROM river_droplet_tags ";
		tagsQuery += "INNER JOIN tags ON (tags.id = tag_id) ";
		tagsQuery += "INNER JOIN rivers_droplets ON (rivers_droplets.id = river_droplet_tags.rivers_droplets_id) ";
		tagsQuery += "WHERE rivers_droplets.droplet_id IN :drop_ids ";
		tagsQuery += "AND deleted = 0 ";

		// Query for retrieving link metadata
		linksQuery = "SELECT rivers_droplets.droplet_id, link_id AS id, url ";
		linksQuery += "FROM droplets_links  ";
		linksQuery += "INNER JOIN links ON (links.id = link_id)  ";
		linksQuery += "INNER JOIN rivers_droplets ON (rivers_droplets.droplet_id = droplets_links.droplet_id)";
		linksQuery += "WHERE rivers_droplets.droplet_id IN :drop_ids  ";
		linksQuery += "AND links.id NOT IN ( ";
		linksQuery += "	SELECT link_id FROM river_droplet_links  ";
		linksQuery += " INNER JOIN rivers_droplets ON (rivers_droplets.id = river_droplet_links.rivers_droplets_id) ";
		linksQuery += "	WHERE rivers_droplets.droplet_id IN :drop_ids  ";
		linksQuery += "	AND deleted = 1) ";
		linksQuery += "UNION ALL  ";
		linksQuery += "SELECT rivers_droplets.droplet_id, link_id AS id, url  ";
		linksQuery += "FROM river_droplet_links  ";
		linksQuery += "INNER JOIN links ON (links.id = link_id)  ";
		linksQuery += "INNER JOIN rivers_droplets ON (rivers_droplets.id = river_droplet_links.rivers_droplets_id) ";
		linksQuery += "WHERE rivers_droplets.droplet_id IN :drop_ids  ";
		linksQuery += "AND deleted = 0 ";

		// Query for retrieving the drop image
		dropImageQuery = "SELECT droplets.id, droplet_image FROM droplets ";
		dropImageQuery += "INNER JOIN rivers_droplets ON (rivers_droplets.droplet_id = droplets.id) ";
		dropImageQuery += "WHERE rivers_droplets.droplet_id IN :drop_ids ";
		dropImageQuery += "AND droplets.droplet_image > 0";

		// Query for retrieving media metadata
		mediaQuery = "SELECT rivers_droplets.droplet_id, media.id AS id, media.url AS url, type, media_thumbnails.size AS thumbnail_size, ";
		mediaQuery += "media_thumbnails.url AS thumbnail_url ";
		mediaQuery += "FROM droplets_media ";
		mediaQuery += "INNER JOIN media ON (media.id = droplets_media.media_id) ";
		mediaQuery += "INNER JOIN rivers_droplets ON (rivers_droplets.droplet_id = droplets_media.droplet_id) ";
		mediaQuery += "LEFT JOIN media_thumbnails ON (media_thumbnails.media_id = media.id) ";
		mediaQuery += "WHERE rivers_droplets.droplet_id IN :drop_ids ";
		mediaQuery += "AND media.id NOT IN ( ";
		mediaQuery += "	SELECT media_id ";
		mediaQuery += "	FROM river_droplet_media ";
		mediaQuery += " INNER JOIN rivers_droplets ON (rivers_droplets.id = river_droplet_media.rivers_droplets_id) ";
		mediaQuery += "	WHERE rivers_droplets.droplet_id IN :drop_ids ";
		mediaQuery += "	AND deleted = 1) ";
		mediaQuery += "UNION ALL ";
		mediaQuery += "SELECT rivers_droplets.droplet_id, media.id AS id, media.url AS url, type, media_thumbnails.size AS thumbnail_size, media_thumbnails.url AS thumbnail_url ";
		mediaQuery += "FROM river_droplet_media ";
		mediaQuery += "INNER JOIN media ON (media.id = river_droplet_media.media_id) ";
		mediaQuery += "INNER JOIN rivers_droplets ON (rivers_droplets.id = river_droplet_media.rivers_droplets_id) ";
		mediaQuery += "LEFT JOIN media_thumbnails ON (media_thumbnails.media_id = media.id) ";
		mediaQuery += "WHERE rivers_droplets.droplet_id IN :drop_ids ";
		mediaQuery += "AND deleted = 0; ";

		// Query for retrieving place metadata
		placesQuery = "SELECT rivers_droplets.droplet_id, place_id AS id, place_name, place_name_canonical, ";
		placesQuery += "places.hash AS place_hash, latitude, longitude ";
		placesQuery += "FROM droplets_places ";
		placesQuery += "INNER JOIN places ON (places.id = place_id) ";
		placesQuery += "INNER JOIN rivers_droplets ON (rivers_droplets.droplet_id = droplets_places.droplet_id) ";
		placesQuery += "WHERE rivers_droplets.droplet_id IN :drop_ids ";
		placesQuery += "AND places.id NOT IN ( ";
		placesQuery += "	SELECT place_id ";
		placesQuery += "	FROM river_droplet_places ";
		placesQuery += "    INNER JOIN rivers_droplets ON (rivers_droplets.id = river_droplet_places.rivers_droplets_id) ";
		placesQuery += "	WHERE rivers_droplets.droplet_id IN :drop_ids ";
		placesQuery += "	AND deleted = 1) ";
		placesQuery += "UNION ALL ";
		placesQuery += "SELECT rivers_droplets_id AS droplet_id, place_id AS id, place_name, place_name_canonical, places.hash AS place_hash, latitude, longitude ";
		placesQuery += "FROM river_droplet_places ";
		placesQuery += "INNER JOIN places ON (places.id = place_id) ";
		placesQuery += "INNER JOIN rivers_droplets ON (rivers_droplets.id = river_droplet_places.rivers_droplets_id) ";
		placesQuery += "WHERE rivers_droplets.droplet_id IN :drop_ids ";
		placesQuery += "AND deleted = 0 ";

		// Query for retrieving the RiverDrop id
		contextDropQuery = "SELECT id, droplet_id FROM rivers_droplets WHERE droplet_id IN :dropIds";
	}
	
	@Override
	public void delete(RiverDrop t) {
		String sql = String.format(
				"DELETE FROM `river_droplets_read` WHERE `rivers_droplets_id` = %d",
				t.getId());
		em.createNativeQuery(sql).executeUpdate();
		super.delete(t);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.RiverDropDao#findTag(com.ushahidi
	 * .swiftriver.core.model.RiverDrop, com.ushahidi.swiftriver.core.model.Tag)
	 */
	public RiverDropTag findTag(RiverDrop riverDrop, Tag tag) {
		String sql = "FROM RiverDropTag WHERE riverDrop = :riverDrop AND tag = :tag";

		TypedQuery<RiverDropTag> query = em
				.createQuery(sql, RiverDropTag.class);
		query.setParameter("riverDrop", riverDrop);
		query.setParameter("tag", tag);

		List<RiverDropTag> dropTags = query.getResultList();
		return dropTags.isEmpty() ? null : dropTags.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.RiverDropDao#addTag(com.ushahidi
	 * .swiftriver.core.model.RiverDrop, com.ushahidi.swiftriver.core.model.Tag)
	 */
	public void addTag(RiverDrop riverDrop, Tag tag) {
		RiverDropTag riverDropTag = new RiverDropTag();

		riverDropTag.setRiverDrop(riverDrop);
		riverDropTag.setTag(tag);
		riverDropTag.setDeleted(false);

		this.em.persist(riverDropTag);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.RiverDropDao#deleteTag(com.ushahidi
	 * .swiftriver.core.model.RiverDrop, com.ushahidi.swiftriver.core.model.Tag)
	 */
	public boolean deleteTag(RiverDrop riverDrop, Tag tag) {
		RiverDropTag riverDropTag = findTag(riverDrop, tag);

		if (riverDropTag != null && riverDropTag.isDeleted()) {
			return false;
		} else if (riverDropTag != null && !riverDropTag.isDeleted()) {
			riverDropTag.setDeleted(true);
			this.em.merge(riverDropTag);
		} else {
			riverDropTag = new RiverDropTag();
			riverDropTag.setRiverDrop(riverDrop);
			riverDropTag.setTag(tag);
			riverDropTag.setDeleted(true);

			this.em.persist(riverDropTag);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.RiverDropDao#findPlace(com.ushahidi
	 * .swiftriver.core.model.RiverDrop,
	 * com.ushahidi.swiftriver.core.model.Place)
	 */
	public RiverDropPlace findPlace(RiverDrop riverDrop, Place place) {
		String sql = "FROM RiverDropPlace WHERE riverDrop = :riverDrop AND place = :place";

		TypedQuery<RiverDropPlace> query = em.createQuery(sql,
				RiverDropPlace.class);
		query.setParameter("riverDrop", riverDrop);
		query.setParameter("place", place);

		List<RiverDropPlace> dropPlaces = query.getResultList();
		return dropPlaces.isEmpty() ? null : dropPlaces.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.RiverDropDao#addPlace(com.ushahidi
	 * .swiftriver.core.model.RiverDrop,
	 * com.ushahidi.swiftriver.core.model.Place)
	 */
	public void addPlace(RiverDrop riverDrop, Place place) {
		RiverDropPlace riverDropPlace = new RiverDropPlace();

		riverDropPlace.setRiverDrop(riverDrop);
		riverDropPlace.setPlace(place);
		riverDropPlace.setDeleted(false);

		this.em.persist(riverDropPlace);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.RiverDropDao#deletePlace(com.ushahidi
	 * .swiftriver.core.model.RiverDrop,
	 * com.ushahidi.swiftriver.core.model.Place)
	 */
	public boolean deletePlace(RiverDrop riverDrop, Place place) {
		RiverDropPlace riverDropPlace = findPlace(riverDrop, place);

		if (riverDropPlace != null && riverDropPlace.isDeleted()) {
			return false;
		} else if (riverDropPlace != null && !riverDropPlace.isDeleted()) {
			riverDropPlace.setDeleted(true);
			this.em.merge(riverDropPlace);
		} else {
			riverDropPlace = new RiverDropPlace();
			riverDropPlace.setRiverDrop(riverDrop);
			riverDropPlace.setPlace(place);
			riverDropPlace.setDeleted(true);

			this.em.persist(riverDropPlace);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.RiverDropDao#findLink(com.ushahidi
	 * .swiftriver.core.model.RiverDrop,
	 * com.ushahidi.swiftriver.core.model.Link)
	 */
	public RiverDropLink findLink(RiverDrop riverDrop, Link link) {
		String sql = "FROM RiverDropLink WHERE riverDrop = :riverDrop AND link = :link";

		TypedQuery<RiverDropLink> query = em.createQuery(sql,
				RiverDropLink.class);
		query.setParameter("riverDrop", riverDrop);
		query.setParameter("link", link);

		List<RiverDropLink> dropLinks = query.getResultList();
		return dropLinks.isEmpty() ? null : dropLinks.get(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.RiverDropDao#addLink(com.ushahidi
	 * .swiftriver.core.model.RiverDrop,
	 * com.ushahidi.swiftriver.core.model.Link)
	 */
	public void addLink(RiverDrop riverDrop, Link link) {
		RiverDropLink riverDropLink = new RiverDropLink();

		riverDropLink.setRiverDrop(riverDrop);
		riverDropLink.setLink(link);
		riverDropLink.setDeleted(false);

		this.em.persist(riverDropLink);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.RiverDropDao#deleteLink(com.ushahidi
	 * .swiftriver.core.model.RiverDrop,
	 * com.ushahidi.swiftriver.core.model.Link)
	 */
	public boolean deleteLink(RiverDrop riverDrop, Link link) {
		RiverDropLink riverDropLink = findLink(riverDrop, link);

		if (riverDropLink != null && riverDropLink.isDeleted()) {
			return false;
		} else if (riverDropLink != null && !riverDropLink.isDeleted()) {
			riverDropLink.setDeleted(true);
			this.em.merge(riverDropLink);
		} else {
			riverDropLink = new RiverDropLink();
			riverDropLink.setRiverDrop(riverDrop);
			riverDropLink.setLink(link);
			riverDropLink.setDeleted(true);

			this.em.persist(riverDropLink);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.RiverDropDao#addComment(com.ushahidi
	 * .swiftriver.core.model.RiverDrop,
	 * com.ushahidi.swiftriver.core.model.Account, java.lang.String)
	 */
	public RiverDropComment addComment(RiverDrop riverDrop, Account account,
			String commentText) {
		RiverDropComment dropComment = new RiverDropComment();

		dropComment.setRiverDrop(riverDrop);
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
	 * com.ushahidi.swiftriver.core.api.dao.RiverDropDao#deleteComment(java.
	 * lang.Long)
	 */
	public boolean deleteComment(Long commentId) {
		String sql = "DELETE FROM RiverDropComment WHERE id = ?1";
		return em.createQuery(sql).setParameter(1, commentId).executeUpdate() == 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.api.dao.RiverDropDao#findForm(com.ushahidi
	 * .swiftriver.core.model.RiverDrop, java.lang.Long)
	 */
	@Override
	public RiverDropForm findForm(Long dropId, Long formId) {
		String query = "SELECT df ";
		query += "FROM RiverDropForm df ";
		query += "JOIN df.drop d ";
		query += "JOIN df.form f ";
		query += "WHERE d.id = :dropId ";
		query += "AND f.id = :formId";

		RiverDropForm dropForm = null;
		try {
			dropForm = (RiverDropForm) em.createQuery(query)
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
		sql += "FROM river_droplet_form form, river_droplet_form_field field ";
		sql += "WHERE form.id = field.droplet_form_id ";
		sql += "AND drop_id IN (:drop_ids)  ";

		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("drop_ids", dropIndex.keySet());
		List<Map<String, Object>> results = this.namedJdbcTemplate
				.queryForList(sql, params);

		// Add form values to their respective drops.
		Map<Long, RiverDropForm> dropForms = new HashMap<Long, RiverDropForm>();
		for (Map<String, Object> result : results) {

			Long dropId = ((Number) result.get("drop_id")).longValue();
			Drop drop = drops.get(dropIndex.get(dropId));
			if (drop.getForms() == null) {
				drop.setForms(new ArrayList<DropForm>());
			}

			Long dropFormId = ((Number) result.get("id")).longValue();
			RiverDropForm dropForm = dropForms.get(dropFormId);

			if (dropForm == null) {
				dropForm = new RiverDropForm();
				dropForm.setId(dropFormId);
				Form form = new Form();
				form.setId(((Number) result.get("form_id")).longValue());
				dropForm.setForm(form);
				dropForm.setValues(new ArrayList<RiverDropFormField>());

				dropForms.put(dropFormId, dropForm);
			}

			RiverDropFormField value = new RiverDropFormField();
			FormField field = new FormField();
			field.setId(((Number) result.get("field_id")).longValue());
			value.setField(field);
			value.setValue((String) result.get("value"));

			List<RiverDropFormField> values = dropForm.getValues();
			values.add(value);

			if (!drop.getForms().contains(dropForm)) {
				drop.getForms().add(dropForm);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.RiverDropDao#isRead(com.ushahidi.swiftriver.core.model.RiverDrop, com.ushahidi.swiftriver.core.model.Account)
	 */
	public boolean isRead(RiverDrop riverDrop, Account account) {
		String sql = "SELECT * FROM `river_droplets_read` " +
				"WHERE `rivers_droplets_id` = :riverDropId " +
				"AND account_id = :accountId";
		
		Query query = em.createNativeQuery(sql);
		query.setParameter("riverDropId", riverDrop.getId());
		query.setParameter("accountId", account.getId());

		return query.getResultList().size() == 1;
	}

}
