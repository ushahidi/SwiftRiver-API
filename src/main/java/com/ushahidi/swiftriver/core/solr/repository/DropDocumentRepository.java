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
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.data.web.PageableDefaults;

import com.ushahidi.swiftriver.core.solr.DropDocument;

public interface DropDocumentRepository extends SolrCrudRepository<DropDocument, String> {
	
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
	@Query(value = "title:*?0* OR content:*?0*")
	public List<DropDocument> findByTitleOrContentContains(String searchTerm,
			@PageableDefaults(pageNumber = 0, value = 50) Pageable pageable);
	
	/**
	 * Returns all {@link DropDocument} entities with the IDs
	 * specified in <code>ids</code>
	 * 
	 * @param ids
	 * @return
	 */
	@Query(value = "id:(?0)")
	public List<DropDocument> findAll(List<String> ids);
		
}
