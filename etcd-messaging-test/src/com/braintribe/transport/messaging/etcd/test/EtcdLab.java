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

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.braintribe.common.lcd.Numbers;
import com.braintribe.testing.category.SpecialEnvironment;
import com.braintribe.transport.messaging.etcd.EtcdConnection;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Watch;
import io.etcd.jetcd.options.WatchOption;
import io.etcd.jetcd.options.WatchOption.Builder;
import io.etcd.jetcd.watch.WatchEvent;

@Category(SpecialEnvironment.class)
public class EtcdLab {

	@Test
	public void testBlockingWatcher() throws Exception {

		List<String> endpointUrls = new ArrayList<>();
		endpointUrls.add("http://127.0.0.1:2379");

		List<URI> endpointUris = endpointUrls.stream().map(u -> {
			try {
				return new URI(u);
			} catch (URISyntaxException e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toList());

		Client client = Client.builder().maxInboundMessageSize((int) Numbers.MEGABYTE * 100).endpoints(endpointUris).build();
		KV kvClient = client.getKVClient();

		ByteSequence bsKey = ByteSequence.from("foo", StandardCharsets.UTF_8);
		ByteSequence bsValue = ByteSequence.from("bar", StandardCharsets.UTF_8);

		Builder watchBuilder = WatchOption.newBuilder().withNoDelete(true);
		watchBuilder.withRange(EtcdConnection.getRangeEnd("foo")).withProgressNotify(false);
		WatchOption option = watchBuilder.build();

		Watch watch = client.getWatchClient();
		watch.watch(bsKey, option, response -> {

			for (WatchEvent event : response.getEvents()) {
				String stringKey = Optional.ofNullable(event.getKeyValue().getKey()).map(k -> k.toString(StandardCharsets.UTF_8)).orElse("");
				System.out.println("Received event for " + stringKey);
			}
		});

		kvClient.put(bsKey, bsValue).get();
		kvClient.get(bsKey).get();

	}

}
