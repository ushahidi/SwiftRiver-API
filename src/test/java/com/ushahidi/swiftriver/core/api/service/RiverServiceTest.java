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

import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.User;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDao;
import com.ushahidi.swiftriver.core.api.dto.CreateRiverDTO;
import com.ushahidi.swiftriver.core.api.dto.GetRiverDTO;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.River;

public class RiverServiceTest {
	
	private River river;
	
	private GetRiverDTO getRiverDTO;
	
	private RiverService riverService;

	private RiverDao mockedRiverDao;
	
	private Mapper mockedMapper;

	@Before
	public void setup() {
		river = new River();
		
		mockedRiverDao = mock(RiverDao.class);
		
		when(mockedRiverDao.findById(anyInt())).thenReturn(river);
		
		mockedMapper = mock(Mapper.class);
		when(mockedMapper.map(river, GetRiverDTO.class)).thenReturn(getRiverDTO);
		
		riverService = new RiverService();
		riverService.setRiverDao(mockedRiverDao);
		riverService.setMapper(mockedMapper);
	}
	
	@Test
	public void findById() throws NotFoundException {
		GetRiverDTO actualGetRiverDTO = riverService.getRiverById(22L);

		verify(mockedRiverDao).findById(22);
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
		when(mockedAccountDao.findByUsername(anyString())).thenReturn(mockedAccount);
		when(mockedAccount.getRiverQuotaRemaining()).thenReturn(1);
		when(mockedRiverDao.findByName(anyString())).thenReturn(null);
		when(mockedMapper.map(mockedRiverTO, River.class)).thenReturn(mockedRiver);
		when(mockedRiver.getRiverName()).thenReturn("");
		when(mockedMapper.map(mockedRiver, GetRiverDTO.class)).thenReturn(mockedGetRiverTO);
		
		RiverService riverService = new RiverService();
		riverService.setAccountDao(mockedAccountDao);
		riverService.setRiverDao(mockedRiverDao);
		riverService.setMapper(mockedMapper);
		
		GetRiverDTO actualGetRiverTO = riverService.createRiver(mockedUser, mockedRiverTO);
		
		verify(mockedRiverDao).save(mockedRiver);
		verify(mockedAccountDao).decreaseRiverQuota(mockedAccount, 1);
		verify(mockedMapper).map(mockedRiver, GetRiverDTO.class);
		
		assertEquals(mockedGetRiverTO, actualGetRiverTO);
		
	}
}
