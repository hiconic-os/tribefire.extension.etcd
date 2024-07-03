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

import com.braintribe.model.messaging.etcd.EtcdMessaging;
import com.braintribe.transport.messaging.api.MessagingContext;

import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;

public class TopicListenerWorker extends AbstractListenerWorker {

	public TopicListenerWorker(EtcdMessaging providerConfiguration, MessagingContext messagingContext, Client client, KV kvClient, EtcdConnection etcdConnection) {
		super(providerConfiguration, messagingContext, client, kvClient, etcdConnection);
	}

	@Override
	protected boolean acceptMessage(ReceivedMessageContext context, String myWorkerId) throws Exception {
		return true;
	}

	@Override
	protected String getDestinationType() {
		return "topic";
	}
}
