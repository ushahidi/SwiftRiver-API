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
package com.ushahidi.swiftriver.core.api.dto;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

import com.ushahidi.swiftriver.core.model.Identity;

/**
 * Unit test for the Identity DTO
 * @author ekala
 *
 */
public class IdentityDTOTest {

	/**
	 * @verifies a {@link Identity} model is created from
	 * a DTO (Map<String, Object>) representation
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testCreateModel() {
		Object[][] identityData = {
				{"channel", "rss"},
				{"identity_orig_id", "http://allafrica.com/tools/headlines/rdf/latest/headlines.rdf"},
				{"identity_username", "http://allafrica.com/latest/"},
				{"identity_name", "AllAfrica News: Latest"},
				{"identity_avatar", "http://allafrica.com/static/images/structure/aa-logo.png"}
		};
		
		IdentityDTO dto = new IdentityDTO();
		Identity identity = dto.createModel(ArrayUtils.toMap(identityData));
		
		String expectedHash = "bbe447f2ce1ae7d429fec1edecb9fe0d";
		assertEquals(expectedHash, identity.getHash());
	}

}