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

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Model for channels 
 * @author ekala
 *
 */
@Entity
@Table(name = "channel_filters")
public class Channel implements Serializable {
	
	private static final long serialVersionUID = 3575711835255222818L;

	@Id
	@GeneratedValue
	private int id;
	
	@Column(name = "channel")
	private String channel;
	
	@ManyToOne
	private River river;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "filter_date_add")
	private Date dateAdded;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "filter_date_modified")
	private Date dateModified;
	
	@Column(name = "filter_enabled")
	private boolean filterEnabled;
	
	@Column(name = "filter_last_run")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastRun;	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "filter_last_successful_run")
	private Date lastSuccessfulRun;
	
	@Column(name = "filter_runs")
	private int filterRuns;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name="channel_filter_id")
	private Set<ChannelOption> channelOptions = new HashSet<ChannelOption>();

	public Channel() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public River getRiver() {
		return river;
	}

	public void setRiver(River river) {
		this.river = river;
	}

	public Date getFilterDateAdd() {
		return dateAdded;
	}

	public void setDateAdded(Date dateAdded) {
		this.dateAdded = dateAdded;
	}

	public Date getDateModified() {
		return dateModified;
	}

	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}

	public boolean isFilterEnabled() {
		return filterEnabled;
	}

	public void setFilterEnabled(boolean filterEnabled) {
		this.filterEnabled = filterEnabled;
	}

	public Date getFilterLastRun() {
		return lastRun;
	}

	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}

	public Date getLastSuccessfulRun() {
		return lastSuccessfulRun;
	}

	public void setLastSuccessfulRun(Date lastSuccessfulRun) {
		this.lastSuccessfulRun = lastSuccessfulRun;
	}

	public int getFilterRuns() {
		return filterRuns;
	}

	public void setFilterRuns(int filterRuns) {
		this.filterRuns = filterRuns;
	}

	public Set<ChannelOption> getChannelOptions() {
		return channelOptions;
	}

	public void setChannelOptions(Set<ChannelOption> channelOptions) {
		this.channelOptions = channelOptions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((channel == null) ? 0 : channel.hashCode());
		result = prime * result + ((river == null) ? 0 : river.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Channel other = (Channel) obj;
		if (channel == null) {
			if (other.channel != null)
				return false;
		} else if (!channel.equals(other.channel))
			return false;
		if (river == null) {
			if (other.river != null)
				return false;
		} else if (!river.equals(other.river))
			return false;
		return true;
	}
}
