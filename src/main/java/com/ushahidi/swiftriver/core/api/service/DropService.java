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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.DropDao;
import com.ushahidi.swiftriver.core.api.dao.IdentityDao;
import com.ushahidi.swiftriver.core.api.dao.LinkDao;
import com.ushahidi.swiftriver.core.api.dao.MediaDao;
import com.ushahidi.swiftriver.core.api.dao.PlaceDao;
import com.ushahidi.swiftriver.core.api.dao.TagDao;
import com.ushahidi.swiftriver.core.api.dto.DropDTO;
import com.ushahidi.swiftriver.core.api.dto.EntityDTO;
import com.ushahidi.swiftriver.core.api.dto.IdentityDTO;
import com.ushahidi.swiftriver.core.api.dto.LinkDTO;
import com.ushahidi.swiftriver.core.api.dto.MediaDTO;
import com.ushahidi.swiftriver.core.api.dto.PlaceDTO;
import com.ushahidi.swiftriver.core.api.dto.TagDTO;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Identity;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Media;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.Tag;

/**
 * Service class for drops
 * @author ekala
 *
 */
@Service
public class DropService {

//> DAO interfaces 
	@Autowired
	private DropDao dropDao;

	@Autowired
	private IdentityDao identityDao;
	
	@Autowired
	private LinkDao linkDao;
	
	@Autowired
	private TagDao tagDao;
	
	@Autowired
	private MediaDao mediaDao;
	
	@Autowired
	private PlaceDao placeDao;
	
	/* Logger */
	private static Logger LOG = LoggerFactory.getLogger(DropService.class);
	

	public void setDropDao(DropDao dropDao) {
		this.dropDao = dropDao;
	}

	public Drop getDrop(Long id) {
		return dropDao.findById(id);
	}

	/**
	 * Posts new drops and their associated metadata (tags, places, media
	 * links) to the database.
	 * @param drops
	 */
	@Transactional(readOnly = false)
	public void createDrops(ArrayList<Map<String, Object>> drops) {
		// In-memory store for DTOs that have been mapped to their respective entities 
		Set<Identity> mappedIdentities = new HashSet<Identity>();
		Set<Tag> mappedTags = new HashSet<Tag>();
		Set<Place> mappedPlaces = new HashSet<Place>();
		Set<Link> mappedLinks = new HashSet<Link>();
		Set<Media> mappedMedia = new HashSet<Media>();

		// DTOs for identities, drops, tags, places, links and media
		IdentityDTO identityDTO = new IdentityDTO();
		DropDTO dropDTO = new DropDTO();
		TagDTO tagDTO = new TagDTO();
		PlaceDTO placeDTO = new PlaceDTO();
		LinkDTO linkDTO = new LinkDTO();
		MediaDTO mediaDTO = new MediaDTO();

		// Maps identities and their respective drops
		Map<String, Set<Drop>> identityDropsMap = new HashMap<String, Set<Drop>>();
		Map<String, Drop> dropCache = new HashMap<String, Drop>();
		
		// Maps tags to their respective drops
		Map<String, Set<Tag>> dropTagsMap = new HashMap<String, Set<Tag>>();
		Map<String, Set<Place>> dropPlacesMap = new HashMap<String, Set<Place>>();
		Map<String, Set<Link>> dropLinksMap = new HashMap<String, Set<Link>>();
		Map<String, Set<Media>> dropMediaMap = new HashMap<String, Set<Media>>();

		// List of hashes for the mapped identity entities
		// Used for eliminating duplicate
		ArrayList<String> identityHashes = new ArrayList<String>();
		
		// Hashes for the mapped drop entities - used for eliminating
		// duplicates
		ArrayList<String> dropHashes = new ArrayList<String>();

		
		// Map the identities to models
		for (Map<String, Object> entry: drops) {
			// Create identity model and add it to identities collection
			Identity identity= identityDTO.createModel(entry);
			mappedIdentities.add(identity);

			// Set the identity for the drop
			entry.put("identity", identity);
			Drop drop = dropDTO.createModel(entry);

			String dropHash = drop.getDropletHash();
			dropHashes.add(dropHash);
			
			String identityHash = identity.getHash();
			identityHashes.add(identityHash);
			
			// Get the set of drops for the current identity
			Set<Drop> identityDrops = new HashSet<Drop>();

			if (identityDropsMap.get(identityHash) != null) {
				identityDrops = (HashSet<Drop>) identityDropsMap.get(identityHash);
				identityDropsMap.remove(identityHash);
			}
			
			// Add drop to the set of drops for the identity
			identityDrops.add(drop);
			
			// Put back the identity
			identityDropsMap.put(identityHash, identityDrops);
			
			// Create models for tags contained in the current drop entry
			this.<Tag>populateDropMetadataMap(dropHash, entry, "tags", tagDTO,dropTagsMap, mappedTags);

			// Create models for places in the current drop entry
			this.<Place>populateDropMetadataMap(dropHash, entry, "places", placeDTO, dropPlacesMap, mappedPlaces);
			
			// Create models for links contained in the current drop entry
			this.<Link>populateDropMetadataMap(dropHash, entry, "links", linkDTO, dropLinksMap, mappedLinks);
			
			// Create models for media contained in the current drop entry
			this.<Media>populateDropMetadataMap(dropHash, entry, "media", mediaDTO, dropMediaMap, mappedMedia);
		}
		
		// Post identities and drops to the DB
		this.createIdentities(identityHashes, mappedIdentities);
		this.postDrops(dropHashes, mappedIdentities, identityDropsMap, dropCache);
		
		// TODO Abort the next phase if no drops were posted to the DB
		
		// Post drop metadata to the DB
		this.createDropTags(dropTagsMap, mappedTags, dropCache);
		this.createDropPlaces(dropPlacesMap, mappedPlaces, dropCache);
		this.createDropLinks(dropLinksMap, mappedLinks, dropCache);
		this.createDropMedia(dropMediaMap, mappedMedia, dropCache);
	}

	/**
	 * Creates the new drops
	 * 
	 * @param dropHashes
	 * @param mappedIdentities
	 * @param identityDropsMap
	 */
	private void postDrops(ArrayList<String> dropHashes, Set<Identity> mappedIdentities,
			Map<String,Set<Drop>> identityDropsMap, Map<String, Drop> dropCache) {

		// Maintains the set of drops mapped to identities
		Set<Drop> mappedDrops = new HashSet<Drop>();

		// For each identity, get the associated drops and add
		// them to set of mapped drops
		for (Identity entry: mappedIdentities) {
			for (Drop drop: identityDropsMap.get(entry.getHash())) {
				drop.setIdentity(entry);
				mappedDrops.add(drop);
			}
		}

		// Remove duplicate drops 
		for (Drop drop: dropDao.findDropsByHash(dropHashes)) {
			mappedDrops.remove(drop);
		}
		
		// If there are no new drops, return
		if (mappedDrops.isEmpty()) {
			LOG.info("No new drops found. Exiting");
			return;
		}
		
		// Assign IDs to the new drops
		Long dropSequenceNumber = dropDao.getSequenceNumber("droplets", mappedDrops.size());
		for (Drop drop: mappedDrops) {
			// Remove the drop from the set
			mappedDrops.remove(drop);
			
			// Increment the sequence number and set the drop id
			dropSequenceNumber++;
			drop.setId(dropSequenceNumber);
			
			// Add the drop back
			mappedDrops.add(drop);

			// Cache the drop in memory
			dropCache.put(drop.getDropletHash(), drop);
		}
		
		// Post the new drops to the database
		dropDao.saveAll(mappedDrops);		
	}

	/**
	 * Creates the new drop tags
	 * 
	 * @param dropTagsMap
	 * @param mappedTags
	 * @param dropCache
	 */
	private void createDropTags(Map<String, Set<Tag>> dropTagsMap,
			Set<Tag> mappedTags, Map<String, Drop> dropCache) {

		ArrayList<String> tagHashes = new ArrayList<String>();
		for (Map.Entry<String, Set<Tag>> entry: dropTagsMap.entrySet()) {
			for (Tag tag: entry.getValue()) {
				tagHashes.add(tag.getHash());
			}
		}

		List<Tag> existingTags = tagDao.findByHash(tagHashes);
		for (Tag tag: existingTags) {
			mappedTags.remove(tag);
		}
		
		Long tagSequenceNumber = tagDao.getSequenceNumber("tags", mappedTags.size());
		for (Tag tag: mappedTags) {
			mappedTags.remove(tag);
			tagSequenceNumber++;
			tag.setId(tagSequenceNumber);
			mappedTags.add(tag);
		}

		tagDao.saveAll(mappedTags);
		mappedTags.addAll(existingTags);
		
		// Update the drop<--->tags in-memory mapping and post to the database
		// TODO Find a better way to do this 
		for (Tag tag: mappedTags) {
			for (Map.Entry<String, Set<Tag>> entry: dropTagsMap.entrySet()) {
				Set<Tag> dropTags = entry.getValue();
				for (Tag t: dropTags) {
					if (dropTags.contains(tag)) {
						dropTags.remove(t);
						dropTags.add(tag);
					}
				}
				
				Drop drop = dropCache.get(entry.getKey());
				dropDao.addTags(drop.getId(), dropTags);
			}
		}
	}

	/**
	 * Creates new places and associates them with the respective drops
	 * 
	 * @param dropPlacesMap
	 * @param mappedPlaces
	 * @param dropCache
	 */
	private void createDropPlaces(Map<String, Set<Place>> dropPlacesMap,
			Set<Place> mappedPlaces, Map<String, Drop> dropCache) {

		ArrayList<String> placeHashes = new ArrayList<String>();
		for (Map.Entry<String, Set<Place>> entry: dropPlacesMap.entrySet()) {
			for (Place place: entry.getValue()) {
				placeHashes.add(place.getHash());
			}
		}
		
		List<Place> existingPlaces = placeDao.findByHash(placeHashes);
		for (Place place: existingPlaces) {
			mappedPlaces.remove(place);
		}

		Long placeSequenceNumber = placeDao.getSequenceNumber("places", mappedPlaces.size());
		for (Place place: mappedPlaces) {
			mappedPlaces.remove(place);
			placeSequenceNumber++;
			place.setId(placeSequenceNumber);
			mappedPlaces.add(place);
		}

		placeDao.saveAll(mappedPlaces);
		mappedPlaces.addAll(existingPlaces);
		
		// Update drop--places mapping and post to the database
		for (Place place: mappedPlaces) {
			for (Map.Entry<String, Set<Place>> entry: dropPlacesMap.entrySet()) {
				Set<Place> dropPlaces = entry.getValue();
				for (Place p: dropPlaces) {
					if (dropPlaces.contains(place)) {
						dropPlaces.remove(p);
						dropPlaces.add(place);
					}
				}

				// Post to the database
				Drop drop = dropCache.get(entry.getKey());
				dropDao.addPlaces(drop.getId(), dropPlaces);
			}
		}
	}

	/**
	 * Creates new links and associates them with the respective drops
	 * 
	 * @param dropLinksMap
	 * @param mappedLinks
	 * @param dropCache
	 */
	private void createDropLinks(Map<String, Set<Link>> dropLinksMap,
			Set<Link> mappedLinks, Map<String, Drop> dropCache) {
		
		ArrayList<String> linkHashes = new ArrayList<String>();
		for (Map.Entry<String, Set<Link>> entry: dropLinksMap.entrySet()) {
			for (Link link: entry.getValue()) {
				linkHashes.add(link.getHash());
			}
		}

		List<Link> existingLinks = linkDao.findByHash(linkHashes);
		for (Link link: existingLinks) {
			mappedLinks.remove(link);
		}

		Long linkSequenceNumber = linkDao.getSequenceNumber("links", mappedLinks.size());
		for (Link link: mappedLinks) {
			mappedLinks.remove(link);
			linkSequenceNumber++;
			link.setId(linkSequenceNumber);
			mappedLinks.add(link);
		}

		linkDao.saveAll(mappedLinks);
		mappedLinks.addAll(existingLinks);
		
		// Update drop--links mapping and post to the DB
		for (Link link: mappedLinks) {
			for (Map.Entry<String, Set<Link>> entry: dropLinksMap.entrySet()) {
				Set<Link> dropLinks = entry.getValue();
				for (Link l: dropLinks) {
					if (dropLinks.contains(link)) {
						dropLinks.remove(l);
						dropLinks.add(link);
					}
				}
				
				Drop drop = dropCache.get(entry.getKey());
				dropDao.addLinks(drop.getId(), dropLinks);
			}
		}
	}

	/**
	 * Creates new media and associates them with the respective drops
	 * 
	 * @param dropMediaMap
	 * @param mappedMedia
	 * @param dropCache
	 */
	private void createDropMedia(Map<String, Set<Media>> dropMediaMap, Set<Media> mappedMedia,
			Map<String, Drop> dropCache) {
		
		ArrayList<String> mediaHashes = new ArrayList<String>();
		for (Map.Entry<String, Set<Media>> entry: dropMediaMap.entrySet()) {
			for (Media media: entry.getValue()) {
				mediaHashes.add(media.getHash());
			}
		}
		
		List<Media> existingMedia = mediaDao.findByHash(mediaHashes);
		for (Media media: existingMedia) {
			mappedMedia.remove(media);
		}

		Long mediaSequenceNumber = mediaDao.getSequenceNumber("media", mappedMedia.size());
		for (Media media: mappedMedia) {
			mappedMedia.remove(media);
			mediaSequenceNumber++;
			media.setId(mediaSequenceNumber);
			mappedMedia.add(media);
		}

		mediaDao.saveAll(mappedMedia);
		mappedMedia.addAll(existingMedia);
		
		// Update drop-media mapping and post to the DB
		for (Media media: mappedMedia) {
			for (Map.Entry<String, Set<Media>> entry: dropMediaMap.entrySet()) {
				Set<Media> dropMedia = entry.getValue();
				for (Media m: dropMedia) {
					if (dropMedia.contains(media)) {
						dropMedia.remove(m);
						dropMedia.add(media);
					}
				}
				
				Drop drop = dropCache.get(entry.getKey());
				dropDao.addMultipleMedia(drop.getId(), dropMedia);
			}
		}
	}

	/**
	 * Creates the newly submitted identities
	 * 
	 * @param identityHashes
	 * @param mappedIdentities
	 */
	private void createIdentities(ArrayList<String> identityHashes, Set<Identity> mappedIdentities) {

		// Remove duplicate identities
		List<Identity> existingIdentities = identityDao.findIdentitiesByHash(identityHashes); 
		for (Identity entry: existingIdentities) {
			mappedIdentities.remove(entry);
		}

		// Any new identities found?
		if (mappedIdentities.isEmpty()) {
			LOG.info("No new identities found");
		} else {
			LOG.info(String.format("Found %d new identities", mappedIdentities.size()));

			// Assign IDs to the new identities
			Long identitySequenceNo = identityDao.getSequenceNumber("identities", mappedIdentities.size());
			for (Identity entry: mappedIdentities) {
				mappedIdentities.remove(entry);

				identitySequenceNo++;
				entry.setId(identitySequenceNo);
				mappedIdentities.add(entry);
			}

			// Post the new identities to the database
			identityDao.saveAll(mappedIdentities);
		}
		
		// Merge the lists of the new and existing identities
		if (!existingIdentities.isEmpty()) {
			mappedIdentities.addAll(existingIdentities);
		}		
	}
	
	/**
	 * Populates the metadata map for a drop entry. This is a generic method where the type
	 * parameter is specified during invocation.
	 * 
	 * @param dropHash
	 * @param dropEntity
	 * @param metadataKey
	 * @param metadataEntityDTO
	 * @param metadataMap
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> void populateDropMetadataMap(String dropHash, Map<String, Object> dropEntity, 
			String metadataKey, EntityDTO metadataEntityDTO, Map<String, Set<T>> metadataMap,
			Set<T> metadataSet) {

		Set<T> dropMetadataSet = new HashSet<T>();

		for (Map<String, Object> entry: (ArrayList<Map<String, Object>>)dropEntity.get(metadataKey)) {
			T medataEntity = (T) metadataEntityDTO.createModel(entry);
			if (metadataMap.get(dropHash) != null) {
				dropMetadataSet = metadataMap.get(dropHash);
				metadataMap.remove(dropHash);
			}
			
			dropMetadataSet.add(medataEntity);
			metadataMap.put(dropHash, dropMetadataSet);

			metadataSet.add(medataEntity);
		}
	}
	
}
