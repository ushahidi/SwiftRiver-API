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
package com.ushahidi.swiftriver.core.api.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.solr.DropDocument;
import com.ushahidi.swiftriver.core.solr.repository.DropDocumentRepository;

/**
 * Tests for {@link DropIndexService}
 * 
 * @author ekala
 */

public class DropIndexServiceTest {

	private DropDocumentRepository mockRepository;
	
	private DropIndexService dropIndexService;
	
	private Mapper mapper = new DozerBeanMapper();
	
	private DropDao mockDropDao;
	
	@Before
	public void setUp() {
		mockRepository = mock(DropDocumentRepository.class);
		mockDropDao = mock(DropDao.class);

		dropIndexService = new DropIndexService();
		dropIndexService.setRepository(mockRepository);
		dropIndexService.setMapper(mapper);
		dropIndexService.setDropDao(mockDropDao);
	}
	
	@Test
	public void addToIndex() {
		Drop mockDrop = mock(Drop.class);
		
		dropIndexService.addToIndex(mockDrop);

		ArgumentCaptor<DropDocument> dropDocumentArgument = ArgumentCaptor.forClass(DropDocument.class);
		verify(mockRepository).save(dropDocumentArgument .capture());
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void addAllToIndex() {
		List<Drop> drops = new ArrayList<Drop>();
		drops.add(mock(Drop.class));
		drops.add(mock(Drop.class));
		dropIndexService.addAllToIndex(drops);
		ArgumentCaptor<List> dropDocumentListArgument = ArgumentCaptor
				.forClass(List.class);
		verify(mockRepository).save(dropDocumentListArgument.capture());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void findDrops() {
		DropDocument dropDocument = new DropDocument();
		dropDocument.setId("1");
		dropDocument.setChannel("channel");
		dropDocument.setTitle("title");
		dropDocument.setContent("content");

		List<DropDocument> dropDocuments = new ArrayList<DropDocument>();
		dropDocuments.add(dropDocument);
		
		when(mockRepository.find(anyString(), 
				any(Pageable.class))).thenReturn(dropDocuments);
		dropIndexService.findDrops("keyword", 30, 6);

		// First, we verify that a search is performed against the index
		ArgumentCaptor<PageRequest> pageRequestArgument = ArgumentCaptor.forClass(
				PageRequest.class);
		ArgumentCaptor<String> searchTermArgument = ArgumentCaptor.forClass(String.class);

		verify(mockRepository).find(searchTermArgument.capture(), 
				pageRequestArgument.capture());
		
		// Assert that "keyword" is the value used for the search
		String searchTerm = searchTermArgument.getValue();
		assertEquals("keyword", searchTerm);

		// Validate the Pageable instance passed to DropDocumentRepository
		PageRequest pageRequest = pageRequestArgument.getValue();
		assertEquals(5, pageRequest.getPageNumber());
		assertEquals(30, pageRequest.getPageSize());

		// Then we verify that the drops & their metadata are retrieved from the DB 
		ArgumentCaptor<List> dropIdArgument = ArgumentCaptor.forClass(List.class);
		verify(mockDropDao).findAll(dropIdArgument.capture());
	}
		
}
