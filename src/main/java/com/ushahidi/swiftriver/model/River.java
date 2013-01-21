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
 */package com.ushahidi.swiftriver.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
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
	@GeneratedValue
	private long id;
	
	@ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
	@JoinColumn(name = "account_id")
	private Account account;
	
	@Column(name="river_name", nullable=false)
	private String riverName;
	
	@Column(name="river_name_url", nullable=false)
	private String riverNameUrl;
	
	@Column(name="river_active")
	private boolean active;
	
	@Column(name="river_public", nullable=false)
	private boolean riverPublic;
	
	@Column(name="default_layout")
	private String defaultLayout;
	
	@Column(name="river_date_add")
	private Timestamp dateAdded;
	
	@Column(name="max_drop_id")
	private long maxDropId;
	
	@Column(name="drop_count")
	private long dropCount;
	
	@Column(name="river_date_expiry")
	private Timestamp expiryDate;
	
	@Column(name="river_expired")
	private boolean expired;
	
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
	@JoinTable(name = "rivers_droplets", joinColumns = @JoinColumn(name="river_id"), inverseJoinColumns = @JoinColumn(name="droplet_id"))
	private List<Drop> drops = null;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name="river_collaborators", joinColumns = @JoinColumn(name="river_id"), inverseJoinColumns = @JoinColumn(name="user_id"))
	private List<User> collaborators = null;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name="river_id")
	private List<Channel> channels = null;

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

	public Timestamp getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Timestamp dateAdded) {
		this.dateAdded = dateAdded;
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

	public List<Drop> getDrops() {
		return drops;
	}

	public void setDrops(List<Drop> drops) {
		this.drops = drops;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public List<User> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(List<User> collaborators) {
		this.collaborators = collaborators;
	}

	public Date getRiverDateAdd() {
		return getDateAdded();
	}

	public void setRiverDateAdd(Timestamp riverDateAdd) {
		this.setDateAdded(riverDateAdd);
	}


	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public List<Channel> getChannels() {
		return channels;
	}

	public void setChannels(List<Channel> channels) {
		this.channels = channels;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
		result = prime * result
				+ ((riverName == null) ? 0 : riverName.hashCode());
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
		River other = (River) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
		if (riverName == null) {
			if (other.riverName != null)
				return false;
		} else if (!riverName.equals(other.riverName))
			return false;
		return true;
	}
	
}
