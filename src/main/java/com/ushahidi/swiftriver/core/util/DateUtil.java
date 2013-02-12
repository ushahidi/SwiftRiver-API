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
package com.ushahidi.swiftriver.core.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtil {
	
	final static Logger logger = LoggerFactory.getLogger(DateUtil.class);

	/**
	 * Format given date as an RFC822 formatted date/time String.
	 * 
	 * Uses current time if the given date is null.
	 * 
	 * @param date
	 * @return
	 */
	public static String formatRFC822(Date date, String timezone) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
		
		if (timezone != null) {
			dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
		}
		
		String dateString = null;
		
		if (date != null) {
			dateString = dateFormat.format(date);
		} 
		
		return dateString;
	}

}
