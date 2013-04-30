/**
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ushahidi.swiftriver.core.model;

/**
 * Defines the set of possible actions an account can
 * perform on a SwiftRiver object.
 * 
 * @author Ushahidi, Inc
 *
 */
public enum ActivityType {

	/**
	 * Account created an object.
	 */
	CREATE,
	
	/**
	 * Account subscribed to an object.
	 */
	FOLLOW,
	
	/**
	 * Account invited another account to collaborate
	 */
	INVITE,
	
	/**
	 * Account made a comment on an object
	 */
	COMMENT
}
