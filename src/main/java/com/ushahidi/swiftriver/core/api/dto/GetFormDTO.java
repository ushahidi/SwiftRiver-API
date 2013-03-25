/**
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ushahidi.swiftriver.core.api.dto;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Ushahidi, Inc
 * 
 */
public class GetFormDTO {
	
	private String id;

	private String name;
	
	private Account account;

	private List<GetFormFieldDTO> fields;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public List<GetFormFieldDTO> getFields() {
		return fields;
	}

	public void setFields(List<GetFormFieldDTO> fields) {
		this.fields = fields;
	}

	public static class Account {
		private long id;
		
		@JsonProperty("account_path")
		private String accountPath;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getAccountPath() {
			return accountPath;
		}

		public void setAccountPath(String accountPath) {
			this.accountPath = accountPath;
		}
		
	}
}
