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

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.api.dto.GetRiverDTO;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.River;

@Transactional(readOnly = true)
@Service
public class RiverService {
	
	/* Logger */
	final Logger logger = LoggerFactory.getLogger(RiverService.class);

	@Autowired
	private RiverDao riverDao;
	
	@Autowired
	private Mapper mapper;


	public RiverDao getRiverDao() {
		return riverDao;
	}

	public void setRiverDao(RiverDao riverDao) {
		this.riverDao = riverDao;
	}
	
	public Mapper getMapper() {
		return mapper;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * Get a RiverDTO for the River with the given id
	 * 
	 * @param id
	 * @return
	 * @throws NotFoundException
	 */
	public GetRiverDTO getRiverById(Long id) throws NotFoundException {
		River river = riverDao.findById(id);
		
		if (river == null) {
			throw new NotFoundException();
		}
				
		return mapGetRiverDTO(river);
	}
	
	/**
	 * Convert the given River into a GetRiverDTO
	 * @param river
	 * @return
	 */
	public GetRiverDTO mapGetRiverDTO(River river) {
		GetRiverDTO riverDTO = mapper.map(river, GetRiverDTO.class);
		
		return riverDTO;
	}
	
}
