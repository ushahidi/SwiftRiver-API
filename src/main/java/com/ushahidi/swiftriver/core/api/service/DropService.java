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

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.api.dao.LinkDao;
import com.ushahidi.swiftriver.core.api.dao.PlaceDao;
import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.api.dto.CreateDropDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCommentDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.DropComment;


@Service
@Transactional(readOnly = true)
public class DropService {
	
	final Logger logger = LoggerFactory.getLogger(DropService.class);
	
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
	
	public Mapper getMapper() {
		return mapper;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	public DropDao getDropDao() {
		return dropDao;
	}

	public void setDropDao(DropDao dropDao) {
		this.dropDao = dropDao;
	}

	public AccountDao getAccountDao() {
		return accountDao;
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public LinkDao getLinkDao() {
		return linkDao;
	}

	public void setLinkDao(LinkDao linkDao) {
		this.linkDao = linkDao;
	}

	public PlaceDao getPlaceDao() {
		return placeDao;
	}

	public void setPlaceDao(PlaceDao placeDao) {
		this.placeDao = placeDao;
	}

	public TagDao getTagDao() {
		return tagDao;
	}

	public void setTagDao(TagDao tagDao) {
		this.tagDao = tagDao;
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

	/**
	 * Create the given list of drops
	 * 
	 * @param drops
	 * @return
	 */
	 @Transactional(readOnly = false)
	public List<GetDropDTO> createDrops(List<CreateDropDTO> dropDTOs) {
		 
		List<Drop> drops = new ArrayList<Drop>();		
		for(CreateDropDTO dto : dropDTOs) {
			drops.add(mapper.map(dto, Drop.class));
		}
		
		dropDao.createDrops(drops);
		
		List<GetDropDTO> getDropDTOs = new ArrayList<GetDropDTO>();
		for (Drop drop : drops) {
			getDropDTOs.add(mapper.map(drop, GetDropDTO.class));
		}
		
		return getDropDTOs;
	}
	 
	 
}
