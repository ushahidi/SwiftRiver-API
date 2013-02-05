/**
 * The contents of this file are subject to the Affero General
 * Public License (AGPL) Version 3; you may not use this file 
 * except in compliance with the License. You may obtain a copy
 * of the License at http://www.gnu.org/licenses/agpl.html
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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

}
