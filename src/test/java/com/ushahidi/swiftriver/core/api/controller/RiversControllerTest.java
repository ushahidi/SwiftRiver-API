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

import org.junit.Test;

/**
 * Tests for {@link RiversController}
 * @author ekala
 *
 */
public class RiversControllerTest extends AbstractControllerTest {
	
	/**
	 * Test for {@link RiversController#getRiver(Long)}. Verifies that
	 * the payload contains all properties defined in the API spec for the
	 * GET rivers/:id resource
	 */
	@Test
	public void getRiver() throws Exception {
		this.mockMvc.perform(get("/v1/rivers/1"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.name").value("River Number One"))
			.andExpect(jsonPath("$.drop_count").value(0))
			.andExpect(jsonPath("$.drop_quota").value(10000))
			.andExpect(jsonPath("$.full").value(false))
			.andExpect(jsonPath("$.extension_count").value(0));
	}

}
