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

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import com.ushahidi.swiftriver.core.support.dozer.ListToStringConverter;

/**
 * @author Ushahidi, Inc
 *
 */
public class ListToStringConverterTest {

	ListToStringConverter converter;
	
	@Before
	public void setup() {
		converter = new ListToStringConverter();
		converter.setObjectMapper(new ObjectMapper());
	}
	
	@Test
	public void convertFrom() {
		
		List dest = null;
		dest = converter.convertFrom("[\"testing\"]", dest);
		
		assertEquals(1, dest.size());
		assertEquals("testing", dest.get(0));
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
