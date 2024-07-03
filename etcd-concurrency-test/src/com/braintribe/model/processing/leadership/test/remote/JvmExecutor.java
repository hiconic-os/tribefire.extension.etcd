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
package com.braintribe.model.processing.leadership.test.remote;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.braintribe.model.processing.leadership.test.worker.PortWriter;

public class JvmExecutor {

	public static List<RemoteProcess> executeWorkers(
			int workerCount, 
			String domainId, 
			int failProbability,
			int iterations,
			List<Integer> remoteWriterPorts,
			int udpOffset) throws Exception {

		String separator = System.getProperty("file.separator");
		String classpath = System.getProperty("java.class.path");
		String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
		
		List<RemoteProcess> remoteProcesses = new ArrayList<RemoteProcess>();

		for (int i=0; i<workerCount; ++i) {
			String candidateId = "Writer-"+i+"-"+UUID.randomUUID().toString();
			int listeningPort = PortWriter.UDP_BASEPORT + udpOffset + i;
			remoteWriterPorts.add(listeningPort);
			
			ProcessBuilder processBuilder = new ProcessBuilder(path, "-cp", classpath, "-DtestMode=remote", WorkerExecutor.class.getName(), 
					"failProbability="+failProbability, 
					"domainId="+domainId, 
					"candidateId="+candidateId,
					"id="+i,
					"listeningPort="+listeningPort,
					"iterations="+iterations);
			processBuilder.inheritIO();
			Process process = processBuilder.start();
			RemoteProcess remoteProcess = new RemoteProcess(process, candidateId);
			remoteProcesses.add(remoteProcess);
			
			InputStream inStream = process.getInputStream();
			InputStreamWriter ishStdout = new InputStreamWriter(inStream);
			ishStdout.start();
		}
		
		return remoteProcesses;
		
	}

}
