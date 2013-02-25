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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Table(name="rivers")
public class River {

	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;
	
	@Column(name="river_name", nullable=false)
	private String riverName;
	
	@Column(name="river_name_canonical", nullable=false)
	private String riverNameCanonical;
	
	private String description;
	
	@Column(name="river_active")
	private Boolean active;
	
	@Column(name="river_public", nullable=false)
	private Boolean riverPublic;
	
	@Column(name="default_layout")
	private String defaultLayout;
	
	@Column(name="river_date_add")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateAdded;
	
	@Column(name="max_drop_id")
	private Long maxDropId;
	
	@Column(name="drop_count")
	private Integer dropCount;
	
	@Column(name="river_date_expiry")
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiryDate;
	
	@Column(name="river_expired")
	private Boolean expired;
	
	@Column(name="extension_count")
	private Integer extensionCount;
	
	@Column(name="expiry_notification_sent")
	private Boolean expiryNotificationSent;
	
	@Column(name="public_token")
	private String publicToken;
	
	@Column(name="drop_quota")
	private Integer dropQuota;
	
	@Column(name = "river_full")
	private Boolean full;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="river")
	private List<RiverCollaborator> collaborators;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name="river_followers", joinColumns = @JoinColumn(name="river_id"), inverseJoinColumns = @JoinColumn(name="account_id"))
	private List<Account> followers;

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

	public String getRiverName() {
		return riverName;
	}

	public void setRiverName(String riverName) {
		this.riverName = riverName;
	}

	public String getRiverNameCanonical() {
		return riverNameCanonical;
	}

	public void setRiverNameCanonical(String riverNameCanonical) {
		this.riverNameCanonical = riverNameCanonical;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getRiverPublic() {
		return riverPublic;
	}

	public void setRiverPublic(Boolean riverPublic) {
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

	public Long getMaxDropId() {
		return maxDropId;
	}

	public void setMaxDropId(Long maxDropId) {
		this.maxDropId = maxDropId;
	}

	public Integer getDropCount() {
		return dropCount;
	}

	public void setDropCount(Integer dropCount) {
		this.dropCount = dropCount;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public Boolean getExpired() {
		return expired;
	}

	public void setExpired(Boolean expired) {
		this.expired = expired;
	}

	public Integer getExtensionCount() {
		return extensionCount;
	}

	public void setExtensionCount(Integer extensionCount) {
		this.extensionCount = extensionCount;
	}

	public Boolean getExpiryNotificationSent() {
		return expiryNotificationSent;
	}

	public void setExpiryNotificationSent(Boolean expiryNotificationSent) {
		this.expiryNotificationSent = expiryNotificationSent;
	}

	public String getPublicToken() {
		return publicToken;
	}

	public void setPublicToken(String publicToken) {
		this.publicToken = publicToken;
	}

	public Integer getDropQuota() {
		return dropQuota;
	}

	public void setDropQuota(Integer dropQuota) {
		this.dropQuota = dropQuota;
	}

	public Boolean getFull() {
		return full;
	}

	public void setFull(Boolean full) {
		this.full = full;
	}

	public List<RiverCollaborator> getCollaborators() {
		return collaborators;
	}

	public void setCollaborators(List<RiverCollaborator> collaborators) {
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

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(account).
	            append(riverNameCanonical).
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

        River other = (River) obj;
        return new EqualsBuilder().
            append(account, other.account).
            append(riverNameCanonical, other.riverNameCanonical).
            isEquals();
	}
}
