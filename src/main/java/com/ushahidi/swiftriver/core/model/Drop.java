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
@Table(name = "droplets")
public class Drop implements Serializable{

	private static final long serialVersionUID = 8626488270653399780L;

	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "identity_id")
	private Identity identity;

	@Column(name = "channel")
	private String channel;
	
	@Column(name = "droplet_hash")
	private String dropletHash;
	
	@Column(name = "droplet_orig_id")
	private String originId;
	
	@Column(name = "droplet_type")
	private String dropletType;
	
	@Column(name = "droplet_title")
	private String title;
	
	@Column(name = "droplet_content")
	private String content;
	
	@Column(name = "droplet_locale")
	private String dropletLocale;
	
	@Column(name = "droplet_image")
	private Long dropletImage;
	
	@Column(name = "droplet_date_pub")
	@Temporal(TemporalType.TIMESTAMP)
	private Date datePublished;
	
	@Column(name = "droplet_date_add")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateAdded;
	
	@Column(name = "original_url")
	private Long originalUrl;
	
	@Column(name = "comment_count")
	private int commentCount;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "droplets_tags",
			joinColumns = @JoinColumn(name="droplet_id"),
			inverseJoinColumns = @JoinColumn(name="tag_id"))
	private List<Tag> tags;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "droplets_places",
			joinColumns = @JoinColumn(name="droplet_id"),
			inverseJoinColumns = @JoinColumn(name="place_id"))
	private List<Place> places;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "droplets_links",
			joinColumns = @JoinColumn(name="droplet_id"),
			inverseJoinColumns = @JoinColumn(name="link_id"))
	private List<Link> links;
	
	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(
			name = "droplets_media",
			joinColumns = @JoinColumn(name="droplet_id"),
			inverseJoinColumns = @JoinColumn(name="media_id"))
	private List<Media> media;

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

	public String getOriginId() {
		return originId;
	}

	public void setOriginId(String originId) {
		this.originId = originId;
	}

	public String getDropletType() {
		return dropletType;
	}

	public void setDropletType(String dropletType) {
		this.dropletType = dropletType;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public Date getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
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

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public List<Place> getPlaces() {
		return places;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public List<Media> getMedia() {
		return media;
	}

	public void setMedia(List<Media> media) {
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
