/**
 * The contents of this file are subject to the Affero General
 * Public License (AGPL) Version 3; you may not use this file 
 * except in compliance with the License. You may obtain a copy
 * of the License at http://www.gnu.org/licenses/agpl.html
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "river_channel_options")
public class ChannelOption  {

	@Id
	@GeneratedValue
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "river_channel_id")
	private Channel channel;
	
	@Column(name = "key", nullable = false)
	private String key;
	
	@Column(name = "value", nullable = false)
	private String value;
	
	public ChannelOption() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
