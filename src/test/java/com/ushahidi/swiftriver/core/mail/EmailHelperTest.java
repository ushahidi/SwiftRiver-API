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
package com.ushahidi.swiftriver.core.mail;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailHelperTest {

	private EmailHelper emailHelper;
	private JavaMailSender mockMailSender;

	@Before
	public void setUp() {
		mockMailSender = mock(JavaMailSender.class);

		// Initialize the velocity engine
		VelocityEngine velocityEngine = new VelocityEngine();

		Properties props = new Properties();
		props.put("resource.loader", "class");
		props.put("class.resource.loader.class",
				"org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine.init(props);

		emailHelper = new EmailHelper();
		emailHelper.setMailSender(mockMailSender);
		emailHelper.setSenderAddress("no-reply@myswiftriver.com");
		emailHelper.setBaseLinkUrl("http://myswiftriver.com");
		emailHelper.setVelocityEngine(velocityEngine);
	}

	@Test
	public void getPasswordResetEmailBody_CrowdmapID() {
		String testName = "Test Name";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("token", "%token%");
		params.put("email", "me@myswiftriver.com");

		String emailBody = emailHelper.getEmailBody(EmailType.RESET_PASSWORD, params, testName);
		Pattern pattern = Pattern.compile("(token=%25token%25){1}");
		assertTrue(pattern.matcher(emailBody).find());
	}
}
