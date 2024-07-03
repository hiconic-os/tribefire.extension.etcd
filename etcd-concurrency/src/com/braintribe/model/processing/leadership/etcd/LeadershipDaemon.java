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

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.braintribe.logging.Logger;

public class LeadershipDaemon implements Runnable {

	protected static Logger logger = Logger.getLogger(LeadershipDaemon.class);

	protected EtcdLeadershipManager manager = null;
	protected boolean exitLoop = false;

	protected long lastElection = 0L;
	protected long lastCheck = 0L;

	protected CountDownLatch hasStopped = new CountDownLatch(1);

	public LeadershipDaemon(EtcdLeadershipManager manager) {
		this.manager = manager;
	}

	@Override
	public void run() {

		logger.trace(() -> "Running leadership daemon");

		try {
			if (exitLoop) {
				logger.debug(() -> "Not executing daemon further as this daemon should shut down.");
				hasStopped.countDown();
				return;
			}

			long now = System.currentTimeMillis();

			boolean doElection = false;
			if ((now - lastElection) > this.manager.checkInterval) {
				logger.trace(() -> "The election interval "+this.manager.checkInterval+" has been exceeded.");
				doElection = true;
			}

			if (doElection) {

				this.performElection(null);
				logger.trace(() -> "Performed election");
				lastElection = now;

			}
		} catch(Exception e) {
			logger.error("Error while executing LeadershipDaemon", e);
		}

		logger.trace(() -> "Leadership daemon is done with this run.");
	}

	protected void performElection(String domainId) {

		Set<LeadershipWrapper> leadershipListeners = this.manager.getLeadershipListeners(domainId);
		
		logger.trace(() -> "Performing an election run among "+leadershipListeners.size()+" registered listeners.");
		
		for (LeadershipWrapper listenerWrapper : leadershipListeners) {
			if (listenerWrapper.isLeader()) {
				logger.trace(() -> "Currently assumed leader will be checked: "+listenerWrapper);
				
				if (!manager.refreshLeadership(listenerWrapper.getDomainId(), listenerWrapper.getCandidateId())) {
					
					logger.trace(() -> "Revoking leadership in domain "+listenerWrapper.getDomainId()+" from candidate "+listenerWrapper.getCandidateId()+" located at "+listenerWrapper.getCandidate().getInstanceId());

					listenerWrapper.setLeader(false);
					listenerWrapper.getListener().surrenderLeadership(new EtcdLeadershipHandle(listenerWrapper.getDomainId(), listenerWrapper.getCandidateId(), manager.etcdProcessing));
					this.manager.notifyLeaderToSurrender(listenerWrapper.getDomainId(), listenerWrapper.getCandidateId());
				} else {
					logger.trace(() -> "The current leader is alive and will not be changed: "+listenerWrapper);
				}
				
			} else {
				logger.trace(() -> "Probing to make Listener leader: "+listenerWrapper);
				
				if (manager.tryLeadership(listenerWrapper.getDomainId(), listenerWrapper.getCandidateId())) {
					
					logger.trace(() -> "Granting leadership in domain "+listenerWrapper.getDomainId()+" to candidate "+listenerWrapper.getCandidateId()+" located at "+listenerWrapper.getCandidate().getInstanceId());
					
					listenerWrapper.setLeader(true);
					listenerWrapper.getListener().onLeadershipGranted(new EtcdLeadershipHandle(listenerWrapper.getDomainId(), listenerWrapper.getCandidateId(), manager.etcdProcessing));
					manager.notifyCandidatesOfNewLeader(listenerWrapper.getDomainId(), listenerWrapper.getCandidateId());
				} else {
					logger.trace(() -> "Probing did not succeed to make Listener leader: "+listenerWrapper);
				}
			}

		}

	}

	public void stopDaemon() {
		this.exitLoop = true;
		try {
			if (!hasStopped.await(30L, TimeUnit.SECONDS)) {
				logger.error("Timeout while waiting for stop.");
			}
		} catch (InterruptedException e) {
			logger.trace(() -> "Interrupted while waiting for stop.", e);
		}
	}

}
