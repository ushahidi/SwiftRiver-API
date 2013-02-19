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
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.dozer.Mapper;
import org.junit.Test;
import org.springframework.security.core.userdetails.User;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.ChannelDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.api.dto.CreateChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateRiverDTO;
import com.ushahidi.swiftriver.core.api.dto.GetChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.GetRiverDTO;
import com.ushahidi.swiftriver.core.api.exception.ForbiddenException;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Channel;
import com.ushahidi.swiftriver.core.model.River;

public class RiverServiceTest {


	@Test
	public void findById() throws NotFoundException {
		River river = new River();

		RiverDao mockedRiverDao = mock(RiverDao.class);

		when(mockedRiverDao.findById(anyLong())).thenReturn(river);

		Mapper mockedMapper = mock(Mapper.class);
		GetRiverDTO getRiverDTO = mock(GetRiverDTO.class);
		when(mockedMapper.map(river, GetRiverDTO.class))
				.thenReturn(getRiverDTO);

		RiverService riverService = new RiverService();
		riverService.setRiverDao(mockedRiverDao);
		riverService.setMapper(mockedMapper);

		GetRiverDTO actualGetRiverDTO = riverService.getRiverById(22L);

		verify(mockedRiverDao).findById(22L);
		assertEquals(getRiverDTO, actualGetRiverDTO);
	}

	@Test
	public void createRiver() {
		CreateRiverDTO mockedRiverTO = mock(CreateRiverDTO.class);
		AccountDao mockedAccountDao = mock(AccountDao.class);
		RiverDao mockedRiverDao = mock(RiverDao.class);
		Account mockedAccount = mock(Account.class);
		River mockedRiver = mock(River.class);
		User mockedUser = mock(User.class);
		Mapper mockedMapper = mock(Mapper.class);
		GetRiverDTO mockedGetRiverTO = mock(GetRiverDTO.class);

		when(mockedUser.getUsername()).thenReturn("");
		when(mockedAccountDao.findByUsername(anyString())).thenReturn(
				mockedAccount);
		when(mockedAccount.getRiverQuotaRemaining()).thenReturn(1);
		when(mockedRiverDao.findByName(anyString())).thenReturn(null);
		when(mockedMapper.map(mockedRiverTO, River.class)).thenReturn(
				mockedRiver);
		when(mockedRiver.getRiverName()).thenReturn("");
		when(mockedMapper.map(mockedRiver, GetRiverDTO.class)).thenReturn(
				mockedGetRiverTO);

		RiverService riverService = new RiverService();
		riverService.setAccountDao(mockedAccountDao);
		riverService.setRiverDao(mockedRiverDao);
		riverService.setMapper(mockedMapper);

		GetRiverDTO actualGetRiverTO = riverService.createRiver(mockedUser,
				mockedRiverTO);

		verify(mockedRiverDao).create(mockedRiver);
		verify(mockedAccountDao).decreaseRiverQuota(mockedAccount, 1);
		verify(mockedMapper).map(mockedRiver, GetRiverDTO.class);

		assertEquals(mockedGetRiverTO, actualGetRiverTO);

	}

	@Test
	public void createChannel() {
		CreateChannelDTO mockedCreateChannelTO = mock(CreateChannelDTO.class);
		RiverDao mockedRiverDao = mock(RiverDao.class);
		ChannelDao mockedChannelDao = mock(ChannelDao.class);
		River mockedRiver = mock(River.class);
		Channel mockedChannel = mock(Channel.class);
		Mapper mockedMapper = mock(Mapper.class);
		GetChannelDTO mockedGetChannelTO = mock(GetChannelDTO.class);

		when(mockedRiverDao.findById(1L)).thenReturn(mockedRiver);
		when(mockedMapper.map(mockedCreateChannelTO, Channel.class))
				.thenReturn(mockedChannel);
		when(mockedMapper.map(mockedChannel, GetChannelDTO.class)).thenReturn(
				mockedGetChannelTO);

		RiverService riverService = new RiverService();
		riverService.setMapper(mockedMapper);
		riverService.setRiverDao(mockedRiverDao);
		riverService.setChannelDao(mockedChannelDao);

		GetChannelDTO actualChannelTO = riverService.createChannel(1L,
				mockedCreateChannelTO);

		verify(mockedRiverDao).findById(1L);
		verify(mockedChannel).setRiver(mockedRiver);
		verify(mockedChannelDao).create(mockedChannel);

		assertEquals(mockedGetChannelTO, actualChannelTO);
	}

	@Test
	public void isOwnerForOwnerAccount() {
		Account account = new Account();
		account.setAccountPath("owner_account");

		River river = new River();
		river.setAccount(account);

		RiverService riverService = new RiverService();
		
		assertTrue(riverService.isOwner(river, account));
	}

	@Test
	public void isOwnerForCollaboratingAccount() {
		River river = new River();

		Account account = new Account();
		List<River> collaboratingRivers = new ArrayList<River>();
		collaboratingRivers.add(river);
		account.setCollaboratingRivers(collaboratingRivers);
		
		RiverService riverService = new RiverService();

		assertTrue(riverService.isOwner(river, account));
	}

	@Test
	public void isOwnerForNoneOwnerAccount() {
		River river = new River();
		river.setAccount(new Account());

		Account account = new Account();
		List<River> collaboratingRivers = new ArrayList<River>();
		account.setCollaboratingRivers(collaboratingRivers);
		
		RiverService riverService = new RiverService();

		assertFalse(riverService.isOwner(river, account));
	}

	@Test(expected = NotFoundException.class)
	public void deleteNonExistentChannel() {
		
		ChannelDao mockChannelDao = mock(ChannelDao.class);
		when(mockChannelDao.findById(anyLong())).thenReturn(null);
		
		RiverService riverService = new RiverService();
		riverService.setChannelDao(mockChannelDao);
		
		riverService.deleteChannel(1L, 1L, null);
	}
	
	@Test(expected = NotFoundException.class)
	public void deleteNonExistentChannelInRiver() {
		ChannelDao mockChannelDao = mock(ChannelDao.class);
		Channel mockChannel = mock(Channel.class);
		River mockRiver = mock(River.class);
		when(mockChannelDao.findById(anyLong())).thenReturn(mockChannel);
		when(mockChannel.getRiver()).thenReturn(mockRiver);
		
		RiverService riverService = new RiverService();
		riverService.setChannelDao(mockChannelDao);
		
		riverService.deleteChannel(1L, 1L, null);
	}
	
	@Test(expected = ForbiddenException.class)
	public void deleteOtherUserChannel() {
		ChannelDao mockChannelDao = mock(ChannelDao.class);
		AccountDao mockAccountDao = mock(AccountDao.class);
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
		
		
		RiverService riverService = new RiverService();
		riverService.setChannelDao(mockChannelDao);
		riverService.setAccountDao(mockAccountDao);
		
		riverService.deleteChannel(1L, 1L, "user");
	}
	
	@Test
	public void deleteChannel() {
		ChannelDao mockChannelDao = mock(ChannelDao.class);
		AccountDao mockAccountDao = mock(AccountDao.class);
		Channel mockChannel = mock(Channel.class);
		Account account = new Account();
		account.setAccountPath("other-account");
		account.setCollaboratingRivers(new ArrayList<River>());
		River river = new River();
		river.setId(1L);
		river.setAccount(account);
		
		when(mockAccountDao.findByUsername(anyString())).thenReturn(account);
		when(mockChannelDao.findById(anyLong())).thenReturn(mockChannel);
		when(mockChannel.getRiver()).thenReturn(river);
		
		
		RiverService riverService = new RiverService();
		riverService.setChannelDao(mockChannelDao);
		riverService.setAccountDao(mockAccountDao);
		
		riverService.deleteChannel(1L, 1L, "user");
		
		verify(mockChannelDao).delete(mockChannel);
	}
}
