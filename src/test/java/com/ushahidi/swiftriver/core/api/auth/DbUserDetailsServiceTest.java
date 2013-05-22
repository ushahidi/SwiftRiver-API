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
package com.ushahidi.swiftriver.core.api.auth;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ushahidi.swiftriver.core.api.dao.UserDao;
import com.ushahidi.swiftriver.core.model.Role;
import com.ushahidi.swiftriver.core.model.User;

public class DbUserDetailsServiceTest {
	
	private UserDao mockUserDao;
	
	private DbUserDetailsService dbUserDetailsService;
	
	@Before
	public void setup() {
		mockUserDao = mock(UserDao.class);
		dbUserDetailsService = new DbUserDetailsService();
		dbUserDetailsService.setUserDao(mockUserDao);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void loadUserByUsername() {
		User user = new User();
		user.setUsername("username");
		user.setPassword("password");
		user.setActive(true);
		user.setExpired(false);
		user.setLocked(false);
		Role r = new Role();
		r.setName("user");
		Set<Role> roles = new HashSet<Role>();
		roles.add(r);
		user.setRoles(roles);
		
		when(mockUserDao.findByUsernameOrEmail(anyString())).thenReturn(user);
		
		UserDetails ud = dbUserDetailsService.loadUserByUsername("username");
		
		assertEquals("username", ud.getUsername());
		assertEquals("password", ud.getPassword());
		assertTrue(ud.isEnabled());
		assertTrue(ud.isCredentialsNonExpired());
		assertTrue(ud.isAccountNonLocked());
		
		Set<GrantedAuthority> authorities =  (Set<GrantedAuthority>) ud.getAuthorities();
		assertEquals(1, authorities.size());
		assertEquals("ROLE_USER", authorities.iterator().next().getAuthority());
	}
}
