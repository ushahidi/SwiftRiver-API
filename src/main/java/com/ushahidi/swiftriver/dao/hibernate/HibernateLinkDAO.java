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
package com.ushahidi.swiftriver.dao.hibernate;

import com.ushahidi.swiftriver.dao.LinkDAO;
import com.ushahidi.swiftriver.model.Link;

public class HibernateLinkDAO extends AbstractHibernateDAO<Link, Long> implements LinkDAO {

	public HibernateLinkDAO() {
		super(Link.class);
	}

	/**
	 * @see LinkDAO#getByHash(String)
	 */
	public Link getByHash(String hash) {
		return (Link) hibernateTemplate.find("from Link where hash = ?", hash).get(0);
	}

}
