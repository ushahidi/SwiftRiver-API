package com.ushahidi.swiftriver.core.api.auth;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

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
		
		when(mockUserDao.findByUsername(anyString())).thenReturn(user);
		
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
