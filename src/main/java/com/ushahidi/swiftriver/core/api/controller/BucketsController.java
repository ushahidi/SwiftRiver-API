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
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ushahidi.swiftriver.core.model.Account;

@Controller
@RequestMapping("/v1/buckets")
public class BucketsController {

	/**
	 * Handler for creating a new bucket.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public Account createAccount(@RequestBody Map<String, Object> body) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for obtaining a specific bucket.
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Account getBucket(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for modifying an existing bucket.
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public Account modifyBucket(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for deleting an existing bucket.
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseBody
	public Account deleteBucket(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for adding a collaborator to a bucket.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators", method = RequestMethod.POST)
	public Account addCollaborator(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for getting a bucket's collaborators.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators", method = RequestMethod.GET)
	public Account getCollaborators(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for modifying an existing collaborator.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators", method = RequestMethod.PUT)
	public Account modifyCollaborator(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for removing a bucket collaborator.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/collaborators", method = RequestMethod.DELETE)
	public Account deleteCollaborator(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for adding a follower to a bucket.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers", method = RequestMethod.POST)
	public Account addFollower(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for a bucket's followers.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers", method = RequestMethod.GET)
	public Account getFollowers(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for removing a follower.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/followers", method = RequestMethod.DELETE)
	public Account deleteFollower(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for adding a subscription to a bucket.
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
	 * Handler for getting subscriptions defined a bucket.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.GET)
	public Account getSubscriptions(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for modifying an existing subscription.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.PUT)
	public Account modifySubscription(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Handler for deleting a subscription form a bucket.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/subscriptions", method = RequestMethod.DELETE)
	public Account deleteSubscription(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Get drops in the bucket.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/drops", method = RequestMethod.GET)
	public Account getDrops(
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
	 * Handler for deleting a drop from a bucket.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(value = "/{id}/drops/{dropId}", method = RequestMethod.DELETE)
	public Account deleteDrop(@PathVariable Long id, @PathVariable Long dropId) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}
}
