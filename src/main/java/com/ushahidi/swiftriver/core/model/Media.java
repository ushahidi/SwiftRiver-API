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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Table(name = "media")
public class Media  {
	
	@Id
	private long id;
	
	@Column(name = "hash", nullable = false)
	private String hash;
	
	@Column(name = "url", nullable = false)
	private String url;
	
	@Column(name = "type", nullable = false)
	private String type;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="media")
	private List<MediaThumbnail> thumbnails;
	
	public Media() {
		
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<MediaThumbnail> getThumbnails() {
		return thumbnails;
	}

	public void setThumbnails(List<MediaThumbnail> thumbnails) {
		this.thumbnails = thumbnails;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).
	            append(url).
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

        Media other = (Media) obj;
        return new EqualsBuilder().
            append(url, other.url).
            isEquals();
	}

}
