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
import java.util.Date;
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
import com.ushahidi.swiftriver.core.api.dto.CreateLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.CreatePlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateTagDTO;
import com.ushahidi.swiftriver.core.api.dto.FollowerDTO;
import com.ushahidi.swiftriver.core.api.dto.GetBucketDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetPlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetTagDTO;
import com.ushahidi.swiftriver.core.api.exception.UnauthorizedExpection;
import com.ushahidi.swiftriver.core.api.service.BucketService;

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
	 * @param createDTO
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators", method = RequestMethod.POST)
	@ResponseBody
	public GetCollaboratorDTO addCollaborator(@RequestBody CreateCollaboratorDTO createDTO,
			@PathVariable Long id, Principal principal) {
		return bucketService.addCollaborator(id, createDTO, principal.getName());
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
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators/{accountId}", method = RequestMethod.PUT)
	@ResponseBody
	public GetCollaboratorDTO modifyCollaborator(@RequestBody CreateCollaboratorDTO body,
			@PathVariable Long id, @PathVariable Long accountId, Principal principal) {
		return bucketService.modifyCollaborator(id, accountId, body, principal.getName());
	}

	/**
	 * Handler for removing a bucket collaborator.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators/{accountId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteCollaborator(@PathVariable Long id, @PathVariable Long accountId) {
		bucketService.deleteCollaborator(id, accountId, null);
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
			@RequestParam(value = "max_id", required = false) Long maxId,
			@RequestParam(value = "since_id", required = false) Long sinceId,
			@RequestParam(value = "date_from", required = false) Date dateFrom,
			@RequestParam(value = "date_to", required = false) Date dateTo,
			@RequestParam(value = "keywords", required = false) String keywords,
			@RequestParam(value = "channels", required = false) String channels,
			@RequestParam(value = "location", required = false) String location,
			@RequestParam(value = "photos", required = false) Boolean photos,
			Principal principal) {

		if (principal == null) {
			throw new UnauthorizedExpection(); 
		}

		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("count", count);
		
		if (maxId != null) requestParams.put("max_id", maxId);
		if (sinceId != null) requestParams.put("since_id", sinceId);
		if (dateFrom != null) requestParams.put("date_from", dateFrom);
		if (dateTo != null) requestParams.put("dae_to", dateTo);
		if (keywords != null) requestParams.put("keywords", keywords);
		if (channels != null) requestParams.put("channels", channels);
		if (location != null) requestParams.put("location", location);
		if (photos != null && photos == true) requestParams.put("photos", photos);

		return bucketService.getDrops(id, principal.getName(), requestParams);
	}

	/**
	 * Handler for deleting a drop from a bucket.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteDrop(@PathVariable Long id, @PathVariable Long dropId) {
		bucketService.deleteDrop(id, dropId);
	}
	
	/**
	 * Handler for adding a drop to a bucket
	 * 
	 * @param id
	 * @param dropId
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}", method = RequestMethod.PUT)
	@ResponseBody
	public void addDrop(@PathVariable long id, @PathVariable long dropId, Principal principal) {
		bucketService.addDrop(id, dropId, principal.getName());
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
			@RequestBody CreateTagDTO createDTO) {
		return bucketService.addDropTag(id, dropId, createDTO);
	}
	
	/**
	 * Handler for deleting a tag from a drop that is in a bucket 
	 * @param id
	 * @param dropId
	 * @param linkId
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}/tags/{tagId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteDropTag(@PathVariable Long id, @PathVariable Long dropId, @PathVariable Long tagId) {
		bucketService.deleteDropTag(id, dropId, tagId);
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
			@RequestBody CreateLinkDTO createDTO) {
		return bucketService.addDropLink(id, dropId, createDTO);
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
	public void deleteDropLink(@PathVariable Long id, @PathVariable Long dropId, @PathVariable Long linkId) {
		bucketService.deleteDropLink(id, dropId, linkId);
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
			@RequestBody CreatePlaceDTO createDTO) {
		return bucketService.addDropPlace(id, dropId, createDTO);
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
	public void deleteDropPlace(@PathVariable Long id, @PathVariable Long dropId, @PathVariable Long placeId) {
		bucketService.deleteDropPlace(id, dropId, placeId);
	}
	
	
}
