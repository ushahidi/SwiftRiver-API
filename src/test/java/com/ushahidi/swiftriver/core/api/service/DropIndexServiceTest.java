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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

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
	
	@Before
	public void setUp() {
		mockRepository = mock(DropDocumentRepository.class);

		dropIndexService = new DropIndexService();
		dropIndexService.setRepository(mockRepository);
		dropIndexService.setMapper(mapper);
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
		
}
