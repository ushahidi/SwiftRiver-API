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
 */package com.ushahidi.swiftriver.core.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.api.dao.LinkDao;
import com.ushahidi.swiftriver.core.api.dao.PlaceDao;
import com.ushahidi.swiftriver.core.api.dto.GetCommentDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetPlaceDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.DropComment;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.util.HashUtil;


/**
 * Drops service class
 * 
 * @author ekala
 */

@Service
public class DropService {
	
	final static Logger LOG = LoggerFactory.getLogger(DropService.class);
	
	@Autowired
	private Mapper mapper;

	@Autowired	 
	private DropDao dropDao;

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private LinkDao linkDao;

	@Autowired
	private PlaceDao placeDao;
	
	@Transactional
	public GetCommentDTO addComment(long id, Map<String, Object> body,
			String username) {
		Drop drop = dropDao.findById(id);
		if (drop == null) {
			throw new NotFoundException();
		}

		Account account = accountDao.findByUsername(username);
		String commentText = (String) body.get("comment_text");

		DropComment comment = dropDao.addComment(drop, account, commentText);
		return mapper.map(comment, GetCommentDTO.class);
	}

	/**
	 * Gets and returns the comments for the drop with the specified
	 * <code>id</code>
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	public List<GetCommentDTO> getComments(long id) {
		Drop drop = dropDao.findById(id);
		if (drop == null) {
			throw new NotFoundException();
		}

		List<GetCommentDTO> dropComments = new ArrayList<GetCommentDTO>();
		
		for (DropComment comment: drop.getComments()) {
			dropComments.add(mapper.map(comment, GetCommentDTO.class));
		}
		
		return dropComments;
	 }

	 /**
	  * Deletes the comment 
	  * @param id
	  * @param commentId
	  * @param username
	  */
	 @Transactional
	 public void deleteComment(long id, long commentId) {
		 Drop drop = dropDao.findById(id);
		 // Check if the drop exists
		 if (drop == null) {
			 throw new NotFoundException();
		 }

		 // Does the comment exist
		 DropComment dropComment = dropDao.findCommentById(commentId);
		 if (dropComment == null || dropComment.isDeleted() || dropComment.getDrop().getId() != id) {
			 throw new NotFoundException();
		 }

		 // Delete the comment
		 dropDao.deleteComment(dropComment);
	 }
	 
	 /**
	  * Adds a link to the {@link Drop} specified in <code>id</code> with the 
	  * link only being accessible to the {@link Account} associated with the
	  * username in <code>username</code>
	  * 
	  * @param id
	  * @param username
	  * @param body
	  * @return
	  */
	 @Transactional
	 public GetLinkDTO addLink(Long id, String username, Map<String, Object> body) {
		Drop drop = dropDao.findById(id);
		if (drop == null) {
			throw new NotFoundException();
		}
		
		// Validate input parameters
		if (!body.containsKey("url")) {
			throw new BadRequestException();
		}

		String url = (String)body.get("url");
		String hash = HashUtil.md5(url);
		Link link = linkDao.findByHash(hash);
		if (link == null) {
			link = new Link();
			link.setUrl(url);
			link.setHash(hash);
			
			linkDao.save(link);
		}

		Account account = accountDao.findByUsername(username);
		dropDao.addLink(drop, account, link);

		return mapper.map(link, GetLinkDTO.class);
	}

	 /**
	  * Deletes the link with the specified <code>linkId</code>
	  * from the drop specified in <code>id</code> by marking
	  * it (the link) as removed for the {@link Account} with the username
	  * in <code>username</code>
	  * 
	  * @param id
	  * @param linkId
	  * @param username
	  * @return
	  */
	 @Transactional
	 public void deleteLink(long id, long linkId, String username) {
		Drop drop = dropDao.findById(linkId);
		if (drop == null) {
			throw new NotFoundException();
		}
		
		// Does the link exist?
		Link link = linkDao.findById(linkId);
		if (link == null) {
			throw new NotFoundException();
		}		
		
		
		Account account = accountDao.findByUsername(username);
		dropDao.removeLink(drop, link, account);
	}

	 /**
	  * Adds a {@link Place} to the drop specified in <code>id</code> with
	  * the place only accessible to the {@link Account} that is tied 
	  * to <code>username</code>
	  * 
	  * @param id
	  * @param principal
	  * @param body
	  * @return
	  */
	 @Transactional
	 public GetPlaceDTO addPlace(long id, String username, Map<String, Object> body) {
		 Drop drop = dropDao.findById(id);
		 if (drop == null) {
			 throw new NotFoundException();
		 }

		 // Validate the input data
		 String[] keys = {"name", "latitude", "longitude"};
		 for (String key: keys) {
			 if (!body.containsKey(key)) {
				 throw new BadRequestException();
			 }
		 }

		 // Extract the properties
		 String placeName = (String)body.get("name");
		 Float latitude = Float.parseFloat(((Double) body.get("latitude")).toString());
		 Float longitude = Float.parseFloat(((Double) body.get("longitude")).toString());

		 String hash = HashUtil.md5(placeName + longitude.toString() + latitude.toString());

		 // Generate a hash for the place name
		 Place place = placeDao.findByHash(hash);
		 if (place == null) {
			 place = new Place();
			 place.setPlaceName(placeName);
			 place.setPlaceNameCanonical(placeName.toLowerCase());
			 place.setHash(hash);
			 place.setLatitude(latitude);
			 place.setLongitude(longitude);

			 placeDao.save(place);
		 }

		 Account account = accountDao.findByUsername(username);
		 dropDao.addPlace(drop, account, place);

		 return mapper.map(place, GetPlaceDTO.class);
	 }

	 /**
	  * Deletes the place with the specified <code>placeId</code>
	  * from the drop specified in <code>id</code> by marking
	  * it (the link) as removed for the {@link Account} with the username
	  * in <code>username</code>
	  * 
	  * @param id
	  * @param placeId
	  * @param username
	  */
	 @Transactional
	 public void deletePlace(long id, long placeId, String username) {
		 Drop drop = dropDao.findById(id);
		 if (drop == null) {
			 throw new NotFoundException();
		 }
		 
		 Place place = placeDao.findById(placeId);
		 if (place == null) {
			 throw new NotFoundException();
		 }
		 
		 Account account = accountDao.findByUsername(username);
		 dropDao.removePlace(drop, place, account);
	 }
	 
	 
}
