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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
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
@Table(name="rivers")
public class River implements Serializable{

	private static final long serialVersionUID = -7099235346765215176L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	private Account account;
	
	@Column(name="river_name", nullable=false)
	private String name;
	
	@Column(name="river_name_url", nullable=false)
	private String url;
	
	@Column(name="river_active")
	private boolean active;
	
	@Column(name="river_public", nullable=false)
	private boolean riverPublic;
	
	@Column(name="default_layout")
	private String defaultLayout;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="river_date_add")
	private Date dateAdded;
	
	@Column(name="max_drop_id")
	private long maxDropId;
	
	@Column(name="drop_count")
	private long dropCount;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="river_date_expiry")
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
	private long dropQuota;
	
	@Column(name = "river_full")
	private boolean riverFull;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "rivers_droplets", joinColumns = @JoinColumn(name="river_id"), inverseJoinColumns = @JoinColumn(name="droplet_id"))
	private List<Drop> drops;
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy="river")
	private List<RiverCollaborator> collaborators;

	@OneToMany(cascade = CascadeType.ALL, mappedBy="river")
	private List<Channel> channels;

	public River() {
		
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
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


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
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


	public long getDropCount() {
		return dropCount;
	}


	public void setDropCount(long dropCount) {
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


	public List<RiverCollaborator> getCollaborators() {
		return collaborators;
	}


	public void setCollaborators(List<RiverCollaborator> collaborators) {
		this.collaborators = collaborators;
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
		River other = (River) obj;
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
