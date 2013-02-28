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
	
	@JsonProperty("is_collaborator")
	private boolean collaborator;
	
	@JsonProperty("is_following")
	private boolean isFollower;

	private Owner owner;
	
	private List<River> rivers;
	
	@JsonProperty("collaborating_rivers")
	private List<River> collaboratingRivers;
	
	@JsonProperty("following_rivers")
	private List<River> followingRivers;
	
	private List<Bucket> buckets;
	
	@JsonProperty("collaborating_buckets")
	private List<Bucket> collaboratingBuckets;
	
	@JsonProperty("following_buckets")
	private List<Bucket> followingBuckets;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
	private String token;
	
	public static class River {
		
		private long id;
		
		@JsonProperty("name")
		private String riverName;
		
		private String description;
		
		private String category;
		
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
		
		private Account account;

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

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
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

		public Account getAccount() {
			return account;
		}

		public void setAccount(Account account) {
			this.account = account;
		}
	}
	
	public static class Bucket {
		
		private long id;
		
		private String name;
		
		private String description;
		
		private String category;
		
		@JsonProperty("follower_count")
		private int followerCount;
		
		private boolean collaborating;
		
		private boolean following;
		
		@JsonProperty("public")
		private boolean published;
		
		@JsonProperty("drop_count")
		private int dropCount;
		
		@JsonProperty("date_added")
		private String dateAdded;
		
		private Account account;

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

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public int getFollowerCount() {
			return followerCount;
		}

		public void setFollowerCount(int followerCount) {
			this.followerCount = followerCount;
		}

		public boolean isCollaborating() {
			return collaborating;
		}

		public void setCollaborating(boolean collaborating) {
			this.collaborating = collaborating;
		}

		public boolean isFollowing() {
			return following;
		}

		public void setFollowing(boolean following) {
			this.following = following;
		}

		public boolean isPublished() {
			return published;
		}

		public void setPublished(boolean published) {
			this.published = published;
		}

		public int getDropCount() {
			return dropCount;
		}

		public void setDropCount(int dropCount) {
			this.dropCount = dropCount;
		}

		public String getDateAdded() {
			return dateAdded;
		}

		public void setDateAdded(String dateAdded) {
			this.dateAdded = dateAdded;
		}

		public Account getAccount() {
			return account;
		}

		public void setAccount(Account account) {
			this.account = account;
		}
	}
	
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

	public List<River> getRivers() {
		return rivers;
	}

	public void setRivers(List<River> rivers) {
		this.rivers = rivers;
	}

	public List<River> getCollaboratingRivers() {
		return collaboratingRivers;
	}

	public void setCollaboratingRivers(List<River> collaboratingRivers) {
		this.collaboratingRivers = collaboratingRivers;
	}

	public List<River> getFollowingRivers() {
		return followingRivers;
	}

	public void setFollowingRivers(List<River> followingRivers) {
		this.followingRivers = followingRivers;
	}

	public List<Bucket> getBuckets() {
		return buckets;
	}

	public void setBuckets(List<Bucket> buckets) {
		this.buckets = buckets;
	}

	public boolean isAccountPrivate() {
		return accountPrivate;
	}

	public void setAccountPrivate(boolean accountPrivate) {
		this.accountPrivate = accountPrivate;
	}

	public boolean isCollaborator() {
		return collaborator;
	}

	public void setCollaborator(boolean collaborator) {
		this.collaborator = collaborator;
	}

	public boolean isFollower() {
		return isFollower;
	}

	public void setFollower(boolean isFollower) {
		this.isFollower = isFollower;
	}

	public List<Bucket> getCollaboratingBuckets() {
		return collaboratingBuckets;
	}

	public void setCollaboratingBuckets(List<Bucket> collaboratingBuckets) {
		this.collaboratingBuckets = collaboratingBuckets;
	}

	public List<Bucket> getFollowingBuckets() {
		return followingBuckets;
	}

	public void setFollowingBuckets(List<Bucket> followingBuckets) {
		this.followingBuckets = followingBuckets;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}


}
