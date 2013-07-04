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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Model for identities
 * 
 * @author ekala
 */
@Entity
@Table(name = "identities")
public class Identity implements Serializable {
	
	private static final long serialVersionUID = -8738305639598973112L;

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="Seq") 
    @TableGenerator(name="Seq", table="seq", 
        pkColumnName="name", valueColumnName="id", 
        pkColumnValue="identities") 
	private long id;
	
	@Column(name="hash")
	private String hash;
	
	@Column(name="channel")
	private String channel;
	
	@Lob
	@Column(name="identity_orig_id")
	private String originId;
	
	@Column(name="identity_username")
	private String username;
	
	@Column(name="identity_name")
	private String name;
	
	@Lob
	@Column(name="identity_avatar")
	private String avatar;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="identity_date_add")
	private Date dateAdded;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="identity_date_modified")
	private Date dateModified;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="identity")
	private List<Drop> drops;
	
	public Identity() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getOriginId() {
		return originId;
	}

	public void setOriginId(String originId) {
		this.originId = originId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
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

	public List<Drop> getDrops() {
		return drops;
	}

	public void setDrops(List<Drop> drops) {
		this.drops = drops;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((hash == null) ? 0 : hash.hashCode());
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
		Identity other = (Identity) obj;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		return true;
	}
}
