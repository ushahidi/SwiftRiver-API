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
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * 
 * @author ekala
 *
 */
@Entity
@Table(name = "droplets")
public class Drop implements Serializable{

	private static final long serialVersionUID = 8626488270653399780L;

	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "identity_id")
	private Identity identity;

	@Column(name = "channel", nullable = false)
	private String channel;
	
	@Column(name = "droplet_hash", nullable = false)
	private String dropletHash;
	
	@Column(name = "droplet_orig_id", nullable = false)
	private String dropletOrigId;
	
	@Column(name = "droplet_type", nullable = false)
	private String dropletType;
	
	@Column(name = "droplet_title", nullable = false)
	private String dropletTitle;
	
	@Column(name = "droplet_content", nullable = false)
	private String dropletContent;
	
	@Column(name = "droplet_locale")
	private String dropletLocale;
	
	@Column(name = "droplet_image")
	private Long dropletImage;
	
	@Column(name = "droplet_date_pub", nullable = false)
	private Timestamp datePublished;
	
	@Column(name = "droplet_date_add")
	private Timestamp dateAdded;
	
	@Column(name = "original_url")
	private Long originalUrl;
	
	@Column(name = "comment_count")
	private int commentCount;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
			name = "droplets_tags",
			joinColumns = @JoinColumn(name="droplet_id"),
			inverseJoinColumns = @JoinColumn(name="tag_id"))
	private Set<Tag> tags = new HashSet<Tag>();
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
			name = "droplets_places",
			joinColumns = @JoinColumn(name="droplet_id"),
			inverseJoinColumns = @JoinColumn(name="place_id"))
	private Set<Place> places = new HashSet<Place>();
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
			name = "droplets_links",
			joinColumns = @JoinColumn(name="droplet_id"),
			inverseJoinColumns = @JoinColumn(name="link_id"))
	private Set<Link> links = new HashSet<Link>();
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
			name = "droplets_media",
			joinColumns = @JoinColumn(name="droplet_id"),
			inverseJoinColumns = @JoinColumn(name="media_id"))
	private Set<Media> media = new HashSet<Media>();

	public Drop() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Identity getIdentity() {
		return identity;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getDropletHash() {
		return dropletHash;
	}

	public void setDropletHash(String dropletHash) {
		this.dropletHash = dropletHash;
	}

	public String getDropletOrigId() {
		return dropletOrigId;
	}

	public void setDropletOrigId(String dropletOrigId) {
		this.dropletOrigId = dropletOrigId;
	}

	public String getDropletType() {
		return dropletType;
	}

	public void setDropletType(String dropletType) {
		this.dropletType = dropletType;
	}

	public String getDropletTitle() {
		return dropletTitle;
	}

	public void setDropletTitle(String dropletTitle) {
		this.dropletTitle = dropletTitle;
	}

	public String getDropletContent() {
		return dropletContent;
	}

	public void setDropletContent(String dropletContent) {
		this.dropletContent = dropletContent;
	}

	public String getDropletLocale() {
		return dropletLocale;
	}

	public void setDropletLocale(String dropletLocale) {
		this.dropletLocale = dropletLocale;
	}

	public Long getDropletImage() {
		return dropletImage;
	}

	public void setDropletImage(Long dropletImage) {
		this.dropletImage = dropletImage;
	}

	public Timestamp getDropletDatePub() {
		return datePublished;
	}

	public void setDropletDatePub(Timestamp dropletDatePub) {
		this.datePublished = dropletDatePub;
	}

	public Date getDropletDateAdd() {
		return dateAdded;
	}

	public void setDropletDateAdd(Timestamp dropletDateAdd) {
		this.dateAdded = dropletDateAdd;
	}

	public Long getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(Long originalUrl) {
		this.originalUrl = originalUrl;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public Set<Place> getPlaces() {
		return places;
	}

	public void setPlaces(Set<Place> places) {
		this.places = places;
	}

	public Set<Link> getLinks() {
		return links;
	}

	public void setLinks(Set<Link> links) {
		this.links = links;
	}

	public Set<Media> getMedia() {
		return media;
	}

	public void setMedia(Set<Media> media) {
		this.media = media;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dropletHash == null) ? 0 : dropletHash.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
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
		Drop other = (Drop) obj;
		if (dropletHash == null) {
			if (other.dropletHash != null)
				return false;
		} else if (!dropletHash.equals(other.dropletHash))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

}
