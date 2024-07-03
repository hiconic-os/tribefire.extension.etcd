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

import java.util.Arrays;

import com.braintribe.codec.marshaller.bin.Bin2Marshaller;
import com.braintribe.model.messaging.expert.Messaging;
import com.braintribe.transport.messaging.api.MessagingConnectionProvider;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.etcd.EtcdMessaging;

public class EtcdMessagingConnectionProvider {
	
	public static final EtcdMessagingConnectionProvider instance = new EtcdMessagingConnectionProvider();
	
	private MessagingConnectionProvider<? extends Messaging> messagingConnectionProvider;
	
	private EtcdMessagingConnectionProvider() {
		messagingConnectionProvider = getMessagingConnectionProvider();
	}
	
	public MessagingConnectionProvider<? extends Messaging> get() {
		return messagingConnectionProvider;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected com.braintribe.transport.messaging.api.MessagingConnectionProvider<? extends Messaging> getMessagingConnectionProvider() {
		
		Messaging denotationType = createDenotationType();
		
		com.braintribe.transport.messaging.api.Messaging messaging = getExpertByDenotationType(denotationType);
		
		return messaging.createConnectionProvider(denotationType, getMessagingContext());
		
	}
	
	protected Messaging createDenotationType() {
		
		com.braintribe.model.messaging.etcd.EtcdMessaging messagingDenotationType = com.braintribe.model.messaging.etcd.EtcdMessaging.T.create();
		messagingDenotationType.setProject("bamboo-archery-180615");
		messagingDenotationType.setEndpointUrls(Arrays.asList("http://localhost:2379"));
		
		return messagingDenotationType;
		
	}
	
	protected com.braintribe.transport.messaging.api.Messaging<? extends Messaging> getExpertByDenotationType(Messaging denotationType) {
		
		if (denotationType instanceof com.braintribe.model.messaging.etcd.EtcdMessaging) {
			EtcdMessaging messaging = new EtcdMessaging();
			return messaging;
		}
		
		return null;
	}

	protected MessagingContext getMessagingContext() {
		MessagingContext context = new MessagingContext();
		context.setMarshaller(new Bin2Marshaller());
		return context;
	}
}
