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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.braintribe.codec.marshaller.bin.Bin2Marshaller;
import com.braintribe.model.messaging.Message;
import com.braintribe.model.messaging.expert.Messaging;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.etcd.EtcdMessaging;

public class EtcdDenotationTypeBasedTest {
	
	private DateFormat df = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected com.braintribe.transport.messaging.api.MessagingConnectionProvider getMessagingConnectionProvider() {
		
		Messaging denotationType = createDenotationType();
		
		com.braintribe.transport.messaging.api.Messaging messaging = getExpertByDenotationType(denotationType);
		
		return messaging.createConnectionProvider(denotationType, getMessagingContext());
		
	}

	
	protected Messaging createDenotationType() {
		
		com.braintribe.model.messaging.etcd.EtcdMessaging messagingDenotationType = com.braintribe.model.messaging.etcd.EtcdMessaging.T.create();
		messagingDenotationType.setProject("bamboo-archery-180615");
		
		return messagingDenotationType;
		
	}
	
	protected com.braintribe.transport.messaging.api.Messaging<? extends Messaging> getExpertByDenotationType(Messaging denotationType) {
		
		if (denotationType instanceof com.braintribe.model.messaging.etcd.EtcdMessaging) {
			EtcdMessaging messaging = new EtcdMessaging();
			return messaging;
		}
		
		return null;
		
	}
	
	protected void populate(Message message) {
		
		String iden = "Message-"+df.format(new Date());
		
		message.setBody("Body: "+iden);
		message.setCorrelationId("CorrelationId: "+iden);
		message.setHeaders(createTestHeaders(iden));
	}
	
	private static Map<String, Object> createTestHeaders(String iden) {
		
		String[] testArray = new String[] {"A", "B", "C"};
		
		Map<String, Object> h = new HashMap<String, Object>();
		h.put("String-header", "String-header of "+iden);
		h.put("Long-header", Long.MIN_VALUE);
		h.put("Integer-header", Integer.MAX_VALUE);
		h.put("Boolean-header", Boolean.TRUE);
		
		//makes the marshalling fail with BinMarshaller:
		//h.put("Array-header", testArray);
		
		h.put("List-header", Arrays.asList(testArray));
		
		return h;
	}
	
	protected MessagingContext getMessagingContext() {
		MessagingContext context = new MessagingContext();
		context.setMarshaller(new Bin2Marshaller());
		return context;
	}
}
