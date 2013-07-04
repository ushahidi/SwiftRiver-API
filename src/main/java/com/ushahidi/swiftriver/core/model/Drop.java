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
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "droplets")
public class Drop {

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="Seq") 
    @TableGenerator(name="Seq", table="seq", 
        pkColumnName="name", valueColumnName="id", 
        pkColumnValue="droplets") 
	private long id;
	
	@Column(name = "droplet_hash", nullable = false)
	private String hash;
	
	@Column(name = "channel", nullable = false)
	private String channel;
	
	@Lob
	@Column(name = "droplet_title", nullable = false)
	private String title;

	@Lob
	@Column(name = "droplet_content", nullable = false)
	private String content;

	@ManyToOne
	@JoinColumn(name = "identity_id")
	private Identity identity;
	
	@Column(name = "droplet_date_pub", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date datePublished;
	
	@ManyToOne
	@JoinColumn(name = "original_url")
	private Link originalUrl;

	@Column(name = "droplet_orig_id", nullable = false)
	private String originalId;
	
	@Column(name = "comment_count")
	private int commentCount;

	@Column(name = "droplet_type", nullable = false)
	private String type;

	@Column(name = "droplet_locale")
	private String locale;

	@ManyToOne
	@JoinColumn(name = "droplet_image")
	private Media image;

	@Column(name = "droplet_date_add")
	@Temporal(TemporalType.TIMESTAMP)
	private Date dateAdded;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "droplets_tags", joinColumns = @JoinColumn(name = "droplet_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
	private List<Tag> tags;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "droplets_places", joinColumns = @JoinColumn(name = "droplet_id"), inverseJoinColumns = @JoinColumn(name = "place_id"))
	private List<Place> places;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "droplets_links", joinColumns = @JoinColumn(name = "droplet_id"), inverseJoinColumns = @JoinColumn(name = "link_id"))
	private List<Link> links;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "droplets_media", joinColumns = @JoinColumn(name = "droplet_id"), inverseJoinColumns = @JoinColumn(name = "media_id"))
	private List<Media> media;
	
	/* List of buckets this drop is in*/ 
	@Transient
	private List<Bucket> buckets;
	
	@Transient
	private List<Long> riverIds;
	
	@Transient
	private Boolean read;
	
	@SuppressWarnings("rawtypes")
	@Transient
	private List<DropForm> forms;

	@Transient
	private List<Long> markAsRead;
	
	@Transient
	private List<Long> bucketIds;
	
	@Transient
	private List<Long> channelIds;
	
	@Transient
	private Long trackingId;

	public Drop() {

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

	public String getOriginalId() {
		return originalId;
	}

	public void setOriginalId(String originalId) {
		this.originalId = originalId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getLocale() {
		return locale;
	}

	public void setLocal(String locale) {
		this.locale = locale;
	}

	public Media getImage() {
		return image;
	}

	public void setImage(Media image) {
		this.image = image;
	}

	public Date getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(Date dropletDatePub) {
		this.datePublished = dropletDatePub;
	}

	public Date getDateAdded() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public Link getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(Link originalUrl) {
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

	public List<Bucket> getBuckets() {
		return buckets;
	}

	public void setBuckets(List<Bucket> buckets) {
		this.buckets = buckets;
	}

	public List<Long> getRiverIds() {
		return riverIds;
	}

	public void setRiverIds(List<Long> riverIds) {
		this.riverIds = riverIds;
	}

	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	@SuppressWarnings("rawtypes")
	public List<DropForm> getForms() {
		return forms;
	}

	@SuppressWarnings("rawtypes")
	public void setForms(List<DropForm> forms) {
		this.forms = forms;
	}

	public List<Long> getMarkAsRead() {
		return markAsRead;
	}

	public void setMarkAsRead(List<Long> markAsRead) {
		this.markAsRead = markAsRead;
	}

	public List<Long> getBucketIds() {
		return bucketIds;
	}

	public void setBucketIds(List<Long> bucketIds) {
		this.bucketIds = bucketIds;
	}

	public List<Long> getChannelIds() {
		return channelIds;
	}

	public void setChannelIds(List<Long> channelIds) {
		this.channelIds = channelIds;
	}

	public Long getTrackingId() {
		return trackingId;
	}

	public void setTrackingId(Long trackingId) {
		this.trackingId = trackingId;
	}

}
