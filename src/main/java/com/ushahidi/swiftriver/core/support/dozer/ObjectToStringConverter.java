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

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.dozer.DozerConverter;

/**
 * @author Ushahidi, Inc
 *
 */
public class ObjectToStringConverter extends DozerConverter<Object, String> {
	
	ObjectMapper objectMapper;

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	public ObjectToStringConverter() {
		super(Object.class, String.class);
	}

	public ObjectToStringConverter(Class<Object> prototypeA,
			Class<String> prototypeB) {
		super(prototypeA, prototypeB);
	}

	@Override
	public Object convertFrom(String source, Object dest) {
		try {
			if (source != null) {
				dest = objectMapper.readValue(source, Object.class);
			}
		} catch (JsonParseException e) {
			throw new IllegalStateException(e);
		} catch (JsonMappingException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return dest;
	}

	@Override
	public String convertTo(Object source, String dest) {
		try {
			if (source != null) {
				dest = objectMapper.writeValueAsString(source);
			}
		} catch (JsonGenerationException e) {
			throw new IllegalStateException(e);
		} catch (JsonMappingException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		return dest;
	}

}
