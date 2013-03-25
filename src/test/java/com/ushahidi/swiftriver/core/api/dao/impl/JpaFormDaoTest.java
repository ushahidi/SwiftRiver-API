package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Form;
import com.ushahidi.swiftriver.core.model.FormField;
import com.ushahidi.swiftriver.core.support.AbstractIntegrationTest;

public class JpaFormDaoTest extends AbstractIntegrationTest {

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
		field.setDescription("test description");
		field.setType("test type");
		field.setRequired(true);
		field.setOptions("test options");
		field.setForm(form);
		form.setFields(new ArrayList<FormField>());
		form.getFields().add(field);

		formDao.create(form);

		String sql = "SELECT account_id, name FROM forms WHERE id = ?";

		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql,
				form.getId());
		assertEquals(1L, ((Number)results.get("account_id")).longValue());
		assertEquals("Test Form", results.get("name"));

		sql = "SELECT form_id, title, description, type, required, options FROM form_fields WHERE id = ?";

		results = this.jdbcTemplate.queryForMap(sql, field.getId());
		
		assertEquals("test title", results.get("title"));
		assertEquals("test description", results.get("description"));
		assertEquals("test type", results.get("type"));
		assertEquals(true, results.get("required"));
		assertEquals("test options", results.get("options"));
	}
}
