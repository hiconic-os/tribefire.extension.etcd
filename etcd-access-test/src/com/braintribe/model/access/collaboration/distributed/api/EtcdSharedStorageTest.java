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
package com.braintribe.model.access.collaboration.distributed.api;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;

import com.braintribe.codec.marshaller.json.JsonStreamMarshaller;
import com.braintribe.integration.etcd.supplier.ClientSupplier;
import com.braintribe.model.access.smood.collaboration.distributed.api.sharedstorage.AbstractSharedStorageTest;
import com.braintribe.testing.category.SpecialEnvironment;

@Category(SpecialEnvironment.class)
public class EtcdSharedStorageTest extends AbstractSharedStorageTest {

	protected static final List<String> endpointUrls = List.of("http://localhost:2379");

	@Override
	protected DcsaSharedStorage newDcsaSharedStorage() {

		EtcdDcsaSharedStorage storage = new EtcdDcsaSharedStorage();
		storage.setProject("storage-test-" + UUID.randomUUID().toString());
		// TODO: add authentication case here if needed
		storage.setClientSupplier(new ClientSupplier(endpointUrls, null, null));
		JsonStreamMarshaller marshaller = new JsonStreamMarshaller();
		storage.setMarshaller(marshaller);
		storage.setTtlInSeconds(60);

		return storage;
	}
}
