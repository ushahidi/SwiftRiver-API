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

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.BucketDropDao;
import com.ushahidi.swiftriver.core.model.BucketDrop;
import com.ushahidi.swiftriver.core.model.BucketDropLink;
import com.ushahidi.swiftriver.core.model.BucketDropPlace;
import com.ushahidi.swiftriver.core.model.BucketDropTag;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.Tag;

@Repository
public class JpaBucketDropDao extends AbstractJpaDao<BucketDrop> implements
		BucketDropDao {

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDropDao#addTag(com.ushahidi.swiftriver.core.model.BucketDrop, com.ushahidi.swiftriver.core.model.Tag)
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
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDropDao#findTag(com.ushahidi.swiftriver.core.model.BucketDrop, com.ushahidi.swiftriver.core.model.Tag)
	 */
	public BucketDropTag findTag(BucketDrop bucketDrop, Tag tag) {
		String sql = "FROM BucketDropTag WHERE bucketDrop = :bucketDrop AND tag = :tag";

		TypedQuery<BucketDropTag> query = em.createQuery(sql, BucketDropTag.class);
		query.setParameter("bucketDrop", bucketDrop);
		query.setParameter("tag", tag);

		List<BucketDropTag> dropTags = query.getResultList();
		return dropTags.isEmpty() ? null : dropTags.get(0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDropDao#deleteTag(com.ushahidi.swiftriver.core.model.BucketDrop, com.ushahidi.swiftriver.core.model.Tag)
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
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDropDao#addLink(com.ushahidi.swiftriver.core.model.BucketDrop, com.ushahidi.swiftriver.core.model.Link)
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
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDropDao#findLink(com.ushahidi.swiftriver.core.model.BucketDrop, com.ushahidi.swiftriver.core.model.Link)
	 */
	public BucketDropLink findLink(BucketDrop bucketDrop, Link link) {
		String sql = "FROM BucketDropLink WHERE bucketDrop = :bucketDrop AND link = :link";

		TypedQuery<BucketDropLink> query = em.createQuery(sql, BucketDropLink.class);
		query.setParameter("bucketDrop", bucketDrop);
		query.setParameter("link", link);

		List<BucketDropLink> dropLinks = query.getResultList();
		return dropLinks.isEmpty() ? null : dropLinks.get(0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDropDao#deleteLink(com.ushahidi.swiftriver.core.model.BucketDrop, com.ushahidi.swiftriver.core.model.Link)
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
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDropDao#findPlace(com.ushahidi.swiftriver.core.model.BucketDrop, com.ushahidi.swiftriver.core.model.Place)
	 */
	public BucketDropPlace findPlace(BucketDrop bucketDrop, Place place) {
		String sql = "FROM BucketDropPlace WHERE bucketDrop = :bucketDrop AND place = :place";

		TypedQuery<BucketDropPlace> query = em.createQuery(sql, BucketDropPlace.class);
		query.setParameter("bucketDrop", bucketDrop);
		query.setParameter("place", place);

		List<BucketDropPlace> dropPlaces = query.getResultList();
		return dropPlaces.isEmpty() ? null : dropPlaces.get(0);
	}

	/*
	 * (non-Javadoc)
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDropDao#addPlace(com.ushahidi.swiftriver.core.model.BucketDrop, com.ushahidi.swiftriver.core.model.Place)
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
	 * @see com.ushahidi.swiftriver.core.api.dao.BucketDropDao#deletePlace(com.ushahidi.swiftriver.core.model.BucketDrop, com.ushahidi.swiftriver.core.model.Place)
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

}
