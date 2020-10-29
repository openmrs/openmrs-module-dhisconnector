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

import org.openmrs.api.context.Context;
import org.openmrs.module.dhisconnector.api.DHISConnectorService;
import org.openmrs.module.dhisconnector.api.model.DHISMapping;
import org.openmrs.module.dhisconnector.api.model.DHISMappingElement;
import org.openmrs.module.dhisconnector.web.controller.DHISConnectorRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Retrievable;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Resource(name = RestConstants.VERSION_1 + DHISConnectorRestController.DHISCONNECTOR_NAMESPACE
		+ "/mappings", supportedClass = DHISMapping.class, supportedOpenmrsVersions = { "1.8.*",
				"1.9.*, 1.10.*, 1.11.*", "1.12.*", "2.*" })
public class MappingResource extends DataDelegatingCrudResource implements Retrievable {

	@Override
	public DHISMapping getByUniqueId(String s) {
		return Context.getService(DHISConnectorService.class).getMapping(s);
	}

	/**
	 * Overridden to Permanently delete a {@link DHISMapping} instead of
	 * retiring it, no support for voiding one is included so-far
	 */
	@Override
	protected void delete(Object o, String s, RequestContext requestContext) throws ResponseException {
		if(Context.getService(DHISConnectorService.class).permanentlyDeleteMapping((DHISMapping) o)) {
			//TODO anything to be done here?
		}
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
	@PropertySetter("elements")
	public static void setElements(DHISMapping dm, Object value) {
		ArrayList<LinkedHashMap<String, String>> mappings = (ArrayList<LinkedHashMap<String, String>>) value;
		List<DHISMappingElement> elements = new ArrayList<DHISMappingElement>();

		for (LinkedHashMap<String, String> mapping : mappings) {
			Iterator it = mapping.entrySet().iterator();

			DHISMappingElement dme = new DHISMappingElement();

			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry) it.next();

				if (pair.getKey().equals("indicator")) {
					dme.setIndicator((String) pair.getValue());
				} else if (pair.getKey().equals("dataElement")) {
					dme.setDataElement((String) pair.getValue());
				} else if (pair.getKey().equals("comboOption")) {
					dme.setComboOption((String) pair.getValue());
				}

			}
			dm.addElement(dme);
		}
	}

	protected NeedsPaging<DHISMapping> doGetAll(RequestContext context) {

		List<DHISMapping> mappings = Context.getService(DHISConnectorService.class).getMappings();

		if (mappings == null)
			mappings = new ArrayList<DHISMapping>();

		return new NeedsPaging<DHISMapping>(mappings, context);
	}

	@Override
	public DHISMapping newDelegate() {
		return new DHISMapping();
	}

	@Override
	public Object save(Object o) {
		return Context.getService(DHISConnectorService.class).saveMapping((DHISMapping) o);
	}

	public DelegatingResourceDescription getCreatableProperties() {

		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("name");
		description.addProperty("created");
		description.addProperty("dataSetUID");
		description.addProperty("periodIndicatorReportGUID");
		description.addProperty("periodType");
		description.addProperty("elements");

		return description;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("name");
		description.addProperty("created");
		description.addProperty("dataSetUID");
		description.addProperty("periodIndicatorReportGUID");
		description.addProperty("periodType");
		description.addProperty("elements");

		return description;
	}
}
