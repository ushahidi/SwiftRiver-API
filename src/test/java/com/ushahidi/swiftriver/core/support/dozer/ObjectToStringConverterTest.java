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
package com.ushahidi.swiftriver.core.support.dozer;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.ushahidi.swiftriver.core.support.dozer.ObjectToStringConverter;

/**
 * @author Ushahidi, Inc
 *
 */
public class ObjectToStringConverterTest {
	
	ObjectToStringConverter converter;
	
	@Before
	public void setup() {
		converter = new ObjectToStringConverter();
		converter.setObjectMapper(new ObjectMapper());
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void convertFromList() {
		
		List dest = null;
		dest = (List) converter.convertFrom("[\"testing\"]", dest);
		
		assertEquals(1, dest.size());
		assertEquals("testing", dest.get(0));
	}
	
	@Test
	public void convertFromString() {
		
		String dest = null;
		dest = (String) converter.convertFrom("\"testing\"", dest);
		
		assertEquals("testing", dest);
	}
	
	@Test
	public void convertTo() {
		
		String dest = null;
		List<String> source = new ArrayList<String>();
		source.add("test string");
		dest = converter.convertTo(source, dest);
		
		assertEquals("[\"test string\"]", dest);
	}
}
