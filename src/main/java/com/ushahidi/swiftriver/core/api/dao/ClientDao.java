package com.ushahidi.swiftriver.core.api.dao;

import com.ushahidi.swiftriver.core.model.Client;

public interface ClientDao extends GenericDao<Client> {

	/**
	 * Get a client using its client identifier
	 * 
	 * @param username
	 * @return
	 */
	public Client findByClientId(String clientId);
}
