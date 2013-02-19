package com.ushahidi.swiftriver.core.api.dto;

public class ModifyChannelDTO {
	
	private String channel;
	
	private boolean active;
	
	private String parameters;

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
