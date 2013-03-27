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

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.BucketCollaboratorDao;
import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.api.dao.BucketDropDao;
import com.ushahidi.swiftriver.core.api.dao.LinkDao;
import com.ushahidi.swiftriver.core.api.dao.PlaceDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDropDao;
import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.api.dto.CreateCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.CreatePlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateTagDTO;
import com.ushahidi.swiftriver.core.api.dto.DropSourceDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketCollaborator;
import com.ushahidi.swiftriver.core.model.BucketDrop;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverDrop;
import com.ushahidi.swiftriver.core.model.Tag;

public class BucketServiceTest {
	
private BucketDao mockBucketDao;
	
	private AccountDao mockAccountDao;
	
	private BucketCollaboratorDao mockBucketCollaboratorDao;
	
	private BucketDropDao mockBucketDropDao;
	
	private Mapper mockMapper;
	
	private BucketService bucketService;
	
	private TagDao mockTagDao;

	private PlaceDao mockPlaceDao;

	private LinkDao mockLinkDao;

	private RiverDropDao mockRiverDropDao;
	
	@Before
	public void setup() {
		mockBucketDao = mock(BucketDao.class);
		mockAccountDao = mock(AccountDao.class);
		mockBucketCollaboratorDao = mock(BucketCollaboratorDao.class);
		mockBucketDropDao = mock(BucketDropDao.class);
		mockTagDao = mock(TagDao.class);
		mockPlaceDao = mock(PlaceDao.class);
		mockLinkDao = mock(LinkDao.class);
		mockRiverDropDao = mock(RiverDropDao.class);
		mockMapper = mock(Mapper.class);
		
		bucketService = new BucketService();
		bucketService.setBucketDao(mockBucketDao);
		bucketService.setAccountDao(mockAccountDao);
		bucketService.setBucketCollaboratorDao(mockBucketCollaboratorDao);
		bucketService.setBucketDropDao(mockBucketDropDao);
		bucketService.setTagDao(mockTagDao);
		bucketService.setPlaceDao(mockPlaceDao);
		bucketService.setLinkDao(mockLinkDao);
		bucketService.setRiverDropDao(mockRiverDropDao);
		bucketService.setMapper(mockMapper);
	}

	@Test
	public void getCollaborators() {
		Bucket mockBucket = mock(Bucket.class);
		BucketCollaborator bucketCollaborator = new BucketCollaborator();
		List<BucketCollaborator> collaborators = new ArrayList<BucketCollaborator>();
		collaborators.add(bucketCollaborator);

		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockBucket.getCollaborators()).thenReturn(collaborators);

		List<GetCollaboratorDTO> actual = bucketService.getCollaborators(1L);

		verify(mockMapper).map(bucketCollaborator, GetCollaboratorDTO.class);
		assertEquals(1, actual.size());
	}

	@Test
	public void addCollaborator() {
		CreateCollaboratorDTO createCollaborator = new CreateCollaboratorDTO();
		createCollaborator.setReadOnly(true);
		createCollaborator.setAccount(new CreateCollaboratorDTO.Account());

		Bucket mockBucket = mock(Bucket.class);
		Account mockAuthAccount = mock(Account.class);
		Account mockAccount = mock(Account.class);

		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(
				mockAuthAccount);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockBucketDao.findCollaborator(anyLong(), anyLong())).thenReturn(
				null);
		when(mockAccountDao.findById(anyLong())).thenReturn(mockAccount);

		bucketService.addCollaborator(1L, createCollaborator, "admin");

		verify(mockBucketDao).addCollaborator(mockBucket, mockAccount, true);
	}

	@Test
	public void modifyCollaborator() {
		ModifyCollaboratorDTO to = new ModifyCollaboratorDTO();
		to.setActive(true);
		to.setReadOnly(false);

		BucketCollaborator collaborator = mock(BucketCollaborator.class);
		Account mockAuthAccount = mock(Account.class);
		Bucket mockBucket = mock(Bucket.class);

		when(mockBucketCollaboratorDao.findById(anyLong())).thenReturn(
				collaborator);
		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(
				mockAuthAccount);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);

		bucketService.modifyCollaborator(1L, 2L, to, "admin");

		verify(collaborator).setActive(true);
		verify(collaborator).setReadOnly(false);
		verify(mockBucketDao).updateCollaborator(collaborator);
	}

	@Test
	public void deleteCollaborator() {
		BucketCollaborator collaborator = mock(BucketCollaborator.class);
		Account mockAuthAccount = mock(Account.class);
		Bucket mockBucket = mock(Bucket.class);

		when(mockBucketCollaboratorDao.findById(anyLong())).thenReturn(
				collaborator);
		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(
				mockAuthAccount);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);

		bucketService.deleteCollaborator(1L, 2L, "admin");
		verify(mockBucketCollaboratorDao).delete(collaborator);
	}

	@Test
	public void addDropTag() {
		CreateTagDTO createTag = mock(CreateTagDTO.class);

		Bucket mockBucket = mock(Bucket.class);
		BucketDrop mockBucketDrop = mock(BucketDrop.class);
		Account mockAuthAccount = mock(Account.class);
		Tag mockTag = mock(Tag.class);

		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(mockAuthAccount);
		when(mockBucketDropDao.findById(anyLong())).thenReturn(mockBucketDrop);
		when(mockBucketDrop.getBucket()).thenReturn(mockBucket);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockTagDao.findByHash(anyString())).thenReturn(mockTag);

		bucketService.addDropTag(1L, 3L, createTag, "user1");

		verify(mockBucketDao).findById(1L);
		verify(mockBucketDropDao).findById(3L);
		verify(mockBucketDropDao).findTag(mockBucketDrop, mockTag);
		verify(mockBucketDropDao).addTag(mockBucketDrop, mockTag);
	}
	
	@Test
	public void deleteDropTag() {
		Bucket mockBucket = mock(Bucket.class);
		BucketDrop mockBucketDrop = mock(BucketDrop.class);
		Account mockAuthAccount = mock(Account.class);
		Tag mockTag = mock(Tag.class);

		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(mockAuthAccount);
		when(mockBucketDropDao.findById(anyLong())).thenReturn(mockBucketDrop);
		when(mockBucketDrop.getBucket()).thenReturn(mockBucket);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockTagDao.findById(anyLong())).thenReturn(mockTag);
		when(mockBucketDropDao.deleteTag(mockBucketDrop, mockTag)).thenReturn(true);
		
		bucketService.deleteDropTag(1L, 2L, 15L, "admin");
		verify(mockTagDao).findById(15L);
		verify(mockBucketDropDao).deleteTag(mockBucketDrop, mockTag);
	}
	
	@Test
	public void addPlace() {
		CreatePlaceDTO dto = new CreatePlaceDTO();
		dto.setName("Nyeri");
		dto.setLongitude(36.9476f);
		dto.setLatitude(-0.42013f);

		Bucket mockBucket = mock(Bucket.class);
		BucketDrop mockBucketDrop = mock(BucketDrop.class);
		Account mockAuthAccount = mock(Account.class);
		Place mockPlace = mock(Place.class);

		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(mockAuthAccount);
		when(mockBucketDropDao.findById(anyLong())).thenReturn(mockBucketDrop);
		when(mockBucketDrop.getBucket()).thenReturn(mockBucket);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockPlaceDao.findByHash(anyString())).thenReturn(mockPlace);
		
		bucketService.addDropPlace(1L, 22L, dto, "admin");
		verify(mockBucketDropDao).addPlace(mockBucketDrop, mockPlace);
	}
	
	@Test
	public void deleteDropPlace() {
		Bucket mockBucket = mock(Bucket.class);
		BucketDrop mockBucketDrop = mock(BucketDrop.class);
		Account mockAuthAccount = mock(Account.class);
		Place mockPlace = mock(Place.class);

		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(mockAuthAccount);
		when(mockBucketDropDao.findById(anyLong())).thenReturn(mockBucketDrop);
		when(mockBucketDrop.getBucket()).thenReturn(mockBucket);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockPlaceDao.findById(anyLong())).thenReturn(mockPlace);
		when(mockBucketDropDao.deletePlace(mockBucketDrop, mockPlace)).thenReturn(true);
		
		bucketService.deleteDropPlace(1L, 22L, 33L, "admin");
		verify(mockBucketDropDao).deletePlace(mockBucketDrop, mockPlace);
	}
	
	@Test
	public void addLink() {
		CreateLinkDTO createLink = new CreateLinkDTO();
		createLink.setUrl("http://www.ushahidi.com");

		Bucket mockBucket = mock(Bucket.class);
		BucketDrop mockBucketDrop = mock(BucketDrop.class);
		Account mockAuthAccount = mock(Account.class);
		Link mockLink = mock(Link.class);

		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(mockAuthAccount);
		when(mockBucketDropDao.findById(anyLong())).thenReturn(mockBucketDrop);
		when(mockBucketDrop.getBucket()).thenReturn(mockBucket);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockLinkDao.findByHash(anyString())).thenReturn(mockLink);

		bucketService.addDropLink(1L, 22L, createLink, "admin");
		verify(mockBucketDropDao).addLink(mockBucketDrop, mockLink);
	}
	
	@Test
	public void deleteLink() {
		Bucket mockBucket = mock(Bucket.class);
		BucketDrop mockBucketDrop = mock(BucketDrop.class);
		Account mockAuthAccount = mock(Account.class);
		Link mockLink = mock(Link.class);

		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(mockAuthAccount);
		when(mockBucketDropDao.findById(anyLong())).thenReturn(mockBucketDrop);
		when(mockBucketDrop.getBucket()).thenReturn(mockBucket);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockLinkDao.findById(anyLong())).thenReturn(mockLink);
		when(mockBucketDropDao.deleteLink(mockBucketDrop, mockLink)).thenReturn(true);
		
		bucketService.deleteDropLink(1L, 5L, 22L, "admin");
		verify(mockLinkDao).findById(22L);
		verify(mockBucketDropDao).deleteLink(mockBucketDrop, mockLink);
		
	}
	
	@Test
	public void addDropFromRiver() {
		DropSourceDTO dropSource = new DropSourceDTO();
		dropSource.setSource("river");

		Bucket mockBucket = mock(Bucket.class);
		RiverDrop mockRiverDrop = mock(RiverDrop.class);
		Account mockAuthAccount = mock(Account.class);
		Drop mockDrop = mock(Drop.class);

		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(mockAuthAccount);
		when(mockRiverDropDao.findById(anyLong())).thenReturn(mockRiverDrop);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDrop.getDrop()).thenReturn(mockDrop);
		when(mockBucketDao.findDrop(anyLong(), anyLong())).thenReturn(null);
		
		bucketService.addDrop(1L, 22L, dropSource, "admin");
		verify(mockBucketDropDao).createFromRiverDrop(mockRiverDrop, mockBucket);		
	}
	
	@Test
	public void addDropFromBucket() {
		DropSourceDTO dropSource = new DropSourceDTO();
		dropSource.setSource("bucket");

		Bucket mockBucket = mock(Bucket.class);
		Bucket mockOtherBucket = mock(Bucket.class);
		BucketDrop mockBucketDrop = mock(BucketDrop.class);
		Account mockAuthAccount = mock(Account.class);

		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(mockAuthAccount);
		when(mockBucketDropDao.findById(anyLong())).thenReturn(mockBucketDrop);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockBucketDrop.getBucket()).thenReturn(mockOtherBucket);
		
		bucketService.addDrop(1L, 12L, dropSource, "admin");
		verify(mockBucketDropDao).findById(12L);
		verify(mockBucketDropDao).createFromExisting(mockBucketDrop, mockBucket);		
	}
	
	@Test
	public void addExistingBucketDropFromRiver() {
		DropSourceDTO dropSource = new DropSourceDTO();
		dropSource.setSource("river");

		Bucket mockBucket = mock(Bucket.class);
		RiverDrop mockRiverDrop = mock(RiverDrop.class);
		Account mockAuthAccount = mock(Account.class);
		Drop mockDrop = mock(Drop.class);
		BucketDrop mockBucketDrop = mock(BucketDrop.class);

		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(mockAuthAccount);
		when(mockRiverDropDao.findById(anyLong())).thenReturn(mockRiverDrop);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockRiverDrop.getDrop()).thenReturn(mockDrop);
		when(mockBucketDao.findDrop(anyLong(), anyLong())).thenReturn(mockBucketDrop);
		
		bucketService.addDrop(1L, 22L, dropSource, "admin");
		verify(mockBucketDropDao).increaseVeracity(mockBucketDrop);
		
	}
	
	@Test(expected=BadRequestException.class)
	public void addExistingBucketDropFromBucket() {
		DropSourceDTO dropSource = new DropSourceDTO();
		dropSource.setSource("bucket");

		Bucket mockBucket = mock(Bucket.class);
		BucketDrop mockBucketDrop = mock(BucketDrop.class);
		Account mockAuthAccount = mock(Account.class);

		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(mockAuthAccount);
		when(mockBucketDropDao.findById(anyLong())).thenReturn(mockBucketDrop);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockBucketDrop.getBucket()).thenReturn(mockBucket);
		
		bucketService.addDrop(1L, 12L, dropSource, "admin");
		verify(mockBucketDropDao).findById(12L);
		
	}
	
	@Test
	public void filterVisible() {
		Account queryingAccount = new Account();
		queryingAccount.setCollaboratingBuckets(new ArrayList<Bucket>());
		Bucket ownedPrivateBucket = new Bucket();
		ownedPrivateBucket.setPublished(false);
		ownedPrivateBucket.setAccount(queryingAccount);
		Bucket unOwnedPrivateBucket = new Bucket();
		unOwnedPrivateBucket.setPublished(false);
		unOwnedPrivateBucket.setAccount(new Account());
		Bucket publicBucket = new Bucket();
		publicBucket.setPublished(true);
		publicBucket.setAccount(new Account());

		List<Bucket> buckets = new ArrayList<Bucket>();
		buckets.add(ownedPrivateBucket);
		buckets.add(unOwnedPrivateBucket);
		buckets.add(publicBucket);

		List<Bucket> filtered = bucketService.filterVisible(buckets,
				queryingAccount);
		
		assertEquals(2, filtered.size());
		assertTrue(filtered.contains(ownedPrivateBucket));
		assertTrue(filtered.contains(publicBucket));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void markDropAsRead() {
		Bucket mockBucket = mock(Bucket.class);
		Account mockAccount = mock(Account.class);
		BucketDrop mockBucketDrop = mock(BucketDrop.class);
		List<BucketDrop> mockReadBucketDrops = (List<BucketDrop>) mock(List.class); 

		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsername(anyString())).thenReturn(mockAccount);
		when(mockBucket.isPublished()).thenReturn(true);
		when(mockBucketDropDao.findById(anyLong())).thenReturn(mockBucketDrop);
		when(mockBucketDrop.getBucket()).thenReturn(mockBucket);
		when(mockBucketDropDao.isRead(mockBucketDrop, mockAccount)).thenReturn(false);
		when(mockAccount.getReadBucketDrops()).thenReturn(mockReadBucketDrops);
		
		bucketService.markDropAsRead(1L, 5L, "user1");
	
		verify(mockBucketDropDao).isRead(mockBucketDrop, mockAccount);
		verify(mockReadBucketDrops).add(mockBucketDrop);
		verify(mockAccountDao).update(mockAccount);
	}

}
