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

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.params.HttpParams;
import org.apache.http.params.SyncBasicHttpParams;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
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
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void setServerURL(String serverURL) {
		this.serverURL = serverURL;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
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

	@SuppressWarnings("unchecked")
	public User signIn(String email, String password) {
		User user = userDao.findByUsernameOrEmail(email);
		if (user != null) {
			String baseUrl = this.getBaseRequestUrl(CrowdmapIDApiRequest.SIGNIN);

			HttpParams params = new SyncBasicHttpParams();
			params.setParameter("email", email);
			params.setParameter("password", password);
			
			String jsonResponse = executeApiRequest(baseUrl, params);

			if (jsonResponse.length() == 0) {
				return null;
			}
			
			try {
				Map<String, Object> responseMap = jacksonObjectMapper.readValue(jsonResponse, Map.class);
				
				if (!responseMap.containsKey("session_id") || responseMap.containsKey("user_id")) {
					// Authentication failed					
					return null;
				}
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return user;
	}

	/**
	 * Internal helper method for sending request to the CrowdmapID server
	 * 
	 * @param baseUrl
	 * @param params
	 * @return
	 */
	private String executeApiRequest(String baseUrl, HttpParams params) {
		HttpPost httpPost = new HttpPost(baseUrl);
		params.setParameter(this.apiKeyParamName, this.apiKey);
		httpPost.setParams(params);

		String response = null;
		try {
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			response = httpClient.execute(httpPost, responseHandler);
		} catch (ClientProtocolException e) {
			logger.error("An error occurred when processign request: {}",
					e.getMessage());
		} catch (IOException e) {
			logger.error("An error occurred when reading the response from the server: {}",
					e.getMessage());
		}

		return response;
	}

	/**
	 * Generates and returns the URI of the endpoint to be used
	 * for the specified <code>CrowdmapIDApiRequest</code>
	 *  
	 * @param apiRequest
	 * @return
	 */
	private String getBaseRequestUrl(CrowdmapIDApiRequest apiRequest) {
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
