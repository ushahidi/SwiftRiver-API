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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
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
		mockHttpClient = mock(HttpClient.class);

		crowdmapIDClient = new CrowdmapIDClient();
		crowdmapIDClient.setServerURL("https://crowdmapid.com/api");
		crowdmapIDClient.setApiKey("ABXMDJ04874LPD");
		crowdmapIDClient.setApiKeyParamName("api_secret");
		crowdmapIDClient.setUserDao(mockUserDao);
		crowdmapIDClient.setHttpClient(mockHttpClient);
		crowdmapIDClient.setObjectMapper(new ObjectMapper());
	}
	
	@Test
	public void signIn() throws Exception {
		String email = "test@swiftriver.dev";
		String password = "pa55w0rd";
		
		String apiResponse = "{\"success\": true}";
		InputStream inputStream = new ByteArrayInputStream(apiResponse.getBytes());

		// Mock objects for this test
		User mockUser = mock(User.class);
		HttpResponse mockResponse = mock(HttpResponse.class);
		HttpEntity mockHttpEntity = mock(HttpEntity.class);
		StatusLine mockStatusLine = mock(StatusLine.class);

		when(mockUserDao.findByUsernameOrEmail(anyString())).thenReturn(mockUser);
		when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
		when(mockStatusLine.getStatusCode()).thenReturn(200);
		when(mockResponse.getEntity()).thenReturn(mockHttpEntity);
		when(mockHttpEntity.getContent()).thenReturn(inputStream);
		when(mockHttpClient.execute(any(HttpUriRequest.class))).thenReturn(mockResponse);
		
		crowdmapIDClient.signIn(email, password);
		verify(mockUserDao).findByUsernameOrEmail(email);

		ArgumentCaptor<HttpPost> httpPostArgument = ArgumentCaptor.forClass(HttpPost.class);
		verify(mockHttpClient).execute(httpPostArgument.capture());

		HttpPost httpPost = httpPostArgument.getValue();

		assertEquals("/api/signin", httpPost.getURI().getPath());
	}
	
	@Test
	public void changePassword() throws Exception {
		String email = "test@swiftriver.dev";
		String oldPassword = "old-pa55w0rd";
		String newPassword = "new-pa55w0rd";

		String apiResponse = "{\"success\": true}";
		InputStream inputStream = new ByteArrayInputStream(apiResponse.getBytes());

		// Mock objects for this test
		HttpResponse mockResponse = mock(HttpResponse.class);
		HttpEntity mockHttpEntity = mock(HttpEntity.class);
		StatusLine mockStatusLine = mock(StatusLine.class);

		when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
		when(mockStatusLine.getStatusCode()).thenReturn(200);
		when(mockResponse.getEntity()).thenReturn(mockHttpEntity);
		when(mockHttpEntity.getContent()).thenReturn(inputStream);
		when(mockHttpClient.execute(any(HttpUriRequest.class))).thenReturn(mockResponse);

		boolean loginStatus = crowdmapIDClient.changePassword(email, oldPassword, newPassword);
		
		ArgumentCaptor<HttpPost> httpPostArgument = ArgumentCaptor.forClass(HttpPost.class);

		verify(mockHttpClient).execute(httpPostArgument.capture());
		
		HttpPost httpPost = httpPostArgument.getValue();
		
		assertTrue(loginStatus);
		assertEquals("/api/changepassword", httpPost.getURI().getPath());
	}
	
}
