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
package com.ushahidi.swiftriver.core.solr.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Utility class for Solr search operations
 * 
 * @author ekala
 */
public class QueryUtil {

	/**
	 * Given a comma-separated list of search terms; 
	 * <code>searchTerms</code>, converts the list to an array
	 * and concatenates the elements using "AND".
	 * 
	 * If <code>searchTerm</code> comprises of only one string,
	 * no modification takes place
	 * 
	 * @param searchTerms
	 * @return
	 */
	public static String getQueryString(String searchTerms) {
		if (searchTerms == null || searchTerms.trim().length() == 0)
			return "*:*";

		List<String> keywordsList = new ArrayList<String>();

		// Sanitize the each keyword
		for(String keyword: searchTerms.split(",")) {
			if (searchTerms.trim().length() > 1) {
				keywordsList.add(keyword.trim());
			}
		}
		String[] keywordsArray = keywordsList.toArray(new String[keywordsList.size()]);

		return keywordsArray.length == 1 
				? searchTerms : StringUtils.join(keywordsArray, " AND ");
		
	}
}
