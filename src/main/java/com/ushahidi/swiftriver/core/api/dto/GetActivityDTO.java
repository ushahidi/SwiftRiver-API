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

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * @author Ushahidi, Inc
 *
 */
public class GetActivityDTO {

	private String id;
	
	@JsonProperty("date_added")
	private String actionDateAdd;
	
	private Account account;
	
	private String action;
	
	@JsonProperty("action_on")
	private String actionOn;
	
	@JsonProperty("action_on_obj")
	private Target actionOnObj;
	
	private GetAccountDTO actionTo;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getActionDateAdd() {
		return actionDateAdd;
	}

	public void setActionDateAdd(String actionDateAdd) {
		this.actionDateAdd = actionDateAdd;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActionOn() {
		return actionOn;
	}

	public void setActionOn(String actionOn) {
		this.actionOn = actionOn;
	}

	public Target getActionOnObj() {
		return actionOnObj;
	}

	public void setActionOnObj(Target actionOnObj) {
		this.actionOnObj = actionOnObj;
	}

	public GetAccountDTO getActionTo() {
		return actionTo;
	}

	public void setActionTo(GetAccountDTO actionTo) {
		this.actionTo = actionTo;
	}
	
	public static class Account {
		
		private long id;
		
		@JsonProperty("account_path")
		private String accountPath;
		
		private Owner owner;
		
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

		public Owner getOwner() {
			return owner;
		}

		public void setOwner(Owner owner) {
			this.owner = owner;
		}

		public static class Owner {
			
			private String avatar;
			
			private String name;

			public String getAvatar() {
				return avatar;
			}

			public void setAvatar(String avatar) {
				this.avatar = avatar;
			}

			public String getName() {
				return name;
			}

			public void setName(String name) {
				this.name = name;
			}
		}
	}
	
	public static class Target {
		
		private long id;
		
		private String name;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}
}
