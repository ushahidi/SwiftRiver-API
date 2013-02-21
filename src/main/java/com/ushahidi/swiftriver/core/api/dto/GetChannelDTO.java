package com.ushahidi.swiftriver.core.api.dto;

import org.codehaus.jackson.annotate.JsonProperty;

public class GetChannelDTO {

	private long id;
	
	private String channel;
	
	private boolean active;
	
	private String parameters;
	
	@JsonProperty("drop_count")
	private int dropCount;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getParameters() {
		return parameters;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
}
