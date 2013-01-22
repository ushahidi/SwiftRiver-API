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
package com.ushahidi.swiftriver.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;

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

import org.apache.commons.lang.ArrayUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ushahidi.swiftriver.SwiftRiverException;

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
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "account_id")
	private Account account;
	
	@Column(name="user_id")
	private int userId;
	
	@Column(name = "bucket_name", nullable=false)
	private String bucketName;
	
	@Column(name = "bucket_name_url", nullable=false)
	private String bucketNameUrl;
	
	@Column(name = "bucket_description")
	private String bucketDescription;
	
	@Column(name = "bucket_publish")
	private boolean bucketPublish;
	
	@Column(name = "default_layout")
	private String defaultLayout;
	
	@Column(name = "bucket_date_add")
	private Timestamp bucketDateAdd;
	
	@Column(name = "public_token")
	private String publicToken;
	
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
			inverseJoinColumns = @JoinColumn(name="user_id")
	)
	private Collection<User> collaborators = null;
	
	public Bucket() {
		
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

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getBucketNameUrl() {
		return bucketNameUrl;
	}

	public void setBucketNameUrl(String bucketNameUrl) {
		this.bucketNameUrl = bucketNameUrl;
	}

	public String getBucketDescription() {
		return bucketDescription;
	}

	public void setBucketDescription(String bucketDescription) {
		this.bucketDescription = bucketDescription;
	}

	public boolean isBucketPublish() {
		return bucketPublish;
	}

	public void setBucketPublish(boolean bucketPublish) {
		this.bucketPublish = bucketPublish;
	}

	public String getDefaultLayout() {
		return defaultLayout;
	}

	public void setDefaultLayout(String defaultLayout) {
		this.defaultLayout = defaultLayout;
	}

	public Timestamp getBucketDateAdd() {
		return bucketDateAdd;
	}

	public void setBucketDateAdd(Timestamp bucketDateAdd) {
		this.bucketDateAdd = bucketDateAdd;
	}

	public String getPublicToken() {
		return publicToken;
	}

	public void setPublicToken(String publicToken) {
		this.publicToken = publicToken;
	}

	public long getId() {
		return id;
	}

	public Collection<Drop> getDrops() {
		return drops;
	}

	public void setDrops(Collection<Drop> drops) {
		this.drops = drops;
	}

	public Collection<User> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(Collection<User> collaborators) {
		this.collaborators = collaborators;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result
				+ ((bucketName == null) ? 0 : bucketName.hashCode());
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
		if (bucketName == null) {
			if (other.bucketName != null)
				return false;
		} else if (!bucketName.equals(other.bucketName))
			return false;
		return true;
	}

	/**
	 * Returns a JSON representation of the bucket 
	 */
	@Override
	public String toString() {
		if (this.getAccount().equals(null)) {
			throw new SwiftRiverException("No data found in Bucket reference reference");
		}

		// 2D array representation of the bucket data
		Object[][] bucketData = {
				{"id", this.getId()},
				{"name", this.getBucketName()},
				{"description", this.getBucketDescription()},
				{"url", this.getBucketNameUrl()},
				{"date_added", this.getBucketDateAdd()},
				{"public", this.isBucketPublish()},
				{"account", this.getAccount().toString()}
		};

		// Serialize the array to JSON
		Gson gson = new GsonBuilder().create();
		return gson.toJson(ArrayUtils.toMap(bucketData));
	}

}
