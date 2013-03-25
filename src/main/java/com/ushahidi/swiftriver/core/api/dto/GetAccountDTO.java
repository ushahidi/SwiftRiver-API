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
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Transfer Object for Accounts
 *
 */
public class GetAccountDTO {

	private long id;
	
	@JsonProperty("account_path")
	private String accountPath;
	
	@JsonProperty("date_added")
	private String dateAdded;
	
	private boolean active;
	
	@JsonProperty("private")
	private boolean accountPrivate;
	
	@JsonProperty("river_quota_remaining")
	private int riverQuotaRemaining;
	
	@JsonProperty("follower_count")
	private int followerCount;
	
	@JsonProperty("following_count")
	private int followingCount;
	
	private Owner owner;
	
	private List<GetRiverDTO> rivers;
	
	@JsonProperty("collaborating_rivers")
	private List<GetRiverDTO> collaboratingRivers;
	
	@JsonProperty("following_rivers")
	private List<GetRiverDTO> followingRivers;
	
	private List<GetBucketDTO> buckets;
	
	@JsonProperty("collaborating_buckets")
	private List<GetBucketDTO> collaboratingBuckets;
	
	@JsonProperty("following_buckets")
	private List<GetBucketDTO> followingBuckets;
	
	private List<GetFormDTO> forms;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String token;
	
	public static class Owner {
		
        private String name;
        
        private String email;
        
        private String username;
        
        private String avatar;
        
        @JsonProperty("date_added")
        private String createdDate;
        
        private Boolean active;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getCreatedDate() {
			return createdDate;
		}

		public void setCreatedDate(String createdDate) {
			this.createdDate = createdDate;
		}

		public String getAvatar() {
			return avatar;
		}

		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}

		public Boolean getActive() {
			return active;
		}

		public void setActive(Boolean active) {
			this.active = active;
		}
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

	public String getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(String dateAdded) {
		this.dateAdded = dateAdded;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getRiverQuotaRemaining() {
		return riverQuotaRemaining;
	}

	public void setRiverQuotaRemaining(int riverQuotaRemaining) {
		this.riverQuotaRemaining = riverQuotaRemaining;
	}

	public int getFollowerCount() {
		return followerCount;
	}

	public void setFollowerCount(int followerCount) {
		this.followerCount = followerCount;
	}

	public int getFollowingCount() {
		return followingCount;
	}

	public void setFollowingCount(int followingCount) {
		this.followingCount = followingCount;
	}

	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public List<GetRiverDTO> getRivers() {
		return rivers;
	}

	public void setRivers(List<GetRiverDTO> rivers) {
		this.rivers = rivers;
	}

	public List<GetRiverDTO> getCollaboratingRivers() {
		return collaboratingRivers;
	}

	public void setCollaboratingRivers(List<GetRiverDTO> collaboratingRivers) {
		this.collaboratingRivers = collaboratingRivers;
	}

	public List<GetRiverDTO> getFollowingRivers() {
		return followingRivers;
	}

	public void setFollowingRivers(List<GetRiverDTO> followingRivers) {
		this.followingRivers = followingRivers;
	}

	public List<GetBucketDTO> getBuckets() {
		return buckets;
	}

	public void setBuckets(List<GetBucketDTO> buckets) {
		this.buckets = buckets;
	}

	public boolean isAccountPrivate() {
		return accountPrivate;
	}

	public void setAccountPrivate(boolean accountPrivate) {
		this.accountPrivate = accountPrivate;
	}

	public List<GetBucketDTO> getCollaboratingBuckets() {
		return collaboratingBuckets;
	}

	public void setCollaboratingBuckets(List<GetBucketDTO> collaboratingBuckets) {
		this.collaboratingBuckets = collaboratingBuckets;
	}

	public List<GetBucketDTO> getFollowingBuckets() {
		return followingBuckets;
	}

	public void setFollowingBuckets(List<GetBucketDTO> followingBuckets) {
		this.followingBuckets = followingBuckets;
	}

	public List<GetFormDTO> getForms() {
		return forms;
	}

	public void setForms(List<GetFormDTO> forms) {
		this.forms = forms;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}


}
