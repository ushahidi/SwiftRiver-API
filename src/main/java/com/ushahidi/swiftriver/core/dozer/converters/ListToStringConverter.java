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
package com.ushahidi.swiftriver.core.dozer.converters;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.dozer.DozerConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom Dozer converter to handle the mapping to/form 
 * <code>java.util.List</code> and <code>java.lang.String</code> fields
 * 
 * @author ekala
 */
public class ListToStringConverter extends DozerConverter<List, String> {

	static final Logger LOGGER = LoggerFactory.getLogger(ListToStringConverter.class);

	public ListToStringConverter() {
		super(List.class, String.class);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List convertFrom(String source, List destination) {
		try {
			// Convert the string to JSON
			ObjectMapper om = new ObjectMapper();
			for (String entry: om.readValue(source, String[].class)) {
				destination.add(entry);
			}
		} catch (JsonParseException je) {
			LOGGER.error(je.getMessage());
		} catch (JsonMappingException me) {
			LOGGER.error(me.getMessage());
		} catch (IOException ie) {
			LOGGER.error(ie.getMessage());
		}

		return destination;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String convertTo(List source, String destination) {
		destination = ArrayUtils.toString(((List<String>) source).toArray());
		
		return destination;
	}

}
