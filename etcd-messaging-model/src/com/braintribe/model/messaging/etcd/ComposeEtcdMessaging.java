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

import com.braintribe.model.generic.annotation.Initializer;
import com.braintribe.model.generic.annotation.meta.Mandatory;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;
import com.braintribe.model.plugin.Plugable;

/**
 * <p>
 * A {@link Plugable} denotation type holding the basic configuration properties of a Etcd connection factory.
 * 
 */
public interface ComposeEtcdMessaging extends EtcdMessaging {

	final EntityType<ComposeEtcdMessaging> T = EntityTypes.T(ComposeEtcdMessaging.class);

	public final static String authority = "authority";
	public final static String authorityPrefix = "authorityPrefix";
	public final static String certificate = "certificate";

	@Mandatory
	String getAuthority();
	void setAuthority(String authority);

	// prefix for authority - used because a wildcard is not working - should define the authority more in detail; in
	// general it can be anything - not sure how it will behave in the the future so let this configurable.
	// e.g. TCP portals 'portal2335-0.astute-etcd-58.2678148991.composedb.com:19714' become
	// 'xxx.astute-etcd-58.2678148991.composedb.com'
	@Mandatory
	@Initializer("'xxx.'")
	String getAuthorityPrefix();
	void setAuthorityPrefix(String authorityPrefix);

	@Mandatory
	String getCertificate();
	void setCertificate(String certificate);
}
