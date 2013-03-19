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
package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Identity;

public class JpaIdentityDaoTest extends AbstractDaoTest {
	
	@Autowired
	JpaIdentityDao identityDao;
	
	
	@Test
	public void getIdentities() {
		List<Drop> drops = new ArrayList<Drop>();
		Drop drop = new Drop();
		drop.setChannel("test channel");

		Identity identity = new Identity();
		identity.setOriginId("test identity original id");
		identity.setAvatar("test avatar");
		identity.setName("test name");
		identity.setUsername("test username");
		drop.setIdentity(identity);
		drops.add(drop);
		
		identityDao.getIdentities(drops);
		
		String sql = "SELECT id, hash, channel, identity_orig_id, " +
				"identity_username, identity_name, identity_avatar " +
				"FROM identities WHERE id = ?";

		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql,
				drop.getIdentity().getId());
		
		assertEquals(3L, ((Number)results.get("id")).longValue());
		assertEquals("f335db65e19fd5ebf49d5f9ad21f1f07", results.get("hash"));
		assertEquals("test channel", results.get("channel"));
		assertEquals("test identity original id", results.get("identity_orig_id"));
		assertEquals("test username", results.get("identity_username"));
		assertEquals("test name", results.get("identity_name"));
		assertEquals("test avatar", results.get("identity_avatar"));
	}

}
