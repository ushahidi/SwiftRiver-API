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
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ushahidi.swiftriver.core.api.dto.CollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateRiverDTO;
import com.ushahidi.swiftriver.core.api.dto.FollowerDTO;
import com.ushahidi.swiftriver.core.api.dto.GetChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO;
import com.ushahidi.swiftriver.core.api.dto.GetRiverDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyRiverDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ErrorField;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.api.service.RiverService;
import com.ushahidi.swiftriver.core.model.Account;

@Controller
@RequestMapping("/v1/rivers")
public class RiversController extends AbstractController {

	final Logger logger = LoggerFactory.getLogger(RiversController.class);

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
	public GetRiverDTO createRiver(Principal principal,
			@RequestBody CreateRiverDTO riverTO) {
		return riverService.createRiver(riverTO, principal.getName());
	}

	/**
	 * Handler for obtaining a specific river.
	 * 
	 * @param id
	 * @return
	 * @throws NotFoundException
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public GetRiverDTO getRiver(@PathVariable Long id) throws NotFoundException {
		return riverService.getRiverById(id);
	}

	/**
	 * Handler for modifying an existing river.
	 * 
	 * @param riverId
	 * @return
	 */
	@RequestMapping(value = "/{riverId}", method = RequestMethod.PUT)
	@ResponseBody
	public GetRiverDTO modifyRiver(Principal principal,
			@PathVariable Long riverId,
			@RequestBody ModifyRiverDTO modifyRiverTO) {
		return riverService.modifyRiver(riverId, modifyRiverTO,
				principal.getName());
	}

	/**
	 * Handler for deleting an existing river.
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteRiver(@PathVariable Long id) {
		riverService.deleteRiver(id);
	}

	/**
	 * Handler for adding a channel to a river.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/channels", method = RequestMethod.POST)
	@ResponseBody
	public GetChannelDTO createChannel(
			@RequestBody CreateChannelDTO createChannelTO, @PathVariable Long id) {
		return riverService.createChannel(id, createChannelTO);
	}

	/**
	 * Handler for modifying an existing channel.
	 * 
	 * @param body
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{riverId}/channels/{channelId}", method = RequestMethod.PUT)
	@ResponseBody
	public GetChannelDTO modifyChannel(Principal principal,
			@PathVariable Long riverId, @PathVariable Long channelId,
			@RequestBody ModifyChannelDTO modifyChannelTO) {
		return riverService.modifyChannel(riverId, channelId, modifyChannelTO,
				principal.getName());
	}

	/**
	 * Handler for deleting a channel from a River.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{riverId}/channels/{channelId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteChannel(Principal principal, @PathVariable Long riverId,
			@PathVariable Long channelId) {
		riverService.deleteChannel(riverId, channelId, principal.getName());
	}

	/**
	 * Handler for adding a collaborator to a river.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators", method = RequestMethod.POST)
	@ResponseBody
	public CollaboratorDTO addCollaborator(@RequestBody CollaboratorDTO body,
			@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for getting a river's collaborators.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators", method = RequestMethod.GET)
	@ResponseBody
	public List<CollaboratorDTO> getCollaborators(@PathVariable Long id) {
		return riverService.getCollaborators(id);
	}

	/**
	 * Handler for modifying an existing collaborator.
	 * 
	 * @param body
	 * 
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators/{collaboratorId}", method = RequestMethod.PUT)
	@ResponseBody
	public CollaboratorDTO modifyCollaborator(@PathVariable Long id,
			@PathVariable Long collaboratorId, @RequestBody CollaboratorDTO body) {
		return riverService.modifyCollaborator(id, collaboratorId, body);
	}

	/**
	 * Handler for removing a river collaborator.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators/{collaboratorId}", method = RequestMethod.DELETE)
	public void deleteCollaborator(@PathVariable Long id,
			@PathVariable Long collaboratorId) {
		riverService.deleteCollaborator(id, collaboratorId);
	}

	/**
	 * Handler for adding a follower to a river.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers", method = RequestMethod.POST)
	public void addFollower(@RequestBody FollowerDTO body, @PathVariable Long id) {
		riverService.addFollower(id, body);
	}

	/**
	 * Handler for a river's followers.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers", method = RequestMethod.GET)
	@ResponseBody
	public List<FollowerDTO> getFollowers(@PathVariable Long id) {
		return riverService.getFollowers(id);
	}

	/**
	 * Handler for removing a follower.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers/{followerId}", method = RequestMethod.DELETE)
	public void deleteFollower(@PathVariable Long id,
			@PathVariable Long followerId) {
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
	 * @return
	 * @throws NotFoundException
	 */
	@RequestMapping(value = "/{id}/drops", method = RequestMethod.GET)
	@ResponseBody
	public List<GetDropDTO> getDrops(
			@PathVariable Long id,
			Principal principal,
			@RequestParam(value = "count", required = false, defaultValue = "10") Integer count,
			@RequestParam(value = "max_id", required = false) Long maxId,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(value = "since_id", required = false) Long sinceId,
			@RequestParam(value = "date_from", required = false) Date dateFrom,
			@RequestParam(value = "date_to", required = false) Date dateTo,
			@RequestParam(value = "keywords", required = false) String keywords,
			@RequestParam(value = "channels", required = false) String channels,
			@RequestParam(value = "locations", required = false) String location)
			throws NotFoundException {

		if (maxId == null) {
			maxId = Long.MAX_VALUE;
		}

		List<Long> channel = new ArrayList<Long>();
		if (channels != null) {
			for (String channelId : channels.split(",")) {
				try {
					channel.add(Long.parseLong(channelId));
				} catch (NumberFormatException ex) {
					BadRequestException e = new BadRequestException(
							"Invalid channels.");
					List<ErrorField> errors = new ArrayList<ErrorField>();
					errors.add(new ErrorField("channel", "invalid"));
					e.setErrors(errors);
					throw e;
				}
			}
		}

		return riverService.getDrops(id, maxId, sinceId, page, count, channel,
				principal.getName());
	}

	/**
	 * Stream a river.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/drops", method = RequestMethod.GET, headers = "X-Stream")
	public Account getDropsStream(@PathVariable Long id) {
		// TODO: redirect to streaming server.
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
		riverService.deleteDrop(id, dropId);
	}
}
