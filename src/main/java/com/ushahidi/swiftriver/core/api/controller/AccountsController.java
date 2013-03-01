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

import com.ushahidi.swiftriver.core.api.dto.CreateAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateClientDTO;
import com.ushahidi.swiftriver.core.api.dto.GetAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.GetClientDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyClientDTO;
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
	public GetAccountDTO getAccountById(@PathVariable Long id)
			throws NotFoundException {
		return accountService.getAccountById(id);
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
			@RequestParam(value = "token", required = false) boolean getToken)
			throws NotFoundException {
		return accountService.getAccountByName(accountPath, getToken);
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
	public GetAccountDTO getAccountByEmail(
			@RequestParam("email") String email,
			@RequestParam(value = "token", required = false) boolean getToken)
			throws NotFoundException {
		return accountService.getAccountByEmail(email, getToken);
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
	public List<GetAccountDTO> searchAccounts(@RequestParam("q") String query)
			throws NotFoundException {
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
	 * @return
	 */
	@RequestMapping(value = "/{accountId}", method = RequestMethod.PUT)
	@ResponseBody
	public GetAccountDTO modifyAccount(@RequestBody ModifyAccountDTO body,
			@PathVariable Long accountId) {
		return accountService.modifyAccount(accountId, body);
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
		
		return accountService.createClient(accountId, body, principal.getName());
	}

	@RequestMapping(value = "/{accountId}/apps", method = RequestMethod.GET)
	@ResponseBody
	public List<GetClientDTO> getApps(@PathVariable Long accountId, Principal principal) {
		return accountService.getClients(accountId, principal.getName());
	}

	@RequestMapping(value = "/{accountId}/apps/{appId}", method = RequestMethod.PUT)
	@ResponseBody
	public GetClientDTO modifyApp(@RequestBody ModifyClientDTO body,
			@PathVariable Long accountId, @PathVariable Long appId, Principal principal) {
		return accountService.modifyClient(accountId, appId, body, principal.getName());
	}

	@RequestMapping(value = "/{accountId}/apps/{appId}", method = RequestMethod.DELETE)
	@ResponseBody
	public void deleteApp(@PathVariable Long accountId, @PathVariable Long appId, Principal principal) {
		accountService.deleteApp(accountId, appId, principal.getName());
	}
}