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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.JpaDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.api.dto.ChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.DropDTO;
import com.ushahidi.swiftriver.core.api.dto.RiverDTO;
import com.ushahidi.swiftriver.core.model.Channel;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.River;

/**
 * Service class for rivers
 * @author ekala
 *
 */
@Service
public class RiverService {

	@Autowired
	private RiverDao riverDao;
	
	/* Logger */
	private static Logger logger = LoggerFactory.getLogger(RiverService.class);

	public void setRiverDao(RiverDao riverDao) {
		this.riverDao = riverDao;
	}

	public JpaDao<River, Long> getServiceDao() {
		return riverDao;
	}
	
	/**
	 * Creates and returns a new river
	 * @param riverData
	 * @return
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> createRiver(Map<String, Object> riverData) {
		RiverDTO riverDTO = new RiverDTO();
		
		// TODO Extract account information from OAuth headers
		River river = riverDao.save(riverDTO.createModel(riverData));		
		return riverDTO.createDTO(river);
	}

	/**
	 * Gets and returns a single river
	 * @param id
	 * @return
	 */
	public Map<String, Object> getRiver(long id) {
		// Fetch the river from the database
		River river = riverDao.findById(id);
		
		// Verify that the river exists
		if (river == null) {
			logger.debug("Could not find river with id " + id);
			return null;
		}
		
		RiverDTO riverDTO = new RiverDTO();
		return riverDTO.createDTO(river);
	}

	/**
	 * Gets and returns the list of drops with an ID greater than 
	 * @param sinceId
	 * 
	 * @param id Database ID of the river
	 * @param sinceId
	 * @param dropCount The number of drops to return
	 * @return
	 */
	@Transactional
	public ArrayList<Map<String, Object>> getDropsSinceId(Long id, Long sinceId, int dropCount) {
		ArrayList<Map<String, Object>> dropsArray = new ArrayList<Map<String,Object>>();
		DropDTO dropDTO = new DropDTO();
		for (Drop drop: riverDao.getDrops(id, sinceId, dropCount)) {
			dropsArray.add(dropDTO.createDTO(drop));
		}

		return dropsArray;
	}

	/**
	 * Gets the list of river collaborators
	 */
	@Transactional
	public List<Map<String, Object>> getCollaborators(Long riverId) {
		throw new UnsupportedOperationException();
	}

	public void removeDrop(long riverId, Drop drop) {
		throw new UnsupportedOperationException();
	}

	public void addDrop(long riverId, Drop drop) {
		throw new UnsupportedOperationException();
	}

	public void addDrops(long riverId, Collection<Drop> drops) {
		throw new UnsupportedOperationException();
	}

	public void addChannel(long riverId, Channel channel) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets and returns the channels in a river as a list of DTOs
	 * @param riverId
	 * @return
	 */
	@Transactional
	public ArrayList<Map<String, Object>> getChannels(long riverId) {
		ArrayList<Map<String, Object>> channelsList = new ArrayList<Map<String,Object>>();
		ChannelDTO channelDTO = new ChannelDTO();

		River river = riverDao.findById(riverId);
		for (Channel channel: river.getChannels()) {
			channelsList.add(channelDTO.createDTO(channel));
		}

		return channelsList;
	}

	public void removeChannel(long riverId, Channel channel) {
		riverDao.removeChannel(riverId, channel);
	}

}
