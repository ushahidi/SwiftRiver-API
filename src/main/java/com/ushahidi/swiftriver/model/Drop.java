/**
 * The contents of this file are subject to the Affero General
 * Public License (AGPL) Version 3; you may not use this file 
 * except in compliance with the License. You may obtain a copy
 * of the License at http://www.gnu.org/licenses/agpl.html
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;
import javax.persistence.*;

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
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
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
	private long dropletImage;
	
	@Column(name = "droplet_date_pub", nullable = false)
	private Timestamp dropletDatePub;
	
	@Column(name = "droplet_date_add")
	private Timestamp dropletDateAdd;
	
	@Column(name = "original_url")
	private long originalUrl;
	
	@Column(name = "comment_count")
	private int commentCount;
	
	@OneToMany
	@JoinTable(name = "droplets_tags", joinColumns = { @JoinColumn(name="droplet_id") })
	private Set<Tag> tags = null;
	
	@OneToMany
	@JoinTable(name = "droplets_places", joinColumns = { @JoinColumn(name="droplet_id") })
	private Set<Place> places = null;
	
	@OneToMany
	@JoinTable(name = "droplets_links", joinColumns = { @JoinColumn(name="droplet_id") })
	private Set<Link> links = null;
	
	@OneToMany
	@JoinTable(name = "droplets_media", joinColumns = { @JoinColumn(name="droplet_id") })
	private Set<Media> media = null;

	public Drop() {
		
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public long getDropletImage() {
		return dropletImage;
	}

	public void setDropletImage(long dropletImage) {
		this.dropletImage = dropletImage;
	}

	public Timestamp getDropletDatePub() {
		return dropletDatePub;
	}

	public void setDropletDatePub(Timestamp dropletDatePub) {
		this.dropletDatePub = dropletDatePub;
	}

	public Date getDropletDateAdd() {
		return dropletDateAdd;
	}

	public void setDropletDateAdd(Timestamp dropletDateAdd) {
		this.dropletDateAdd = dropletDateAdd;
	}

	public long getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(long originalUrl) {
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

	
}
