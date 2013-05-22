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
package com.ushahidi.swiftriver.core.api.auth.crowdmapid;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import com.ushahidi.swiftriver.core.model.Role;
import com.ushahidi.swiftriver.core.model.User;

/**
 * Tests for the {@link CrowdmapIDAuthenticationProvider} class
 * 
 * @author ekala
 *
 */
public class CrowdmapIDAuthenticationProviderTest {
	
	private CrowdmapIDAuthenticationProvider authenticationProvider;
	
	private CrowdmapIDClient mockCrowdmapIDClient;

	@Before
	public void setUp() {
		mockCrowdmapIDClient = mock(CrowdmapIDClient.class);
		authenticationProvider = new CrowdmapIDAuthenticationProvider();
		authenticationProvider.setCrowdmapIDClient(mockCrowdmapIDClient);
	}

	/**
	 * Tests authenticating a user via CrowmdmapID
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void authenticate() {
		Authentication mockAuthentication =  mock(Authentication.class);
		Object mockCredentials = mock(Object.class);
		User mockUser = mock(User.class);

		Set<Role> userRoles = new HashSet<Role>();
		Role role = new Role();
		role.setName("user");
		userRoles.add(role);

		when(mockAuthentication.getName()).thenReturn("test@swiftapp.com");
		when(mockAuthentication.getCredentials()).thenReturn(mockCredentials);
		when(mockCredentials.toString()).thenReturn("pa55w0rd");
		when(mockCrowdmapIDClient.signIn(anyString(), anyString())).thenReturn(mockUser);
		when(mockUser.getRoles()).thenReturn(userRoles);
		
		Authentication authentication = authenticationProvider.authenticate(mockAuthentication);
		List<GrantedAuthority> authorities = (List<GrantedAuthority>) authentication.getAuthorities();

		verify(mockCrowdmapIDClient).signIn("test@swiftapp.com", "pa55w0rd");
		assertEquals(1, authorities.size());
		assertEquals("ROLE_USER", authorities.get(0).getAuthority());
	}
}
