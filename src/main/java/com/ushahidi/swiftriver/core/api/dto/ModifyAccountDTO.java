package com.ushahidi.swiftriver.core.api.dto;

import org.codehaus.jackson.annotate.JsonProperty;

public class ModifyAccountDTO {

	@JsonProperty("account_path")
	private String accountPath;
	
	private User owner;
	
	@JsonProperty("private")
	private Boolean accountPrivate;
	
	@JsonProperty("river_quota_remaining")
	private Integer riverQuotaRemaining;
	
	private String token;
	
	public String getAccountPath() {
		return accountPath;
	}

	public void setAccountPath(String accountPath) {
		this.accountPath = accountPath;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Boolean getAccountPrivate() {
		return accountPrivate;
	}

	public void setAccountPrivate(Boolean accountPrivate) {
		this.accountPrivate = accountPrivate;
	}

	public Integer getRiverQuotaRemaining() {
		return riverQuotaRemaining;
	}

	public void setRiverQuotaRemaining(Integer riverQuotaRemaining) {
		this.riverQuotaRemaining = riverQuotaRemaining;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public static class User {
		
		private String name;
		
		private String email;
				
		private String password;
		
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

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	}
}
