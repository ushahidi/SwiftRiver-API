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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.ChannelDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.api.dto.ChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.DropDTO;
import com.ushahidi.swiftriver.core.api.dto.RiverDTO;
import com.ushahidi.swiftriver.core.model.Channel;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverCollaborator;
import com.ushahidi.swiftriver.core.utils.SwiftRiverUtils;

/**
 * Service class for rivers
 * @author ekala
 *
 */
@Service
public class RiverService {

	@Autowired
	private RiverDao riverDao;
	
	@Autowired
	private ChannelDao channelDao;

	/* Logger */
	final static Logger LOG = LoggerFactory.getLogger(RiverService.class);

	public void setRiverDao(RiverDao riverDao) {
		this.riverDao = riverDao;
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
	@Transactional
	public Map<String, Object> getRiver(Long id) {
		// Fetch the river from the database
		River river = riverDao.findById(id);
		
		// Verify that the river exists
		if (river == null) {
			LOG.debug(String.format("Could not find river with id %d", id));
			return null;
		}
		
		// Load the channels
		river.getChannels();
		return new RiverDTO().createDTO(river);
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
	public List<Map<String, Object>> getDropsSinceId(Long id, Long sinceId, int dropCount) {
		List<Map<String, Object>> dropsArray = new ArrayList<Map<String,Object>>();
		DropDTO dropDTO = new DropDTO();
		for (Drop drop: riverDao.getDrops(id, sinceId, dropCount)) {
			dropsArray.add(dropDTO.createDTO(drop));
		}

		return dropsArray;
	}

	/**
	 * Modifies an existing river and returns {@link Map}
	 * representation (of the river) with the modified data
	 * 
	 * @param id
	 * @param body
	 * @return
	 */
	@Transactional(readOnly = false)
	public Map<String, Object> updateRiver(Long id, Map<String, Object> body) {
		River river = riverDao.findById(id);
		
		if (river == null) {
			// TODO throw not found exception
		}

		String riverName = (String) body.get("name");

		river.setRiverName(riverName);
		river.setRiverPublic(Boolean.parseBoolean((String) body.get("public")));
		river.setRiverNameUrl(SwiftRiverUtils.getURLSlug(riverName));

		// Save the changes
		riverDao.update(river);
		
		return new RiverDTO().createDTO(river);		
	}

	/**
	 * Deletes a river
	 * @param id
	 */
	@Transactional
	public boolean deleteRiver(Long id) {
		River river = riverDao.findById(id);
		
		// Throw exception if the river doesn't exist
		if (river == null) {
			LOG.info(String.format("River with id %d not found", id.longValue()));
			return false;
		}

		// Delete the river
		riverDao.delete(river);
		return true;
	}

	/**
	 * Gets and returns the channels in a river as a list of DTOs
	 * @param riverId
	 * @return
	 */
	@Transactional
	public List<Map<String, Object>> getChannels(Long riverId) {
		List<Map<String, Object>> channelsList = new ArrayList<Map<String,Object>>();
		ChannelDTO channelDTO = new ChannelDTO();
	
		River river = riverDao.findById(riverId);
		for (Channel channel: river.getChannels()) {
			channelsList.add(channelDTO.createDTO(channel));
		}
	
		return channelsList;
	}

	/**
	 * Adds a channel to a river
	 * 
	 * @param id
	 * @param body
	 * @return
	 */
	@Transactional
	public Map<String, Object> addChannel(Long id, Map<String, Object> body) {
		ChannelDTO dto = new ChannelDTO();
		
		River river = riverDao.findById(id);
		Channel channel = dto.createModel(body);
		channel.setRiver(river);
		
		river.getChannels().add(channel);
		channelDao.save(channel);
		
		return dto.createDTO(channel);
	}

	@Transactional
	public List<Map<String, Object>> getCollaborators(Long id) {
		List<Map<String, Object>> collaborators = new ArrayList<Map<String,Object>>();
		
		River river = riverDao.findById(id);
		for (RiverCollaborator entry: river.getCollaborators()) {
			// TODO: Create Map<String, Object> representation for each
			// entry
		}
		
		return collaborators;
	}

}
