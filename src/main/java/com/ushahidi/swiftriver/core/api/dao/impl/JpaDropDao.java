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
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Media;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.Tag;

/**
 * Repository class for drops
 * @author ekala
 *
 */
@Repository
public class JpaDropDao extends AbstractJpaDao<Drop, Long> implements DropDao {


	public JpaDropDao() {
		super(Drop.class);
	}

	/**
	 * @see DropDao#addLink(long, Link)
	 */
	public void addLink(long dropId, Link link) {
		findById(dropId).getLinks().add(link);
	}

	/**
	 * @see DropDao#addLinks(long, Collection)
	 */
	public void addLinks(long dropId, Collection<Link> links) {
		Drop drop = this.findById(dropId);
		drop.getLinks().addAll((Set<Link>) links);
		this.save(drop);
	}

	public void removeLink(long dropId, Link link) {
		findById(dropId).getLinks().remove(link);
	}

	/**
	 * @see DropDao#addPlace(Long, Place)
	 */
	public void addPlace(Long dropId, Place place) {
		findById(dropId).getPlaces().add(place);
	}

	/**
	 * @see DropDao#addPlaces(long, Collection)
	 */
	public void addPlaces(long dropId, Collection<Place> places) {
		Drop drop = this.findById(dropId);
		drop.getPlaces().addAll((Set<Place>) places);
		this.save(drop);
	}

	/**
	 * @see DropDao#removePlace(Long, Place)
	 */
	public void removePlace(Long dropId, Place place) {
		findById(dropId).getPlaces().remove(place);
	}

	/**
	 * @see DropDao#addMedia(long, Media)
	 */
	public void addMedia(long dropId, Media media) {
		findById(dropId).getMedia().add(media);
	}

	/**
	 * @see DropDao#addMultipleMedia(long, Collection)
	 */
	public void addMultipleMedia(long dropId, Collection<Media> media) {
		Drop drop = this.findById(dropId);
		drop.getMedia().addAll((Set<Media>) media);
		this.save(drop);
	}

	/**
	 * @see DropDao#removeMedia(long, Media)
	 */
	public void removeMedia(long dropId, Media media) {
		findById(dropId).getMedia().remove(media);
	}

	/**
	 * @see DropDao#addTag(Long, Tag)
	 */
	public void addTag(Long dropId, Tag tag) {
		findById(dropId).getTags().add(tag);
	}

	/**
	 * @see DropDao#addTags(long, Collection)
	 */
	public void addTags(long dropId, Collection<Tag> tags) {
		Drop drop = this.findById(dropId);
		drop.getTags().addAll((Set<Tag>) tags);
		this.save(drop);
	}

	/**
	 * @see DropDao#removeTag(Long, Tag)
	 */
	public void removeTag(Long dropId, Tag tag) {
		findById(dropId).getTags().remove(tag);
	}

	/**
	 * @see DropDao#findDropsByHash(ArrayList)
	 */
	@SuppressWarnings("unchecked")
	public List<Drop> findDropsByHash(ArrayList<String> dropHashes) {
		String sql = "FROM Drop d WHERE d.dropletHash in (?1)";

		Query query = entityManager.createQuery(sql);
		query.setParameter(1, dropHashes);

		return (List<Drop>) query.getResultList();
	}

}
