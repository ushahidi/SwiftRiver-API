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
package com.ushahidi.swiftriver.core.api.dto;

import java.util.Date;

import org.codehaus.jackson.annotate.JsonProperty;

import com.ushahidi.swiftriver.core.model.BucketCollaborator;
import com.ushahidi.swiftriver.core.model.RiverCollaborator;

/**
 * Generic DTO mapping class for {@link BucketCollaborator}
 * and {@link RiverCollaborator} entities
 * 
 * @author ekala
 *
 */
public class GetCollaboratorDTO {
	
	private long id;
	
	private String name;
	
	@JsonProperty("account_path")
	private String accountPath;
	
	private String email;
	
	private boolean active;
	
	@JsonProperty("read_only")
	private boolean readOnly;
	
	@JsonProperty("date_added")
	private Date dateAdded;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccountPath() {
		return accountPath;
	}

	public void setAccountPath(String accountPath) {
		this.accountPath = accountPath;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}
	
	
}
