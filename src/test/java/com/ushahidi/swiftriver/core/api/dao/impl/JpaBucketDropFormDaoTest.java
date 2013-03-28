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
import com.ushahidi.swiftriver.core.api.dao.BucketDropDao;
import com.ushahidi.swiftriver.core.api.dao.BucketDropFormDao;
import com.ushahidi.swiftriver.core.model.Form;
import com.ushahidi.swiftriver.core.model.FormField;
import com.ushahidi.swiftriver.core.model.BucketDrop;
import com.ushahidi.swiftriver.core.model.BucketDropForm;
import com.ushahidi.swiftriver.core.model.BucketDropFormField;

/**
 * @author Ushahidi, Inc
 * 
 */
public class JpaBucketDropFormDaoTest extends AbstractJpaDaoTest {

	@Autowired
	FormDao formDao;

	@Autowired
	FormFieldDao formFieldDao;

	@Autowired
	BucketDropDao bucketDropDao;

	@Autowired
	BucketDropFormDao dropFormDao;

	@Test
	public void create() {
		Form form = formDao.findById(1L);
		BucketDrop drop = bucketDropDao.findById(1L);
		BucketDropForm dropForm = new BucketDropForm();
		dropForm.setForm(form);
		dropForm.setDrop(drop);
		dropForm.setValues(new ArrayList<BucketDropFormField>());
		BucketDropFormField fieldValue = new BucketDropFormField();
		FormField field = formFieldDao.findById(2L);
		fieldValue.setDropForm(dropForm);
		fieldValue.setField(field);
		fieldValue.setValue("field value");
		dropForm.getValues().add(fieldValue);

		dropFormDao.create(dropForm);

		String sql = "SELECT drop_id, form_id FROM bucket_droplet_form WHERE id = ?";

		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql,
				dropForm.getId());
		assertEquals(1L,
				((Number) results.get("drop_id")).longValue());
		assertEquals(1L, ((Number) results.get("form_id")).longValue());

		sql = "SELECT droplet_form_id, field_id, value FROM bucket_droplet_form_field WHERE id = ?";
		results = this.jdbcTemplate.queryForMap(sql, fieldValue.getId());
		assertEquals(dropForm.getId(), results.get("droplet_form_id"));
		assertEquals(2L, ((Number) results.get("field_id")).longValue());
		assertEquals("field value", results.get("value"));
	}
	
	@Test
	public void update() {
		BucketDropForm dropForm = dropFormDao.findById(1L);
		dropForm.setValues(new ArrayList<BucketDropFormField>());
		BucketDropFormField fieldValue = new BucketDropFormField();
		FormField field = formFieldDao.findById(1L);
		fieldValue.setField(field);
		fieldValue.setValue("field value");
		dropForm.getValues().add(fieldValue);
		
		dropFormDao.update(dropForm);
		
		String sql = "SELECT droplet_form_id, field_id, value FROM bucket_droplet_form_field WHERE id = ?";
		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql, fieldValue.getId());
		assertEquals(dropForm.getId(), results.get("droplet_form_id"));
		assertEquals(1L, ((Number) results.get("field_id")).longValue());
		assertEquals("field value", results.get("value"));
	}
	
	@Test
	public void delete() {
		BucketDropForm dropForm = dropFormDao.findById(1L);
		
		dropFormDao.delete(dropForm);
		
		em.flush();
		
		String sql = "SELECT count(*) FROM bucket_droplet_form WHERE id = 1";
		assertEquals(0, this.jdbcTemplate.queryForInt(sql));
		
		sql = "SELECT count(*) FROM bucket_droplet_form_field WHERE droplet_form_id = 1";
		assertEquals(0, this.jdbcTemplate.queryForInt(sql));
	}
}
