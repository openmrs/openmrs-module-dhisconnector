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
package org.openmrs.module.dhisconnector.web.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisconnector.DHISServerConfiguration;
import org.openmrs.module.dhisconnector.LocationToOrgUnitMapping;
import org.openmrs.module.dhisconnector.api.DHISConnectorService;
import org.openmrs.module.dhisconnector.api.model.DHISMapping;
import org.openmrs.module.dhisconnector.api.model.DHISMappingElement;
import org.openmrs.module.dhisconnector.api.model.DHISServer;
import org.openmrs.module.dhisconnector.api.model.DHISServerConfigurationDTO;
import org.openmrs.module.dhisconnector.web.controller.DHISConnectorRestController;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.Retrievable;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Resource(name = RestConstants.VERSION_1 + DHISConnectorRestController.DHISCONNECTOR_NAMESPACE
		+ "/servers", supportedClass = DHISServer.class, supportedOpenmrsVersions = { "1.8.*",
				"1.9.*, 1.10.*, 1.11.*", "1.12.*", "2.*" })
public class DHISServersConigurationResource extends DataDelegatingCrudResource implements Retrievable {

	@Override
	public DHISServer getByUniqueId(String s) {
		List<DHISServerConfiguration> all = Context.getService(DHISConnectorService.class).getDHISServerConfigurations();
		
		DHISServer server = new DHISServer();
		
		for (DHISServerConfiguration dhisServerConfiguration : all) {
			DHISServerConfigurationDTO dto = new DHISServerConfigurationDTO();
			dto.setPassword(dhisServerConfiguration.getPassword());
			dto.setUrl(dhisServerConfiguration.getUrl());
			dto.setUser(dhisServerConfiguration.getUser());
			
			server.addConfiguration(dto);
		}
		return server;
	}

	/**
	 * Overridden to Permanently delete a {@link DHISMapping} instead of
	 * retiring it, no support for voiding one is included so-far
	 */
	@Override
	protected void delete(Object o, String s, RequestContext requestContext) throws ResponseException {
		Context.getService(DHISConnectorService.class).permanentlyDHISServerConfiguration(transformDTOtoEntity((DHISServerConfigurationDTO) o));
	}

	@Override
	public void purge(Object o, RequestContext requestContext) throws ResponseException {
	}

	/**
	 * Annotated setter for elements TODO: Figure out the correct way to do this
	 *
	 * @param dm
	 * @param value
	 */
	@PropertySetter("configurations")
	public static void setConfigurations(DHISServer dm, Object value) {
		ArrayList<LinkedHashMap<String, String>> mappings = (ArrayList<LinkedHashMap<String, String>>) value;
		for (LinkedHashMap<String, String> mapping : mappings) {
			Iterator it = mapping.entrySet().iterator();

			DHISServerConfigurationDTO server = new DHISServerConfigurationDTO();

			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();

				if (pair.getKey().equals("url")) {
					server.setUrl((String) pair.getValue());
				} else if (pair.getKey().equals("user")) {
					server.setUser((String) pair.getValue());
				} else if (pair.getKey().equals("password")) {
					server.setPassword((String) pair.getValue());
				}

			}
			dm.addConfiguration(server);
		}
	}

	@Override
	public DHISServer newDelegate() {
		return new DHISServer();
	}

	@Override
	public Object save(Object o) {
        return null; 
	}

	public DelegatingResourceDescription getCreatableProperties() {

		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("configurations");

		return description;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("configurations");

		return description;
	}
	
	
	private List<DHISServerConfiguration> transformDTOstoEntities(DHISServer server){
		
		List<DHISServerConfiguration> servers = new ArrayList<>();
		
		for (DHISServerConfigurationDTO dto : server.getConfigurations()) {
			
			servers.add( new DHISServerConfiguration(dto.getUrl(), dto.getUser(), dto.getPassword()));
		}
			
		return servers;

	}
	
	private DHISServerConfiguration transformDTOtoEntity(DHISServerConfigurationDTO server) {
			
	    return new DHISServerConfiguration(server.getUrl(), null, null);
		
	}

}
