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
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * Integration tests for the security configuration
 * 
 * @author ekala
 */
public class AuthenticationProviderIntegrationTest {

	private AuthenticationProvider authenticationProvider;
	
	@SuppressWarnings("resource")
	@Before
	public void setUp() {
		GenericXmlApplicationContext context = new GenericXmlApplicationContext();
		context.getEnvironment().setActiveProfiles("test");

		context.load(new String[]{
			"file:src/main/webapp/WEB-INF/spring/root-context.xml",
			"file:src/main/webapp/WEB-INF/spring/security-context.xml",
			"file:src/main/webapp/WEB-INF/spring/solr-context.xml"
		});
		context.refresh();
		
		authenticationProvider = (AuthenticationProvider) context.getBean("authenticationProvider");
	}
	
	@Test
	public void testAuthentication() throws Exception {
		Authentication auth = new UsernamePasswordAuthenticationToken("test+user@swiftapp.com", "@pa55w0rdUsh!");
		Authentication authResult = authenticationProvider.authenticate(auth);
		assertNotNull(authResult);
		assertEquals("swiftapp.test", authResult.getName());
	}
}
