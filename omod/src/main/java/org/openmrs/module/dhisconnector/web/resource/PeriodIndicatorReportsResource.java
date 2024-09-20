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
import java.util.List;
import java.util.Map;

import org.openmrs.api.context.Context;
import org.openmrs.module.dhisconnector.api.DHISConnectorService;
import org.openmrs.module.dhisconnector.web.controller.DHISConnectorRestController;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.CohortIndicatorDataSetDefinition.CohortIndicatorAndDimensionColumn;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimpleIndicatorDataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SimpleIndicatorDataSetDefinition.SimpleIndicatorColumn;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.openmrs.module.reporting.report.definition.service.ReportDefinitionService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.Retrievable;
import org.openmrs.module.webservices.rest.web.resource.impl.AlreadyPaged;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + DHISConnectorRestController.DHISCONNECTOR_NAMESPACE
		+ "/periodindicatorreports", supportedClass = PeriodIndicatorReportDefinition.class, supportedOpenmrsVersions = {
				"1.8.*", "1.9.*, 1.10.*, 1.11.*", "1.12.*", "2.*" })
public class PeriodIndicatorReportsResource extends DataDelegatingCrudResource implements Retrievable {

	@Override
	public Object getByUniqueId(String s) {
		return Context.getService(ReportDefinitionService.class).getDefinitionByUuid(s);
	}

	protected NeedsPaging<PeriodIndicatorReportDefinition> doGetAll(RequestContext context) {

		List<ReportDefinition> all = Context.getService(ReportDefinitionService.class).getAllDefinitions(false);

		List<PeriodIndicatorReportDefinition> pireports = new ArrayList<PeriodIndicatorReportDefinition>();

		for (ReportDefinition r : all) {
			if (r instanceof PeriodIndicatorReportDefinition) {
				pireports.add((PeriodIndicatorReportDefinition) r);
			}
		}

		return new NeedsPaging<PeriodIndicatorReportDefinition>(pireports, context);
	}

	// TODO do this properly
	@Override
	protected PageableResult doSearch(RequestContext context) {
		String query = context.getParameter("q");
		if (query == null) {
			return new EmptySearchResult();
		}

		DHISConnectorService dcs = Context.getService(DHISConnectorService.class);

		List<PeriodIndicatorReportDefinition> reports = dcs.getReportWithMappings(dcs.getMappings());

		return new AlreadyPaged<PeriodIndicatorReportDefinition>(context, reports, false);

	}

	@Override
	protected void delete(Object o, String s, RequestContext requestContext) throws ResponseException {
		// not supporting delete
	}

	@Override
	public Object newDelegate() {
		return new PeriodIndicatorReportDefinition();
	}

	@Override
	public Object save(Object o) {
		return null; // not supporting save
	}

	@Override
	public void purge(Object o, RequestContext requestContext) throws ResponseException {
		// not supporting purge
	}

	public SimpleObject getSimpleColumns(PeriodIndicatorReportDefinition pird) {
		SimpleObject simpleColumns = new SimpleObject();

		if (pird.getDataSetDefinitions().get("defaultDataSet") != null && (pird
				.getDataSetDefinitions().get("defaultDataSet")).getParameterizable() != null) {

			List<CohortIndicatorDataSetDefinition.CohortIndicatorAndDimensionColumn> cols = ((CohortIndicatorDataSetDefinition) (pird
					.getDataSetDefinitions().get("defaultDataSet")).getParameterizable()).getColumns();

			for (CohortIndicatorDataSetDefinition.CohortIndicatorAndDimensionColumn cd : cols) {
				simpleColumns.add(cd.getName(), cd.getDimensionOptions());
			}
		} else {
			Map<String, Mapped<? extends DataSetDefinition>> dataSetDefinitions = pird.getDataSetDefinitions();

			for (Mapped<? extends DataSetDefinition> mapped : dataSetDefinitions.values()) {

				if (mapped.getParameterizable() instanceof CohortIndicatorDataSetDefinition) {

					List<CohortIndicatorAndDimensionColumn> cols2 = ((CohortIndicatorDataSetDefinition) mapped
							.getParameterizable()).getColumns();

					for (CohortIndicatorAndDimensionColumn cd : cols2) {
						simpleColumns.add(cd.getName(), cd.getDimensionOptions());
					}
				}else if (mapped.getParameterizable() instanceof SimpleIndicatorDataSetDefinition) {

					List<SimpleIndicatorColumn> cols2 = ((SimpleIndicatorDataSetDefinition) mapped
							.getParameterizable()).getColumns();

					for (SimpleIndicatorColumn cd : cols2) {
						simpleColumns.add(cd.getName(), null);
					}
				}
			}
		}

		return simpleColumns;
	}

	public SimpleObject getSchema(PeriodIndicatorReportDefinition pird) {
		SimpleObject ret = new SimpleObject();
		ret.add("columns", getSimpleColumns(pird));
		return ret;
	}

	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("uuid");
		description.addProperty("name");
		description.addProperty("schema", findMethod("getSchema"));
		return description;
	}
}
