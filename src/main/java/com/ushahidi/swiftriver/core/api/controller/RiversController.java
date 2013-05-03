/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/agpl.html>
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.core.api.controller;

import java.security.Principal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.ushahidi.swiftriver.core.api.dto.CreateChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateCommentDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.CreatePlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateRiverDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateRuleDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateTagDTO;
import com.ushahidi.swiftriver.core.api.dto.FollowerDTO;
import com.ushahidi.swiftriver.core.api.dto.FormValueDTO;
import com.ushahidi.swiftriver.core.api.dto.GetChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCommentDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetPlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetTagDTO;
import com.ushahidi.swiftriver.core.api.dto.GetPlaceTrend;
import com.ushahidi.swiftriver.core.api.dto.GetRiverDTO;
import com.ushahidi.swiftriver.core.api.dto.GetRuleDTO;
import com.ushahidi.swiftriver.core.api.dto.GetTagTrend;
import com.ushahidi.swiftriver.core.api.dto.ModifyChannelDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyFormValueDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyRiverDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ErrorField;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.api.service.RiverService;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.support.DropFilter;
import com.ushahidi.swiftriver.core.support.TrendFilter;

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
	@ResponseBody
	public void deleteRiver(@PathVariable Long id, Principal principal) {
		riverService.deleteRiver(id, principal.getName());
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
	public GetCollaboratorDTO addCollaborator(
			@RequestBody CreateCollaboratorDTO body, @PathVariable Long id,
			Principal principal) {

		List<ErrorField> errors = new ArrayList<ErrorField>();
		if (body.getAccount() == null) {
			errors.add(new ErrorField("account", "missing"));
		}

		if (!errors.isEmpty()) {
			BadRequestException e = new BadRequestException(
					"Invalid parameter.");
			e.setErrors(errors);
			throw e;
		}

		return riverService.addCollaborator(id, body, principal.getName());
	}

	/**
	 * Handler for getting a river's collaborators.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators", method = RequestMethod.GET)
	@ResponseBody
	public List<GetCollaboratorDTO> getCollaborators(@PathVariable Long id) {
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
	public GetCollaboratorDTO modifyCollaborator(@PathVariable Long id,
			@PathVariable Long collaboratorId,
			@RequestBody ModifyCollaboratorDTO body, Principal principal) {

		if (body.getReadOnly() == null && body.getActive() == null) {
			List<ErrorField> errors = new ArrayList<ErrorField>();
			errors.add(new ErrorField("read_only", "missing"));
			errors.add(new ErrorField("active", "missing"));
			BadRequestException e = new BadRequestException(
					"Invalid parameter.");
			e.setErrors(errors);
			throw e;
		}

		return riverService.modifyCollaborator(id, collaboratorId, body,
				principal.getName());
	}

	/**
	 * Handler for removing a river collaborator.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators/{collaboratorId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteCollaborator(@PathVariable Long id,
			@PathVariable Long collaboratorId, Principal principal) {
		riverService
				.deleteCollaborator(id, collaboratorId, principal.getName());
	}

	/**
	 * Handler for adding a follower to a river.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers/{accountId}", method = RequestMethod.PUT)
	@ResponseBody
	public void addFollower(@PathVariable Long id, @PathVariable Long accountId) {
		riverService.addFollower(id, accountId);
	}

	/**
	 * Handler for a river's followers.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers", method = RequestMethod.GET)
	@ResponseBody
	public List<FollowerDTO> getFollowers(@PathVariable Long id,
			@RequestParam(value = "follower", required = false) Long accountId) {
		return riverService.getFollowers(id, accountId);
	}

	/**
	 * Handler for removing a follower.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers/{followerId}", method = RequestMethod.DELETE)
	@ResponseBody
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
			@RequestParam(value = "date_from", required = false) String dateFromS,
			@RequestParam(value = "date_to", required = false) String dateToS,
			@RequestParam(value = "keywords", required = false) String keywords,
			@RequestParam(value = "channels", required = false) String channels,
			@RequestParam(value = "channel_ids", required = false) String cIds,
			@RequestParam(value = "state", required = false) String state,
			@RequestParam(value = "photos", required = false) Boolean photos)
			throws NotFoundException {

		if (maxId == null) {
			maxId = Long.MAX_VALUE;
		}

		List<ErrorField> errors = new ArrayList<ErrorField>();

		List<Long> channelIds = new ArrayList<Long>();
		if (cIds != null) {
			for (String cId : cIds.split(",")) {
				try {
					channelIds.add(Long.parseLong(cId));
				} catch (NumberFormatException ex) {
					errors.add(new ErrorField("channel_ids", "invalid"));
				}
			}
		}

		List<String> channelList = new ArrayList<String>();
		if (channels != null) {
			channelList.addAll(Arrays.asList(channels.split(",")));
		}

		Boolean isRead = null;
		if (state != null) {
			if (state.equalsIgnoreCase("read") || state.equalsIgnoreCase("unread")) {
				isRead = state.equalsIgnoreCase("read");
			} else {
				errors.add(new ErrorField("state", "invalid"));
			}
		}

		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
		Date dateFrom = null;
		if (dateFromS != null) {
			try {
				dateFrom = dateFormat.parse(dateFromS);
			} catch (ParseException e) {
				errors.add(new ErrorField("date_from", "invalid"));
			}
		}

		Date dateTo = null;
		if (dateToS != null) {
			try {
				dateTo = dateFormat.parse(dateToS);
			} catch (ParseException e) {
				errors.add(new ErrorField("date_to", "invalid"));
			}
		}

		if (!errors.isEmpty()) {
			BadRequestException e = new BadRequestException(
					"Invalid parameter.");
			e.setErrors(errors);
			throw e;
		}
		
		DropFilter dropFilter = new DropFilter();
		dropFilter.setMaxId(maxId);
		dropFilter.setSinceId(sinceId);
		dropFilter.setChannels(channelList);
		dropFilter.setChannelIds(channelIds);
		dropFilter.setDateFrom(dateFrom);
		dropFilter.setDateTo(dateTo);
		dropFilter.setRead(isRead);
		dropFilter.setPhotos(photos);
		dropFilter.setKeywords(keywords);

		return riverService.getDrops(id, dropFilter, page, count, principal.getName());
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
	@ResponseBody
	public void deleteDrop(@PathVariable Long id, @PathVariable Long dropId, Principal principal) {
		riverService.deleteDrop(id, dropId, principal.getName());
	}

	/**
	 * Handler for marking a drop as read.
	 * 
	 * @param id
	 * @param dropId
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "/{id}/drops/read/{dropId}", method = RequestMethod.PUT)
	@ResponseBody
	public void markDropAsRead(@PathVariable Long id, @PathVariable Long dropId, Principal principal) {
		riverService.markDropAsRead(id, dropId, principal.getName());
	}
	
	/**
	 * Handler for adding a tag to a drop that is in a river
	 * 
	 * @param id
	 * @param dropId
	 * @param createDTO
	 * @return
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}/tags", method = RequestMethod.POST)
	@ResponseBody
	public GetTagDTO addDropTag(@PathVariable Long id,
			@PathVariable Long dropId, @RequestBody CreateTagDTO createDTO,
			Principal principal) {
		return riverService.addDropTag(id, dropId, createDTO,
				principal.getName());
	}

	/**
	 * Handler for deleting a tag from a drop that is in a river
	 * 
	 * @param id
	 * @param dropId
	 * @param linkId
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}/tags/{tagId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteDropTag(@PathVariable Long id, @PathVariable Long dropId,
			@PathVariable Long tagId, Principal principal) {
		riverService.deleteDropTag(id, dropId, tagId, principal.getName());
	}

	/**
	 * Handler for adding a link to a drop that is in a river
	 * 
	 * @param id
	 * @param dropId
	 * @param createDTO
	 * @return
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}/links", method = RequestMethod.POST)
	@ResponseBody
	public GetLinkDTO addDropLink(@PathVariable Long id,
			@PathVariable Long dropId, @RequestBody CreateLinkDTO createDTO,
			Principal principal) {
		return riverService.addDropLink(id, dropId, createDTO,
				principal.getName());
	}

	/**
	 * Handler for deleting a link from a drop that is in a river
	 * 
	 * @param id
	 * @param dropId
	 * @param linkId
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}/links/{linkId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteDropLink(@PathVariable Long id,
			@PathVariable Long dropId, @PathVariable Long linkId,
			Principal principal) {
		riverService.deleteDropLink(id, dropId, linkId, principal.getName());
	}

	/**
	 * Handler for adding a place to a drop
	 * 
	 * @param id
	 * @param dropId
	 * @param createDTO
	 * @return
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}/places", method = RequestMethod.POST)
	@ResponseBody
	public GetPlaceDTO addDropPlace(@PathVariable Long id,
			@PathVariable Long dropId, @RequestBody CreatePlaceDTO createDTO,
			Principal principal) {
		return riverService.addDropPlace(id, dropId, createDTO,
				principal.getName());
	}

	/**
	 * Handler for deleting a place from a drop that is in a river
	 * 
	 * @param id
	 * @param dropId
	 * @param placeId
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}/places/{placeId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteDropPlace(@PathVariable Long id,
			@PathVariable Long dropId, @PathVariable Long placeId,
			Principal principal) {
		riverService.deleteDropPlace(id, dropId, placeId, principal.getName());
	}

	/**
	 * Handler for adding a comment to a bucket drop
	 * 
	 * @param id
	 * @param dropId
	 * @param createDTO
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "{id}/drops/{dropId}/comments", method = RequestMethod.POST)
	@ResponseBody
	public GetCommentDTO addDropComment(@PathVariable Long id,
			@PathVariable Long dropId, @RequestBody CreateCommentDTO createDTO,
			Principal principal) {

		return riverService.addDropComment(id, dropId, createDTO,
				principal.getName());
	}

	/**
	 * Handler for getting the comments of bucket drop
	 * 
	 * @param id
	 * @param dropId
	 * @return
	 */
	@RequestMapping(value = "{id}/drops/{dropId}/comments", method = RequestMethod.GET)
	@ResponseBody
	public List<GetCommentDTO> getDropComments(@PathVariable Long id,
			@PathVariable Long dropId, Principal principal) {
		return riverService.getDropComments(id, dropId, principal.getName());
	}

	/**
	 * Handler for deleting a comment from a bucket drop
	 * 
	 * @param id
	 * @param dropId
	 * @param commentId
	 * @param principal
	 */
	@RequestMapping(value = "{id}/drops/{dropId}/comments/{commentId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteDropComment(@PathVariable Long id,
			@PathVariable Long dropId, @PathVariable Long commentId,
			Principal principal) {
		riverService.deleteDropComment(id, dropId, commentId,
				principal.getName());
	}

	/**
	 * Handler for adding a form to a river drop
	 * 
	 * @param id
	 * @param dropId
	 * @param createDTO
	 * @return
	 */
	@RequestMapping(value = "/{riverId}/drops/{dropId}/forms", method = RequestMethod.POST)
	@ResponseBody
	public FormValueDTO addDropForm(@PathVariable Long riverId,
			@PathVariable Long dropId,
			@RequestBody FormValueDTO createDTO, Principal principal) {
		return riverService.addDropForm(riverId, dropId, createDTO, principal.getName());
	}

	/**
	 * Handler for modifying form values for a river drop.
	 * 
	 * @param riverId
	 * @param dropId
	 * @param formId  
	 * @return
	 */
	@RequestMapping(value = "/{riverId}/drops/{dropId}/forms/{formId}", method = RequestMethod.PUT)
	@ResponseBody
	public FormValueDTO modifyDropForm(Principal principal,
			@PathVariable Long riverId,
			@PathVariable Long dropId,
			@PathVariable Long formId,
			@RequestBody ModifyFormValueDTO modifyFormTo) {
		return riverService.modifyDropForm(riverId, dropId, formId, modifyFormTo, principal.getName());
	}
	
	/**
	 * Handler for deleting a form from a river drop
	 * 
	 * @param id
	 * @param dropId
	 * @param commentId
	 * @param principal
	 */
	@RequestMapping(value = "{riverId}/drops/{dropId}/forms/{formId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteDropForm(@PathVariable Long riverId,
			@PathVariable Long dropId, @PathVariable Long formId,
			Principal principal) {
		riverService.deleteDropForm(riverId, dropId, formId, principal.getName());
	}
	
	/**
	 * Handler for fetching the rules for the river
	 * 
	 * @param id
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "{id}/rules", method = RequestMethod.GET)
	@ResponseBody
	public List<GetRuleDTO> getRules(@PathVariable  Long id, Principal principal) {
		return riverService.getRules(id, principal.getName());
	}
	
	/**
	 * Handler for creating a new rule
	 * 
	 * @param id
	 * @param createRuleDTO
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "{id}/rules", method = RequestMethod.POST)
	@ResponseBody
	public GetRuleDTO addRule(@PathVariable Long id, @RequestBody CreateRuleDTO createRuleDTO,
			Principal principal) {
		return riverService.addRule(id, createRuleDTO, principal.getName());
	}

	/**
	 * Handler for modifying a rule
	 * 
	 * @param id
	 * @param ruleId
	 * @param createRuleDTO
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "{id}/rules/{ruleId}", method = RequestMethod.PUT)
	@ResponseBody
	public GetRuleDTO modifyRule(@PathVariable Long id, @PathVariable Long ruleId,
			@RequestBody CreateRuleDTO createRuleDTO, Principal principal) {
		return riverService.modifyRule(id, ruleId, createRuleDTO, principal.getName());
	}
	
	/**
	 * Handler for deleting a rule
	 * 
	 * @param id
	 * @param ruleId
	 * @param principal
	 */
	@RequestMapping(value = "{id}/rules/{ruleId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteRule(@PathVariable Long id, @PathVariable Long ruleId, Principal principal) {
		riverService.deleteRule(id, ruleId, principal.getName());
	}
	
	/**
	 * Handler for getting the list of trending tags within the
	 * river with the specified <code>id</code>
	 * 
	 * @param id
	 * @param principal
	 * @param since
	 * @param until
	 * @return
	 */
	@RequestMapping(value="{id}/trends/tags", method=RequestMethod.GET)
	@ResponseBody
	public List<GetTagTrend> getTrendingTags(@PathVariable Long id, Principal principal,
			@RequestParam(value = "count", required = false, defaultValue = "20") int count,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			@RequestParam(value = "since", required = false) String since,
			@RequestParam(value = "until", required = false) String until) {
		
		List<ErrorField> errors = new ArrayList<ErrorField>();

		// Validate the dates
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date dateFrom = null;
		Date dateTo = null;
		
		if (since != null) {
			try {
				dateFrom = dateFormat.parse(since);
			} catch (ParseException e) {
				errors.add(new ErrorField("since", "invalid"));
			}
		}
		
		if (until != null) {
			try {
				dateTo = dateFormat.parse(until);
			} catch (ParseException e) {
				errors.add(new ErrorField("until", "invalid"));
			}
		}
		
		// Do we have any validation errors
		if (!errors.isEmpty()) {
			BadRequestException exception =  new BadRequestException();
			exception.setErrors(errors);
			throw exception;
		}

		TrendFilter trendFilter = new TrendFilter();
		trendFilter.setCount(count);
		trendFilter.setPage(page);
		trendFilter.setDateFrom(dateFrom);
		trendFilter.setDateTo(dateTo);

		return riverService.getTrendingTags(id, trendFilter, principal.getName());
	}
	
	
	/**
	 * Handler for getting the list of trending places within the
	 * river with the specified <code>id</code>
	 * 
	 * @param id
	 * @param principal
	 * @param since
	 * @param until
	 * @return
	 */
	@RequestMapping(value="{id}/trends/places", method=RequestMethod.GET)
	@ResponseBody
	public List<GetPlaceTrend> getTrendingPlaces(@PathVariable Long id, Principal principal,
			@RequestParam(value = "count", required = false, defaultValue = "20") int count,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page,
			@RequestParam(value = "since", required = false) String since,
			@RequestParam(value = "until", required = false) String until) {
		
		List<ErrorField> errors = new ArrayList<ErrorField>();

		// Validate the dates
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date dateFrom = null;
		Date dateTo = null;
		
		if (since != null) {
			try {
				dateFrom = dateFormat.parse(since);
			} catch (ParseException e) {
				errors.add(new ErrorField("since", "invalid"));
			}
		}
		
		if (until != null) {
			try {
				dateTo = dateFormat.parse(until);
			} catch (ParseException e) {
				errors.add(new ErrorField("until", "invalid"));
			}
		}

		// Do we have any validation errors
		if (!errors.isEmpty()) {
			BadRequestException exception =  new BadRequestException();
			exception.setErrors(errors);
			throw exception;
		}
		
		TrendFilter trendFilter = new TrendFilter();
		trendFilter.setCount(count);
		trendFilter.setPage(page);
		trendFilter.setDateFrom(dateFrom);
		trendFilter.setDateTo(dateTo);

		return riverService.getTredingPlaces(id, trendFilter, principal.getName());
	}
	
}
