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

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.controller.DropsController;
import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.api.dao.LinkDao;
import com.ushahidi.swiftriver.core.api.dao.PlaceDao;
import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.api.dto.CreateCommentDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.CreatePlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateTagDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCommentDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetPlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetTagDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.DropComment;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.Tag;
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

	@Autowired
	private TagDao tagDao;
	
	/**
	 * Adds the {@link DropComment} in <code>dto</code> to the list of
	 * comments for the drop in <code>id</code> and associates
	 * it with the {@link Account} owned by <code>username</code>.
	 * The created entity is transformed to DTO for purposes of consumption
	 * by {@link DropsController}
	 * 
	 * @param id
	 * @param dto
	 * @param username
	 * @return
	 */
	@Transactional
	public GetCommentDTO addComment(long id, CreateCommentDTO dto,
			String username) {
		Drop drop = getDropById(id);

		Account account = accountDao.findByUsername(username);

		// Post the comment to the database
		DropComment comment = dropDao.addComment(drop, account, dto.getCommentText());

		GetCommentDTO commentDTO =  mapper.map(comment, GetCommentDTO.class);
		commentDTO.getAccount().setEmail(comment.getAccount().getOwner().getEmail());
		commentDTO.getAccount().setName(comment.getAccount().getOwner().getName());

		return commentDTO;
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
		Drop drop = getDropById(id);

		List<GetCommentDTO> dropComments = new ArrayList<GetCommentDTO>();
		
		for (DropComment comment: drop.getComments()) {
			GetCommentDTO commentDTO = mapper.map(comment, GetCommentDTO.class);
			commentDTO.getAccount().setEmail(comment.getAccount().getOwner().getEmail());
			commentDTO.getAccount().setName(comment.getAccount().getOwner().getName());

			dropComments.add(commentDTO);
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
	  * @param dto
	  * @param username
	  * @return
	  */
	 @Transactional
	 public GetLinkDTO addLink(Long id, CreateLinkDTO dto, String username) {
		Drop drop = getDropById(id);

		if (dto.getUrl() == null || dto.getUrl().trim().length() == 0) {
			throw new BadRequestException();
		}

		String hash = HashUtil.md5(dto.getUrl());
		Link link = linkDao.findByHash(hash);
		if (link == null) {
			link = new Link();
			link.setUrl(dto.getUrl());
			link.setHash(hash);
			
			linkDao.create(link);
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
		Drop drop = getDropById(linkId);
		
		// Does the link exist?
		Link link = linkDao.findById(linkId);
		if (link == null) {
			throw new NotFoundException("The requested link does not exist");
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
	  * @param dto
	  * @param principal
	  * @return
	  */
	 @Transactional
	 public GetPlaceDTO addPlace(long id, CreatePlaceDTO dto, String username) {
		 Drop drop = getDropById(id);

		 String hash = HashUtil.md5(dto.getName() + Float.toString(dto.getLongitude()) +  Float.toString(dto.getLatitude()));

		 // Generate a hash for the place name
		 Place place = placeDao.findByHash(hash);
		 if (place == null) {
			 place = new Place();
			 place.setPlaceName(dto.getName());
			 place.setPlaceNameCanonical(dto.getName().toLowerCase());
			 place.setHash(hash);
			 place.setLatitude(dto.getLatitude());
			 place.setLongitude(dto.getLongitude());

			 placeDao.create(place);
		 }

		 Account account = accountDao.findByUsername(username);
		 dropDao.addPlace(drop, account, place);

		 return mapper.map(place, GetPlaceDTO.class);
	 }

	 /**
	  * Deletes the place with the specified <code>placeId</code>
	  * from the drop specified in <code>id</code> by marking
	  * it (the place) as removed for the {@link Account} associated
	  * with <code>username</code>
	  * 
	  * @param id
	  * @param placeId
	  * @param username
	  */
	 @Transactional
	 public void deletePlace(long id, long placeId, String username) {
		 Drop drop = getDropById(placeId);

		 Place place = placeDao.findById(placeId);
		 if (place == null) {
			 throw new NotFoundException();
		 }
		 
		 Account account = accountDao.findByUsername(username);
		 dropDao.removePlace(drop, place, account);
	 }

	 /**
	  * Creates a {@link Tag} entity from <code>tag</code> and adds it
	  * to the list of tags visible to the {@link Account} associated
	  * with <code>username</code> whenever they access the drop specified
	  * in <code>id</code>. The created entity is transformed to DTO for
	  * purposes of consumption by {@link DropsController}
	  * 
	  * @param id
	  * @param dto
	  * @param username
	  * @return
	  */
	 @Transactional
	 public GetTagDTO addTag(Long id, CreateTagDTO dto, String username) {
		 Drop drop = getDropById(id);
		 
		 String hash = HashUtil.md5(dto.getTag() + dto.getTagType());
		 Tag tag = tagDao.findByHash(hash);
		 if (tag == null) {
			 tag = new Tag();
			 tag.setTag(dto.getTag());
			 tag.setTagCanonical(dto.getTag().toLowerCase());
			 tag.setType(dto.getTagType());
			 tag.setHash(hash);
			 
			 tagDao.create(tag);
		 }
		 
		 Account account = accountDao.findByUsername(username);
		 dropDao.addTag(drop, tag, account);
		 
		 return mapper.map(tag, GetTagDTO.class);
	 }

	 /**
	  * Deletes the {@link Tag} from the list of tags for the drop
	  * specified in <code>id</code> by leaving it out of the list
	  * of tags (for this drop) accessible to the {@link Account}
	  * associated with <code>username</code>
	  *  
	  * @param id
	  * @param tagId
	  * @param username
	  */
	 @Transactional
	 public void deleteTag(long id, long tagId, String username) {
		 Drop drop = getDropById(id);
		 
		 Tag tag = tagDao.findById(tagId);
		 if (tag == null) {
			 throw new NotFoundException("The requested tag does not exist");
		 }
		 
		 Account account = accountDao.findByUsername(username);
		 dropDao.removeTag(drop, tag, account);
	 }
	 
	 /**
	  * Internal helper method to locate the drop in the
	  * database
	  * 
	  * @param id
	  * @return
	  */
	 private Drop getDropById(long id) {
		 Drop drop = dropDao.findById(id);
		 if (drop == null) {
			 throw new NotFoundException("The requested drop does not exist");
		 }
		 return drop;
	 }
	 
	 
}
