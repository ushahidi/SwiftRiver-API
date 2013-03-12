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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.ushahidi.swiftriver.core.api.dao.UserDao;
import com.ushahidi.swiftriver.core.model.Role;
import com.ushahidi.swiftriver.core.model.User;

@Transactional(readOnly = true)
public class DbUserDetailsService implements UserDetailsService {

	@Autowired
	UserDao userDao;

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {

		User dbUser = userDao.findByUsername(username);

		if (dbUser == null)
			throw new UsernameNotFoundException("User not found");

		List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();

		for (Role role : dbUser.getRoles()) {
			authorities.add(new SimpleGrantedAuthority("ROLE_"
					+ role.getName().toUpperCase()));
		}

		return new org.springframework.security.core.userdetails.User(
				dbUser.getUsername(), dbUser.getPassword(), dbUser.getActive(),
				!dbUser.getExpired(), !dbUser.getExpired(),
				!dbUser.getLocked(), authorities);
	}

}
