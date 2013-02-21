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
package com.ushahidi.swiftriver.core.api.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.DropComment;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Media;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.Tag;

public interface DropDao extends GenericDao<Drop> {
	
	/**
	 * Adds a collection of links to a drop
	 * 
	 * @param dropId
	 * @param links
	 */
	public void addLinks(long dropId, Collection<Link> links);
	
	/**
	 * Creates a new {@link Link} from <code>url</code> and associates it
	 * with the {@link Account} specified in <code>account</code>
	 * 
	 * @param drop
	 * @param account
	 * @param link
	 */
	public void addLink(Drop drop, Account account, Link link);

	/**
	 * Removes a link from the list of a drop's links
	 * @param drop
	 * @param link
	 */
	public void removeLink(Drop drop, Link link, Account account);

	/**
	 * Adds a collection of places to a drop
	 * @param dropId
	 * @param places
	 */
	public void addPlaces(long dropId, Collection<Place> places);
	
	/**
	 * Removes a place from the list of places contained in a drop
	 * @param drop
	 * @param place
	 */
	public void removePlace(Drop drop, Place place, Account account);
	
	/**
	 * Adds a collection of media to a drop
	 * @param dropId
	 * @param media
	 */
	public void addMultipleMedia(long dropId, Collection<Media> media);
		
	/**
	 * Adds a collection of tags to the drop
	 * @param dropId
	 * @param tags
	 */
	public void addTags(long dropId, Collection<Tag> tags);
	
	/**
	 * Removes a tag from a drop's tag collection
	 * @param drop
	 * @param tag
	 */
	public void removeTag(Drop drop, Tag tag, Account account);
	
	/**
	 * Returns all drops with a hash in @param dropHashes
	 *  
	 * @param dropHashes Hashes of drops
	 * @return
	 */
	public List<Drop> findDropsByHash(ArrayList<String> dropHashes);
	
	
	/**
	 * Populate the metadata into the drops in the given array.
	 * 
	 * @param drops
	 * @param queryingAccount 
	 * @return
	 */
	public void populateMetadata(List<Drop> drops, Account queryingAccount);

	/**
	 * Gets and returns the {@link DropComment} record with the specified
	 * <code>commentId</code> 
	 * 
	 * @param commentId
	 * 
	 * @return {@link DropComment}
	 */
	public DropComment findCommentById(Long commentId);

	/**
	 * Deletes the specified {@link DropComment} from the database
	 * 
	 * @param dropComment
	 */
	public void deleteComment(DropComment dropComment);

	/**
	 * Adds a new {@link DropComment} record to the drop_comments table
	 * 
	 * @param drop
	 * @param account
	 * @param commentText
	 * @return {@link DropComment}
	 */
	public DropComment addComment(Drop drop, Account account, String commentText);

	/**
	 * Adds the {@link Place} entity in <code>place</code> to the list of places
	 * for the {@link Drop} in <code>drop</code> but only accessible to the
	 * {@link Account} in <code>account</code>
	 *  
	 * @param drop
	 * @param account
	 * @param place
	 */
	public void addPlace(Drop drop, Account account, Place place);

	/**
	 * Adds the {@link Tag} entity in <code>tag</code> to the list of places
	 * for the {@link Drop} in <code>drop</code> but only accessible to the
	 * {@link Account} in <code>account</code>
	 * @param drop
	 * @param tag
	 * @param account
	 */
	public void addTag(Drop drop, Tag tag, Account account);
	
}
