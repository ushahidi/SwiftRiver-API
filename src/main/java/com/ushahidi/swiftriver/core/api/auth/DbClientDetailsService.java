package com.ushahidi.swiftriver.core.api.auth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.BaseClientDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.ClientDao;
import com.ushahidi.swiftriver.core.model.Client;
import com.ushahidi.swiftriver.core.model.Role;

@Transactional(readOnly = true)
public class DbClientDetailsService implements ClientDetailsService {

	@Autowired
	ClientDao clientDao;

	public ClientDao getClientDao() {
		return clientDao;
	}

	public void setClientDao(ClientDao clientDao) {
		this.clientDao = clientDao;
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
		Set<String> grantTypes = new HashSet<String>();
		grantTypes.add("authorization_code");
		grantTypes.add("refresh_token");

		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
		for (Role role : dbClient.getRoles()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_"
					+ role.getName().toUpperCase()));

			// Only trusted clients can use username and password
			if (role.getName().equals("trusted_client")) {
				grantTypes.add("password");
			}
		}

		BaseClientDetails details = new BaseClientDetails();
		details.setClientId(dbClient.getClientId());
		details.setClientSecret(dbClient.getClientSecret());
		details.setRegisteredRedirectUri(redirectUri);
		details.setAuthorities(authorities);
		details.setAuthorizedGrantTypes(grantTypes);

		return details;
	}
}
