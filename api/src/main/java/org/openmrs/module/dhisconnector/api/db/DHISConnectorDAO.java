/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.dhisconnector.api.db;

import java.util.List;

import org.openmrs.Location;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.module.dhisconnector.DHISServerConfiguration;
import org.openmrs.module.dhisconnector.DHISServerReportsToReceive;
import org.openmrs.module.dhisconnector.LocationToOrgUnitMapping;
import org.openmrs.module.dhisconnector.ReportToDataSetMapping;
import org.openmrs.module.dhisconnector.api.model.DHISServerConfigurationDTO;

/**
 *  Database methods for {@link DHISConnectorService}.
 */
public interface DHISConnectorDAO {

	List<ReportToDataSetMapping> getAllReportToDataSetMappings();

	ReportToDataSetMapping getReportToDataSetMappingByUuid(String uuid);

	ReportToDataSetMapping getReportToDataSetMapping(Integer id);

	void deleteReportToDataSetMapping(ReportToDataSetMapping reportToDataSetMapping);

	void saveReportToDataSetMapping(ReportToDataSetMapping reportToDataSetMapping);

	List<LocationToOrgUnitMapping> getAllLocationToOrgUnitMappings();

	LocationToOrgUnitMapping getLocationToOrgUnitMappingByUuid(String uuid);

	LocationToOrgUnitMapping getLocationToOrgUnitMappingByOrgUnitUid(String orgUnitUid);
	
	LocationToOrgUnitMapping getLocationToOrgUnitMappingByOrgUnitUidAndServerUuid(String orgUnitUid, String serverUuid);
	
	LocationToOrgUnitMapping getLocationToOrgUnitMappingByLocationAndOrgUnitIdAndServerUuid(Location location, String orgUnitId, String serverUuid);

	void saveLocationToOrgUnitMapping(LocationToOrgUnitMapping locationToOrgUnitMapping);

	void deleteLocationToOrgUnitMappingsByLocation(Location location);
	
	void deleteLocationToOrgUnitMappingsByLocationAndServerUuidAndOrgUnitUid(Location location, String serverUuid, String orgUnitUid);

	SerializedObject getSerializedObjectByUuid(String uuid);

	void saveSerializedObject(SerializedObject serializedObject);

	void deleteSerializedObjectByUuid(String uuid);
	
	void saveDHISServerConfiguration(DHISServerConfiguration server);
	
	void saveDHISServerConfiguration(List<DHISServerConfiguration> servers);
	
	List<DHISServerConfiguration> getDHISServerConfigurations();
	
	void deleteDHISServerConfiguration(DHISServerConfiguration dHISServerConfiguration);
	
	void saveDHISServerReportsToReceive(List<DHISServerReportsToReceive> serverWithReportsToReceive);
	
	List<DHISServerReportsToReceive> getDHISServerReportsToReceive();
	
	DHISServerReportsToReceive getDHISServerReportsToReceiveByServerUuidAndReportUuid(String dhisServerUuid, String sespReportUuid);
	
	void deleteDHISServerReportsToReceiveByServerUuidAndReportUuid(String dhisServerUuid, String sespReportUuid);
	
	DHISServerConfiguration getDHISServerByUrl(String serverUrl);
	
	DHISServerConfiguration getDHISServerByUuid(String serverUuid);
	
	List<DHISServerReportsToReceive> getDHISServerReportsToReceiveByServerUuid(String dhisServerUuid);
	
	List<LocationToOrgUnitMapping> getLocationsToOrgUnitMappingByServerUuid(String serverUuid);
	
	void exportServerConfigurations(String filename);
	
}
