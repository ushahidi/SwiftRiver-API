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
@Table(name = "buckets_droplets")
public class BucketDrop {
	
	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "droplet_id")
	private Drop drop;
	
	@ManyToOne
	private Bucket bucket;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="droplet_date_added")
	private Date dateAdded;
	
	@Column(name="droplet_veracity")
	private long veracity;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="bucketDrop")
	private List<BucketDropLink> links;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="bucketDrop")
	private List<BucketDropPlace> places;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="bucketDrop")
	private List<BucketDropTag> tags;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="bucketDrop")
	private List<BucketDropComment> comments;
	
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

	public Bucket getBucket() {
		return bucket;
	}

	public void setBucket(Bucket bucket) {
		this.bucket = bucket;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public long getVeracity() {
		return veracity;
	}

	public void setVeracity(long veracity) {
		this.veracity = veracity;
	}

	public List<BucketDropLink> getLinks() {
		return links;
	}

	public void setLinks(List<BucketDropLink> links) {
		this.links = links;
	}

	public List<BucketDropPlace> getPlaces() {
		return places;
	}

	public void setPlaces(List<BucketDropPlace> places) {
		this.places = places;
	}

	public List<BucketDropTag> getTags() {
		return tags;
	}

	public void setTags(List<BucketDropTag> tags) {
		this.tags = tags;
	}

	public List<BucketDropComment> getComments() {
		return comments;
	}

	public void setComments(List<BucketDropComment> comments) {
		this.comments = comments;
	}

}
