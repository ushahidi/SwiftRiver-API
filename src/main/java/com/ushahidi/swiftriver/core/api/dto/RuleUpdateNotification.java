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
package com.ushahidi.swiftriver.core.api.dto;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.ushahidi.swiftriver.core.api.dto.CreateRuleDTO.RuleAction;
import com.ushahidi.swiftriver.core.api.dto.CreateRuleDTO.RuleCondition;

public class RuleUpdateNotification {

	private long id;

	@JsonProperty("river_id")
	private long riverId;
		
	private List<RuleCondition> conditions;
	
	private List<RuleAction> actions;
	
	@JsonProperty("all_conditions")
	private boolean matchAllConditions;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getRiverId() {
		return riverId;
	}

	public void setRiverId(long riverId) {
		this.riverId = riverId;
	}

	public List<RuleCondition> getConditions() {
		return conditions;
	}

	public void setConditions(List<RuleCondition> conditions) {
		this.conditions = conditions;
	}

	public List<RuleAction> getActions() {
		return actions;
	}

	public void setActions(List<RuleAction> actions) {
		this.actions = actions;
	}

	public boolean isMatchAllConditions() {
		return matchAllConditions;
	}

	public void setMatchAllConditions(boolean matchAllConditions) {
		this.matchAllConditions = matchAllConditions;
	}	
	
}
