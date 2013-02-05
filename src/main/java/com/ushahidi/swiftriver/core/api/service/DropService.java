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
package com.ushahidi.swiftriver.core.api.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.api.dao.JpaDao;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Media;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.Tag;

/**
 * Service class for drops
 * @author ekala
 *
 */
@Service
public class DropService extends AbstractServiceImpl<Drop, Long> {
	
	@Autowired
	private DropDao dropDAO;

	public void setDropDAO(DropDao dropDAO) {
		this.dropDAO = dropDAO;
	}

	public Drop getDrop(Long id) {
		return dropDAO.findById(id);
	}

	public void createDrops(Collection<Drop> drops) {
		dropDAO.createDrops(drops);
	}

	public void addLink(long dropId, Link link) {
		dropDAO.addLink(dropId, link);
	}

	public void addLinks(long dropId, Collection<Link> links) {
		dropDAO.addLinks(dropId, links);
	}

	public void removeLink(long dropId, Link link) {
		dropDAO.removeLink(dropId, link);
	}

	public void addPlace(Long dropId, Place place) {
		dropDAO.addPlace(dropId, place);
	}

	public void addPlaces(long dropId, Collection<Place> places) {
		dropDAO.addPlaces(dropId, places);
	}

	public void removePlace(Long dropId, Place place) {
		dropDAO.removePlace(dropId, place);
	}

	public void addMedia(long dropId, Media media) {
		dropDAO.addMedia(dropId, media);
	}

	public void addMultipleMedia(long dropId, Collection<Media> media) {
		dropDAO.addMultipleMedia(dropId, media);
	}

	public void removeMedia(long dropId, Media media) {
		dropDAO.removeMedia(dropId, media);
	}

	public void addTag(Long dropId, Tag tag) {
		dropDAO.addTag(dropId, tag);
	}

	public void addTags(long dropId, Collection<Tag> tags) {
		dropDAO.addTags(dropId, tags);
	}

	public void removeTag(Long dropId, Tag tag) {
		dropDAO.removeTag(dropId, tag);
	}

	@Override
	public JpaDao<Drop, Long> getServiceDao() {
		// TODO Auto-generated method stub
		return null;
	}

}
