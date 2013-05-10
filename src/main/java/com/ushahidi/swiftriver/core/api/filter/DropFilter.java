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

}
