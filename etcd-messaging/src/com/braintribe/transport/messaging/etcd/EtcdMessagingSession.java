// ============================================================================
// Copyright BRAINTRIBE TECHNOLOGY GMBH, Austria, 2002-2022
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ============================================================================
package com.braintribe.transport.messaging.etcd;

import com.braintribe.logging.Logger;
import com.braintribe.model.messaging.Destination;
import com.braintribe.model.messaging.Message;
import com.braintribe.model.messaging.Queue;
import com.braintribe.model.messaging.Topic;
import com.braintribe.transport.messaging.api.MessageConsumer;
import com.braintribe.transport.messaging.api.MessageProducer;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.MessagingException;
import com.braintribe.transport.messaging.api.MessagingSession;

/**
 * <p>
 * {@link MessagingSession} implementation for {@link EtcdMessaging}.
 * 
 * @see MessagingSession
 * @author roman.kurmanowytsch
 */
public class EtcdMessagingSession implements MessagingSession {

	private EtcdConnection connection;
	private MessagingContext messagingContext;

	private static final Logger logger = Logger.getLogger(EtcdMessagingSession.class);

	private com.braintribe.model.messaging.etcd.EtcdMessaging providerConfiguration;

	public EtcdMessagingSession(com.braintribe.model.messaging.etcd.EtcdMessaging providerConfiguration) {
		this.providerConfiguration = providerConfiguration;
	}

	public EtcdConnection getConnection() {
		return connection;
	}

	@Override
	public MessageProducer createMessageProducer() throws MessagingException {
		return createMessageProducer(null);
	}

	@Override
	public void open() throws MessagingException {
		// nothing to do
	}

	@Override
	public void close() throws MessagingException {
		// nothing to do; handled by connection
	}

	@Override
	public Queue createQueue(String name) throws MessagingException {
		logger.trace(() -> "Creating queue: " + name);
		Queue t = Queue.T.create();
		t.setName(name);

		return t;
	}

	@Override
	public Topic createTopic(String name) throws MessagingException {
		logger.trace(() -> "Creating topic: " + name);
		Topic t = Topic.T.create();
		t.setName(name);

		return t;
	}

	@Override
	public Message createMessage() throws MessagingException {
		return Message.T.create();
	}

	@Override
	public MessageProducer createMessageProducer(Destination destination) throws MessagingException {

		logger.debug(() -> "Creating message producer for destination: " + destination + ", name: "
				+ (destination != null ? destination.getName() : "<null>"));

		EtcdMessageProducer producer = new EtcdMessageProducer(destination);
		producer.setApplicationId(messagingContext.getApplicationId());
		producer.setNodeId(messagingContext.getNodeId());
		producer.setMessagingContext(messagingContext);
		producer.setSession(this);
		producer.setConnection(connection);

		connection.registerProducer(producer);
		logger.trace(() -> "Created a producer for destination: " + destination);

		return producer;
	}

	@Override
	public MessageConsumer createMessageConsumer(Destination destination) throws MessagingException {

		logger.debug(() -> "Creating message consumer for destination: " + destination + ", name: "
				+ (destination != null ? destination.getName() : "<null>"));

		EtcdMessageConsumer consumer = new EtcdMessageConsumer(destination, providerConfiguration);
		consumer.setApplicationId(messagingContext.getApplicationId());
		consumer.setNodeId(messagingContext.getNodeId());
		consumer.setMessagingContext(messagingContext);
		consumer.setSession(this);
		consumer.setConnection(connection);

		connection.registerConsumer(consumer);
		connection.subscribe(destination.getName(), consumer);

		logger.trace(() -> "Created a consumer for destination: " + destination);

		return consumer;
	}

	public void setMessagingContext(MessagingContext messagingContext2) {
		this.messagingContext = messagingContext2;
	}

	public void setConnection(EtcdConnection etcdConnection) {
		this.connection = etcdConnection;
	}

	@Override
	public String toString() {
		if (connection == null) {
			return "etcd Messaging";
		} else {
			return "etcd Messaging (" + connection.toString() + ")";
		}
	}
}
