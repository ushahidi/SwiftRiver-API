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

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.controller.RiversController;
import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.api.dto.CreateCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.FollowerDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO;
import com.ushahidi.swiftriver.core.api.dto.GetRiverDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverCollaborator;

@Transactional(readOnly = true)
@Service
public class RiverService {
	
	/* Logger */
	final Logger logger = LoggerFactory.getLogger(RiverService.class);

	@Autowired
	private RiverDao riverDao;
	
	@Autowired
	private AccountDao accountDao;
	
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
				
		return mapper.map(river, GetRiverDTO.class);
	}

	/**
	 * Get a list of drops in the river
	 * 
	 * @param id - Id of the river
	 * @param maxId - Maximum id of the drops to return
	 * @param dropCount - Number of drops to return
	 * @param username - Username of the account querying the drops
	 * @return
	 * @throws NotFoundException
	 */
	public List<GetDropDTO> getDrops(Long id, Long maxId, int dropCount, String username) throws NotFoundException {
		
		Account queryingAccount = accountDao.findByUsername(username);
		List<Drop> drops = riverDao.getDrops(id, maxId, dropCount, queryingAccount);
		
		List<GetDropDTO> getDropDTOs = new ArrayList<GetDropDTO>();
		
		if (drops == null) {
			throw new NotFoundException();
		}
		
		for (Drop drop : drops) {
			getDropDTOs.add(mapper.map(drop, GetDropDTO.class));
		}
		return getDropDTOs; 
	}
	

	
	/**
	 * Deletes a river
	 * @param id
	 */
	@Transactional(readOnly = false)
	public boolean deleteRiver(Long id) {
		River river = riverDao.findById(id);

		// Throw exception if the river doesn't exist
		if (river == null) {
			throw new NotFoundException();
		}

		// Delete the river
		riverDao.delete(river);
		return true;
	}


	@Transactional
	public List<GetCollaboratorDTO> getCollaborators(Long riverId) throws NotFoundException {
		River river = riverDao.findById(riverId);
		if (river == null) {
			throw new NotFoundException();
		}

		List<GetCollaboratorDTO> collaborators = new ArrayList<GetCollaboratorDTO>();

		for (RiverCollaborator entry: river.getCollaborators()) {
			GetCollaboratorDTO dto = new GetCollaboratorDTO();

			dto.setId(entry.getAccount().getId());
			dto.setActive(entry.isActive());
			dto.setReadOnly(entry.isReadOnly());
			dto.setDateAdded(entry.getDateAdded());

			collaborators.add(dto);
		}

		return collaborators;
	}

	/**
	 * Adds a collaborator to the specified river
	 * 
	 * @param riverId
	 * @param body
	 * @throws NotFoundException,BadRequestException
	 */
	@Transactional
	public GetCollaboratorDTO addCollaborator(Long riverId, GetCollaboratorDTO body) throws NotFoundException,BadRequestException {
		// Check if the bucket exists
		River river = riverDao.findById(riverId);
		if (river == null) {
			throw new NotFoundException();
		}

		//Is the account already collaborating on the river
		if (riverDao.findCollaborator(riverId, body.getId()) != null) {
			throw new BadRequestException("The account is already collaborating on the river");
		}

		Account account = accountDao.findById(body.getId());
		riverDao.addCollaborator(river, account, body.isReadOnly());

		body.setId(account.getId());
		body.setAccountPath(account.getAccountPath());
		return body;
	}

	/**
	 * Modifies a collaborator
	 * 
	 * @param riverId
	 * @param accountId
	 * @param body
	 * @return
	 */
	@Transactional
	public GetCollaboratorDTO modifyCollaborator(Long riverId,
			Long accountId, CreateCollaboratorDTO body) {

		RiverCollaborator collaborator = riverDao.findCollaborator(riverId, accountId);

		// Collaborator exists?
		if (collaborator == null) {
			throw new NotFoundException();
		}

		collaborator.setActive(body.isActive());
		collaborator.setReadOnly(body.isReadOnly());

		// Post changes to the DB
		riverDao.updateCollaborator(collaborator);

		return mapCollaboratorDTO(collaborator);		
	}

	/**
	 * Removes a collaborator in <code>accountId</code> from the river
	 * specified in <code>riverId</code>. <code>accountId</code> is the
	 * {@link Account} id of the collaborator
	 * 
	 * @param riverId
	 * @param accountId
	 */
	@Transactional
	public void deleteCollaborator(Long riverId, Long accountId) {
		riverDao.deleteCollaborator(riverId, accountId);
	}

	/**
	 * Adds a follower to the specified river
	 * @param id
	 * @param body
	 * @return
	 */
	@Transactional
	public void addFollower(Long id, FollowerDTO body) {
		// Does the river exist?
		River river = riverDao.findById(id);
		if (river == null) {
			throw new NotFoundException();
		}

		Account account = accountDao.findById(body.getId());
		if (account == null) {
			throw new NotFoundException();
		}

		river.getFollowers().add(account);
		riverDao.update(river);

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
	public List<FollowerDTO> getFollowers(Long id) {
		River river = riverDao.findById(id);

		// Does the river exist?
		if (river == null) {
			throw new NotFoundException();
		}

		List<FollowerDTO> followerList = new ArrayList<FollowerDTO>();
		for (Account account: river.getFollowers()) {
			FollowerDTO accountDto = mapper.map(account, FollowerDTO.class);
			accountDto.setName(account.getOwner().getName());
			accountDto.setEmail(account.getOwner().getEmail());

			followerList.add(accountDto);
		}

		return followerList;
	}

	/**
	 * Deletes the follower whose {@link Account} id is <code>accountId</code>
	 * from the river specified by <code>riverId</code>
	 * 
	 * @param riverId
	 * @param accountId
	 */
	@Transactional
	public void deleteFollower(Long riverId, Long accountId) {
		// Load the river and check if it exists
		River river = riverDao.findById(riverId);		
		if (river == null) {
			throw new NotFoundException();
		}

		// Load the account and check if it exists
		Account account = accountDao.findById(accountId);
		if (account == null) {
			throw new NotFoundException();
		}

		river.getFollowers().remove(account);
		riverDao.update(river);
	}

	/**
	 * Deletes the drop specified by <code>dropId</code> from the
	 * river in <code>id</code> 
	 * @param id
	 * @param dropId
	 * @return boolean
	 */
	public boolean deleteDrop(Long id, Long dropId) {
		return riverDao.removeDrop(id, dropId);		
	}

	/**
	 * Maps a {@link RiverCollaborator} entity to {@link GetCollaboratorDTO}
	 * 
	 * @param collaborator
	 * @return {@link GetCollaboratorDTO}
	 */
	private GetCollaboratorDTO mapCollaboratorDTO(RiverCollaborator collaborator) {

		Account account = collaborator.getAccount();

		GetCollaboratorDTO collaboratorDTO = new GetCollaboratorDTO();
		collaboratorDTO.setId(account.getId());
		collaboratorDTO.setAccountPath(account.getAccountPath());
		collaboratorDTO.setActive(collaborator.isActive());
		collaboratorDTO.setReadOnly(collaborator.isReadOnly());
		collaboratorDTO.setDateAdded(collaborator.getDateAdded());
		
		return collaboratorDTO;
	}
}
