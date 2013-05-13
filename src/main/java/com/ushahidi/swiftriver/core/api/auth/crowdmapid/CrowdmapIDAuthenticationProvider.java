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

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.model.Role;
import com.ushahidi.swiftriver.core.model.User;

/**
 * This class handles authentication of users via CrowdmapID
 * 
 * @author ekala
 *
 */
@Transactional
public class CrowdmapIDAuthenticationProvider implements AuthenticationProvider {

	private CrowdmapIDClient crowdmapIDClient;

	public void setCrowdmapIDClient(CrowdmapIDClient crowdmapIDClient) {
		this.crowdmapIDClient = crowdmapIDClient;
	}

	@Transactional(readOnly = true)
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {

		String username = authentication.getName();
		String password = authentication.getCredentials().toString();
		
		User user = crowdmapIDClient.signIn(username, password);
		Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
		for (Role role: user.getRoles()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase()));
		}

		return new UsernamePasswordAuthenticationToken(username, "", authorities);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
