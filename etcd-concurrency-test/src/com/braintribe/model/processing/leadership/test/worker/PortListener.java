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
package com.braintribe.model.processing.leadership.test.worker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.braintribe.utils.DateTools;

public class PortListener implements Runnable {

	protected boolean stop = false;
	protected Socket socket = null;
	protected InputStream inputStream = null;
	protected PortListenerServer portListener = null;
	protected CountDownLatch closed = new CountDownLatch(1);

	public PortListener(PortListenerServer portListener, Socket s) {
		this.portListener = portListener;
		this.socket = s;
	}

	private static void print(String text) {
		System.out.println(DateTools.encode(new Date(), DateTools.LEGACY_DATETIME_WITH_MS_FORMAT)+" [PortListener]: "+text);
		System.out.flush();
	}


	@Override
	public void run() {

		try {
			try {

				print("Starting to listen on socket.");

				this.inputStream = this.socket.getInputStream();
				this.socket.setSoTimeout(2000);
				BufferedReader in = new BufferedReader(new InputStreamReader(this.inputStream));
				while (!this.stop) {
					try {
						String line = in.readLine();
						if (line != null) {
							if (line.equals("quit")) {
								break;
							}
							System.out.println(line);
						}
					} catch(SocketTimeoutException ignore) { /* Ignore */
					}
				}
			} catch(Exception e) {
				if (!this.socket.isClosed()) {
					e.printStackTrace(System.out);
				}
			}

			print("Informing port listener server that this communicator has closed down");

			this.closed();
		} finally {

			print("Closed");

			closed.countDown();
		}
	}

	private void closed() {
		try {
			if (this.inputStream != null) {
				this.inputStream.close();
			}
			if (this.socket != null) {
				this.socket.close();
			}
		} catch(Exception e) { /* Ignore */
		}
		this.portListener.communicatorClosed(this);
	}


	public void close() {
		this.stop = true;
		try {
			if (!closed.await(60000L, TimeUnit.MILLISECONDS)) {
				throw new RuntimeException("Timeout while waiting for close.");
			}
		} catch (InterruptedException e) {
			throw new RuntimeException("Interrupted while waiting for close.", e);
		}
	}

}
