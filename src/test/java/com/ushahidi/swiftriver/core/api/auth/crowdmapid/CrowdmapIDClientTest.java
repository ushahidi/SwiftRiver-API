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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.ushahidi.swiftriver.core.api.dao.UserDao;
import com.ushahidi.swiftriver.core.model.User;

/**
 * Unit tests for {@link CrowdmapID}
 * @author ekala
 *
 */
public class CrowdmapIDClientTest {

	private CrowdmapIDClient crowdmapIDClient;
	
	private UserDao mockUserDao;
	
	private HttpClient mockHttpClient;

	@Before
	public void setUp() {
		mockUserDao = mock(UserDao.class);
		mockHttpClient = mock(DefaultHttpClient.class);

		crowdmapIDClient = new CrowdmapIDClient();
		crowdmapIDClient.setServerURL("https://crowdmapid.com");
		crowdmapIDClient.setApiKey("ABXMDJ04874LPD");
		crowdmapIDClient.setApiKeyParamName("api_secret");
		crowdmapIDClient.setUserDao(mockUserDao);
		crowdmapIDClient.setHttpClient(mockHttpClient);
		crowdmapIDClient.setObjectMapper(new ObjectMapper());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void signIn() throws Exception {
		String email = "test@swiftriver.dev";
		String password = "pa55w0rd";
		
		User mockUser = mock(User.class);
		String mockResponse = mock(String.class);

		when(mockUserDao.findByUsernameOrEmail(anyString())).thenReturn(mockUser);
		when(mockHttpClient.execute(any(HttpUriRequest.class), any(ResponseHandler.class))).thenReturn(mockResponse);
		
		crowdmapIDClient.signIn(email, password);
		verify(mockUserDao).findByUsernameOrEmail(email);

		ArgumentCaptor<HttpPost> httpPostArgument = ArgumentCaptor.forClass(HttpPost.class);
		ArgumentCaptor<ResponseHandler> responseHandlerArgument = ArgumentCaptor.forClass(ResponseHandler.class);

		verify(mockHttpClient).execute(httpPostArgument.capture(), responseHandlerArgument.capture());

		HttpPost httpPost = httpPostArgument.getValue();
		HttpParams requestParams = httpPost.getParams();

		assertEquals("https", httpPost.getURI().getScheme());
		assertEquals("crowdmapid.com", httpPost.getURI().getHost());
		assertEquals("/signin", httpPost.getURI().getPath());
		assertEquals("pa55w0rd", requestParams.getParameter("password").toString());
		assertEquals("test@swiftriver.dev", requestParams.getParameter("email").toString());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void changePassword() throws Exception {
		String email = "test@swiftriver.dev";
		String oldPassword = "old-pa55w0rd";
		String newPassword = "new-pa55w0rd";

		String mockResponse = mock(String.class);

		when(mockHttpClient.execute(any(HttpUriRequest.class), any(ResponseHandler.class))).thenReturn(mockResponse);
		boolean loginStatus = crowdmapIDClient.changePassword(email, oldPassword, newPassword);
		
		ArgumentCaptor<HttpPost> httpPostArgument = ArgumentCaptor.forClass(HttpPost.class);
		ArgumentCaptor<ResponseHandler> responseHandlerArgument = ArgumentCaptor.forClass(ResponseHandler.class);

		verify(mockHttpClient).execute(httpPostArgument.capture(), responseHandlerArgument.capture());
		
		HttpPost httpPost = httpPostArgument.getValue();
		HttpParams requestParams = httpPost.getParams();
		
		assertTrue(loginStatus);
		assertEquals(oldPassword, requestParams.getParameter("oldpassword"));
		assertEquals(newPassword, requestParams.getParameter("newpassword"));
	}
	
}
