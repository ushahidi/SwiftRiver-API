/**
 * The contents of this file are subject to the Affero General
 * Public License (AGPL) Version 3; you may not use this file 
 * except in compliance with the License. You may obtain a copy
 * of the License at http://www.gnu.org/licenses/agpl.html
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;
import javax.persistence.*;

/**
 * 
 * @author ekala
 *
 */
@Entity
@Table(name="accounts")
public class Account implements Serializable{
	
	private static final long serialVersionUID = 370787290154418798L;

	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(name = "account_path", nullable = false, unique = true)
	private String accountPath;
	
	@Column(name = "account_private")
	private boolean accountPrivate;
	
	@Column(name="account_date_add")
	private Timestamp accountDateAdd;

	@Column(name = "account_date_modified")
	private Timestamp accountDateModified;
	
	@Column(name = "account_active")
	private boolean accountActive;
	
	@Column(name="river_quota_remaining")
	private int riverQuotaRemaining;
	
	@OneToMany
	@JoinTable(name = "user_followers", joinColumns = { @JoinColumn(name = "user_id") })
	private Set<Account> followers = null;
	
	@OneToMany
	@JoinTable(name = "user_followers", joinColumns = { @JoinColumn(name = "follower_id") })
	private Set<Account> following = null;
	
	public Account() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getAccountPath() {
		return accountPath;
	}

	public void setAccountPath(String accountPath) {
		this.accountPath = accountPath;
	}

	public boolean isAccountPrivate() {
		return accountPrivate;
	}

	public void setAccountPrivate(boolean accountPrivate) {
		this.accountPrivate = accountPrivate;
	}

	public Date getDateModified() {
		return accountDateModified;
	}

	public void setDateModified(Timestamp dateModified) {
		this.accountDateModified = dateModified;
	}

	public boolean isAccountActive() {
		return accountActive;
	}

	public void setAccountActive(boolean accountActive) {
		this.accountActive = accountActive;
	}

	public int getRiverQuotaRemaining() {
		return riverQuotaRemaining;
	}

	public void setRiverQuotaRemaining(int riverQuotaRemaining) {
		this.riverQuotaRemaining = riverQuotaRemaining;
	}


	public Set<Account> getFollowers() {
		return followers;
	}

	public void setFollowers(Set<Account> followers) {
		this.followers = followers;
	}

	public Set<Account> getFollowing() {
		return following;
	}

	public void setFollowing(Set<Account> following) {
		this.following = following;
	}

	public Date getAccountDateAdd() {
		return accountDateAdd;
	}

	public void setAccountDateAdd(Timestamp accountDateAdd) {
		this.accountDateAdd = accountDateAdd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accountPath == null) ? 0 : accountPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (accountPath == null) {
			if (other.accountPath != null)
				return false;
		} else if (!accountPath.equals(other.accountPath))
			return false;
		return true;
	}

}
