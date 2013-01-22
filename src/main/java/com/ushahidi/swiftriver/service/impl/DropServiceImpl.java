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
package com.ushahidi.swiftriver.service.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ushahidi.swiftriver.dao.DropDAO;
import com.ushahidi.swiftriver.dao.SwiftRiverDAO;
import com.ushahidi.swiftriver.model.Drop;
import com.ushahidi.swiftriver.model.Link;
import com.ushahidi.swiftriver.model.Media;
import com.ushahidi.swiftriver.model.Place;
import com.ushahidi.swiftriver.model.Tag;
import com.ushahidi.swiftriver.service.DropService;

/**
 * Service class for drops
 * @author ekala
 *
 */
@Service
public class DropServiceImpl extends AbstractServiceImpl<Drop, Long> implements DropService {
	
	@Autowired
	private DropDAO dropDAO;

	public void setDropDAO(DropDAO dropDAO) {
		this.dropDAO = dropDAO;
	}

	public SwiftRiverDAO<Drop, Long> getServiceDAO() {
		return dropDAO;
	}

	public Drop getDrop(Long id) {
		return dropDAO.findById(id);
	}

	@Override
	public void createDrops(Collection<Drop> drops) {
		dropDAO.createDrops(drops);
	}

	@Override
	public void addLink(long dropId, Link link) {
		dropDAO.addLink(dropId, link);
	}

	@Override
	public void addLinks(long dropId, Collection<Link> links) {
		dropDAO.addLinks(dropId, links);
	}

	@Override
	public void removeLink(long dropId, Link link) {
		dropDAO.removeLink(dropId, link);
	}

	@Override
	public Collection<Link> getLinks(long dropId) {
		return dropDAO.getLinks(dropId);
	}

	@Override
	public void addPlace(Long dropId, Place place) {
		dropDAO.addPlace(dropId, place);
	}

	@Override
	public void addPlaces(long dropId, Collection<Place> places) {
		dropDAO.addPlaces(dropId, places);
	}

	@Override
	public void removePlace(Long dropId, Place place) {
		dropDAO.removePlace(dropId, place);
	}

	@Override
	public Collection<Place> getPlaces(long dropId) {
		return dropDAO.getPlaces(dropId);
	}

	@Override
	public void addMedia(long dropId, Media media) {
		dropDAO.addMedia(dropId, media);
	}

	@Override
	public void addMultipleMedia(long dropId, Collection<Media> media) {
		dropDAO.addMultipleMedia(dropId, media);
	}

	@Override
	public void removeMedia(long dropId, Media media) {
		dropDAO.removeMedia(dropId, media);
	}

	@Override
	public Collection<Media> getMedia(long dropId) {
		return dropDAO.getMedia(dropId);
	}

	@Override
	public void addTag(Long dropId, Tag tag) {
		dropDAO.addTag(dropId, tag);
	}

	@Override
	public void addTags(long dropId, Collection<Tag> tags) {
		dropDAO.addTags(dropId, tags);
	}

	@Override
	public void removeTag(Long dropId, Tag tag) {
		dropDAO.removeTag(dropId, tag);
	}

	@Override
	public Collection<Tag> getTags(long dropId) {
		return dropDAO.getTags(dropId);
	}

}
