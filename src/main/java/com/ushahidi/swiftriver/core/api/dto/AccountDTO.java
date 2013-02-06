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
package com.ushahidi.swiftriver.core.api.dto;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import com.ushahidi.swiftriver.core.model.Account;

/**
 * DTO mapping class for buckets
 * 
 * @author ekala
 */
public class AccountDTO extends AbstractDTO<Account> {

	/**
	 * @see {@link AbstractDTO#createMapFromEntity(Object)}
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> createMapFromEntity(Account entity) {
		Object[][] accountData = {
				{"id", entity.getId()},
				{"account_path", entity.getAccountPath()},
				{"active", entity.isActive()},
				{"public", entity.isAccountPrivate()}
		};

		return ArrayUtils.toMap(accountData);
	}

	/**
	 * @see {@link AbstractDTO#createEntityFromMap(Map)}
	 */
	public Account createEntityFromMap(Map<String, Object> map) {
		Account account = new Account();

		account.setAccountPath((String) map.get("account_path"));
		account.setActive((Boolean) map.get("active"));
		account.setAccountPrivate((Boolean) map.get("public"));
		
		return account;
	}

	@Override
	protected String[] getValidationKeys() {
		return new String[]{"account_path", "public", "active"};
	}

	@Override
	protected void copyFromMap(Account target, Map<String, Object> source) {
		Account dummy = createEntityFromMap(source);
		
		target.setAccountPath(dummy.getAccountPath());
		target.setAccountPrivate(dummy.isAccountPrivate());
		target.setActive(dummy.isActive());
	}

}
