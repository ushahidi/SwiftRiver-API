/**
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.FormDao;
import com.ushahidi.swiftriver.core.api.dao.FormFieldDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDropDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDropFormDao;
import com.ushahidi.swiftriver.core.model.Form;
import com.ushahidi.swiftriver.core.model.FormField;
import com.ushahidi.swiftriver.core.model.RiverDrop;
import com.ushahidi.swiftriver.core.model.RiverDropForm;
import com.ushahidi.swiftriver.core.model.RiverDropFormField;

/**
 * @author Ushahidi, Inc
 * 
 */
public class JpaRiverDropFormDaoTest extends AbstractJpaDaoTest {

	@Autowired
	FormDao formDao;

	@Autowired
	FormFieldDao formFieldDao;

	@Autowired
	RiverDropDao riverDropDao;

	@Autowired
	RiverDropFormDao dropFormDao;

	@Test
	public void create() {
		Form form = formDao.findById(1L);
		RiverDrop drop = riverDropDao.findById(1L);
		RiverDropForm dropForm = new RiverDropForm();
		dropForm.setForm(form);
		dropForm.setRiverDrop(drop);
		dropForm.setValues(new ArrayList<RiverDropFormField>());
		RiverDropFormField fieldValue = new RiverDropFormField();
		FormField field = formFieldDao.findById(2L);
		fieldValue.setDropForm(dropForm);
		fieldValue.setField(field);
		fieldValue.setValue("field value");
		dropForm.getValues().add(fieldValue);

		dropFormDao.create(dropForm);

		String sql = "SELECT river_droplets_id, form_id FROM river_droplet_form WHERE id = ?";

		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql,
				dropForm.getId());
		assertEquals(1L,
				((Number) results.get("river_droplets_id")).longValue());
		assertEquals(1L, ((Number) results.get("form_id")).longValue());

		sql = "SELECT droplet_form_id, field_id, value FROM river_droplet_form_field WHERE id = ?";
		results = this.jdbcTemplate.queryForMap(sql, fieldValue.getId());
		assertEquals(dropForm.getId(), results.get("droplet_form_id"));
		assertEquals(2L, ((Number) results.get("field_id")).longValue());
		assertEquals("field value", results.get("value"));
	}
	
	@Test
	public void update() {
		RiverDropForm dropForm = dropFormDao.findById(1L);
		dropForm.setValues(new ArrayList<RiverDropFormField>());
		RiverDropFormField fieldValue = new RiverDropFormField();
		FormField field = formFieldDao.findById(1L);
		fieldValue.setField(field);
		fieldValue.setValue("field value");
		dropForm.getValues().add(fieldValue);
		
		dropFormDao.update(dropForm);
		
		String sql = "SELECT droplet_form_id, field_id, value FROM river_droplet_form_field WHERE id = ?";
		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql, fieldValue.getId());
		assertEquals(dropForm.getId(), results.get("droplet_form_id"));
		assertEquals(1L, ((Number) results.get("field_id")).longValue());
		assertEquals("field value", results.get("value"));
	}
	
	@Test
	public void delete() {
		RiverDropForm dropForm = dropFormDao.findById(1L);
		
		dropFormDao.delete(dropForm);
		
		em.flush();
		
		String sql = "SELECT count(*) FROM river_droplet_form WHERE id = 1";
		assertEquals(0, this.jdbcTemplate.queryForInt(sql));
		
		sql = "SELECT count(*) FROM river_droplet_form_field WHERE droplet_form_id = 1";
		assertEquals(0, this.jdbcTemplate.queryForInt(sql));
	}
}
