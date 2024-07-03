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
package tribefire.extension.etcd.etcd_vitals.wire.space;

import com.braintribe.gm.model.reason.Maybe;
import com.braintribe.model.plugin.etcd.EtcdPlugableLeadershipManager;
import com.braintribe.model.plugin.etcd.EtcdPlugableLockManager;
import com.braintribe.utils.lcd.NullSafe;

import tribefire.extension.etcd.vitals.model.deployment.EtcdLeadershipManager;
import tribefire.extension.etcd.vitals.model.deployment.EtcdLockManager;
import tribefire.extension.etcd.vitals.model.deployment.EtcdMessaging;
import tribefire.module.api.DenotationTransformationContext;
import tribefire.module.api.DenotationTransformerRegistry;

/**
 * @author peter.gazdik
 */
public class EtcdPluggablesEdr2ccMorphers {

	// TODO currently we only know this is edr2cc because there is no other use-case for DenoTrans.
	public static final String globalIdPrefix = "edr2cc:etcd:";

	public static void bindMorphers(DenotationTransformerRegistry tegistry) {
		tegistry.registerStandardMorpher(EtcdPlugableLeadershipManager.T, EtcdLeadershipManager.T, EtcdPluggablesEdr2ccMorphers::leadership);

		tegistry.registerStandardMorpher(EtcdPlugableLockManager.T, EtcdLockManager.T, EtcdPluggablesEdr2ccMorphers::locking);

		tegistry.registerStandardMorpher(com.braintribe.model.messaging.etcd.EtcdMessaging.T, EtcdMessaging.T,
				EtcdPluggablesEdr2ccMorphers::messaging);
	}

	private static Maybe<EtcdLeadershipManager> leadership(DenotationTransformationContext context, EtcdPlugableLeadershipManager pluggable) {
		EtcdLeadershipManager deployable = context.create(EtcdLeadershipManager.T);
		deployable.setGlobalId(globalIdPrefix + context.denotationId());

		deployable.setEndpointUrls(pluggable.getEndpointUrls());
		deployable.setExternalId(deployable.getGlobalId());
		deployable.setName("etcd Leadership Manager");

		deployable.setUsername(pluggable.getUsername());
		deployable.setPassword(pluggable.getPassword());
		deployable.setProject(pluggable.getProject());

		deployable.setDefaultLeadershipTimeout(pluggable.getDefaultLeadershipTimeout());
		deployable.setDefaultCandidateTimeout(pluggable.getDefaultCandidateTimeout());
		deployable.setCheckInterval(pluggable.getCheckInterval());

		return Maybe.complete(deployable);
	}

	private static Maybe<EtcdLockManager> locking(DenotationTransformationContext context, EtcdPlugableLockManager pluggable) {
		EtcdLockManager deployable = context.create(EtcdLockManager.T);
		deployable.setGlobalId(globalIdPrefix + context.denotationId());

		deployable.setEndpointUrls(pluggable.getEndpointUrls());
		deployable.setExternalId(deployable.getGlobalId());
		deployable.setName("etcd Lock Manager");

		deployable.setUsername(pluggable.getUsername());
		deployable.setPassword(pluggable.getPassword());
		deployable.setProject(pluggable.getProject());
		
		return Maybe.complete(deployable);
	}

	private static Maybe<EtcdMessaging> messaging(DenotationTransformationContext context,
			com.braintribe.model.messaging.etcd.EtcdMessaging pluggable) {

		EtcdMessaging deployable = context.create(EtcdMessaging.T);
		deployable.setGlobalId(globalIdPrefix + context.denotationId());

		deployable.setExternalId(deployable.getGlobalId());
		deployable.setName(NullSafe.get(pluggable.getName(), "etcd Message Queue"));

		deployable.setEndpointUrls(pluggable.getEndpointUrls());
		deployable.setProject(pluggable.getProject());
		deployable.setUsername(pluggable.getUsername());
		deployable.setPassword(pluggable.getPassword());

		return Maybe.complete(deployable);
	}
}
