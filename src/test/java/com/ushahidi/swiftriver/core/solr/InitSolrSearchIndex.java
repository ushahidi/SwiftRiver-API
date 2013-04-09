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

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import com.ushahidi.swiftriver.core.api.service.DropIndexService;
import com.ushahidi.swiftriver.core.model.Drop;

/**
 * Initializes the search index for the embedded solr server
 * by populating it with the drops test data.
 * 
 * @author ekala
 *
 */
@WebAppConfiguration
@ContextConfiguration(locations = {
		"file:src/main/webapp/WEB-INF/spring/root-context.xml",
		"file:src/main/webapp/WEB-INF/spring/web-context.xml",
		"file:src/main/webapp/WEB-INF/spring/solr-context.xml",
})
@ActiveProfiles(profiles = {"test"})
public class InitSolrSearchIndex {

	@Resource
	private DropIndexService dropIndexService;

	@PersistenceContext
	private EntityManager entityManager;

	public void init() {
		// First, purge all items from the index
		dropIndexService.deleteAllFromIndex();

		// Fetch the drops and them to the index 
		List<Drop> drops = entityManager.createQuery("FROM Drop", 
				Drop.class).getResultList();
		
		dropIndexService.addAllToIndex(drops);
	}
}
