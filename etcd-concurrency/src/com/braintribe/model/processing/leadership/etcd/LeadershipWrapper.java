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

import java.util.Date;

import com.braintribe.model.leadership.Candidate;
import com.braintribe.model.processing.leadership.api.LeadershipListener;

public class LeadershipWrapper {
	
	protected String domainId;
	protected String candidateId;
	protected Candidate candidate;
	protected LeadershipListener listener;
	
	public LeadershipWrapper(String domainId, String candidateId, LeadershipListener listener, Candidate candidate) {
		super();
		this.domainId = domainId;
		this.candidateId = candidateId;
		this.listener = listener;
		this.candidate = candidate;
	}

	
	public String getDomainId() {
		return domainId;
	}
	public String getCandidateId() {
		return candidateId;
	}
	public LeadershipListener getListener() {
		return listener;
	}
	public Candidate getCandidate() {
		return candidate;
	}
	
	@Override
	public String toString() {
		return ""+domainId+"/"+candidateId+": candidate: "+candidate;
	}


	public boolean isLeader() {
		return candidate.getIsLeader();
	}
	public void setLeader(boolean b) {
		candidate.setIsLeader(b);
	}
	public long getMsSinceLastPing() {
		Date pingTimestamp = candidate.getPingTimestamp();
		long msSinceLastPing = System.currentTimeMillis() - pingTimestamp.getTime();
		return msSinceLastPing;
	}
	public void updatePingTimestamp() {
		candidate.setPingTimestamp(new Date());
	}
	public void updateLeadershipPingTimestamp() {
		candidate.setLeadershipPingTimestamp(new Date());
	}

}
