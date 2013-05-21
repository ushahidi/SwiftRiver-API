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

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.http.client.utils.URIBuilder;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.ushahidi.swiftriver.core.model.User;
import com.ushahidi.swiftriver.core.model.UserToken;

public class EmailHelper {

	private JavaMailSender mailSender;
	
	private VelocityEngine velocityEngine;

	/** Email address of the sender */
	private String senderAddress;
	
	/** Base URL for creating links within the mail message */
	private String baseLinkUrl;

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}
	
	public void setSenderAddress(String senderAddress) {
		this.senderAddress = senderAddress;
	}
	
	public void setBaseLinkUrl(String baseLinkUrl) {
		this.baseLinkUrl = baseLinkUrl;
	}
	

	/**
	 * Sends a message requesting the specified <code>user</code>
	 * with instructions on how to activate their newly created
	 * user account
	 * 
	 * @param user
	 * @param userToken
	 */
	public void sendAccountActivationEmail(final User user, UserToken userToken) {
		// Get the mail body
		Map<String, Object> templateParams = new HashMap<String, Object>();
		templateParams.put("email", user.getEmail());
		templateParams.put("token", userToken.getToken());

		final String mailBody = getEmailBody(EmailType.ACTIVATE_ACCOUNT, templateParams, user.getName());
		baseLinkUrl += "/activate";

		// Prepare the MIME message
		final String subject = "Welcome to SwiftRiver";
		MimeMessagePreparator preparator = getMimeMessagePreparator(user, subject, mailBody);

		// Send the account activation email
		mailSender.send(preparator);
	}
	
	/**
	 * Sends a message to the specified <code>user</code> with
	 * instructions on how to reset their password
	 * 
	 * @param user
	 * @param userToken
	 */
	public void sendPasswordResetEmail(final User user, UserToken userToken) {
		// Get the mail body with all the properties set
		Map<String, Object> templateParams = new HashMap<String, Object>();
		templateParams.put("email", user.getEmail());
		templateParams.put("token", userToken.getToken());

		String mailBody = getEmailBody(EmailType.RESET_PASSWORD, templateParams, user.getName());
		baseLinkUrl += "/reset_password";

		// Prepare the MIME message
		final String subject = "How to reset your SwiftRiver password"; 
		MimeMessagePreparator preparator = getMimeMessagePreparator(user, subject, mailBody);

		// Send the message
		mailSender.send(preparator);
	}
	
	/**
	 * Returns the mail body for the specified <code>emailType</code>
	 * with all the properties in <code>templateParams</code> having been
	 * set
	 *  
	 * @param emailType
	 * @param templateParams
	 * @param name
	 * @return
	 */
	public String getEmailBody(EmailType emailType, Map<String, Object> templateParams, String name) {
		Map<String, Object> body = new HashMap<String, Object>();
		try {
			URIBuilder uriBuilder = new URIBuilder(baseLinkUrl);
			for (Map.Entry<String, Object> entry: templateParams.entrySet()) {
				uriBuilder.addParameter(entry.getKey(), (String) entry.getValue());
			}
	
			body.put("name", name);
			body.put("url", uriBuilder.toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		String templateLocation = EmailType.getTemplateLocation(emailType);
		
		return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, 
				templateLocation, "UTF-8", body);
	}

	private MimeMessagePreparator getMimeMessagePreparator(final User user,
			final String subject, final String mailBody) {
		MimeMessagePreparator preparator = new MimeMessagePreparator() {
			
			public void prepare(MimeMessage mimeMessage) throws Exception {
				MimeMessageHelper mimeHelper = new MimeMessageHelper(mimeMessage, true);
				mimeHelper.setFrom(senderAddress);
				mimeHelper.setTo(user.getEmail());
				mimeHelper.setReplyTo(senderAddress);
				mimeHelper.setSubject(subject);
				mimeHelper.setText(mailBody, true);
			}
		};
		
		return preparator;
	}
}
