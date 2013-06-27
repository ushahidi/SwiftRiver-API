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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dozer.Mapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.BucketCollaboratorDao;
import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.api.dao.BucketDropDao;
import com.ushahidi.swiftriver.core.api.dao.BucketDropFormDao;
import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.api.dao.LinkDao;
import com.ushahidi.swiftriver.core.api.dao.PlaceDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDropDao;
import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.api.dto.CreateBucketDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.CreatePlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateTagDTO;
import com.ushahidi.swiftriver.core.api.dto.FormValueDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyFormValueDTO;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.ActivityType;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketCollaborator;
import com.ushahidi.swiftriver.core.model.BucketDrop;
import com.ushahidi.swiftriver.core.model.BucketDropForm;
import com.ushahidi.swiftriver.core.model.BucketDropFormField;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.Tag;
import com.ushahidi.swiftriver.core.model.User;
import com.ushahidi.swiftriver.core.support.MapperFactory;

public class BucketServiceTest {

	private BucketDao mockBucketDao;

	private AccountDao mockAccountDao;

	private BucketCollaboratorDao mockBucketCollaboratorDao;

	private BucketDropDao mockBucketDropDao;

	private BucketDropFormDao mockBucketDropFormDao;

	private Mapper mockMapper;

	private final static Mapper mapper = MapperFactory.getMapper();

	private BucketService bucketService;

	private TagDao mockTagDao;

	private PlaceDao mockPlaceDao;

	private LinkDao mockLinkDao;

	private RiverDropDao mockRiverDropDao;

	private AccountService mockAccountService;

	private DropDao mockDropDao;

	@Before
	public void setup() {
		mockBucketDao = mock(BucketDao.class);
		mockAccountDao = mock(AccountDao.class);
		mockBucketCollaboratorDao = mock(BucketCollaboratorDao.class);
		mockBucketDropDao = mock(BucketDropDao.class);
		mockBucketDropFormDao = mock(BucketDropFormDao.class);
		mockTagDao = mock(TagDao.class);
		mockPlaceDao = mock(PlaceDao.class);
		mockLinkDao = mock(LinkDao.class);
		mockRiverDropDao = mock(RiverDropDao.class);
		mockMapper = mock(Mapper.class);
		mockAccountService = mock(AccountService.class);
		mockDropDao = mock(DropDao.class);

		bucketService = new BucketService();
		bucketService.setBucketDao(mockBucketDao);
		bucketService.setAccountDao(mockAccountDao);
		bucketService.setBucketCollaboratorDao(mockBucketCollaboratorDao);
		bucketService.setBucketDropDao(mockBucketDropDao);
		bucketService.setBucketDropFormDao(mockBucketDropFormDao);
		bucketService.setTagDao(mockTagDao);
		bucketService.setPlaceDao(mockPlaceDao);
		bucketService.setLinkDao(mockLinkDao);
		bucketService.setRiverDropDao(mockRiverDropDao);
		bucketService.setMapper(mockMapper);
		bucketService.setAccountService(mockAccountService);
		bucketService.setDropDao(mockDropDao);
	}

	@Test
	public void createBucket() {
		CreateBucketDTO dto = new CreateBucketDTO();
		dto.setName("My New Bucket");
		dto.setPublished(false);

		Account account = new Account();
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(account);

		bucketService.createBucket(dto, "user");

		ArgumentCaptor<Bucket> argument = ArgumentCaptor.forClass(Bucket.class);
		verify(mockBucketDao).create(argument.capture());

		Bucket bucket = argument.getValue();
		assertEquals("My New Bucket", bucket.getName());
		assertFalse(bucket.isPublished());

		verify(mockAccountService).logActivity(account, ActivityType.CREATE,
				bucket);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getCollaborators() {
		Bucket mockBucket = mock(Bucket.class);

		BucketCollaborator bucketCollaborator = new BucketCollaborator();
		bucketCollaborator.setBucket(mockBucket);
		bucketCollaborator.setAccount(new Account());
		bucketCollaborator.setActive(false);
		bucketCollaborator.setReadOnly(true);
		
		List<BucketCollaborator> collaborators = new ArrayList<BucketCollaborator>();
		collaborators.add(bucketCollaborator);

		GetCollaboratorDTO mockCollaboratorDTO = mock(GetCollaboratorDTO.class);
		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockBucket.getCollaborators()).thenReturn(collaborators);
		when(mockMapper.map((Account) anyObject(), 
				any(Class.class))).thenReturn(mockCollaboratorDTO);

		List<GetCollaboratorDTO> actual = bucketService.getCollaborators(1L);
		ArgumentCaptor<Account> accountArgument = ArgumentCaptor.forClass(Account.class);

		verify(mockMapper, times(1)).map(accountArgument.capture(), eq(GetCollaboratorDTO.class));
		assertEquals(1, actual.size());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void addCollaborator() {
		CreateCollaboratorDTO createCollaborator = new CreateCollaboratorDTO();
		createCollaborator.setReadOnly(true);
		createCollaborator.setAccount(new CreateCollaboratorDTO.Account());

		Bucket mockBucket = mock(Bucket.class);
		Account mockAuthAccount = mock(Account.class);
		Account mockAccount = mock(Account.class);

		GetCollaboratorDTO mockCollaboratorDTO = mock(GetCollaboratorDTO.class);
		BucketCollaborator mockCollaborator = mock(BucketCollaborator.class);

		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockBucketDao.findCollaborator(anyLong(), anyLong())).thenReturn(
				null);
		when(mockAccountDao.findById(anyLong())).thenReturn(mockAccount);
		when(mockBucketDao.addCollaborator((Bucket) anyObject(), 
				(Account) anyObject(), anyBoolean())).thenReturn(mockCollaborator);
		when(mockMapper.map((Account) anyObject(), 
				any(Class.class))).thenReturn(mockCollaboratorDTO);

		bucketService.addCollaborator(1L, createCollaborator, "admin");

		verify(mockBucketDao).addCollaborator(mockBucket, mockAccount, true);
		verify(mockAccountService).logActivity(eq(mockAuthAccount), 
				eq(ActivityType.INVITE), any(BucketCollaborator.class));

		ArgumentCaptor<Account> accountArgument = ArgumentCaptor.forClass(Account.class);
		verify(mockMapper).map(accountArgument.capture(), eq(GetCollaboratorDTO.class));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void modifyCollaborator() {
		ModifyCollaboratorDTO to = new ModifyCollaboratorDTO();
		to.setActive(true);
		to.setReadOnly(false);

		BucketCollaborator collaborator = mock(BucketCollaborator.class);
		Account mockAuthAccount = mock(Account.class);
		Bucket mockBucket = mock(Bucket.class);

		GetCollaboratorDTO mockCollaboratorDTO = mock(GetCollaboratorDTO.class);

		when(mockBucketCollaboratorDao.findById(anyLong())).thenReturn(
				collaborator);
		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockBucketDao.findCollaborator(anyLong(), 
				anyLong())).thenReturn(collaborator);
		when(mockMapper.map((Account) anyObject(), 
				any(Class.class))).thenReturn(mockCollaboratorDTO);

		bucketService.modifyCollaborator(1L, 2L, to, "admin");

		verify(collaborator).setActive(true);
		verify(collaborator).setReadOnly(false);
		verify(mockBucketDao).updateCollaborator(collaborator);
	}

	@Test
	public void deleteCollaborator() {
		BucketCollaborator collaborator = mock(BucketCollaborator.class);
		Account mockAuthAccount = mock(Account.class);
		Account mockCollaboratorAccount = mock(Account.class);
		Bucket mockBucket = mock(Bucket.class);

		when(mockBucketCollaboratorDao.findById(anyLong())).thenReturn(
				collaborator);
		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockBucketDao.findCollaborator(anyLong(), anyLong())).thenReturn(collaborator);
		when(collaborator.getAccount()).thenReturn(mockCollaboratorAccount);

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
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockBucketDao.findBucketDrop(anyLong(), anyLong())).thenReturn(mockBucketDrop);
		when(mockBucketDrop.getBucket()).thenReturn(mockBucket);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockTagDao.findByHash(anyString())).thenReturn(mockTag);

		bucketService.addDropTag(1L, 3L, createTag, "user1");

		verify(mockBucketDao).findById(1L);
		verify(mockBucketDao).findBucketDrop(1L, 3L);
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
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockBucketDao.findBucketDrop(anyLong(), anyLong())).thenReturn(mockBucketDrop);
		when(mockBucketDrop.getBucket()).thenReturn(mockBucket);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockTagDao.findById(anyLong())).thenReturn(mockTag);
		when(mockBucketDropDao.deleteTag(mockBucketDrop, mockTag)).thenReturn(
				true);

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
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockBucketDao.findBucketDrop(anyLong(), anyLong())).thenReturn(mockBucketDrop);
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
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockBucketDao.findBucketDrop(anyLong(),anyLong())).thenReturn(mockBucketDrop);
		when(mockBucketDrop.getBucket()).thenReturn(mockBucket);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockPlaceDao.findById(anyLong())).thenReturn(mockPlace);
		when(mockBucketDropDao.deletePlace(mockBucketDrop, mockPlace))
				.thenReturn(true);

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
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockBucketDao.findBucketDrop(anyLong(), anyLong())).thenReturn(mockBucketDrop);
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
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockBucketDao.findBucketDrop(anyLong(), anyLong())).thenReturn(mockBucketDrop);
		when(mockBucketDrop.getBucket()).thenReturn(mockBucket);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);
		when(mockLinkDao.findById(anyLong())).thenReturn(mockLink);
		when(mockBucketDropDao.deleteLink(mockBucketDrop, mockLink))
				.thenReturn(true);

		bucketService.deleteDropLink(1L, 5L, 22L, "admin");
		verify(mockLinkDao).findById(22L);
		verify(mockBucketDropDao).deleteLink(mockBucketDrop, mockLink);

	}

	@Test
	public void addDrop() {
		Bucket mockBucket = mock(Bucket.class);
		Drop mockDrop = mock(Drop.class);
		Account mockAuthAccount = mock(Account.class);

		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockDropDao.findById(anyLong())).thenReturn(mockDrop);
		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(
				mockAuthAccount);
		when(mockBucketDao.findBucketDrop(anyLong(), anyLong())).thenReturn(null);
		when(mockBucket.getAccount()).thenReturn(mockAuthAccount);

		bucketService.addDrop(1L, 12L, "admin");
		ArgumentCaptor<BucketDrop> bucketDropArgument = ArgumentCaptor.forClass(BucketDrop.class);
		verify(mockBucketDao).findBucketDrop(1L, 12L);
		verify(mockBucketDropDao).create(bucketDropArgument.capture());
		verify(mockBucketDao).increaseDropCount(mockBucket);
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

	@Test
	public void addDropForm() {
		Account account = new Account();
		Bucket bucket = new Bucket();
		bucket.setId(1L);
		bucket.setAccount(account);
		BucketDrop drop = new BucketDrop();
		drop.setBucket(bucket);

		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(account);
		when(mockBucketDao.findById(anyLong())).thenReturn(bucket);
		when(mockBucketDao.findBucketDrop(anyLong(), anyLong())).thenReturn(drop);

		FormValueDTO dto = new FormValueDTO();
		dto.setId("1");
		dto.setValues(new ArrayList<FormValueDTO.FormFieldValue>());
		FormValueDTO.FormFieldValue value = new FormValueDTO.FormFieldValue();
		value.setId("2");
		value.setValue(Arrays.asList("Value 1", "Value 2"));
		dto.getValues().add(value);

		bucketService.setMapper(mapper);

		bucketService.addDropForm(1L, 1L, dto, "user");

		ArgumentCaptor<BucketDropForm> argument = ArgumentCaptor
				.forClass(BucketDropForm.class);
		verify(mockBucketDropFormDao).create(argument.capture());

		BucketDropForm dropForm = argument.getValue();
		assertNotNull(dropForm.getForm());
		assertEquals(1L, ((Number) dropForm.getForm().getId()).longValue());
		BucketDropFormField fieldValue = dropForm.getValues().get(0);
		assertEquals(2L, ((Number) fieldValue.getField().getId().longValue()));
		assertEquals("[\"Value 1\",\"Value 2\"]", fieldValue.getValue());
	}

	@Test
	public void modifyDropForm() {
		Account account = new Account();
		Bucket bucket = new Bucket();
		bucket.setId(1L);
		bucket.setAccount(account);
		BucketDrop drop = new BucketDrop();
		drop.setBucket(bucket);
		drop.setForms(new ArrayList<BucketDropForm>());
		BucketDropForm form = new BucketDropForm();
		form.setId(1L);
		drop.getForms().add(form);

		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(account);
		when(mockBucketDao.findById(anyLong())).thenReturn(bucket);
		when(mockBucketDropDao.findById(anyLong())).thenReturn(drop);
		when(mockBucketDropDao.findForm(anyLong(), anyLong())).thenReturn(form);

		ModifyFormValueDTO dto = new ModifyFormValueDTO();
		dto.setValues(new ArrayList<ModifyFormValueDTO.FormFieldValue>());
		ModifyFormValueDTO.FormFieldValue value = new ModifyFormValueDTO.FormFieldValue();
		value.setId("2");
		value.setValue(Arrays.asList("Value 3", "Value 4"));
		dto.getValues().add(value);

		bucketService.setMapper(mapper);

		bucketService.modifyDropForm(1L, 1L, 1L, dto, "user");

		ArgumentCaptor<BucketDropForm> argument = ArgumentCaptor
				.forClass(BucketDropForm.class);
		verify(mockBucketDropFormDao).update(argument.capture());

		BucketDropForm dropForm = argument.getValue();
		BucketDropFormField fieldValue = dropForm.getValues().get(0);
		assertEquals("[\"Value 3\",\"Value 4\"]", fieldValue.getValue());
	}

	@Test
	public void deleteDropForm() {
		Account account = new Account();
		Bucket bucket = new Bucket();
		bucket.setId(1L);
		bucket.setAccount(account);
		BucketDrop drop = new BucketDrop();
		drop.setBucket(bucket);
		drop.setForms(new ArrayList<BucketDropForm>());
		BucketDropForm form = new BucketDropForm();
		form.setId(1L);
		drop.getForms().add(form);

		when(mockAccountDao.findByUsernameOrEmail(anyString())).thenReturn(account);
		when(mockBucketDao.findById(anyLong())).thenReturn(bucket);
		when(mockBucketDropDao.findById(anyLong())).thenReturn(drop);
		when(mockBucketDropDao.findForm(anyLong(), anyLong())).thenReturn(form);

		ModifyFormValueDTO dto = new ModifyFormValueDTO();
		dto.setValues(new ArrayList<ModifyFormValueDTO.FormFieldValue>());
		ModifyFormValueDTO.FormFieldValue value = new ModifyFormValueDTO.FormFieldValue();
		value.setId("2");
		value.setValue(Arrays.asList("Value 3", "Value 4"));
		dto.getValues().add(value);

		bucketService.setMapper(mapper);

		bucketService.deleteDropForm(1L, 1L, 1L, "user");

		ArgumentCaptor<BucketDropForm> argument = ArgumentCaptor
				.forClass(BucketDropForm.class);
		verify(mockBucketDropFormDao).delete(argument.capture());

		assertEquals(form, argument.getValue());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void markDropAsRead() {
		Bucket mockBucket = mock(Bucket.class);
		Account mockAccount = mock(Account.class);
		BucketDrop mockBucketDrop = mock(BucketDrop.class);
		List<BucketDrop> mockReadBucketDrops = (List<BucketDrop>) mock(List.class);

		when(mockBucketDao.findById(anyLong())).thenReturn(mockBucket);
		when(mockAccountDao.findByUsernameOrEmail(anyString()))
				.thenReturn(mockAccount);
		when(mockBucket.isPublished()).thenReturn(true);
		when(mockBucketDao.findBucketDrop(anyLong(), anyLong())).thenReturn(mockBucketDrop);
		when(mockBucketDrop.getBucket()).thenReturn(mockBucket);
		when(mockBucketDropDao.isRead(mockBucketDrop, mockAccount)).thenReturn(
				false);
		when(mockAccount.getReadBucketDrops()).thenReturn(mockReadBucketDrops);

		bucketService.markDropAsRead(1L, 5L, "user1");

		verify(mockBucketDropDao).isRead(mockBucketDrop, mockAccount);
		verify(mockReadBucketDrops).add(mockBucketDrop);
		verify(mockAccountDao).update(mockAccount);
	}
	
	@Test
	public void addFollower() {
		Bucket bucket = new Bucket();
		bucket.setFollowers(new ArrayList<Account>());
		when(mockBucketDao.findById(anyLong())).thenReturn(bucket);

		Account account = new Account();
		User user = new User();
		user.setUsername("user");
		account.setOwner(user);
		when(mockAccountDao.findById(anyLong())).thenReturn(account);

		bucketService.addFollower(1L, 1L, "user");

		ArgumentCaptor<Bucket> argument = ArgumentCaptor.forClass(Bucket.class);
		verify(mockBucketDao).update(argument.capture());
		
		assertTrue(argument.getValue().getFollowers().contains(account));
		
		verify(mockAccountService).logActivity(account, ActivityType.FOLLOW, bucket);
	}
	
	@Test
	public void isOwnerForOwnerAccount() {
		Account account = new Account();
		account.setAccountPath("owner_account");

		Bucket bucket = new Bucket();
		bucket.setAccount(account);

		assertTrue(bucketService.isOwner(bucket, account));
	}

	@Test
	public void isOwnerForEditorCollaboratingAccount() {
		BucketCollaborator collaborator = new BucketCollaborator();
		collaborator.setReadOnly(false);
		when(mockBucketDao.findCollaborator(anyLong(), anyLong())).thenReturn(collaborator);

		assertTrue(bucketService.isOwner(new Bucket(), new Account()));
	}
	
	@Test
	public void isOwnerForViewerCollaboratingAccount() {
		BucketCollaborator collaborator = new BucketCollaborator();
		collaborator.setReadOnly(true);
		when(mockBucketDao.findCollaborator(anyLong(), anyLong())).thenReturn(collaborator);

		assertFalse(bucketService.isOwner(new Bucket(), new Account()));
	}


	@Test
	public void isOwnerForNoneOwnerAccount() {
		Bucket bucket = new Bucket();
		bucket.setAccount(new Account());

		Account account = new Account();
		List<Bucket> collaboratingBuckets = new ArrayList<Bucket>();
		account.setCollaboratingBuckets(collaboratingBuckets);

		assertFalse(bucketService.isOwner(bucket, account));
	}

}
