/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.dhisconnector.api.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
		"configurations"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class DHISServer {

	@JsonProperty("configurations")
	private List<DHISServerConfigurationDTO> configurations = new ArrayList<DHISServerConfigurationDTO>();

	/**
	 * @return The configurations
	 */
	@JsonProperty("configurations")
	public List<DHISServerConfigurationDTO> getConfigurations() {
		return configurations;
	}

	/**
	 * @param configurations The configurations
	 */
	@JsonProperty("configurations")
	public void setElements(List<DHISServerConfigurationDTO> configurations) {
		this.configurations = configurations;
	}

	public void addConfiguration(DHISServerConfigurationDTO el) {
		if (this.configurations == null) {
			this.configurations = new ArrayList<DHISServerConfigurationDTO>();
		}

		this.configurations.add(el);
	}
}
