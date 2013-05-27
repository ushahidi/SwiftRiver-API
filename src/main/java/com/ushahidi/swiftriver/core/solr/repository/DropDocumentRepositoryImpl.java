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

import javax.annotation.Resource;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;

import com.ushahidi.swiftriver.core.solr.DropDocument;

public class DropDocumentRepositoryImpl implements DropSearchRepository {

	@Resource
	private SolrTemplate solrTemplate;
	
	final Logger logger = LoggerFactory.getLogger(DropDocumentRepositoryImpl.class);
	
	public void setSolrTemplate(SolrTemplate solrTemplate) {
		this.solrTemplate = solrTemplate;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.solr.repository.DropSearchRepository#find
	 * (java.lang.String, org.springframework.data.domain.Pageable)
	 */
	public List<DropDocument> find(String searchTerm, Pageable pageable) {

		SolrQuery solrQuery = getPreparedSolrQuery(searchTerm, pageable);
		return getSearchResults(solrQuery);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ushahidi.swiftriver.core.solr.repository.DropSearchRepository#findInRiver
	 * (java.lang.Long, java.lang.String,
	 * org.springframework.data.domain.Pageable)
	 */
	public List<DropDocument> findInRiver(Long riverId, String searchTerm,
			Pageable pageable) {
		searchTerm = String.format(searchTerm + " AND riverId:(%d)", riverId);
		SolrQuery solrQuery = getPreparedSolrQuery(searchTerm, pageable);
		
		return getSearchResults(solrQuery);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ushahidi.swiftriver.core.solr.repository.DropSearchRepository#
	 * findInBucket(java.lang.Long, java.lang.String,
	 * org.springframework.data.domain.Pageable)
	 */
	public List<DropDocument> findInBucket(Long bucketId, String searchTerm,
			Pageable pageable) {
		searchTerm = String.format(searchTerm + " AND bucketId:(%d)", bucketId);
		SolrQuery solrQuery = getPreparedSolrQuery(searchTerm, pageable);
		
		return getSearchResults(solrQuery);
	}

	private List<DropDocument> getSearchResults(SolrQuery solrQuery) {
		List<DropDocument> results = null;
		try {
			QueryResponse queryResponse = solrTemplate.getSolrServer().query(solrQuery);
			results = queryResponse.getBeans(DropDocument.class);
		} catch (SolrServerException e) {
			logger.error("An error occurred during search {}", e.getMessage());
		}

		return results;
	}

	private SolrQuery getPreparedSolrQuery(String searchTerm, Pageable pageable) {
		// Calculate the start row
		Integer start = pageable.getPageNumber() * pageable.getPageSize();

		SolrQuery solrQuery = new SolrQuery();
		
		solrQuery.set("q", searchTerm);
		solrQuery.set("defType", "edismax");
		solrQuery.set("stopwords", true);
		solrQuery.set("lowercaseOperators", true);
		solrQuery.setStart(start);
		solrQuery.setRows(pageable.getPageSize());

		return solrQuery;
	}
}
