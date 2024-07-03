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
package com.braintribe.transport.messaging.etcd.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.braintribe.common.lcd.Numbers;
import com.braintribe.model.messaging.Destination;
import com.braintribe.model.messaging.Message;
import com.braintribe.provider.Holder;
import com.braintribe.testing.category.SpecialEnvironment;
import com.braintribe.transport.messaging.api.MessageConsumer;
import com.braintribe.transport.messaging.api.MessageProducer;
import com.braintribe.transport.messaging.api.MessagingConnection;
import com.braintribe.transport.messaging.api.MessagingConnectionProvider;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.MessagingSession;
import com.braintribe.transport.messaging.api.test.GmMessagingHeavyDistributionTest;

@Category(SpecialEnvironment.class)
@Ignore // Skipping these tests for etcd, as they create thousands of destinations.
public class EtcdMessagingHeavyDistributionTest extends GmMessagingHeavyDistributionTest {

	static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	static final Random rnd = new Random();

	@Override
	protected MessagingConnectionProvider<? extends MessagingConnection> getMessagingConnectionProvider() {
		return EtcdMessagingConnectionProvider.instance.get();
	}

	@Override
	protected MessagingContext getMessagingContext() {
		return EtcdMessagingConnectionProvider.instance.getMessagingContext();
	}

	/**
	 * Tests increasing sizes of messages 1-10 MB
	 */
	@Test
	public void testLargeMessage() throws Exception {

		for (int s = 1; s <= 10; ++s) {
			System.out.println("Testing " + s);
			MessagingConnection connection = getMessagingConnectionProvider().provideMessagingConnection();

			MessagingSession session = connection.createMessagingSession();

			Destination destination = session.createQueue("tf-test-large-message");

			MessageConsumer messageConsumer = session.createMessageConsumer(destination);

			CountDownLatch countDown = new CountDownLatch(1);
			Holder<String> receivedMessageHolder = new Holder<>();
			messageConsumer.setMessageListener(m -> {
				String content = (String) m.getBody();
				receivedMessageHolder.accept(content);
				countDown.countDown();
			});

			int len = (int) Numbers.MEGABYTE * s;
			String sentBody = randomString(len);
			String iden = UUID.randomUUID().toString();

			Message message = session.createMessage();
			message.setPersistent(false);
			message.setBody(sentBody);
			message.setCorrelationId("CorrelationId: " + iden);

			MessageProducer messageProducer = session.createMessageProducer(destination);

			messageProducer.sendMessage(message);

			countDown.await(10, TimeUnit.SECONDS);

			String receivedMessage = receivedMessageHolder.get();
			assertThat(receivedMessage).isEqualTo(sentBody);

			connection.close();
		}
	}

	protected static String randomString(int len) {
		StringBuilder sb = new StringBuilder(len);
		for (int i = 0; i < len; i++)
			sb.append(AB.charAt(rnd.nextInt(AB.length())));
		return sb.toString();
	}
}
