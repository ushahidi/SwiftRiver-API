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
package com.ushahidi.swiftriver.core.api.dto;

import java.util.List;

/**
 * @author Ushahidi, Inc
 * 
 */
public class ModifyFormValueDTO {

	private List<FormFieldValue> values;

	public List<FormFieldValue> getValues() {
		return values;
	}

	public void setValues(List<FormFieldValue> values) {
		this.values = values;
	}

	public static class FormFieldValue {

		private String id;

		private Object value;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "FormFieldValue [id=" + id + ", value=" + value + "]";
		}
	}

	@Override
	public String toString() {
		return "ModifyFormValueDTO [values=" + values + "]";
	}
}
