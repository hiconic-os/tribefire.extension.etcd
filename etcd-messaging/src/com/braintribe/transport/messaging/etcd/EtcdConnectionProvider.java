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

import java.net.InetAddress;
import java.net.URI;
import java.time.Duration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import com.braintribe.common.lcd.Numbers;
import com.braintribe.logging.Logger;
import com.braintribe.transport.messaging.api.MessagingConnectionProvider;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.api.MessagingException;
import com.braintribe.util.network.NetworkTools;
import com.braintribe.utils.StringTools;

/**
 * <p>
 * {@link MessagingConnectionProvider} implementation for providing {@link EtcdConnection}(s).
 * 
 * @see MessagingConnectionProvider
 * @see EtcdConnection
 * @author roman.kurmanowytsch
 */
public class EtcdConnectionProvider implements MessagingConnectionProvider<EtcdConnection> {

	private static final Logger logger = Logger.getLogger(EtcdConnectionProvider.class);

	private com.braintribe.model.messaging.etcd.EtcdMessaging providerConfiguration;
	private MessagingContext messagingContext;

	private Set<EtcdConnection> connections = new HashSet<>();
	private ReentrantLock connectionsLock = new ReentrantLock();
	protected Map<String, String> resolvedHosts;
	protected long nextHostResolving = -1L;

	public EtcdConnectionProvider() {
	}

	public void setConnectionConfiguration(com.braintribe.model.messaging.etcd.EtcdMessaging providerConfiguration) {
		this.providerConfiguration = providerConfiguration;
	}

	public MessagingContext getMessagingContext() {
		return messagingContext;
	}

	public void setMessagingContext(MessagingContext messagingContext) {
		this.messagingContext = messagingContext;
	}

	@Override
	public EtcdConnection provideMessagingConnection() throws MessagingException {

		EtcdConnection connection = new EtcdConnection(providerConfiguration);
		connection.setMessagingContext(messagingContext);

		connectionsLock.lock();
		try {
			connections.add(connection);
		} finally {
			connectionsLock.unlock();
		}

		return connection;

	}

	@Override
	public void close() {
		connectionsLock.lock();
		try {
			for (EtcdConnection con : connections) {
				try {
					con.close();
				} catch (Exception e) {
					logger.error("Could not close connection: " + con, e);
				}
			}
		} finally {
			connectionsLock.unlock();
		}
	}

	@Override
	public String toString() {
		return description();
	}

	@Override
	public String description() {
		if (providerConfiguration == null) {
			return "etcd Messaging (non-configured)";
		} else {

			List<String> endpointUrls = providerConfiguration.getEndpointUrls();

			long now = System.currentTimeMillis();
			if (endpointUrls != null && !endpointUrls.isEmpty() && (resolvedHosts == null || now > nextHostResolving)) {
				nextHostResolving = now + Numbers.MILLISECONDS_PER_MINUTE * 5;
				Map<String, String> map = new LinkedHashMap<>();
				for (String url : endpointUrls) {
					try {
						URI uri = new URI(url);
						String host = uri.getHost();
						if (!map.containsKey(host)) {
							List<InetAddress> addresses = NetworkTools.resolveHostname(host, Duration.ofSeconds(5));
							String addressesString = addresses.stream().map(a -> a.getHostAddress()).collect(Collectors.joining(","));
							map.put(host, addressesString);
						}

					} catch (Exception e) {
						logger.debug(() -> "Error while trying to resolve host from URL " + url, e);
					}
				}

				resolvedHosts = map;
			}

			StringBuilder sb = new StringBuilder("etcd Messaging (");
			if (endpointUrls == null || endpointUrls.isEmpty()) {
				sb.append("No URLs specified.");
			} else {
				sb.append(StringTools.join(", ", endpointUrls));
				if (resolvedHosts != null && !resolvedHosts.isEmpty()) {
					sb.append("; DNS resolution: ");
					String resolutionInformation = resolvedHosts.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue())
							.collect(Collectors.joining(", "));
					sb.append(resolutionInformation);
				}
			}
			sb.append(")");

			return sb.toString();
		}
	}
}
