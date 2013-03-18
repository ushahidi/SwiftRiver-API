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
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.controller.RiversController;
import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.ChannelDao;
import com.ushahidi.swiftriver.core.api.dao.LinkDao;
import com.ushahidi.swiftriver.core.api.dao.PlaceDao;
import com.ushahidi.swiftriver.core.api.dao.RiverCollaboratorDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDropDao;
import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.api.dto.ChannelUpdateNotification;
import com.ushahidi.swiftriver.core.api.dto.CreateChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateCommentDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.CreatePlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateRiverDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateTagDTO;
import com.ushahidi.swiftriver.core.api.dto.FollowerDTO;
import com.ushahidi.swiftriver.core.api.dto.GetChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCommentDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetPlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetTagDTO;
import com.ushahidi.swiftriver.core.api.dto.GetRiverDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyRiverDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ErrorField;
import com.ushahidi.swiftriver.core.api.exception.ForbiddenException;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketDrop;
import com.ushahidi.swiftriver.core.model.Channel;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverCollaborator;
import com.ushahidi.swiftriver.core.model.RiverDrop;
import com.ushahidi.swiftriver.core.model.RiverDropComment;
import com.ushahidi.swiftriver.core.model.Tag;
import com.ushahidi.swiftriver.core.util.HashUtil;

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

	@Autowired
	private RiverDropDao riverDropDao;

	@Autowired
	private TagDao tagDao;

	@Autowired
	private LinkDao linkDao;

	@Autowired
	private PlaceDao placeDao;

	@Autowired
	private AmqpTemplate amqpTemplate;

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

	public void setRiverDropDao(RiverDropDao riverDropDao) {
		this.riverDropDao = riverDropDao;
	}

	public void setTagDao(TagDao tagDao) {
		this.tagDao = tagDao;
	}

	public void setLinkDao(LinkDao linkDao) {
		this.linkDao = linkDao;
	}

	public void setPlaceDao(PlaceDao placeDao) {
		this.placeDao = placeDao;
	}

	public AmqpTemplate getAmqpTemplate() {
		return amqpTemplate;
	}

	public void setAmqpTemplate(AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
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
		River river = getRiver(riverId);
		Account account = accountDao.findByUsername(authUser);

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
		River river = getRiver(id);

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
		River river = getRiver(riverId);

		Channel channel = mapper.map(createChannelTO, Channel.class);
		channel.setRiver(river);
		channelDao.create(channel);

		ChannelUpdateNotification notification = new ChannelUpdateNotification();
		notification.setChannel(channel.getChannel());
		notification.setRiverId(riverId);
		notification.setParameters(channel.getParameters());
		amqpTemplate.convertAndSend("web.channel." + notification.getChannel()
				+ ".add", notification);

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

		ChannelUpdateNotification notification = new ChannelUpdateNotification();
		notification.setChannel(channel.getChannel());
		notification.setRiverId(riverId);
		notification.setParameters(channel.getParameters());
		amqpTemplate.convertAndSend("web.channel." + notification.getChannel()
				+ ".delete", notification);
	}

	@Transactional(readOnly = false)
	public GetChannelDTO modifyChannel(Long riverId, Long channelId,
			ModifyChannelDTO modifyChannelTO, String authUser) {
		Channel channel = getRiverChannel(riverId, channelId, authUser);

		// Get the channel before modification for a deletion notification
		ChannelUpdateNotification beforeNotification = new ChannelUpdateNotification();
		beforeNotification.setChannel(channel.getChannel());
		beforeNotification.setRiverId(riverId);
		beforeNotification.setParameters(channel.getParameters());

		mapper.map(modifyChannelTO, channel);
		channelDao.update(channel);

		// Get the channel after modification for an add notification
		ChannelUpdateNotification afterNotification = new ChannelUpdateNotification();
		afterNotification.setChannel(channel.getChannel());
		afterNotification.setRiverId(riverId);
		afterNotification.setParameters(channel.getParameters());

		amqpTemplate.convertAndSend(
				"web.channel." + beforeNotification.getChannel() + ".delete",
				beforeNotification);
		amqpTemplate.convertAndSend(
				"web.channel." + afterNotification.getChannel() + ".add",
				afterNotification);

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

		if (riverDao.findById(id) == null) {
			throw new NotFoundException(String.format(
					"River %d does not exist", id));
		}

		Account queryingAccount = accountDao.findByUsername(username);

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
		River river = getRiver(id);

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
		River river = getRiver(riverId);

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
		River river = getRiver(riverId);

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

		River river = getRiver(riverId);

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

		River river = getRiver(riverId);

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
	 * @param accountId
	 * @return
	 */
	@Transactional
	public void addFollower(Long id, Long accountId) {
		// Does the river exist?
		River river = getRiver(id);

		Account account = accountDao.findById(accountId);
		if (account == null) {
			throw new NotFoundException("Account not found");
		}

		river.getFollowers().add(account);
		riverDao.update(river);

	}

	/**
	 * Gets and returns a list of {@link Account} entities that are following
	 * the river identified by <code>id</code>. The entities are transformed to
	 * DTO for purposes of consumption by {@link RiversController}.
	 * 
	 * <code>accountId</code> can be null. When specified, the method verifies
	 * that the {@link Account} associated with it is following the river. If
	 * following, the return list contains only a single {@link FollowerDTO}
	 * object else, a {@link NotFoundException} is thrown
	 * 
	 * @param id
	 * @param accountId
	 * @return
	 */
	@Transactional
	public List<FollowerDTO> getFollowers(Long id, Long accountId) {
		River river = getRiver(id);

		List<FollowerDTO> followerList = new ArrayList<FollowerDTO>();
		if (accountId != null) {
			Account account = accountDao.findById(accountId);
			if (account == null) {
				throw new NotFoundException(String.format(
						"Account %d does not exist", accountId));
			}

			if (river.getFollowers().contains(account)) {
				followerList.add(mapFollowerDTO(account));
			} else {
				throw new NotFoundException(String.format(
						"Account %d does not follow river %d", accountId, id));
			}
		} else {
			for (Account account : river.getFollowers()) {
				followerList.add(mapFollowerDTO(account));
			}
		}

		return followerList;
	}

	/**
	 * Helper method for transforming an {@link Account} entity to a
	 * {@link FollowerDTO} object
	 * 
	 * @param account
	 * @return
	 */
	private FollowerDTO mapFollowerDTO(Account account) {
		FollowerDTO accountDto = mapper.map(account, FollowerDTO.class);

		accountDto.setName(account.getOwner().getName());
		accountDto.setEmail(account.getOwner().getEmail());

		return accountDto;
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
		River river = getRiver(riverId);

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

	public boolean isOwner(River river, String authUser) {
		Account account = accountDao.findByUsername(authUser);
		return isOwner(river, account);
	}

	public boolean isOwner(River river, Account account) {
		return river.getAccount() == account
				|| account.getCollaboratingRivers().contains(river);
	}

	private River getRiver(Long id) {
		River river = riverDao.findById(id);
		if (river == null) {
			throw new NotFoundException(String.format(
					"River with id %d not found", id));
		}

		return river;
	}

	/**
	 * Adds a {@link Tag} to the {@link RiverDrop} with the specified
	 * <code>dropId</code> The drop must be in the {@link River} whose ID is
	 * specified in <code>id</code>
	 * 
	 * The created {@link Tag} entity is transformed to a DTO for purposes of
	 * consumption by {@link RiversController}
	 * 
	 * @param id
	 * @param dropId
	 * @param createDTO
	 * @param name
	 * @return
	 */
	@Transactional
	public GetTagDTO addDropTag(Long riverId, Long dropId,
			CreateTagDTO createDTO, String authUser) {

		River river = getRiver(riverId);

		if (!isOwner(river, authUser))
			throw new ForbiddenException("Permission denied");

		// Get the bucket drop
		RiverDrop riverDrop = getRiverDrop(dropId, river);

		String hash = HashUtil.md5(createDTO.getTag() + createDTO.getTagType());
		Tag tag = tagDao.findByHash(hash);
		if (tag == null) {
			tag = new Tag();
			tag.setTag(createDTO.getTag());
			tag.setType(createDTO.getTagType());

			tagDao.create(tag);
		} else {
			// Check if the tag exists in the bucket drop
			if (riverDropDao.findTag(riverDrop, tag) != null) {
				throw new BadRequestException(String.format(
						"Tag %s of type %s has already been added to drop %d",
						tag.getTag(), tag.getType(), dropId));
			}
		}

		riverDropDao.addTag(riverDrop, tag);
		return mapper.map(tag, GetTagDTO.class);
	}

	/**
	 * Deletes the {@link Tag} with the id specified in <code>tagId</code> from
	 * the {@link RiverDrop} specified in <code>dropId</code>
	 * 
	 * The request {@link BucketDrop} must be a member of the {@link River} with
	 * the ID specified in <code>id</code> else a {@link NotFoundException} is
	 * thrown
	 * 
	 * @param riverId
	 * @param dropId
	 * @param tagId
	 * @param authUser
	 */
	@Transactional
	public void deleteDropTag(Long riverId, Long dropId, Long tagId,
			String authUser) {
		River river = getRiver(riverId);
		if (!isOwner(river, authUser))
			throw new ForbiddenException("Permission denied");

		RiverDrop riverDrop = getRiverDrop(dropId, river);

		Tag tag = tagDao.findById(tagId);

		if (tag == null) {
			throw new NotFoundException(String.format("Tag %d does not exist",
					tagId));
		}

		if (!riverDropDao.deleteTag(riverDrop, tag)) {
			throw new NotFoundException(String.format(
					"Drop %d does not have tag %d", dropId, tagId));
		}

	}

	/**
	 * Adds a {@link Link} to the {@link RiverDrop} with the specified
	 * <code>dropId</code> The drop must be in the {@link River} whose ID is
	 * specified in <code>id</code>
	 * 
	 * The created {@link Link} entity is transformed to a DTO for purposes of
	 * consumption by {@link RiversController}
	 * 
	 * @param id
	 * @param dropId
	 * @param createDTO
	 * @param authUser
	 * @return
	 */
	@Transactional
	public GetLinkDTO addDropLink(Long riverId, Long dropId,
			CreateLinkDTO createDTO, String authUser) {
		River river = getRiver(riverId);

		if (!isOwner(river, authUser))
			throw new ForbiddenException("Permission denied");

		RiverDrop riverDrop = getRiverDrop(dropId, river);

		String hash = HashUtil.md5(createDTO.getUrl());
		Link link = linkDao.findByHash(hash);
		if (link == null) {
			link = new Link();
			link.setUrl(createDTO.getUrl());
			link.setHash(hash);

			linkDao.create(link);
		} else {
			// Has the link already been added ?
			if (riverDropDao.findLink(riverDrop, link) != null) {
				throw new BadRequestException(String.format(
						"%s has already been added to drop %d", link.getUrl(),
						dropId));
			}
		}

		riverDropDao.addLink(riverDrop, link);
		return mapper.map(link, GetLinkDTO.class);
	}

	/**
	 * Deletes the {@link Link} with the id specified in <code>linkId</code>
	 * from the {@link RiverDrop} specified in <code>dropId</code>
	 * 
	 * The request {@link RiverDrop} must be a member of the {@link River} with
	 * the ID specified in <code>id</code> else a {@link NotFoundException} is
	 * thrown
	 * 
	 * @param id
	 * @param dropId
	 * @param linkId
	 * @param authUser
	 */
	@Transactional
	public void deleteDropLink(Long riverId, Long dropId, Long linkId,
			String authUser) {
		River river = getRiver(riverId);

		if (!isOwner(river, authUser))
			throw new ForbiddenException("Permission denied");

		RiverDrop riverDrop = getRiverDrop(dropId, river);
		Link link = linkDao.findById(linkId);

		if (link == null) {
			throw new NotFoundException(String.format("Link %d does not exist",
					linkId));
		}

		if (!riverDropDao.deleteLink(riverDrop, link)) {
			throw new NotFoundException(String.format(
					"Drop %d does not have link %d", dropId, linkId));
		}
	}

	/**
	 * Adds a {@link Place} to the {@link RiverDrop} with the specified
	 * <code>dropId</code> The drop must be in the {@link River} whose ID is
	 * specified in <code>id</code>
	 * 
	 * The created {@link Place} entity is transformed to a DTO for purposes of
	 * consumption by {@link RiversController}
	 * 
	 * @param riverId
	 * @param dropId
	 * @param createDTO
	 * @param authUser
	 * @return
	 */
	@Transactional
	public GetPlaceDTO addDropPlace(Long riverId, Long dropId,
			CreatePlaceDTO createDTO, String authUser) {
		River river = getRiver(riverId);

		if (!isOwner(river, authUser))
			throw new ForbiddenException("Permission denied");

		RiverDrop riverDrop = getRiverDrop(dropId, river);

		String hashInput = createDTO.getName();
		hashInput += Float.toString(createDTO.getLongitude());
		hashInput += Float.toString(createDTO.getLatitude());

		String hash = HashUtil.md5(hashInput);

		// Generate a hash for the place name
		Place place = placeDao.findByHash(hash);
		if (place == null) {
			place = new Place();
			place.setPlaceName(createDTO.getName());
			place.setLatitude(createDTO.getLatitude());
			place.setLongitude(createDTO.getLongitude());

			placeDao.create(place);
		} else {
			if (riverDropDao.findPlace(riverDrop, place) != null) {
				throw new BadRequestException(
						String.format(
								"Drop %d already has the place %s with coordinates [%f, %f]",
								dropId, place.getPlaceName(),
								place.getLatitude(), place.getLongitude()));
			}
		}

		riverDropDao.addPlace(riverDrop, place);
		return mapper.map(place, GetPlaceDTO.class);
	}

	/**
	 * Deletes the {@link Link} with the id specified in <code>linkId</code>
	 * from the {@link RiverDrop} specified in <code>dropId</code>
	 * 
	 * The request {@link RiverDrop} must be a member of the {@link Bucket} with
	 * the ID specified in <code>id</code> else a {@link NotFoundException} is
	 * thrown
	 * 
	 * @param riverId
	 * @param dropId
	 * @param placeId
	 * @param authUser
	 */
	@Transactional
	public void deleteDropPlace(Long riverId, Long dropId, Long placeId,
			String authUser) {
		River river = getRiver(riverId);

		if (!isOwner(river, authUser))
			throw new ForbiddenException("Permission denied");

		RiverDrop riverDrop = getRiverDrop(dropId, river);
		Place place = placeDao.findById(placeId);

		if (place == null) {
			throw new NotFoundException(String.format(
					"Place %d does not exist", placeId));
		}

		if (!riverDropDao.deletePlace(riverDrop, place)) {
			throw new NotFoundException(String.format(
					"Drop %d does not have place %d", dropId, placeId));
		}

	}

	/**
	 * Helper method to retrieve a {@link RiverDrop} record from the database
	 * and verify that the retrieved entity belongs to the {@link River}
	 * specified in <code>river</code>
	 * 
	 * @param dropId
	 * @param river
	 * @return
	 */
	private RiverDrop getRiverDrop(Long dropId, River river) {
		RiverDrop riverDrop = riverDropDao.findById(dropId);
		if (riverDrop == null
				|| (riverDrop != null && !riverDrop.getRiver().equals(river))) {
			throw new NotFoundException(
					String.format("Drop %d does not exist in river %d", dropId,
							river.getId()));
		}
		return riverDrop;
	}

	/**
	 * Filter the given list of rivers returning only those that are visible to
	 * the given queryingAccount.
	 * 
	 * @param rivers
	 * @param queryingAccount
	 * @return
	 */
	public List<River> filterVisible(List<River> rivers, Account queryingAccount) {
		List<River> visible = new ArrayList<River>();

		for (River river : rivers) {
			if (isOwner(river, queryingAccount) || river.getRiverPublic()) {
				visible.add(river);
			}
		}

		return visible;
	}

	/**
	 * Adds a comment to the {@link RiverDrop} entity specified in
	 * <code>dropId</code>. This entity must be associated with the
	 * {@link River} entity specified in <code>riverId</code> otherwise a
	 * {@link NotFoundException} will be thrown.
	 * 
	 * @param riverId
	 * @param dropId
	 * @param createDTO
	 * @param authUser
	 * @return
	 */
	public GetCommentDTO addDropComment(Long riverId, Long dropId,
			CreateCommentDTO createDTO, String authUser) {

		if (createDTO.getCommentText() == null
				|| createDTO.getCommentText().trim().length() == 0) {
			throw new BadRequestException("The no comment text specified");
		}

		River river = getRiver(riverId);

		if (!river.getRiverPublic() && !isOwner(river, authUser))
			throw new ForbiddenException("Permission Denied");

		RiverDrop riverDrop = getRiverDrop(dropId, river);
		Account account = accountDao.findByUsername(authUser);
		RiverDropComment dropComment = riverDropDao.addComment(riverDrop,
				account, createDTO.getCommentText());

		return mapper.map(dropComment, GetCommentDTO.class);
	}

	/**
	 * Get and return the list of {@link RiverDropComment} entities for the
	 * {@link RiverDrop} with the ID specified in <code>dropId</code>
	 * 
	 * @param riverId
	 * @param dropId
	 * @param authUser
	 * @return
	 */
	public List<GetCommentDTO> getDropComments(Long riverId, Long dropId,
			String authUser) {
		River river = getRiver(riverId);

		if (!river.getRiverPublic() && !isOwner(river, authUser))
			throw new ForbiddenException("Permission Denied");

		RiverDrop riverDrop = getRiverDrop(dropId, river);
		List<GetCommentDTO> commentsList = new ArrayList<GetCommentDTO>();
		for (RiverDropComment dropComment : riverDrop.getComments()) {
			GetCommentDTO commentDTO = mapper.map(dropComment,
					GetCommentDTO.class);
			commentsList.add(commentDTO);
		}

		return commentsList;
	}

	/**
	 * Deletes the {@link RiverDropComment} entity specified in
	 * <code>commentId</code> from the {@link RiverDrop} entity specified in
	 * <code>dropId</code>
	 * 
	 * @param riverId
	 * @param dropId
	 * @param commentId
	 * @param authUser
	 */
	public void deleteDropComment(Long riverId, Long dropId, Long commentId,
			String authUser) {
		River river = getRiver(riverId);

		if (!isOwner(river, authUser))
			throw new ForbiddenException("Permission Denied");

		getRiverDrop(dropId, river);

		if (!riverDropDao.deleteComment(commentId)) {
			throw new NotFoundException(String.format(
					"Comment %d does not exist", commentId));
		}
	}

}
