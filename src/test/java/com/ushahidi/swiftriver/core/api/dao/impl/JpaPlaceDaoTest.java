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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ushahidi.swiftriver.core.api.dao.AbstractDaoTest;
import com.ushahidi.swiftriver.core.api.dao.PlaceDao;
import com.ushahidi.swiftriver.core.model.Drop;
import com.ushahidi.swiftriver.core.model.Place;

public class JpaPlaceDaoTest extends AbstractDaoTest {

	@Autowired
	private PlaceDao placeDao;

	/**
	 * Test for
	 * {@link PlaceDao#create(com.ushahidi.swiftriver.core.model.Place)}
	 */
	@Test
	public void testCreate() {
		Place place = new Place();

		place.setPlaceName("Muthaiga");
		place.setLatitude(-1.25f);
		place.setLongitude(36.8333f);

		placeDao.create(place);

		assertTrue(place.getId() > 0);
		assertEquals("muthaiga", place.getPlaceNameCanonical());
	}

	@Test
	public void getPlaces() {
		List<Place> places = new ArrayList<Place>();
		Place existingPlace = new Place();
		existingPlace.setPlaceName("Freetown ");
		existingPlace.setLongitude(-13.2299f);
		existingPlace.setLatitude(8.484f);
		places.add(existingPlace);
		Place newPlace = new Place();
		newPlace.setPlaceName(" Neverland ");
		newPlace.setLongitude(-35.2033f);
		newPlace.setLatitude(31.9216f);
		places.add(newPlace);

		List<Drop> drops = new ArrayList<Drop>();
		Drop drop = new Drop();
		drop.setId(5);
		drop.setPlaces(places);
		drops.add(drop);

		placeDao.getPlaces(drops);

		assertEquals(4, existingPlace.getId());

		String sql = "SELECT hash, place_name, place_name_canonical, longitude, latitude "
				+ "FROM places WHERE id = ?";

		Map<String, Object> results = this.jdbcTemplate.queryForMap(sql,
				newPlace.getId());

		assertEquals("51d4f9db1572cc747a7cef338781ea6a", results.get("hash"));
		assertEquals("Neverland", results.get("place_name"));
		assertEquals("neverland", results.get("place_name_canonical"));
		assertEquals(-35.2033f, ((Number)results.get("longitude")).floatValue(), 0.0002);
		assertEquals(31.9216f, ((Number)results.get("latitude")).floatValue(), 0.0002);

		sql = "SELECT place_id FROM droplets_places WHERE droplet_id = 5";

		List<Long> dropletPlaces = this.jdbcTemplate.queryForList(sql,
				Long.class);
		assertEquals(3, dropletPlaces.size());
		assertTrue(dropletPlaces.contains(newPlace.getId()));
	}
	
	/**
	 * A drop with a null places entry should not cause an exception
	 */
	@Test
	public void getPlacesWithDropInListMissingPlaces() {
		List<Place> places = new ArrayList<Place>();
		Place existingPlace = new Place();
		existingPlace.setPlaceName("Freetown ");
		existingPlace.setLongitude(-13.2299f);
		existingPlace.setLatitude(8.484f);
		places.add(existingPlace);
		Place newPlace = new Place();
		newPlace.setPlaceName(" Neverland ");
		newPlace.setLongitude(-35.2033f);
		newPlace.setLatitude(31.9216f);
		places.add(newPlace);

		List<Drop> drops = new ArrayList<Drop>();
		Drop dropWithoutPlaces = new Drop();
		dropWithoutPlaces.setId(1);
		drops.add(dropWithoutPlaces);
		Drop drop = new Drop();
		drop.setId(5);
		drop.setPlaces(places);
		drops.add(drop);

		placeDao.getPlaces(drops);
	}
}
