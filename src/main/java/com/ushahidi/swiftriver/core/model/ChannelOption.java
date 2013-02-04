/**
 * The contents of this file are subject to the Affero General
 * Public License (AGPL) Version 3; you may not use this file 
 * except in compliance with the License. You may obtain a copy
 * of the License at http://www.gnu.org/licenses/agpl.html
 * 
 * Copyright (C) Ushahidi Inc. All Rights Reserved.
 */
package com.ushahidi.swiftriver.core.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * 
 * @author ekala
 *
 */
@Entity
@Table(name = "channel_filter_options")
public class ChannelOption implements Serializable {
	
	private static final long serialVersionUID = 2202984709505558045L;

	@Id
	@GeneratedValue
	private int id;
	
	@Column(name = "channel_filter_id")
	private int channelId;
	
	@Column(name = "key", nullable = false)
	private String key;
	
	@Column(name = "value", nullable = false)
	private String value;
	
	public ChannelOption() {
		
	}

	public int getChannel() {
		return channelId;
	}

	public void setChannel(int channel) {
		this.channelId = channel;
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
