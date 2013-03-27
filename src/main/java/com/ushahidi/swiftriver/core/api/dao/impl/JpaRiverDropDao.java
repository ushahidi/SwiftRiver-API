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

import java.util.Date;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.RiverDropDao;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.RiverDrop;
import com.ushahidi.swiftriver.core.model.RiverDropComment;
import com.ushahidi.swiftriver.core.model.RiverDropLink;
import com.ushahidi.swiftriver.core.model.RiverDropPlace;
import com.ushahidi.swiftriver.core.model.RiverDropTag;
import com.ushahidi.swiftriver.core.model.Tag;

@Repository
public class JpaRiverDropDao extends AbstractJpaDao<RiverDrop> implements RiverDropDao {

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.RiverDropDao#findTag(com.ushahidi.swiftriver.core.model.RiverDrop, com.ushahidi.swiftriver.core.model.Tag)
	 */
	public RiverDropTag findTag(RiverDrop riverDrop, Tag tag) {
		String sql = "FROM RiverDropTag WHERE riverDrop = :riverDrop AND tag = :tag";
	
		TypedQuery<RiverDropTag> query = em.createQuery(sql, RiverDropTag.class);
		query.setParameter("riverDrop", riverDrop);
		query.setParameter("tag", tag);
	
		List<RiverDropTag> dropTags = query.getResultList();
		return dropTags.isEmpty() ? null : dropTags.get(0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.RiverDropDao#addTag(com.ushahidi.swiftriver.core.model.RiverDrop, com.ushahidi.swiftriver.core.model.Tag)
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
	 * @see com.ushahidi.swiftriver.core.api.dao.RiverDropDao#deleteTag(com.ushahidi.swiftriver.core.model.RiverDrop, com.ushahidi.swiftriver.core.model.Tag)
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
	 * @see com.ushahidi.swiftriver.core.api.dao.RiverDropDao#findPlace(com.ushahidi.swiftriver.core.model.RiverDrop, com.ushahidi.swiftriver.core.model.Place)
	 */
	public RiverDropPlace findPlace(RiverDrop riverDrop, Place place) {
		String sql = "FROM RiverDropPlace WHERE riverDrop = :riverDrop AND place = :place";
	
		TypedQuery<RiverDropPlace> query = em.createQuery(sql, RiverDropPlace.class);
		query.setParameter("riverDrop", riverDrop);
		query.setParameter("place", place);
	
		List<RiverDropPlace> dropPlaces = query.getResultList();
		return dropPlaces.isEmpty() ? null : dropPlaces.get(0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.RiverDropDao#addPlace(com.ushahidi.swiftriver.core.model.RiverDrop, com.ushahidi.swiftriver.core.model.Place)
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
	 * @see com.ushahidi.swiftriver.core.api.dao.RiverDropDao#deletePlace(com.ushahidi.swiftriver.core.model.RiverDrop, com.ushahidi.swiftriver.core.model.Place)
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
	 * @see com.ushahidi.swiftriver.core.api.dao.RiverDropDao#findLink(com.ushahidi.swiftriver.core.model.RiverDrop, com.ushahidi.swiftriver.core.model.Link)
	 */
	public RiverDropLink findLink(RiverDrop riverDrop, Link link) {
		String sql = "FROM RiverDropLink WHERE riverDrop = :riverDrop AND link = :link";
	
		TypedQuery<RiverDropLink> query = em.createQuery(sql, RiverDropLink.class);
		query.setParameter("riverDrop", riverDrop);
		query.setParameter("link", link);
	
		List<RiverDropLink> dropLinks = query.getResultList();
		return dropLinks.isEmpty() ? null : dropLinks.get(0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.RiverDropDao#addLink(com.ushahidi.swiftriver.core.model.RiverDrop, com.ushahidi.swiftriver.core.model.Link)
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
	 * @see com.ushahidi.swiftriver.core.api.dao.RiverDropDao#deleteLink(com.ushahidi.swiftriver.core.model.RiverDrop, com.ushahidi.swiftriver.core.model.Link)
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
	 * @see com.ushahidi.swiftriver.core.api.dao.RiverDropDao#addComment(com.ushahidi.swiftriver.core.model.RiverDrop, com.ushahidi.swiftriver.core.model.Account, java.lang.String)
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
	 * @see com.ushahidi.swiftriver.core.api.dao.RiverDropDao#deleteComment(java.lang.Long)
	 */
	public boolean deleteComment(Long commentId) {
		String sql = "DELETE FROM RiverDropComment WHERE id = ?1";
		return em.createQuery(sql).setParameter(1, commentId).executeUpdate() == 1;		
	}

	/*
	 * (non-Javadoc)
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
