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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * This <code>RiverTagTrend</code> class is a persistence class
 * for the  <code>river_tag_trends</code> table. 
 * 
 * @author ekala
 *
 */
@Entity
@Table(name = "river_tag_trends")
public class RiverTagTrend {

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE, generator="Seq")
	@TableGenerator(name="Seq", table="seq",
		pkColumnName="name",
		valueColumnName="id",
		pkColumnValue="river_tag_trends")
	private long id;
	
	private String hash;
	
	@ManyToOne
	private River river;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date_pub")
	private Date datePublished;
	
	private String tag;
	
	@Column(name = "tag_type")
	private String tagType;
	
	private long count;

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

	public River getRiver() {
		return river;
	}

	public void setRiver(River river) {
		this.river = river;
	}

	public Date getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTagType() {
		return tagType;
	}

	public void setTagType(String tagType) {
		this.tagType = tagType;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
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
		RiverTagTrend other = (RiverTagTrend) obj;
		if (hash == null) {
			if (other.hash != null)
				return false;
		} else if (!hash.equals(other.hash))
			return false;
		return true;
	}
	
	
}
