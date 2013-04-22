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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.http.MediaType;

public class DropsControllerTest extends AbstractControllerTest {

	@Test
	public void createDrops() throws Exception {
		String postBody = "[" +
				"{\"source\": {\"origin_id\": \"the original identity id\"}, " +
				"\"original_id\": \"the original\"," +
				"\"channel\":\"manual channel\", " +
				"\"rivers\":[1], " +
				"\"channel_ids\":[2], " +
				"\"title\":\"the title\", " +
				"\"content\":\"the content\", " +
				"\"date_published\":\"Tue, 7 Mar 2013 03:08:45 +0000\", " +
				"\"original_url\":\"http://gizmodo.com/5995191/is-anyone-actually-going-to-buy-an-ibeetle\"" +
				"}]";

		this.mockMvc
				.perform(
						post("/v1/drops").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("admin")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(11));
	}
	
	@Test
	public void createDropWithMissingFields() throws Exception {
		String postBody = "[{}]";

		this.mockMvc
				.perform(
						post("/v1/drops").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("admin")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("[0].title"))
				.andExpect(jsonPath("$.errors[0].code").value("missing"))
				.andExpect(jsonPath("$.errors[1].field").value("[0].content"))
				.andExpect(jsonPath("$.errors[1].code").value("missing"))
				.andExpect(jsonPath("$.errors[2].field").value("[0].channel"))
				.andExpect(jsonPath("$.errors[2].code").value("missing"))
				.andExpect(jsonPath("$.errors[3].field").value("[0].date_published"))
				.andExpect(jsonPath("$.errors[3].code").value("missing"))
				.andExpect(jsonPath("$.errors[4].field").value("[0].original_id"))
				.andExpect(jsonPath("$.errors[4].code").value("missing"))
				.andExpect(jsonPath("$.errors[5].field").value("[0].identity.origin_id"))
				.andExpect(jsonPath("$.errors[5].code").value("missing"));
	}
}
