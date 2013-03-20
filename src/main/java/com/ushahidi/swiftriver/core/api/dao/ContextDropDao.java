package com.ushahidi.swiftriver.core.api.dao;

import java.util.List;

import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Drop;

public interface ContextDropDao {

	
	/**
	 * Populate the metadata into the drops in the given array.
	 * 
	 * @param drops
	 * @param queryingAccount
	 * @return
	 */
	public void populateMetadata(List<Drop> drops, Account queryingAccount);
}
