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

import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.FormDao;
import com.ushahidi.swiftriver.core.api.dao.FormFieldDao;
import com.ushahidi.swiftriver.core.api.dto.CreateFormDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateFormFieldDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyFormDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyFormFieldDTO;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.ActivityType;
import com.ushahidi.swiftriver.core.model.Form;
import com.ushahidi.swiftriver.core.model.FormField;
import com.ushahidi.swiftriver.core.support.MapperFactory;

public class FormServiceTest  {

	private static final Mapper mapper = MapperFactory.getMapper();

	private AccountDao mockAccountDao;

	private FormDao mockFormDao;
	
	private FormFieldDao mockFormFieldDao;

	private FormService formService;
	
	private AccountService mockAccountService;
	
	private Form form;
	
	private FormField field;
	
	private Account account;

	@Before
	public void setup() {
		mockAccountDao = mock(AccountDao.class);
		mockFormDao = mock(FormDao.class);
		mockFormFieldDao = mock(FormFieldDao.class);
		mockAccountService = mock(AccountService.class);
		
		account = new Account();
		account.setId(1L);
		form = new Form();
		form.setAccount(account);
		form.setFields(new ArrayList<FormField>());
		field = new FormField();
		field.setForm(form);
		form.getFields().add(field);
		
		when(mockAccountDao.findByUsernameOrEmail("user")).thenReturn(account);
		when(mockFormDao.findById(1L)).thenReturn(form);
		when(mockFormFieldDao.findById(1L)).thenReturn(field);

		formService = new FormService();
		formService.setAccountDao(mockAccountDao);
		formService.setMapper(mapper);
		formService.setFormDao(mockFormDao);
		formService.setFormFieldDao(mockFormFieldDao);
		formService.setAccountService(mockAccountService);
	}

	@Test
	public void createForm() {
		CreateFormDTO createFormDTO = new CreateFormDTO();
		createFormDTO.setName("Dangerous Speech Categorisation");

		formService.createForm(createFormDTO, "user");

		ArgumentCaptor<Form> argument = ArgumentCaptor.forClass(Form.class);
		verify(mockFormDao).create(argument.capture());

		Form form = argument.getValue();
		assertEquals("Dangerous Speech Categorisation", form.getName());
		
		verify(mockAccountService).logActivity(account, ActivityType.CREATE, form);
	}
	
	@Test
	public void modifyForm() {
		ModifyFormDTO dto = new ModifyFormDTO();
		dto.setName("new name");
		
		formService.modifyForm(1L, dto, "user");
		
		ArgumentCaptor<Form> argument = ArgumentCaptor.forClass(Form.class);
		verify(mockFormDao).update(argument.capture());
		
		assertEquals("new name", argument.getValue().getName());
	}
	
	@Test
	public void deleteForm() {
		formService.deleteForm(1L, "user");
		
		ArgumentCaptor<Form> argument = ArgumentCaptor.forClass(Form.class);
		verify(mockFormDao).delete(argument.capture());
		
		assertEquals(form, argument.getValue());
	}
	
	@Test
	public void createField() {
		CreateFormFieldDTO dto = new CreateFormFieldDTO();
		dto.setTitle("field title");
		dto.setType("multiple");
		dto.setRequired(true);
		dto.setDescription("field description");
		dto.setOptions(new ArrayList<String>());
		dto.getOptions().add("Test Option");
		
		formService.createField(1L, dto, "user");

		ArgumentCaptor<FormField> argument = ArgumentCaptor.forClass(FormField.class);
		verify(mockFormFieldDao).create(argument.capture());

		FormField field = argument.getValue();
		assertEquals("field title", field.getTitle());
		assertEquals("multiple", field.getType());
		assertEquals(true, field.getRequired());
		assertEquals("field description", field.getDescription());
		assertEquals("[\"Test Option\"]", field.getOptions());
	}
	
	@Test
	public void modifyFormField() {
		ModifyFormFieldDTO dto = new ModifyFormFieldDTO();
		dto.setTitle("New Title");
		dto.setType("multiple");
		dto.setRequired(false);
		dto.setDescription("New Description");
		dto.setOptions(new ArrayList<String>());
		dto.getOptions().add("New Option");
		
		formService.modifyField(1L, 1L, dto, "user");
		
		ArgumentCaptor<FormField> argument = ArgumentCaptor.forClass(FormField.class);
		verify(mockFormFieldDao).update(argument.capture());
		
		FormField field = argument.getValue();
		assertEquals("New Title", field.getTitle());
		assertEquals("multiple", field.getType());
		assertEquals(false, field.getRequired());
		assertEquals("New Description", field.getDescription());
		assertEquals("[\"New Option\"]", field.getOptions());
	}
	
	@Test
	public void deleteFormField() {
		formService.deleteField(1L, 1L, "user");
		
		ArgumentCaptor<FormField> argument = ArgumentCaptor.forClass(FormField.class);
		verify(mockFormFieldDao).delete(argument.capture());
		
		assertEquals(field, argument.getValue());
	}
}
