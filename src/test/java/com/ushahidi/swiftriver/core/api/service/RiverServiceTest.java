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
import org.junit.Before;
import org.junit.Test;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.ChannelDao;
import com.ushahidi.swiftriver.core.api.dao.RiverCollaboratorDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.api.dto.CreateChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateRiverDTO;
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
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverCollaborator;

public class RiverServiceTest {
	
	private RiverDao mockRiverDao;
	
	private AccountDao mockAccountDao;
	
	private ChannelDao mockChannelDao;
	
	private RiverCollaboratorDao mockRiverCollaboratorDao;
	
	private Mapper mockMapper;
	
	private RiverService riverService;
	
	@Before
	public void setup() {
		mockRiverDao = mock(RiverDao.class);
		mockAccountDao = mock(AccountDao.class);
		mockChannelDao = mock(ChannelDao.class);
		mockRiverCollaboratorDao = mock(RiverCollaboratorDao.class);
		mockMapper = mock(Mapper.class);
		
		riverService = new RiverService();
		riverService.setRiverDao(mockRiverDao);
		riverService.setAccountDao(mockAccountDao);
		riverService.setChannelDao(mockChannelDao);
		riverService.setRiverCollaboratorDao(mockRiverCollaboratorDao);
		riverService.setMapper(mockMapper);
	}


	@Test
	public void findById() throws NotFoundException {
		River river = new River();

		when(mockRiverDao.findById(anyLong())).thenReturn(river);

		GetRiverDTO getRiverDTO = mock(GetRiverDTO.class);
		when(mockMapper.map(river, GetRiverDTO.class))
				.thenReturn(getRiverDTO);

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

		when(mockAccountDao.findByUsername(anyString())).thenReturn(
				mockAccount);
		when(mockAccount.getRiverQuotaRemaining()).thenReturn(1);
		when(mockRiverDao.findByName(anyString())).thenReturn(null);
		when(mockMapper.map(mockRiverTO, River.class)).thenReturn(
				mockRiver);
		when(mockRiver.getRiverName()).thenReturn("");
		when(mockMapper.map(mockRiver, GetRiverDTO.class)).thenReturn(
				mockGetRiverTO);

		GetRiverDTO actualGetRiverTO = riverService.createRiver(mockRiverTO, "username");

		verify(mockRiverDao).create(mockRiver);
		verify(mockAccountDao).decreaseRiverQuota(mockAccount, 1);
		verify(mockMapper).map(mockRiver, GetRiverDTO.class);

		assertEquals(mockGetRiverTO, actualGetRiverTO);

	}

	@Test
	public void createChannel() {
		CreateChannelDTO mockedCreateChannelTO = mock(CreateChannelDTO.class);
		River mockRiver = mock(River.class);
		Channel mockChannel = mock(Channel.class);
		GetChannelDTO mockGetChannelTO = mock(GetChannelDTO.class);

		when(mockRiverDao.findById(1L)).thenReturn(mockRiver);
		when(mockMapper.map(mockedCreateChannelTO, Channel.class))
				.thenReturn(mockChannel);
		when(mockMapper.map(mockChannel, GetChannelDTO.class)).thenReturn(
				mockGetChannelTO);

		GetChannelDTO actualChannelTO = riverService.createChannel(1L,
				mockedCreateChannelTO);

		verify(mockRiverDao).findById(1L);
		verify(mockChannel).setRiver(mockRiver);
		verify(mockChannelDao).create(mockChannel);

		assertEquals(mockGetChannelTO, actualChannelTO);
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
		
		
		riverService.deleteChannel(1L, 1L, "user");
		
		verify(mockChannelDao).delete(mockChannel);
	}
	
	@Test
	public void modifyChannel() {
		Channel mockChannel = mock(Channel.class);
		ModifyChannelDTO modifyChannelTo = mock(ModifyChannelDTO.class);
		GetChannelDTO mockGetChannelTO = mock(GetChannelDTO.class);
		Account account = new Account();
		account.setAccountPath("other-account");
		account.setCollaboratingRivers(new ArrayList<River>());
		River river = new River();
		river.setId(1L);
		river.setAccount(account);
		
		when(mockAccountDao.findByUsername(anyString())).thenReturn(account);
		when(mockChannelDao.findById(anyLong())).thenReturn(mockChannel);
		when(mockChannel.getRiver()).thenReturn(river);
		when(mockMapper.map(mockChannel, GetChannelDTO.class)).thenReturn(mockGetChannelTO);
		
		
		GetChannelDTO actualGetChannelDTO = riverService.modifyChannel(1L, 1L, modifyChannelTo, "user");
		
		verify(mockChannelDao).update(mockChannel);
		assertEquals(mockGetChannelTO, actualGetChannelDTO);
	}
	
	@Test
	public void modifyRiver() {
		Account mockAccount = mock(Account.class);
		River mockRiver = mock(River.class);
		ModifyRiverDTO modifyRiverTO = mock(ModifyRiverDTO.class);
		GetRiverDTO expectedGetRiverDTO = mock(GetRiverDTO.class);
		
		when(mockAccountDao.findByUsername(anyString())).thenReturn(mockAccount);
		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockRiver.getAccount()).thenReturn(mockAccount);
		when(mockMapper.map(mockRiver, GetRiverDTO.class)).thenReturn(expectedGetRiverDTO);
		
		RiverService riverService = new RiverService();
		riverService.setAccountDao(mockAccountDao);
		riverService.setRiverDao(mockRiverDao);
		riverService.setMapper(mockMapper);
		
		GetRiverDTO actualGetRiverDTO = riverService.modifyRiver(1L, modifyRiverTO, "");
		
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
		when(mockAccountDao.findByUsername(anyString())).thenReturn(mockAuthAccount);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDao.findCollaborator(anyLong(), anyLong())).thenReturn(null);
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
		
		when(mockRiverCollaboratorDao.findById(anyLong())).thenReturn(collaborator);
		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(mockAuthAccount);
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
		
		when(mockRiverCollaboratorDao.findById(anyLong())).thenReturn(collaborator);
		when(mockRiverDao.findById(anyLong())).thenReturn(mockRiver);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(mockAuthAccount);
		when(mockRiver.getAccount()).thenReturn(mockAuthAccount);
		
		riverService.deleteCollaborator(1L, 2L, "admin");
		verify(mockRiverCollaboratorDao).delete(collaborator);
	}
}
