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

import com.ushahidi.swiftriver.core.api.controller.RiversController;
import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.ChannelDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.api.dto.AccountDTO;
import com.ushahidi.swiftriver.core.api.dto.ChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.ChannelOptionDTO;
import com.ushahidi.swiftriver.core.api.dto.DropDTO;
import com.ushahidi.swiftriver.core.api.dto.RiverCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.RiverDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ResourceNotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Channel;
import com.ushahidi.swiftriver.core.model.ChannelOption;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverCollaborator;

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

	@Autowired
	private AccountDao accountDao;

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
		River river = riverDao.save(riverDTO.createEntityFromMap(riverData));		
		return riverDTO.createMapFromEntity(river);
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
		if (river == null)
			return null;
		
		// Load the channels
		river.getChannels();
		return new RiverDTO().createMapFromEntity(river);
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
			dropsArray.add(dropDTO.createMapFromEntity(drop));
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
		River river  = riverDao.findById(id);
		
		// River exists
		if (river ==  null) {
			throw new ResourceNotFoundException();
		}
		
		RiverDTO dto = new RiverDTO();
		if (!dto.updateEntityFromMap(river, body)) {
			throw new BadRequestException("Invalid or missing parameters");
		}
		
		// Save the changes
		riverDao.update(river);

		return dto.createMapFromEntity(river);		
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
			channelsList.add(channelDTO.createMapFromEntity(channel));
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
		if (river == null)
			throw new ResourceNotFoundException("The specified river does not exist");

		Channel channel = dto.createEntityFromMap(body);
		channel.setRiver(river);
		
		channelDao.save(channel);
		
		// Check for channel options
		if (body.containsKey("options")) {			
			channelDao.addChannelOptions(channel, getChannelOptionsFromMap(body));
		}
		
		return dto.createMapFromEntity(channel);
	}


	/**
	 * Modifies an existing channel
	 * 
	 * @param id
	 * @param channelId
	 * @param body
	 * @return
	 */
	@Transactional
	public Map<String, Object> modifyChannel(Long id, Integer channelId,
			Map<String, Object> body) {

		Channel channel = channelDao.findById(channelId);
		
		if (channel == null) {
			throw new ResourceNotFoundException();
		}
		
		// Does the channel belong to the river
		if (!channel.getRiver().getId().equals(id)) {
			throw new ResourceNotFoundException();
		}

		ChannelDTO dto = new ChannelDTO();
		if (!dto.updateEntityFromMap(channel, body)) {
			throw new BadRequestException();
		}
		
		// Remove all channel options for the current channel
		channelDao.deleteAllChannelOptions(channel);
		
		channelDao.addChannelOptions(channel, getChannelOptionsFromMap(body));
		
		return dto.createMapFromEntity(channel);
	}

	@SuppressWarnings("unchecked")
	private List<ChannelOption> getChannelOptionsFromMap(
			Map<String, Object> body) {

		List<Map<String, Object>> options = (List<Map<String, Object>>)body.get("options");
		ChannelOptionDTO optionDTO = new ChannelOptionDTO();
		
		List<ChannelOption> channelOptions = new ArrayList<ChannelOption>();
		for (Map<String, Object> entry: options) {
			channelOptions.add(optionDTO.createEntityFromMap(entry));
		}
		
		return channelOptions;
	}

	/**
	 * Deletes the channel identified by @param channel_id
	 * from the river
	 *  
	 * @param riverId
	 * @param channelId
	 */
	@Transactional
	public void deleteChannel(Long riverId, Integer channelId) {
		// Look up the channel in the databases
		Channel channel = channelDao.findById(channelId);

		// Does the channel exist?
		if (channel == null) {
			LOG.error(String.format("The specified channel(%d) does not exist", channelId));
			throw new ResourceNotFoundException();
		}

		// Does the channel belong to the specified river
		if (channel.getRiver() == null || (!channel.getRiver().getId().equals(riverId))) {
			LOG.error("The channel does not belong to the specified river or the river is invalid");
			throw new ResourceNotFoundException();
		}			
		
		channelDao.delete(channel);
	}

	@Transactional
	public List<Map<String, Object>> getCollaborators(Long riverId) {
		List<Map<String, Object>> collaborators = new ArrayList<Map<String,Object>>();
		
		RiverCollaboratorDTO dto = new RiverCollaboratorDTO();
		River river = riverDao.findById(riverId);
		for (RiverCollaborator entry: river.getCollaborators()) {
			collaborators.add(dto.createMapFromEntity(entry));
		}
		
		return collaborators;
	}

	/**
	 * Adds a collaborator to the specified river
	 * 
	 * @param riverId
	 * @param body
	 */
	@SuppressWarnings("unchecked")
	@Transactional
	public Map<String, Object> addCollaborator(Long riverId, Map<String, Object> body) {
		boolean readOnly = (Boolean)body.get("read_only");

		// Get the account
		Integer accountId = (Integer) ((Map<String, Object>)body.get("account")).get("id");
		Account account = accountDao.findById(Long.parseLong(accountId.toString()));
		
		RiverCollaborator collaborator = riverDao.addCollaborator(riverId, account, readOnly);
		
		return new RiverCollaboratorDTO().createMapFromEntity(collaborator);
		
	}

	/**
	 * Modifies a collaborator
	 * 
	 * @param riverId
	 * @param collaboratorId
	 * @param body
	 * @return
	 */
	@Transactional
	public Map<String, Object> modifyCollaborator(Long riverId,
			Long collaboratorId, Map<String, Object> body) {

		RiverCollaborator collaborator = riverDao.findCollaborator(riverId, collaboratorId);
		
		// Collaborator exists?
		if (collaborator == null) {
			throw new ResourceNotFoundException();
		}

		RiverCollaboratorDTO dto = new RiverCollaboratorDTO();
		
		// Copy the new properties to the loaded entity
		if (!dto.updateEntityFromMap(collaborator, body)) {
			throw new BadRequestException();
		}
		
		// Post changes to the DB
		riverDao.updateCollaborator(collaborator);

		return dto.createMapFromEntity(collaborator);		
	}

	/**
	 * Removes a collaborator from the specified river
	 * @param riverId
	 * @param collaboratorId
	 */
	@Transactional
	public void deleteCollaborator(Long riverId, Long collaboratorId) {
		riverDao.deleteCollaborator(riverId, collaboratorId);
	}

	/**
	 * Adds a follower to the specified river
	 * @param id
	 * @param body
	 * @return
	 */
	@Transactional
	public Map<String, Object> addFollower(Long id, Map<String, Object> body) {
		if (!body.containsKey("id")) {
			throw new BadRequestException();
		}
		
		// Load the river
		River river = riverDao.findById(id);

		Integer accountId = (Integer) body.get("id");
		Account account = accountDao.findById(Long.parseLong(accountId.toString()));

		river.getFollowers().add(account);
		riverDao.update(river);

		return new AccountDTO().createMapFromEntity(account);
	}

	/**
	 * Gets and returns a list of {@link Account} entities that are following 
	 * the river identified by <code>id</code>. The entities are transformed
	 * to DTO for purposes of consumption by {@link RiversController}
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	public List<Map<String, Object>> getFollowers(Long id) {
		River river = riverDao.findById(id);
		
		// Does the river exist?
		if (river == null) {
			throw new ResourceNotFoundException();
		}

		List<Map<String, Object>> followerList = new ArrayList<Map<String,Object>>();
		AccountDTO dto = new AccountDTO();
		for (Account account: river.getFollowers()) {
			followerList.add(dto.createMapFromEntity(account));
		}
		
		return followerList;
	}

	/**
	 * Deletes the follower whose {@link Account} id is <code>followerId</code>
	 * from the river specified by <code>riverId</code>
	 * 
	 * @param riverId
	 * @param followerId
	 */
	@Transactional
	public void deleteFollower(Long riverId, Long followerId) {
		// Load the river and check if it exists
		River river = riverDao.findById(riverId);		
		if (river == null) {
			throw new ResourceNotFoundException();
		}

		// Load the account and check if it exists
		Account account = accountDao.findById(followerId);
		if (account == null) {
			throw new ResourceNotFoundException();
		}
		
		river.getFollowers().remove(account);
		riverDao.update(river);
	}
}
