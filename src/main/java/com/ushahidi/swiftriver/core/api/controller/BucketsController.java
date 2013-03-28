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
import java.util.HashMap;
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

import com.ushahidi.swiftriver.core.api.dto.CreateBucketDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateCommentDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.CreatePlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateTagDTO;
import com.ushahidi.swiftriver.core.api.dto.DropSourceDTO;
import com.ushahidi.swiftriver.core.api.dto.FollowerDTO;
import com.ushahidi.swiftriver.core.api.dto.GetBucketDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCommentDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetPlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetTagDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ErrorField;
import com.ushahidi.swiftriver.core.api.exception.UnauthorizedExpection;
import com.ushahidi.swiftriver.core.api.service.BucketService;
import com.ushahidi.swiftriver.core.model.BucketDrop;

@Controller
@RequestMapping("/v1/buckets")
public class BucketsController extends AbstractController {

	@Autowired
	private BucketService bucketService;
	
	/**
	 * Handler for creating a new bucket.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public GetBucketDTO createBucket(@RequestBody CreateBucketDTO createDTO, Principal principal) {
		return bucketService.createBucket(createDTO, principal.getName());
	}

	/**
	 * Handler for obtaining a specific bucket.
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public GetBucketDTO getBucket(@PathVariable Long id, Principal principal) {
		return bucketService.getBucket(id, principal.getName());
	}

	/**
	 * Handler for modifying an existing bucket.
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public GetBucketDTO modifyBucket(@RequestBody CreateBucketDTO modifiedBucket,
			@PathVariable Long id, Principal principal) {
		return bucketService.modifyBucket(id, modifiedBucket, principal.getName());
	}

	/**
	 * Handler for deleting an existing bucket.
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteBucket(@PathVariable Long id, Principal principal) {
		bucketService.deleteBucket(id, principal.getName());
	}

	/**
	 * Handler for adding a collaborator to a bucket.
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

		return bucketService.addCollaborator(id, body, principal.getName());
	}

	/**
	 * Handler for getting a bucket's collaborators.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators", method = RequestMethod.GET)
	@ResponseBody
	public List<GetCollaboratorDTO> getCollaborators(@PathVariable Long id) {
		return bucketService.getCollaborators(id);
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

		return bucketService.modifyCollaborator(id, collaboratorId, body,
				principal.getName());
	}

	/**
	 * Handler for removing a bucket collaborator.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators/{collaboratorId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteCollaborator(@PathVariable Long id,
			@PathVariable Long collaboratorId, Principal principal) {
		bucketService.deleteCollaborator(id, collaboratorId, principal.getName());
	}

	/**
	 * Handler for adding a follower to a bucket.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers/{accountId}", method = RequestMethod.PUT)
	@ResponseBody
	public void addFollower(@PathVariable long id, @PathVariable long accountId, Principal principal) {
		bucketService.addFollower(id, accountId, principal.getName());
	}

	/**
	 * Handler for a bucket's followers.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers", method = RequestMethod.GET)
	@ResponseBody
	public List<FollowerDTO> getFollowers(@PathVariable Long id,
			@RequestParam(value = "follower", required = false) Long accountId) {
		return bucketService.getFollowers(id, accountId);
	}

	/**
	 * Handler for removing a follower.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers/{accountId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteFollower(@PathVariable Long id, @PathVariable Long accountId) {
		bucketService.deleteFollower(id, accountId);
	}

	/**
	 * Handler for adding a subscription to a bucket.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> addSubscription(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for getting subscriptions defined a bucket.
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
	@ResponseBody
	public Map<String, Object> modifySubscription(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for deleting a subscription form a bucket.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/subscriptions/{subscriptionId}", method = RequestMethod.DELETE)
	public void deleteSubscription(@PathVariable Long id, @PathVariable Long subscriptionId) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Get drops in the bucket.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/drops", method = RequestMethod.GET)
	@ResponseBody
	public List<GetDropDTO> getDrops(
			@PathVariable Long id,
			@RequestParam(value = "count", required = false, defaultValue = "50") Integer count,
			@RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@RequestParam(value = "max_id", required = false) Long maxId,
			@RequestParam(value = "since_id", required = false) Long sinceId,
			@RequestParam(value = "date_from", required = false) String dateFrom,
			@RequestParam(value = "date_to", required = false) String dateTo,
			@RequestParam(value = "keywords", required = false) String keywords,
			@RequestParam(value = "channels", required = false) String channels,
			@RequestParam(value = "location", required = false) String location,
			@RequestParam(value = "photos", required = false) Boolean photos,
			Principal principal) {

		if (principal == null) {
			throw new UnauthorizedExpection(); 
		}

		List<ErrorField> errors = new ArrayList<ErrorField>();

		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("count", count);
		requestParams.put("page", page);
		
		if (maxId != null && maxId > 0) requestParams.put("maxId", maxId);
		if (sinceId != null && sinceId > 0) requestParams.put("sinceId", sinceId);

		// Dates
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		if (dateFrom != null) {
			try {
				requestParams.put("dateFrom", dateFormat.parse(dateFrom));
			} catch (ParseException e) {
				errors.add(new ErrorField("date_from", "invalid"));
			}
		}

		if (dateTo != null) {
			try {
				requestParams.put("dateTo", dateFormat.parse(dateTo));
			} catch (ParseException e) {
				errors.add(new ErrorField("date_to", "invalid"));
			}
		}

		// Keywords
		if (keywords != null) requestParams.put("keywords", keywords);

		// Channels
		if (channels != null && channels.trim().length() > 0) {
			List<String> channelsList = Arrays.asList(channels.split(","));
			requestParams.put("channels", channelsList);
		}

		if (location != null) requestParams.put("location", location);
		if (photos != null && photos == true) requestParams.put("photos", photos);
		
		if (!errors.isEmpty()) {
			BadRequestException exception = new BadRequestException("Invalid parameters");
			exception.setErrors(errors);
			throw exception;
		}

		return bucketService.getDrops(id, principal.getName(), requestParams);
	}

	/**
	 * Handler for deleting a {@link BucketDrop} from a bucket
	 * 
	 * @param id
	 * @param dropId
	 * @param principal
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteBucketDrop(@PathVariable Long id, @PathVariable Long dropId,
			Principal principal) {
		bucketService.deleteBucketDrop(id, dropId, principal.getName());
	}

	/**
	 * Handler for marking bucket drops as read
	 * 
	 * @param id
	 * @param dropId
	 * @param principal
	 */
	@RequestMapping(value = "{id}/drops/read/{dropId}", method = RequestMethod.PUT)
	@ResponseBody
	public void markDropAsRead(@PathVariable Long id, @PathVariable Long dropId, Principal principal) {
		bucketService.markDropAsRead(id, dropId, principal.getName());
	}
	
	/**
	 * Handler for adding a drop to a bucket
	 * 
	 * @param id
	 * @param dropId
	 * @param sourceDTO
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}", method = RequestMethod.PUT)
	@ResponseBody
	public void addDrop(@RequestBody DropSourceDTO sourceDTO, @PathVariable long id, 
			@PathVariable long dropId, Principal principal) {
		bucketService.addDrop(id, dropId, sourceDTO, principal.getName());
	}
	
	/**
	 * Handler for adding a tag to a drop that is in a bucket
	 * 
	 * @param id
	 * @param dropId
	 * @param createDTO
	 * @return
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}/tags", method = RequestMethod.POST)
	@ResponseBody
	public GetTagDTO addDropTag(@PathVariable Long id, @PathVariable Long dropId, 
			@RequestBody CreateTagDTO createDTO, Principal principal) {
		return bucketService.addDropTag(id, dropId, createDTO, principal.getName());
	}
	
	/**
	 * Handler for deleting a tag from a drop that is in a bucket 
	 * @param id
	 * @param dropId
	 * @param linkId
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}/tags/{tagId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteDropTag(@PathVariable Long id, @PathVariable Long dropId,
			@PathVariable Long tagId, Principal principal) {
		bucketService.deleteDropTag(id, dropId, tagId, principal.getName());
	}


	/**
	 * Handler for adding a link to a drop that is in a bucket
	 * 
	 * @param id
	 * @param dropId
	 * @param createDTO
	 * @return
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}/links", method = RequestMethod.POST)
	@ResponseBody
	public GetLinkDTO addDropLink(@PathVariable Long id, @PathVariable Long dropId, 
			@RequestBody CreateLinkDTO createDTO, Principal principal) {
		return bucketService.addDropLink(id, dropId, createDTO, principal.getName());
	}

	/**
	 * Handler for deleting a link from a drop that is in a bucket
	 * 
	 * @param id
	 * @param dropId
	 * @param linkId
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}/links/{linkId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteDropLink(@PathVariable Long id, @PathVariable Long dropId, 
			@PathVariable Long linkId, Principal principal) {
		bucketService.deleteDropLink(id, dropId, linkId, principal.getName());
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
	public GetPlaceDTO addDropPlace(@PathVariable Long id, @PathVariable Long dropId, 
			@RequestBody CreatePlaceDTO createDTO, Principal principal) {
		return bucketService.addDropPlace(id, dropId, createDTO, principal.getName());
	}

	/**
	 * Handler for deleting a place from a drop that is in a bucket
	 * 
	 * @param id
	 * @param dropId
	 * @param placeId
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}/places/{placeId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteDropPlace(@PathVariable Long id, @PathVariable Long dropId,
			@PathVariable Long placeId, Principal principal) {
		bucketService.deleteDropPlace(id, dropId, placeId, principal.getName());
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
	public GetCommentDTO addDropComment(@PathVariable Long id, @PathVariable Long dropId, 
			@RequestBody CreateCommentDTO createDTO, Principal principal) {
		
		return bucketService.addDropComment(id, dropId, createDTO, principal.getName());
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
	public List<GetCommentDTO> getDropComments(@PathVariable Long id, @PathVariable Long dropId,
			Principal principal) {
		return bucketService.getDropComments(id, dropId, principal.getName());
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
	public void deleteDropComment(@PathVariable Long id, @PathVariable Long dropId, 
			@PathVariable Long commentId, Principal principal) {
		bucketService.deleteDropComment(id, dropId, commentId, principal.getName());
	}
	
	/**
	 * Handler for adding a comment to a bucket
	 * 
	 * @param id
	 * @param createDTO
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "{id}/comments", method=RequestMethod.POST)
	@ResponseBody
	public GetCommentDTO addComment(@PathVariable Long id, @RequestBody CreateCommentDTO createDTO,
			Principal principal) {
		return bucketService.addBucketComment(id, createDTO, principal.getName());
	}
	
	/**
	 * Handler for fetching the list of bucket comments
	 * 
	 * @param id
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "{id}/comments", method=RequestMethod.GET)
	@ResponseBody
	public List<GetCommentDTO> getComments(@PathVariable Long id, Principal principal) {
		return bucketService.getBucketComments(id, principal.getName());
	}

	/**
	 * Handler for deleting a comment from a bucket
	 * 
	 * @param id
	 * @param commentId
	 * @param principal
	 */
	@RequestMapping(value = "{id}/comments/{commentId}", method=RequestMethod.DELETE)
	@ResponseBody
	public void deleteComment(@PathVariable Long id, @PathVariable Long commentId,
			Principal principal) {
		bucketService.deleteBucketComment(id, commentId, principal.getName());
	}
	
}
