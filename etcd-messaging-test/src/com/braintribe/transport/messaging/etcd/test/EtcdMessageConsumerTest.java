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

import org.junit.experimental.categories.Category;

//import org.junit.Test;

import com.braintribe.model.messaging.Destination;
import com.braintribe.model.messaging.Message;
import com.braintribe.testing.category.SpecialEnvironment;
import com.braintribe.transport.messaging.api.MessageConsumer;
import com.braintribe.transport.messaging.api.MessageListener;
import com.braintribe.transport.messaging.api.MessagingConnection;
import com.braintribe.transport.messaging.api.MessagingException;
import com.braintribe.transport.messaging.api.MessagingSession;

@Category(SpecialEnvironment.class)
public class EtcdMessageConsumerTest extends EtcdDenotationTypeBasedTest {
	
	public static void main(String[] args) {
		EtcdMessageConsumerTest t = new EtcdMessageConsumerTest();
		
		try {
			t.testSynchronous();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//@Test
	public void testSynchronous() throws Exception {
		
		MessagingConnection connection = getMessagingConnectionProvider().provideMessagingConnection();
		
		MessagingSession session = connection.createMessagingSession();
		
		Destination destination = session.createQueue("tf-test-queue-02");
		
		MessageConsumer messageConsumer = session.createMessageConsumer(destination);
		
		while(true) {
			Message message = messageConsumer.receive();
			System.out.println("GOT MESSAGE FROM RECEIVE(): "+message.getBody());
		}
	}
	
	//@Test
	public void testAsynchronous() throws Exception {
		
		MessagingConnection connection = getMessagingConnectionProvider().provideMessagingConnection();
		
		MessagingSession session = connection.createMessagingSession();
		
		Destination destination = session.createTopic("tf-test-topic-02");
		
		MessageConsumer messageConsumer = session.createMessageConsumer(destination);
		
		messageConsumer.setMessageListener(new MessageListener() {

			@Override
			public void onMessage(Message message) throws MessagingException {
				System.out.println("GOT MESSAGE FROM LISTENER: "+message.getBody());
			}
			
		});
		
		Thread.sleep(20000);
		
	}
	
}
