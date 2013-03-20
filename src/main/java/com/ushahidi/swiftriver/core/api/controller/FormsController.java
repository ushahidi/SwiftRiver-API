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
package com.ushahidi.swiftriver.core.api.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ushahidi.swiftriver.core.api.dto.CreateFieldDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateFormDTO;
import com.ushahidi.swiftriver.core.api.dto.GetChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.GetFormDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyFieldDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyFormDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ErrorField;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.api.service.FormService;

/**
 * Endpoints for managing Forms
 * 
 * @author Ushahidi, Inc
 * 
 */
@Controller
@RequestMapping("/v1/forms")
public class FormsController extends AbstractController {
	
	@Autowired
	private FormService formService;

	/**
	 * Endpoint for creating a new Form
	 * 
	 * @param principal
	 * @param formTo
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public GetFormDTO createForm(Principal principal,
			@RequestBody CreateFormDTO formTo) {

		// Validate a form name is provided
		List<ErrorField> errors = new ArrayList<ErrorField>();
		if (formTo.getName() == null) {
			errors.add(new ErrorField("name", "missing"));
		}

//		// Validate form fields are provided
//		if (formTo.getFields() != null) {
//			List<CreateFieldDTO> fields = formTo.getFields();
//			for (int i = 0; i < fields.size(); i++) {
//				CreateFieldDTO field = fields.get(i);
//
//				if (field.getTitle() == null) {
//					errors.add(new ErrorField("fields[" + i + "].title",
//							"missing"));
//				}
//
//				if (field.getType() == null) {
//					errors.add(new ErrorField("fields[" + i + "].type",
//							"missing"));
//				}
//			}
//		}

		if (!errors.isEmpty()) {
			BadRequestException e = new BadRequestException(
					"Invalid parameter.");
			e.setErrors(errors);
			throw e;
		}

		return formService.createForm(formTo, principal.getName());
	}

	/**
	 * Handler for retrieving a specific Form
	 * 
	 * @param id
	 * @return
	 * @throws NotFoundException
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public GetFormDTO getForm(@PathVariable Long id) throws NotFoundException {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for modifying an existing form.
	 * 
	 * @param principal
	 * @param id
	 * @param modifyFormTo
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public GetFormDTO modifyForm(Principal principal, @PathVariable Long id,
			@RequestBody ModifyFormDTO modifyFormTo) {
		
		return formService.modifyForm(id, modifyFormTo, principal.getName());
	}

	/**
	 * Handler for deleting an existing form.
	 * 
	 * @param id
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteForm(@PathVariable Long id, Principal principal) {
		formService.deleteForm(id, principal.getName());
	}

	/**
	 * Handler for creating a form field.
	 * 
	 * @param createFieldTo
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}/fields", method = RequestMethod.POST)
	@ResponseBody
	public GetChannelDTO createField(@RequestBody CreateFieldDTO createFieldTo,
			@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for modifying an existing form field.
	 * 
	 * @param body
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{formId}/fields/{fieldId}", method = RequestMethod.PUT)
	@ResponseBody
	public GetChannelDTO modifyField(Principal principal,
			@PathVariable Long formId, @PathVariable Long fieldId,
			@RequestBody ModifyFieldDTO modifyFieldTO) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for deleting a form field.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{formId}/fields/{fieldId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteField(Principal principal, @PathVariable Long formId,
			@PathVariable Long fieldId) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}
}
