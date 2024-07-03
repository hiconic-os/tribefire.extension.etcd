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
package com.braintribe.model.processing.lock.etcd.wire.space;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.braintribe.codec.marshaller.api.Marshaller;
import com.braintribe.codec.marshaller.bin.Bin2Marshaller;
import com.braintribe.codec.marshaller.bin.BinMarshaller;
import com.braintribe.codec.marshaller.common.BasicConfigurableMarshallerRegistry;
import com.braintribe.integration.etcd.supplier.ClientSupplier;
import com.braintribe.model.activemqdeployment.ActiveMqWorker;
import com.braintribe.model.messaging.jms.JmsActiveMqConnection;
import com.braintribe.model.messaging.jms.JmsConnection;
import com.braintribe.model.processing.activemq.service.ActiveMqWorkerImpl;
import com.braintribe.model.processing.lock.etcd.EtcdLockManager;
import com.braintribe.model.processing.lock.etcd.impl.EtcdLockManagerTest;
import com.braintribe.model.processing.lock.etcd.wire.contract.EtcdLockingTestContract;
import com.braintribe.transport.messaging.api.MessagingContext;
import com.braintribe.transport.messaging.jms.JmsActiveMqMessagingSessionProvider;
import com.braintribe.utils.lcd.StringTools;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.context.WireContextConfiguration;
import com.braintribe.wire.api.scope.InstanceConfiguration;

@Managed
public class EtcdLockingTestSpace implements EtcdLockingTestContract {

	@Override
	public void onLoaded(WireContextConfiguration configuration) {
		EtcdLockingTestContract.super.onLoaded(configuration);

		// This initializes the lock manager and with it the db table
		lockManager();
	}

	@Override
	@Managed
	public Integer failProbability() {
		return 10;
	}

	@Managed
	@Override
	public EtcdLockManager lockManager() {
		EtcdLockManager bean = new EtcdLockManager();
		// TODO: add authentication case here if needed
		bean.setClientSupplier(new ClientSupplier(endpointUrls(), null, null));
		bean.setMessagingSessionProvider(messagingSessionProvider());
		return bean;
	}

	@Managed
	private JmsActiveMqMessagingSessionProvider messagingSessionProvider() {
		// we need to initialize service first
		activeMqService();

		JmsActiveMqMessagingSessionProvider bean = new JmsActiveMqMessagingSessionProvider();
		bean.setMessagingContext(messagingContext());
		bean.setConnectionDenotation(connectionDenotation());
		return bean;
	}

	@Managed
	private JmsConnection connectionDenotation() {
		JmsActiveMqConnection bean = JmsActiveMqConnection.T.create("48194a50-6ca2-43a1-a948-4f6a56f35cb6");
		bean.setHostAddress("tcp://localhost:" + EtcdLockManagerTest.ACTIVEMQ_PORT + "?soTimeout=5000&daemon=true");
		return bean;
	}

	@Managed
	private MessagingContext messagingContext() {
		MessagingContext bean = new MessagingContext();
		bean.setMarshaller(messageMarshaller());
		return bean;
	}

	@Managed
	private Marshaller messageMarshaller() {
		Bin2Marshaller bean = new Bin2Marshaller();
		return bean;
	}

	@Managed
	private BasicConfigurableMarshallerRegistry marshallerRegistry() {
		BasicConfigurableMarshallerRegistry bean = new BasicConfigurableMarshallerRegistry();
		bean.registerMarshaller("application/gm", binMarshaller());
		bean.registerMarshaller("gm/bin", binMarshaller());
		return bean;
	}

	@Managed
	private BinMarshaller binMarshaller() {
		BinMarshaller bean = new BinMarshaller();
		bean.setWriteRequiredTypes(false);
		return bean;
	}

	@Override
	@Managed
	public Integer workerCount() {
		return 2;
	}

	@Override
	@Managed
	public ActiveMqWorkerImpl activeMqService() {

		String testMode = System.getProperty("testMode");
		if (!StringTools.isEmpty(testMode)) {
			if (testMode.equalsIgnoreCase("remote")) {
				return null;
			}
		}

		ActiveMqWorkerImpl bean = new ActiveMqWorkerImpl();

		File dir = new File("res/activemq-data");
		dir.mkdirs();

		bean.setBindAddress("0.0.0.0");
		bean.setPort(EtcdLockManagerTest.ACTIVEMQ_PORT);
		bean.setBrokerName("testbroker");
		bean.setDataDirectory(dir);
		bean.setPersistenceDbDir(dir);
		bean.setUseJmx(false);
		bean.setWorkerIdentification(ActiveMqWorker.T.create());
		bean.setPersistent(false);

		InstanceConfiguration.currentInstance().onDestroy(new Runnable() {
			@Override
			public void run() {
				try {
					bean.stop(null);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		try {
			bean.start(null);
		} catch (Exception e) {
			throw new RuntimeException("Error while starting ActiveMq worker.", e);
		}

		return bean;
	}

	@Override
	public List<String> endpointUrls() {
		return Arrays.asList("http://localhost:2379");
	}
}
