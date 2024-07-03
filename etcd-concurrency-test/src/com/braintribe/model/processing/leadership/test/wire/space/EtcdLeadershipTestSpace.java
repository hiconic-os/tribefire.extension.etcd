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
package com.braintribe.model.processing.leadership.test.wire.space;

import java.util.Arrays;
import java.util.List;

import com.braintribe.integration.etcd.supplier.ClientSupplier;
import com.braintribe.model.processing.leadership.api.LeadershipManager;
import com.braintribe.model.processing.leadership.etcd.EtcdLeadershipManager;
import com.braintribe.model.processing.leadership.test.wire.contract.EtcdLeadershipTestContract;
import com.braintribe.model.service.api.InstanceId;
import com.braintribe.wire.api.annotation.Managed;
import com.braintribe.wire.api.context.WireContextConfiguration;

@Managed
public class EtcdLeadershipTestSpace implements EtcdLeadershipTestContract {

	@Override
	public void onLoaded(WireContextConfiguration configuration) {
		EtcdLeadershipTestContract.super.onLoaded(configuration);

		// This initializes the leadership manager and with it the db table
		leadershipManager();
	}

	@Override
	@Managed
	public Integer failProbability() {
		return 10;
	}

	@Override
	@Managed
	public LeadershipManager leadershipManager() {
		EtcdLeadershipManager bean = new EtcdLeadershipManager();
		// TODO: add authentication case here if needed
		bean.setClientSupplier(new ClientSupplier(endpointUrls(), null, null));
		bean.setLocalInstanceId(instanceId());
		return bean;
	}

	@Managed
	private InstanceId instanceId() {
		InstanceId bean = InstanceId.T.create();
		bean.setNodeId("test-node");
		bean.setApplicationId("test-app");
		return bean;
	}

	@Override
	@Managed
	public Long writeInterval() {
		return 1_000L;
	}

	@Override
	@Managed
	public String host() {
		return "localhost";
	}

	@Override
	@Managed
	public Integer port() {
		return 2048;
	}

	@Override
	@Managed
	public Integer workerCount() {
		return 2;
	}

	@Override
	public List<String> endpointUrls() {
		return Arrays.asList("http://localhost:2379");
	}

}
