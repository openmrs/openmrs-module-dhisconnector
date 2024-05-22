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
package org.openmrs.module.dhisconnector.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.openmrs.Location;
import org.openmrs.api.OpenmrsService;
import org.openmrs.module.dhisconnector.DHISServerConfiguration;
import org.openmrs.module.dhisconnector.DHISServerReportsToReceive;
import org.openmrs.module.dhisconnector.LocationToOrgUnitMapping;
import org.openmrs.module.dhisconnector.ReportToDataSetMapping;
import org.openmrs.module.dhisconnector.api.model.DHISDataSet;
import org.openmrs.module.dhisconnector.api.model.DHISDataValueSet;
import org.openmrs.module.dhisconnector.api.model.DHISMapping;
import org.openmrs.module.dhisconnector.api.model.DHISOrganisationUnit;
import org.openmrs.module.dhisconnector.api.model.DHISServerConfigurationDTO;
import org.openmrs.module.dhisconnector.api.model.DHISImportSummary;
import org.openmrs.module.reporting.report.definition.PeriodIndicatorReportDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * This service exposes module's core functionality. It is a Spring managed bean
 * which is configured in moduleApplicationContext.xml. It can be accessed only
 * via Context:<br>
 * <code>
 * Context.getService(DHISConnectorService.class).someMethod();
 * </code>
 *
 * @see org.openmrs.api.context.Context
 */
@Transactional
public interface DHISConnectorService extends OpenmrsService {

	/**
	 * Queries a DHIS endoint and returns the result as JSON
	 *
	 * @param endpoint The endpoint to query
	 * @return Result as JSON string
	 */
	public String getDataFromDHISEndpoint(String endpoint);

	public String postDataToDHISEndpoint(String endpoint, String jsonPayload);

	/**
	 * Tests to check if the given DHIS server details are correct
	 *
	 * @param url  The URL of the server
	 * @param user The login username
	 * @param pass The login password
	 * @return True if the details are correct, false otherwise
	 */
	public boolean testDHISServerDetails(String url, String user, String pass);

	public Object saveMapping(DHISMapping mapping);

	public Object postDataValueSet(DHISDataValueSet dataValueSet);

	public List<DHISMapping> getMappings();

	public List<PeriodIndicatorReportDefinition> getReportWithMappings(List<DHISMapping> mappings);

	public List<DHISOrganisationUnit> getDHISOrgUnits();
	
	public List<DHISOrganisationUnit> getDHISOrgUnitsByServer(DHISServerConfiguration server);

	public DHISDataSet getDHISDataSetById(String id);

	public String uploadMappings(MultipartFile mapping);

//	public String importMappings(MultipartFile mapping, boolean shouldReplaceMetadata) throws IOException;
//	
//	public String[] exportMappings(String[] selectedMappings, boolean shouldIncludeMetadata) throws IOException;

	public String[] exportSelectedMappings(String[] selectedMappings);

	public boolean dhis2BackupExists();

	public String getLastSyncedAt();

	public String getDHIS2APIBackupPath();

	public String uploadDHIS2APIBackup(MultipartFile dhis2APIBackup);

	public DHISMapping getMapping(String s);

	public boolean permanentlyDeleteMapping(DHISMapping mapping);

	String getAdxFromDxf(DHISDataValueSet dataValueSet);

	Integer getNumberOfFailedDataPosts();

	void postPreviouslyFailedData();

	List<ReportToDataSetMapping> getAllReportToDataSetMappings();

	ReportToDataSetMapping getReportToDataSetMappingByUuid(String uuid);

	ReportToDataSetMapping getReportToDataSetMapping(Integer id);

	void deleteReportToDataSetMapping(ReportToDataSetMapping reportToDataSetMapping);

	void saveReportToDataSetMapping(ReportToDataSetMapping reportToDataSetMapping);

	void deleteReportToDataSetMapping(String reportToDataSetMappingUuid);

	List<String> runAndPushReportToDHIS(ReportToDataSetMapping reportToDatasetMapping);

	ArrayList<List<String>> runAllAutomatedReportsAndPostToDHIS();

	String transformToDHISPeriod(Calendar startDate, Calendar endDate, String periodType, Date lastRun);

	List<LocationToOrgUnitMapping> getAllLocationToOrgUnitMappings();

	LocationToOrgUnitMapping getLocationToOrgUnitMappingByUuid(String uuid);

	LocationToOrgUnitMapping getLocationToOrgUnitMappingByOrgUnitUid(String orgUnitUid);
	
	LocationToOrgUnitMapping getLocationToOrgUnitMappingByLocationAndOrgUnitIdAndServerUuid(Location location, String orgUnitId, String serverUuid);

	void saveLocationToOrgUnitMapping(LocationToOrgUnitMapping locationToOrgUnitMapping);

	void deleteLocationToOrgUnitMappingsByLocation(Location location);

	Object reSendReportToDHIS(String reportName);

	List<String> getFileNameOfFailedDataPosts();

	boolean isDHISMonthOpenToSendReport(String dhisDatasetListName, String periodType, String periodValue,
			String datasetid);
	
	void saveDHISServerConfiguration(DHISServerConfiguration server);
	
	List<DHISServerConfiguration> getDHISServerConfigurations();
	
	public void permanentlyDHISServerConfiguration(DHISServerConfiguration server);
	
	void saveDHISServerReportsToReceive(String[] reports);
	
	List<DHISServerReportsToReceive> getDHISServerReportsToReceive();
	
	DHISServerReportsToReceive getDHISServerReportsToReceiveByServerUuidAndReportUuid(String dhisServerUuid, String sespReportUuid);
	
	DHISServerConfiguration getDHISServerByUrl(String serverUrl);
	
	public String postDataToMultipleDHISEndpoint(String endpoint, String jsonPayload, DHISServerConfigurationDTO dhisServer);

	public List<Object> postDataValueSetToMultiPleDhisServers(DHISDataValueSet dataValueSet);
	
	DHISServerConfiguration getDHISServerByUuid(String serverUuid);
	
	void deleteLocationToOrgUnitMappingsByLocationAndServerUuidAndOrgUnitUid(Location location, String serverUuid, String orgUnitUid);
	
	void deleteDHISServerReportsToReceiveByServerUuidAndReportUuid(String dhisServerUuid, String sespReportUuid);
	
	void verifyDHISServerReportsToReceiveToBeDeleted(String[] payload, List<DHISServerReportsToReceive> serversWithReports);
	
	List<DHISServerReportsToReceive> getDHISServerReportsToReceiveByServerUuid(String dhisServerUuid);
	
	LocationToOrgUnitMapping getLocationToOrgUnitMappingByOrgUnitUidAndServerUuid(String orgUnitUid, String serverUuid);
	
	List<LocationToOrgUnitMapping> getLocationsToOrgUnitMappingByServerUuid(String serverUuid);
}
