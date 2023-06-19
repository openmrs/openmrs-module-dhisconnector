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

import org.openmrs.api.context.Context;
import org.openmrs.module.dhisconnector.LocationToOrgUnitMapping;
import org.openmrs.module.dhisconnector.api.DHISConnectorService;
import org.openmrs.module.dhisconnector.api.model.DHISDataSet;
import org.openmrs.module.dhisconnector.web.controller.DHISConnectorRestController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Retrievable;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@SuppressWarnings({ "rawtypes" })
@Resource(name = RestConstants.VERSION_1 + DHISConnectorRestController.DHISCONNECTOR_NAMESPACE
		+ "/dhismonthcheck", supportedClass = DHISDataSet.class, supportedOpenmrsVersions = { "1.8.*",
				"1.9.*, 1.10.*, 1.11.*", "1.12.*", "2.*" })
public class DHISMonthCheckResource extends DataDelegatingCrudResource implements Retrievable {
	@Override
	public SimpleObject search(RequestContext context) throws ResponseException {
		String dhisreportdataset = context.getParameter("dhisreportdataset");
		String periodtype = context.getParameter("periodtype");
		String reportperiod = context.getParameter("reportperiod");
		String datasetid = context.getParameter("datasetid");

		boolean monthOpenToSendReport = Context.getService(DHISConnectorService.class)
				.isDHISMonthOpenToSendReport(dhisreportdataset, periodtype, reportperiod, datasetid);

		SimpleObject simpleObject = new SimpleObject();
		simpleObject.add("monthOpen", monthOpenToSendReport);
		return simpleObject;
	}

	@Override
	public LocationToOrgUnitMapping getByUniqueId(String s) {
		return Context.getService(DHISConnectorService.class).getLocationToOrgUnitMappingByUuid(s);
	}

	/**
	 * Overridden to Permanently delete a {@link LocationToOrgUnitMapping} instead
	 * of retiring it, no support for voiding one is included so-far
	 */
	@Override
	protected void delete(Object o, String s, RequestContext requestContext) throws ResponseException {
	}

	@Override
	public void purge(Object o, RequestContext requestContext) throws ResponseException {
	}

	@Override
	public LocationToOrgUnitMapping newDelegate() {
		return null;
	}

	@Override
	public Object save(Object o) {
		return null;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("id");
		description.addProperty("uuid");

		return description;
	}
}
