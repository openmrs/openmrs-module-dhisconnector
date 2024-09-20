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

import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisconnector.api.DHISConnectorService;
import org.openmrs.module.dhisconnector.api.model.DHISDataValue;
import org.openmrs.module.dhisconnector.api.model.DHISDataValueSet;
import org.openmrs.module.dhisconnector.api.model.DHISImportErrorSummary;
import org.openmrs.module.dhisconnector.api.model.DHISImportSummary;
import org.openmrs.module.dhisconnector.api.model.DHISServerConfigurationDTO;
import org.openmrs.module.dhisconnector.web.controller.DHISConnectorRestController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Retrievable;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + DHISConnectorRestController.DHISCONNECTOR_NAMESPACE
		+ "/dhisdatavaluesets", supportedClass = DHISDataValueSet.class, supportedOpenmrsVersions = { "1.8.*",
				"1.9.*, 1.10.*, 1.11.*", "1.12.*", "2.*" })
public class DHISDataValueSetsResource extends DataDelegatingCrudResource implements Retrievable {

	@Override
	public Object getByUniqueId(String s) {
		return null;
	}

	@Override
	protected void delete(Object o, String s, RequestContext requestContext) throws ResponseException {

	}

	@Override
	public DHISDataValueSet newDelegate() {
		return new DHISDataValueSet();
	}

	@Override
	public Object save(Object o) {
		//Object summary = Context.getService(DHISConnectorService.class).postDataValueSet((DHISDataValueSet) o);
		List<Object> summary = Context.getService(DHISConnectorService.class).postDataValueSetToMultiPleDhisServers((DHISDataValueSet) o);

		ObjectMapper mapper = new ObjectMapper();
//		SimpleObject ret = null;
//
//		try {
//			ret = SimpleObject.parseJson(mapper.writeValueAsString(summary));
//		} catch (Exception e) {
//			return null;
//		}
		
		SimpleObject reportSendResponse = null;
		
		try {
		reportSendResponse = new SimpleObject();
		
		for (int i = 0; i < summary.size(); i++) {
			reportSendResponse.add(Integer.toString(i), SimpleObject.parseJson(mapper.writeValueAsString(summary.get(i))));
		}
		}catch(Exception e) {
			
		}

		return reportSendResponse;

	}

	@PropertySetter("dataValues")
	public static void setDataValues(DHISDataValueSet dvs, Object value) {
		ArrayList<LinkedHashMap<String, String>> dataElements = (ArrayList<LinkedHashMap<String, String>>) value;

		for (LinkedHashMap<String, String> dataElement : dataElements) {
			Iterator it = dataElement.entrySet().iterator();

			DHISDataValue dv = new DHISDataValue();

			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();

				if (pair.getKey().equals("categoryOptionCombo")) {
					dv.setCategoryOptionCombo((String) pair.getValue());
				} else if (pair.getKey().equals("value")) {
					dv.setValue(pair.getValue().toString());
				} else if (pair.getKey().equals("comment")) {
					dv.setComment((String) pair.getValue());
				} else if (pair.getKey().equals("dataElement")) {
					dv.setDataElement((String) pair.getValue());
				}
			}

			dvs.addDataValue(dv);
		}
	}
	
	@PropertySetter("dhisServers")
	public static void setDhisServers(DHISDataValueSet dvs, Object value) {
		ArrayList<LinkedHashMap<String, String>> dataServers = (ArrayList<LinkedHashMap<String, String>>) value;

		for (LinkedHashMap<String, String> serverElement : dataServers) {
			Iterator it = serverElement.entrySet().iterator();

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

			dvs.addDhisServer(server);
		}
	}

	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("dataSet");
		description.addProperty("period");
		description.addProperty("orgUnit");
		description.addProperty("reportName");
		description.addProperty("dataValues");
		description.addProperty("dhisServers");
		return description;
	}

	@Override
	public void purge(Object o, RequestContext requestContext) throws ResponseException {

	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("dataSet");
		description.addProperty("period");
		description.addProperty("orgUnit");
		description.addProperty("reportName");
		description.addProperty("dataValues");
		description.addProperty("dhisServers");
		return description;
	}
}
