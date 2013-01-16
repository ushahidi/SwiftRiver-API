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
@Table(name = "buckets")
public class Bucket implements Serializable {
	
	private static final long serialVersionUID = 1566257762453998371L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)	
	private long id;
	
	@ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name = "account_id")
	private Account account;
	
	private User user;
	
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
	private Set<Drop> drops = null;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "bucket_collaborators",
			joinColumns = @JoinColumn(name = "bucket_id"),
			inverseJoinColumns = @JoinColumn(name="user_id")
	)
	private Set<User> collaborators = null;
	
	public Bucket() {
		
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public Date getBucketDateAdd() {
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

	public Set<Drop> getDrops() {
		return drops;
	}

	public void setDrops(Set<Drop> drops) {
		this.drops = drops;
	}

	public Set<User> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(Set<User> collaborators) {
		this.collaborators = collaborators;
	}

	public void setId(long id) {
		this.id = id;
	}

}
