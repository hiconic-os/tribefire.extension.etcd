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
import com.braintribe.model.messaging.etcd.ComposeEtcdMessaging;
import com.braintribe.transport.messaging.api.Messaging;
import com.braintribe.transport.messaging.api.MessagingContext;

/**
 * <p>
 * Etcd implementation of the GenericModel-based messaging system.
 * 
 * @see Messaging
 * @author roman.kurmanowytsch
 */
public class EtcdMessaging implements Messaging<com.braintribe.model.messaging.etcd.EtcdMessaging> {

	private static final Logger logger = Logger.getLogger(EtcdMessaging.class);

	@Override
	public EtcdConnectionProvider createConnectionProvider(com.braintribe.model.messaging.etcd.EtcdMessaging denotation, MessagingContext context) {

		EtcdConnectionProvider connectionProvider = new EtcdConnectionProvider();
		connectionProvider.setConnectionConfiguration(denotation);
		connectionProvider.setMessagingContext(context);

		if (logger.isDebugEnabled()) {
			if (denotation instanceof ComposeEtcdMessaging) {
				ComposeEtcdMessaging composeEtcdMessaging = (ComposeEtcdMessaging) denotation;
				logger.debug(
						() -> "Created '" + EtcdConnectionProvider.class.getSimpleName() + "' with endpointUrls: '" + denotation.getEndpointUrls()
								+ "' project: '" + denotation.getProject() + "' username: '" + denotation.getUsername() + "' authority: '"
								+ composeEtcdMessaging.getAuthority() + "' authorityPrefix: '" + composeEtcdMessaging.getAuthorityPrefix() + "'");
			} else {
				// EtcdMessaging
				logger.debug(() -> "Created '" + EtcdConnectionProvider.class.getSimpleName() + "' with endpointUrls: '"
						+ denotation.getEndpointUrls() + "' project: '" + denotation.getProject() + "' username: '" + denotation.getUsername() + "'");
			}
		}

		return connectionProvider;
	}

}
