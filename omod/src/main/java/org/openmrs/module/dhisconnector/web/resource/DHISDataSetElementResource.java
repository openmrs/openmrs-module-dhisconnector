package org.openmrs.module.dhisconnector.web.resource;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisconnector.api.DHISConnectorService;
import org.openmrs.module.dhisconnector.api.model.DHISDataSetElement;
import org.openmrs.module.dhisconnector.web.controller.DHISConnectorRestController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.Retrievable;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + DHISConnectorRestController.DHISCONNECTOR_NAMESPACE
		+ "/dhisdatasetelements", supportedClass = DHISDataSetElement.class, supportedOpenmrsVersions = { "1.8.*",
				"1.9.*, 1.10.*, 1.11.*", "1.12.*", "2.*" })
public class DHISDataSetElementResource extends DataDelegatingCrudResource implements Retrievable {
	public static final String DATASETELEMENTS_PATH = "/api/dataSetElements";

	private static final String CO_FIELDS_PARAM = ".json?paging=false&fields=:identifiable,dataElement[:identifiable],categoryCombo[:identifiable],dataSet[:identifiable]";

	@Override
	public DHISDataSetElement getByUniqueId(String s) {
		ObjectMapper mapper = new ObjectMapper();

		String jsonResponse = Context.getService(DHISConnectorService.class).getDataFromDHISEndpoint(
				DATASETELEMENTS_PATH + "/" + s + CO_FIELDS_PARAM);

		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		DHISDataSetElement ret = null;

		try {
			if(StringUtils.isNotBlank(jsonResponse))
				ret = mapper.readValue(jsonResponse, DHISDataSetElement.class);
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return ret;
	}

	@Override
	protected void delete(Object o, String s, RequestContext requestContext) throws ResponseException {

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
		description.addProperty("dataElement", Representation.REF);
		description.addProperty("dataSet", Representation.REF);
		return description;
	}
}
