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

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * DTO mapping class for creating {@link Bucket} entities
 * 
 * @author ekala
 *
 */
public class CreateBucketDTO {
	
	private String name;
	
	@JsonProperty("public")
	private Boolean published;
	
	@JsonProperty("default_layout")
	private String defaultLayout;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean isPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	public String getDefaultLayout() {
		return defaultLayout;
	}

	public void setDefaultLayout(String defaultLayout) {
		this.defaultLayout = defaultLayout;
	}

}
