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
import java.util.Date;
import java.util.List;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.controller.RiversController;
import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.ChannelDao;
import com.ushahidi.swiftriver.core.api.dao.RiverCollaboratorDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.api.dto.CreateChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateRiverDTO;
import com.ushahidi.swiftriver.core.api.dto.FollowerDTO;
import com.ushahidi.swiftriver.core.api.dto.GetChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO;
import com.ushahidi.swiftriver.core.api.dto.GetRiverDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyRiverDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ErrorField;
import com.ushahidi.swiftriver.core.api.exception.ForbiddenException;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Channel;
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
	private ChannelDao channelDao;

	@Autowired
	private RiverCollaboratorDao riverCollaboratorDao;

	@Autowired
	private Mapper mapper;

	public RiverDao getRiverDao() {
		return riverDao;
	}

	public void setRiverDao(RiverDao riverDao) {
		this.riverDao = riverDao;
	}

	public AccountDao getAccountDao() {
		return accountDao;
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public ChannelDao getChannelDao() {
		return channelDao;
	}

	public void setChannelDao(ChannelDao channelDao) {
		this.channelDao = channelDao;
	}

	public RiverCollaboratorDao getRiverCollaboratorDao() {
		return riverCollaboratorDao;
	}

	public void setRiverCollaboratorDao(
			RiverCollaboratorDao riverCollaboratorDao) {
		this.riverCollaboratorDao = riverCollaboratorDao;
	}

	public Mapper getMapper() {
		return mapper;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * Creates a new River
	 * 
	 * @param riverTO
	 * @return
	 */
	@Transactional(readOnly = false)
	public GetRiverDTO createRiver(CreateRiverDTO riverTO, String authUser) {
		Account account = accountDao.findByUsername(authUser);

		if (!(account.getRiverQuotaRemaining() > 0))
			throw new ForbiddenException("River quota exceeded");

		if (riverDao.findByName(riverTO.getRiverName()) != null) {
			BadRequestException ex = new BadRequestException(
					"Duplicate river name");
			List<ErrorField> errors = new ArrayList<ErrorField>();
			errors.add(new ErrorField("name", "duplicate"));
			ex.setErrors(errors);
			throw ex;
		}

		River river = mapper.map(riverTO, River.class);
		river.setAccount(account);
		riverDao.create(river);

		accountDao.decreaseRiverQuota(account, 1);

		return mapper.map(river, GetRiverDTO.class);
	}

	/**
	 * Modify an existing river.
	 * 
	 * @param riverId
	 * @param modifyRiverTO
	 * @param authUser
	 * @return
	 */
	@Transactional(readOnly = false)
	public GetRiverDTO modifyRiver(Long riverId, ModifyRiverDTO modifyRiverTO,
			String authUser) {
		Account account = accountDao.findByUsername(authUser);
		River river = riverDao.findById(riverId);

		if (!isOwner(river, account))
			throw new ForbiddenException(
					"Authenticated user does not own the river");

		if (modifyRiverTO.getRiverName() != null
				&& !modifyRiverTO.getRiverName().equals(river.getRiverName())) {
			if (riverDao.findByName(modifyRiverTO.getRiverName()) != null) {
				BadRequestException ex = new BadRequestException(
						"Duplicate river name");
				List<ErrorField> errors = new ArrayList<ErrorField>();
				errors.add(new ErrorField("name", "duplicate"));
				ex.setErrors(errors);
				throw ex;
			}
		}

		mapper.map(modifyRiverTO, river);
		riverDao.update(river);

		return mapper.map(river, GetRiverDTO.class);
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
			throw new NotFoundException(String.format(
					"River with id %d not found", id));
		}

		return mapper.map(river, GetRiverDTO.class);
	}

	/**
	 * Add a channel to the given river.
	 * 
	 * @param riverId
	 * @param createChannelTO
	 * @return
	 */
	public GetChannelDTO createChannel(Long riverId,
			CreateChannelDTO createChannelTO) {
		River river = riverDao.findById(riverId);

		if (river == null) {
			throw new NotFoundException("River not found");
		}

		Channel channel = mapper.map(createChannelTO, Channel.class);
		channel.setRiver(river);
		channelDao.create(channel);

		return mapper.map(channel, GetChannelDTO.class);
	}

	/**
	 * @param riverId
	 * @param channelId
	 */
	@Transactional(readOnly = false)
	public void deleteChannel(Long riverId, Long channelId, String authUser) {
		Channel channel = getRiverChannel(riverId, channelId, authUser);
		channelDao.delete(channel);
	}

	@Transactional(readOnly = false)
	public GetChannelDTO modifyChannel(Long riverId, Long channelId,
			ModifyChannelDTO modifyChannelTO, String authUser) {
		Channel channel = getRiverChannel(riverId, channelId, authUser);
		mapper.map(modifyChannelTO, channel);
		channelDao.update(channel);

		return mapper.map(channel, GetChannelDTO.class);
	}

	public Channel getRiverChannel(Long riverId, long channelId, String authUser) {
		Channel channel = channelDao.findById(channelId);

		if (channel == null)
			throw new NotFoundException("The given channel was not found");

		River river = channel.getRiver();
		if (!river.getId().equals(riverId))
			throw new NotFoundException(
					"The given river does not countain the given channel.");

		Account account = accountDao.findByUsername(authUser);

		if (!isOwner(river, account))
			throw new ForbiddenException(
					"Logged in user does not own the river.");

		return channel;
	}

	/**
	 * Get a list of drops in the river
	 * 
	 * @param id
	 *            - Id of the river
	 * @param maxId
	 *            - Maximum id of the drops to return
	 * @param dropCount
	 *            - Number of drops to return
	 * @param username
	 *            - Username of the account querying the drops
	 * @return
	 * @throws NotFoundException
	 */
	public List<GetDropDTO> getDrops(Long id, Long maxId, Long sinceId,
			int page, int dropCount, List<String> channelList,
			List<Long> channelIds, Boolean isRead, Date dateFrom, Date dateTo,
			String username) throws NotFoundException {

		Account queryingAccount = accountDao.findByUsername(username);
		River river = riverDao.findById(id);

		if (river == null) {
			throw new NotFoundException("River not found");
		}

		List<Drop> drops = null;
		if (sinceId != null) {
			drops = riverDao.getDropsSince(id, sinceId, dropCount, channelList,
					channelIds, isRead, dateFrom, dateTo, queryingAccount);
		} else {
			drops = riverDao.getDrops(id, maxId, page, dropCount, channelList,
					channelIds, isRead, dateFrom, dateTo, queryingAccount);
		}

		List<GetDropDTO> getDropDTOs = new ArrayList<GetDropDTO>();

		if (drops == null) {
			throw new NotFoundException("No drops found");
		}

		for (Drop drop : drops) {
			getDropDTOs.add(mapper.map(drop, GetDropDTO.class));
		}
		return getDropDTOs;
	}

	/**
	 * Deletes a river
	 * 
	 * @param id
	 */
	@Transactional(readOnly = false)
	public boolean deleteRiver(Long id) {
		River river = riverDao.findById(id);

		// Throw exception if the river doesn't exist
		if (river == null) {
			throw new NotFoundException("River not found");
		}

		// Delete the river
		riverDao.delete(river);
		return true;
	}

	/**
	 * Get collaborators of the given river
	 * 
	 * @param riverId
	 * @return
	 * @throws NotFoundException
	 */
	public List<GetCollaboratorDTO> getCollaborators(Long riverId)
			throws NotFoundException {
		River river = riverDao.findById(riverId);
		if (river == null) {
			throw new NotFoundException("River not found");
		}

		List<GetCollaboratorDTO> collaborators = new ArrayList<GetCollaboratorDTO>();

		for (RiverCollaborator collaborator : river.getCollaborators()) {
			collaborators.add(mapper
					.map(collaborator, GetCollaboratorDTO.class));
		}

		return collaborators;
	}

	/**
	 * Adds a collaborator to the specified river
	 * 
	 * @param riverId
	 * @param createCollaboratorTO
	 * @throws NotFoundException
	 *             ,BadRequestException
	 */
	public GetCollaboratorDTO addCollaborator(Long riverId,
			CreateCollaboratorDTO createCollaboratorTO, String authUser)
			throws NotFoundException, BadRequestException {

		// Check if the river exists
		River river = riverDao.findById(riverId);
		if (river == null)
			throw new NotFoundException("River not found.");

		// Check if the authenticating user has permission to add a collaborator
		Account authAccount = accountDao.findByUsername(authUser);
		if (!isOwner(river, authAccount))
			throw new ForbiddenException("Permission denied.");

		// Is the account already collaborating on the river
		if (riverDao.findCollaborator(riverId, createCollaboratorTO
				.getAccount().getId()) != null)
			throw new BadRequestException(
					"The account is already collaborating on the river");

		Account account = accountDao.findById(createCollaboratorTO.getAccount()
				.getId());
		if (account == null)
			throw new NotFoundException("Account not found");

		RiverCollaborator collaborator = riverDao.addCollaborator(river,
				account, createCollaboratorTO.isReadOnly());

		return mapper.map(collaborator, GetCollaboratorDTO.class);
	}

	/**
	 * Modifies a collaborator
	 * 
	 * @param riverId
	 * @param accountId
	 * @param modifyCollaboratorTO
	 * @return
	 */
	@Transactional(readOnly = false)
	public GetCollaboratorDTO modifyCollaborator(Long riverId,
			Long collaboratorId, ModifyCollaboratorDTO modifyCollaboratorTO,
			String authUser) {

		River river = riverDao.findById(riverId);

		if (river == null)
			throw new NotFoundException("River not found.");

		Account authAccount = accountDao.findByUsername(authUser);

		if (!isOwner(river, authAccount))
			throw new ForbiddenException("Permission denied.");

		RiverCollaborator collaborator = riverCollaboratorDao
				.findById(collaboratorId);

		// Collaborator exists?
		if (collaborator == null) {
			throw new NotFoundException("Collaborator not found");
		}

		if (modifyCollaboratorTO.getActive() != null) {
			collaborator.setActive(modifyCollaboratorTO.getActive());
		}

		if (modifyCollaboratorTO.getReadOnly() != null) {
			collaborator.setReadOnly(modifyCollaboratorTO.getReadOnly());
		}

		// Post changes to the DB
		riverDao.updateCollaborator(collaborator);

		return mapper.map(collaborator, GetCollaboratorDTO.class);
	}

	/**
	 * Removes a collaborator in <code>accountId</code> from the river specified
	 * in <code>riverId</code>. <code>accountId</code> is the {@link Account} id
	 * of the collaborator
	 * 
	 * @param riverId
	 * @param accountId
	 */
	@Transactional
	public void deleteCollaborator(Long riverId, Long collaboratorId,
			String authUser) {

		River river = riverDao.findById(riverId);

		if (river == null)
			throw new NotFoundException("River not found.");

		Account authAccount = accountDao.findByUsername(authUser);

		if (!isOwner(river, authAccount))
			throw new ForbiddenException("Permission denied.");

		RiverCollaborator collaborator = riverCollaboratorDao
				.findById(collaboratorId);

		if (collaborator == null)
			throw new NotFoundException("Collaborator not found.");

		riverCollaboratorDao.delete(collaborator);
	}

	/**
	 * Adds a follower to the specified river
	 * 
	 * @param id
	 * @param body
	 * @return
	 */
	@Transactional
	public void addFollower(Long id, FollowerDTO body) {
		// Does the river exist?
		River river = riverDao.findById(id);
		if (river == null) {
			throw new NotFoundException("River not found");
		}

		Account account = accountDao.findById(body.getId());
		if (account == null) {
			throw new NotFoundException("Account not found");
		}

		river.getFollowers().add(account);
		riverDao.update(river);

	}

	/**
	 * Gets and returns a list of {@link Account} entities that are following
	 * the river identified by <code>id</code>. The entities are transformed to
	 * DTO for purposes of consumption by {@link RiversController}
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	public List<FollowerDTO> getFollowers(Long id) {
		River river = riverDao.findById(id);

		// Does the river exist?
		if (river == null) {
			throw new NotFoundException("River not found");
		}

		List<FollowerDTO> followerList = new ArrayList<FollowerDTO>();
		for (Account account : river.getFollowers()) {
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
			throw new NotFoundException("River not found");
		}

		// Load the account and check if it exists
		Account account = accountDao.findById(accountId);
		if (account == null) {
			throw new NotFoundException("Account not found");
		}

		river.getFollowers().remove(account);
		riverDao.update(river);
	}

	/**
	 * Deletes the drop specified by <code>dropId</code> from the river in
	 * <code>id</code>
	 * 
	 * @param id
	 * @param dropId
	 * @return boolean
	 */
	public boolean deleteDrop(Long id, Long dropId) {
		return riverDao.removeDrop(id, dropId);
	}

	public boolean isOwner(River river, Account account) {
		return river.getAccount() == account
				|| account.getCollaboratingRivers().contains(river);
	}
}
