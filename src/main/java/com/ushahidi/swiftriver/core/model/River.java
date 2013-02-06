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
 */package com.ushahidi.swiftriver.core.model;

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

@Entity
@Table(name="rivers")
public class River {

	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;
	
	@Column(name="river_name", nullable=false)
	private String riverName;
	
	@Column(name="river_active")
	private boolean active;
	
	@Column(name="river_public", nullable=false)
	private boolean riverPublic;
	
	@Column(name="default_layout")
	private String defaultLayout;
	
	@Column(name="river_date_add")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateAdded;
	
	@Column(name="max_drop_id")
	private long maxDropId;
	
	@Column(name="drop_count")
	private int dropCount;
	
	@Column(name="river_date_expiry")
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiryDate;
	
	@Column(name="river_expired")
	private boolean expired;
	
	@Column(name="extension_count")
	private int extensionCount;
	
	@Column(name="expiry_notification_sent")
	private boolean expiryNotificationSent;
	
	@Column(name="public_token")
	private String publicToken;
	
	@Column(name="drop_quota")
	private int dropQuota;
	
	@Column(name = "river_full")
	private boolean full;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "rivers_droplets", joinColumns = @JoinColumn(name="river_id"), inverseJoinColumns = @JoinColumn(name="droplet_id"))
	private List<Drop> drops = null;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name="river_collaborators", joinColumns = @JoinColumn(name="river_id"), inverseJoinColumns = @JoinColumn(name="account_id"))
	private List<Account> collaborators = null;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name="river_subscriptions", joinColumns = @JoinColumn(name="river_id"), inverseJoinColumns = @JoinColumn(name="account_id"))
	private List<Account> followers = null;

	@OneToMany(cascade = CascadeType.ALL, mappedBy="river")
	private List<Channel> channels = null;
	
	public River() {
		
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

	public String getRiverName() {
		return riverName;
	}

	public void setRiverName(String riverName) {
		this.riverName = riverName;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isRiverPublic() {
		return riverPublic;
	}

	public void setRiverPublic(boolean riverPublic) {
		this.riverPublic = riverPublic;
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

	public long getMaxDropId() {
		return maxDropId;
	}

	public void setMaxDropId(long maxDropId) {
		this.maxDropId = maxDropId;
	}

	public int getDropCount() {
		return dropCount;
	}

	public void setDropCount(int dropCount) {
		this.dropCount = dropCount;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public int getExtensionCount() {
		return extensionCount;
	}

	public void setExtensionCount(int extensionCount) {
		this.extensionCount = extensionCount;
	}

	public boolean isExpiryNotificationSent() {
		return expiryNotificationSent;
	}

	public void setExpiryNotificationSent(boolean expiryNotificationSent) {
		this.expiryNotificationSent = expiryNotificationSent;
	}

	public String getPublicToken() {
		return publicToken;
	}

	public void setPublicToken(String publicToken) {
		this.publicToken = publicToken;
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

	public List<Drop> getDrops() {
		return drops;
	}

	public void setDrops(List<Drop> drops) {
		this.drops = drops;
	}

	public List<Account> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(List<Account> collaborators) {
		this.collaborators = collaborators;
	}

	public List<Account> getFollowers() {
		return followers;
	}

	public void setFollowers(List<Account> followers) {
		this.followers = followers;
	}

	public List<Channel> getChannels() {
		return channels;
	}

	public void setChannels(List<Channel> channels) {
		this.channels = channels;
	}
	
}
