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
package com.ushahidi.swiftriver.core.util;

public class GravatarUtil {

	/**
	 * Return the gravatar url for the given email address.
	 * 
	 * @param email
	 * @return
	 */
	public static String gravatar(String email) {
		String url = "https://secure.gravatar.com/avatar/%s?s=80&d=mm&r=g";
		String md5Hex = MD5Util.md5Hex(email.trim().toLowerCase());
		return String.format(url, md5Hex);
	}
}
