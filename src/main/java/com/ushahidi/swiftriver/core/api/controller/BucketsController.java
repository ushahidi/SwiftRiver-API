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

import com.ushahidi.swiftriver.core.api.dto.BucketDTO;
import com.ushahidi.swiftriver.core.api.dto.CollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.FollowerDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO;
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
	public BucketDTO createBucket(@RequestBody BucketDTO body) {
		return bucketService.createBucket(body);
	}

	/**
	 * Handler for obtaining a specific bucket.
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public BucketDTO getBucket(@PathVariable Long id) {
		return bucketService.getBucket(id);
	}

	/**
	 * Handler for modifying an existing bucket.
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public BucketDTO modifyBucket(@RequestBody BucketDTO body,
			@PathVariable Long id) {
		return bucketService.modifyBucket(id, body);
	}

	/**
	 * Handler for deleting an existing bucket.
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteBucket(@PathVariable Long id) {
		bucketService.deleteBucket(id);
	}

	/**
	 * Handler for adding a collaborator to a bucket.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators", method = RequestMethod.POST)
	@ResponseBody
	public CollaboratorDTO addCollaborator(@RequestBody CollaboratorDTO body,
			@PathVariable Long id) {
		return bucketService.addCollaborator(id, body);
	}

	/**
	 * Handler for getting a bucket's collaborators.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators", method = RequestMethod.GET)
	@ResponseBody
	public List<CollaboratorDTO> getCollaborators(@PathVariable Long id) {
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
	public CollaboratorDTO modifyCollaborator(@RequestBody CollaboratorDTO body,
			@PathVariable Long id, @PathVariable Long accountId) {
		return bucketService.modifyCollaborator(id, accountId, body);
	}

	/**
	 * Handler for removing a bucket collaborator.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators/{accountId}", method = RequestMethod.DELETE)
	public void deleteCollaborator(@PathVariable Long id, @PathVariable Long accountId) {
		bucketService.deleteCollaborator(id, accountId);
	}

	/**
	 * Handler for adding a follower to a bucket.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers", method = RequestMethod.POST)
	public void addFollower(@RequestBody FollowerDTO body,
			@PathVariable Long id) {
		bucketService.addFollower(id, body);
	}

	/**
	 * Handler for a bucket's followers.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers", method = RequestMethod.GET)
	@ResponseBody
	public List<FollowerDTO> getFollowers(@PathVariable Long id) {
		return bucketService.getFollowers(id);
	}

	/**
	 * Handler for removing a follower.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers/{accountId}", method = RequestMethod.DELETE)
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
			@RequestParam(value = "location", required = false) String location) {
		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("count", count);
		
		if (maxId != null) requestParams.put("max_id", maxId);
		if (sinceId != null) requestParams.put("since_id", sinceId);
		if (dateFrom != null) requestParams.put("date_from", dateFrom);
		if (dateTo != null) requestParams.put("dae_to", dateTo);
		if (keywords != null) requestParams.put("keywords", keywords);
		if (channels != null) requestParams.put("channels", channels);
		if (location != null) requestParams.put("location", location);

		return bucketService.getDrops(id, requestParams);
	}

	/**
	 * Handler for deleting a drop from a bucket.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}", method = RequestMethod.DELETE)
	public void deleteDrop(@PathVariable Long id, @PathVariable Long dropId) {
		bucketService.deleteDrop(id, dropId);
	}
}
