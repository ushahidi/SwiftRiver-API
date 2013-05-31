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
package com.ushahidi.swiftriver.core.api.filter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ushahidi.swiftriver.core.api.exception.InvalidFilterException;

/**
 * This is a wrapper class for the parameters used
 * for querying drops  
 * 
 * @author ekala
 *
 */
public class DropFilter {

	private List<String> channels;

	private Boolean read;

	private Boolean photos;

	private Date dateFrom;

	private Date dateTo;
	
	private Long sinceId;
	
	private Long maxId;
	
	private List<Long> channelIds;
	
	private List<Long> dropIds;

	private String keywords;

	private BoundingBox boundingBox;

	/**
	 * @return the channels
	 */
	public List<String> getChannels() {
		return channels;
	}

	/**
	 * @param channels
	 *            the channels to set
	 */
	public void setChannels(List<String> channels) {
		this.channels = channels;
	}

	/**
	 * @return the read
	 */
	public Boolean getRead() {
		return read;
	}

	/**
	 * @param read
	 *            the read to set
	 */
	public void setRead(Boolean read) {
		this.read = read;
	}

	/**
	 * @return the photos
	 */
	public Boolean getPhotos() {
		return photos;
	}

	/**
	 * @param photos
	 *            the photos to set
	 */
	public void setPhotos(Boolean photos) {
		this.photos = photos;
	}

	/**
	 * @return the dateFrom
	 */
	public Date getDateFrom() {
		return dateFrom;
	}

	/**
	 * Sets the minimum date to be used for fetching data.
	 *  
	 * If {@link DropFilter#dateFrom} > {@link DropFilter#dateTo},  
	 * a {@link InvalidFilterException} is thrown with a message that
	 * the dateFrom cannot be later than dateTo
	 * 
	 * @param dateFrom
	 * @throws InvalidFilterException 
	 */
	public void setDateFrom(Date dateFrom) throws InvalidFilterException {
		if (dateFrom == null)
			return;

		this.dateFrom = dateFrom;
		if (dateTo != null && dateFrom.after(dateTo)) {
			throw new InvalidFilterException(String.format(
					"dateFrom %s cannot be later than dateTo %s", 
					dateFrom.toString(), dateTo.toString()));
		}
		verifyDates();
	}

	/**
	 * @return Date
	 */
	public Date getDateTo() {
		return dateTo;
	}

	/**
	 * Sets the maximum date to be used for fetching data.
	 * 
	 * If {@link DropFilter#dateTo} < {@link DropFilter#dateFrom},
	 * a {@link InvalidFilterException} with a message that dateTo cannot
	 * be earlier than dateFrom 
	 *  
	 * @param dateTo
	 * @throws InvalidFilterException 
	 */
	public void setDateTo(Date dateTo) throws InvalidFilterException {
		if (dateTo == null)
			return;
		this.dateTo = dateTo;
		if (dateFrom != null && dateTo.before(dateFrom)) {
			throw new InvalidFilterException(String.format(
					"dateTo %s cannot be earlier than dateFrom %s", 
					dateTo.toString(), dateFrom.toString()));
		}
		
		verifyDates();
		
	}

	public Long getSinceId() {
		return sinceId;
	}

	public void setSinceId(Long sinceId) {
		this.sinceId = sinceId;
	}

	public Long getMaxId() {
		return maxId;
	}

	public void setMaxId(Long maxId) {
		this.maxId = maxId;
	}

	public List<Long> getChannelIds() {
		return channelIds;
	}

	public void setChannelIds(List<Long> channelIds) {
		this.channelIds = channelIds;
	}

	public List<Long> getDropIds() {
		return dropIds;
	}

	public void setDropIds(List<Long> dropIds) {
		this.dropIds = dropIds;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

	/**
	 * Creates a {@link BoundingBox} from the latitude,longitude
	 * pairs specified in the <code>locations</code> parameter.
	 * 
	 * The SouthWest corner of the bounding box should always
	 * come first
	 * 
	 * @param boundingBoxStr
	 * @throws InvalidFilterException
	 */
	public void setBoundingBox(String boundingBoxStr) throws InvalidFilterException {
		if (boundingBoxStr == null)
			return;

		// Validate the location bounds
		String[] bounds = boundingBoxStr.split(",");
		if (bounds.length != 4) {
			throw new InvalidFilterException(String.format(
					"Invalid bounding box '[%s]'", boundingBoxStr));
		}
		
		// Get the bounding box values
		float latFrom = Float.parseFloat(bounds[0]);
		float lngFrom = Float.parseFloat(bounds[1]);
		float latTo = Float.parseFloat(bounds[2]);
		float lngTo = Float.parseFloat(bounds[3]);
		
		// Validate each value
		if (!isValidLatitude(latFrom)) {
			throw new InvalidFilterException(String.format(
					"Invalid latitude in bounding box: %f", latFrom));
		}

		if (!isValidLongitude(lngFrom)) {
			throw new InvalidFilterException(String.format(
					"Invalid longitude in bounding box %f", lngFrom));
		}
		
		if (!isValidLatitude(latTo)) {
			throw new InvalidFilterException(String.format(
					"Invalid latitude in bounding box: %f", latTo));
		}
		
		if (!isValidLongitude(lngTo)) {
			throw new InvalidFilterException(String.format(
					"Invalid longitude in bounding box %f", lngTo));
		}
		
		// Verify that the SouthWest corner is the first pair
		// in the bounding box
		if ((latFrom > latTo) || (lngFrom > lngTo) ) {
			throw new InvalidFilterException(String.format(
					"Invalid bounding box: '%s'. The SouthWest corner should be specified first",
					boundingBoxStr));
		}
		
		// Create and set the bounding box
		this.boundingBox = new BoundingBox(latFrom, lngFrom, latTo, lngTo);
	}

	/**
	 * Validates the specified <code>latitude</code> value
	 * 
	 * @param latitude
	 * @return
	 */
	private boolean isValidLatitude(float latitude) {
		return latitude <= 90 && latitude >= -90;
	}

	/**
	 * Validates the specified <code>longitude</code> value
	 * 
	 * @param longitude
	 * @return
	 */
	private boolean isValidLongitude(float longitude) {
		return longitude <= 180 && longitude >= -180;
	}

	/**
	 * Checks if dateFrom = dateTo. If they are equal,
	 * dateTo = dateFrom + 24 hrs
	 */
	private void verifyDates() {
		if (dateFrom != null && dateTo != null && dateFrom.equals(dateTo)) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dateTo);
			calendar.add(Calendar.HOUR, 24);
	
			this.dateTo = calendar.getTime(); 
		}
	}

	/**
	 * This is a class for representing a rectangular geographical area
	 * (bounding box) to be used in performing spatial queries via
	 * Solr
	 * 
	 * @author ekala
	 */
	public static class BoundingBox {
		
		private Float latFrom;

		private Float lngFrom;

		private Float latTo;

		private Float lngTo;

		/**
		 * Initializes the bounding box
		 * 
		 * @param latFrom
		 * @param lngFrom
		 * @param latTo
		 * @param lngTo
		 */
		public BoundingBox(Float latFrom, Float lngFrom, Float latTo, Float lngTo) {
			setLatFrom(latFrom);
			setLngFrom(lngFrom);
			setLatTo(latTo);
			setLngTo(lngTo);
		}

		/**
		 * Returns the latitude for the SouthWest corner of the bounding box
		 * 
		 * @return
		 */
		public Float getLatFrom() {
			return latFrom;
		}

		/**
		 * Sets the latitude of the SouthWest corner of the bounding box
		 * @param latFrom
		 */
		public void setLatFrom(Float latFrom) {
			this.latFrom = latFrom;
		}

		/**
		 * Returns the longitude of the SouthWest corner of the bounding box
		 * 
		 * @return
		 */
		public Float getLngFrom() {
			return lngFrom;
		}

		/**
		 * Sets the longitude for the SouthWest corner of the bounding box
		 * 
		 * @param lngFrom
		 */
		public void setLngFrom(Float lngFrom) {
			this.lngFrom = lngFrom;
		}

		/**
		 * Returns the latitude of the NorthEast corner of the bounding box
		 * 
		 * @return
		 */
		public Float getLatTo() {
			return latTo;
		}

		/**
		 * Sets the latitude for the NorthEast corner of the bounding box
		 * 
		 * @param latTo
		 */
		public void setLatTo(Float latTo) {
			this.latTo = latTo;
		}

		/**
		 * Gets the longitude of the NorthEast corner of the bounding box
		 * 
		 * @return
		 */
		public Float getLngTo() {
			return lngTo;
		}

		/**
		 * Sets the longigude of the NorthEast corner of the bounding box
		 * 
		 * @param lngTo
		 */
		public void setLngTo(Float lngTo) {
			this.lngTo = lngTo;
		}
		
		
	}
}
