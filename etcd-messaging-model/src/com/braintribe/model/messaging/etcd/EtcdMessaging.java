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
package com.braintribe.model.messaging.etcd;

import java.util.List;

import com.braintribe.model.generic.annotation.meta.Confidential;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.messaging.expert.Messaging;
import com.braintribe.model.plugin.Plugable;

/**
 * <p>
 * A {@link Plugable} denotation type holding the basic configuration properties of a Etcd connection factory.
 * 
 */
public interface EtcdMessaging extends Messaging {

	final EntityType<EtcdMessaging> T = EntityTypes.T(EtcdMessaging.class);

	public final static String project = "project";
	public final static String endpointUrls = "endpointUrls";
	public final static String username = "username";
	public final static String password = "password";

	String getProject();
	void setProject(String project);

	List<String> getEndpointUrls();
	void setEndpointUrls(List<String> endpointUrls);

	String getUsername();
	void setUsername(String username);

	@Confidential
	String getPassword();
	void setPassword(String password);
}
