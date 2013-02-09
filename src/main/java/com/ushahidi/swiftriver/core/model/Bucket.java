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

import java.io.Serializable;
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

/**
 * 
 * @author ekala
 *
 */
@Entity
@Table(name = "buckets")
public class Bucket implements Serializable {
	
	private static final long serialVersionUID = 1566257762453998371L;

	@Id
	@GeneratedValue	
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;
	
	@Column(name="user_id")
	private int userId;
	
	@Column(name = "bucket_name", nullable=false)
	private String name;
	
	@Column(name = "bucket_name_url", nullable=false)
	private String url;
	
	@Column(name = "bucket_description")
	private String description;
	
	@Column(name = "bucket_publish")
	private boolean published;
	
	@Column
	private boolean active;
	
	@Column(name = "default_layout")
	private String defaultLayout;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "bucket_date_add")
	private Date dateAdded;
	
	@Column(name = "public_token")
	private String publicToken;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "buckets_droplets",
			joinColumns = @JoinColumn(name="bucket_id"),
			inverseJoinColumns = @JoinColumn(name="droplet_id")
	)
	private List<Drop> drops;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy="bucket")
	private List<BucketCollaborator> collaborators;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name="bucket_followers", joinColumns=@JoinColumn(name="bucket_id"), inverseJoinColumns=@JoinColumn(name="account_id"))
	private List<Account> followers;
	
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

	public int getUser() {
		return userId;
	}

	public void setUser(int userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public List<Drop> getDrops() {
		return drops;
	}

	public void setDrops(List<Drop> drops) {
		this.drops = drops;
	}

	public List<BucketCollaborator> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(List<BucketCollaborator> collaborators) {
		this.collaborators = collaborators;
	}

	public List<Account> getFollowers() {
		return followers;
	}

	public void setFollowers(List<Account> followers) {
		this.followers = followers;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result
				+ ((name == null) ? 0 : name.hashCode());
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
		Bucket other = (Bucket) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
