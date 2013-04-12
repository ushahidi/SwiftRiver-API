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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

public class SearchControllerTest extends AbstractControllerTest {
	
	@Test
	public void searchDrops() throws Exception {
		this.mockMvc.perform(get("/v1/search?q=droplet"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.[*]").value(hasSize(10)));
	}
	
	/**
	 * Test drops search where the <code>count</code> parameter has been
	 * specified
	 * 
	 * @throws Exception
	 */
	@Test
	public void searchDropsWithCount() throws Exception {
		this.mockMvc.perform(get("/v1/search?q=droplet&count=3"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[*]").value(hasSize(3)));
		
	}
	
	/**
	 * Tests search where the search type (<code>type</code> parameter)
	 * is invalid
	 * 
	 * @throws Exception
	 */
	@Test
	public void searchWithInvalidSearchType() throws Exception {
		this.mockMvc.perform(get("/v1/search?q=droplet&type=invalid"))
		.andExpect(status().isBadRequest());
		
	}
	
	/**
	 * Tests drops search with a non-existent page number. The request should
	 * return an empty result
	 * 
	 * @throws Exception
	 */
	@Test
	public void searchDropsNonExistentPageNumber() throws Exception {
		this.mockMvc.perform(get("/v1/search?q=droplet&page=10"))
			.andExpect(status().isNotFound());
	}
	
	@Test
	public void searchBuckets() throws Exception {
		this.mockMvc.perform(get("/v1/search?q=bucket&type=buckets"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[*]").value(hasSize(2)));
	}
	
	@Test
	public void searchRivers() throws Exception {
		this.mockMvc.perform(get("/v1/search?q=river&type=rivers"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[*]").value(hasSize(1)));
		
	}
	
	@Test
	public void searchUsers() throws Exception {
		this.mockMvc.perform(get("/v1/search?q=user&type=users"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[*]").value(hasSize(4)));
	}
	
	@Test
	public void searchUserWithPrivateAsset() throws Exception {
		this.mockMvc.perform(get("/v1/search?q=user1&type=users"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[*]").value(hasSize(1)))
			.andExpect(jsonPath("$.[0].rivers.[*]").value(hasSize(1)));
	}
}
