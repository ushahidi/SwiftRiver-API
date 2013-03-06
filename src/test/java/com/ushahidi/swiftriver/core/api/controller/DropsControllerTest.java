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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Tests for {@link DropsController}
 * @author ekala
 *
 */
public class DropsControllerTest extends AbstractControllerTest {

	private Authentication authentication;

	@Before
	public void before() {
		authentication = new UsernamePasswordAuthenticationToken("user1", "password");
		SecurityContextHolder.getContext().setAuthentication(authentication);		
	}

	/**
	 * Tests {@link DropsController#getComments(long)}
	 * @throws Exception
	 */
	@Test
	public void getComments() throws Exception {
		this.mockMvc.perform(get("/v1/drops/1/comments"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.[*]").isArray())
			.andExpect(jsonPath("$.[0].account.email").value("user1@myswiftriver.com"));
	}
}
