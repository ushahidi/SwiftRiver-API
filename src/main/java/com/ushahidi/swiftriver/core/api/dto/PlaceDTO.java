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
package com.ushahidi.swiftriver.core.api.dto;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import com.ushahidi.swiftriver.core.model.Place;
import com.ushahidi.swiftriver.core.utils.SwiftRiverUtils;

/**
 * DTO class for the Place model
 * @author ekala
 *
 */
public class PlaceDTO extends AbstractDTO<Place> {

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> createMapFromEntity(Place entity) {
		Object[][] placeData = {
				{"id", entity.getId()},
				{"name", entity.getName()}, 
				{"coordinates", new Double[]{entity.getLongitude(), entity.getLatitude()} }
		};

		return ArrayUtils.toMap(placeData);
	}

	@Override
	public Place createEntityFromMap(Map<String, Object> map) {
		String placeName = (String) map.get("name");

		Place place = new Place();
		place.setName(placeName);
		place.setName(place.getName().toLowerCase());
		
		Double[] coordinates = (Double[]) map.get("coordinates");
		place.setLongitude(coordinates[0]);
		place.setLatitude(coordinates[1]);
		
		// Generate the place hash
		String placeHash = SwiftRiverUtils.getMD5Hash(placeName, coordinates[0], coordinates[1]);
		place.setHash(placeHash);

		return place;
	}

	@Override
	protected String[] getValidationKeys() {
		// TODO Auto-generated method stub
		return new String[]{};
	}

	@Override
	protected void copyFromMap(Place target, Map<String, Object> source) {
		// TODO Auto-generated method stub
		
	}

}
