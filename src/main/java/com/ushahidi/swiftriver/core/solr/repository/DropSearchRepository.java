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
package com.ushahidi.swiftriver.core.solr.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.ushahidi.swiftriver.core.api.filter.DropFilter;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.solr.DropDocument;

public interface DropSearchRepository {
	/**
	 * Finds and returns a {@link List} of {@link DropDocument} entities
	 * that contain the phrase specified in <code>searchTerm</code> in their
	 * title or content.
	 * 
	 * <b>NOTE:</b> Pages are zero-indexed, thus providing 0 for 
	 * <code>page</code> - in the {@link Pageable} instance for the 
	 * <code>pageable</code> parameter-  will return the first page
	 * 
	 * @param searchTerm
	 * @param pageable
	 * @return
	 */
	public List<DropDocument> find(String searchTerm, Pageable pageable);

	/**
	 * Returns all {@link DropDocument} entities in the {@link River} with the
	 * ID specified in <code>riverId</code> and contain <code>searchTerm</code>
	 * in their title or content
	 * 
	 * @param riverId
	 * @param dropFilter
	 * @param pageable
	 * @return
	 */
	public List<DropDocument> findInRiver(Long riverId, DropFilter dropFilter, 
			Pageable pageable);

	/**
	 * Returns all {@link DropDocument} entities in the {@link Bucket} with the
	 * ID specified in <code>bucket</code> and contain <code>searchTerm</code>
	 * in their title or content
	 * 
	 * @param bucketId
	 * @param dropFilter
	 * @param pageable
	 * @return
	 */
	public List<DropDocument> findInBucket(Long bucketId, DropFilter dropFilter,
			Pageable pageable);

}
