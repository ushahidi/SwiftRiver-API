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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;
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
import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.api.dto.ChannelUpdateNotification;
import com.ushahidi.swiftriver.core.api.dto.CreateChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.CreatePlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateRiverDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateTagDTO;
import com.ushahidi.swiftriver.core.api.dto.GetChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.GetRiverDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyRiverDTO;
import com.ushahidi.swiftriver.core.api.exception.ForbiddenException;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Channel;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverCollaborator;
import com.ushahidi.swiftriver.core.model.RiverDrop;
import com.ushahidi.swiftriver.core.model.Tag;

public class RiverServiceTest {

	private RiverDao mockRiverDao;

	private AccountDao mockAccountDao;

	private ChannelDao mockChannelDao;

	private RiverCollaboratorDao mockRiverCollaboratorDao;

	private Mapper mockMapper;

	private Mapper mapper;

	private RiverService riverService;

	private RiverDropDao mockRiverDropDao;

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
		mockTagDao = mock(TagDao.class);
		mockLinkDao = mock(LinkDao.class);
		mockPlaceDao = mock(PlaceDao.class);
		mockMapper = mock(Mapper.class);
		mapper = new DozerBeanMapper();
		mockAmqpTemplate = mock(AmqpTemplate.class);

		riverService = new RiverService();
		riverService.setRiverDao(mockRiverDao);
		riverService.setAccountDao(mockAccountDao);
		riverService.setChannelDao(mockChannelDao);
		riverService.setRiverCollaboratorDao(mockRiverCollaboratorDao);
		riverService.setRiverDropDao(mockRiverDropDao);
		riverService.setTagDao(mockTagDao);
		riverService.setLinkDao(mockLinkDao);
		riverService.setPlaceDao(mockPlaceDao);
		riverService.setMapper(mockMapper);
		riverService.setAmqpTemplate(mockAmqpTemplate);
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

		when(mockAccountDao.findByUsername(anyString()))
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
	public void isOwnerForCollaboratingAccount() {
		River river = new River();

		Account account = new Account();
		List<River> collaboratingRivers = new ArrayList<River>();
		collaboratingRivers.add(river);
		account.setCollaboratingRivers(collaboratingRivers);

		assertTrue(riverService.isOwner(river, account));
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

		when(mockAccountDao.findByUsername(anyString())).thenReturn(account);
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
		Channel channel = new Channel();
		channel.setChannel("the channel");
		channel.setParameters("the parameters");
		channel.setRiver(river);

		when(mockAccountDao.findByUsername(anyString())).thenReturn(account);
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
		assertEquals("web.channel.the channel.delete", routingKeyArgument.getValue());

		verify(mockChannelDao).delete(channel);
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

		when(mockAccountDao.findByUsername(anyString())).thenReturn(account);
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

		when(mockAccountDao.findByUsername(anyString()))
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

	@Test
	public void getCollaborators() {
		River mockRiver = mock(River.class);
		RiverCollaborator riverCollaborator = new RiverCollaborator();
		List<RiverCollaborator> collaborators = new ArrayList<RiverCollaborator>();
		collaborators.add(riverCollaborator);

		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockRiver.getCollaborators()).thenReturn(collaborators);

		List<GetCollaboratorDTO> actual = riverService.getCollaborators(1L);

		verify(mockMapper).map(riverCollaborator, GetCollaboratorDTO.class);
		assertEquals(1, actual.size());
	}

	@Test
	public void addCollaborator() {
		CreateCollaboratorDTO createCollaborator = new CreateCollaboratorDTO();
		createCollaborator.setReadOnly(true);
		createCollaborator.setAccount(new CreateCollaboratorDTO.Account());

		River mockRiver = mock(River.class);
		Account mockAuthAccount = mock(Account.class);
		Account mockAccount = mock(Account.class);

		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDao.findCollaborator(anyLong(), anyLong())).thenReturn(
				null);
		when(mockAccountDao.findById(anyLong())).thenReturn(mockAccount);

		riverService.addCollaborator(1L, createCollaborator, "admin");

		verify(mockRiverDao).addCollaborator(mockRiver, mockAccount, true);
	}

	@Test
	public void modifyCollaborator() {
		ModifyCollaboratorDTO to = new ModifyCollaboratorDTO();
		to.setActive(true);
		to.setReadOnly(false);

		RiverCollaborator collaborator = mock(RiverCollaborator.class);
		Account mockAuthAccount = mock(Account.class);
		River mockRiver = mock(River.class);

		when(mockRiverCollaboratorDao.findById(anyLong())).thenReturn(
				collaborator);
		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);

		riverService.modifyCollaborator(1L, 2L, to, "admin");

		verify(collaborator).setActive(true);
		verify(collaborator).setReadOnly(false);
		verify(mockRiverDao).updateCollaborator(collaborator);
	}

	@Test
	public void deleteCollaborator() {
		RiverCollaborator collaborator = mock(RiverCollaborator.class);
		Account mockAuthAccount = mock(Account.class);
		River mockRiver = mock(River.class);

		when(mockRiverCollaboratorDao.findById(anyLong())).thenReturn(
				collaborator);
		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);

		riverService.deleteCollaborator(1L, 2L, "admin");
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
		when(mockAccountDao.findByUsername(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiverDropDao.findById(anyLong())).thenReturn(mockRiverDrop);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDrop.getRiver()).thenReturn(mockRiver);
		when(mockTagDao.findByHash(anyString())).thenReturn(mockTag);

		riverService.addDropTag(1L, 3L, createTag, "user1");

		verify(mockRiverDao).findById(1L);
		verify(mockRiverDropDao).findById(3L);
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
		when(mockAccountDao.findByUsername(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiverDropDao.findById(anyLong())).thenReturn(mockRiverDrop);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDrop.getRiver()).thenReturn(mockRiver);
		when(mockTagDao.findById(anyLong())).thenReturn(mockTag);
		when(mockRiverDropDao.deleteTag(mockRiverDrop, mockTag)).thenReturn(
				true);

		riverService.deleteDropTag(2L, 3L, 100L, "user1");
		verify(mockTagDao).findById(100L);
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
		when(mockAccountDao.findByUsername(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiverDropDao.findById(anyLong())).thenReturn(mockRiverDrop);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDrop.getRiver()).thenReturn(mockRiver);
		when(mockLinkDao.findByHash(anyString())).thenReturn(mockLink);

		riverService.addDropLink(1L, 3L, dto, "user3");
		verify(mockRiverDropDao).findById(3L);
		verify(mockRiverDropDao).addLink(mockRiverDrop, mockLink);

	}

	@Test
	public void deleteDropLink() {
		River mockRiver = mock(River.class);
		RiverDrop mockRiverDrop = mock(RiverDrop.class);
		Account mockAuthAccount = mock(Account.class);
		Link mockLink = mock(Link.class);

		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiverDropDao.findById(anyLong())).thenReturn(mockRiverDrop);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDrop.getRiver()).thenReturn(mockRiver);
		when(mockLinkDao.findById(anyLong())).thenReturn(mockLink);
		when(mockRiverDropDao.deleteLink(mockRiverDrop, mockLink)).thenReturn(
				true);

		riverService.deleteDropLink(1L, 22L, 55L, "admin");

		verify(mockRiverDropDao).findById(22L);
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
		when(mockAccountDao.findByUsername(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiverDropDao.findById(anyLong())).thenReturn(mockRiverDrop);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDrop.getRiver()).thenReturn(mockRiver);
		when(mockPlaceDao.findByHash(anyString())).thenReturn(mockPlace);

		riverService.addDropPlace(2L, 33L, createPlace, "user1");
		verify(mockRiverDropDao).findById(33L);
		verify(mockAccountDao).findByUsername("user1");
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
		when(mockAccountDao.findByUsername(anyString())).thenReturn(
				mockAuthAccount);
		when(mockRiverDropDao.findById(anyLong())).thenReturn(mockRiverDrop);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDrop.getRiver()).thenReturn(mockRiver);
		when(mockPlaceDao.findById(anyLong())).thenReturn(mockPlace);
		when(mockRiverDropDao.deletePlace(mockRiverDrop, mockPlace))
				.thenReturn(true);

		riverService.deleteDropPlace(2L, 90L, 78L, "admin");
		verify(mockRiverDropDao).findById(90L);
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
	
	@SuppressWarnings("unchecked")
	@Test
	public void markDropAsRead() {
		River mockRiver = mock(River.class);
		Account mockAccount = mock(Account.class);
		RiverDrop mockRiverDrop = mock(RiverDrop.class);
		List<RiverDrop> mockReadRiverDrops = (List<RiverDrop>) mock(List.class); 

		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(mockAccount);
		when(mockRiver.getRiverPublic()).thenReturn(true);
		when(mockRiverDropDao.findById(anyLong())).thenReturn(mockRiverDrop);
		when(mockRiverDrop.getRiver()).thenReturn(mockRiver);
		when(mockRiverDropDao.isRead(mockRiverDrop, mockAccount)).thenReturn(false);
		when(mockAccount.getReadRiverDrops()).thenReturn(mockReadRiverDrops);
		
		riverService.markDropAsRead(1L, 4L, "user1");
	
		verify(mockRiverDropDao).isRead(mockRiverDrop, mockAccount);
		verify(mockReadRiverDrops).add(mockRiverDrop);
		verify(mockAccountDao).update(mockAccount);
	}
}
