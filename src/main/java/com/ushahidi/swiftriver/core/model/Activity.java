/**
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ushahidi.swiftriver.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Ushahidi, Inc
 *
 */
@Entity
@Inheritance
@DiscriminatorColumn(name="action_on")
@Table(name = "account_actions")
public abstract class Activity {
	
	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "account_id")
	private Account account;
	
	private String action;
	
	@Column(name = "action_on", insertable = false, updatable = false)
	private String actionOn;
	
	@ManyToOne
	@JoinColumn(name = "action_to_id")
	private Account actionTo;
	
	@Column(name = "action_date_add")
	private Date actionDateAdd;
	
	private Boolean confirmed;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
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

	public Account getActionTo() {
		return actionTo;
	}

	public void setActionTo(Account actionTo) {
		this.actionTo = actionTo;
	}

	public Date getActionDateAdd() {
		return actionDateAdd;
	}

	public void setActionDateAdd(Date actionDateAdd) {
		this.actionDateAdd = actionDateAdd;
	}

	public Boolean getConfirmed() {
		return confirmed;
	}

	public void setConfirmed(Boolean confirmed) {
		this.confirmed = confirmed;
	}

}
