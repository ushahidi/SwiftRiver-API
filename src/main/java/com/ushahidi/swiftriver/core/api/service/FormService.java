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
package com.ushahidi.swiftriver.core.api.service;

import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.FormDao;
import com.ushahidi.swiftriver.core.api.dao.FormFieldDao;
import com.ushahidi.swiftriver.core.api.dto.CreateFormDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateFormFieldDTO;
import com.ushahidi.swiftriver.core.api.dto.GetFormDTO;
import com.ushahidi.swiftriver.core.api.dto.GetFormFieldDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyFormDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyFormFieldDTO;
import com.ushahidi.swiftriver.core.api.exception.ForbiddenException;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.ActivityType;
import com.ushahidi.swiftriver.core.model.Form;
import com.ushahidi.swiftriver.core.model.FormField;
import com.ushahidi.swiftriver.core.util.ErrorUtil;

/**
 * @author Ushahidi, Inc
 * 
 */
@Transactional(readOnly = true)
@Service
public class FormService {

	@Autowired
	private FormDao formDao;

	@Autowired
	private FormFieldDao formFieldDao;

	@Autowired
	private Mapper mapper;

	@Autowired
	private AccountDao accountDao;
	
	@Autowired
	private AccountService accountService;

	public void setFormDao(FormDao formDao) {
		this.formDao = formDao;
	}

	public void setFormFieldDao(FormFieldDao formFieldDao) {
		this.formFieldDao = formFieldDao;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}

	/**
	 * Create a Form
	 * 
	 * @param formTo
	 * @param authUser
	 * @return
	 */
	@Transactional(readOnly = false)
	public GetFormDTO createForm(CreateFormDTO formTo, String authUser) {
		Account account = accountDao.findByUsernameOrEmail(authUser);

		Form form = mapper.map(formTo, Form.class);
		form.setAccount(account);
		
		try {
			formDao.create(form);
		} catch (DataIntegrityViolationException e) {
			throw ErrorUtil.getBadRequestException("name", "duplicate");
		}
		
		accountService.logActivity(account, ActivityType.CREATE, form);

		return mapper.map(form, GetFormDTO.class);
	}

	/**
	 * Retrieve a Form
	 * 
	 * @param id
	 * @param authUser
	 * @return
	 */
	public GetFormDTO getForm(Long id, String authUser) {
		Form form = getForm(id);
		return mapper.map(form, GetFormDTO.class);
	}

	/**
	 * Update an existing form
	 * 
	 * @param formId
	 * @param modifyFormTo
	 * @param authUser
	 * @return
	 */
	@Transactional(readOnly = false)
	public GetFormDTO modifyForm(Long formId, ModifyFormDTO modifyFormTo,
			String authUser) {

		Form form = getForm(formId);

		Account account = accountDao.findByUsernameOrEmail(authUser);

		if (form.getAccount().getId() != account.getId())
			throw new ForbiddenException("Permission denied.");

		mapper.map(modifyFormTo, form);
		formDao.update(form);

		return mapper.map(form, GetFormDTO.class);
	}

	/**
	 * Delete a form
	 * 
	 * @param formId
	 * @param name
	 */
	@Transactional(readOnly = false)
	public void deleteForm(Long formId, String authUser) {
		Form form = getForm(formId);

		Account account = accountDao.findByUsernameOrEmail(authUser);

		if (form.getAccount().getId() != account.getId())
			throw new ForbiddenException("Permission denied.");

		formDao.delete(form);
	}

	/**
	 * Create a field in a Form
	 * 
	 * @param formId
	 * @param fieldTo
	 * @return
	 */
	public GetFormFieldDTO createField(Long formId, CreateFormFieldDTO fieldTo,
			String authUser) {
		Form form = getForm(formId);

		Account account = accountDao.findByUsernameOrEmail(authUser);

		if (form.getAccount().getId() != account.getId())
			throw new ForbiddenException("Permission denied.");

		FormField field = mapper.map(fieldTo, FormField.class);
		field.setForm(form);
		formFieldDao.create(field);

		return mapper.map(field, GetFormFieldDTO.class);
	}

	/**
	 * Modify the given form field.
	 * 
	 * @param formId
	 * @param fieldId
	 * @param modifyFieldTO
	 * @param authUser
	 * @return
	 */
	public GetFormFieldDTO modifyField(Long formId, Long fieldId,
			ModifyFormFieldDTO modifyFieldTO, String authUser) {
		FormField field = getFormField(formId, fieldId);

		Account account = accountDao.findByUsernameOrEmail(authUser);

		if (field.getForm().getAccount().getId() != account.getId())
			throw new ForbiddenException("Permission denied.");

		mapper.map(modifyFieldTO, field);
		formFieldDao.update(field);

		return mapper.map(field, GetFormFieldDTO.class);
	}
	
	/**
	 * Delete a form field
	 * 
	 * @param formId
	 * @param fieldId
	 * @param authUser
	 */
	public void deleteField(Long formId, Long fieldId, String authUser) {
		FormField field = getFormField(formId, fieldId);
		
		Account account = accountDao.findByUsernameOrEmail(authUser);

		if (field.getForm().getAccount().getId() != account.getId())
			throw new ForbiddenException("Permission denied.");
		
		formFieldDao.delete(field);
	}

	/**
	 * Helper method for retrieving the specified form.
	 * 
	 * @param formId
	 */
	private Form getForm(Long formId) {
		Form form = formDao.findById(formId);

		if (form == null)
			throw new NotFoundException(String.format(
					"The form with id %d does not exist.", formId));

		return form;
	}

	/**
	 * Helper method for retrieving a fomr field from a form.
	 * 
	 * @param formId
	 * @param fieldId
	 * @return
	 */
	private FormField getFormField(Long formId, Long fieldId) {
		FormField field = formFieldDao.findById(fieldId);

		if (field == null)
			throw new NotFoundException(String.format(
					"The field with id %d does not exist.", fieldId));

		Form form = getForm(formId);
		
		if (!form.getFields().contains(field))
			throw new NotFoundException(String.format(
					"The field with id %d does not exist.", fieldId));
		
		return field;
	}
}
