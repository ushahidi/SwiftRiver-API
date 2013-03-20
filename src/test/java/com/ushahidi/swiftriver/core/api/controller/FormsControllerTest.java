/**
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ushahidi.swiftriver.core.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Ushahidi, Inc
 * 
 */
public class FormsControllerTest extends AbstractControllerTest {

	@Test
	public void createForm() throws Exception {
		String postBody = "{\"name\":\"Dangerous Speech Categorisation\",\"fields\":[{\"title\":\"Language\",\"description\":\"Language the audience is being addressed in\",\"type\":\"mutiple\",\"required\":false,\"options\":[\"English\",\"Swahili\",\"Luo\",\"Kalenjin\",\"Luhya\",\"Kikuyu\",\"Sheng\",\"Other\"]},{\"title\":\"Speaker\",\"description\":\"Description of the speaker\",\"type\":\"select\",\"required\":false,\"options\":[\"Politician\",\"Journalist\",\"Blogger\",\"Community Leader\",\"Anonymous Commenter\",\"Identifiable Commenter\",\"Public Figure\"]},{\"title\":\"Target Audience\",\"description\":\"Audience most likely to react to this statement/article\",\"type\":\"text\",\"required\":true}]}";

		this.mockMvc
				.perform(
						post("/v1/forms").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user1")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").exists());
	}

	@Test
	@Transactional
	public void createFormWithoutName() throws Exception {
		String postBody = "{\"fields\":[{\"title\":\"Language\",\"description\":\"Language the audience is being addressed in\",\"type\":\"mutiple\",\"required\":false,\"options\":[\"English\",\"Swahili\",\"Luo\",\"Kalenjin\",\"Luhya\",\"Kikuyu\",\"Sheng\",\"Other\"]},{\"title\":\"Speaker\",\"description\":\"Description of the speaker\",\"type\":\"select\",\"required\":false,\"options\":[\"Politician\",\"Journalist\",\"Blogger\",\"Community Leader\",\"Anonymous Commenter\",\"Identifiable Commenter\",\"Public Figure\"]},{\"title\":\"Target Audience\",\"description\":\"Audience most likely to react to this statement/article\",\"type\":\"text\",\"required\":true}]}";

		this.mockMvc
				.perform(
						post("/v1/forms").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user1")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(jsonPath("$.errors[0].field").value("name"))
				.andExpect(jsonPath("$.errors[0].code").value("missing"));
	}

	public void createFormWithFieldsMissingRequiredValues() throws Exception {
		String postBody = "{\"name\":\"Dangerous Speech Categorisation\",\"fields\":[{\"description\":\"Language the audience is being addressed in\",\"required\":false,\"options\":[\"English\",\"Swahili\",\"Luo\",\"Kalenjin\",\"Luhya\",\"Kikuyu\",\"Sheng\",\"Other\"]}]}";

		this.mockMvc
				.perform(
						post("/v1/forms").content(postBody)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user1")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").exists())
				.andExpect(jsonPath("$.errors").exists())
				.andExpect(jsonPath("$.errors").isArray())
				.andExpect(
						jsonPath("$.errors[0].field").value("fields[0].title"))
				.andExpect(jsonPath("$.errors[0].code").value("missing"))
				.andExpect(
						jsonPath("$.errors[1].field").value("fields[0].type"))
				.andExpect(jsonPath("$.errors[1].code").value("missing"));
	}

	@Test
	public void modifyForm() throws Exception {
		String body = "{\"name\":\"Form Renamed\"}";

		this.mockMvc
				.perform(
						put("/v1/forms/1").content(body)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user1")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name").value("Form Renamed"));
	}

	@Test
	public void modifyNonExistentForm() throws Exception {
		String body = "{\"name\":\"Non existent form\"}";

		this.mockMvc
				.perform(
						put("/v1/forms/9999").content(body)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user1")))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").exists());
	}

	@Test
	public void modifyFormWithoutPermission() throws Exception {
		String body = "{\"name\":\"Not allowed\"}";

		this.mockMvc
				.perform(
						put("/v1/forms/1").content(body)
								.contentType(MediaType.APPLICATION_JSON)
								.principal(getAuthentication("user3")))
				.andExpect(status().isForbidden())
				.andExpect(jsonPath("$.message").exists());
	}

	@Test
	public void deleteForm() throws Exception {
		this.mockMvc.perform(
				delete("/v1/forms/1").principal(getAuthentication("user1")))
				.andExpect(status().isOk());
	}
	
	@Test
	public void deleteNonExistentForm() throws Exception {
		this.mockMvc.perform(
				delete("/v1/forms/9999").principal(getAuthentication("user1")))
				.andExpect(status().isNotFound());
	}
	
	@Test
	public void deleteFormWithoutPermission() throws Exception {
		this.mockMvc.perform(
				delete("/v1/forms/1").principal(getAuthentication("user2")))
				.andExpect(status().isForbidden());
	}
}
