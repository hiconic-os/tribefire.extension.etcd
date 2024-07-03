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

import com.braintribe.model.accessdeployment.IncrementalAccess;
import com.braintribe.model.generic.reflection.EntityType;
import com.braintribe.model.generic.reflection.EntityTypes;

public interface EtcdAccess extends IncrementalAccess {

	final EntityType<EtcdAccess> T = EntityTypes.T(EtcdAccess.class);

	public final static String connector = "connector";
	public final static String projectName = "projectName";

	void setConnector(EtcdConnector connector);
	EtcdConnector getConnector();

	void setProjectName(String projectName);
	String getProjectName();

}
