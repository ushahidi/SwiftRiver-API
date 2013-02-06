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

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.utils.SwiftRiverUtils;

/**
 * DTO mapping class for buckets
 * @author ekala
 *
 */
public class BucketDTO extends AbstractDTO<Bucket> {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> createMapFromEntity(Bucket entity) {
		// TODO: Add owner information to the bucket data
		Object[][] bucketData = {
				{"id", entity.getName()},
				{"name", entity.getName()}, 
				{"url", entity.getUrl()},
				{"public", entity.isPublished()},
				{"date_added", entity.getDateAdded()}};

		return ArrayUtils.toMap(bucketData);
	}

	@Override
	public Bucket createEntityFromMap(Map<String, Object> map) {
		Bucket bucket = new Bucket();

		String bucketName = (String) map.get("name");

		bucket.setName(bucketName);
		bucket.setUrl(SwiftRiverUtils.getURLSlug(bucketName));
		bucket.setPublished((Boolean) map.get("active"));
		
		return bucket;
	}

	@Override
	protected String[] getValidationKeys() {
		// TODO Auto-generated method stub
		return new String[]{"name", "public"};
	}

	@Override
	protected void copyFromMap(Bucket target, Map<String, Object> source) {
		Bucket bucket = createEntityFromMap(source);

		target.setName(bucket.getName());
		target.setUrl(bucket.getUrl());
		target.setPublished(bucket.isPublished());
		
	}

}
