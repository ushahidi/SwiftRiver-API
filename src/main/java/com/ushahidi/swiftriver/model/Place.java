/**
 * The contents of this file are subject to the Affero General
 * Public License (AGPL) Version 3; you may not use this file 
 * except in compliance with the License. You may obtain a copy
 * of the License at http://www.gnu.org/licenses/agpl.html
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * 
 * @author ekala
 *
 */
@Entity
@Table(name="places")
public class Place implements Serializable{
	
	private static final long serialVersionUID = -8003953814633031845L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name="hash", nullable = false)
	private String hash;
	
	@Column(name="place_name", nullable = false)
	private String placeName;
	
	@Column(name="place_name_canonical")
	private String placeNameCanonical;
	
	@Column(name="longitude")
	private double longitude;
	
	@Column(name="latitude")
	private double latitude;

	public Place() {
		
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
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

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}
