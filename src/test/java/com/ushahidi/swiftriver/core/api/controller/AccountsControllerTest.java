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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

public class AccountsControllerTest extends AbstractControllerTest {

	@Test
	public void getAccountById() throws Exception {
		this.mockMvc
				.perform(
						get("/v1/accounts/1").principal(
								getAuthentication("admin")))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	public void getAccountByNonExistentId() throws Exception {
		this.mockMvc.perform(
				get("/v1/accounts/9999").principal(getAuthentication("user1")))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getAccountByName() throws Exception {
		this.mockMvc
				.perform(
						get("/v1/accounts?account_path=user1").principal(
								getAuthentication("admin")))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.id").value(3));
	}

	@Test
	public void getAccountByEmail() throws Exception {
		this.mockMvc
				.perform(
						get("/v1/accounts?email=user3@myswiftriver.com")
								.principal(getAuthentication("user1")))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.id").value(5));
	}

	@Test
	public void getAccountWithTokenForAccountPath() throws Exception {
		this.mockMvc
				.perform(
						get("/v1/accounts?account_path=user1&token=1")
								.principal(getAuthentication("user1")))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.id").value(3))
				.andExpect(jsonPath("$.token").exists());
	}

	@Test
	public void getAccountWithTokenForEmail() throws Exception {
		this.mockMvc
				.perform(
						get("/v1/accounts?email=user3@myswiftriver.com&token=1")
								.principal(getAuthentication("user1")))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.id").value(5))
				.andExpect(jsonPath("$.token").exists());
	}

	@Test
	public void searchAccounts() throws Exception {
		this.mockMvc
				.perform(
						get("/v1/accounts?q=my").principal(
								getAuthentication("user1")))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].id").value(1));
	}

	@Test
	public void getAuthenticatedUserAccount() throws Exception {
		Authentication authentication = new UsernamePasswordAuthenticationToken(
				"user1", "password");
		SecurityContextHolder.getContext().setAuthentication(authentication);

		this.mockMvc
				.perform(get("/v1/accounts/me").principal(authentication))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.id").value(3))
				.andExpect(jsonPath("$.account_path").value("user1"))
				.andExpect(jsonPath("$.active").value(true))
				.andExpect(jsonPath("$.private").value(false))
				.andExpect(jsonPath("$.river_quota_remaining").value(20))
				.andExpect(jsonPath("$.follower_count").value(2))
				.andExpect(jsonPath("$.following_count").value(1))
				.andExpect(jsonPath("$.owner.name").value("User 1"))
				.andExpect(
						jsonPath("$.owner.email").value(
								"user1@myswiftriver.com"))
				.andExpect(jsonPath("$.owner.username").value("user1"))
				.andExpect(
						jsonPath("$.owner.avatar")
								.value("https://secure.gravatar.com/avatar/373329f529512d8898e8a8aeea3a7675?s=80&d=mm&r=g"))
				.andExpect(jsonPath("$.rivers").exists())
				.andExpect(jsonPath("$.rivers[0].id").exists())
				.andExpect(jsonPath("$.rivers[0].name").exists())
				.andExpect(jsonPath("$.rivers[0].follower_count").exists())
				.andExpect(jsonPath("$.rivers[0].public").exists())
				.andExpect(jsonPath("$.rivers[0].active").exists())
				.andExpect(jsonPath("$.rivers[0].drop_count").exists())
				.andExpect(jsonPath("$.rivers[0].drop_quota").exists())
				.andExpect(jsonPath("$.rivers[0].full").exists())
				.andExpect(jsonPath("$.rivers[0].extension_count").exists())
				.andExpect(jsonPath("$.buckets").exists())
				.andExpect(jsonPath("$.buckets[0].id").exists())
				.andExpect(jsonPath("$.buckets[0].name").exists())
				.andExpect(jsonPath("$.buckets[0].description").exists())
				.andExpect(jsonPath("$.buckets[0].follower_count").exists())
				.andExpect(jsonPath("$.buckets[0].public").exists())
				.andExpect(jsonPath("$.buckets[0].drop_count").exists())
				.andExpect(jsonPath("$.forms").exists());
	}

	@Test
	public void createAccount() throws Exception {
		String postBody = "{\"name\":\"Dexter Morgan\",\"account_path\":\"dexter\",\"email\":\"dexter@example.com\",\"password\":\"mirabilis\"}";

		this.mockMvc
				.perform(
						post("/v1/accounts").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user1")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(7))
				.andExpect(jsonPath("$.active").value(false))
				.andExpect(jsonPath("$.token").exists());
	}

	@Test
	public void createAccountWithoutName() throws Exception {
		String postBody = "{\"account_path\":\"dexter\",\"email\":\"dexter@example.com\",\"password\":\"mirabilis\"}";

		this.mockMvc
				.perform(
						post("/v1/accounts").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user1")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("name"))
				.andExpect(jsonPath("$.errors[0].code").value("missing"));
	}

	@Test
	public void createAccountWithoutAccountPath() throws Exception {
		String postBody = "{\"name\":\"Dexter Morgan\",\"email\":\"dexter@example.com\",\"password\":\"mirabilis\"}";

		this.mockMvc
				.perform(
						post("/v1/accounts").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user1")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("account_path"))
				.andExpect(jsonPath("$.errors[0].code").value("missing"));
	}

	@Test
	public void createAccountWithoutEmail() throws Exception {
		String postBody = "{\"name\":\"Dexter Morgan\",\"account_path\":\"dexter\",\"password\":\"mirabilis\"}";

		this.mockMvc
				.perform(
						post("/v1/accounts").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user1")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("email"))
				.andExpect(jsonPath("$.errors[0].code").value("missing"));
	}

	@Test
	public void createAccountWithoutPassword() throws Exception {
		String postBody = "{\"name\":\"Dexter Morgan\",\"account_path\":\"dexter\",\"email\":\"dexter@example.com\"}";

		this.mockMvc
				.perform(
						post("/v1/accounts").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user1")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("password"))
				.andExpect(jsonPath("$.errors[0].code").value("missing"));
	}

	@Test
	public void modifyNonExistentAccount() throws Exception {
		String postBody = "{\"account_path\":\"dexter\"}";

		this.mockMvc.perform(
				put("/v1/accounts/9999").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("default"))).andExpect(
				status().isNotFound());
	}

	@Test
	public void modifyAccount() throws Exception {
		String postBody = "{\"account_path\":\"dexter\", \"private\":true, \"river_quota_remaining\":93, \"owner\": {\"name\": \"Papa Smurf\", \"email\": \"example@example.com\"}}";

		this.mockMvc
				.perform(
						put("/v1/accounts/1").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("admin")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.account_path").value("dexter"))
				.andExpect(jsonPath("$.private").value(true))
				.andExpect(jsonPath("$.river_quota_remaining").value(93))
				.andExpect(jsonPath("$.owner.name").value("Papa Smurf"))
				.andExpect(
						jsonPath("$.owner.email").value("example@example.com"));
	}

	@Test
	public void modifyAccountPathToDuplicate() throws Exception {
		String postBody = "{\"account_path\":\"user1\"}";

		this.mockMvc
				.perform(
						put("/v1/accounts/1").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("admin")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("account_path"))
				.andExpect(jsonPath("$.errors[0].code").value("duplicate"));
	}

	@Test
	public void modifyAccountEmailToDuplicate() throws Exception {
		String postBody = "{\"owner\":{\"email\":\"user1@myswiftriver.com\"}}";

		this.mockMvc
				.perform(
						put("/v1/accounts/1").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("admin")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("owner.email"))
				.andExpect(jsonPath("$.errors[0].code").value("duplicate"));
	}

	@Test
	public void changePassword() throws Exception {
		String postBody = "{\"owner\":{\"current_password\": \"password\", \"password\": \"password2\"}}";
		this.mockMvc.perform(
				put("/v1/accounts/1").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("admin"))).andExpect(
				status().isOk());
	}

	@Test
	public void changePasswordWithInvalidCurrentPassword() throws Exception {
		String postBody = "{\"owner\":{\"current_password\": \"invalidpassword\", \"password\": \"password2\"}}";
		this.mockMvc.perform(
				put("/v1/accounts/1").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("admin"))).andExpect(
				status().isBadRequest());
	}

	@Test
	public void resetPassword() throws Exception {
		String postBody = "{\"token\":\"18012e9d-0e26-47f5-848f-ad81c96fc3f4\",\"owner\":{\"password\":\"new password\"}}";

		this.mockMvc.perform(
				put("/v1/accounts/6").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("user4"))).andExpect(
				status().isOk());
	}

	@Test
	public void resetPasswordWithoutOwnerArray() throws Exception {
		String postBody = "{\"token\":\"15f8cc2c-e7c1-4298-9f41-f42d1de3043e\"}";

		this.mockMvc
				.perform(
						put("/v1/accounts/5").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user3")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("password"))
				.andExpect(jsonPath("$.errors[0].code").value("missing"));
	}

	@Test
	public void resetasswordWithoutPasswordInOwnerArray() throws Exception {
		String postBody = "{\"token\":\"15f8cc2c-e7c1-4298-9f41-f42d1de3043e\",\"owner\":{}}";

		this.mockMvc
				.perform(
						put("/v1/accounts/5").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user3")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("password"))
				.andExpect(jsonPath("$.errors[0].code").value("missing"));
	}

	@Test
	public void resetPasswordWithoutToken() throws Exception {
		String postBody = "{\"owner\":{\"password\":\"new password\"}}";

		this.mockMvc
				.perform(
						put("/v1/accounts/1").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("admin")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("token"))
				.andExpect(jsonPath("$.errors[0].code").value("missing"));
	}

	@Test
	public void resetPasswordWithInvalidTokenToken() throws Exception {
		String postBody = "{\"token\":\"This is invalid\",\"owner\":{\"password\":\"new password\"}}";

		this.mockMvc
				.perform(
						put("/v1/accounts/1").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("admin")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("token"))
				.andExpect(jsonPath("$.errors[0].code").value("invalid"));
	}

	@Test
	public void resetPasswordWithExpiredTokenToken() throws Exception {
		String postBody = "{\"token\":\"4f3cf69c18da-f848-5f74-62e0-d9e21081\",\"owner\":{\"password\":\"new password\"}}";

		this.mockMvc
				.perform(
						put("/v1/accounts/1").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("admin")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("token"))
				.andExpect(jsonPath("$.errors[0].code").value("invalid"));
	}

	@Test
	public void activateAccount() throws Exception {
		String postBody = "{\"token\":\"18012e9d-0e26-47f5-848f-ad81c96fc3f4\"}";

		this.mockMvc
				.perform(
						put("/v1/accounts/6").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user4")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.active").value(true))
				.andExpect(jsonPath("$.owner.active").value(true));
	}

	@Test
	public void activateAccountWithExpiredToken() throws Exception {
		String postBody = "{\"token\":\"4f3cf69c18da-f848-5f74-62e0-d9e21081\"}";

		this.mockMvc
				.perform(
						put("/v1/accounts/6").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user4")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("token"))
				.andExpect(jsonPath("$.errors[0].code").value("invalid"));
	}

	@Test
	public void activateAccountWithNonExistentToken() throws Exception {
		String postBody = "{\"token\":\"this can't be a token\"}";

		this.mockMvc
				.perform(
						put("/v1/accounts/6").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user4")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("token"))
				.andExpect(jsonPath("$.errors[0].code").value("invalid"));
	}

	@Test
	public void getApps() throws Exception {
		this.mockMvc
				.perform(
						get("/v1/accounts/1/apps").principal(
								getAuthentication("admin")))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].client_id").value("trusted-client"))
				.andExpect(jsonPath("$[0].client_secret").value("somesecret"))
				.andExpect(
						jsonPath("$[0].redirect_uri").value(
								"http://example.com/oauth/redirect"))
				.andExpect(jsonPath("$[0].name").value("my app"))
				.andExpect(
						jsonPath("$[0].description").value(
								"my app's description"))
				.andExpect(jsonPath("$[0].homepage").value("my app's homepage"));
	}

	@Test
	public void getUnknownAccountApps() throws Exception {
		this.mockMvc.perform(
				get("/v1/accounts/9999/apps").principal(
						getAuthentication("user1"))).andExpect(
				status().isNotFound());
	}

	@Test
	public void getOtherUserApps() throws Exception {
		this.mockMvc.perform(
				get("/v1/accounts/1/apps")
						.principal(getAuthentication("user1"))).andExpect(
				status().isForbidden());
	}

	@Test
	public void createApp() throws Exception {
		String postBody = "{\"name\":\"My App\",\"description\":\"App Description\",\"redirect_uri\":\"http://example.com\",\"homepage\":\"http://example.com\"}";

		this.mockMvc
				.perform(
						post("/v1/accounts/1/apps").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("admin")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(2))
				.andExpect(jsonPath("$.client_id").exists())
				.andExpect(jsonPath("$.client_secret").exists());
	}

	@Test
	public void createAppInUnknownAccount() throws Exception {
		String postBody = "{\"name\":\"My App\",\"description\":\"App Description\",\"redirect_uri\":\"http://example.com\",\"homepage\":\"http://example.com\"}";

		this.mockMvc.perform(
				post("/v1/accounts/9999/apps").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("user1"))).andExpect(
				status().isNotFound());
	}

	@Test
	public void createAppWithoutPermission() throws Exception {
		String postBody = "{\"name\":\"My App\",\"description\":\"App Description\",\"redirect_uri\":\"http://example.com\",\"homepage\":\"http://example.com\"}";

		this.mockMvc.perform(
				post("/v1/accounts/1/apps").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("user1"))).andExpect(
				status().isForbidden());
	}

	@Test
	public void createAppWithMissingParameters() throws Exception {
		String postBody = "{\"name\":\"My App\",\"description\":\"App Description\",\"homepage\":\"http://example.com\"}";

		this.mockMvc
				.perform(
						post("/v1/accounts/1/apps").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("admin")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("redirect_uri"))
				.andExpect(jsonPath("$.errors[0].code").value("missing"));

		postBody = "{\"redirect_uri\":\"redirect uri\",\"description\":\"App Description\",\"homepage\":\"http://example.com\"}";

		this.mockMvc
				.perform(
						post("/v1/accounts/1/apps").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("admin")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("name"))
				.andExpect(jsonPath("$.errors[0].code").value("missing"));
	}

	@Test
	public void deleteApp() throws Exception {
		this.mockMvc.perform(
				delete("/v1/accounts/1/apps/1").contentType(
						MediaType.APPLICATION_JSON).principal(
						getAuthentication("admin"))).andExpect(status().isOk());
	}

	@Test
	public void deleteUnknownApp() throws Exception {
		this.mockMvc.perform(
				delete("/v1/accounts/1/apps/9999").contentType(
						MediaType.APPLICATION_JSON).principal(
						getAuthentication("admin"))).andExpect(
				status().isNotFound());
	}

	@Test
	public void deleteAppWithoutPermssion() throws Exception {
		this.mockMvc.perform(
				delete("/v1/accounts/1/apps/1").contentType(
						MediaType.APPLICATION_JSON).principal(
						getAuthentication("user1"))).andExpect(
				status().isForbidden());
	}

	@Test
	public void modifyApp() throws Exception {
		String postBody = "{\"name\":\"My App Renamed\",\"description\":\"App Description Renamed\",\"redirect_uri\":\"http://example.com/renamed\",\"homepage\":\"http://example.com/home/renamed\"}";

		this.mockMvc
				.perform(
						put("/v1/accounts/1/apps/1").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("admin")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("My App Renamed"))
				.andExpect(
						jsonPath("$.description").value(
								"App Description Renamed"))
				.andExpect(
						jsonPath("$.redirect_uri").value(
								"http://example.com/renamed"))
				.andExpect(
						jsonPath("$.homepage").value(
								"http://example.com/home/renamed"))
				.andExpect(jsonPath("$.client_id").value("trusted-client"))
				.andExpect(jsonPath("$.client_secret").value("somesecret"));
	}

	@Test
	public void modifyAppWithoutPermission() throws Exception {
		String postBody = "{\"name\":\"My App Renamed\",\"description\":\"App Description Renamed\",\"redirect_uri\":\"http://example.com/renamed\",\"homepage\":\"http://example.com/home/renamed\"}";

		this.mockMvc.perform(
				put("/v1/accounts/1/apps/1").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("user1"))).andExpect(
				status().isForbidden());
	}

	@Test
	public void modifyUnknownApp() throws Exception {
		String postBody = "{\"name\":\"My App Renamed\",\"description\":\"App Description Renamed\",\"redirect_uri\":\"http://example.com/renamed\",\"homepage\":\"http://example.com/home/renamed\"}";

		this.mockMvc.perform(
				put("/v1/accounts/1/apps/9999").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("admin"))).andExpect(
				status().isNotFound());
	}

	@Test
	public void modifyAppInUnknownAccount() throws Exception {
		String postBody = "{\"name\":\"My App Renamed\",\"description\":\"App Description Renamed\",\"redirect_uri\":\"http://example.com/renamed\",\"homepage\":\"http://example.com/home/renamed\"}";

		this.mockMvc.perform(
				put("/v1/accounts/9999/apps/1").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("admin"))).andExpect(
				status().isNotFound());
	}

	@Test
	public void modifyAppPartially() throws Exception {
		String postBody = "{\"name\":\"apps's new name\"}";

		this.mockMvc
				.perform(
						put("/v1/accounts/1/apps/1").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("admin")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.description").value(notNullValue()));
	}

	@Test
	public void modifyAppResetCredentials() throws Exception {
		String postBody = "{\"client_id\":\"reset\"}";

		this.mockMvc
				.perform(
						put("/v1/accounts/1/apps/1").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("admin")))
				.andExpect(status().isOk())
				.andExpect(
						jsonPath("$.client_id").value(
								not(equalTo("trusted-client"))))
				.andExpect(jsonPath("$.client_id").value(not(equalTo("reset"))))
				.andExpect(
						jsonPath("$.client_secret").value(
								not(equalTo("somesecret"))));
	}

	/**
	 * Test for {@link AccountsController#deleteFollower(Long, Long)} where the
	 * accountId parameter is null
	 * 
	 * @throws Exception
	 */
	@Test
	public void getFollowers() throws Exception {
		this.mockMvc
				.perform(get("/v1/accounts/3/followers"))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.[0].account_path").exists());
	}

	/**
	 * Test for {@link AccountsController#getFollowers(Long, Long)} where the
	 * accountId parameter has been specified
	 * 
	 * @throws Exception
	 */
	@Test
	public void getFollowersByAccount() throws Exception {
		this.mockMvc
				.perform(get("/v1/accounts/3/followers").param("follower", "5"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.[0].account_path").value("user3"));
	}

	/**
	 * Test for {@link AccountsController#addFollower(Long, Long)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void addFollower() throws Exception {
		this.mockMvc.perform(put("/v1/accounts/5/followers/4")).andExpect(
				status().isOk());
	}

	/**
	 * Test for {@link AccountsController#addFollower(Long, Long)} where the
	 * {@link Account} id of the follower is the same as the id of the
	 * {@link Account} to be followed
	 * 
	 * @throws Exception
	 */
	public void addInvalidFollower() throws Exception {
		this.mockMvc.perform(put("/v1/accounts/3/followers/3")).andExpect(
				status().isBadRequest());
	}

	/**
	 * Test for {@link AccountsController#deleteFollower(Long, Long)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteFollower() throws Exception {
		this.mockMvc.perform(delete("/v1/accounts/3/followers/5")).andExpect(
				status().isOk());
	}

	/**
	 * Test for {@link AccountsController#deleteFollower(Long, Long)} where the
	 * specified account id doesn't exist the target account
	 * 
	 * @throws Exception
	 */
	public void deleteNonExistentFollower() throws Exception {
		this.mockMvc.perform(delete("/v1/accounts/3/followers/1000"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getActivities() throws Exception {
		this.mockMvc
				.perform(
						get("/v1/accounts/3/activities").principal(
								getAuthentication("user3")))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].id").value("8"))
				.andExpect(jsonPath("$[0].date_added").exists())
				.andExpect(jsonPath("$[0].account.id").value(3))
				.andExpect(jsonPath("$[0].action").value("invite"))
				.andExpect(jsonPath("$[0].action_on").value("bucket_collaborator"))
				.andExpect(jsonPath("$[0].action_on_obj.id").value(2))
				.andExpect(jsonPath("$[0].action_on_obj.account.owner.name").value("User 2"))
				.andExpect(jsonPath("$[0].action_on_obj.bucket.id").value(1))
				
				// Check river property set for the river_collaborator activity
				.andExpect(jsonPath("$[1].action_on_obj.river.id").value(1));
	}
	
	@Test
	public void getActivitiesFromAccountWithoutActivities() throws Exception {
		this.mockMvc
				.perform(
						get("/v1/accounts/4/activities").principal(
								getAuthentication("user3")))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void getActivitiesOfFollowedAccounts() throws Exception {
		this.mockMvc
				.perform(
						get("/v1/accounts/activities").principal(
								getAuthentication("user1")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$[0].id").value("6"));
	}
}
