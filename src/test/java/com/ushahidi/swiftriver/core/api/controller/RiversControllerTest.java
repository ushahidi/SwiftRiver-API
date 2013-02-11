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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;

import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ushahidi.swiftriver.core.util.DateUtil;

public class RiversControllerTest extends AbstractControllerTest {

	@Test
	public void getAccountByNonExistentId() throws Exception {
		this.mockMvc.perform(get("/v1/rivers/9999")).andExpect(
				status().isNotFound());
	}

	@Test
	public void getRiverById() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"EEE, d MMM yyyy HH:mm:ss Z");
		this.mockMvc
				.perform(get("/v1/rivers/1"))
				.andExpect(status().isOk())
				.andExpect(
						content().contentType("application/json;charset=UTF-8"))
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.account.id").value(3))
				.andExpect(jsonPath("$.account.account_path").value("user1"))
				.andExpect(jsonPath("$.active").value(true))
				.andExpect(jsonPath("$.full").value(false))
				.andExpect(jsonPath("$.name").value("River 1"))
				.andExpect(jsonPath("$.follower_count").value(0))
				.andExpect(jsonPath("$.public").value(true))
				.andExpect(jsonPath("$.drop_count").value(100))
				.andExpect(jsonPath("$.drop_quota").value(10000))
				.andExpect(
						jsonPath("$.date_added")
								.value(DateUtil.formatRFC822(dateFormat
										.parse("Wed, 2 Jan 2013 00:00:02 +0000"), null)))
				.andExpect(
						jsonPath("$.expiry_date")
								.value(DateUtil.formatRFC822(dateFormat
										.parse("Sat, 2 Feb 2013 03:00:02 +0300"), null)))
				.andExpect(jsonPath("$.extension_count").value(0))
				.andExpect(jsonPath("$.channels").isArray());
	}

	@Test
	public void getDrops() throws Exception {
		Authentication authentication = new UsernamePasswordAuthenticationToken(
				"user1", "password");
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		this.mockMvc.perform(get("/v1/rivers/1/drops").principal(authentication)).andExpect(
				status().isOk());
	}
}
