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
package com.braintribe.model.processing.leadership.etcd;

import java.net.URLEncoder;

import com.braintribe.integration.etcd.EtcdProcessing;
import com.braintribe.logging.Logger;
import com.braintribe.model.processing.leadership.api.LeadershipHandle;

public class EtcdLeadershipHandle implements LeadershipHandle {

	private static Logger logger = Logger.getLogger(EtcdLeadershipHandle.class);
	
	private String identification;
	private EtcdProcessing etcdProcessing;
	private String domainId;

	public EtcdLeadershipHandle(String domainId, String identification, EtcdProcessing etcdProcessing) {
		this.domainId = domainId;
		this.identification = identification;
		this.etcdProcessing = etcdProcessing;
	}
	
	@Override
	public String getIdentification() {
		return identification;
	}

	@Override
	public void release() {
		try {
			String key = EtcdLeadershipManager.leadershipPrefix+URLEncoder.encode(domainId, "UTF-8");
			etcdProcessing.deleteIfMatches(key, identification);
		} catch(Exception e) {
			logger.error("Error while releasing leadership.", e);
		}
	}

}
