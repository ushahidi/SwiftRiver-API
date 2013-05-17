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
import java.util.ArrayList;
import java.util.List;

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

import com.ushahidi.swiftriver.core.api.dto.ActivateAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateClientDTO;
import com.ushahidi.swiftriver.core.api.dto.FollowerDTO;
import com.ushahidi.swiftriver.core.api.dto.GetAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.GetActivityDTO;
import com.ushahidi.swiftriver.core.api.dto.GetClientDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyClientDTO;
import com.ushahidi.swiftriver.core.api.dto.ResetPasswordDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ErrorField;
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
	@ResponseBody
	public GetAccountDTO createAccount(@RequestBody CreateAccountDTO body) {

		List<ErrorField> errors = new ArrayList<ErrorField>();
		if (body.getName() == null) {
			errors.add(new ErrorField("name", "missing"));
		}

		if (body.getAccountPath() == null) {
			errors.add(new ErrorField("account_path", "missing"));
		}

		if (body.getEmail() == null) {
			errors.add(new ErrorField("email", "missing"));
		}

		if (body.getPassword() == null) {
			errors.add(new ErrorField("password", "missing"));
		}

		if (!errors.isEmpty()) {
			BadRequestException e = new BadRequestException(
					"Invalid parameter.");
			e.setErrors(errors);
			throw e;
		}

		return accountService.createAccount(body);
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
	public GetAccountDTO getAccountById(@PathVariable Long id,
			Principal principal) throws NotFoundException {
		return accountService.getAccountById(id, principal.getName());
	}

	/**
	 * Get account details for the specified account_path
	 * 
	 * @param id
	 * @return
	 * @throws NotFoundException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "account_path")
	@ResponseBody
	public GetAccountDTO getAccountByName(
			@RequestParam("account_path") String accountPath,
			@RequestParam(value = "token", required = false) boolean getToken,
			Principal principal) throws NotFoundException {
		return accountService.getAccountByAccountPath(accountPath, getToken,
				principal.getName());
	}

	/**
	 * Get account details for the specified email
	 * 
	 * @param id
	 * @return
	 * @throws NotFoundException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "email")
	@ResponseBody
	public GetAccountDTO getAccountByEmail(@RequestParam("email") String email,
			@RequestParam(value = "token", required = false) boolean getToken,
			Principal principal) throws NotFoundException {
		return accountService.getAccountByEmail(email, getToken,
				principal.getName());
	}

	/**
	 * Get account details for the specified id.
	 * 
	 * @param id
	 * @return
	 * @throws NotFoundException
	 */
	@RequestMapping(method = RequestMethod.GET, params = "q")
	@ResponseBody
	public List<GetAccountDTO> searchAccounts(@RequestParam("q") String query,
			Principal principal) throws NotFoundException {
		return accountService.searchAccounts(query, principal.getName());
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
	public GetAccountDTO getAccount(Principal principal)
			throws NotFoundException {
		String username = principal.getName();
		return accountService.getAccountByUsername(username);
	}

	/**
	 * Modify the given account
	 * 
	 * @param body
	 * @param accountId
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "/{accountId}", method = RequestMethod.PUT)
	@ResponseBody
	public GetAccountDTO modifyAccount(@RequestBody ModifyAccountDTO body,
			@PathVariable Long accountId, Principal principal) {
		return accountService.modifyAccount(accountId, body,
				principal.getName());
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

	/**
	 * Get a list of followers for the given account
	 * 
	 * @param id
	 * @param accountId
	 * @return
	 */
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
	public void deleteFollower(@PathVariable Long id,
			@PathVariable Long accountId) {
		accountService.deleteFollower(id, accountId);
	}

	/**
	 * Get a list of followers the account is following
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}/following", method = RequestMethod.GET)
	@ResponseBody
	public Account getFollowing(@PathVariable Long id) {
		throw new UnsupportedOperationException("Method Not Yet Implemented");
	}

	/**
	 * Get Activities by the given accoun
	 * 
	 * @param accountId
	 * @param principal
	 * @return
	 */
	@RequestMapping(value = "/{accountId}/activities", method = RequestMethod.GET)
	@ResponseBody
	public List<GetActivityDTO> getActivities(
			@PathVariable Long accountId,
			Principal principal) {
		return accountService.getActivities(accountId, principal.getName());
	}
	
	/**
	 * Get activities from Accounts the logged in user is following
	 * 
	 * @param principal
	 * @param count
	 * @param lastId
	 * @param newer
	 * @return
	 */
	@RequestMapping(value = "/timeline", method = RequestMethod.GET)
	@ResponseBody
	public List<GetActivityDTO> getTimeline(
			Principal principal,
			@RequestParam(value = "count", required = false, defaultValue = "50") Integer count,
			@RequestParam(value = "last_id", required = false) Long lastId,
			@RequestParam(value = "newer", required = false) Boolean newer) {
		return accountService.getTimeline(count, lastId, newer, principal.getName());
	}

	@RequestMapping(value = "/{accountId}/apps", method = RequestMethod.POST)
	@ResponseBody
	public GetClientDTO createApp(@RequestBody CreateClientDTO body,
			@PathVariable Long accountId, Principal principal) {

		List<ErrorField> errors = new ArrayList<ErrorField>();
		if (body.getName() == null) {
			errors.add(new ErrorField("name", "missing"));
		}

		if (body.getRedirectUri() == null) {
			errors.add(new ErrorField("redirect_uri", "missing"));
		}

		if (!errors.isEmpty()) {
			BadRequestException e = new BadRequestException(
					"Invalid parameter.");
			e.setErrors(errors);
			throw e;
		}

		return accountService
				.createClient(accountId, body, principal.getName());
	}

	@RequestMapping(value = "/{accountId}/apps", method = RequestMethod.GET)
	@ResponseBody
	public List<GetClientDTO> getApps(@PathVariable Long accountId,
			Principal principal) {
		return accountService.getClients(accountId, principal.getName());
	}

	@RequestMapping(value = "/{accountId}/apps/{appId}", method = RequestMethod.PUT)
	@ResponseBody
	public GetClientDTO modifyApp(@RequestBody ModifyClientDTO body,
			@PathVariable Long accountId, @PathVariable Long appId,
			Principal principal) {
		return accountService.modifyClient(accountId, appId, body,
				principal.getName());
	}

	@RequestMapping(value = "/{accountId}/apps/{appId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteApp(@PathVariable Long accountId,
			@PathVariable Long appId, Principal principal) {
		accountService.deleteApp(accountId, appId, principal.getName());
	}
	
	/**
	 * Handles activation of newly created accounts
	 * 
	 * @param body
	 */
	@RequestMapping(value = "/activate", method = RequestMethod.POST)
	@ResponseBody
	public void activateAccount(@RequestBody ActivateAccountDTO body) {
		accountService.activateAccount(body);
	}
	
	/**
	 * Handles resetting of passwords following a <code>forgot_password</code>
	 * request i.e. the client must have submitted a <code>forgot_password</code>
	 * request in order to be issue with a secret token which is then used
	 * to validate the <code>reset_password</code> operation  
	 * 
	 * @param resetPasswordDto
	 */
	@RequestMapping(value="/reset_password", method=RequestMethod.POST)
	@ResponseBody
	public void resetPassword(@RequestBody ResetPasswordDTO resetPasswordDto) {
		accountService.resetPassword(resetPasswordDto);
	}
}