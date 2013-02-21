package com.ushahidi.swiftriver.core.api.dto;

import org.codehaus.jackson.annotate.JsonProperty;

public class ModifyRiverDTO {

	@JsonProperty("name")
	private String riverName;
	
	private String description;

	@JsonProperty("public")
	private Boolean riverPublic;

	public String getRiverName() {
		return riverName;
	}

	public void setRiverName(String riverName) {
		this.riverName = riverName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getRiverPublic() {
		return riverPublic;
	}

	public void setRiverPublic(Boolean riverPublic) {
		this.riverPublic = riverPublic;
	}
}
