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

public class CreateRuleDTO {

	private String name;
	
	private Integer type;
	
	private List<RuleCondition> conditions;
	
	private List<RuleAction> actions;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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
	
	public static class RuleCondition {
		private String field;
		
		private String operator;
		
		private String value;

		public String getField() {
			return field;
		}

		public void setField(String field) {
			this.field = field;
		}

		public String getOperator() {
			return operator;
		}

		public void setOperator(String operator) {
			this.operator = operator;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
		
	}
	
	public static class RuleAction {
		
		private boolean markAsRead;
		
		private boolean removeFromRiver;
		
		private long addToBucket;

		public boolean isMarkAsRead() {
			return markAsRead;
		}

		public void setMarkAsRead(boolean markAsRead) {
			this.markAsRead = markAsRead;
		}

		public boolean isRemoveFromRiver() {
			return removeFromRiver;
		}

		public void setRemoveFromRiver(boolean removeFromRiver) {
			this.removeFromRiver = removeFromRiver;
		}

		public long getAddToBucket() {
			return addToBucket;
		}

		public void setAddToBucket(long addToBucket) {
			this.addToBucket = addToBucket;
		}
		
		
	}

}
