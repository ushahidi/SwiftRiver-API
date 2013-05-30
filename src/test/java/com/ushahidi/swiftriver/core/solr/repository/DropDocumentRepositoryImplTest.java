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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;

import com.ushahidi.swiftriver.core.api.filter.DropFilter;
import com.ushahidi.swiftriver.core.solr.DropDocument;

public class DropDocumentRepositoryImplTest {

	private SolrServer mockSolrServer;

	private DropDocumentRepositoryImpl dropSearchRepository;
	
	@Before
	public void setUp() {
		mockSolrServer = mock(SolrServer.class);
		SolrTemplate solrTemplate = new SolrTemplate(mockSolrServer);

		dropSearchRepository = new DropDocumentRepositoryImpl();
		dropSearchRepository.setSolrTemplate(solrTemplate);
	}
	
	@Test
	public void find() throws Exception {
		PageRequest page = new PageRequest(0, 20);
		String searchTerm = "test search phrase";
		QueryResponse mockQueryResponse = mock(QueryResponse.class);
		List<DropDocument> foundDocuments = new ArrayList<DropDocument>();

		when(mockSolrServer.query(any(SolrQuery.class))).thenReturn(mockQueryResponse);
		when(mockQueryResponse.getBeans(DropDocument.class)).thenReturn(foundDocuments);

		dropSearchRepository.find(searchTerm, page);
		ArgumentCaptor<SolrQuery> solrQueryArgument = ArgumentCaptor.forClass(SolrQuery.class);
		
		verify(mockSolrServer).query(solrQueryArgument.capture());

		SolrQuery solrQuery = solrQueryArgument.getValue();
		assertEquals(searchTerm, solrQuery.getQuery());
		assertEquals("edismax", solrQuery.get("defType"));
	}
	
	@Test
	public void findInRiverWithBoundingBox() throws Exception {
		QueryResponse mockQueryResponse = mock(QueryResponse.class);
		List<DropDocument> foundDocuments = new ArrayList<DropDocument>();

		when(mockSolrServer.query(any(SolrQuery.class))).thenReturn(mockQueryResponse);
		when(mockQueryResponse.getBeans(DropDocument.class)).thenReturn(foundDocuments);

		Pageable pageRequest = new PageRequest(0, 20);
		DropFilter dropFilter = new DropFilter();
		dropFilter.setBoundingBox("36.8,-122.75,37.8,-121.75");

		dropSearchRepository.findInRiver(1L, dropFilter, pageRequest);
		ArgumentCaptor<SolrQuery> solrQueryArgument = ArgumentCaptor.forClass(SolrQuery.class);
		
		verify(mockSolrServer).query(solrQueryArgument.capture());
		SolrQuery solrQuery = solrQueryArgument.getValue();
		
		String[] expectedFilterQuery = new String[]{
				"riverId:(1)",
				"geo:[36.8,-122.75 TO 37.8,-121.75]"
				};
		
		String[] actualFilterQuery = solrQuery.getFilterQueries();
		
		assertEquals("*:*", solrQuery.getQuery());
		assertEquals(2, actualFilterQuery.length);
		assertEquals(expectedFilterQuery[0], actualFilterQuery[0]);
		assertEquals(expectedFilterQuery[1], actualFilterQuery[1]);
	}

	@Test
	public void findInBucketWithBoundingBox() throws Exception {
		QueryResponse mockQueryResponse = mock(QueryResponse.class);
		List<DropDocument> foundDocuments = new ArrayList<DropDocument>();

		when(mockSolrServer.query(any(SolrQuery.class))).thenReturn(mockQueryResponse);
		when(mockQueryResponse.getBeans(DropDocument.class)).thenReturn(foundDocuments);

		Pageable pageRequest = new PageRequest(0, 20);
		DropFilter dropFilter = new DropFilter();
		dropFilter.setBoundingBox("40,-74,-73,41");

		dropSearchRepository.findInBucket(20L, dropFilter, pageRequest);
		ArgumentCaptor<SolrQuery> solrQueryArgument = ArgumentCaptor.forClass(SolrQuery.class);
		
		verify(mockSolrServer).query(solrQueryArgument.capture());
		SolrQuery solrQuery = solrQueryArgument.getValue();
		
		String[] expectedFilterQuery = new String[]{
				"bucketId:(20)",
				"geo:[40.0,-74.0 TO -73.0,41.0]"
				};
		
		String[] actualFilterQuery = solrQuery.getFilterQueries();
		
		assertEquals("*:*", solrQuery.getQuery());
		assertEquals(2, actualFilterQuery.length);
		assertEquals(expectedFilterQuery[0], actualFilterQuery[0]);
		assertEquals(expectedFilterQuery[1], actualFilterQuery[1]);
		
	}

}
