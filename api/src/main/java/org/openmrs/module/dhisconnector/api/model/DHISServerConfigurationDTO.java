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
		"url",
		"user",
		"password",
		"serveruuid"
})
public class DHISServerConfigurationDTO {

	@JsonProperty("url")
	private String url;

	@JsonProperty("user")
	private String user;

	@JsonProperty("password")
	private String password;
	
	@JsonProperty("serveruuid")
	private String serveruuid;
	
	
	public DHISServerConfigurationDTO() {
	}
	
	public DHISServerConfigurationDTO(String url, String user, String password, String serveruuid) {
		this.url = url;
		this.user = user;
		this.password = password;
		this.serveruuid = serveruuid;
	}

	@JsonProperty("url")
	public String getUrl() {
		return url;
	}

	@JsonProperty("url")
	public void setUrl(String url) {
		this.url = url;
	}

	@JsonProperty("user")
	public String getUser() {
		return user;
	}

	@JsonProperty("user")
	public void setUser(String user) {
		this.user = user;
	}
	
	@JsonProperty("password")
	public String getPassword() {
		return password;
	}

	@JsonProperty("password")
	public void setPassword(String password) {
		this.password = password;
	}

	@JsonProperty("serveruuid")
	public String getServeruuid() {
		return serveruuid;
	}

	@JsonProperty("serveruuid")
	public void setServeruuid(String serveruuid) {
		this.serveruuid = serveruuid;
	}
	
	
}
