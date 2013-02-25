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
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.controller.BucketsController;
import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.api.dto.CreateBucketDTO;
import com.ushahidi.swiftriver.core.api.dto.CreateCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.FollowerDTO;
import com.ushahidi.swiftriver.core.api.dto.GetBucketDTO;
import com.ushahidi.swiftriver.core.api.dto.GetCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO;
import com.ushahidi.swiftriver.core.api.dto.ModifyCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ForbiddenException;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.api.exception.UnauthorizedExpection;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketCollaborator;
import com.ushahidi.swiftriver.core.model.Drop;

/**
 * Service class for buckets
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

	/* Logger */
	final static Logger LOG = LoggerFactory.getLogger(BucketService.class);

	public void setBucketDao(BucketDao bucketDao) {
		this.bucketDao = bucketDao;
	}

	/**
	 * Creates a new {@link Bucket} entity under the {@link Account}
	 * associated with <code>username</code>.
	 * 
	 * The created entity is transformed to DTO
	 * `
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
			throw new BadRequestException(String.format("User %s already has a bucket named %s",
					username, bucketName));
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
	 * Gets and returns a {@link Bucket} entity. If the {@link Bucket}
	 * has the <code>published</code> property set to true, the
	 * querying {@link Account} associated with <code>username</code>
	 * must be an owner of the {@link Bucket} otherwise {@link BucketsController}
	 * returns a 404 to the client
	 * 
	 * The retrieved entity is transformed to DTO for purposes of 
	 * consumption by {@link BucketsController}
	 * 
	 * @param id
	 * @param username TODO
	 * @return
	 */
	public GetBucketDTO getBucket(Long id, String username) {
		Bucket bucket = getBucketById(id);

		Account queryingAccount = accountDao.findByUsername(username);

		if (!bucket.isPublished() && !queryingAccount.equals(bucket.getAccount())) {
			// Is the querying account a collaborator?
			if (!isCollaborator(bucket, queryingAccount, true)) {
				throw new NotFoundException();
			}
		}

		return mapper.map(bucket, GetBucketDTO.class);
	}

	/**
	 * Modifies a bucket with the specified <code>id</code> using
	 * the information provided in <code>modifiedBucket</code>
	 * If the {@link Account} associated with <code>username</code>
	 * is not the creator of the bucket or is not a collaborator with
	 * edit privileges, an {@link UnauthorizedExpection} is throw.
	 * 
	 * The modified {@link Bucket} is transformed into a DTO for purposes
	 * of consumption by {@link BucketsController}
	 * 
	 * @param id
	 * @param modifiedBucket
	 * @param username
	 * @return
	 */
	@Transactional(readOnly = false)
	public GetBucketDTO modifyBucket(Long id, CreateBucketDTO modifiedBucket, String username) {
		Bucket bucket = bucketDao.findById(id);
		if (bucket == null) {
			throw new NotFoundException();
		}

		Account account = accountDao.findByUsername(username);

		// Is the account the creator of the bucket or a collaborator with edit privileges?
		if (!isOwner(bucket, account)) {
			throw new UnauthorizedExpection(String.format("% is not authorized to modify this bucket",
					username));			
		}

		// Get the submitted name
		String bucketName = modifiedBucket.getName();

		if (bucketName == null) {
			throw new BadRequestException("The bucket name must be specified");
		}

		// Check if the owner already has a bucket with the
		// specified name
		Bucket existingBucket = bucketDao.findBucketByName(bucket.getAccount(), bucketName); 
		if (!bucket.getName().equals(bucketName) && existingBucket != null && 
				existingBucket.getId() != bucket.getId()) {
			throw new BadRequestException(String.format("Another bucket named %s already exists", bucketName));
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
	 * {@link Account} associated with <code>username</code> must
	 * be the creator of the {@link Bucket} else a {@link ForbiddenException}
	 * is thrown
	 * 
	 * @param id
	 * @param username 
	 */
	@Transactional(readOnly = false)
	public void deleteBucket(Long id, String username) {
		Bucket bucket = getBucketById(id);
		Account queryingAccount = accountDao.findByUsername(username);
		if (!bucket.getAccount().equals(queryingAccount)) {
			throw new ForbiddenException();
		}
		bucketDao.delete(bucket);
	}

	/**
	 * Adds a collaborator to the bucket specified by <id>. 
	 * 
	 * If the {@link Account} id specified in <code>body</code> has already 
	 * been added as a collaborator, a {@link BadRequestException} is thrown
	 * and execution halts.
	 *  
	 * @param id
	 * @param createDTO
	 * @param username
	 * @return
	 */
	@Transactional(readOnly = false)
	public GetCollaboratorDTO addCollaborator(Long id, CreateCollaboratorDTO createDTO, String username) {
		// Does the bucket exist
		Bucket bucket = getBucketById(id);

		// Verify that the querying account is authorized
		Account queryingAccount = accountDao.findByUsername(username);
		if (!isOwner(bucket, queryingAccount)) {
			throw new UnauthorizedExpection(String.format("%s is not authorized to add a collaborator",
					username));
		}

		Account account = accountDao.findById(createDTO.getAccount().getId());
		if (account == null) {
			throw new BadRequestException("The specified account id does not exist");
		}

		// Does the account already exist as a collaborator?
		BucketCollaborator collaborator = bucketDao.findCollaborator(bucket, account);
		if (collaborator != null) {
			LOG.error(String.format("The specified account(%d) is already collaborating on bucket(%d)", 
					id, createDTO.getAccount().getId()));

			throw new BadRequestException();
		}
		
	
		collaborator = bucketDao.addCollaborator(bucket, account, createDTO.isReadOnly());
		
		return mapper.map(collaborator, GetCollaboratorDTO.class);
	}

	/**
	 * Gets and returns a the of users collaborating on the
	 * bucket specified by <code>id</code>
	 * 
	 * @param id
	 * @return {@link List}
	 */
	public List<GetCollaboratorDTO> getCollaborators(Long id) {
		Bucket bucket = getBucketById(id);		
		List<GetCollaboratorDTO> collaborotorList = new ArrayList<GetCollaboratorDTO>();

		// Iterate through each collaborator and add them
		// to the return list
		for (BucketCollaborator collaborator: bucket.getCollaborators()) {
			collaborotorList.add(mapper.map(collaborator, GetCollaboratorDTO.class));
		}

		return collaborotorList;
	}

	/**
	 * Modifies and returns the modified collaborator identified by <code>accountId</code>
	 * for the river in <code>id</code>
	 * 
	 * @param id Database id of the {@link Bucket} to which a collaborator is to be added
	 * @param accountId Database id of the {@link Account} to be added as a collaborator
	 * @param createDTO
	 * @param username
	 * @return {link BucketCollaboratorDTO}
	 */
	@Transactional(readOnly = false)
	public GetCollaboratorDTO modifyCollaborator(Long id, Long accountId,
			ModifyCollaboratorDTO createDTO, String username) {
		
		Bucket bucket = getBucketById(id);

		Account queryAccount = accountDao.findByUsername(username);
		if (!isOwner(bucket, queryAccount)) {
			throw new UnauthorizedExpection();
		}

		Account account = accountDao.findById(accountId);

		// Locate the collaborator
		BucketCollaborator collaborator = bucketDao.findCollaborator(bucket, account);
		
		if (collaborator == null) {
			throw new NotFoundException();
		}
		
		collaborator.setActive(createDTO.getActive());
		collaborator.setReadOnly(createDTO.getReadOnly());
		
		// Update
		bucketDao.updateCollaborator(collaborator);
		
		return mapper.map(collaborator, GetCollaboratorDTO.class);
	}

	/**
	 * Removes the account with the specified <code>accountId</code> from
	 * the list of collaborators on the bucket with the specified <code>id</code>
	 * parameter
	 * 
	 * @param id Database id of the {@link Bucket}
	 * @param accountId Database id of the collaborating {@link Account}
	 * @param username TODO
	 */
	@Transactional(readOnly = false)
	public void deleteCollaborator(Long id, Long accountId, String username) {
		Bucket bucket = getBucketById(id);

		Account queryAccount = accountDao.findByUsername(username);
		if (!isOwner(bucket, queryAccount)) {
			throw new UnauthorizedExpection();
		}

		Account account = accountDao.findById(accountId);

		BucketCollaborator collaborator = bucketDao.findCollaborator(bucket, account);
		if (collaborator == null) {
			throw new NotFoundException();
		}

		bucketDao.deleteCollaborator(collaborator);
		
	}

	/**
	 * Adds the {@link Account} specified in <code>accountId</code>
	 * to the list of followers for the {@link Bucket} identified by
	 * <code>id</code>
	 * 
	 * @param id
	 * @param accountId
	 * @param username
	 */
	@Transactional(readOnly = false)
	public void addFollower(long id, long accountId, String username) {
		Bucket bucket = getBucketById(id);
		
		Account account = accountDao.findById(accountId);
		if (account == null) {
			throw new NotFoundException();
		}
		
		// Verify that the account following the bucket is tied to the
		// currently logged in user
		if (!account.getOwner().getUsername().equals(username)) {
			throw new UnauthorizedExpection();
		}

		// Is the account already following the bucket
		if (bucket.getFollowers().contains(account)) {
			LOG.error(String.format("%s  is already following bucket %d", 
					accountId, id));
			throw new BadRequestException();
		}
		
		bucket.getFollowers().add(account);
		bucketDao.update(bucket);
		
	}

	/**
	 * Gets and returns the list of followers for the {@link Bucket}
	 * with the specified <code>id</code>
	 * 
	 * @param id
	 * @return {@link List<AccountDTO>}
	 */
	public List<FollowerDTO> getFollowers(Long id) {
		Bucket bucket = getBucketById(id);
		
		List<FollowerDTO> followers = new ArrayList<FollowerDTO>();
		for (Account account: bucket.getFollowers()) {
			FollowerDTO dto = mapper.map(account, FollowerDTO.class);
			
			// Set the name and email address
			dto.setName(account.getOwner().getName());
			dto.setEmail(account.getOwner().getEmail());
			
			followers.add(dto);
		}
		
		return followers;
	}

	/**
	 * Removes the account with the specified <code>accountId</code>
	 * from the list of {@link Account}s following the bucket identified
	 * by <code>id</code>
	 * 
	 * @param id
	 * @param accountId
	 */
	@Transactional(readOnly = false)
	public void deleteFollower(Long id, Long accountId) {
		Bucket bucket = getBucketById(id);
		
		Account account = accountDao.findById(accountId);
		if (account == null) {
			throw new NotFoundException();
		}

		bucket.getFollowers().remove(account);
		bucketDao.update(bucket);
	}

	/**
	 * Gets and returns the drops for the bucket specified in <code>id</code>
	 * using the parameters contained in <code>requestParams</code>
	 * 
	 * Nullity and type safety checks are performed on the request parameters
	 * before they're passed on to DAO for building out the DB query and subsequent
	 * fetching of the {@link Drop} entities
	 *  
	 * @param id
	 * @param username
	 * @param requestParams
	 * @return
	 */
	public List<GetDropDTO> getDrops(Long id, String username, Map<String, Object> requestParams) {
		Bucket bucket = getBucketById(id);
		
		// Check for channels parameter, split the string and convert the
		// resultant array to a list
		if (requestParams.containsKey("channels")) {
			String channels = (String)requestParams.get("channels");
			if (channels.trim().length() == 0) {
				LOG.error("No value specified for the \"channels\" parameter.");
				throw new BadRequestException();
			}
			List<String> channelsList = Arrays.asList(StringUtils.split(channels, ','));
			requestParams.put("channels", channelsList);
		}

		Account account = accountDao.findByUsername(username);
		List<Drop> drops = bucketDao.getDrops(bucket.getId(), account, requestParams);
		
		List<GetDropDTO> bucketDrops = new ArrayList<GetDropDTO>();
		for (Drop drop: drops) {			
			GetDropDTO dropDto = mapper.map(drop, GetDropDTO.class);
			bucketDrops.add(dropDto);
		}

		return bucketDrops;
	}

	/**
	 * Deletes the {@link Drop} specified in <code>bucketId</code> from the
	 * {@link Bucket} specified in <code>id</code>
	 * 
	 * If the drop does not exist in the specified bucket, a
	 * {@link NotFoundException} exception is thrown
	 * 
	 * @param id
	 * @param dropId
	 */
	@Transactional(readOnly = false)
	public void deleteDrop(Long id, Long dropId) {		 
		if (!bucketDao.deleteDrop(id, dropId)) {
			throw new NotFoundException();
		}
	}
	
	/**
	 * Adds the {@link Drop} specified in <code>dropId</code> to the {@link Bucket}
	 * in <code>id</code>. The {@link Account} associated with <code>username</code>
	 * is used to verify whether the user submitting the request is authorized
	 *  
	 * @param id
	 * @param dropId
	 * @param username
	 */
	@Transactional
	public void addDrop(long id, long dropId, String username) {
		Bucket bucket = getBucketById(id);		
		Account account = accountDao.findByUsername(username);
	
		if (!isOwner(bucket, account)) {
			throw new UnauthorizedExpection();
		}
		
		if (!bucketDao.addDrop(bucket, dropId)) {
			throw new BadRequestException();
		}
		
	}

	/**
	 * Internal helper method to retrieve a bucket using its <code>id</code>
	 * in the database
	 * 
	 * @param id
	 * @return
	 */
	private Bucket getBucketById(long id) {
		Bucket bucket = bucketDao.findById(id);
		if (bucket == null) {
			throw new NotFoundException();
		}
		
		return bucket;
	}
	
	/**
	 * Verifies whether the {@link Account} in <code>account</code>
	 * is an owner of the {@link Bucket} specified in <code>bucket</code>
	 * 
	 * An owner is an {@link Account} that is a creator of the {@Bucket}
	 * or a {@link BucketCollaborator} with edit privileges i.e. the
	 * <code>readOnly</code> property is false
	 * 
	 * @param bucket
	 * @param account
	 * @return
	 */
	private boolean isOwner(Bucket bucket, Account account) {
		if (account.equals(bucket.getAccount())) {
			return true;
		}
		
		return isCollaborator(bucket, account, false);

	}

	/**
	 * Verifies whether the {@link Account} in <code>account</code> is collaborating
	 * on the {@link Bucket} in <code>bucket</code>
	 * 
	 * @param bucket
	 * @param account
	 * @param readOnly
	 * @return
	 */
	private boolean isCollaborator(Bucket bucket, Account account, boolean readOnly) {
		BucketCollaborator collaborator = bucketDao.findCollaborator(bucket, account);
		if (collaborator == null) {
			return false;
		}
		
		return (readOnly && collaborator.isReadOnly()); 
	}
}
