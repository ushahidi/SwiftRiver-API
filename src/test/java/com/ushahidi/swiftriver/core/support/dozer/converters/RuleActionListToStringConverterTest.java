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
package com.ushahidi.swiftriver.core.support.dozer.converters;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ushahidi.swiftriver.core.api.dto.CreateRuleDTO.RuleAction;
import com.ushahidi.swiftriver.core.support.dozer.converters.RuleActionListToStringConverter;

public class RuleActionListToStringConverterTest {

	@Test
	public void testConvertFrom() {
		RuleActionListToStringConverter converter = new RuleActionListToStringConverter();
		String source = "[{\"addToBucket\": 200, \"markAsRead\": false, \"removeFromRiver\": \"true\"}]";
		List<RuleAction> output = converter.convertFrom(source);
		assertNotNull(output);
		
		RuleAction ruleAction = output.get(0);
		assertEquals(200L, ruleAction.getAddToBucket());
		assertFalse(ruleAction.isMarkAsRead());
		assertTrue(ruleAction.isRemoveFromRiver());
	}
	
	@Test
	public void testConvertTo() {
		RuleActionListToStringConverter converter = new RuleActionListToStringConverter();
		RuleAction ruleAction = new RuleAction();
		ruleAction.setRemoveFromRiver(true);
		
		List<RuleAction> source = new ArrayList<RuleAction>();
		source.add(ruleAction);

		String output = converter.convertTo(source);
		String expected = "[{\"markAsRead\":false,\"removeFromRiver\":true,\"addToBucket\":0}]";
		
		assertEquals(expected, output);
	}
}
