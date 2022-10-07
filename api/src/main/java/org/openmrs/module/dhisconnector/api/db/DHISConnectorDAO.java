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
import org.openmrs.module.dhisconnector.LocationToOrgUnitMapping;
import org.openmrs.module.dhisconnector.ReportToDataSetMapping;

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

	void saveLocationToOrgUnitMapping(LocationToOrgUnitMapping locationToOrgUnitMapping);

	void deleteLocationToOrgUnitMappingsByLocation(Location location);

	SerializedObject getSerializedObjectByUuid(String uuid);

	void saveSerializedObject(SerializedObject serializedObject);

	void deleteSerializedObjectByUuid(String uuid);
}
