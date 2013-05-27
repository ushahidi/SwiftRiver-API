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
import org.springframework.data.solr.core.SolrTemplate;

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
}
