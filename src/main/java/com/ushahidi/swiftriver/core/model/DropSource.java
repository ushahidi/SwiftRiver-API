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
package com.ushahidi.swiftriver.core.model;

/**
 * Type used to indicate the source of a <code>com.ushahidi.swiftriver.core.model.Drop</code>
 * when populating the metadata properties i.e. links, places, tags, media
 * 
 * @author ekala
 */
public enum DropSource {
	/**
	 * The source of the drop being populated is a 
	 * <code>com.ushahdi.swiftriver.core.model.River</code>
	 */
	RIVER,

	/**
	 * The source of the drop being populated is a 
	 * <code>com.ushahdi.swiftriver.core.model.Bucket</code>
	 */	
	BUCKET
	
}
