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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.braintribe.codec.marshaller.json.JsonStreamMarshaller;
import com.braintribe.integration.etcd.supplier.ClientSupplier;
import com.braintribe.model.access.collaboration.distributed.api.model.CsaAppendDataManipulation;
import com.braintribe.model.access.collaboration.distributed.api.model.CsaOperation;
import com.braintribe.testing.category.SpecialEnvironment;

@Category(SpecialEnvironment.class)
public class EtcdDcsaSharedStorageTest {

	protected final static List<String> endpointUrls = List.of("http://localhost:2379");

	@Test
	public void testDcsaStorage() throws Exception {

		EtcdDcsaSharedStorage storage = new EtcdDcsaSharedStorage();
		storage.setProject("storage-test-" + UUID.randomUUID().toString());
		// TODO: add authentication case here if needed
		storage.setClientSupplier(new ClientSupplier(endpointUrls, null, null));
		JsonStreamMarshaller marshaller = new JsonStreamMarshaller();
		storage.setMarshaller(marshaller);
		storage.setTtlInSeconds(60);

		String accessId = UUID.randomUUID().toString();

		CsaAppendDataManipulation csaOperation = CsaAppendDataManipulation.T.create();
		csaOperation.setId(UUID.randomUUID().toString());

		String revisionAfterFirstWrite = storage.storeOperation(accessId, csaOperation);

		assertThat(revisionAfterFirstWrite).isNotNull();
		assertThat(revisionAfterFirstWrite).isNotEmpty();
		assertThat(revisionAfterFirstWrite).isEqualTo("0000000000000001");

		System.out.println("Revision after 1st write: " + revisionAfterFirstWrite);

		CsaAppendDataManipulation csaOperation2 = CsaAppendDataManipulation.T.create();
		csaOperation2.setId(UUID.randomUUID().toString());
		String revisionAfterSecondWrite = storage.storeOperation(accessId, csaOperation2);

		assertThat(revisionAfterSecondWrite).isNotNull();
		assertThat(revisionAfterSecondWrite).isNotEmpty();
		assertThat(revisionAfterSecondWrite).isEqualTo("0000000000000002");

		assertThat(getNumericalPartOfKey(revisionAfterSecondWrite)).isGreaterThan(getNumericalPartOfKey(revisionAfterFirstWrite));

		System.out.println("Revision after 2nd write: " + revisionAfterSecondWrite);

		DcsaIterable iterable = storage.readOperations(accessId, null);
		assertThat(iterable.getLastReadMarker()).isEqualTo(revisionAfterSecondWrite);

		int count = 0;
		for (CsaOperation op : iterable) {
			count++;
		}

		assertThat(count).isEqualTo(2);

		iterable = storage.readOperations(accessId, revisionAfterFirstWrite);
		count = 0;
		CsaOperation resultOp = null;
		for (CsaOperation op : iterable) {
			resultOp = op;
			count++;
		}

		assertThat(count).isEqualTo(1);
		assertThat(resultOp.getId().toString()).isEqualTo(csaOperation2.getId().toString());
	}

	protected long getNumericalPartOfKey(String key) {
		int index = key.lastIndexOf('/');
		return Long.parseLong(key.substring(index + 1));
	}
}
