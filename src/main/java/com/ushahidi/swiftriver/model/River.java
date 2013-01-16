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
@Table(name="rivers")
public class River implements Serializable{

	private static final long serialVersionUID = -7099235346765215176L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JoinColumn(name = "account_id")
	private Account account;
	
	@Column(name="river_name", nullable=false)
	private String riverName;
	
	@Column(name="river_name_url", nullable=false)
	private String riverNameUrl;
	
	@Column(name="river_active")
	private boolean riverActive;
	
	@Column(name="river_public", nullable=false)
	private boolean riverPublic;
	
	@Column(name="default_layout")
	private String defaultLayout;
	
	@Column(name="river_date_add")
	private Timestamp riverDateAdd;
	
	@Column(name="max_drop_id")
	private long maxDropId;
	
	@Column(name="drop_count")
	private long dropCount;
	
	@Column(name="river_date_expiry")
	private Timestamp riverDateExpiry;
	
	@Column(name="river_expired")
	private boolean riverExpired;
	
	@Column(name="extension_count")
	private int extensionCount;
	
	@Column(name="expiry_notification_sent")
	private boolean expiryNotificationSent;
	
	@Column(name="email_id")
	private String emailId;
	
	@Column(name="public_token")
	private String publicToken;
	
	@Column(name="drop_quota")
	private long dropQuota;
	
	@Column(name = "river_full")
	private boolean riverFull;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "rivers_droplets", joinColumns = @JoinColumn(name="river_id"))
	private Set<Drop> drops = null;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name="river_collaborators", joinColumns = @JoinColumn(name="river_id"))
	private Set<User> collaborators = null;

	public River() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRiverName() {
		return riverName;
	}

	public void setRiverName(String riverName) {
		this.riverName = riverName;
	}

	public String getRiverNameUrl() {
		return riverNameUrl;
	}

	public void setRiverNameUrl(String riverNameUrl) {
		this.riverNameUrl = riverNameUrl;
	}

	public boolean isRiverActive() {
		return riverActive;
	}

	public void setRiverActive(boolean riverActive) {
		this.riverActive = riverActive;
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

	public long getMaxDropId() {
		return maxDropId;
	}

	public void setMaxDropId(long maxDropId) {
		this.maxDropId = maxDropId;
	}

	public long getDropCount() {
		return dropCount;
	}

	public void setDropCount(long dropCount) {
		this.dropCount = dropCount;
	}

	public boolean isRiverExpired() {
		return riverExpired;
	}

	public void setRiverExpired(boolean riverExpired) {
		this.riverExpired = riverExpired;
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

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String getPublicToken() {
		return publicToken;
	}

	public void setPublicToken(String publicToken) {
		this.publicToken = publicToken;
	}

	public long getDropQuota() {
		return dropQuota;
	}

	public void setDropQuota(long dropQuota) {
		this.dropQuota = dropQuota;
	}

	public boolean isRiverFull() {
		return riverFull;
	}

	public void setRiverFull(boolean riverFull) {
		this.riverFull = riverFull;
	}

	public Set<Drop> getDrops() {
		return drops;
	}

	public void setDrops(Set<Drop> drops) {
		this.drops = drops;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Set<User> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(Set<User> collaborators) {
		this.collaborators = collaborators;
	}

	public Date getRiverDateAdd() {
		return riverDateAdd;
	}

	public void setRiverDateAdd(Timestamp riverDateAdd) {
		this.riverDateAdd = riverDateAdd;
	}

	public Date getRiverDateExpiry() {
		return riverDateExpiry;
	}

	public void setRiverDateExpiry(Timestamp riverDateExpiry) {
		this.riverDateExpiry = riverDateExpiry;
	}
	
}
