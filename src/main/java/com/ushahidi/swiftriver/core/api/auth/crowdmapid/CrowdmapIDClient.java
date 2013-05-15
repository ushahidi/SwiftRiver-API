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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.UserDao;
import com.ushahidi.swiftriver.core.model.User;

/**
 * This class handles interaction with the 
 * <a href="https://github.com/ushahidi/crowdmapid-server</a>CrowdmapID</a>
 *  identity management system
 *  
 * @author ekala
 *
 */
public class CrowdmapIDClient {

	private String serverURL;
	
	private String apiKey;
	
	private String apiKeyParamName;
	
	@Autowired
	private UserDao userDao;

	private HttpClient httpClient;

	private final Logger logger = LoggerFactory.getLogger(CrowdmapIDClient.class);
	
	@Autowired
	private ObjectMapper jacksonObjectMapper;
	
	/**
	 * Default empty constructor
	 */
	public CrowdmapIDClient() {
		
	}

	/**
	 * Initializes the <code>CrowdmapIDClient</code> with the url of the
	 * <code>CrowdmapID</code> server, the api key for authenticating
	 * requests and the name of the request parameter used to specify
	 * the api key
	 * 
	 * @param serverURL
	 * @param apiKey
	 * @param apiKeyParamName
	 */
	public CrowdmapIDClient(String serverURL, String apiKey, String apiKeyParamName) {
		setServerURL(serverURL);
		setApiKey(apiKey);
		setApiKeyParamName(apiKeyParamName);
		
		httpClient = new DefaultHttpClient();
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public String getServerURL() {
		return serverURL;
	}

	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getApiKeyParamName() {
		return apiKeyParamName;
	}

	public void setApiKeyParamName(String apiKeyParamName) {
		this.apiKeyParamName = apiKeyParamName;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.jacksonObjectMapper = objectMapper;
	}

	/**
	 * Sends a signin request to the CrowdmapID server
	 * @param email
	 * @param password
	 * @return
	 */
	public User signIn(String email, String password) {
		User user = userDao.findByUsernameOrEmail(email);
		if (user != null) {
			HttpParams params = new SyncBasicHttpParams();
			params.setParameter("email", email);
			params.setParameter("password", password);
			
			Map<String, Object> apiResponse = executeApiRequest(CrowdmapIDRequestType.SIGNIN, params);
			if (apiResponse.isEmpty()) {
				return null;
			}
			
		}
		return user;
	}

	/**
	 * Sends a changepassword to the CrowdmapID server
	 * 
	 * @param email
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	public boolean changePassword(String email, String oldPassword,
			String newPassword) {
		HttpParams params = new SyncBasicHttpParams();
	
		params.setParameter("email", email);
		params.setParameter("oldpassword", oldPassword);
		params.setParameter("newpassword", newPassword);
		
		Map<String, Object> response = executeApiRequest(CrowdmapIDRequestType.CHANGEPASSWORD, params);
		
		return !response.isEmpty();
	}

	/**
	 * Internal helper method for sending request to the CrowdmapID server
	 * 
	 * @param apiRequest
	 * @param params
	 * @return
	 */
	private Map<String, Object> executeApiRequest(CrowdmapIDRequestType apiRequest, HttpParams params) {
		// Base URI for the request
		String baseURIStr = getBaseRequestUrl(apiRequest);
		Map<String, Object> responseMap = new HashMap<String, Object>();

		// Build the request and set parameters
		HttpPost httpPost = new HttpPost(baseURIStr);
		params.setParameter(this.apiKeyParamName, this.apiKey);
		httpPost.setParams(params);
		httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

		String apiResponse = null;
		
		// Execute the request
		try {
			HttpResponse httpResponse = httpClient.execute(httpPost);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			logger.info("{} return status {}", this.serverURL, statusCode);

			apiResponse = readApiResponse(httpResponse);

			if (statusCode != 200) {
				logger.error("The server returned status {} - {}", 
						statusCode,  apiResponse);
				return responseMap;
			}
			
		} catch (ClientProtocolException e) {
			logger.error("An error occurred when processing request: {} - {}",
					this.serverURL, e.getMessage());
		} catch (IOException e) {
			logger.error("An error occurred when reading the response from the server: {} - {}",
					this.serverURL, e.getMessage());
		}

		// Deserialize the response into a java.util.Map
		if (apiResponse != null) {
			try {
				responseMap = jacksonObjectMapper.readValue(apiResponse, 
						new TypeReference<Map<String, Object>>() {
				});
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Check for the status
		if (!responseMap.isEmpty()) {
			Boolean status = (Boolean) responseMap.get("success");
			if (!status) {
				logger.error("The API returned the following error message: {}", 
						responseMap.get("error"));

				// Clear all mappings and return an empty map
				responseMap.clear();
			}
		}
		return responseMap;
	}

	/**
	 * Reads the response from the API into a <code>java.lang.String</code>
	 * 
	 * @param httpResponse
	 * @return
	 * @throws IOException
	 */
	private String readApiResponse(HttpResponse httpResponse) throws IOException {
		HttpEntity entity = httpResponse.getEntity();

		// Write the server to response to a buffer
		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
		StringBuffer buffer = new StringBuffer();
		String output = null;
		while ((output = reader.readLine()) != null) {
			buffer.append(output);
		}

		// Close the reader
		reader.close();
		return buffer.toString();
	}

	/**
	 * Generates and returns the URI of the endpoint to be used
	 * for the specified <code>CrowdmapIDApiRequest</code>
	 *  
	 * @param apiRequest
	 * @return
	 */
	private String getBaseRequestUrl(CrowdmapIDRequestType apiRequest) {
		switch (apiRequest) {
		case SIGNIN:
			return serverURL + "/signin";
			
		case CHANGEPASSWORD:
			return serverURL + "/changepassword"; 
			
		case REGISTER:
			return serverURL + "/register";
			
		case REQUESTPASSWORD:
			return serverURL + "/requestpassword";
			
		case SETPASSWORD:
			return serverURL + "/setpassword";
		}

		return null;
	}

	
}
