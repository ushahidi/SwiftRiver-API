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

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ushahidi.swiftriver.core.api.exception.ResourceNotFoundException;
import com.ushahidi.swiftriver.core.api.service.RiverService;
import com.ushahidi.swiftriver.core.model.Account;

@Controller
@RequestMapping("/v1/rivers")
public class RiversController {

	@Autowired
	private RiverService riverService;

	/**
	 * Handler for creating a new river.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> createRiver(@RequestBody Map<String, Object> body) {
		return riverService.createRiver(body);
	}

	/**
	 * Handler for obtaining a specific river.
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getRiver(@PathVariable Long id) {
		Map<String, Object> river = riverService.getRiver(id);
		if (river == null) {
			throw new ResourceNotFoundException("The river does not exist");
		}
		return river;
	}

	/**
	 * Handler for modifying an existing river.
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public Map<String, Object> modifyRiver(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		return riverService.updateRiver(id, body);
	}

	/**
	 * Handler for deleting an existing river.
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)	
	public void deleteRiver(@PathVariable Long id) {
		if (!riverService.deleteRiver(id)) {
			throw new ResourceNotFoundException("The river does not exist");
		}
	}

	/**
	 * Handler for adding a channel to a river.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/channels", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> addChannel(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		return riverService.addChannel(id, body);
	}

	/**
	 * Handler for getting channels defined a river.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/channels", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, Object>> getChannels(@PathVariable Long id) {
		return riverService.getChannels(id);
	}

	/**
	 * Handler for modifying an existing channel.
	 * @param body
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{id}/channels/{channelId}", method = RequestMethod.PUT)
	@ResponseBody
	public Map<String, Object> modifyChannel(@PathVariable Long id,
			@PathVariable Integer channelId,
			@RequestBody Map<String, Object> body) {
		return riverService.modifyChannel(id, channelId, body);
	}

	/**
	 * Handler for deleting a channel from a River.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/channels/{channelId}", method = RequestMethod.DELETE)
	public void deleteChannel(@PathVariable Long id, @PathVariable Integer channelId) {
		riverService.deleteChannel(id, channelId);
	}

	/**
	 * Handler for adding a collaborator to a river.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> addCollaborator(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		return riverService.addCollaborator(id, body);
	}

	/**
	 * Handler for getting a river's collaborators.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, Object>> getCollaborators(@PathVariable Long id) {
		return riverService.getCollaborators(id);
	}

	/**
	 * Handler for modifying an existing collaborator.
	 * @param body
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators/{collaboratorId}", method = RequestMethod.PUT)
	@ResponseBody
	public Map<String, Object> modifyCollaborator(@PathVariable Long id,
			@PathVariable Long collaboratorId, @RequestBody Map<String, Object> body) {
		return riverService.modifyCollaborator(id, collaboratorId, body);
	}

	/**
	 * Handler for removing a river collaborator.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators/{collaboratorId}", method = RequestMethod.DELETE)
	public void deleteCollaborator(@PathVariable Long id, @PathVariable Long collaboratorId) {
		riverService.deleteCollaborator(id, collaboratorId);
	}

	/**
	 * Handler for adding a follower to a river.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> addFollower(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		return riverService.addFollower(id, body);
	}

	/**
	 * Handler for a river's followers.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, Object>> getFollowers(@PathVariable Long id) {
		return riverService.getFollowers(id);
	}

	/**
	 * Handler for removing a follower.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers/{followerId}", method = RequestMethod.DELETE)
	public void deleteFollower(@PathVariable Long id, @PathVariable Long followerId) {
		riverService.deleteFollower(id, followerId);
	}

	/**
	 * Handler for adding a subscription to a river.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.POST)
	public Account addSubscription(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for getting subscriptions defined a river.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, Object>> getSubscriptions(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for modifying an existing subscription.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.PUT)
	public void modifySubscription(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for deleting a subscription form a river.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.DELETE)
	public void deleteSubscription(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Get drops in the river.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/drops", method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, Object>> getDrops(
			@PathVariable Long id,
			@RequestParam(value = "count", required = false, defaultValue = "50") int count,
			@RequestParam(value = "max_id", required = false) long maxId,
			@RequestParam(value = "since_id", required = false) long sinceId,
			@RequestParam(value = "date_from", required = false) Date dateFrom,
			@RequestParam(value = "date_to", required = false) Date dateTo,
			@RequestParam(value = "keywords", required = false) String keywords,
			@RequestParam(value = "channels", required = false) String channels,
			@RequestParam(value = "count", required = false, defaultValue = "50") String location) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}
	
	/**
	 * Handler for deleting a drop from a river.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}", method = RequestMethod.DELETE)
	public void deleteDrop(@PathVariable Long id, @PathVariable Long dropId) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

}