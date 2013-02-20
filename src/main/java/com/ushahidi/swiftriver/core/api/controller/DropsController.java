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

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/v1/drops")
public class DropsController extends AbstractController {

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
	public Map<String, Object> addComment(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for getting comments defined a drop.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/comments", method = RequestMethod.GET)
	public Map<String, Object> getComments(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for deleting a comment.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/comments", method = RequestMethod.DELETE)
	public Map<String, Object> deleteComment(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}
	
	/**
	 * Handler for adding a link to a drop.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/links", method = RequestMethod.POST)
	public Map<String, Object> addLink(@RequestBody Map<String, Object> body, @PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}
	
	/**
	 * Handler for deleting a link.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/links", method = RequestMethod.DELETE)
	public Map<String, Object> deleteLink(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}
	
	/**
	 * Handler for adding a place to a drop.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/places", method = RequestMethod.POST)
	public Map<String, Object> addPlace(@RequestBody Map<String, Object> body, @PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}
	
	/**
	 * Handler for deleting a place.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/places", method = RequestMethod.DELETE)
	public Map<String, Object> deletePlace(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}
	
	/**
	 * Handler for adding a tag to a drop.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/tags", method = RequestMethod.POST)
	public Map<String, Object> addTag(@RequestBody Map<String, Object> body, @PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}
	
	/**
	 * Handler for deleting a tag.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/tags", method = RequestMethod.DELETE)
	public Map<String, Object> deleteTag(@PathVariable Long id) {
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
