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

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

/**
 * Mock AmqpTemplate for Unit Testing
 *
 */
public class MockAmqpTemplate implements AmqpTemplate {

	@Override
	public void send(Message message) throws AmqpException {
		// Do nothing
		
	}

	@Override
	public void send(String routingKey, Message message) throws AmqpException {
		// Do nothing
		
	}

	@Override
	public void send(String exchange, String routingKey, Message message)
			throws AmqpException {
		// Do nothing
		
	}

	@Override
	public void convertAndSend(Object message) throws AmqpException {
		// Do nothing
		
	}

	@Override
	public void convertAndSend(String routingKey, Object message)
			throws AmqpException {
		// Do nothing
		
	}

	@Override
	public void convertAndSend(String exchange, String routingKey,
			Object message) throws AmqpException {
		// Do nothing
		
	}

	@Override
	public void convertAndSend(Object message,
			MessagePostProcessor messagePostProcessor) throws AmqpException {
		// Do nothing
		
	}

	@Override
	public void convertAndSend(String routingKey, Object message,
			MessagePostProcessor messagePostProcessor) throws AmqpException {
		// Do nothing
		
	}

	@Override
	public void convertAndSend(String exchange, String routingKey,
			Object message, MessagePostProcessor messagePostProcessor)
			throws AmqpException {
		// Do nothing
		
	}

	@Override
	public Message receive() throws AmqpException {
		// Do nothing
		return null;
	}

	@Override
	public Message receive(String queueName) throws AmqpException {
		// Do nothing
		return null;
	}

	@Override
	public Object receiveAndConvert() throws AmqpException {
		// Do nothing
		return null;
	}

	@Override
	public Object receiveAndConvert(String queueName) throws AmqpException {
		// Do nothing
		return null;
	}

	@Override
	public Message sendAndReceive(Message message) throws AmqpException {
		// Do nothing
		return null;
	}

	@Override
	public Message sendAndReceive(String routingKey, Message message)
			throws AmqpException {
		// Do nothing
		return null;
	}

	@Override
	public Message sendAndReceive(String exchange, String routingKey,
			Message message) throws AmqpException {
		// Do nothing
		return null;
	}

	@Override
	public Object convertSendAndReceive(Object message) throws AmqpException {
		// Do nothing
		return null;
	}

	@Override
	public Object convertSendAndReceive(String routingKey, Object message)
			throws AmqpException {
		// Do nothing
		return null;
	}

	@Override
	public Object convertSendAndReceive(String exchange, String routingKey,
			Object message) throws AmqpException {
		// Do nothing
		return null;
	}

	@Override
	public Object convertSendAndReceive(Object message,
			MessagePostProcessor messagePostProcessor) throws AmqpException {
		// Do nothing
		return null;
	}

	@Override
	public Object convertSendAndReceive(String routingKey, Object message,
			MessagePostProcessor messagePostProcessor) throws AmqpException {
		// Do nothing
		return null;
	}

	@Override
	public Object convertSendAndReceive(String exchange, String routingKey,
			Object message, MessagePostProcessor messagePostProcessor)
			throws AmqpException {
		// Do nothing
		return null;
	}

}
