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

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

public class GetDropDTO {

	private long id;
	
	private String title;
	
	private String content;
	
	private String channel;
	
	@JsonProperty("source")
	private Identity identity;
	
	@JsonProperty("date_published")
	private String datePublished;
	
	@JsonProperty("user_score")
	private int userScore;
	
	@JsonProperty("original_url")
	private String originalUrl;
	
	@JsonProperty("original_id")
	private String originalId;
	
	@JsonProperty("comment_count")
	private int commentCount;
	
	private GetMediaDTO image;
	
	private List<Bucket> buckets;
	
	private List<GetTagDTO> tags;
	
	private List<GetLinkDTO> links;
	
	private List<GetMediaDTO> media;
	
	private List<GetPlaceDTO> places;
	
	private Boolean read;
	
	private List<FormValueDTO> forms;
	
	public static class Identity {
		
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
	
	public static class Bucket {
		
		private long id;
		
		private String name;

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
	}
	
	public static class GetTagDTO {
		
		private long id;
		
		private String tag;
		
		private String type;

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

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}
	
	public static class GetLinkDTO {
		
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
	
	public static class GetMediaDTO {
		
		private long id;
		
		private String url;
		
		private String type;
		
		private List<MediaThumbnail> thumbnails;

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
		
		public List<MediaThumbnail> getThumbnails() {
			return thumbnails;
		}

		public void setThumbnails(List<MediaThumbnail> thumbnails) {
			this.thumbnails = thumbnails;
		}

		public static class MediaThumbnail {
			
			private int size;
			
			private String url;

			public int getSize() {
				return size;
			}

			public void setSize(int size) {
				this.size = size;
			}

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}
			
		}
	}
	
	public static class GetPlaceDTO {
		
		private long id;
		
		private String type;
		
		@JsonProperty("name")
		private String placeName;
		
		private float longitude;
		
		private float latitude;

		public long getId() {
			return id;
		}

		public void setId(long id) {
			this.id = id;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getPlaceName() {
			return placeName;
		}

		public void setPlaceName(String placeName) {
			this.placeName = placeName;
		}

		public float getLongitude() {
			return longitude;
		}

		public void setLongitude(float longitude) {
			this.longitude = longitude;
		}

		public float getLatitude() {
			return latitude;
		}

		public void setLatitude(float latitude) {
			this.latitude = latitude;
		}
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Identity getIdentity() {
		return identity;
	}

	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	public String getDatePublished() {
		return datePublished;
	}

	public void setDatePublished(String datePublished) {
		this.datePublished = datePublished;
	}

	public int getUserScore() {
		return userScore;
	}

	public void setUserScore(int userScore) {
		this.userScore = userScore;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public String getOriginalId() {
		return originalId;
	}

	public void setOriginalId(String originalId) {
		this.originalId = originalId;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public GetMediaDTO getImage() {
		return image;
	}

	public void setImage(GetMediaDTO image) {
		this.image = image;
	}

	public List<Bucket> getBuckets() {
		return buckets;
	}

	public void setBuckets(List<Bucket> buckets) {
		this.buckets = buckets;
	}

	public List<GetTagDTO> getTags() {
		return tags;
	}

	public void setTags(List<GetTagDTO> tags) {
		this.tags = tags;
	}

	public List<GetLinkDTO> getLinks() {
		return links;
	}

	public void setLinks(List<GetLinkDTO> links) {
		this.links = links;
	}

	public List<GetMediaDTO> getMedia() {
		return media;
	}

	public void setMedia(List<GetMediaDTO> media) {
		this.media = media;
	}

	public List<GetPlaceDTO> getPlaces() {
		return places;
	}

	public void setPlaces(List<GetPlaceDTO> places) {
		this.places = places;
	}

	public Boolean getRead() {
		return read;
	}

	public void setRead(Boolean read) {
		this.read = read;
	}

	public List<FormValueDTO> getForms() {
		return forms;
	}

	public void setForms(List<FormValueDTO> forms) {
		this.forms = forms;
	}
}
