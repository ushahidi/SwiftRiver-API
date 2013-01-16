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
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.*;

/**
 * 
 * @author ekala
 *
 */
@Entity
@Table(name = "channel_filters")
public class Channel implements Serializable {
	
	private static final long serialVersionUID = 3575711835255222818L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "channel")
	private String channel;
	
	@ManyToOne(cascade  = CascadeType.ALL)
	@JoinColumn(name = "river_id")
	private River river;

	@Column(name = "filter_date_add")
	private Timestamp filterDateAdd;
	
	@Column(name = "filter_date_modified")
	private Timestamp filterDateModified;
	
	@Column(name = "filter_enabled")
	private boolean filterEnabled;
	
	@Column(name = "filter_last_run")
	private Timestamp filterLastRun;	

	@Column(name = "filter_last_successful_run")
	private Timestamp filterLastSuccessfulRun;
	
	@Column(name = "filter_runs")
	private int filterRuns;

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
		return filterDateAdd;
	}

	public void setFilterDateAdd(Timestamp filterDateAdd) {
		this.filterDateAdd = filterDateAdd;
	}

	public Date getFilterDateModified() {
		return filterDateModified;
	}

	public void setFilterDateModified(Timestamp filterDateModified) {
		this.filterDateModified = filterDateModified;
	}

	public boolean isFilterEnabled() {
		return filterEnabled;
	}

	public void setFilterEnabled(boolean filterEnabled) {
		this.filterEnabled = filterEnabled;
	}

	public Date getFilterLastRun() {
		return filterLastRun;
	}

	public void setFilterLastRun(Timestamp filterLastRun) {
		this.filterLastRun = filterLastRun;
	}

	public Date getFilterLastSuccessfulRun() {
		return filterLastSuccessfulRun;
	}

	public void setFilterLastSuccessfulRun(Timestamp filterLastSuccessfulRun) {
		this.filterLastSuccessfulRun = filterLastSuccessfulRun;
	}

	public int getFilterRuns() {
		return filterRuns;
	}

	public void setFilterRuns(int filterRuns) {
		this.filterRuns = filterRuns;
	}

}
