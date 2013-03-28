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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * @author Ushahidi, Inc
 *
 */
@Entity
@Table(name="river_droplet_form")
public class RiverDropForm {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="river_droplets_id")
	private RiverDrop riverDrop;

	@ManyToOne
	private Form form;
	
	@OneToMany(cascade=CascadeType.REMOVE, fetch=FetchType.EAGER, mappedBy="dropForm")
	private List<RiverDropFormField> values;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RiverDrop getRiverDrop() {
		return riverDrop;
	}

	public void setRiverDrop(RiverDrop riverDrop) {
		this.riverDrop = riverDrop;
	}

	public Form getForm() {
		return form;
	}

	public void setForm(Form form) {
		this.form = form;
	}

	public List<RiverDropFormField> getValues() {
		return values;
	}

	public void setValues(List<RiverDropFormField> values) {
		this.values = values;
	}

}