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
package com.ushahidi.swiftriver.core.solr;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import com.ushahidi.swiftriver.core.model.Drop;

/**
 * Solr document class for {@link Drop} entities
 * 
 * @author ekala
 */
public class DropDocument {
	
	// Field names
	public static final String FIELD_ID = "id";
	public static final String FIELD_CHANNEL = "channel";
	public static final String FIELD_TITLE = "droplet_title";
	public static final String FIELD_CONTENT = "droplet_content";
	public static final String FIELD_DATE_PUB = "droplet_date_pub";

	@Field
	private String id;
	
	@Field
	private String channel;
	
	@Field("droplet_title")
	private String title;
	
	@Field("droplet_content")
	private String content;
	
	@Field("droplet_date_pub")
	private Date datePublished;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
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

}
