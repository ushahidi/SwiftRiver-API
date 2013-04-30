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

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * @author Ushahidi, Inc
 *
 */
@Entity
@DiscriminatorValue("bucket")
public class BucketActivity extends Activity {

	@ManyToOne
	@JoinColumn(name = "action_on_id")
	private Bucket actionOnObj;

	public Bucket getActionOnObj() {
		return actionOnObj;
	}

	public void setActionOnObj(Bucket actionOnObj) {
		this.actionOnObj = actionOnObj;
	}
}
