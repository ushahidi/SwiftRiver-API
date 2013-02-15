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
package com.ushahidi.swiftriver.core.api.dao.impl;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.api.dao.PlaceDao;
import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.util.HashUtil;

public class JpaPlaceDaoTest extends AbstractDaoTest {

	@Autowired
	private PlaceDao placeDao;
	
	/**
	 * Test for {@link PlaceDao#save(com.ushahidi.swiftriver.core.model.Place)}
	 */
	@Test
	public void testSave() {
		Place place = new Place();
		String placeName = "Muthaiga";
		float longitude = 36.8333f;
		float latitude = -1.25f;
		
		String hash = HashUtil.md5(placeName + Float.toString(longitude) + Float.toString(latitude));

		place.setPlaceName(placeName);
		place.setPlaceNameCanonical(placeName.toLowerCase());
		place.setLatitude(latitude);
		place.setLongitude(longitude);
		place.setHash(hash);

		placeDao.save(place);
		
		assertTrue(place.getId() > 0);
	
	}
}
