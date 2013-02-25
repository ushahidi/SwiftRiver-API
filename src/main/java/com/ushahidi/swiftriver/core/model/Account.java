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
package com.ushahidi.swiftriver.core.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Table(name = "accounts")
public class Account {


	@Id
	@GeneratedValue
	private long id;

	@Column(name = "account_path")
	private String accountPath;

	@Column(name = "account_private")
	private boolean accountPrivate;

	@Column(name = "account_active")
	private boolean active;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "account_date_add")
	private Date dateAdded;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "account_date_modified")
	private Date dateModified;

	@Column(name = "river_quota_remaining")
	private int riverQuotaRemaining;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User owner;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="account")
	private List<AccountFollower> followers;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="follower_id")
	private List<AccountFollower> following;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="account")
	private List<River> rivers;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinTable(name="river_collaborators", joinColumns = @JoinColumn(name="account_id"), inverseJoinColumns = @JoinColumn(name="river_id"))
	private List<River> collaboratingRivers;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinTable(name="river_followers", joinColumns = @JoinColumn(name="account_id"), inverseJoinColumns = @JoinColumn(name="river_id"))
	private List<River> followingRivers;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="account")
	private List<Bucket> buckets;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinTable(name="bucket_followers", joinColumns = @JoinColumn(name="account_id"), inverseJoinColumns = @JoinColumn(name="bucket_id"))
	private List<Bucket> followingBuckets;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinTable(name="bucket_collaborators", joinColumns = @JoinColumn(name="account_id"), inverseJoinColumns = @JoinColumn(name="bucket_id"))
	private List<Bucket> collaboratingBuckets;
	
	@Version
	private long version;
	
	public Account() {

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

	public boolean isAccountPrivate() {
		return accountPrivate;
	}

	public void setAccountPrivate(boolean accountPrivate) {
		this.accountPrivate = accountPrivate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public int getRiverQuotaRemaining() {
		return riverQuotaRemaining;
	}

	public void setRiverQuotaRemaining(int riverQuotaRemaining) {
		this.riverQuotaRemaining = riverQuotaRemaining;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
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

	public List<Bucket> getFollowingBuckets() {
		return followingBuckets;
	}

	public void setFollowingBuckets(List<Bucket> followingBuckets) {
		this.followingBuckets = followingBuckets;
	}

	public List<Bucket> getCollaboratingBuckets() {
		return collaboratingBuckets;
	}

	public void setCollaboratingBuckets(List<Bucket> collaboratingBuckets) {
		this.collaboratingBuckets = collaboratingBuckets;
	}

	public List<AccountFollower> getFollowers() {
		return followers;
	}

	public void setFollowers(List<AccountFollower> followers) {
		this.followers = followers;
	}

	public List<AccountFollower> getFollowing() {
		return following;
	}

	public void setFollowing(List<AccountFollower> following) {
		this.following = following;
	}

	public long getVersion() {
		return version;
	}

	protected void setVersion(long version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(accountPath).
	            toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;

        Account other = (Account) obj;
        return new EqualsBuilder().
            append(accountPath, other.accountPath).
            isEquals();
	}
}
