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

import java.util.Collection;
import java.util.Date;

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

@Entity
@Table(name = "buckets")
public class Bucket  {
	
	@Id
	@GeneratedValue	
	private long id;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "account_id")
	private Account account;
	
	@Column(name = "bucket_name", nullable=false)
	private String bucketName;
	
	@Column(name = "bucket_description")
	private String description;
	
	@Column(name = "bucket_publish")
	private boolean published;
	
	@Column(name = "default_layout")
	private String defaultLayout;
	
	@Column(name = "bucket_date_add")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateAdded;
	
	@Column(name = "public_token")
	private String publicToken;
	
	@Column(name = "drop_count")
	private int dropCount;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "buckets_droplets",
			joinColumns = @JoinColumn(name="bucket_id"),
			inverseJoinColumns = @JoinColumn(name="droplet_id")
	)
	private Collection<Drop> drops = null;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "bucket_collaborators",
			joinColumns = @JoinColumn(name = "bucket_id"),
			inverseJoinColumns = @JoinColumn(name="account_id")
	)
	private Collection<Account> collaborators = null;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "bucket_subscriptions",
			joinColumns = @JoinColumn(name = "bucket_id"),
			inverseJoinColumns = @JoinColumn(name="account_id")
	)
	private Collection<Account> followers = null;
	
	public Bucket() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public String getDefaultLayout() {
		return defaultLayout;
	}

	public void setDefaultLayout(String defaultLayout) {
		this.defaultLayout = defaultLayout;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public String getPublicToken() {
		return publicToken;
	}

	public void setPublicToken(String publicToken) {
		this.publicToken = publicToken;
	}

	public Collection<Drop> getDrops() {
		return drops;
	}

	public void setDrops(Collection<Drop> drops) {
		this.drops = drops;
	}

	public Collection<Account> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(Collection<Account> collaborators) {
		this.collaborators = collaborators;
	}

	public Collection<Account> getFollowers() {
		return followers;
	}

	public void setFollowers(Collection<Account> followers) {
		this.followers = followers;
	}

	public int getDropCount() {
		return dropCount;
	}

	public void setDropCount(int dropCount) {
		this.dropCount = dropCount;
	}

}
