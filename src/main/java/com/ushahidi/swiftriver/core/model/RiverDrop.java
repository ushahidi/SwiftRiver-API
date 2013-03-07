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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="rivers_droplets")
public class RiverDrop {
	
	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne
	@JoinColumn(name="droplet_id")
	private Drop drop;
	
	@ManyToOne
	private River river;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="droplet_date_pub")
	private Date datePublished;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="riverDrop")
	private List<RiverDropLink> links;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="riverDrop")
	private List<RiverDropPlace> places;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="riverDrop")
	private List<RiverDropTag> tags;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Drop getDrop() {
		return drop;
	}

	public void setDrop(Drop drop) {
		this.drop = drop;
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

	public List<RiverDropLink> getLinks() {
		return links;
	}

	public void setLinks(List<RiverDropLink> links) {
		this.links = links;
	}

	public List<RiverDropPlace> getPlaces() {
		return places;
	}

	public void setPlaces(List<RiverDropPlace> places) {
		this.places = places;
	}

	public List<RiverDropTag> getTags() {
		return tags;
	}

	public void setTags(List<RiverDropTag> tags) {
		this.tags = tags;
	}

	
}
