package com.ushahidi.swiftriver.core.api.dao.impl;

import java.util.ArrayList;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Form;
import com.ushahidi.swiftriver.core.model.FormField;

public class JpaFormDaoTest extends AbstractDaoTest {
	
	@Autowired
	JpaFormDao formDao;

	@Test
	public void testSave() {
		Account account = new Account();
		account.setId(1L);
		Form form = new Form();
		form.setName("Test Form");
		form.setAccount(account);
		FormField field = new FormField();
		field.setTitle("test title");
		field.setType("test type");
		field.setForm(form);
		form.setFields(new ArrayList<FormField>());
		form.getFields().add(field);
		
		formDao.create(form);
	}
}
