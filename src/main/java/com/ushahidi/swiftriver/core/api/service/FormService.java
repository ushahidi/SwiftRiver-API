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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.FormDao;
import com.ushahidi.swiftriver.core.api.dto.CreateFormDTO;
import com.ushahidi.swiftriver.core.api.dto.GetFormDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyFormDTO;
import com.ushahidi.swiftriver.core.api.exception.ForbiddenException;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Form;

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
	private Mapper mapper;

	@Autowired
	private AccountDao accountDao;

	public void setFormDao(FormDao formDao) {
		this.formDao = formDao;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
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
		Account account = accountDao.findByUsername(authUser);

		Form form = mapper.map(formTo, Form.class);
		form.setAccount(account);
		formDao.create(form);

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

		Account account = accountDao.findByUsername(authUser);

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

		Account account = accountDao.findByUsername(authUser);

		if (form.getAccount().getId() != account.getId())
			throw new ForbiddenException("Permission denied.");
		
		formDao.delete(form);
	}

	/**
	 * Helper method for retrieving the specified form
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

}
