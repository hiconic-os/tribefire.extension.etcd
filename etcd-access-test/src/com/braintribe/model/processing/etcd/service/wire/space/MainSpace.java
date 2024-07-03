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
package com.braintribe.model.processing.etcd.service.wire.space;

import java.util.List;

import com.braintribe.common.lcd.Numbers;
import com.braintribe.gm.service.wire.common.contract.CommonServiceProcessingContract;
import com.braintribe.gm.service.wire.common.contract.ServiceProcessingConfigurationContract;
import com.braintribe.integration.etcd.supplier.ClientSupplier;
import com.braintribe.model.generic.eval.Evaluator;
import com.braintribe.model.processing.etcd.service.EtcdBinaryProcessor;
import com.braintribe.model.processing.etcd.service.wire.contract.MainContract;
import com.braintribe.model.processing.service.common.ConfigurableDispatchingServiceProcessor;
import com.braintribe.model.resourceapi.base.BinaryRequest;
import com.braintribe.model.service.api.ServiceRequest;
import com.braintribe.wire.api.annotation.Import;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.context.WireContextConfiguration;

@Managed
public class MainSpace implements MainContract {

	protected final static List<String> endpointUrls = List.of("http://localhost:2379");

	@Import
	private ServiceProcessingConfigurationContract serviceProcessingConfiguration;

	@Import
	private CommonServiceProcessingContract commonServiceProcessing;

	@Override
	public void onLoaded(WireContextConfiguration configuration) {
		serviceProcessingConfiguration.registerServiceConfigurer(this::configureServices);
	}

	@Override
	public Evaluator<ServiceRequest> evaluator() {
		return commonServiceProcessing.evaluator();
	}

	private void configureServices(ConfigurableDispatchingServiceProcessor bean) {
		bean.removeInterceptor("auth");
		bean.register(BinaryRequest.T, etcdBinaryProcessor());
	}

	@Override
	@Managed
	public EtcdBinaryProcessor etcdBinaryProcessor() {
		EtcdBinaryProcessor bean = new EtcdBinaryProcessor();
		bean.setTtlInSeconds(60);
		// TODO: add authentication case here if needed
		bean.setClientSupplier(new ClientSupplier(endpointUrls, null, null));
		bean.setProject("etcd-binary-streamer-test");
		bean.setChunkSize(1 * (int) Numbers.MEGABYTE);
		return bean;
	}

	//
//	@Managed
//	private PersistenceGmSessionFactory sessionFactory() {
//		TestSessionFactory bean = new TestSessionFactory(evaluator());
//		bean.addAccess(access());
//		return bean;
//	}
//

}
