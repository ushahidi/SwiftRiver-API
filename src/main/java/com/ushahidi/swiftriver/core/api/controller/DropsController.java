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
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ushahidi.swiftriver.core.api.dto.GetCommentDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetPlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetTagDTO;
import com.ushahidi.swiftriver.core.api.service.DropService;

@Controller
@RequestMapping("/v1/drops")
public class DropsController {

	@Autowired
	private DropService dropService;

	/**
	 * Handler for creating a drop.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public Map<String, Object> createDrop(@RequestBody Map<String, Object> body) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for adding a comment to a drop.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/comments", method = RequestMethod.POST)
	@ResponseBody
	public GetCommentDTO addComment(@RequestBody Map<String, Object> body,
			@PathVariable Long id, Principal principal) {
		return dropService.addComment(id, body, principal.getName());
	}

	/**
	 * Handler for getting comments defined a drop.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/comments", method = RequestMethod.GET)
	@ResponseBody
	public List<GetCommentDTO> getComments(@PathVariable long id) {
		return dropService.getComments(id);
	}

	/**
	 * Handler for deleting a comment.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/comments/{commentId}", method = RequestMethod.DELETE)
	public void deleteComment(@PathVariable long id, @PathVariable long commentId, Principal principal) {
		dropService.deleteComment(id, commentId);
	}
	
	/**
	 * Handler for adding a link to a drop.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/links", method = RequestMethod.POST)
	@ResponseBody
	public GetLinkDTO addLink(@RequestBody Map<String, Object> body, @PathVariable Long id, Principal principal) {
		return dropService.addLink(id, principal.getName(), body);
	}
	
	/**
	 * Handler for deleting a link.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/links/{linkId}", method = RequestMethod.DELETE)
	public void deleteLink(@PathVariable long id, @PathVariable long linkId, Principal principal) {
		dropService.deleteLink(id, linkId, principal.getName());
	}
	
	/**
	 * Handler for adding a place to a drop.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/places", method = RequestMethod.POST)
	@ResponseBody
	public GetPlaceDTO addPlace(@RequestBody Map<String, Object> body, @PathVariable long id, Principal principal) {
		return dropService.addPlace(id, principal.getName(), body);
	}
	
	/**
	 * Handler for deleting a place.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/places/{placeId}", method = RequestMethod.DELETE)
	public void deletePlace(@PathVariable long id, @PathVariable long placeId, Principal principal) {
		dropService.deletePlace(id, placeId, principal.getName());
	}
	
	/**
	 * Handler for adding a tag to a drop.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/tags", method = RequestMethod.POST)
	@ResponseBody
	public GetTagDTO addTag(@RequestBody Map<String, Object> body, @PathVariable Long id, Principal principal) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}
	
	/**
	 * Handler for deleting a tag.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/tags/{tagId}", method = RequestMethod.DELETE)
	public Map<String, Object> deleteTag(@PathVariable long id, @PathVariable long tagId, Principal principal) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}
	
	/**
	 * Handler for adding a media to a drop.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/media", method = RequestMethod.POST)
	public Map<String, Object> addMedia(@RequestBody Map<String, Object> body, @PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}
	
	/**
	 * Handler for deleting a media.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/media", method = RequestMethod.DELETE)
	public Map<String, Object> deleteMedia(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}
}
