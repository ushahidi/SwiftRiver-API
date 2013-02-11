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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.api.dto.BucketDTO;
import com.ushahidi.swiftriver.core.api.dto.CollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.DropDTO;
import com.ushahidi.swiftriver.core.api.dto.FollowerDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ResourceNotFoundException;
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
	 * Creates a new {@link Bucket} and returns its DTO representation
	 * 
	 * @param bucketData
	 * @return {@link BucketDTO}
	 */
	@Transactional
	public BucketDTO createBucket(BucketDTO body) {
		Bucket bucket = mapper.map(body, Bucket.class);
//		bucket.setAccount(account);
		
		// Save bucket
		bucketDao.save(bucket);
		
		return mapper.map(bucket, BucketDTO.class);
	}

	/**
	 * Gets and returns a single bucket
	 * 
	 * @param id
	 * @return
	 */
	public BucketDTO getBucket(Long id) {
		Bucket bucket = bucketDao.findById(id);
		
		// Verify that the bucket exists
		if (bucket == null) {
			throw new ResourceNotFoundException();
		}
		
		return mapper.map(bucket, BucketDTO.class);
	}

	/**
	 * Modifies a bucket
	 * 
	 * @param id
	 * @param body
	 * @return
	 */
	@Transactional
	public BucketDTO modifyBucket(Long id, BucketDTO body) {
		Bucket bucket = bucketDao.findById(id);
		if (bucket == null) {
			throw new ResourceNotFoundException();
		}

		// Get the submitted name
		String bucketName = body.getName();

		// Check if the owner already have a bucket with the
		// specified name
		if (!bucket.getName().equals(bucketName) && 
				bucketDao.findBucketByName(bucket.getAccount(), bucketName) != null) {
			throw new BadRequestException();
		}
		
		Bucket updated = mapper.map(body, Bucket.class);		

		bucket.setName(bucketName);
		bucket.setPublished(updated.isPublished());
		bucket.setActive(updated.isActive());
		
		bucketDao.update(bucket);
		
		return mapper.map(bucket, BucketDTO.class);
	}

	/**
	 * Deletes the bucket with the specified <code>id</code>
	 * 
	 * @param id
	 */
	@Transactional
	public void deleteBucket(Long id) {
		Bucket bucket = bucketDao.findById(id);
		if (bucket == null) {
			throw new ResourceNotFoundException();
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
	 * @param body
	 * @return
	 */
	@Transactional
	public CollaboratorDTO addCollaborator(Long id, CollaboratorDTO body) {
		// Does the bucket exist
		Bucket bucket = bucketDao.findById(id);
		
		if (bucket == null) {
			throw new ResourceNotFoundException("The specified bucket does not exist");
		}
		
		//FIXME Validation check to determine if the account performing the 
		// operation is an owner of the bucket. Throw UnauthorizedException
		
		// Does the account already exist as a collaborator?
		if (bucketDao.findCollaborator(id, body.getId()) != null) {
			LOG.error(String.format("The specified account(%d) is already collaborating on bucket(%d)", 
					id, body.getId()));
			throw new BadRequestException();
		}
		
		Account account = accountDao.findById(body.getId());
		if (account == null) {
			throw new BadRequestException("The specified account id does not exist");
		}
	
		bucketDao.addCollaborator(bucket, account, body.isReadOnly());
		
		body.setId(account.getId());
		return body;
	}

	/**
	 * Gets and returns a the of users collaborating on the
	 * bucket specified by <code>id</code>
	 * 
	 * @param id
	 * @return {@link List}
	 */
	@Transactional
	public List<CollaboratorDTO> getCollaborators(Long id) {
		Bucket bucket = bucketDao.findById(id);
		if (bucket == null) {
			throw new ResourceNotFoundException();
		}
		
		List<CollaboratorDTO> collaborotorList = new ArrayList<CollaboratorDTO>();

		// Iterate through each collaborator and add them
		// to the return list
		for (BucketCollaborator entry: bucket.getCollaborators()) {
			CollaboratorDTO dto = new CollaboratorDTO();

			dto.setId(entry.getAccount().getId());
			dto.setActive(entry.isActive());
			dto.setReadOnly(entry.isReadOnly());
			dto.setDateAdded(entry.getDateAdded());
			
			collaborotorList.add(dto);
		}

		return collaborotorList;
	}

	/**
	 * Modifies and returns the modified collaborator identified by <code>accountId</code>
	 * for the river in <code>id</code>
	 * 
	 * @param id Database id of the {@link Bucket} to which a collaborator is to be added
	 * @param accountId Database id of the {@link Account} to be added as a collaborator
	 * @param body
	 * @return {link BucketCollaboratorDTO}
	 */
	@Transactional
	public CollaboratorDTO modifyCollaborator(Long id, Long accountId,
			CollaboratorDTO body) {

		if (bucketDao.findById(id) == null) {
			throw new ResourceNotFoundException();
		}

		// Locate the collaborator
		BucketCollaborator collaborator = bucketDao.findCollaborator(id, accountId);
		
		if (collaborator == null) {
			throw new ResourceNotFoundException();
		}
		
		collaborator.setActive(body.isActive());
		collaborator.setReadOnly(body.isReadOnly());
		
		// Update
		bucketDao.updateCollaborator(collaborator);
		
		body.setId(collaborator.getId());

		return body;
	}

	/**
	 * Removes the account with the specified <code>accountId</code> from
	 * the list of collaborators on the bucket with the specified <code>id</code>
	 * parameter
	 * 
	 * @param id Database id of the {@link Bucket}
	 * @param accountId Database id of the collaborating {@link Account}
	 */
	@Transactional
	public void deleteCollaborator(Long id, Long accountId) {
		BucketCollaborator collaborator = bucketDao.findCollaborator(id, accountId);
		if (collaborator == null) {
			throw new ResourceNotFoundException();
		}

		bucketDao.deleteCollaborator(collaborator);
		
	}

	/**
	 * Adds the {@link Account} with the specified id in <code>body</code>
	 * to the list of followers for the {@link Bucket} identified by
	 * <code>id</code>
	 * 
	 * @param id
	 * @param body
	 */
	@Transactional
	public void addFollower(Long id, FollowerDTO body) {
		Bucket bucket = bucketDao.findById(id);
		if (bucket == null) {
			throw new ResourceNotFoundException();
		}
		
		Account account = accountDao.findById(body.getId());
		if (account == null) {
			throw new ResourceNotFoundException();
		}
		
		// Is the account already following the bucket
		if (bucket.getFollowers().contains(account)) {
			LOG.error(String.format("The specified account - %d - is already following bucket %d", 
					body.getId(), id));
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
		Bucket bucket = bucketDao.findById(id);
		if (bucket == null) {
			throw new ResourceNotFoundException();
		}
		
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
	@Transactional
	public void deleteFollower(Long id, Long accountId) {
		Bucket bucket = bucketDao.findById(id);
		if (bucket == null) {
			throw new ResourceNotFoundException();
		}
		
		Account account = accountDao.findById(accountId);
		if (account == null) {
			throw new ResourceNotFoundException();
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
	 * @param requestParams
	 * @return
	 */
	@Transactional
	public List<DropDTO> getDrops(Long id, Map<String, Object> requestParams) {
		Bucket bucket = bucketDao.findById(id);
		if (bucket == null) {
			throw new ResourceNotFoundException();
		}
		
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

		List<Drop> drops = bucketDao.getDrops(id, requestParams);
		
		List<DropDTO> bucketDrops = new ArrayList<DropDTO>();
		for (Drop drop: drops) {			
			DropDTO dropDto = mapper.map(drop, DropDTO.class);
			bucketDrops.add(dropDto);
		}

		return bucketDrops;
	}

	/**
	 * Deletes the {@link Drop} specified in <code>bucketId</code> from the
	 * {@link Bucket} specified in <code>id</code>
	 * 
	 * If the drop does not exist in the specified bucket, a
	 * {@link ResourceNotFoundException} exception is thrown
	 * 
	 * @param id
	 * @param dropId
	 */
	@Transactional
	public void deleteDrop(Long id, Long dropId) {		 
		if (!bucketDao.deleteDrop(id, dropId)) {
			throw new ResourceNotFoundException();
		}
	}

}
