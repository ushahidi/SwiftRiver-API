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

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * DTO class for activities
 * 
 * @author Ushahidi, Inc
 *
 */
public class GetActivityDTO {

	private String id;
	
	@JsonProperty("date_added")
	private String actionDateAdd;
	
	private AccountDTO account;
	
	private String action;
	
	@JsonProperty("action_on")
	private String actionOn;
	
	@JsonProperty("action_on_obj")
	private Object actionOnObj;
	
	private AccountDTO actionTo;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getActionDateAdd() {
		return actionDateAdd;
	}

	public void setActionDateAdd(String actionDateAdd) {
		this.actionDateAdd = actionDateAdd;
	}

	public AccountDTO getAccount() {
		return account;
	}

	public void setAccount(AccountDTO account) {
		this.account = account;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActionOn() {
		return actionOn;
	}

	public void setActionOn(String actionOn) {
		this.actionOn = actionOn;
	}

	public Object getActionOnObj() {
		return actionOnObj;
	}

	public void setActionOnObj(Object actionOnObj) {
		this.actionOnObj = actionOnObj;
	}

	public AccountDTO getActionTo() {
		return actionTo;
	}

	public void setActionTo(AccountDTO actionTo) {
		this.actionTo = actionTo;
	}
}
