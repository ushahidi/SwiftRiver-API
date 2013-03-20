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

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.FormDao;
import com.ushahidi.swiftriver.core.api.dto.CreateFormDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyFormDTO;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Form;

public class FormServiceTest {

	private Mapper mapper = new DozerBeanMapper();;

	private AccountDao mockAccountDao;

	private FormDao mockFormDao;

	private FormService formService;

	@Before
	public void setup() {
		mockAccountDao = mock(AccountDao.class);
		mockFormDao = mock(FormDao.class);

		formService = new FormService();
		formService.setAccountDao(mockAccountDao);
		formService.setMapper(mapper);
		formService.setFormDao(mockFormDao);
	}

	@Test
	public void createForm() {
		CreateFormDTO createFormDTO = new CreateFormDTO();
		createFormDTO.setName("Dangerous Speech Categorisation");

		formService.createForm(createFormDTO, "");

		ArgumentCaptor<Form> argument = ArgumentCaptor.forClass(Form.class);
		verify(mockFormDao).create(argument.capture());

		assertEquals("Dangerous Speech Categorisation", argument.getValue()
				.getName());
	}
	
	@Test
	public void modifyForm() {
		Account account = new Account();
		account.setId(1L);
		Form form = new Form();
		form.setAccount(account);
		
		ModifyFormDTO dto = new ModifyFormDTO();
		dto.setName("new name");
		
		when(mockAccountDao.findByUsername("user")).thenReturn(account);
		when(mockFormDao.findById(1L)).thenReturn(form);
		formService.modifyForm(1L, dto, "user");
		
		ArgumentCaptor<Form> argument = ArgumentCaptor.forClass(Form.class);
		verify(mockFormDao).update(argument.capture());
		
		assertEquals("new name", argument.getValue().getName());
	}
	
	@Test
	public void deleteForm() {
		Account account = new Account();
		account.setId(1L);
		Form form = new Form();
		form.setAccount(account);
		
		when(mockAccountDao.findByUsername("user")).thenReturn(account);
		when(mockFormDao.findById(1L)).thenReturn(form);
		formService.deleteForm(1L, "user");
		
		ArgumentCaptor<Form> argument = ArgumentCaptor.forClass(Form.class);
		verify(mockFormDao).delete(argument.capture());
		
		assertEquals(form, argument.getValue());
	}
}
