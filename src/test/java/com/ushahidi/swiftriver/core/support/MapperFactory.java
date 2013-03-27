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
package com.ushahidi.swiftriver.core.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.dozer.CustomConverter;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import com.ushahidi.swiftriver.core.support.dozer.ObjectToStringConverter;

/**
 * @author Ushahidi, Inc
 *
 */
public class MapperFactory {

	/**
	 * Construct a Mapper instance for testing
	 * 
	 * @return
	 */
	public static Mapper getMapper() {
		ObjectToStringConverter converter = new ObjectToStringConverter();
		converter.setObjectMapper(new ObjectMapper());
		
		DozerBeanMapper mapper = new DozerBeanMapper(
				Arrays.asList("config/dozer-bean-mappings.xml"));
		List<CustomConverter> converters = new ArrayList<CustomConverter>();
		converters.add(converter);
		mapper.setCustomConverters(converters);
		
		return mapper;
	}
	
}
