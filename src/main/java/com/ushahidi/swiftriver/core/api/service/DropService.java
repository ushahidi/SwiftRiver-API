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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

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
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO;
import com.ushahidi.swiftriver.core.model.Drop;

@Service
@Transactional(readOnly = true)
public class DropService {

	final Logger LOGGER = LoggerFactory.getLogger(DropService.class);

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
	
	@Resource
	private DropIndexService dropIndexService;

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

	public void setDropIndexService(DropIndexService dropIndexService) {
		this.dropIndexService = dropIndexService;
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
		for (CreateDropDTO dto : dropDTOs) {
			drops.add(mapper.map(dto, Drop.class));
		}

		dropDao.createDrops(drops);
		
		// Index all the created drops
		LOGGER.debug("Indexing newly created drops {}", drops);
		dropIndexService.addAllToIndex(drops);

		List<GetDropDTO> getDropDTOs = new ArrayList<GetDropDTO>();
		for (Drop drop : drops) {
			getDropDTOs.add(mapper.map(drop, GetDropDTO.class));
		}

		return getDropDTOs;
	}

}
