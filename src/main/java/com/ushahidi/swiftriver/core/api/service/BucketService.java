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
import java.util.List;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.AccountDao;
import com.ushahidi.swiftriver.core.api.dao.BucketDao;
import com.ushahidi.swiftriver.core.api.dto.BucketCollaboratorDTO;
import com.ushahidi.swiftriver.core.api.dto.BucketDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ResourceNotFoundException;
import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Bucket;
import com.ushahidi.swiftriver.core.model.BucketCollaborator;

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
		
		Bucket updated = mapper.map(body, Bucket.class);

		bucket.setName(updated.getName());
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
	public BucketCollaboratorDTO addCollaborator(Long id, BucketCollaboratorDTO body) {
		// Does the bucket exist
		Bucket bucket = bucketDao.findById(id);
		
		if (bucket == null) {
			throw new ResourceNotFoundException("The specified bucket does not exist");
		}
		
		//FIXME Validation check to determine if the account performing the 
		// operation is an owner of the bucket. Throw UnauthorizedException
		
		// Does the account already exist as a collaborator?
		BucketCollaborator collaborator = bucketDao.findCollaborator(id, body.getId());
		
		if (collaborator != null) {
			LOG.error(String.format("The specified account(%d) is already collaborating on bucket(%d)", 
					id, body.getId()));
			throw new BadRequestException();
		}
		
		Account account = accountDao.findById(body.getId());
		if (account == null) {
			throw new BadRequestException("The specified account id does not exist");
		}
	
		collaborator= bucketDao.addCollaborator(bucket, account, body.isReadOnly());
		
		body.setId(collaborator.getAccount().getId());
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
	public List<BucketCollaboratorDTO> getCollaborators(Long id) {
		Bucket bucket = bucketDao.findById(id);
		if (bucket == null) {
			throw new ResourceNotFoundException();
		}
		
		List<BucketCollaboratorDTO> collaborotorList = new ArrayList<BucketCollaboratorDTO>();

		// Iterate through each collaborator and add them
		// to the return list
		for (BucketCollaborator entry: bucket.getCollaborators()) {
			BucketCollaboratorDTO dto = new BucketCollaboratorDTO();

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
	public BucketCollaboratorDTO modifyCollaborator(Long id, Long accountId,
			BucketCollaboratorDTO body) {

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

}
