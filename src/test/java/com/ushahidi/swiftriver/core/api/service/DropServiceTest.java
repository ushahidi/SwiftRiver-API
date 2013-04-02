/**
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ushahidi.swiftriver.core.api.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.api.dto.CreateDropDTO;
import com.ushahidi.swiftriver.core.model.Drop;

public class DropServiceTest {
	
	DropService dropService;
	
	Mapper mapper =  new DozerBeanMapper();
	
	DropDao mockDropDao;
	
	@Before
	public void setup() {
		
		mockDropDao = mock(DropDao.class);
		
		dropService = new DropService();
		dropService.setMapper(mapper);
		dropService.setDropDao(mockDropDao);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void createDrops() {
		List<CreateDropDTO> dropDTOs = new ArrayList<CreateDropDTO>();
		CreateDropDTO createDTO = new CreateDropDTO();
		createDTO.setTitle("test");
		dropDTOs.add(createDTO);
		
		dropService.createDrops(dropDTOs);
			
		ArgumentCaptor<List> argument = ArgumentCaptor.forClass(List.class);
		verify(mockDropDao).createDrops(argument.capture());
		List<Drop> drops = argument.getValue();
		
		assertEquals(1, drops.size());
		assertEquals("test", drops.get(0).getTitle());
	}

}
