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
package com.ushahidi.swiftriver.core.api.auth;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CustomBCryptPasswordEncoder extends BCryptPasswordEncoder {

	/*
	 * Override to return IllegaArgumentExceptions as a false match instead.
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder#matches
	 * (java.lang.CharSequence, java.lang.String)
	 */
	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {

		boolean ret = false;
		try {
			ret = super.matches(rawPassword, encodedPassword);
		} catch (IllegalArgumentException ex) {
			// Do nothing
		}
		return ret;
	}

}
