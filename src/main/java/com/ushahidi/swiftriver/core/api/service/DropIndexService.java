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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.api.dto.GetDropDTO;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.solr.DropDocument;
import com.ushahidi.swiftriver.core.solr.repository.DropDocumentRepository;

@Service
public class DropIndexService {

	@Resource
	private DropDocumentRepository repository;
	
	@Autowired
	private Mapper mapper;

	@Autowired
	private DropDao dropDao;

	private NamedParameterJdbcTemplate namedJdbcTemplate;
	
	final Logger logger = LoggerFactory.getLogger(DropIndexService.class);
	
	public void setRepository(DropDocumentRepository repository) {
		this.repository = repository;
	}

	public void setMapper(Mapper mapper) {
		this.mapper = mapper;
	}

	public void setDropDao(DropDao dropDao) {
		this.dropDao = dropDao;
	}
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	/** Convenience method for testing purposes */
	public void setNamedJdbcTemplate(NamedParameterJdbcTemplate template) {
		this.namedJdbcTemplate = template;
	}

	/**
	 * Creates a {@link DropDocument} from the {@link Drop} specified
	 * in <code>drop</code>. It is the {@link DropDocument} instance
	 * that is added to the search index
	 * 
	 * @param drop
	 */
	@Transactional(readOnly = false)
	public void addToIndex(Drop drop) {
		List<Drop> drops = new ArrayList<Drop>();
		drops.add(drop);

		this.addAllToIndex(drops);
	}
	
	/**
	 * Adds a collection of {@link Drop} entities specified in <code>drops</code>
	 * to the search index en masse.
	 * 
	 * The {@link Drop} entities are transformed to {@link DropDocument} for
	 * purposes of consumption by {@link DropDocumentRepository} 
	 *  
	 * @param drops
	 */
	@Transactional(readOnly = false)
	public void addAllToIndex(List<Drop> drops) {
		// Set the places, bucket and river ids
		populatePlaces(drops);
		populateRiverIds(drops);
		populateBucketIds(drops);

		logger.debug("Performing batch add of drop entities");

		List<DropDocument> documents = new ArrayList<DropDocument>();
		for (Drop drop: drops) {
			DropDocument document = mapper.map(drop, DropDocument.class);

			// Set the places
			List<String> geo = new ArrayList<String>();
			for (Place place: drop.getPlaces()) {
				String coordinates = String.format("%s,%s", place.getLatitude(), place.getLongitude());
				geo.add(coordinates);
			}
			document.setGeo(geo);
			documents.add(document);
		}
		
		repository.save(documents);
		
		logger.debug("Successfully added {} drops to the search index", drops.size());
	}

	/**
	 * Deletes the {@link Drop} with the ID specified in <code>dropId</code>
	 * from the search index
	 * 
	 * @param dropId
	 */
	@Transactional(readOnly = false)
	public void deleteFromIndex(Long dropId) {
		logger.debug("Removing drop {} from the search index", dropId);
		repository.delete(dropId.toString());
	}
	
	/**
	 * Deletes the collection of {@link DropDocument} entities whose IDs are
	 * in the {@link List} specified in <code>dropIds</code> from the search index
	 * 
	 * @param dropIds
	 */
	@Transactional(readOnly = false)
	public void deleteAllFromIndex(List<Long> dropIds) {
		logger.debug("Deleting documents with IDs {} from the search index", dropIds);

		List<String> documentIds = new ArrayList<String>();
		for (Long dropId: dropIds) {
			documentIds.add(dropId.toString());
		}
		
		// Find all drop documents with the specified IDs
		List<DropDocument> dropDocuments = repository.findAll(documentIds);
		if (dropDocuments.isEmpty()) {
			logger.debug("No documents found in the search index");
			return;
		}

		repository.delete(dropDocuments);
		
		logger.debug("Successfully deleted {} documents from the search index",
				dropDocuments.size());
	}

	/**
	 * Finds and returns a {@link List} of {@link GetDropDTO}
	 * entities that contain the phrase specified in <code>searchTerm</code>
	 * in their <code>title</code> or <code>content</code>
	 * 
	 * The search is performed against the search index and the returned
	 * document IDs are used to retrieve the drops from the database.
	 * 
	 * @param searchTerm
	 * @param count
	 * @param page
	 * @return
	 */
	@Transactional
	public List<GetDropDTO> findDrops(String searchTerm, int count, int page) {
		// Pages are zero-indexed. Therefore, the first page is 0
		page = (page >= 1) ? page - 1 : page;

		// Search the index for drops containing searchTerm
		Pageable pageRequest = new PageRequest(page, count);
		List<DropDocument> dropDocuments = repository.find(searchTerm, pageRequest);

		List<GetDropDTO> drops = new ArrayList<GetDropDTO>();

		// Anything from the search?
		if (dropDocuments.isEmpty()) {
			// Log
			logger.debug(String.format("No documents found containing \"%s\" on page %d",
					searchTerm, count));

			// Return empty list
			return drops;
		}

		List<Long> dropIds = new ArrayList<Long>();
		for (DropDocument document: dropDocuments) {
			dropIds.add(Long.parseLong(document.getId()));
		}

		// Retrieve drops from the DB and transform to DTO
		for(Drop drop: dropDao.findAll(dropIds)) {
			drops.add(mapper.map(drop, GetDropDTO.class));
		}
		
		return drops;
	}
	
	/**
	 * Deletes all the documents from the search index
	 */
	@Transactional(readOnly = false)
	public void deleteAllFromIndex() {
		logger.debug("Flushing search index");
		repository.deleteAll();
	}


	/**
	 * Sets the <code>places</code> property for each of the 
	 * {@link Drop} entities in the <code>drops</code> parameter
	 * 
	 * @param drops
	 */
	private void populatePlaces(List<Drop> drops) {
		// Store the drop index
		Map<Long, Integer> dropIndex = new HashMap<Long, Integer>();
		List<Long> dropIds = new ArrayList<Long>();
		int index = 0;
		for (Drop drop: drops) {
			drop.setPlaces(new ArrayList<Place>());
			dropIds.add(drop.getId());
			dropIndex.put(drop.getId(), index);
			index++;
		}
		
		// Query to fetch the places associated with the drops 
		String sql = "SELECT droplets_places.droplet_id, " +
				"places.longitude, places.latitude " +
				"FROM places " +
				"INNER JOIN droplets_places ON (droplets_places.place_id = places.id) " +
				"WHERE droplets_places.droplet_id IN (:dropIds)";

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("dropIds", dropIds);

		for (Map<String, Object> row: namedJdbcTemplate.queryForList(sql, paramMap)) {
			Long dropId = ((Number) row.get("droplet_id")).longValue();

			Drop drop = drops.get(dropIndex.get(dropId));
			Place place = new Place();
			place.setLongitude(((Number)row.get("longitude")).floatValue());
			place.setLatitude(((Number)row.get("latitude")).floatValue());
			
			drop.getPlaces().add(place);
		}
	}

	/**
	 * Sets the <code>riverIds</code> property for each {@link Drop}
	 * in <code>drops</code>
	 * 
	 * @param drops
	 */
	private void populateRiverIds(List<Drop> drops) {
		// Store the drop index
		Map<Long, Integer> dropIndex = new HashMap<Long, Integer>();
		List<Long> dropIds = new ArrayList<Long>();
		int index = 0;
		for (Drop drop: drops) {
			dropIds.add(drop.getId());
			dropIndex.put(drop.getId(), index);
			index++;
		}

		String sql = "SELECT `droplet_id`, `river_id` " +
				"FROM `rivers_droplets` WHERE `droplet_id` IN (:dropIds)";
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("dropIds", dropIds);

		for(Map<String, Object> row: namedJdbcTemplate.queryForList(sql, paramMap)) {
			Long dropId = ((Number) row.get("droplet_id")).longValue();
			Long riverId = ((Number) row.get("river_id")).longValue();
			
			Drop drop = drops.get(dropIndex.get(dropId));
			if (drop.getRiverIds() == null) {
				drop.setRiverIds(new ArrayList<Long>());
			}
			drop.getRiverIds().add(riverId);
		}
		
	}

	/**
	 * Sets the <code>bucketIds</code> property for each {@link Drop}
	 * in <code>drops</code>
	 * @param drops
	 */
	private void populateBucketIds(List<Drop> drops) {
		// Store the drop index
		Map<Long, Integer> dropIndex = new HashMap<Long, Integer>();
		List<Long> dropIds = new ArrayList<Long>();
		int index = 0;
		for (Drop drop: drops) {
			dropIds.add(drop.getId());
			dropIndex.put(drop.getId(), index);
			index++;
		}
		
		String sql = "SELECT `droplet_id`, `bucket_id` " +
				"FROM `buckets_droplets` WHERE `droplet_id` IN (:dropIds)";

		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("dropIds", dropIds);

		for(Map<String, Object> row: namedJdbcTemplate.queryForList(sql, paramMap)) {
			Long dropId = ((Number) row.get("droplet_id")).longValue();
			Long bucketId = ((Number) row.get("bucket_id")).longValue();
			
			Drop drop = drops.get(dropIndex.get(dropId));
			if (drop.getBucketIds() == null) {
				drop.setBucketIds(new ArrayList<Long>());
			}
			drop.getBucketIds().add(bucketId);
		}
	}

}
