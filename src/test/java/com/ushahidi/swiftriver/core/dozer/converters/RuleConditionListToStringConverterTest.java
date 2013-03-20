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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.ushahidi.swiftriver.core.api.dto.CreateRuleDTO.RuleCondition;

public class RuleConditionListToStringConverterTest {
	
	@Test
	public void testConvertFrom() {
		RuleConditionListToStringConverter converter = new RuleConditionListToStringConverter();

		String source = "[{\"field\":\"content\",\"operator\":\"contains\",\"value\":\"IEBC\"}]";
		List<RuleCondition> destination = converter.convertFrom(source);
		assertNotNull(destination);
		
		RuleCondition condition = destination.get(0);
		assertEquals("content", condition.getField());
		assertEquals("contains", condition.getOperator());
		assertEquals("IEBC", condition.getValue());
	}
	
	@Test
	public void testConvertTo() {
		RuleCondition ruleCondition = new RuleCondition();
		ruleCondition.setField("content");
		ruleCondition.setOperator("contains");
		ruleCondition.setValue("Tana River");
		
		List<RuleCondition> source = new ArrayList<RuleCondition>();
		source.add(ruleCondition);
		
		RuleConditionListToStringConverter converter = new RuleConditionListToStringConverter();

		String expected = "[{\"field\":\"content\",\"operator\":\"contains\",\"value\":\"Tana River\"}]";
		String destination = converter.convertTo(source);
		assertEquals(expected, destination);
	}
}
