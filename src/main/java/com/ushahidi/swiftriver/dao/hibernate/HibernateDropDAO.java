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
package com.ushahidi.swiftriver.dao.hibernate;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.dao.DropDAO;
import com.ushahidi.swiftriver.model.Drop;
import com.ushahidi.swiftriver.model.Link;
import com.ushahidi.swiftriver.model.Media;
import com.ushahidi.swiftriver.model.Place;
import com.ushahidi.swiftriver.model.Tag;

/**
 * Hibernate class for drops
 * @author ekala
 *
 */
@Repository("dropDAO")
@Transactional
public class HibernateDropDAO extends AbstractHibernateDAO<Drop, Long> implements DropDAO {


	public HibernateDropDAO() {
		super(Drop.class);
	}

	/**
	 * @see DropDAO#createDrops(Collection)
	 */
	public void createDrops(Collection<Drop> drops) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see DropDAO#addLink(long, Link)
	 */
	public void addLink(long dropId, Link link) {
		getById(dropId).getLinks().add(link);
	}

	private Drop getById(long dropId) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see DropDAO#getLinks(long)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Link> getLinks(long dropId) {
		String query = "Select d.links From Drop d where d.id = ?";
		return (List<Link>) hibernateTemplate.find(query, new Long(dropId));
	}

	/**
	 * @see DropDAO#addLinks(long, Collection)
	 */
	public void addLinks(long dropId, Collection<Link> links) {
		getById(dropId).getLinks().addAll(links);
	}

	public void removeLink(long dropId, Link link) {
		getById(dropId).getLinks().remove(link);
	}

	/**
	 * @see DropDAO#addPlace(Long, Place)
	 */
	public void addPlace(Long dropId, Place place) {
		getById(dropId).getPlaces().add(place);
	}

	/**
	 * @see DropDAO#addPlaces(long, Collection)
	 */
	public void addPlaces(long dropId, Collection<Place> places) {
		getById(dropId).getPlaces().addAll(places);
	}

	/**
	 * @see DropDAO#removePlace(Long, Place)
	 */
	public void removePlace(Long dropId, Place place) {
		getById(dropId).getPlaces().remove(place);
	}

	/**
	 * @see DropDAO#getPlaces(long)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Place> getPlaces(long dropId) {
		String query = "Select d.places From Drop d where d.id = ?";
		return (List<Place>) hibernateTemplate.find(query, new Long(dropId));
	}

	/**
	 * @see DropDAO#addMedia(long, Media)
	 */
	public void addMedia(long dropId, Media media) {
		getById(dropId).getMedia().add(media);
	}

	/**
	 * @see DropDAO#addMultipleMedia(long, Collection)
	 */
	public void addMultipleMedia(long dropId, Collection<Media> media) {
		getById(dropId).getMedia().addAll(media);
	}

	/**
	 * @see DropDAO#removeMedia(long, Media)
	 */
	public void removeMedia(long dropId, Media media) {
		getById(dropId).getMedia().remove(media);
	}

	/**
	 * @see DropDAO#getMedia(long)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Media> getMedia(long dropId) {
		String query = "Select d.media From Drop d where d.id = ?";
		return (List<Media>) hibernateTemplate.find(query, new Long(dropId));
	}

	/**
	 * @see DropDAO#addTag(Long, Tag)
	 */
	public void addTag(Long dropId, Tag tag) {
		getById(dropId).getTags().add(tag);
	}

	/**
	 * @see DropDAO#addTags(long, Collection)
	 */
	public void addTags(long dropId, Collection<Tag> tags) {
		getById(dropId).getTags().addAll(tags);
	}

	/**
	 * @see DropDAO#removeTag(Long, Tag)
	 */
	public void removeTag(Long dropId, Tag tag) {
		getById(dropId).getTags().remove(tag);
	}

	/**
	 * @see DropDAO#getTags(long)
	 */
	@SuppressWarnings("unchecked")
	public Collection<Tag> getTags(long dropId) {
		String query = "Select d.tags From Drop d where d.id = ?";
		return (List<Tag>) hibernateTemplate.find(query, new Long(dropId));
	}

}
