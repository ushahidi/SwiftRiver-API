package com.ushahidi.swiftriver.core.api.dao.impl;

import javax.persistence.NoResultException;

import com.ushahidi.swiftriver.core.api.dao.ClientDao;
import com.ushahidi.swiftriver.core.model.Client;

public class JpaClientDao extends AbstractJpaDao<Client> implements ClientDao {

	@Override
	public Client findByClientId(String clientId) {
		String query = "SELECT c FROM Client c WHERE c.clientId = :client_id";

		Client client = null;
		try {
			client = (Client) em.createQuery(query)
					.setParameter("client_id", clientId).getSingleResult();
		} catch (NoResultException e) {
			// Do nothing
		}
		return client;
	}

}
