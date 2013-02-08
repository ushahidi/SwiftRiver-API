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

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.http.MediaType;

import com.ushahidi.swiftriver.core.api.dto.BucketCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.BucketDTO;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BucketsControllerTest extends AbstractControllerTest {

	/**
	 * Test for {@link BucketsController#getBucket(Long)}
	 * @throws Exception
	 */
	@Test
	public void getBucket() throws Exception {
		this.mockMvc.perform(get("/v1/buckets/1"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("application/json;charset=UTF-8"))
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.name").value("Bucket Number One"));
	}
	
	/**
	 * Test for {@link BucketsController#modifyBucket(com.ushahidi.swiftriver.core.api.dto.BucketDTO, Long)}
	 * @throws Exception
	 */
	@Test
	public void modifyBucket() throws Exception {
		BucketDTO bucketData = new BucketDTO();
		bucketData.setName("Modified Test Bucket");
		bucketData.setPublished(true);
		
		this.mockMvc.perform(put("/v1/buckets/1")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
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
	public void deleteBucket() throws Exception {
		this.mockMvc.perform(delete("/v1/buckets/2"))
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
		this.mockMvc.perform(delete("/v1/buckets/5000"))
			.andExpect(status().isNotFound());
	}
	
	/**
	 * Test for {@link BucketsController#addCollaborator(BucketCollaboratorDTO, Long)}
	 * @throws Exception
	 */
	@Test
	public void addCollaborator() throws Exception {
		BucketCollaboratorDTO collaborator = new BucketCollaboratorDTO();

		collaborator.setId(1);
		collaborator.setActive(false);
		collaborator.setReadOnly(true);
		
		this.mockMvc.perform(post("/v1/buckets/1/collaborators")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsBytes(collaborator)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
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
	 * Test for {@link BucketsController#modifyCollaborator(BucketCollaboratorDTO, Long, Long)}
	 * @throws Exception
	 */
	@Test
	public void modifyCollaborator() throws Exception {
		BucketCollaboratorDTO collaborator = new BucketCollaboratorDTO();
		collaborator.setActive(true);
		collaborator.setReadOnly(false);

		this.mockMvc.perform(put("/v1/buckets/1/collaborators/2")
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(new ObjectMapper().writeValueAsBytes(collaborator)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.read_only").value(false));
	}
}
