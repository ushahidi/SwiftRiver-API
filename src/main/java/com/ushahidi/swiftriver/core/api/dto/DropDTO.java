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
import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;


/**
 * DTO mapping class for {@link Drop} entities
 * 
 * @author ekala
 */
public class DropDTO {
	
	private long id;
	
	private String channel;
	
	@JsonProperty("source_id")
	private String originId;
	
	@JsonProperty("source_url")
	private String originalUrl;
	
	private String title;
	
	private String content;
	
	@JsonProperty("date_published")
	private Date datePublished;
	
	@JsonProperty("comment_count")
	private int commentCount;
	
	@JsonProperty("source")
	private IdentityDTO identity;
	
	private List<BucketDTO> buckets;
	
	@JsonProperty("user_score")
	private int userScore;
	
	private List<TagDTO>tags;
	
	private List<PlaceDTO> places;
	
	private List<LinkDTO> links;
	
	private List<MediaDTO> media;
	

	/** DTO mapping class for {@link Identity} entities */
	public static class IdentityDTO {
		
		private long id;
		
		private String name;
		
		private String username;
		
		private String avatar;

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

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getAvatar() {
			return avatar;
		}

		public void setAvatar(String avatar) {
			this.avatar = avatar;
		}		
		
	}


	/** DTO mapping class for {@link Tag} entities */
	public static class TagDTO {
		
		private long id;
		
		private String tag;
		
		@JsonProperty("tag_type")
		private String tagType;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
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
		
	}
	

	/** DTO mapping class for {@link Place} entities */
	public static class PlaceDTO {
		private long id;
		
		private String name;
		
		private double[] coordinates;
		
		private double longitude;
		
		private double latitude;

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

		public double[] getCoordinates() {
			return coordinates;
		}

		public void setCoordinates(double[] coordinates) {
			this.coordinates = coordinates;
		}

		public double getLongitude() {
			return longitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}
		
	}
	

	/** DTO mapping class for {@link Link} entities */
	public static class LinkDTO {
		private long id;
		
		private String url;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

	}
	

	/** DTO mapping class for {@link Media} entities */
	public static class MediaDTO {
		private long id;
		
		private String url;
		
		private String type;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
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

	public String getOriginId() {
		return originId;
	}

	public void setOriginId(String originId) {
		this.originId = originId;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
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

	public Date getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(Date datePublished) {
		this.datePublished = datePublished;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public IdentityDTO getIdentity() {
		return identity;
	}

	public void setIdentity(IdentityDTO identity) {
		this.identity = identity;
	}

	public List<BucketDTO> getBuckets() {
		return buckets;
	}

	public void setBuckets(List<BucketDTO> buckets) {
		this.buckets = buckets;
	}

	public int getUserScore() {
		return userScore;
	}

	public void setUserScore(int userScore) {
		this.userScore = userScore;
	}

	public List<TagDTO> getTags() {
		return tags;
	}

	public void setTags(List<TagDTO> tags) {
		this.tags = tags;
	}

	public List<PlaceDTO> getPlaces() {
		return places;
	}

	public void setPlaces(List<PlaceDTO> places) {
		this.places = places;
	}

	public List<LinkDTO> getLinks() {
		return links;
	}

	public void setLinks(List<LinkDTO> links) {
		this.links = links;
	}

	public List<MediaDTO> getMedia() {
		return media;
	}

	public void setMedia(List<MediaDTO> media) {
		this.media = media;
	}
	
	
}
