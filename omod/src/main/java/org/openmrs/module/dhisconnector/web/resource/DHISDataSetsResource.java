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

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisconnector.api.DHISConnectorService;
import org.openmrs.module.dhisconnector.api.model.DHISDataSet;
import org.openmrs.module.dhisconnector.web.controller.DHISConnectorRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Retrievable;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + DHISConnectorRestController.DHISCONNECTOR_NAMESPACE
		+ "/dhisdatasets", supportedClass = DHISDataSet.class, supportedOpenmrsVersions = { "1.8.*", "1.9.*, 1.10.*, 1.11.*",
		"1.12.*", "2.*" })
public class DHISDataSetsResource extends DataDelegatingCrudResource implements Retrievable {

	public static final String DATASETS_PATH = "/api/dataSets";

	public static final String JSON_SUFFIX = ".json";

	public static final String NO_PAGING_PARAM = "?paging=false";

	private static final String NO_PAGING_IDENTIFIABLE_PARAM = "&fields=name,displayName,code,id,periodType,dataElements[id,name,displayName,code],dataSetElements[id,dataElement[id,name,displayName,code],dataSet],organisationUnits[id,name]";

    private static final String DE_IDENTIFIABLE_PARAM = "?fields=name,displayName,code,id,periodType,dataElements[id,name,displayName,code],dataSetElements[id,dataElement[id,name,displayName,code],dataSet],organisationUnits[id,name]";

	@Override
	public DHISDataSet getByUniqueId(String s) {

		ObjectMapper mapper = new ObjectMapper();

		String jsonResponse = Context.getService(DHISConnectorService.class)
				.getDataFromDHISEndpoint(DATASETS_PATH + "/" + s + JSON_SUFFIX + DE_IDENTIFIABLE_PARAM);

		DHISDataSet ret = null;

		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			if(StringUtils.isNotBlank(jsonResponse))
				ret = mapper.readValue(jsonResponse, DHISDataSet.class);
		}
		catch (Exception e) {
			log.error(e.getMessage());
		}

		return ret;

	}

	@Override
	protected void delete(Object o, String s, RequestContext requestContext) throws ResponseException {
		// not supporting
	}

	@Override
	public Object newDelegate() {
		return null;
	}

	@Override
	public Object save(Object o) {
		// Not used
		return null;
	}

	@Override
	public void purge(Object o, RequestContext requestContext) throws ResponseException {
		// not supporting
	}

	protected NeedsPaging<DHISDataSet> doGetAll(RequestContext context) {
		List<DHISDataSet> dataSets = new ArrayList<DHISDataSet>();

		ObjectMapper mapper = new ObjectMapper();
		String jsonResponse = new String();
		JsonNode node;

		jsonResponse = Context.getService(DHISConnectorService.class)
				.getDataFromDHISEndpoint(DATASETS_PATH + JSON_SUFFIX + NO_PAGING_PARAM + NO_PAGING_IDENTIFIABLE_PARAM);

		try {
			mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			if(StringUtils.isNotBlank(jsonResponse)) {
				node = mapper.readTree(jsonResponse);
				dataSets = Arrays.asList(mapper.readValue(node.get("dataSets").toString(), DHISDataSet[].class));
			}
		}
		catch (Exception ex) {
			System.out.print(ex.getMessage());
		}

		return new NeedsPaging<DHISDataSet>(dataSets, context);
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();

		if (representation instanceof FullRepresentation) {
			description.addProperty("id");
			description.addProperty("name");
			description.addProperty("code");
			description.addProperty("periodType");
			description.addProperty("code");
			description.addProperty("categoryCombo");
			description.addProperty("dataElements");
			description.addProperty("dataSetElements");
			description.addProperty("organisationUnits");
		} else {
			description.addProperty("id");
			description.addProperty("name");
			description.addProperty("code");
			description.addProperty("created");
			description.addProperty("lastUpdated");
			description.addProperty("href");
			description.addProperty("organisationUnits");
		}

		return description;
	}
}
