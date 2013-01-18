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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.service.AccountService;

@Controller
@RequestMapping("/v1/accounts")
public class AccountsController {

	final Logger logger = LoggerFactory.getLogger(AccountsController.class);

	@Autowired
	private AccountService accountService;

	/**
	 * Handler for creating a new account.
	 * 
	 * @param body
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	public Account createAccount(@RequestBody Map<String, Object> body) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Get account details for the specified id.
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getAccount(@PathVariable Long id) {
		return accountService.getAccount(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	@ResponseBody
	public Account modifyAccount(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented ");
	}

	@RequestMapping(value = "/{id}/verify", method = RequestMethod.POST)
	@ResponseBody
	public Account verifyAccount(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	@RequestMapping(value = "/{id}/followers", method = RequestMethod.POST)
	@ResponseBody
	public Account addFollower(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	@RequestMapping(value = "/{id}/followers", method = RequestMethod.GET)
	@ResponseBody
	public Account getFollowers(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	@RequestMapping(value = "/{id}/following", method = RequestMethod.GET)
	@ResponseBody
	public Account getFollowing(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	@RequestMapping(value = "/{id}/activities", method = RequestMethod.GET)
	@ResponseBody
	public Account getActivities(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	@RequestMapping(value = "/{id}/apps", method = RequestMethod.POST)
	@ResponseBody
	public Account createApp(@RequestBody Map<String, Object> body,
			@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	@RequestMapping(value = "/{id}/apps", method = RequestMethod.GET)
	@ResponseBody
	public Account getApps(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	@RequestMapping(value = "/{id}/apps/{appId}", method = RequestMethod.PUT)
	@ResponseBody
	public Account modifyApp(@RequestBody Map<String, Object> body,
			@PathVariable Long id, @PathVariable Long appId) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	@RequestMapping(value = "/{id}/apps/{appId}", method = RequestMethod.DELETE)
	@ResponseBody
	public Account deleteApp(@PathVariable Long id, @PathVariable Long appId) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}
}