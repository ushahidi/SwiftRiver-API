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
import org.springframework.security.oauth2.provider.ClientDetails;

import com.ushahidi.swiftriver.core.api.dao.ClientDao;
import com.ushahidi.swiftriver.core.model.Client;
import com.ushahidi.swiftriver.core.model.Role;

public class DbClientDetailsServiceTest {

private ClientDao mockClientDao;
	
	private DbClientDetailsService dbClientDetailsService;
	
	@Before
	public void setup() {
		mockClientDao = mock(ClientDao.class);
		dbClientDetailsService = new DbClientDetailsService();
		dbClientDetailsService.setClientDao(mockClientDao);
		dbClientDetailsService.setKey("2344228477#97{7&6>82");
	}

	@Test
	public void loadClientByClientId() {
		Client client = new Client();
		client.setClientId("trusted-client");
		client.setClientSecret("8b22f281afd911c3dfc59270af43db1995d5968a3447c780ba3e152e603fd9a0");
		client.setRedirectUri("example");
		client.setActive(true);
		Role r = new Role();
		r.setName("client");
		Set<Role> roles = new HashSet<Role>();
		roles.add(r);
		client.setRoles(roles);
		
		when(mockClientDao.findByClientId(anyString())).thenReturn(client);
		
		ClientDetails ud = dbClientDetailsService.loadClientByClientId("client-id");
		
		assertEquals("trusted-client", ud.getClientId());
		assertEquals("somesecret", ud.getClientSecret());
		assertEquals(1, ud.getRegisteredRedirectUri().size());
		assertTrue(ud.getRegisteredRedirectUri().contains("example"));
		assertTrue(ud.getAuthorizedGrantTypes().contains("authorization_code"));
		assertTrue(ud.getAuthorizedGrantTypes().contains("refresh_token"));
		assertTrue(!ud.getAuthorizedGrantTypes().contains("password"));
		
		assertEquals(1, ud.getAuthorities().size());
		GrantedAuthority authority = ud.getAuthorities().iterator().next();
		assertEquals("ROLE_CLIENT", authority.getAuthority());
	}
}
