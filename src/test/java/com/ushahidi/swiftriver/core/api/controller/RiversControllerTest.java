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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.http.MediaType;

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
			.andExpect(jsonPath("$.extension_count").value(0))
			.andExpect(jsonPath("$.channels[*]").isArray());
	}
	
	/**
	 * Test or {@link RiversController#modifyRiver(java.util.Map, Long)}
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void modifyRiver() throws Exception {
		Object[][] riverData = {
				{"id", 1},
				{"name","Modified River Name"},
				{"public", true},
				{"active", false}
		};
		Map<String, Object> riverMap = ArrayUtils.toMap(riverData);
		
		this.mockMvc.perform(put("/v1/rivers/1")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsBytes(riverMap)))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.name").value("Modified River Name"))
			.andExpect(jsonPath("$.active").value(false));
	}
	
	/**
	 * Test for {@link RiversController#deleteRiver(Long)}
	 * @throws Exception
	 */
	@Test
	public void deleteRiver() throws Exception {
		this.mockMvc.perform(delete("/v1/rivers/2"))
			.andExpect(status().isOk());
	}
	
	/**
	 * Test for {@link RiversController#deleteRiver(Long)} where
	 * the specified does not exist in which case a 404 should
	 * be returned
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteNonExistentRiver() throws Exception {
		this.mockMvc.perform(delete("/v1/rivers/500"))
			.andExpect(status().isNotFound());
	}

	/**
	 * Test for {@link RiversController#getChannels(Long)}
	 * @throws Exception
	 */
	@Test
	public void getChannels() throws Exception {
		this.mockMvc.perform(get("/v1/rivers/1/channels"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.[0].name").value("rss"));
	}

	/**
	 * Test for {@link RiversController#deleteChannel(Long, Integer)} where
	 * both the river and channel exist
	 * @throws Exception
	 */
	@Test
	public void deleteChannel() throws Exception {
		this.mockMvc.perform(delete("/v1/rivers/1/channels/2"))
			.andExpect(status().isOk());
	}
	
	/**
	 * Test for {@link RiversController#deleteChannel(Long, Integer)} where
	 * the river is non-existent
	 * @throws Exception
	 */
	@Test
	public void deleteChannelFromNonExistentRiver() throws Exception {
		this.mockMvc.perform(delete("/v1/rivers/500/channels/3"))
			.andExpect(status().isNotFound());
	}
	
	/**
	 * Test for {@link RiversController#getCollaborators(Long)}
	 * @throws Exception
	 */
	@Test
	public void getCollaborators() throws Exception {
		this.mockMvc.perform(get("/v1/rivers/1/collaborators"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"));
	}
	
	/**
	 * Test for {@link RiversController#addCollaborator(Map, Long)}
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void addCollaborator() throws Exception {
		Map<String, Object> accountData = ArrayUtils.toMap(
				new Object[][]{{"id", 7}});

		// Test collaborator payload
		Object[][] collaboratorData = {
				{"read_only", false},
				{"account", accountData }
		};

		Map<String, Object> collaboratorMap = ArrayUtils.toMap(collaboratorData);
		this.mockMvc.perform(post("/v1/rivers/1/collaborators")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsBytes(collaboratorMap )))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.account.id").value(7))
			.andExpect(jsonPath("$.account.account_path").value("admin5"));			
	}
	
	/**
	 * Test for {@link RiversController#deleteCollaborator(Long, Long)}
	 * @throws Exception
	 */
	@Test
	public void deleteCollaborator() throws Exception {
		this.mockMvc.perform(delete("/v1/rivers/1/collaborators/2"))
			.andExpect(status().isOk());
	}

}
