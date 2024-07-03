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
package com.braintribe.model.etcd.deployment;

import java.util.List;

import com.braintribe.model.deployment.connector.Connector;
import com.braintribe.model.generic.annotation.SelectiveInformation;
import com.braintribe.model.generic.annotation.meta.Confidential;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.annotation.meta.MinLength;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

/**
 * 
 * Base etcd connector
 *
 */
@SelectiveInformation("Etcd Connector: ${name}")
public interface EtcdConnector extends Connector {

	final EntityType<EtcdConnector> T = EntityTypes.T(EtcdConnector.class);

	public final static String endpointUrls = "endpointUrls";
	public final static String username = "username";
	public final static String password = "password";

	List<String> getEndpointUrls();
	@Mandatory
	@MinLength(1)
	void setEndpointUrls(List<String> endpointUrls);

	String getUsername();
	void setUsername(String username);

	@Confidential
	String getPassword();
	void setPassword(String password);

}
