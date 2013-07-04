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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.AmqpTemplate;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.ChannelDao;
import com.ushahidi.swiftriver.core.api.dao.LinkDao;
import com.ushahidi.swiftriver.core.api.dao.PlaceDao;
import com.ushahidi.swiftriver.core.api.dao.RiverCollaboratorDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDropDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDropFormDao;
import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.api.dto.ChannelUpdateNotification;
import com.ushahidi.swiftriver.core.api.dto.CreateChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.CreatePlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateRiverDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateTagDTO;
import com.ushahidi.swiftriver.core.api.dto.FormValueDTO;
import com.ushahidi.swiftriver.core.api.dto.GetChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.GetRiverDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyFormValueDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyRiverDTO;
import com.ushahidi.swiftriver.core.api.exception.ForbiddenException;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.ActivityType;
import com.ushahidi.swiftriver.core.model.Channel;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverCollaborator;
import com.ushahidi.swiftriver.core.model.RiverDrop;
import com.ushahidi.swiftriver.core.model.RiverDropForm;
import com.ushahidi.swiftriver.core.model.RiverDropFormField;
import com.ushahidi.swiftriver.core.model.Tag;
import com.ushahidi.swiftriver.core.support.MapperFactory;

public class RiverServiceTest {

	private RiverDao mockRiverDao;

	private AccountDao mockAccountDao;

	private ChannelDao mockChannelDao;

	private RiverCollaboratorDao mockRiverCollaboratorDao;

	private Mapper mockMapper;

	private final static Mapper mapper = MapperFactory.getMapper();

	private RiverService riverService;

	private AccountService mockAccountService;

	private RiverDropDao mockRiverDropDao;

	private RiverDropFormDao mockRiverDropFormDao;

	private TagDao mockTagDao;

	private LinkDao mockLinkDao;

	private PlaceDao mockPlaceDao;

	private AmqpTemplate mockAmqpTemplate;

	@Before
	public void setup() {
		mockRiverDao = mock(RiverDao.class);
		mockAccountDao = mock(AccountDao.class);
		mockChannelDao = mock(ChannelDao.class);
		mockRiverCollaboratorDao = mock(RiverCollaboratorDao.class);
		mockRiverDropDao = mock(RiverDropDao.class);
		mockRiverDropFormDao = mock(RiverDropFormDao.class);
		mockTagDao = mock(TagDao.class);
		mockLinkDao = mock(LinkDao.class);
		mockPlaceDao = mock(PlaceDao.class);
		mockMapper = mock(Mapper.class);
		mockAmqpTemplate = mock(AmqpTemplate.class);
		mockAccountService = mock(AccountService.class);

		riverService = new RiverService();
		riverService.setRiverDao(mockRiverDao);
		riverService.setAccountDao(mockAccountDao);
		riverService.setChannelDao(mockChannelDao);
		riverService.setRiverCollaboratorDao(mockRiverCollaboratorDao);
		riverService.setRiverDropDao(mockRiverDropDao);
		riverService.setRiverDropFormDao(mockRiverDropFormDao);
		riverService.setTagDao(mockTagDao);
		riverService.setLinkDao(mockLinkDao);
		riverService.setPlaceDao(mockPlaceDao);
		riverService.setMapper(mockMapper);
		riverService.setAmqpTemplate(mockAmqpTemplate);
		riverService.setAccountService(mockAccountService);
	}

	@Test
	public void findById() throws NotFoundException {
		River river = new River();

		when(mockRiverDao.findById(anyLong())).thenReturn(river);

		GetRiverDTO getRiverDTO = mock(GetRiverDTO.class);
		when(mockMapper.map(river, GetRiverDTO.class)).thenReturn(getRiverDTO);

		GetRiverDTO actualGetRiverDTO = riverService.getRiverById(22L);

		verify(mockRiverDao).findById(22L);
		assertEquals(getRiverDTO, actualGetRiverDTO);
	}

	@Test
	public void createRiver() {
		CreateRiverDTO mockRiverTO = mock(CreateRiverDTO.class);
		Account mockAccount = mock(Account.class);
		River mockRiver = mock(River.class);
		GetRiverDTO mockGetRiverTO = mock(GetRiverDTO.class);

		when(mockAccountDao.findByUsernameOrEmail(anyString()))
				.thenReturn(mockAccount);
		when(mockAccount.getRiverQuotaRemaining()).thenReturn(1);
		when(mockRiverDao.findByName(anyString())).thenReturn(null);
		when(mockMapper.map(mockRiverTO, River.class)).thenReturn(mockRiver);
		when(mockRiver.getRiverName()).thenReturn("");
		when(mockMapper.map(mockRiver, GetRiverDTO.class)).thenReturn(
				mockGetRiverTO);

		GetRiverDTO actualGetRiverTO = riverService.createRiver(mockRiverTO,
				"username");

		verify(mockRiverDao).create(mockRiver);
		verify(mockAccountDao).decreaseRiverQuota(mockAccount, 1);
		verify(mockMapper).map(mockRiver, GetRiverDTO.class);
		verify(mockAccountService).logActivity(mockAccount,
				ActivityType.CREATE, mockRiver);

		assertEquals(mockGetRiverTO, actualGetRiverTO);

	}

	@Test
	public void createChannel() {
		CreateChannelDTO createChannelTO = new CreateChannelDTO();
		createChannelTO.setChannel("a channel");
		createChannelTO.setParameters("channel parameters");

		River river = new River();
		when(mockRiverDao.findById(1L)).thenReturn(river);

		riverService.setMapper(mapper);

		GetChannelDTO actualChannelTO = riverService.createChannel(1L,
				createChannelTO);

		assertNotNull(actualChannelTO.getId());

		ArgumentCaptor<ChannelUpdateNotification> notificationArgument = ArgumentCaptor
				.forClass(ChannelUpdateNotification.class);
		ArgumentCaptor<String> routingKeyArgument = ArgumentCaptor
				.forClass(String.class);
		verify(mockAmqpTemplate).convertAndSend(routingKeyArgument.capture(),
				notificationArgument.capture());
		ChannelUpdateNotification notification = notificationArgument
				.getValue();
		assertEquals("a channel", notification.getChannel());
		assertEquals("channel parameters", notification.getParameters());
		assertEquals(1L, notification.getRiverId());
		assertEquals("web.channel.a channel.add", routingKeyArgument.getValue());

		ArgumentCaptor<Channel> channelArgument = ArgumentCaptor
				.forClass(Channel.class);
		verify(mockChannelDao).create(channelArgument.capture());
		Channel channel = channelArgument.getValue();
		assertEquals(river, channel.getRiver());
		assertEquals("a channel", channel.getChannel());
		assertEquals("channel parameters", channel.getParameters());
	}

	@Test
	public void isOwnerForOwnerAccount() {
		Account account = new Account();
		account.setAccountPath("owner_account");

		River river = new River();
		river.setAccount(account);

		assertTrue(riverService.isOwner(river, account));
	}

	@Test
	public void isOwnerForEditorCollaboratingAccount() {
		RiverCollaborator collaborator = new RiverCollaborator();
		collaborator.setReadOnly(false);
		when(mockRiverDao.findCollaborator(anyLong(), anyLong())).thenReturn(collaborator);

		assertTrue(riverService.isOwner(new River(), new Account()));
	}
	
	@Test
	public void isOwnerForViewerCollaboratingAccount() {
		RiverCollaborator collaborator = new RiverCollaborator();
		collaborator.setReadOnly(true);
		when(mockRiverDao.findCollaborator(anyLong(), anyLong())).thenReturn(collaborator);

		assertFalse(riverService.isOwner(new River(), new Account()));
	}


	@Test
	public void isOwnerForNoneOwnerAccount() {
		River river = new River();
		river.setAccount(new Account());

		Account account = new Account();
		List<River> collaboratingRivers = new ArrayList<River>();
		account.setCollaboratingRivers(collaboratingRivers);

		assertFalse(riverService.isOwner(river, account));
	}

	@Test(expected = NotFoundException.class)
	public void deleteNonExistentChannel() {

		when(mockChannelDao.findById(anyLong())).thenReturn(null);

		riverService.deleteChannel(1L, 1L, null);
	}

	@Test(expected = NotFoundException.class)
	public void deleteNonExistentChannelInRiver() {
		Channel mockChannel = mock(Channel.class);
		River mockRiver = mock(River.class);
		when(mockChannelDao.findById(anyLong())).thenReturn(mockChannel);
		when(mockChannel.getRiver()).thenReturn(mockRiver);

		riverService.deleteChannel(1L, 1L, null);
	}

	@Test(expected = ForbiddenException.class)
	public void deleteOtherUserChannel() {
		Channel mockChannel = mock(Channel.class);
		River river = new River();
		river.setId(1L);
		river.setAccount(new Account());
		Account account = new Account();
		account.setAccountPath("other-account");
		account.setCollaboratingRivers(new ArrayList<River>());

		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(account);
		when(mockChannelDao.findById(anyLong())).thenReturn(mockChannel);
		when(mockChannel.getRiver()).thenReturn(river);

		riverService.deleteChannel(1L, 1L, "user");
	}

	@Test
	public void deleteChannel() {
		Account account = new Account();
		account.setAccountPath("other-account");
		account.setCollaboratingRivers(new ArrayList<River>());
		River river = new River();
		river.setId(1L);
		river.setAccount(account);
		river.setDropCount(800);
		Channel channel = new Channel();
		channel.setChannel("the channel");
		channel.setParameters("the parameters");
		channel.setDropCount(300);
		channel.setRiver(river);

		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(account);
		when(mockChannelDao.findById(anyLong())).thenReturn(channel);

		riverService.deleteChannel(1L, 1L, "user");

		ArgumentCaptor<ChannelUpdateNotification> notificationArgument = ArgumentCaptor
				.forClass(ChannelUpdateNotification.class);
		ArgumentCaptor<String> routingKeyArgument = ArgumentCaptor
				.forClass(String.class);
		verify(mockAmqpTemplate).convertAndSend(routingKeyArgument.capture(),
				notificationArgument.capture());
		ChannelUpdateNotification notification = notificationArgument
				.getValue();		
		assertEquals("the channel", notification.getChannel());
		assertEquals("the parameters", notification.getParameters());
		assertEquals(1L, notification.getRiverId());
		assertEquals("web.channel.the channel.delete",
				routingKeyArgument.getValue());

		verify(mockChannelDao).delete(channel);
		verify(mockRiverDao).update(river);
		assertEquals(500, river.getDropCount().intValue());
	}

	@Test
	public void modifyChannel() {
		Account account = new Account();
		account.setAccountPath("other-account");
		account.setCollaboratingRivers(new ArrayList<River>());

		River river = new River();
		river.setId(1L);
		river.setAccount(account);

		Channel channel = new Channel();
		channel.setChannel("channel before");
		channel.setParameters("parameters before");
		channel.setRiver(river);

		ModifyChannelDTO modifyChannelTo = new ModifyChannelDTO();
		modifyChannelTo.setChannel("channel after");
		modifyChannelTo.setParameters("parameters after");

		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(account);
		when(mockChannelDao.findById(anyLong())).thenReturn(channel);
		riverService.setMapper(mapper);

		riverService.modifyChannel(1L, 1L, modifyChannelTo, "user");

		ArgumentCaptor<Channel> channelArgument = ArgumentCaptor
				.forClass(Channel.class);
		verify(mockChannelDao).update(channelArgument.capture());
		Channel updatedChannel = channelArgument.getValue();
		assertEquals(river, updatedChannel.getRiver());
		assertEquals("channel after", updatedChannel.getChannel());
		assertEquals("parameters after", updatedChannel.getParameters());

		ArgumentCaptor<ChannelUpdateNotification> notificationArgument = ArgumentCaptor
				.forClass(ChannelUpdateNotification.class);
		ArgumentCaptor<String> routingKeyArgument = ArgumentCaptor
				.forClass(String.class);
		verify(mockAmqpTemplate, times(2)).convertAndSend(
				routingKeyArgument.capture(), notificationArgument.capture());
		List<ChannelUpdateNotification> notifications = notificationArgument
				.getAllValues();
		assertEquals("channel before", notifications.get(0).getChannel());
		assertEquals("parameters before", notifications.get(0).getParameters());
		assertEquals(1L, notifications.get(0).getRiverId());
		assertEquals("channel after", notifications.get(1).getChannel());
		assertEquals("parameters after", notifications.get(1).getParameters());
		assertEquals(1L, notifications.get(0).getRiverId());

		List<String> routingKeys = routingKeyArgument.getAllValues();
		assertEquals("web.channel.channel before.delete", routingKeys.get(0));
		assertEquals("web.channel.channel after.add", routingKeys.get(1));
	}

	@Test
	public void modifyRiver() {
		Account mockAccount = mock(Account.class);
		River mockRiver = mock(River.class);
		ModifyRiverDTO modifyRiverTO = mock(ModifyRiverDTO.class);
		GetRiverDTO expectedGetRiverDTO = mock(GetRiverDTO.class);

		when(mockAccountDao.findByUsernameOrEmail(anyString()))
				.thenReturn(mockAccount);
		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockRiver.getAccount()).thenReturn(mockAccount);
		when(mockMapper.map(mockRiver, GetRiverDTO.class)).thenReturn(
				expectedGetRiverDTO);

		RiverService riverService = new RiverService();
		riverService.setAccountDao(mockAccountDao);
		riverService.setRiverDao(mockRiverDao);
		riverService.setMapper(mockMapper);

		GetRiverDTO actualGetRiverDTO = riverService.modifyRiver(1L,
				modifyRiverTO, "");

		verify(mockMapper).map(modifyRiverTO, mockRiver);
		verify(mockRiverDao).update(mockRiver);
		verify(mockMapper).map(mockRiver, GetRiverDTO.class);

		assertEquals(expectedGetRiverDTO, actualGetRiverDTO);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getCollaborators() {
		River mockRiver = mock(River.class);
		RiverCollaborator riverCollaborator = new RiverCollaborator();
		List<RiverCollaborator> collaborators = new ArrayList<RiverCollaborator>();
		collaborators.add(riverCollaborator);

		GetCollaboratorDTO mockCollaboratorDTO = mock(GetCollaboratorDTO.class);

		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockRiver.getCollaborators()).thenReturn(collaborators);
		when(mockMapper.map((Account) anyObject(), 
				any(Class.class))).thenReturn(mockCollaboratorDTO);

		List<GetCollaboratorDTO> actual = riverService.getCollaborators(1L);

		ArgumentCaptor<Account> accountArgument = ArgumentCaptor.forClass(Account.class);
		verify(mockMapper, times(1)).map(accountArgument.capture(), eq(GetCollaboratorDTO.class));
		assertEquals(1, actual.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void addCollaborator() {
		CreateCollaboratorDTO createCollaborator = new CreateCollaboratorDTO();
		createCollaborator.setReadOnly(true);
		createCollaborator.setAccount(new CreateCollaboratorDTO.Account());

		River mockRiver = mock(River.class);
		Account mockAuthAccount = mock(Account.class);
		Account mockAccount = mock(Account.class);
		
		RiverCollaborator mockCollaborator = mock(RiverCollaborator.class);
		GetCollaboratorDTO mockCollaboratorDTO = mock(GetCollaboratorDTO.class);
		
		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDao.findCollaborator(anyLong(), anyLong())).thenReturn(
				null);
		when(mockAccountDao.findById(anyLong())).thenReturn(mockAccount);
		when(mockRiverDao.addCollaborator((River) anyObject(), 
				(Account)anyObject(), anyBoolean())).thenReturn(mockCollaborator);
		when(mockMapper.map((Account) anyObject(), 
				any(Class.class))).thenReturn(mockCollaboratorDTO);

		riverService.addCollaborator(1L, createCollaborator, "admin");

		verify(mockRiverDao).addCollaborator(mockRiver, mockAccount, true);
		verify(mockAccountService).logActivity(eq(mockAuthAccount),
				eq(ActivityType.INVITE), any(RiverCollaborator.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void modifyCollaborator() {
		ModifyCollaboratorDTO to = new ModifyCollaboratorDTO();
		to.setActive(true);
		to.setReadOnly(false);

		RiverCollaborator collaborator = mock(RiverCollaborator.class);
		Account mockAuthAccount = mock(Account.class);
		River mockRiver = mock(River.class);

		GetCollaboratorDTO mockCollaboratorDTO = mock(GetCollaboratorDTO.class);

		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDao.findCollaborator(anyLong(), 
				anyLong())).thenReturn(collaborator);
		when(mockMapper.map((Account) anyObject(), 
				any(Class.class))).thenReturn(mockCollaboratorDTO);

		riverService.modifyCollaborator(1L, 5L, to, "admin");

		verify(mockRiverDao).findCollaborator(1L, 5L);
		verify(collaborator).setActive(true);
		verify(collaborator).setReadOnly(false);
		verify(mockRiverDao).updateCollaborator(collaborator);
	}

	@Test
	public void deleteCollaborator() {
		RiverCollaborator collaborator = mock(RiverCollaborator.class);
		Account mockAuthAccount = mock(Account.class);
		Account mockCollaboratorAccount = mock(Account.class);
		River mockRiver = mock(River.class);

		when(mockRiverCollaboratorDao.findById(anyLong())).thenReturn(
				collaborator);
		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDao.findCollaborator(anyLong(), 
				anyLong())).thenReturn(collaborator);
		when(collaborator.getAccount()).thenReturn(mockCollaboratorAccount);

		riverService.deleteCollaborator(1L, 4L, "admin");
		verify(mockRiverCollaboratorDao).delete(collaborator);
	}

	@Test
	public void addDropTag() {
		CreateTagDTO createTag = mock(CreateTagDTO.class);

		River mockRiver = mock(River.class);
		RiverDrop mockRiverDrop = mock(RiverDrop.class);
		Account mockAuthAccount = mock(Account.class);
		Tag mockTag = mock(Tag.class);

		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiverDao.findRiverDrop(anyLong(), anyLong())).thenReturn(mockRiverDrop);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDrop.getRiver()).thenReturn(mockRiver);
		when(mockTagDao.findByHash(anyString())).thenReturn(mockTag);

		riverService.addDropTag(1L, 3L, createTag, "user1");

		verify(mockRiverDao).findById(1L);
		verify(mockRiverDao).findRiverDrop(1L, 3L);
		verify(mockRiverDropDao).findTag(mockRiverDrop, mockTag);
		verify(mockRiverDropDao).addTag(mockRiverDrop, mockTag);
	}

	@Test
	public void deleteDropTag() {
		River mockRiver = mock(River.class);
		RiverDrop mockRiverDrop = mock(RiverDrop.class);
		Account mockAuthAccount = mock(Account.class);
		Tag mockTag = mock(Tag.class);

		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiverDao.findRiverDrop(anyLong(), anyLong())).thenReturn(mockRiverDrop);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDrop.getRiver()).thenReturn(mockRiver);
		when(mockTagDao.findById(anyLong())).thenReturn(mockTag);
		when(mockRiverDropDao.deleteTag(mockRiverDrop, mockTag)).thenReturn(
				true);

		riverService.deleteDropTag(2L, 3L, 100L, "user1");
		verify(mockTagDao).findById(100L);
		verify(mockRiverDao).findRiverDrop(2L, 3L);
		verify(mockRiverDropDao).deleteTag(mockRiverDrop, mockTag);

	}

	@Test
	public void addDropLink() {
		CreateLinkDTO dto = new CreateLinkDTO();
		dto.setUrl("http://www.nation.co.ke");

		River mockRiver = mock(River.class);
		RiverDrop mockRiverDrop = mock(RiverDrop.class);
		Account mockAuthAccount = mock(Account.class);
		Link mockLink = mock(Link.class);

		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiverDao.findRiverDrop(anyLong(), anyLong())).thenReturn(mockRiverDrop);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDrop.getRiver()).thenReturn(mockRiver);
		when(mockLinkDao.findByHash(anyString())).thenReturn(mockLink);

		riverService.addDropLink(1L, 3L, dto, "user3");
		verify(mockRiverDao).findRiverDrop(1L, 3L);
		verify(mockRiverDropDao).addLink(mockRiverDrop, mockLink);

	}

	@Test
	public void deleteDropLink() {
		River mockRiver = mock(River.class);
		RiverDrop mockRiverDrop = mock(RiverDrop.class);
		Account mockAuthAccount = mock(Account.class);
		Link mockLink = mock(Link.class);

		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiverDao.findRiverDrop(anyLong(), anyLong())).thenReturn(mockRiverDrop);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDrop.getRiver()).thenReturn(mockRiver);
		when(mockLinkDao.findById(anyLong())).thenReturn(mockLink);
		when(mockRiverDropDao.deleteLink(mockRiverDrop, mockLink)).thenReturn(
				true);

		riverService.deleteDropLink(1L, 22L, 55L, "admin");

		verify(mockRiverDao).findRiverDrop(1L, 22L);
		verify(mockLinkDao).findById(55L);
		verify(mockRiverDropDao).deleteLink(mockRiverDrop, mockLink);

	}

	@Test
	public void addDropPlace() {
		CreatePlaceDTO createPlace = new CreatePlaceDTO();

		River mockRiver = mock(River.class);
		RiverDrop mockRiverDrop = mock(RiverDrop.class);
		Account mockAuthAccount = mock(Account.class);
		Place mockPlace = mock(Place.class);

		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiverDao.findRiverDrop(anyLong(), anyLong())).thenReturn(mockRiverDrop);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDrop.getRiver()).thenReturn(mockRiver);
		when(mockPlaceDao.findByHash(anyString())).thenReturn(mockPlace);

		riverService.addDropPlace(2L, 33L, createPlace, "user1");
		verify(mockRiverDao).findRiverDrop(2L, 33L);
		verify(mockAccountDao).findByUsernameOrEmail("user1");
		verify(mockRiverDropDao).findPlace(mockRiverDrop, mockPlace);
		verify(mockRiverDropDao).addPlace(mockRiverDrop, mockPlace);

	}

	@Test
	public void deleteDropPlace() {
		River mockRiver = mock(River.class);
		RiverDrop mockRiverDrop = mock(RiverDrop.class);
		Account mockAuthAccount = mock(Account.class);
		Place mockPlace = mock(Place.class);

		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiverDao.findRiverDrop(anyLong(), anyLong())).thenReturn(mockRiverDrop);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDrop.getRiver()).thenReturn(mockRiver);
		when(mockPlaceDao.findById(anyLong())).thenReturn(mockPlace);
		when(mockRiverDropDao.deletePlace(mockRiverDrop, mockPlace))
				.thenReturn(true);

		riverService.deleteDropPlace(2L, 90L, 78L, "admin");
		verify(mockRiverDao).findRiverDrop(2L, 90L);
		verify(mockPlaceDao).findById(78L);
		verify(mockRiverDropDao).deletePlace(mockRiverDrop, mockPlace);
	}

	@Test
	public void filterVisible() {
		Account queryingAccount = new Account();
		queryingAccount.setCollaboratingRivers(new ArrayList<River>());
		River ownedPrivateRiver = new River();
		ownedPrivateRiver.setRiverPublic(false);
		ownedPrivateRiver.setAccount(queryingAccount);
		River unOwnedPrivateRiver = new River();
		unOwnedPrivateRiver.setRiverPublic(false);
		unOwnedPrivateRiver.setAccount(new Account());
		River publicRiver = new River();
		publicRiver.setRiverPublic(true);
		publicRiver.setAccount(new Account());

		List<River> rivers = new ArrayList<River>();
		rivers.add(ownedPrivateRiver);
		rivers.add(unOwnedPrivateRiver);
		rivers.add(publicRiver);

		List<River> filtered = riverService.filterVisible(rivers,
				queryingAccount);

		assertEquals(2, filtered.size());
		assertTrue(filtered.contains(ownedPrivateRiver));
		assertTrue(filtered.contains(publicRiver));
	}

	@Test
	public void addDropForm() {
		Account account = new Account();
		River river = new River();
		river.setId(1L);
		river.setAccount(account);
		RiverDrop drop = new RiverDrop();
		drop.setRiver(river);

		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(account);
		when(mockRiverDao.findById(anyLong())).thenReturn(river);
		when(mockRiverDao.findRiverDrop(anyLong(), anyLong())).thenReturn(drop);

		FormValueDTO dto = new FormValueDTO();
		dto.setId("1");
		dto.setValues(new ArrayList<FormValueDTO.FormFieldValue>());
		FormValueDTO.FormFieldValue value = new FormValueDTO.FormFieldValue();
		value.setId("2");
		value.setValue(Arrays.asList("Value 1", "Value 2"));
		dto.getValues().add(value);

		riverService.setMapper(mapper);

		riverService.addDropForm(1L, 1L, dto, "user");

		ArgumentCaptor<RiverDropForm> argument = ArgumentCaptor
				.forClass(RiverDropForm.class);
		verify(mockRiverDropFormDao).create(argument.capture());

		RiverDropForm dropForm = argument.getValue();
		assertNotNull(dropForm.getForm());
		assertEquals(1L, ((Number) dropForm.getForm().getId()).longValue());
		RiverDropFormField fieldValue = dropForm.getValues().get(0);
		assertEquals(2L, ((Number) fieldValue.getField().getId().longValue()));
		assertEquals("[\"Value 1\",\"Value 2\"]", fieldValue.getValue());
	}

	@Test
	public void modifyDropForm() {
		Account account = new Account();
		River river = new River();
		river.setId(1L);
		river.setAccount(account);
		RiverDrop drop = new RiverDrop();
		drop.setRiver(river);
		drop.setForms(new ArrayList<RiverDropForm>());
		RiverDropForm form = new RiverDropForm();
		form.setId(1L);
		drop.getForms().add(form);

		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(account);
		when(mockRiverDao.findById(anyLong())).thenReturn(river);
		when(mockRiverDropDao.findById(anyLong())).thenReturn(drop);
		when(mockRiverDropDao.findForm(anyLong(), anyLong())).thenReturn(form);

		ModifyFormValueDTO dto = new ModifyFormValueDTO();
		dto.setValues(new ArrayList<ModifyFormValueDTO.FormFieldValue>());
		ModifyFormValueDTO.FormFieldValue value = new ModifyFormValueDTO.FormFieldValue();
		value.setId("2");
		value.setValue(Arrays.asList("Value 3", "Value 4"));
		dto.getValues().add(value);

		riverService.setMapper(mapper);

		riverService.modifyDropForm(1L, 1L, 1L, dto, "user");

		ArgumentCaptor<RiverDropForm> argument = ArgumentCaptor
				.forClass(RiverDropForm.class);
		verify(mockRiverDropFormDao).update(argument.capture());

		RiverDropForm dropForm = argument.getValue();
		RiverDropFormField fieldValue = dropForm.getValues().get(0);
		assertEquals("[\"Value 3\",\"Value 4\"]", fieldValue.getValue());
	}

	@Test
	public void deleteDropForm() {
		Account account = new Account();
		River river = new River();
		river.setId(1L);
		river.setAccount(account);
		RiverDrop drop = new RiverDrop();
		drop.setRiver(river);
		drop.setForms(new ArrayList<RiverDropForm>());
		RiverDropForm form = new RiverDropForm();
		form.setId(1L);
		drop.getForms().add(form);

		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(account);
		when(mockRiverDao.findById(anyLong())).thenReturn(river);
		when(mockRiverDropDao.findById(anyLong())).thenReturn(drop);
		when(mockRiverDropDao.findForm(anyLong(), anyLong())).thenReturn(form);

		ModifyFormValueDTO dto = new ModifyFormValueDTO();
		dto.setValues(new ArrayList<ModifyFormValueDTO.FormFieldValue>());
		ModifyFormValueDTO.FormFieldValue value = new ModifyFormValueDTO.FormFieldValue();
		value.setId("2");
		value.setValue(Arrays.asList("Value 3", "Value 4"));
		dto.getValues().add(value);

		riverService.setMapper(mapper);

		riverService.deleteDropForm(1L, 1L, 1L, "user");

		ArgumentCaptor<RiverDropForm> argument = ArgumentCaptor
				.forClass(RiverDropForm.class);
		verify(mockRiverDropFormDao).delete(argument.capture());

		assertEquals(form, argument.getValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void markDropAsRead() {
		River mockRiver = mock(River.class);
		Account mockAccount = mock(Account.class);
		RiverDrop mockRiverDrop = mock(RiverDrop.class);
		List<RiverDrop> mockReadRiverDrops = (List<RiverDrop>) mock(List.class);

		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockAccountDao.findByUsernameOrEmail(anyString()))
				.thenReturn(mockAccount);
		when(mockRiver.getRiverPublic()).thenReturn(true);
		when(mockRiverDao.findRiverDrop(anyLong(), anyLong())).thenReturn(mockRiverDrop);
		when(mockRiverDrop.getRiver()).thenReturn(mockRiver);
		when(mockRiverDropDao.isRead(mockRiverDrop, mockAccount)).thenReturn(
				false);
		when(mockAccount.getReadRiverDrops()).thenReturn(mockReadRiverDrops);

		riverService.markDropAsRead(1L, 4L, "user1");

		verify(mockRiverDropDao).isRead(mockRiverDrop, mockAccount);
		verify(mockReadRiverDrops).add(mockRiverDrop);
		verify(mockAccountDao).update(mockAccount);
	}

	@Test
	public void addFollower() {
		River river = new River();
		river.setFollowers(new ArrayList<Account>());
		when(mockRiverDao.findById(anyLong())).thenReturn(river);

		Account account = new Account();
		when(mockAccountDao.findById(anyLong())).thenReturn(account);

		riverService.addFollower(1L, 1L);

		ArgumentCaptor<River> argument = ArgumentCaptor.forClass(River.class);
		verify(mockRiverDao).update(argument.capture());

		assertTrue(argument.getValue().getFollowers().contains(account));

		verify(mockAccountService).logActivity(account, ActivityType.FOLLOW,
				river);
	}
}
