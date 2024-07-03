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
package com.braintribe.model.processing.lock.etcd.config;

import java.io.File;

import com.braintribe.logging.Logger;
import com.braintribe.model.processing.lock.etcd.wire.contract.EtcdLockingTestContract;
import com.braintribe.wire.api.Wire;
import com.braintribe.wire.api.context.WireContext;

public class Configurator {

	private static final Logger logger = Logger.getLogger(Configurator.class);

	protected WireContext<EtcdLockingTestContract> context;

	public Configurator() throws Exception {
		this.initialize();
	}
	
	public void initialize() throws Exception {
		try {
			
			this.cleanDebug();
			
			logger.info("Initializing context");

			context = Wire
					.context(EtcdLockingTestContract.class)
					.bindContracts("com.braintribe.model.processing.lock.etcd.wire")
					.build();
			
			logger.info("Done initializing context");
			
		} catch(Throwable t) {
			logger.info("Error while initializing context", t);
			throw new RuntimeException("Error while initializing context", t);
		}
	}
	
	public void close() {
		context.shutdown();
	}

	protected void cleanDebug() {
		File debugOutput = new File("debug");
		if (debugOutput.exists()) {
			File[] files = debugOutput.listFiles();
			if (files != null) {
				for (File f : files) {
					f.delete();
				}
			}
			debugOutput.delete();
		}
	}
	
	public EtcdLockingTestContract getConfiguration() {
		return context.contract();
	}
}
