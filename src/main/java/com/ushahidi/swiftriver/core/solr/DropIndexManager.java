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
package com.ushahidi.swiftriver.core.solr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.api.service.DropIndexService;
import com.ushahidi.swiftriver.core.model.Drop;

/**
 * Manages the drop index while the application is running. The tasks
 * performed are:
 * <ul>
 * <li>Adding drops to the search index</li>
 * <li>Removing orphaned drops (not in a bucket or river) from the search index</li>
 * </ul>
 */
public class DropIndexManager {

	@Autowired
	private DropDao dropDao;
	
	@Autowired
	private DropIndexService dropIndexService;

	// Indexer properties
	private Properties indexerProperties;

	// Indexer properties file
	private File propertiesFile;
	
	// Property key for the ID of the last drop to be indexed
	private String lastDropIdPropertyKey;

	// Property key for the no. of drops to fetch for indexing
	private String batchSizePropertyKey;
	
	// Logger
	final static Logger logger = LoggerFactory.getLogger(DropIndexManager.class);

	public void setIndexerProperties(Properties indexerProperties) {
		this.indexerProperties = indexerProperties;
	}

	public void setPropertiesFile(File propertiesFile) {
		this.propertiesFile = propertiesFile;
	}

	public void setLastDropIdPropertyKey(String lastDropIdPropertyKey) {
		this.lastDropIdPropertyKey = lastDropIdPropertyKey;
	}

	public void setBatchSizePropertyKey(String batchSizePropertyKey) {
		this.batchSizePropertyKey = batchSizePropertyKey;
	}

	/**
	 * Periodically adds drops to the search index. This method acts as
	 * a fail-safe in the event that the application is restarted before
	 * incoming drops are posted to the search index
	 * 
	 * @throws IOException
	 */
	public void updateIndex() throws IOException {
		int batchSize = Integer.parseInt(indexerProperties.getProperty(batchSizePropertyKey));
		long lastDropId = Long.parseLong(indexerProperties.getProperty(lastDropIdPropertyKey));

		List<Drop> drops = dropDao.findAll(lastDropId, batchSize);

		if (drops.isEmpty()) {
			logger.info("No drops found");
			return;
		}
		
		// Set the riverId and bucketId properties
		logger.info("Setting the riverId and bucketId properties");
		dropDao.populateRiverIds(drops);
		dropDao.populateBucketIds(drops);

		// Add drops to search index
		dropIndexService.addAllToIndex(drops);

		// Get the max drop id from the returned list
		lastDropId = drops.get(drops.size() - 1).getId();

		logger.info("Saving ID of last indexed drop {}", lastDropId);
		indexerProperties.setProperty(lastDropIdPropertyKey, Long.toString(lastDropId));
		
		OutputStream outputStream = new FileOutputStream(propertiesFile);
		indexerProperties.store(outputStream, null);
	}
}
