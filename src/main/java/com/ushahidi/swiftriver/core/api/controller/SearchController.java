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
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
	 * Handles search requests
	 * 
	 * @param searchTerm
	 * @param searchType
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public List<Map<String, Object>> search(
			@RequestParam(value = "q", required = true) String searchTerm,
			@RequestParam(value = "type", required = false, defaultValue = "drops") String searchType,
			@RequestParam(value = "count", required = false, defaultValue = "50") int count,
			@RequestParam(value = "page", required = false, defaultValue = "1") int page) {

		List<ErrorField> errors = new ArrayList<ErrorField>();
		
		// Count must be > 1
		if (count < 1) errors.add(new ErrorField("count", "invalid"));

		// Page must be >= 0
		if (page < 0) errors.add(new ErrorField("page", "invalid"));
		
		// Validate the search type
		String[] validSearchTypes = {"drops", "buckets", "rivers", "users"};
		boolean isValidSearchType = false;
		for (String type: validSearchTypes) {
			if (type.equalsIgnoreCase(searchType)) {
				isValidSearchType = true;
				break;
			}
		}
		
		if (!isValidSearchType) errors.add(new ErrorField("type", "invalid"));
		
		// Do we have any errors
		if (!errors.isEmpty()) {
			BadRequestException e = new BadRequestException("Invalid search parameters");
			e.setErrors(errors);
			throw e;
		}

		// No errors, proceed
		LOGGER.debug("Search request for {}", searchType);		

		List<Map<String, Object>> results = new ArrayList<Map<String,Object>>();
		
		if (searchType.equalsIgnoreCase("drops")) {
			// Find the drops
			results = toMapList(dropIndexService.findDrops(searchTerm, count, page));
		} else if (searchType.equalsIgnoreCase("rivers")) {
			results = toMapList(riverService.findRivers(searchTerm, count, page));
		} else if (searchType.equalsIgnoreCase("buckets")) {
			results = toMapList(bucketService.findBuckets(searchTerm, count, page));
		} else if (searchType.equalsIgnoreCase("users")) {
			results = toMapList(accountService.searchAccounts(searchTerm, count, page));
		}
		
		if (results.isEmpty()) {
			throw new NotFoundException(String.format("We couldn't find any %s matching '%s'", searchType,
					searchTerm));
		}

		// Return result to client
		return results;
	}
	
	/**
	 * Generates and returns a {@link Map} representation of the entities
	 * specified in <code>source</code>
	 * 
	 * @param source
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> toMapList(List<? extends Object> source) {		
		List<Map<String, Object>> mapList = new ArrayList<Map<String,Object>>();
		ObjectMapper mapper = new ObjectMapper();
		for (Object entity: source) {
			Map<String, Object> entityMap = mapper.convertValue(entity, Map.class);
			mapList.add(entityMap);
		}
		return mapList;
	}
}
