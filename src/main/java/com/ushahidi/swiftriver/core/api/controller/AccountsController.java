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

import com.ushahidi.swiftriver.core.api.dto.FollowerDTO;
import com.ushahidi.swiftriver.core.api.dto.GetAccountDTO;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.api.service.AccountService;
import com.ushahidi.swiftriver.core.model.Account;

@Controller
@RequestMapping("/v1/accounts")
public class AccountsController extends AbstractController {

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
	 * @throws NotFoundException 
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseBody
	public GetAccountDTO getAccountById(@PathVariable Long id, Principal principal) throws NotFoundException {
		return accountService.getAccountById(id, principal.getName());
	}
	
	/**
	 * Get account details for the specified id.
	 * 
	 * @param id
	 * @return
	 * @throws NotFoundException 
	 */
	@RequestMapping(method = RequestMethod.GET, params="account_path")
	@ResponseBody
	public GetAccountDTO getAccountByName(@RequestParam("account_path") String accountPath,
			Principal principal) throws NotFoundException {
		return accountService.getAccountByAccountPath(accountPath, principal.getName());
	}
	
	/**
	 * Get account details for the specified id.
	 * 
	 * @param id
	 * @return
	 * @throws NotFoundException 
	 */
	@RequestMapping(method = RequestMethod.GET, params="q")
	@ResponseBody
	public List<GetAccountDTO> searchAccounts(@RequestParam("q") String query) throws NotFoundException {
		return accountService.searchAccounts(query);
	}

	/**
	 * Get account details for the authenticating user.
	 * 
	 * @param id
	 * @return
	 * @throws NotFoundException 
	 */
	@RequestMapping(value = "/me", method = RequestMethod.GET)
	@ResponseBody
	public GetAccountDTO getAccount(Principal principal) throws NotFoundException {
		String username = principal.getName();
		return accountService.getAccountByUsername(username);
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

	/**
	 * Add a follower to an account
	 * 
	 * @param body
	 * @param id
	 */
	@RequestMapping(value = "/{id}/followers/{accountId}", method = RequestMethod.PUT)
	@ResponseBody
	public void addFollower(@PathVariable Long id, @PathVariable Long accountId) {
		accountService.addFollower(id, accountId);
	}

	@RequestMapping(value = "/{id}/followers", method = RequestMethod.GET)
	@ResponseBody
	public List<FollowerDTO> getFollowers(@PathVariable Long id,
			@RequestParam(value = "follower", required = false) Long accountId) {
		return accountService.getFollowers(id, accountId);
	}
	
	/**
	 * Handles deletion of an account from the list of followers
	 * 
	 * @param id
	 * @param accountId
	 */
	@RequestMapping(value = "{id}/followers/{accountId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteFollower(@PathVariable Long id, @PathVariable Long accountId) {
		accountService.deleteFollower(id, accountId);
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