package com.ushahidi.swiftriver.core.api.auth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.ClientDao;
import com.ushahidi.swiftriver.core.model.Client;
import com.ushahidi.swiftriver.core.model.Role;
import com.ushahidi.swiftriver.core.util.TextUtil;

@Transactional(readOnly = true)
public class DbClientDetailsService implements ClientDetailsService {

	final Logger logger = LoggerFactory.getLogger(DbClientDetailsService.class);

	@Autowired
	ClientDao clientDao;

	private String key;

	public ClientDao getClientDao() {
		return clientDao;
	}

	public void setClientDao(ClientDao clientDao) {
		this.clientDao = clientDao;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public ClientDetails loadClientByClientId(String clientId)
			throws ClientRegistrationException {

		Client dbClient = clientDao.findByClientId(clientId);

		if (dbClient == null)
			throw new UsernameNotFoundException("Client not found");

		Set<String> redirectUri = new HashSet<String>();
		redirectUri.add(dbClient.getRedirectUri());

		// All clients can use the authorization and refresh token grant types
		logger.debug("Granting authorization_code and refresh_token grant types");
		Set<String> grantTypes = new HashSet<String>();
		grantTypes.add("authorization_code");
		grantTypes.add("refresh_token");

		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		logger.info("Fetching client roles");
		for (Role role : dbClient.getRoles()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_"
					+ role.getName().toUpperCase()));

			// Only trusted clients can use username and password
			if (role.getName().equals("trusted_client")) {
				grantTypes.add("password");
				grantTypes.add("client_credentials");
			}
		}

		BaseClientDetails details = new BaseClientDetails();
		details.setClientId(dbClient.getClientId());
		details.setRegisteredRedirectUri(redirectUri);
		details.setAuthorities(authorities);
		details.setAuthorizedGrantTypes(grantTypes);

		// Decrypt client secret
		logger.debug("Decrypting client secret");
		TextEncryptor encryptor = Encryptors.text(
				TextUtil.convertStringToHex(key),
				TextUtil.convertStringToHex(dbClient.getClientId()));
		details.setClientSecret(encryptor.decrypt(dbClient.getClientSecret()));

		logger.debug("Client secret successfully decrypted");
		return details;
	}

}
