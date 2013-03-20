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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Table(name = "buckets")
public class Bucket {

	@Id
	@GeneratedValue
	private long id;

	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;

	@Column(name = "bucket_name", nullable = false)
	private String name;
	
	@Column(name="bucket_name_canonical", nullable=false)
	private String bucketNameCanonical;

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

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "bucket")
	private List<BucketCollaborator> collaborators;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "bucket_followers", joinColumns = @JoinColumn(name = "bucket_id"), inverseJoinColumns = @JoinColumn(name = "account_id"))
	private List<Account> followers;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "bucket")
	private List<BucketComment> comments;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBucketNameCanonical() {
		return bucketNameCanonical;
	}

	public void setBucketNameCanonical(String bucketNameCanonical) {
		this.bucketNameCanonical = bucketNameCanonical;
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

	public int getDropCount() {
		return dropCount;
	}

	public void setDropCount(int dropCount) {
		this.dropCount = dropCount;
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
	
	public List<BucketComment> getComments() {
		return comments;
	}

	public void setComments(List<BucketComment> comments) {
		this.comments = comments;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(account).
	            append(bucketNameCanonical).
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

        Bucket other = (Bucket) obj;
        return new EqualsBuilder().
            append(account, other.account).
            append(bucketNameCanonical, other.bucketNameCanonical).
            isEquals();
	}
	
}
