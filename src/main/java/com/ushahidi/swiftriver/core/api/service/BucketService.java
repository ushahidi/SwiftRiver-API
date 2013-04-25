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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.controller.BucketsController;
import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.BucketCollaboratorDao;
import com.ushahidi.swiftriver.core.api.dao.BucketCommentDao;
import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.api.dao.BucketDropDao;
import com.ushahidi.swiftriver.core.api.dao.BucketDropFormDao;
import com.ushahidi.swiftriver.core.api.dao.LinkDao;
import com.ushahidi.swiftriver.core.api.dao.PlaceDao;
import com.ushahidi.swiftriver.core.api.dao.RiverDropDao;
import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.api.dto.CreateBucketDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateCommentDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.CreatePlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateTagDTO;
import com.ushahidi.swiftriver.core.api.dto.DropSourceDTO;
import com.ushahidi.swiftriver.core.api.dto.FollowerDTO;
import com.ushahidi.swiftriver.core.api.dto.FormValueDTO;
import com.ushahidi.swiftriver.core.api.dto.GetBucketDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCommentDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetLinkDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetPlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO.GetTagDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyFormValueDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ForbiddenException;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketCollaborator;
import com.ushahidi.swiftriver.core.model.BucketComment;
import com.ushahidi.swiftriver.core.model.BucketDrop;
import com.ushahidi.swiftriver.core.model.BucketDropComment;
import com.ushahidi.swiftriver.core.model.BucketDropForm;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.RiverDrop;
import com.ushahidi.swiftriver.core.model.Tag;
import com.ushahidi.swiftriver.core.model.drop.DropFilter;
import com.ushahidi.swiftriver.core.solr.DropDocument;
import com.ushahidi.swiftriver.core.solr.repository.DropDocumentRepository;
import com.ushahidi.swiftriver.core.solr.util.QueryUtil;
import com.ushahidi.swiftriver.core.util.HashUtil;

/**
 * Service class for buckets
 * 
 * @author ekala
 * 
 */
@Service
@Transactional(readOnly = true)
public class BucketService {

	@Autowired
	private BucketDao bucketDao;

	@Autowired
	private Mapper mapper;

	@Autowired
	private AccountDao accountDao;

	@Autowired
	private BucketDropDao bucketDropDao;

	@Autowired
	private TagDao tagDao;

	@Autowired
	private LinkDao linkDao;

	@Autowired
	private PlaceDao placeDao;

	@Autowired
	private BucketCollaboratorDao bucketCollaboratorDao;

	@Autowired
	private RiverDropDao riverDropDao;
	
	@Autowired
	private BucketDropFormDao bucketDropFormDao;

	@Autowired
	private BucketCommentDao bucketCommentDao;
	
	@Autowired
	private DropDocumentRepository repository;

	/* Logger */
	final static Logger logger = LoggerFactory.getLogger(BucketService.class);

	public void setBucketDao(BucketDao bucketDao) {
		this.bucketDao = bucketDao;
	}

	public BucketDao getBucketDao() {
		return bucketDao;
	}

	public Mapper getMapper() {
		return mapper;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	public AccountDao getAccountDao() {
		return accountDao;
	}

	public void setAccountDao(AccountDao accountDao) {
		this.accountDao = accountDao;
	}

	public BucketCollaboratorDao getBucketCollaboratorDao() {
		return bucketCollaboratorDao;
	}

	public void setBucketCollaboratorDao(
			BucketCollaboratorDao bucketCollaboratorDao) {
		this.bucketCollaboratorDao = bucketCollaboratorDao;
	}

	public void setBucketDropDao(BucketDropDao bucketDropDao) {
		this.bucketDropDao = bucketDropDao;
	}

	public BucketDropFormDao getBucketDropFormDao() {
		return bucketDropFormDao;
	}

	public void setBucketDropFormDao(BucketDropFormDao bucketDropFormDao) {
		this.bucketDropFormDao = bucketDropFormDao;
	}

	public void setTagDao(TagDao tagDao) {
		this.tagDao = tagDao;
	}

	public void setPlaceDao(PlaceDao placeDao) {
		this.placeDao = placeDao;
	}

	public void setLinkDao(LinkDao linkDao) {
		this.linkDao = linkDao;
	}

	public void setRiverDropDao(RiverDropDao riverDropDao) {
		this.riverDropDao = riverDropDao;
	}

	/**
	 * Creates a new {@link Bucket} entity under the {@link Account} associated
	 * with <code>username</code>.
	 * 
	 * The created entity is transformed to DTO `
	 * 
	 * @param createDTO
	 * @param username
	 * @return
	 */
	@Transactional(readOnly = false)
	public GetBucketDTO createBucket(CreateBucketDTO createDTO, String username) {
		// Get the bucket name
		String bucketName = createDTO.getName();

		if (bucketName == null) {
			throw new BadRequestException("The bucket name must be specified");
		}

		Account account = accountDao.findByUsername(username);

		// Check if a bucket with the specified name already exists
		if (bucketDao.findBucketByName(account, bucketName) != null) {
			throw new BadRequestException(String.format(
					"User %s already has a bucket named %s", username,
					bucketName));
		}

		Bucket bucket = new Bucket();
		bucket.setName(bucketName);
		if (createDTO.isPublished() != null) {
			bucket.setPublished(createDTO.isPublished());
		}
		bucket.setAccount(account);
		bucket.setDateAdded(new Date());

		// Save bucket
		bucketDao.create(bucket);

		return mapper.map(bucket, GetBucketDTO.class);
	}

	/**
	 * Gets and returns a {@link Bucket} entity. If the {@link Bucket} has the
	 * <code>published</code> property set to true, the querying {@link Account}
	 * associated with <code>username</code> must be an owner of the
	 * {@link Bucket} otherwise {@link BucketsController} returns a 404 to the
	 * client
	 * 
	 * The retrieved entity is transformed to DTO for purposes of consumption by
	 * {@link BucketsController}
	 * 
	 * @param id
	 * @param username
	 *            TODO
	 * @return
	 */
	public GetBucketDTO getBucket(Long id, String username) {
		Bucket bucket = getBucket(id);

		if (bucket == null)
			throw new NotFoundException("Bucket not found");

		Account queryingAccount = accountDao.findByUsername(username);

		if (!bucket.isPublished() && !isOwner(bucket, queryingAccount))
			throw new ForbiddenException("Permission Denied");

		return mapper.map(bucket, GetBucketDTO.class);
	}

	/**
	 * Modifies a bucket with the specified <code>id</code> using the
	 * information provided in <code>modifiedBucket</code> If the
	 * {@link Account} associated with <code>username</code> is not the creator
	 * of the bucket or is not a collaborator with edit privileges, an
	 * {@link UnauthorizedExpection} is throw.
	 * 
	 * The modified {@link Bucket} is transformed into a DTO for purposes of
	 * consumption by {@link BucketsController}
	 * 
	 * @param id
	 * @param modifiedBucket
	 * @param username
	 * @return
	 */
	@Transactional(readOnly = false)
	public GetBucketDTO modifyBucket(Long id, CreateBucketDTO modifiedBucket,
			String username) {
		Bucket bucket = bucketDao.findById(id);
		if (bucket == null) {
			throw new NotFoundException();
		}

		Account account = accountDao.findByUsername(username);

		// Is the account the creator of the bucket or a collaborator with edit
		// privileges?
		if (!isOwner(bucket, account))
			throw new ForbiddenException("Permission denied.");

		// Get the submitted name
		String bucketName = modifiedBucket.getName();

		if (bucketName == null) {
			throw new BadRequestException("The bucket name must be specified");
		}

		// Check if the owner already has a bucket with the
		// specified name
		Bucket existingBucket = bucketDao.findBucketByName(bucket.getAccount(),
				bucketName);
		if (!bucket.getName().equals(bucketName) && existingBucket != null
				&& existingBucket.getId() != bucket.getId()) {
			throw new BadRequestException(String.format(
					"Another bucket named %s already exists", bucketName));
		}

		bucket.setName(bucketName);

		// Have the privacy settings changed?
		if (modifiedBucket.isPublished() != null) {
			bucket.setPublished(modifiedBucket.isPublished());
		}

		// Has the layout changed?
		if (modifiedBucket.getDefaultLayout() != null) {
			bucket.setDefaultLayout(modifiedBucket.getDefaultLayout());
		}

		// Update the bucket
		bucketDao.update(bucket);

		return mapper.map(bucket, GetBucketDTO.class);
	}

	/**
	 * Deletes the {@link Bucket} with the specified <code>id</code>. The
	 * {@link Account} associated with <code>username</code> must be the creator
	 * of the {@link Bucket} else a {@link ForbiddenException} is thrown
	 * 
	 * @param id
	 * @param username
	 */
	@Transactional(readOnly = false)
	public void deleteBucket(Long id, String username) {
		Bucket bucket = getBucket(id);
		Account queryingAccount = accountDao.findByUsername(username);
		if (!bucket.getAccount().equals(queryingAccount)) {
			throw new ForbiddenException();
		}
		bucketDao.delete(bucket);
	}

	/**
	 * Get collaborators of the given bucket
	 * 
	 * @param bucketId
	 * @return
	 * @throws NotFoundException
	 */
	public List<GetCollaboratorDTO> getCollaborators(Long bucketId)
			throws NotFoundException {
		Bucket bucket = bucketDao.findById(bucketId);
		if (bucket == null) {
			throw new NotFoundException("Bucket not found");
		}

		List<GetCollaboratorDTO> collaborators = new ArrayList<GetCollaboratorDTO>();

		for (BucketCollaborator collaborator : bucket.getCollaborators()) {
			collaborators.add(mapper
					.map(collaborator, GetCollaboratorDTO.class));
		}

		return collaborators;
	}

	/**
	 * Adds a collaborator to the specified bucket
	 * 
	 * @param bucketId
	 * @param createCollaboratorTO
	 * @throws NotFoundException
	 *             ,BadRequestException
	 */
	public GetCollaboratorDTO addCollaborator(Long bucketId,
			CreateCollaboratorDTO createCollaboratorTO, String authUser)
			throws NotFoundException, BadRequestException {

		// Check if the bucket exists
		Bucket bucket = getBucket(bucketId);

		// Check if the authenticating user has permission to add a collaborator
		Account authAccount = accountDao.findByUsername(authUser);
		if (!isOwner(bucket, authAccount))
			throw new ForbiddenException("Permission denied.");

		// Is the account already collaborating on the bucket
		if (bucketDao.findCollaborator(bucketId, createCollaboratorTO
				.getAccount().getId()) != null)
			throw new BadRequestException(
					"The account is already collaborating on the bucket");

		Account account = accountDao.findById(createCollaboratorTO.getAccount()
				.getId());
		if (account == null)
			throw new NotFoundException("Account not found");

		BucketCollaborator collaborator = bucketDao.addCollaborator(bucket,
				account, createCollaboratorTO.isReadOnly());

		return mapper.map(collaborator, GetCollaboratorDTO.class);
	}

	/**
	 * Modifies a collaborator
	 * 
	 * @param bucketId
	 * @param accountId
	 * @param modifyCollaboratorTO
	 * @return
	 */
	@Transactional(readOnly = false)
	public GetCollaboratorDTO modifyCollaborator(Long bucketId,
			Long collaboratorId, ModifyCollaboratorDTO modifyCollaboratorTO,
			String authUser) {

		Bucket bucket = bucketDao.findById(bucketId);

		if (bucket == null)
			throw new NotFoundException("Bucket not found.");

		Account authAccount = accountDao.findByUsername(authUser);

		if (!isOwner(bucket, authAccount))
			throw new ForbiddenException("Permission denied.");

		BucketCollaborator collaborator = bucketCollaboratorDao
				.findById(collaboratorId);

		// Collaborator exists?
		if (collaborator == null) {
			throw new NotFoundException("Collaborator not found");
		}

		if (modifyCollaboratorTO.getActive() != null) {
			collaborator.setActive(modifyCollaboratorTO.getActive());
		}

		if (modifyCollaboratorTO.getReadOnly() != null) {
			collaborator.setReadOnly(modifyCollaboratorTO.getReadOnly());
		}

		// Post changes to the DB
		bucketDao.updateCollaborator(collaborator);

		return mapper.map(collaborator, GetCollaboratorDTO.class);
	}

	/**
	 * Removes a collaborator in <code>accountId</code> from the bucket
	 * specified in <code>bucketId</code>. <code>accountId</code> is the
	 * {@link Account} id of the collaborator
	 * 
	 * @param bucketId
	 * @param accountId
	 */
	@Transactional
	public void deleteCollaborator(Long bucketId, Long collaboratorId,
			String authUser) {

		Bucket bucket = bucketDao.findById(bucketId);

		if (bucket == null)
			throw new NotFoundException("Bucket not found.");

		Account authAccount = accountDao.findByUsername(authUser);

		if (!isOwner(bucket, authAccount))
			throw new ForbiddenException("Permission denied.");

		BucketCollaborator collaborator = bucketCollaboratorDao
				.findById(collaboratorId);

		if (collaborator == null)
			throw new NotFoundException("Collaborator not found.");

		bucketCollaboratorDao.delete(collaborator);
	}

	/**
	 * Adds the {@link Account} specified in <code>accountId</code> to the list
	 * of followers for the {@link Bucket} identified by <code>id</code>
	 * 
	 * @param id
	 * @param accountId
	 * @param username
	 */
	@Transactional
	public void addFollower(long id, long accountId, String username) {
		Bucket bucket = getBucket(id);

		Account account = accountDao.findById(accountId);
		if (account == null) {
			throw new NotFoundException();
		}

		// Verify that the account following the bucket is tied to the
		// currently logged in user
		if (!account.getOwner().getUsername().equals(username))
			throw new ForbiddenException("Permission denied.");

		// Is the account already following the bucket
		if (bucket.getFollowers().contains(account)) {
			throw new BadRequestException(String.format(
					"%s  is already following bucket %d", accountId, id));
		}

		bucket.getFollowers().add(account);
		bucketDao.update(bucket);

	}

	/**
	 * Gets and returns the list of followers for the {@link Bucket} with the
	 * specified <code>id</code>. If <code>accountId</code> is specified, checks
	 * if the {@link Account} associated with that id is following the bucket
	 * 
	 * @param id
	 * @param accountId
	 * @return {@link List<AccountDTO>}
	 */
	@Transactional
	public List<FollowerDTO> getFollowers(Long id, Long accountId) {
		Bucket bucket = getBucket(id);

		List<FollowerDTO> followers = new ArrayList<FollowerDTO>();

		// Has the accountId parameter been specified
		if (accountId != null) {
			Account follower = accountDao.findById(accountId);
			if (follower == null) {
				throw new NotFoundException(String.format(
						"The account %d does not exist", accountId));
			}

			if (bucket.getFollowers().contains(follower)) {
				followers.add(mapFollowerDTO(follower));
			} else {
				throw new NotFoundException(String.format(
						"Account %d does not follow bucket %d", accountId, id));
			}
		} else {

			for (Account account : bucket.getFollowers()) {
				followers.add(mapFollowerDTO(account));
			}
		}

		return followers;
	}

	private FollowerDTO mapFollowerDTO(Account account) {
		FollowerDTO dto = mapper.map(account, FollowerDTO.class);

		// Set the name and email address
		dto.setName(account.getOwner().getName());
		dto.setEmail(account.getOwner().getEmail());

		return dto;
	}

	/**
	 * Removes the account with the specified <code>accountId</code> from the
	 * list of {@link Account}s following the bucket identified by
	 * <code>id</code>
	 * 
	 * @param id
	 * @param accountId
	 */
	@Transactional
	public void deleteFollower(Long id, Long accountId) {
		Bucket bucket = getBucket(id);

		Account account = accountDao.findById(accountId);
		if (account == null) {
			throw new NotFoundException(String.format(
					"Account %d does not exist", accountId));
		}

		// Does the account exist as a follower?
		if (bucket.getFollowers().contains(account)) {
			bucket.getFollowers().remove(account);
			bucketDao.update(bucket);
		} else {
			throw new NotFoundException(String.format(
					"Account %d does not follow bucket %d", accountId, id));
		}
	}

	/**
	 * Returns the drops for the bucket with the ID specified in <code>id</code>
	 * using the {@link DropFilter} specified in <code>dropFilter</code>
	 * 
	 * @param id
	 * @param dropFilter
	 * @param page
	 * @param dropCount
	 * @param authUser
	 * @return
	 */
	public List<GetDropDTO> getDrops(Long id, DropFilter dropFilter, int page,
			int dropCount, String authUser) {

		Bucket bucket = getBucket(id);
		Account queryingAccount = accountDao.findByUsername(authUser);
		
		if (!hasAccess(bucket, queryingAccount)) {
			throw new ForbiddenException("Access denied");
		}
		
		List<GetDropDTO> getDropDTOs = new ArrayList<GetDropDTO>();

		if (dropFilter.getKeywords() != null) {
			String searchTerm = QueryUtil.getQueryString(dropFilter.getKeywords());

			PageRequest pageRequest = new PageRequest(page - 1, dropCount);
			List<DropDocument> dropDocuments = repository.findInBucket(id, 
					searchTerm, pageRequest);
			
			if (dropDocuments.isEmpty()) {
				return getDropDTOs;
			}
			// Logger
			logger.info("Found {} matches for '{}' in bucket" + id, 
					dropDocuments.size(), searchTerm);

			List<Long> dropIds = new ArrayList<Long>();
			for (DropDocument document: dropDocuments) {
				dropIds.add(Long.parseLong(document.getId()));
			}
			
			// Set page number to 1
			page = 1;
			dropFilter.setDropIds(dropIds);
		}
		
		List<Drop> drops = bucketDao.getDrops(id, dropFilter, page, dropCount, queryingAccount);

		for (Drop drop : drops) {
			GetDropDTO dropDto = mapper.map(drop, GetDropDTO.class);
			getDropDTOs.add(dropDto);
		}

		return getDropDTOs;
	}

	/**
	 * Verifies whether the {@link Account} specified in 
	 * <code>queryingAccount</code> has access to the bucket specified
	 * in <code>bucket</code>
	 * 
	 * @param bucket
	 * @param queryingAccount
	 * @return
	 */
	private boolean hasAccess(Bucket bucket, Account queryingAccount) {
		if (bucket.isPublished())
			return true;

		return bucket.getAccount().equals(queryingAccount) || 
			bucket.getCollaborators().contains(queryingAccount);
	}

	/**
	 * Deletes the {@link BucketDrop} specified in <code>dropId</code> from the
	 * {@link Bucket} specified in <code>bucketId</code>
	 * 
	 * If the drop does not exist in the specified bucket, a
	 * {@link NotFoundException} exception is thrown
	 * 
	 * @param bucketId
	 * @param dropId
	 * @param authUser
	 */
	@Transactional(readOnly = false)
	public void deleteDrop(Long bucketId, Long dropId, String authUser) {
		Bucket bucket = getBucket(bucketId);

		if (!isOwner(bucket, authUser))
			throw new ForbiddenException("Permission denied");

		BucketDrop bucketDrop = getBucketDrop(dropId, bucket);

		// Delete the drop and decrease bucket drop count
		bucketDropDao.delete(bucketDrop);
		bucketDao.decreaseDropCount(bucket);
	}
	
	/**
	 * Deletes the {@link BucketDrop} specified in <code>dropId</code> from the
	 * {@link Bucket} specified in <code>bucketId</code>
	 * 
	 * If the drop does not exist in the specified bucket, a
	 * {@link NotFoundException} exception is thrown
	 * 
	 * @param bucketId
	 * @param dropId
	 * @param authUser
	 */
	@Transactional(readOnly = false)
	public void deleteBucketDrop(Long bucketId, Long dropId, String authUser) {
		Bucket bucket = getBucket(bucketId);

		if (!isOwner(bucket, authUser))
			throw new ForbiddenException("Permission denied");

		BucketDrop bucketDrop = getBucketDrop(dropId, bucket);

		// Delete the drop and decrease bucket drop count
		bucketDropDao.delete(bucketDrop);
		bucketDao.decreaseDropCount(bucket);
	}

	/**
	 * Adds the {@link Drop} specified in <code>dropId</code> to the
	 * {@link Bucket} in <code>bucketId</code>. The {@link Account} associated
	 * with <code>username</code> is used to verify whether the user submitting
	 * the request is authorized
	 * 
	 * @param bucketId
	 * @param dropId
	 * @param dropSourceTO
	 * @param username
	 */
	@Transactional
	public void addDrop(Long bucketId, Long dropId, DropSourceDTO dropSourceTO,
			String username) {
		// Validate the source
		if (!(dropSourceTO.getSource().equals("river") || dropSourceTO
				.getSource().equals("bucket"))) {
			throw new BadRequestException(String.format(
					"Invalid drop source %s", dropSourceTO.getSource()));
		}

		Bucket bucket = getBucket(bucketId);
		Account account = accountDao.findByUsername(username);

		if (!isOwner(bucket, account))
			throw new ForbiddenException("Permission denied.");

		BucketDrop bucketDrop = null;

		if (dropSourceTO.getSource().equals("river")) {
			RiverDrop riverDrop = riverDropDao.findById(dropId);
			if (riverDrop == null) {
				throw new NotFoundException(String.format(
						"Drop %d is not a river drop", dropId));
			}
			Drop drop = riverDrop.getDrop();
			bucketDrop = bucketDao.findDrop(bucketId, drop.getId());

			if (bucketDrop != null) {
				bucketDropDao.increaseVeracity(bucketDrop);
			} else {
				bucketDropDao.createFromRiverDrop(riverDrop, bucket);
				bucketDao.increaseDropCount(bucket);
			}
		} else if (dropSourceTO.getSource().equals("bucket")) {
			bucketDrop = bucketDropDao.findById(dropId);
			if (bucketDrop == null) {
				throw new NotFoundException(String.format(
						"Drop %d is not a bucket drop", dropId));
			}

			if (bucketDrop.getBucket().equals(bucket)) {
				throw new BadRequestException(
						String.format("Drop %d already exists in bucket %d",
								dropId, bucketId));
			}

			// Create a new bucket drop from an existing
			bucketDropDao.createFromExisting(bucketDrop, bucket);

			// Update the drop count
			bucketDao.increaseDropCount(bucket);
		}

	}

	/**
	 * Internal helper method to retrieve a bucket using its <code>id</code> in
	 * the database
	 * 
	 * @param id
	 * @return
	 */
	private Bucket getBucket(Long id) {
		Bucket bucket = bucketDao.findById(id);
		if (bucket == null) {
			throw new NotFoundException(String.format(
					"Bucket %d does not exist", id));
		}

		return bucket;
	}

	/**
	 * Helper method for verifying whether the user submitting the request has
	 * authorization to add/remove metadata to/from the {@link Bucket} specified
	 * in <code>bucketId</code>
	 * 
	 * @param bucket
	 * @param authUser
	 */
	public boolean isOwner(Bucket bucket, String authUser) {
		Account account = accountDao.findByUsername(authUser);
		return isOwner(bucket, account);
	}

	/**
	 * Verifies whether the {@link Account} in <code>account</code> is an owner
	 * of the {@link Bucket} specified in <code>bucket</code>
	 * 
	 * An owner is an {@link Account} that is a creator of the {@link Bucket} 
	 * or a {@link BucketCollaborator} with edit privileges i.e. the
	 * <code>readOnly</code> property is false
	 * 
	 * @param bucket
	 * @param account
	 * @return
	 */
	private boolean isOwner(Bucket bucket, Account account) {
		return bucket.getAccount() == account
				|| isCollaborator(bucket, account, false);

	}

	/**
	 * Verifies whether the {@link Account} in <code>account</code> is
	 * collaborating on the {@link Bucket} in <code>bucket</code>
	 * 
	 * @param bucket
	 * @param account
	 * @param readOnly
	 * @return
	 */
	private boolean isCollaborator(Bucket bucket, Account account,
			boolean readOnly) {
		BucketCollaborator collaborator = bucketDao.findCollaborator(
				bucket.getId(), account.getId());
		if (collaborator == null) {
			return false;
		}

		return (readOnly && collaborator.isReadOnly());
	}

	/**
	 * Adds a {@link Tag} to the {@link BucketDrop} with the specified
	 * <code>dropId</code> The drop must be in the {@link Bucket} whose ID is
	 * specified in <code>bucketId</code>
	 * 
	 * The created {@link Tag} entity is transformed to a DTO for purposes of
	 * consumption by {@link BucketsController}
	 * 
	 * @param bucketId
	 * @param dropId
	 * @param createDTO
	 * @param authUser
	 * @return
	 */
	@Transactional
	public GetTagDTO addDropTag(Long bucketId, Long dropId,
			CreateTagDTO createDTO, String authUser) {
		Bucket bucket = getBucket(bucketId);

		if (!isOwner(bucket, authUser))
			throw new ForbiddenException("Permission denied");

		// Get the bucket drop
		BucketDrop bucketDrop = getBucketDrop(dropId, bucket);

		String hash = HashUtil.md5(createDTO.getTag() + createDTO.getTagType());
		Tag tag = tagDao.findByHash(hash);
		if (tag == null) {
			tag = new Tag();
			tag.setTag(createDTO.getTag());
			tag.setType(createDTO.getTagType());

			tagDao.create(tag);
		} else {
			// Check if the tag exists in the bucket drop
			if (bucketDropDao.findTag(bucketDrop, tag) != null) {
				throw new BadRequestException(String.format(
						"Tag %s of type %s has already been added to drop %d",
						tag.getTag(), tag.getType(), dropId));
			}
		}

		bucketDropDao.addTag(bucketDrop, tag);
		return mapper.map(tag, GetTagDTO.class);
	}

	/**
	 * Deletes the {@link Tag} with the id specified in <code>tagId</code> from
	 * the {@link BucketDrop} specified in <code>dropId</code>
	 * 
	 * The request {@link BucketDrop} must be a member of the {@link Bucket}
	 * with the ID specified in <code>bucketId</code> else a
	 * {@link NotFoundException} is thrown
	 * 
	 * @param bucketId
	 * @param dropId
	 * @param tagId
	 * @param authUser
	 *            TODO
	 */
	@Transactional
	public void deleteDropTag(Long bucketId, Long dropId, Long tagId,
			String authUser) {
		Bucket bucket = getBucket(bucketId);

		if (!isOwner(bucket, authUser))
			throw new ForbiddenException("Permission denied");

		BucketDrop bucketDrop = getBucketDrop(dropId, bucket);

		Tag tag = tagDao.findById(tagId);

		if (tag == null) {
			throw new NotFoundException(String.format("Tag %d does not exist",
					tagId));
		}

		if (!bucketDropDao.deleteTag(bucketDrop, tag)) {
			throw new NotFoundException(String.format(
					"Drop %d does not have tag %d", dropId, tagId));
		}
	}

	/**
	 * Adds a {@link Link} to the {@link BucketDrop} with the specified
	 * <code>dropId</code> The drop must be in the {@link Bucket} whose ID is
	 * specified in <code>bucketId</code>
	 * 
	 * The created {@link Link} entity is transformed to a DTO for purposes of
	 * consumption by {@link BucketsController}
	 * 
	 * @param bucketId
	 * @param dropId
	 * @param createDTO
	 * @param authUser
	 * @return
	 */
	@Transactional
	public GetLinkDTO addDropLink(Long bucketId, Long dropId,
			CreateLinkDTO createDTO, String authUser) {
		Bucket bucket = getBucket(bucketId);

		if (!isOwner(bucket, authUser))
			throw new ForbiddenException("Permission denied");

		BucketDrop bucketDrop = getBucketDrop(dropId, bucket);

		String hash = HashUtil.md5(createDTO.getUrl());
		Link link = linkDao.findByHash(hash);
		if (link == null) {
			link = new Link();
			link.setUrl(createDTO.getUrl());
			link.setHash(hash);

			linkDao.create(link);
		} else {
			// Has the link already been added ?
			if (bucketDropDao.findLink(bucketDrop, link) != null) {
				throw new BadRequestException(String.format(
						"%s has already been added to drop %d", link.getUrl(),
						dropId));
			}
		}

		bucketDropDao.addLink(bucketDrop, link);
		return mapper.map(link, GetLinkDTO.class);
	}

	/**
	 * Deletes the {@link Link} with the id specified in <code>linkId</code>
	 * from the {@link BucketDrop} specified in <code>dropId</code>
	 * 
	 * The request {@link BucketDrop} must be a member of the {@link Bucket}
	 * with the ID specified in <code>bucketId</code> else a
	 * {@link NotFoundException} is thrown
	 * 
	 * @param bucketId
	 * @param dropId
	 * @param linkId
	 * @param authUser
	 */
	@Transactional
	public void deleteDropLink(Long bucketId, Long dropId, Long linkId,
			String authUser) {
		Bucket bucket = getBucket(bucketId);

		if (!isOwner(bucket, authUser))
			throw new ForbiddenException("Permission denied");

		BucketDrop bucketDrop = getBucketDrop(dropId, bucket);
		Link link = linkDao.findById(linkId);

		if (link == null) {
			throw new NotFoundException(String.format("Link %d does not exist",
					linkId));
		}

		if (!bucketDropDao.deleteLink(bucketDrop, link)) {
			throw new NotFoundException(String.format(
					"Drop %d does not have link %d", dropId, linkId));
		}
	}

	/**
	 * Adds a {@link Place} to the {@link BucketDrop} with the specified
	 * <code>dropId</code> The drop must be in the {@link Bucket} whose ID is
	 * specified in <code>bucketId</code>
	 * 
	 * The created {@link Place} entity is transformed to a DTO for purposes of
	 * consumption by {@link BucketsController}
	 * 
	 * @param bucketId
	 * @param dropId
	 * @param createDTO
	 * @param authUser
	 * @return
	 */
	@Transactional
	public GetPlaceDTO addDropPlace(Long bucketId, Long dropId,
			CreatePlaceDTO createDTO, String authUser) {
		Bucket bucket = getBucket(bucketId);

		if (!isOwner(bucket, authUser))
			throw new ForbiddenException("Permission denied");

		BucketDrop bucketDrop = getBucketDrop(dropId, bucket);

		String hashInput = createDTO.getName();
		hashInput += Float.toString(createDTO.getLongitude());
		hashInput += Float.toString(createDTO.getLatitude());

		String hash = HashUtil.md5(hashInput);

		// Generate a hash for the place name
		Place place = placeDao.findByHash(hash);
		if (place == null) {
			place = new Place();
			place.setPlaceName(createDTO.getName());
			place.setPlaceNameCanonical(createDTO.getName().toLowerCase());
			place.setHash(hash);
			place.setLatitude(createDTO.getLatitude());
			place.setLongitude(createDTO.getLongitude());

			placeDao.create(place);
		} else {
			if (bucketDropDao.findPlace(bucketDrop, place) != null) {
				throw new BadRequestException(
						String.format(
								"Drop %d already has the place %s with coordinates [%f, %f]",
								dropId, place.getPlaceName(),
								place.getLatitude(), place.getLongitude()));
			}
		}

		bucketDropDao.addPlace(bucketDrop, place);
		return mapper.map(place, GetPlaceDTO.class);
	}

	/**
	 * Deletes the {@link Place} with the id specified in <code>placeId</code>
	 * from the {@link BucketDrop} specified in <code>dropId</code>
	 * 
	 * The request {@link BucketDrop} must be a member of the {@link Bucket}
	 * with the ID specified in <code>bucketId</code> else a
	 * {@link NotFoundException} is thrown
	 * 
	 * @param bucketId
	 * @param dropId
	 * @param placeId
	 * @param authUser
	 */
	@Transactional
	public void deleteDropPlace(Long bucketId, Long dropId, Long placeId,
			String authUser) {
		Bucket bucket = getBucket(bucketId);

		if (!isOwner(bucket, authUser))
			throw new ForbiddenException("Permission denied");

		BucketDrop bucketDrop = getBucketDrop(dropId, bucket);
		Place place = placeDao.findById(placeId);

		if (place == null) {
			throw new NotFoundException(String.format(
					"Place %d does not exist", placeId));
		}

		if (!bucketDropDao.deletePlace(bucketDrop, place)) {
			throw new NotFoundException(String.format(
					"Drop %d does not have place %d", dropId, placeId));
		}
	}

	/**
	 * Helper method to retrieve a {@link BucketDrop} record from the database
	 * and verify that the retrieved entity belongs to the {@link Bucket}
	 * specified in <code>bucket</code>
	 * 
	 * @param dropId
	 * @param bucket
	 * @return
	 */
	private BucketDrop getBucketDrop(Long dropId, Bucket bucket) {
		BucketDrop bucketDrop = bucketDropDao.findById(dropId);

		if (bucketDrop == null
				|| (bucketDrop != null && !bucketDrop.getBucket()
						.equals(bucket))) {
			throw new NotFoundException(String.format(
					"Drop %d does is not in bucket %d", dropId, bucket.getId()));
		}

		return bucketDrop;
	}

	/**
	 * Filter the given list of buckets returning only those that are visible to
	 * the given queryingAccount.
	 * 
	 * @param buckets
	 * @param queryingAccount
	 * @return
	 */
	public List<Bucket> filterVisible(List<Bucket> buckets,
			Account queryingAccount) {
		List<Bucket> visible = new ArrayList<Bucket>();

		for (Bucket bucket : buckets) {
			if (isOwner(bucket, queryingAccount) || bucket.isPublished()) {
				visible.add(bucket);
			}
		}

		return visible;
	}

	/**
	 * Adds a comment to the {@link BucketDrop} entity specified in
	 * <code>dropId</code>. This entity must be associated with the
	 * {@link Bucket} entity specified in <code>bucketId</code> otherwise a
	 * {@link NotFoundException} will be thrown.
	 * 
	 * @param bucketId
	 * @param dropId
	 * @param createDTO
	 * @param authUser
	 * @return
	 */
	@Transactional
	public GetCommentDTO addDropComment(Long bucketId, Long dropId,
			CreateCommentDTO createDTO, String authUser) {

		if (createDTO.getCommentText() == null
				|| createDTO.getCommentText().trim().length() == 0) {
			throw new BadRequestException("The no comment text specified");
		}

		Bucket bucket = getBucket(bucketId);

		if (!bucket.isPublished() && !isOwner(bucket, authUser))
			throw new ForbiddenException("Permission Denied");

		BucketDrop bucketDrop = getBucketDrop(dropId, bucket);
		Account account = accountDao.findByUsername(authUser);
		BucketDropComment dropComment = bucketDropDao.addComment(bucketDrop,
				account, createDTO.getCommentText());

		return mapper.map(dropComment, GetCommentDTO.class);
	}

	/**
	 * Get and return the list of {@link BucketDropComment} entities for the
	 * {@link BucketDrop} with the ID specified in <code>dropId</code>
	 * 
	 * @param bucketId
	 * @param dropId
	 * @param authUser
	 * @return
	 */
	@Transactional
	public List<GetCommentDTO> getDropComments(Long bucketId, Long dropId,
			String authUser) {
		Bucket bucket = getBucket(bucketId);

		if (!bucket.isPublished() && !isOwner(bucket, authUser))
			throw new ForbiddenException("Permission Denied");

		BucketDrop bucketDrop = getBucketDrop(dropId, bucket);
		List<GetCommentDTO> commentsList = new ArrayList<GetCommentDTO>();
		for (BucketDropComment dropComment : bucketDrop.getComments()) {
			GetCommentDTO commentDTO = mapper.map(dropComment,
					GetCommentDTO.class);
			commentsList.add(commentDTO);
		}

		return commentsList;
	}

	/**
	 * Deletes the {@link BucketDropComment} entity specified in
	 * <code>commentId</code> from the {@link BucketDrop} entity specified in
	 * <code>dropId</code>
	 * 
	 * @param bucketId
	 * @param dropId
	 * @param commentId
	 * @param authUser
	 */
	@Transactional
	public void deleteDropComment(Long bucketId, Long dropId, Long commentId,
			String authUser) {
		Bucket bucket = getBucket(bucketId);

		if (!isOwner(bucket, authUser))
			throw new ForbiddenException("Permission Denied");

		getBucketDrop(dropId, bucket);

		if (!bucketDropDao.deleteComment(commentId)) {
			throw new NotFoundException(String.format(
					"Comment %d does not exist", commentId));
		}
	}

	/**
	 * Adds a comment to comment the {@link Bucket} with the ID specified in
	 * <code>bucketId</code>. If the {@link Bucket} is private i.e.
	 * <code>published</code> is <code>FALSE</code>, a permissions check is
	 * performed.
	 * 
	 * The created {@link BucketComment} entity is transformed to DTO for
	 * purposes of consumption by {@link BucketsController}
	 * 
	 * @param bucketId
	 * @param createDTO
	 * @param authUser
	 * @return
	 */
	@Transactional
	public GetCommentDTO addBucketComment(Long bucketId,
			CreateCommentDTO createDTO, String authUser) {
		if (createDTO.getCommentText() == null) {
			throw new BadRequestException(
					"The comment text has not been specified");
		}
		Bucket bucket = getBucket(bucketId);

		if (!bucket.isPublished() && !isOwner(bucket, authUser))
			throw new ForbiddenException("Permission Denied");

		Account account = accountDao.findByUsername(authUser);
		BucketComment bucketComment = bucketDao.addComment(bucket,
				createDTO.getCommentText(), account);

		return mapper.map(bucketComment, GetCommentDTO.class);
	}

	/**
	 * Gets and returns the list of {@link BucketComment} entities for the
	 * {@link Bucket} with the ID specified in <code>bucketId</code>
	 * 
	 * @param bucketId
	 * @param authUser
	 * @return
	 */
	@Transactional
	public List<GetCommentDTO> getBucketComments(Long bucketId, String authUser) {
		Bucket bucket = getBucket(bucketId);

		if (!bucket.isPublished() && !isOwner(bucket, authUser))
			throw new ForbiddenException("Permission Denied");

		List<GetCommentDTO> dtoComments = new ArrayList<GetCommentDTO>();
		for (BucketComment bucketComment : bucket.getComments()) {
			dtoComments.add(mapper.map(bucketComment, GetCommentDTO.class));
		}

		return dtoComments;
	}

	/**
	 * Deletes the {@link BucketComment} entity with the ID specified in
	 * <code>commentId</code> from the {@link Bucket} with the ID specified in
	 * <code>bucketId</code>. If the {@link BucketComment} does not belong to
	 * the specified bucket, a {@link NotFoundException} is thrown
	 * 
	 * @param bucketId
	 * @param commentId
	 * @param authUser
	 */
	@Transactional
	public void deleteBucketComment(Long bucketId, Long commentId,
			String authUser) {
		Bucket bucket = getBucket(bucketId);

		if (!isOwner(bucket, authUser))
			throw new ForbiddenException("Permission Denied");

		BucketComment bucketComment = bucketCommentDao.findById(commentId);

		if (bucketComment == null || !bucketComment.getBucket().equals(bucket)) {
			throw new NotFoundException(String.format(
					"Comment %d does not exist", commentId));
		}

		bucketCommentDao.delete(bucketComment);
	}

	/**
	 * Add custom form fields to a drop
	 * 
	 * @param bucketId
	 * @param dropId
	 * @param createDTO
	 * @param authUser
	 * @return
	 */
	@Transactional(readOnly = false)
	public FormValueDTO addDropForm(Long bucketId, Long dropId,
			FormValueDTO createDTO, String authUser) {
		
		Bucket bucket = getBucket(bucketId);

		if (!isOwner(bucket, authUser))
			throw new ForbiddenException("Permission denied");
		
		BucketDrop drop = getBucketDrop(dropId, bucket);
		
		BucketDropForm dropForm = mapper.map(createDTO, BucketDropForm.class);
		dropForm.setDrop(drop);
		bucketDropFormDao.create(dropForm);
		
		return mapper.map(dropForm, FormValueDTO.class);
	}

	/**
	 * Modify custom form fields in a drop.
	 * 
	 * @param bucketId
	 * @param dropId
	 * @param formId
	 * @param modifyFormTo
	 * @param name
	 * @return
	 */
	@Transactional(readOnly = false)
	public FormValueDTO modifyDropForm(Long bucketId, Long dropId, Long formId,
			ModifyFormValueDTO modifyFormTo, String authUser) {
		
		Bucket bucket = getBucket(bucketId);

		if (!isOwner(bucket, authUser))
			throw new ForbiddenException("Permission denied");
		
		BucketDropForm dropForm = bucketDropDao.findForm(dropId, formId);

		if (dropForm == null)
			throw new NotFoundException("The specified form was not found");
		
		mapper.map(modifyFormTo, dropForm);
		bucketDropFormDao.update(dropForm);
		
		return mapper.map(dropForm, FormValueDTO.class);
	}

	/**
	 * Remove custom fields from a drop
	 * 
	 * @param bucketId
	 * @param dropId
	 * @param formId
	 * @param name
	 */
	@Transactional(readOnly = false)
	public void deleteDropForm(Long bucketId, Long dropId, Long formId,
			String authUser) {
		Bucket bucket = getBucket(bucketId);

		if (!isOwner(bucket, authUser))
			throw new ForbiddenException("Permission denied");
		
		BucketDropForm dropForm = bucketDropDao.findForm(dropId, formId);

		if (dropForm == null)
			throw new NotFoundException("The specified form was not found");
		
		bucketDropFormDao.delete(dropForm);
	}
	
	/**
	 * Adds the {@link BucketDrop} with the ID specified in <code>dropId</code>
	 * to the list of read drops for the {@link Account} associated with the
	 * username specified in <code>authUser</code>
	 * 
	 * @param bucketId
	 * @param dropId
	 * @param authUser
	 */
	@Transactional(readOnly = false)
	public void markDropAsRead(Long bucketId, Long dropId, String authUser) {
		Bucket bucket = getBucket(bucketId);
		Account account = accountDao.findByUsername(authUser);

		if (!bucket.isPublished() && !this.isOwner(bucket, account)) {
			throw new ForbiddenException("Access denied");
		}
		
		// Only add a drop to the list if it doesn't exist
		BucketDrop bucketDrop = getBucketDrop(dropId, bucket);
		if (!bucketDropDao.isRead(bucketDrop, account)) {
			account.getReadBucketDrops().add(bucketDrop);
			accountDao.update(account);
		}
		
	}

	/**
	 * Returns all {@link Bucket} entities that contain the <code>searchTerm</code>
	 * in their <code>name</code> or <code>description</code> fields. Only entities
	 * with  <code>published = true</code> are returned
	 * 
	 * @param searchTerm
	 * @param count
	 * @param page
	 * @return
	 */
	public List<GetBucketDTO> findBuckets(String searchTerm, int count, int page) {
		List<Bucket> buckets = bucketDao.findAll(searchTerm, count, page);

		List<GetBucketDTO> bucketDTOs = new ArrayList<GetBucketDTO>();
		for (Bucket bucket: buckets) {
			bucketDTOs.add(mapper.map(bucket, GetBucketDTO.class));
		}

		return bucketDTOs;
	}

}
