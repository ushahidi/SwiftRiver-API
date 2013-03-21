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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ushahidi.swiftriver.core.api.dto.CreateDropDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ErrorField;
import com.ushahidi.swiftriver.core.api.service.DropService;

@Controller
@RequestMapping("/v1/drops")
public class DropsController extends AbstractController {

	@Autowired
	private DropService dropService;

	/**
	 * Handler for creating a drop.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public List<GetDropDTO> createDrop(@RequestBody List<CreateDropDTO> drops) {

		List<ErrorField> errors = new ArrayList<ErrorField>();
		for (int i=0; i < drops.size(); i++) {
			CreateDropDTO drop = drops.get(i);

			if (drop.getTitle() == null) {
				errors.add(new ErrorField("[" + i + "].title", "missing"));
			}

			if (drop.getContent() == null) {
				errors.add(new ErrorField("[" + i + "].content", "missing"));
			}

			if (drop.getChannel() == null) {
				errors.add(new ErrorField("[" + i + "].channel", "missing"));
			}

			if (drop.getDatePublished() == null) {
				errors.add(new ErrorField("[" + i + "].date_published", "missing"));
			}

			if (drop.getOriginalId() == null) {
				errors.add(new ErrorField("[" + i + "].original_id", "missing"));
			}

			if (drop.getIdentity() == null
					|| drop.getIdentity().getOriginId() == null) {
				errors.add(new ErrorField("[" + i + "].identity.origin_id", "missing"));
			}
		}
		
		if (!errors.isEmpty()) {
			BadRequestException e = new BadRequestException(
					"Invalid parameter.");
			e.setErrors(errors);
			throw e;
		}

		return dropService.createDrops(drops);
	}

}
