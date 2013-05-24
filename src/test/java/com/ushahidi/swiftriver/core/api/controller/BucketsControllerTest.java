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

import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
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
			.andExpect(jsonPath("$.[0].links").isArray())
			.andExpect(jsonPath("$.[0].buckets[0].name").exists())
			.andExpect(jsonPath("$[3].forms").exists())
			.andExpect(jsonPath("$[3].forms.id").value(hasItems("1","2")))
			.andExpect(jsonPath("$[3].forms.values[2].id").value("3"))
			.andExpect(jsonPath("$[3].forms.values[2].value").value("Kenyans"));;
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
	
	@Test
	public void getDropsFromDate() throws Exception {
		this.mockMvc.perform(get("/v1/buckets/1/drops")
				.param("date_from", "12-SEP-2012")
				.principal(getAuthentication("user1")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[*]").value(hasSize(4)));		
	}
	
	@Test
	public void getDropsToDate() throws Exception {
		this.mockMvc.perform(get("/v1/buckets/1/drops")
				.param("date_to", "12-SEP-2012")
				.principal(getAuthentication("user1")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[*]").value(hasSize(1)));		
	}
	
	@Test
	public void getDropsFromDateToDate() throws Exception {
		this.mockMvc.perform(get("/v1/buckets/1/drops")
				.param("date_from", "16-DEC-2012")
				.param("date_to", "02-FEB-2013")
				.principal(getAuthentication("user1")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[*]").value(hasSize(1)));		
	}

	/**
	 * Test for {@link BucketsController#deleteDrop(Long, Long, String)} where the
	 * drop exists in the target bucket
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteDrop() throws Exception {
		this.mockMvc.perform(delete("/v1/buckets/1/drops/3")
				.principal(getAuthentication("user1")))
			.andExpect(status().isOk());
	}
	
	/**
	 * Test for {@link BucketsController#deleteDrop(Long, Long, String)} where the
	 * drop does not exist in the target bucket. Should return a 404
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteNonExistentDrop() throws Exception {
		this.mockMvc.perform(delete("/v1/buckets/1/drops/500000")
				.principal(getAuthentication("user1")))
			.andExpect(status().isNotFound());
	}
	
	/**
	 * Test for {@link BucketsController#addDrop(long, long, java.security.Principal)}
	 * @throws Exception
	 */
	@Test
	public void addDrop() throws Exception {
		this.mockMvc.perform(put("/v1/buckets/1/drops/5")
				.contentType(MediaType.APPLICATION_JSON)
				.principal(authentication))
		.andExpect(status().isOk());
	}
	
	/**
	 * Test for {@link RiversController#getCollaborators(Long)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void getCollaborators() throws Exception {
		this.mockMvc
				.perform(get("/v1/buckets/1/collaborators"))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$").value(hasSize(2)))
				.andExpect(jsonPath("$[1].id").value(2))
				.andExpect(jsonPath("$[1].active").value(true))
				.andExpect(jsonPath("$[1].read_only").value(true))
				.andExpect(jsonPath("$[1].account.id").value(4))
				.andExpect(jsonPath("$[1].account.account_path").value("user2"))
				.andExpect(jsonPath("$[1].account.owner.name").value("User 2"))
				.andExpect(
						jsonPath("$[1].account.owner.avatar")
								.value("https://secure.gravatar.com/avatar/ee8ce7cae1c9d064b9a2c049ce4a1071?s=80&d=mm&r=g"));
	}

	@Test
	public void getUnknownCollaboratorsFromNonExistentRiver() throws Exception {
		this.mockMvc.perform(get("/v1/buckets/9999/collaborators")).andExpect(
				status().isNotFound());
	}

	@Test
	public void addCollaboratorToNonExistentRiver() throws Exception {

		String postBody = "{\"read_only\":true,\"account\":{\"id\": 9999}}";

		this.mockMvc.perform(
				post("/v1/buckets/1234/collaborators").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("user1"))).andExpect(
				status().isNotFound());
	}

	@Test
	public void addCollaboratorWithoutPermission() throws Exception {

		String postBody = "{\"read_only\":true,\"account\":{\"id\": 9999}}";

		this.mockMvc.perform(
				post("/v1/buckets/1/collaborators").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("user3"))).andExpect(
				status().isForbidden());
	}

	@Test
	public void addCollaboratorWithMissingAccountField() throws Exception {
		String postBody = "{\"read_only\":true}";

		this.mockMvc
				.perform(
						post("/v1/buckets/1/collaborators").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user3")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("account"))
				.andExpect(jsonPath("$.errors[0].code").value("missing"));
		;
	}

	@Test
	public void addCollaborator() throws Exception {

		String postBody = "{\"read_only\":true,\"account\":{\"id\": 5}}";

		this.mockMvc
				.perform(
						post("/v1/buckets/1/collaborators").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user1")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(greaterThan(3)));
	}

	@Test
	public void modifyCollaboratorInNonExistentRiver() throws Exception {

		String postBody = "{\"read_only\":true,\"account\":{\"id\": 9999}}";

		this.mockMvc.perform(
				put("/v1/buckets/1234/collaborators/1").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("user1"))).andExpect(
				status().isNotFound());
	}

	@Test
	public void modifyNonExistentCollaborator() throws Exception {

		String postBody = "{\"read_only\":true,\"account\":{\"id\": 9999}}";

		this.mockMvc.perform(
				put("/v1/buckets/1/collaborators/1234").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("user1"))).andExpect(
				status().isNotFound());
	}

	@Test
	public void modifyCollaboratorWithoutPermission() throws Exception {

		String postBody = "{\"read_only\":true,\"account\":{\"id\": 4}}";

		this.mockMvc.perform(
				put("/v1/buckets/1/collaborators/2").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("user3"))).andExpect(
				status().isForbidden());
	}

	@Test
	public void modifyCollaboratorWithMissingParameters() throws Exception {

		String postBody = "{}";

		this.mockMvc
				.perform(
						put("/v1/buckets/1/collaborators/1").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user1")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("read_only"))
				.andExpect(jsonPath("$.errors[0].code").value("missing"))
				.andExpect(jsonPath("$.errors[1].field").value("active"))
				.andExpect(jsonPath("$.errors[1].code").value("missing"));
	}

	@Test
	public void modifyCollaborator() throws Exception {

		String postBody = "{\"read_only\":true}";

		this.mockMvc.perform(
				put("/v1/buckets/1/collaborators/2").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("user1"))).andExpect(
				status().isOk());
	}

	/**
	 * Test for {@link RiversController#deleteCollaborator(Long, Long)}
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteCollaborator() throws Exception {
		this.mockMvc.perform(
				delete("/v1/buckets/1/collaborators/2").principal(
						getAuthentication("user1"))).andExpect(status().isOk());
	}

	@Test
	public void deleteCollaboratorInNonExistentRiver() throws Exception {
		this.mockMvc.perform(
				delete("/v1/buckets/1234/collaborators/2").principal(
						getAuthentication("user1"))).andExpect(
				status().isNotFound());
	}

	@Test
	public void deleteNonExistentCollaborator() throws Exception {
		this.mockMvc.perform(
				delete("/v1/buckets/1/collaborators/1234").principal(
						getAuthentication("user1"))).andExpect(
				status().isNotFound());
	}

	@Test
	public void deleteCollaboratorWithoutPermission() throws Exception {
		this.mockMvc.perform(
				delete("/v1/buckets/1/collaborators/1").principal(
						getAuthentication("user3"))).andExpect(
				status().isForbidden());
	}
	
	/**
	 * Test for {@link BucketsController#addDropTag(Long, Long, com.ushahidi.swiftriver.core.api.dto.CreateTagDTO)}
	 * @throws Exception
	 */
	@Test
	public void addDropTag() throws Exception {
		String tag = "{\"tag\": \"Precious Blood Riruta\", \"tag_type\": \"organization\"}";
		this.mockMvc.perform(post("/v1/buckets/1/drops/1/tags")
				.content(tag)
				.contentType(MediaType.APPLICATION_JSON)
				.principal(getAuthentication("user1")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.tag").value("Precious Blood Riruta"));
	}
	
	/**
	 * Test for {@link BucketsController#deleteDropTag(Long, Long, Long, java.security.Principal)}
	 * @throws Exception
	 */
	@Test
	public void deleteDropTag() throws Exception {
		this.mockMvc.perform(delete("/v1/buckets/1/drops/1/tags/1")
				.principal(getAuthentication("user1")))
			.andExpect(status().isOk());
	}
	
	/**
	 * Test for {@link BucketsController#addDropLink(Long, Long, com.ushahidi.swiftriver.core.api.dto.CreateLinkDTO, java.security.Principal)}
	 * @throws Exception
	 */
	@Test
	public void addDropLink() throws Exception {
		String link = "{\"url\": \"http://www.ushahidi.com\"}";
		this.mockMvc.perform(post("/v1/buckets/1/drops/1/links")
				.content(link)
				.contentType(MediaType.APPLICATION_JSON)
				.principal(getAuthentication("user1")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.url").value("http://www.ushahidi.com"));
	}
	
	/**
	 * Test for {@link BucketsController#deleteDropLink(Long, Long, Long, java.security.Principal)}
	 * @throws Exception
	 */
	@Test
	public void deleteDropLink() throws Exception {
		this.mockMvc.perform(delete("/v1/buckets/1/drops/1/links/2")
				.principal(getAuthentication("user1")))
			.andExpect(status().isOk());
	}
	
	/**
	 * Test for {@link BucketsController#addDropPlace(Long, Long, com.ushahidi.swiftriver.core.api.dto.CreatePlaceDTO, java.security.Principal)}
	 * @throws Exception
	 */
	@Test
	public void addDropPlace() throws Exception {
		String place = "{\"name\": \"Amsterdam\", \"longitude\":52.3667, \"latitude\": 4.88333 }";

		this.mockMvc.perform(post("/v1/buckets/1/drops/1/places")
				.content(place)
				.contentType(MediaType.APPLICATION_JSON)
				.principal(getAuthentication("user1")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Amsterdam"))
			.andExpect(jsonPath("$.longitude").value(52.3667));
	}
	
	/**
	 * Test for {@link BucketsController#deleteDropPlace(Long, Long, Long, java.security.Principal)}
	 * @throws Exception
	 */
	@Test
	public void deleteDropPlace() throws Exception {
		this.mockMvc.perform(delete("/v1/buckets/1/drops/1/places/2")
				.principal(getAuthentication("user1")))
			.andExpect(status().isOk());
	}

	@Test
	public void addDropComment() throws Exception {
		String comment = "{\"comment_text\": \"Bucket 1 Drop 1 Comment\"}";

		this.mockMvc.perform(post("/v1/buckets/1/drops/1/comments")
				.principal(getAuthentication("user1"))
				.content(comment)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.comment_text").value("Bucket 1 Drop 1 Comment"));
	}

	@Test
	public void deleteDropComment() throws Exception {
		this.mockMvc.perform(delete("/v1/buckets/1/drops/1/comments/3")
				.principal(getAuthentication("user1")))
			.andExpect(status().isOk());
	}
	
	@Test
	public void getDropComments() throws Exception {
		this.mockMvc.perform(get("/v1/buckets/1/drops/1/comments")
				.principal(getAuthentication("user1")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[*]").isArray());
	}
	
	/**
	 * Test for {@link BucketsController#addComment(Long, com.ushahidi.swiftriver.core.api.dto.CreateCommentDTO, java.security.Principal)}
	 * @throws Exception
	 */
	@Test
	public void addComment() throws Exception {
		String comment = "{\"comment_text\": \"Test user 1 comment\"}";	
		this.mockMvc.perform(post("/v1/buckets/1/comments")
				.principal(getAuthentication("user1"))
				.content(comment)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.comment_text").value("Test user 1 comment"));
	}
	
	
	/**
	 * Test for {@link BucketsController#getComments(Long, java.security.Principal)}
	 * @throws Exception
	 */
	@Test
	public void getComments() throws Exception {
		this.mockMvc.perform(get("/v1/buckets/1/comments")
				.principal(getAuthentication("user1")))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.[*]").isArray());
	}
	
	/**
	 * Test for {@link BucketsController#getComments(Long, java.security.Principal)} where
	 * the bucket is private and the user does not have permission to access it
	 * 
	 * @throws Exception
	 */
	@Test
	public void getCommentsWithoutPermissions() throws Exception {
		this.mockMvc.perform(get("/v1/buckets/2/comments")
				.principal(getAuthentication("user1")))
			.andExpect(status().isForbidden());
	}

	/**
	 * Test for {@link BucketsController#deleteComment(Long, Long, java.security.Principal)}
	 * @throws Exception
	 */
	@Test
	public void deleteComment() throws Exception {
		this.mockMvc.perform(delete("/v1/buckets/1/comments/2")
				.principal(getAuthentication("user1")))
			.andExpect(status().isOk());
	}
	
	@Test
	public void createDropForm() throws Exception {
		String postBody = "{\"id\":\"1234\",\"values\":[{\"id\":\"13\",\"value\":[\"English\",\"Swahili\"]},{\"id\":\"14\",\"value\":\"Politician\"},{\"id\":\"15\",\"value\":\"Kenyans\"}]}";

		this.mockMvc
				.perform(
						post("/v1/buckets/1/drops/1/forms").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user1")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value("1234"))
				.andExpect(jsonPath("$.values[0].id").value("13"));
	}

	@Test
	public void createDropFormWithoutPermission() throws Exception {
		String postBody = "{\"id\":\"1234\",\"values\":[{\"id\":\"13\",\"value\":[\"English\",\"Swahili\"]},{\"id\":\"14\",\"value\":\"Politician\"},{\"id\":\"15\",\"value\":\"Kenyans\"}]}";

		this.mockMvc.perform(
				post("/v1/buckets/1/drops/1/forms").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("user3"))).andExpect(
				status().isForbidden());
	}

	@Test
	public void createDropFormInNonExistentBucket() throws Exception {
		String postBody = "{\"id\":\"1234\",\"values\":[{\"id\":\"13\",\"value\":[\"English\",\"Swahili\"]},{\"id\":\"14\",\"value\":\"Politician\"},{\"id\":\"15\",\"value\":\"Kenyans\"}]}";

		this.mockMvc.perform(
				post("/v1/buckets/9999/drops/1/forms").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("user1"))).andExpect(
				status().isNotFound());
	}

	@Test
	public void createDropFormInNonExistentDrop() throws Exception {
		String postBody = "{\"id\":\"1234\",\"values\":[{\"id\":\"13\",\"value\":[\"English\",\"Swahili\"]},{\"id\":\"14\",\"value\":\"Politician\"},{\"id\":\"15\",\"value\":\"Kenyans\"}]}";

		this.mockMvc.perform(
				post("/v1/buckets/1/drops/9999/forms").content(postBody)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("user1"))).andExpect(
				status().isNotFound());
	}

	@Test
	public void modifyDropForm() throws Exception {
		String body = "{\"values\":[{\"id\":1,\"value\":[\"French\"]}]}";

		this.mockMvc
				.perform(
						put("/v1/buckets/1/drops/2/forms/1").content(body)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user1")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.values[0].value").value("French"));
	}

	@Test
	public void modifyDropFormWithoutPermission() throws Exception {
		String body = "{\"values\":[{\"id\":13,\"value\":[\"French\"]}]}";

		this.mockMvc.perform(
				put("/v1/buckets/1/drops/2/forms/1").content(body)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("user3"))).andExpect(
				status().isForbidden());
	}

	@Test
	public void modifyDropFormInNonExistentBucket() throws Exception {
		String body = "{\"values\":[{\"id\":13,\"value\":[\"French\"]}]}";

		this.mockMvc.perform(
				put("/v1/buckets/9999/drops/2/forms/1").content(body)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("user3"))).andExpect(
				status().isNotFound());
	}

	@Test
	public void modifyDropFormInNonExistentDrop() throws Exception {
		String body = "{\"values\":[{\"id\":13,\"value\":[\"French\"]}]}";

		this.mockMvc.perform(
				put("/v1/buckets/1/drops/9999/forms/1").content(body)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("user1"))).andExpect(
				status().isNotFound());
	}

	@Test
	public void modifyDropFormInNonExistentForm() throws Exception {
		String body = "{\"values\":[{\"id\":13,\"value\":[\"French\"]}]}";

		this.mockMvc.perform(
				put("/v1/buckets/1/drops/2/forms/9999").content(body)
						.contentType(MediaType.APPLICATION_JSON)
						.principal(getAuthentication("user1"))).andExpect(
				status().isNotFound());
	}

	@Test
	public void deleteDropForm() throws Exception {
		this.mockMvc.perform(
				delete("/v1/buckets/1/drops/2/forms/1").contentType(
						MediaType.APPLICATION_JSON).principal(
						getAuthentication("user1"))).andExpect(status().isOk());
	}

	@Test
	public void deleteDropFormWithoutPermsion() throws Exception {
		this.mockMvc.perform(
				delete("/v1/buckets/1/drops/2/forms/1").contentType(
						MediaType.APPLICATION_JSON).principal(
						getAuthentication("user3"))).andExpect(
				status().isForbidden());
	}
	
	@Test
	public void deleteDropFormFromNonExistentBucket() throws Exception {
		this.mockMvc.perform(
				delete("/v1/buckets/9999/drops/2/forms/1").contentType(
						MediaType.APPLICATION_JSON).principal(
						getAuthentication("user1"))).andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteDropFormForNonExistentDrop() throws Exception {
		this.mockMvc.perform(
				delete("/v1/buckets/1/drops/9999/forms/1").contentType(
						MediaType.APPLICATION_JSON).principal(
						getAuthentication("user1"))).andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteDropFormForNonExistentForm() throws Exception {
		this.mockMvc.perform(
				delete("/v1/buckets/1/drops/2/forms/9999").contentType(
						MediaType.APPLICATION_JSON).principal(
						getAuthentication("user1"))).andExpect(status().isNotFound());
	}
	
	public void markDropAsRead() throws Exception {
		this.mockMvc.perform(put("/v1/buckets/1/drops/read/1")
				.principal(getAuthentication("user1")))
			.andExpect(status().isOk());
	}
}
