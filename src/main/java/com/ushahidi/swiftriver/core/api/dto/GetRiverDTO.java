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
 * Transfer Object for Rivers
 *
 */
public class GetRiverDTO {
	
	private long id;
	
	@JsonProperty("name")
	private String riverName;
	
	private String category;
	
	private Account account;
	
	@JsonProperty("follower_count")
	private int followerCount;
	
	@JsonProperty("public")
	private boolean riverPublic;
	
	private boolean active;
	
	@JsonProperty("drop_count")
	private int dropCount;
	
	@JsonProperty("drop_quota")
	private int dropQuota;
	
	private boolean full;
	
	@JsonProperty("date_added")
	private String dateAdded;
	
	@JsonProperty("expiry_date")
	private String expiryDate;
	
	@JsonProperty("extension_count")
	private int extensionCount;
	
	private List<Channel> channels;
	
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
	
	public static class Channel {
		
		private long id;
		
		@JsonProperty("name")
		private String channel;
		
		private boolean active;
		
		private List<ChannelOption> options;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getChannel() {
			return channel;
		}

		public void setChannel(String channel) {
			this.channel = channel;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public List<ChannelOption> getOptions() {
			return options;
		}

		public void setOptions(List<ChannelOption> options) {
			this.options = options;
		}
	}
	
	public static class ChannelOption {
		
		private long id;
		
		private String key;
		
		private String value;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getKey() {
			return key;
		}

		public void setKey(String key) {
			this.key = key;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRiverName() {
		return riverName;
	}

	public void setRiverName(String riverName) {
		this.riverName = riverName;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public int getFollowerCount() {
		return followerCount;
	}

	public void setFollowerCount(int followerCount) {
		this.followerCount = followerCount;
	}

	public boolean isRiverPublic() {
		return riverPublic;
	}

	public void setRiverPublic(boolean riverPublic) {
		this.riverPublic = riverPublic;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getDropCount() {
		return dropCount;
	}

	public void setDropCount(int dropCount) {
		this.dropCount = dropCount;
	}

	public int getDropQuota() {
		return dropQuota;
	}

	public void setDropQuota(int dropQuota) {
		this.dropQuota = dropQuota;
	}

	public boolean isFull() {
		return full;
	}

	public void setFull(boolean full) {
		this.full = full;
	}

	public String getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(String dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(String expiryDate) {
		this.expiryDate = expiryDate;
	}

	public int getExtensionCount() {
		return extensionCount;
	}

	public void setExtensionCount(int extensionCount) {
		this.extensionCount = extensionCount;
	}

	public List<Channel> getChannels() {
		return channels;
	}

	public void setChannels(List<Channel> channels) {
		this.channels = channels;
	}

}