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

import com.ushahidi.swiftriver.core.model.Account;
import com.ushahidi.swiftriver.core.model.Link;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.model.River;
import com.ushahidi.swiftriver.core.model.RiverDrop;
import com.ushahidi.swiftriver.core.model.RiverDropComment;
import com.ushahidi.swiftriver.core.model.RiverDropForm;
import com.ushahidi.swiftriver.core.model.RiverDropLink;
import com.ushahidi.swiftriver.core.model.RiverDropPlace;
import com.ushahidi.swiftriver.core.model.RiverDropTag;
import com.ushahidi.swiftriver.core.model.Tag;

public interface RiverDropDao extends GenericDao<RiverDrop>, ContextDropDao {

	/**
	 * Checks whether the {@link Tag} specified in <code>tag</code>
	 * has been added to the {@link RiverDrop} specified in <code>riverDrop</code>
	 * 
	 * @param riverDrop
	 * @param tag
	 * @return
	 */
	public RiverDropTag findTag(RiverDrop riverDrop, Tag tag);

	/**
	 * Adds the {@link Tag} specified in <code>tag</code> to the 
	 * {@link RiverDrop} specified in <code>riverDrop</code>
	 * 
	 * @param riverDrop
	 * @param tag
	 */
	public void addTag(RiverDrop riverDrop, Tag tag);

	/**
	 * Removes the {@link Tag} specified in <code>tag</code> from
	 * the {@link RiverDrop} specified in <code>riverDrop</code>
	 * 
	 * @param riverDrop
	 * @param tag
	 * @return
	 */
	public boolean deleteTag(RiverDrop riverDrop, Tag tag);

	/**
	 * Checks whether the {@link Place} specified in <code>place</code>
	 * has been added to the {@link RiverDrop} specified in <code>riverDrop</code>
	 * 
	 * @param riverDrop
	 * @param place
	 * @return
	 */
	public RiverDropPlace findPlace(RiverDrop riverDrop, Place place);

	/**
	 * Adds the {@link Place} specified in <code>place</code> to the 
	 * {@link RiverDrop} specified in <code>riverDrop</code>
	 * 
	 * @param riverDrop
	 * @param place
	 */
	public void addPlace(RiverDrop riverDrop, Place place);

	/**
	 * Removes the {@link Place} specified in <code>place</code> from
	 * the {@link RiverDrop} specified in <code>riverDrop</code>
	 * 
	 * @param riverDrop
	 * @param place
	 * @return
	 */
	public boolean deletePlace(RiverDrop riverDrop, Place place);

	/**
	 * Checks whether the {@link Link} specified in <code>link</code>
	 * has been added to the {@link RiverDrop} specified in <code>riverDrop</code>
	 * 
	 * @param riverDrop
	 * @param link
	 * @return
	 */
	public RiverDropLink findLink(RiverDrop riverDrop, Link link);

	/**
	 * Adds the {@link Link} specified in <code>link</code> to the 
	 * {@link RiverDrop} specified in <code>riverDrop</code>
	 * 
	 * @param riverDrop
	 * @param link
	 */
	public void addLink(RiverDrop riverDrop, Link link);

	/**
	 * Removes the {@link Link} specified in <code>link</code> from
	 * the {@link RiverDrop} specified in <code>riverDrop</code>
	 * 
	 * @param riverDrop
	 * @param link
	 * @return
	 */
	public boolean deleteLink(RiverDrop riverDrop, Link link);

	/**
	 * Create a new {@link RiverDropComment} entity for the {@link River}
	 * entity specified in <code>river</code>. 
	 * 
	 * @param riverDrop
	 * @param account
	 * @param commentText
	 * @return
	 */
	public RiverDropComment addComment(RiverDrop riverDrop, Account account,
			String commentText);

	/**
	 * Deletes the {@link RiverDropComment} with the ID specified
	 * in <code>commentId</code> from the database.
	 * 
	 * @param commentId
	 * @return
	 */
	public boolean deleteComment(Long commentId);
	
	/**
	 * Return the form with the specified id in the drop
	 * 
	 * @param drop
	 * @param formId
	 * @return
	 */
	public RiverDropForm findForm(Long dropId, Long formId);
	
	/**
	 * Checks whether the {@link RiverDrop} specified in <code>riverDrop</code>
	 * has been added to the list of read drops for the {@link Account}
	 * specified in <code>account</code>
	 * 
	 * @param riverDrop
	 * @param account
	 * @return
	 */
	public boolean isRead(RiverDrop riverDrop, Account account);
}
