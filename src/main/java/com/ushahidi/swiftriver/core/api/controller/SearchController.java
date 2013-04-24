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
package com.ushahidi.swiftriver.core.api.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ushahidi.swiftriver.core.api.dto.GetAccountDTO;
import com.ushahidi.swiftriver.core.api.dto.GetBucketDTO;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO;
import com.ushahidi.swiftriver.core.api.dto.GetRiverDTO;
import com.ushahidi.swiftriver.core.api.exception.BadRequestException;
import com.ushahidi.swiftriver.core.api.exception.ErrorField;
import com.ushahidi.swiftriver.core.api.exception.NotFoundException;
import com.ushahidi.swiftriver.core.api.service.AccountService;
import com.ushahidi.swiftriver.core.api.service.BucketService;
import com.ushahidi.swiftriver.core.api.service.DropIndexService;
import com.ushahidi.swiftriver.core.api.service.RiverService;

/**
 * Search controller - Handles search requests for drops, 
 * rivers, buckets and users
 * 
 * @author ekala
 */
@Controller
@RequestMapping("/v1/search")
public class SearchController extends AbstractController {

	@Autowired
	private DropIndexService dropIndexService;

	@Autowired
	private BucketService bucketService;
	
	@Autowired
	private RiverService riverService;
	
	@Autowired
	private AccountService accountService;
	
	// Logger
	final static Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

	/**
	 * Handles searching for drops by keyword
	 * 
	 * @param searchTerm
	 * @param searchType
	 * @return
	 */
	@RequestMapping(value = "/drops", method = RequestMethod.GET)
	@ResponseBody
	public List<GetDropDTO> searchDrops(
			@RequestParam(value = "q", required = true) String searchTerm,
			@RequestParam(value = "count", required = false, defaultValue = "50") int count,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page) {

		validateBasicParameters(count, page);

		// Find and return the drops
		List<GetDropDTO> drops = dropIndexService.findDrops(searchTerm, count, page);
		if (drops.isEmpty())
			throw new NotFoundException(String.format(
					"Did not find drops matching '%s' on page %d", searchTerm, page));
		
		return drops;
	}
	
	/**
	 * Handles searching for rivers by keyword
	 * 
	 * @param searchTerm
	 * @param count
	 * @param page
	 * @return
	 */
	@RequestMapping(value = "/rivers", method = RequestMethod.GET)
	@ResponseBody
	public List<GetRiverDTO> searchRivers(
			@RequestParam(value = "q", required = true) String searchTerm,
			@RequestParam(value = "count", required = false, defaultValue = "20") int count,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page) {
		
		validateBasicParameters(count, page);
		List<GetRiverDTO> rivers = riverService.findRivers(searchTerm, count, page);
		
		if (rivers.isEmpty()) {
			throw new NotFoundException(String.format(
					"Did not find any rivers matching '%s' on page %d", searchTerm, page));
		}
		
		return rivers;
	}
	
	/**
	 * Handles searching for buckets by keyword
	 * 
	 * @param searchTerm
	 * @param count
	 * @param page
	 * @return
	 */
	@RequestMapping(value = "/buckets", method = RequestMethod.GET)
	@ResponseBody
	public List<GetBucketDTO> searchBuckets(
			@RequestParam(value = "q", required = true) String searchTerm,
			@RequestParam(value = "count", required = false, defaultValue = "20") int count,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page) {
		
		validateBasicParameters(count, page);
		List<GetBucketDTO> buckets = bucketService.findBuckets(searchTerm, count, page);
		
		if (buckets.isEmpty()) {
			throw new NotFoundException(String.format(
					"Did not find any bucktes matching '%s' on page %d", searchTerm, page));
		}
		return buckets;
	}
	
	/**
	 * Handles searching for accounts by keyword
	 * 
	 * @param searchTerm
	 * @param count
	 * @param page
	 * @return
	 */
	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	@ResponseBody
	public List<GetAccountDTO> searchAccounts(
			@RequestParam(value = "q", required = true) String searchTerm,
			@RequestParam(value = "count", required = false, defaultValue = "20") int count,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page) {
		
		validateBasicParameters(count, page);
		List<GetAccountDTO> accounts =  accountService.searchAccounts(searchTerm, count, page);
		
		if (accounts.isEmpty()) {
			throw new NotFoundException(String.format(
					"Did not find any accounts matching '%s' on page %d", searchTerm, page));
		}
		return accounts;
	}
	
	/**
	 * Utility method to validate the count and page query parameters
	 * 
	 * @param count
	 * @param page
	 */
	private void validateBasicParameters(int count, int page) {
		List<ErrorField> errors = new ArrayList<ErrorField>();
		
		// Count must be > 1
		if (count < 1) errors.add(new ErrorField("count", "invalid"));

		// Page must be >= 0
		if (page < 0) errors.add(new ErrorField("page", "invalid"));
		
		// Do we have any errors
		if (!errors.isEmpty()) {
			BadRequestException e = new BadRequestException("Invalid search parameters");
			e.setErrors(errors);
			throw e;
		}
		
	}
	
}
