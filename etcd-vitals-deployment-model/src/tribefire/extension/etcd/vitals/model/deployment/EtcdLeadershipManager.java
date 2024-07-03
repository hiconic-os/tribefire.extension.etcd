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
package tribefire.extension.etcd.vitals.model.deployment;

import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.leadershipdeployment.LeadershipManager;

public interface EtcdLeadershipManager extends LeadershipManager, EtcdConnectionInformation {

	EntityType<EtcdLeadershipManager> T = EntityTypes.T(EtcdLeadershipManager.class);
	
	Long getDefaultLeadershipTimeout();
	void setDefaultLeadershipTimeout(Long defaultLeadershipTimeout);
	
	Long getDefaultCandidateTimeout();
	void setDefaultCandidateTimeout(Long defaultCandidateTimeout);

	Long getCheckInterval();
	void setCheckInterval(Long checkInterval);

}
