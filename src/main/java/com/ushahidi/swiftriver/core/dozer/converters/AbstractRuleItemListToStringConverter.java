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

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.dozer.DozerConverter;

public abstract class AbstractRuleItemListToStringConverter extends
		DozerConverter<List, String> {

	protected ObjectMapper mapper = new ObjectMapper();

	public AbstractRuleItemListToStringConverter() {
		super(List.class, String.class);
	}
	
	@Override
	public String convertTo(List source, String destination) {
		String json = null;
		try {
			json = mapper.writeValueAsString(source);
			destination = json;
		} catch (JsonParseException jp) {
			
		} catch (JsonMappingException jm) {
			
		} catch (IOException io) {
			
		}
		return json;
	}
	

}
