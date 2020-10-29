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
import org.openmrs.module.dhisconnector.api.DHISConnectorService;
import org.openmrs.module.dhisconnector.api.model.DHISCategoryCombo;
import org.openmrs.module.dhisconnector.api.model.DHISOrganisationUnit;
import org.openmrs.module.dhisconnector.web.controller.DHISConnectorRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Retrievable;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

import java.util.List;

@Resource(name = RestConstants.VERSION_1 + DHISConnectorRestController.DHISCONNECTOR_NAMESPACE
		+ "/orgunits", supportedClass = DHISOrganisationUnit.class, supportedOpenmrsVersions = { "1.8.*",
		"1.9.*, 1.10.*, 1.11.*", "1.12.*", "2.*" })
public class DHISOrganisationUnitResource extends DataDelegatingCrudResource implements Retrievable {

	protected NeedsPaging<DHISOrganisationUnit> doGetAll(RequestContext context) {
		List<DHISOrganisationUnit> orgUnits = Context.getService(DHISConnectorService.class).getDHISOrgUnits();
		return new NeedsPaging<DHISOrganisationUnit>(orgUnits, context);
	}

	@Override
	public DHISCategoryCombo getByUniqueId(String s) {
		// Not used
		return null;
	}

	@Override
	protected void delete(Object o, String s, RequestContext requestContext) throws ResponseException {
		// Not used
	}

	@Override
	public Object newDelegate() {
		return null;
	}

	@Override
	public Object save(Object o) {
		return null;
	}

	@Override
	public void purge(Object o, RequestContext requestContext) throws ResponseException {

	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("id");
		description.addProperty("created");
		description.addProperty("name");
		description.addProperty("lastUpdated");
		description.addProperty("code");
		return description;
	}
}
