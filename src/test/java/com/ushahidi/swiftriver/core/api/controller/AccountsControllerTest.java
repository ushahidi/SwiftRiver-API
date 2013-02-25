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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AccountsControllerTest extends AbstractControllerTest {

	@Test
	public void getAccountById() throws Exception {
		this.mockMvc
				.perform(get("/v1/accounts/1")
						.principal(getAuthentication("user1")))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.id").value(1));
	}
	
	@Test
	public void getAccountByNonExistentId() throws Exception {
		this.mockMvc
				.perform(get("/v1/accounts/9999")
						.principal(getAuthentication("user1")))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void getAccountByName() throws Exception {
		this.mockMvc
				.perform(get("/v1/accounts?account_path=user1")
						.principal(getAuthentication("admin")))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.id").value(3));
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
				.andExpect(jsonPath("$.owner.email").value("user1@myswiftriver.com"))
				.andExpect(jsonPath("$.owner.username").value("user1"))
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
				.andExpect(jsonPath("$.buckets[0].drop_count").exists());
	}
	
	/**
	 * Test for {@link AccountsController#deleteFollower(Long, Long)} where the
	 * accountId parameter is null
	 * 
	 * @throws Exception
	 */
	@Test
	public void getFollowers() throws Exception {
		this.mockMvc.perform(get("/v1/accounts/3/followers"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.[0].account_path").exists());
	}

	/**
	 * Test for {@link AccountsController#getFollowers(Long, Long)} where the
	 * accountId parameter has been specified
	 * @throws Exception
	 */
	@Test
	public void getFollowersByAccount() throws Exception {
		this.mockMvc.perform(get("/v1/accounts/3/followers")
				.param("follower", "5"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[0].account_path").value("user3"));
	}

	/**
	 * Test for {@link AccountsController#addFollower(Long, Long)}
	 * @throws Exception
	 */
	@Test
	public void addFollower() throws Exception {
		this.mockMvc.perform(put("/v1/accounts/5/followers/4"))
			.andExpect(status().isOk());
	}
	
	/**
	 * Test for {@link AccountsController#addFollower(Long, Long)} where
	 * the {@link Account} id of the follower is the same as the  id
	 * of the {@link Account} to be followed
	 * 
	 * @throws Exception
	 */
	public void addInvalidFollower() throws Exception {
		this.mockMvc.perform(put("/v1/accounts/3/followers/3"))
			.andExpect(status().isBadRequest());
	}
	
	/**
	 * Test for {@link AccountsController#deleteFollower(Long, Long)}
	 * @throws Exception
	 */
	@Test
	public void deleteFollower() throws Exception {
		this.mockMvc.perform(delete("/v1/accounts/3/followers/5"))
			.andExpect(status().isOk());
	}
	
	/**
	 * Test for {@link AccountsController#deleteFollower(Long, Long)} where the
	 * specified account id doesn't exist the target account
	 * @throws Exception
	 */
	public void deleteNonExistentFollower() throws Exception {
		this.mockMvc.perform(delete("/v1/accounts/3/followers/1000"))
			.andExpect(status().isNotFound());
	}
}
