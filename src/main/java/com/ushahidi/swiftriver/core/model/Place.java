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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Table(name = "places")
public class Place {

	@Id
    @GeneratedValue(strategy=GenerationType.TABLE, generator="Seq") 
    @TableGenerator(name="Seq", table="seq", 
        pkColumnName="name", valueColumnName="id", 
        pkColumnValue="places") 
	private long id;

	@Column(name = "hash", nullable = false)
	private String hash;

	@Column(name = "place_name", nullable = false)
	private String placeName;

	@Column(name = "place_name_canonical")
	private String placeNameCanonical;

	@Column(name = "longitude")
	private Float longitude;

	@Column(name = "latitude")
	private Float latitude;

	public Place() {

	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getPlaceNameCanonical() {
		return placeNameCanonical;
	}

	public void setPlaceNameCanonical(String placeNameCanonical) {
		this.placeNameCanonical = placeNameCanonical;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public Float getLatitude() {
		return latitude;
	}

	public void setLatitude(Float latitude) {
		this.latitude = latitude;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 31).append(placeName).append(longitude)
				.append(latitude).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		Place other = (Place) obj;
		return new EqualsBuilder().append(placeName, other.placeName)
				.append(longitude, other.longitude)
				.append(latitude, other.latitude).isEquals();
	}

}
