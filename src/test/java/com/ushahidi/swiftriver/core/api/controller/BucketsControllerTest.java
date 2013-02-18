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

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dto.CreateCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateBucketDTO;

public class BucketsControllerTest extends AbstractControllerTest {

	private Authentication authentication;
	
	@Before
	public void before() {
		authentication = new UsernamePasswordAuthenticationToken("user1", "password");
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
	
	/**
	 * Test for {@link BucketsController#createBucket(CreateBucketDTO, java.security.Principal)}
	 * @throws Exception
	 */
	@Test
	public void createBucket() throws Exception {
		CreateBucketDTO createDTO = new CreateBucketDTO();
		createDTO.setName("Test Bucket 4");
		createDTO.setPublished(false);

		this.mockMvc.perform(post("/v1/buckets/")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.principal(authentication)
				.content(new ObjectMapper().writeValueAsBytes(createDTO)))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.name").value("Test Bucket 4"));
	}

	/**
	 * Test for {@link BucketsController#getBucket(Long)}
	 * @throws Exception
	 */
	@Test
	public void getBucket() throws Exception {
		this.mockMvc.perform(get("/v1/buckets/1")
				.principal(authentication))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.name").value("Bucket 1"))
			.andExpect(jsonPath("$.account.id").exists());
	}
	
	/**
	 * Test for {@link BucketsController#modifyBucket(com.ushahidi.swiftriver.core.api.dto.GetBucketDTO, Long)}
	 * @throws Exception
	 */
	@Test
	@Transactional
	public void modifyBucket() throws Exception {
		CreateBucketDTO bucketData = new CreateBucketDTO();
		bucketData.setName("Modified Test Bucket");
		bucketData.setPublished(true);
		
		this.mockMvc.perform(put("/v1/buckets/1")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.principal(authentication)
				.content(new ObjectMapper().writeValueAsBytes(bucketData)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Modified Test Bucket"))
			.andExpect(jsonPath("$.public").value(true));
	}
	
	/**
	 * Test for {@link BucketsController#deleteBucket(Long)}
	 * where the bucket with the specified id exists
	 * 
	 * @throws Exception
	 */
	@Test
	@Transactional
	public void deleteBucket() throws Exception {
		this.mockMvc.perform(delete("/v1/buckets/1")
				.principal(authentication))
			.andExpect(status().isOk());
	}
	
	/**
	 * Test for {@link BucketsController#deleteBucket(Long)} where the bucket
	 * with the specified id DOES NOT exist
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteNonExistentBucket() throws Exception {
		this.mockMvc.perform(delete("/v1/buckets/5000")
				.principal(authentication))
			.andExpect(status().isNotFound());
	}
	
	/**
	 * Test for {@link BucketsController#addCollaborator(CreateCollaboratorDTO, Long)}
	 * @throws Exception
	 */
	@Test
	@Transactional
	public void addCollaborator() throws Exception {
		CreateCollaboratorDTO collaborator = new CreateCollaboratorDTO();

		collaborator.setId(5);
		collaborator.setActive(false);
		collaborator.setReadOnly(true);
		
		this.mockMvc.perform(post("/v1/buckets/1/collaborators")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsBytes(collaborator))
				.principal(authentication))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(5))
			.andExpect(jsonPath("$.active").value(false))
			.andExpect(jsonPath("$.read_only").value(true));
	}

	/**
	 * Test for {@link BucketsController#getCollaborators(Long)}
	 * @throws Exception
	 */
	@Test
	public void getCollaborators() throws Exception {
		this.mockMvc.perform(get("/v1/buckets/1/collaborators"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[*]").isArray());
	}

	/**
	 * Test for {@link BucketsController#modifyCollaborator(GetCollaboratorDTO, Long, Long)}
	 * @throws Exception
	 */
	@Test
	@Transactional
	public void modifyCollaborator() throws Exception {
		CreateCollaboratorDTO collaborator = new CreateCollaboratorDTO();
		collaborator.setActive(true);
		collaborator.setReadOnly(false);

		this.mockMvc.perform(put("/v1/buckets/1/collaborators/3")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsBytes(collaborator))
				.principal(authentication))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.read_only").value(false));
	}
	
	/**
	 * Test for {@link BucketsController#getDrops(Long, Integer, Long, Long, java.util.Date, java.util.Date, String, String, String)}
	 * @throws Exception
	 */
	@Test
	public void getDrops() throws Exception {
		this.mockMvc.perform(get("/v1/buckets/1/drops")
				.principal(authentication))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.[0].tags").isArray())
			.andExpect(jsonPath("$.[0].places").isArray())
			.andExpect(jsonPath("$.[0].media").isArray())
			.andExpect(jsonPath("$.[0].links").isArray());
	}
	
	/**
	 * Test for {@link BucketsController#getDrops(Long, Integer, Long, Long, java.util.Date, java.util.Date, String, String, String)}
	 * where the <code>max_id</code> parameter has been specified
	 * 
	 * @throws Exception
	 */
	@Test
	public void getDropsByMaxId() throws Exception {
		this.mockMvc.perform(get("/v1/buckets/1/drops")
				.param("max_id", "31")
				.principal(authentication))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[0].id").value(5));
	}
	
	/**
	 * Test for {@link BucketsController#getDrops(Long, Integer, Long, Long, java.util.Date, java.util.Date, String, String, String)}
	 * where the <code>since_id</code> parameter has been specified
	 * 
	 * @throws Exception
	 */
	@Test
	public void getDropsBySinceId() throws Exception {
		this.mockMvc.perform(get("/v1/buckets/1/drops")
				.param("since_id", "4")
				.principal(authentication))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[0].id").value(5));
	}
	
	/**
	 * Test for {@link BucketsController#getDrops(Long, Integer, Long, Long, java.util.Date, java.util.Date, String, String, String)}
	 * where the <code>channels</code> parameter has been specified
	 * 
	 * @throws Exception
	 */
	@Test
	public void getDropsByChannel() throws Exception {
		this.mockMvc.perform(get("/v1/buckets/1/drops")
				.param("channels", "instagram,facebook")
				.principal(authentication))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[0].id").doesNotExist());
	}
	
	/**
	 * Test for {@link BucketsController#deleteDrop(Long, Long)} where the
	 * drop exists in the target bucket
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteDrop() throws Exception {
		this.mockMvc.perform(delete("/v1/buckets/1/drops/3"))
			.andExpect(status().isOk());
	}
	
	/**
	 * Test for {@link BucketsController#deleteDrop(Long, Long)} where the
	 * drop does not exist in the target bucket. Should return a 404
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteNonExistentDrop() throws Exception {
		this.mockMvc.perform(delete("/v1/buckets/1/drops/500000"))
			.andExpect(status().isNotFound());
	}
}
