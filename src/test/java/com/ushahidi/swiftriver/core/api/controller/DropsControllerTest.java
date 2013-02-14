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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
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
			.andExpect(jsonPath("$.[0].account.account_path").value("user1"));
	}
	
	/**
	 * Tests {@link DropsController#deleteComment(long, long, java.security.Principal)}
	 */
	@Test
	public void deleteComment() throws Exception {		
		this.mockMvc.perform(delete("/v1/drops/1/comments/2")
				.principal(authentication))
			.andExpect(status().isOk());
	}
	
	/**
	 * Tests {@link DropsController#deleteComment(long, long, java.security.Principal)} where
	 * the comment or drop id does not exist
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteNonExistentComment() throws Exception {
		this.mockMvc.perform(delete("/v1/drops/1/comments/2000")
				.principal(authentication))
			.andExpect(status().isNotFound());
	}
	
	/**
	 * Tests {@link DropsController#addComment(java.util.Map, Long, java.security.Principal)}
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void addComment() throws Exception {
		Object[][] commentData = {{"comment_text", "Drop 10 comment"}};
		Map<String, Object> commentText = ArrayUtils.toMap(commentData);

		this.mockMvc.perform(post("/v1/drops/10/comments")
				.principal(authentication)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsBytes(commentText )))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.account.account_path").value("user1"));
	}
	
	/**
	 * Tests {@link DropsController#addLink(Map, Long, java.security.Principal)}
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void addLink() throws Exception {
		Object[][] linkData = {{"url", "http://www.nation.co.ke/Tech/Airtel+could+drop+low+cost+strategy+/-/1017288/1355924/-/lpa7uez/-/"}};
		Map<String, Object> link = ArrayUtils.toMap(linkData);
		
		this.mockMvc.perform(post("/v1/drops/10/links")
				.principal(authentication)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsBytes(link)))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.url").value("http://www.nation.co.ke/Tech/Airtel+could+drop+low+cost+strategy+/-/1017288/1355924/-/lpa7uez/-/"));
	}
	
	/**
	 * Test for {@link DropsController#deleteLink(long, long, java.security.Principal)}
	 * @throws Exception
	 */
	@Test
	public void deleteLink() throws Exception {
		this.mockMvc.perform(delete("/v1/drops/5/links/10")
				.principal(authentication))
			.andExpect(status().isOk());
	}

	/**
	 * Test for {@link DropsController#deleteLink(long, long, java.security.Principal)}
	 * where the link doesn't belong to the drop
	 * @throws Exception
	 */
	@Test
	public void deleteNonExistentLink() throws Exception {
		this.mockMvc.perform(delete("/v1/drops/5/links/1000")
				.principal(authentication))
			.andExpect(status().isNotFound());
	}
	
	/**
	 * Test for {@link DropsController#addPlace(Map, long, java.security.Principal)}
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void addPlace() throws Exception {
		Object[][] placeData = {
				{"name", "Amsterdam"},
				{"latitude", 52.3667},
				{"longitude", 4.88333}
		};
		Map<String, Object> place = ArrayUtils.toMap(placeData);
		
		this.mockMvc.perform(post("/v1/drops/1/places")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsBytes(place))
				.principal(authentication))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.name").value("Amsterdam"))
			.andExpect(jsonPath("$.longitude").value(4.88333));		                     
	}
	
	/**
	 * Test for {@link DropsController#deletePlace(long, long, java.security.Principal)}
	 * @throws Exception
	 */
	@Test
	public void deletePlace() throws Exception {
		this.mockMvc.perform(delete("/v1/drops/1/places/5")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.principal(authentication))
			.andExpect(status().isOk());
	}
}
