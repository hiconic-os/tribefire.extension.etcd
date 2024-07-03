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

import com.braintribe.model.generic.GenericEntity;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface EtcdMessageEnvelope extends GenericEntity {

	final EntityType<EtcdMessageEnvelope> T = EntityTypes.T(EtcdMessageEnvelope.class);

	public final static String body = "body";
	public final static String messageId = "messageId";
	public final static String addresseeNodeId = "addresseeNodeId";
	public final static String addresseeAppId = "addresseeAppId";
	public final static String expiration = "expiration";

	String getBody();
	void setBody(String body);

	String getMessageId();
	void setMessageId(String messageId);
	
	String getAddresseeNodeId();
	void setAddresseeNodeId(String addresseeNodeId);
	
	String getAddresseeAppId();
	void setAddresseeAppId(String addresseeAppId);
	
	Long getExpiration();
	void setExpiration(Long expiration);
	
	
}
