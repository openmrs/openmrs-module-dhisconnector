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
package org.openmrs.module.dhisconnector.api.db.hibernate;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.SerializedObject;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.dhisconnector.DHISServerConfiguration;
import org.openmrs.module.dhisconnector.DHISServerReportsToReceive;
import org.openmrs.module.dhisconnector.LocationToOrgUnitMapping;
import org.openmrs.module.dhisconnector.ReportToDataSetMapping;
import org.openmrs.module.dhisconnector.api.db.DHISConnectorDAO;
import org.openmrs.module.dhisconnector.api.model.DHISServerConfigurationDTO;
import org.springframework.util.ReflectionUtils;

/**
 * It is a default implementation of {@link DHISConnectorDAO}.
 */
public class HibernateDHISConnectorDAO implements DHISConnectorDAO {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private DbSessionFactory sessionFactory;
	
	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(DbSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * @return the sessionFactory
	 */
	public DbSessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	public Connection getConnection() {
		try {
			// reflective lookup to bridge between Hibernate 3.x and 4.x
			Method connectionMethod = sessionFactory.getCurrentSession().getClass().getMethod("connection");
			return (Connection) ReflectionUtils.invokeMethod(connectionMethod, sessionFactory.getCurrentSession());
		}
		catch (NoSuchMethodException ex) {
			throw new IllegalStateException("Cannot find connection() method on Hibernate session", ex);
		}
	}
	
	@Override
	public void saveReportToDataSetMapping(ReportToDataSetMapping reportToDataSetMapping) {
		reportToDataSetMapping.setCreator(Context.getAuthenticatedUser());
		sessionFactory.getCurrentSession().save(reportToDataSetMapping);
	}
	
	@Override
	public void deleteReportToDataSetMapping(ReportToDataSetMapping reportToDataSetMapping) {
		sessionFactory.getCurrentSession().delete(reportToDataSetMapping);
	}
	
	@Override
	public ReportToDataSetMapping getReportToDataSetMapping(Integer id) {
		return (ReportToDataSetMapping) sessionFactory.getCurrentSession().get(ReportToDataSetMapping.class, id);
	}
	
	@Override
	public ReportToDataSetMapping getReportToDataSetMappingByUuid(String uuid) {
		ReportToDataSetMapping mappedIndicatorReport = (ReportToDataSetMapping) sessionFactory.getCurrentSession()
		        .createQuery("from ReportToDataSetMapping r where r.uuid = :uuid").setParameter("uuid", uuid).uniqueResult();
		
		return mappedIndicatorReport;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ReportToDataSetMapping> getAllReportToDataSetMappings() {
		return sessionFactory.getCurrentSession().createCriteria(ReportToDataSetMapping.class).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LocationToOrgUnitMapping> getAllLocationToOrgUnitMappings() {
		return sessionFactory.getCurrentSession().createCriteria(LocationToOrgUnitMapping.class).list();
	}

	@Override
	public LocationToOrgUnitMapping getLocationToOrgUnitMappingByUuid(String uuid) {
		LocationToOrgUnitMapping locationToOrgUnitMapping = (LocationToOrgUnitMapping) sessionFactory.getCurrentSession()
				.createQuery("from LocationToOrgUnitMapping r where r.uuid = :uuid").setParameter("uuid", uuid).uniqueResult();
		return locationToOrgUnitMapping;
	}

	@Override
	public LocationToOrgUnitMapping getLocationToOrgUnitMappingByOrgUnitUid(String orgUnitUid) {
		LocationToOrgUnitMapping locationToOrgUnitMapping = (LocationToOrgUnitMapping) sessionFactory.getCurrentSession()
				.createQuery("from LocationToOrgUnitMapping r where r.orgUnitUid = :orgUnitUid")
				.setParameter("orgUnitUid", orgUnitUid).uniqueResult();
		return locationToOrgUnitMapping;
	}

	@Override
	public void saveLocationToOrgUnitMapping(LocationToOrgUnitMapping locationToOrgUnitMapping) {
		locationToOrgUnitMapping.setCreator(Context.getAuthenticatedUser());
		sessionFactory.getCurrentSession().save(locationToOrgUnitMapping);
	}

	@Override
	public void deleteLocationToOrgUnitMappingsByLocation(Location location) {
		sessionFactory.getCurrentSession()
				.createQuery("delete from LocationToOrgUnitMapping r where r.location = :location")
				.setParameter("location", location).executeUpdate();
	}

	@Override
	public void saveSerializedObject(SerializedObject serializedObject) {
		sessionFactory.getCurrentSession().save(serializedObject);
	}

	@Override
	public SerializedObject getSerializedObjectByUuid(String uuid) {
		SerializedObject serializedObject = (SerializedObject) sessionFactory.getCurrentSession()
				.createQuery("from SerializedObject s where s.uuid = :uuid")
				.setParameter("uuid", uuid).uniqueResult();
		return serializedObject;
	}

	@Override
	public void deleteSerializedObjectByUuid(String uuid) {
		sessionFactory.getCurrentSession()
				.createQuery("delete from SerializedObject s where s.uuid = :uuid")
				.setParameter("uuid", uuid).executeUpdate();
	}

	@Override
	public void saveDHISServerConfiguration(List<DHISServerConfiguration> servers) {
		for (DHISServerConfiguration server : servers) {
			server.setCreator(Context.getAuthenticatedUser());
			sessionFactory.getCurrentSession().save(server);
		}
	}

	@Override
	public void saveDHISServerConfiguration(DHISServerConfiguration server) {
		server.setCreator(Context.getAuthenticatedUser());
		sessionFactory.getCurrentSession().save(server);
	}

	@Override
	public List<DHISServerConfiguration> getDHISServerConfigurations() {
		return sessionFactory.getCurrentSession().createCriteria(DHISServerConfiguration.class).list();
	}

	@Override
	public void deleteDHISServerConfiguration(DHISServerConfiguration dHISServerConfiguration) {
		sessionFactory.getCurrentSession()
		.createQuery("delete from DHISServerConfiguration r where r.url = :url")
		.setParameter("url", dHISServerConfiguration.getUrl()).executeUpdate();
	}

	@Override
	public void saveDHISServerReportsToReceive(List<DHISServerReportsToReceive> serverWithReportsToReceive) {
		for (DHISServerReportsToReceive serverWithReport : serverWithReportsToReceive) {
			serverWithReport.setCreator(Context.getAuthenticatedUser());
			sessionFactory.getCurrentSession().save(serverWithReport);
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DHISServerReportsToReceive> getDHISServerReportsToReceive() {
		return sessionFactory.getCurrentSession().createCriteria(DHISServerReportsToReceive.class).list();
	}

	@Override
	public DHISServerReportsToReceive getDHISServerReportsToReceiveByServerUuidAndReportUuid(String dhisServerUuid,
			String sespReportUuid) {
		DHISServerReportsToReceive dhisServerReportsToReceive = (DHISServerReportsToReceive) sessionFactory.getCurrentSession()
				.createQuery("from DHISServerReportsToReceive d where d.dhisServerUuid = :dhisServerUuid and d.sespReportUuid = :sespReportUuid")
				.setParameter("dhisServerUuid", dhisServerUuid)
				.setParameter("sespReportUuid", sespReportUuid)
				.uniqueResult();
		return dhisServerReportsToReceive;
	}

	@Override
	public DHISServerConfiguration getDHISServerByUrl(String serverUrl) {
		return (DHISServerConfiguration) sessionFactory.getCurrentSession()
		.createQuery("from DHISServerConfiguration r where r.url = :url")
		.setParameter("url", serverUrl).uniqueResult();
	}

	@Override
	public DHISServerConfiguration getDHISServerByUuid(String serverUuid) {
		return (DHISServerConfiguration) sessionFactory.getCurrentSession()
		.createQuery("from DHISServerConfiguration r where r.uuid = :serverUuid")
		.setParameter("serverUuid", serverUuid).uniqueResult();
	}

	@Override
	public LocationToOrgUnitMapping getLocationToOrgUnitMappingByLocationAndOrgUnitIdAndServerUuid(Location location,
			String orgUnitId, String serverUuid) {
		LocationToOrgUnitMapping locationToOrgUnitMapping = (LocationToOrgUnitMapping) sessionFactory.getCurrentSession()
				.createQuery("from LocationToOrgUnitMapping r where r.location = :location"
						+ " and r.orgUnitUid = :orgUnitUid and r.serverUuid = :serverUuid")
				.setParameter("location", location)
				.setParameter("orgUnitUid", orgUnitId)
				.setParameter("serverUuid", serverUuid)
				.uniqueResult();
		return locationToOrgUnitMapping;
	}

	@Override
	public void deleteLocationToOrgUnitMappingsByLocationAndServerUuidAndOrgUnitUid(Location location,
			String serverUuid, String orgUnitUid) {
		sessionFactory.getCurrentSession()
		.createQuery("delete from LocationToOrgUnitMapping r where r.location = :location and r.serverUuid = :serverUuid and r.orgUnitUid = :orgUnitUid")
		.setParameter("location", location)
		.setParameter("serverUuid", serverUuid)
		.setParameter("orgUnitUid", orgUnitUid)
		.executeUpdate();
		
	}

	@Override
	public void deleteDHISServerReportsToReceiveByServerUuidAndReportUuid(String dhisServerUuid,
			String sespReportUuid) {
		sessionFactory.getCurrentSession()
				.createQuery("delete from DHISServerReportsToReceive d where d.dhisServerUuid = :dhisServerUuid and d.sespReportUuid = :sespReportUuid")
				.setParameter("dhisServerUuid", dhisServerUuid)
				.setParameter("sespReportUuid", sespReportUuid)
				.executeUpdate();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<DHISServerReportsToReceive> getDHISServerReportsToReceiveByServerUuid(String dhisServerUuid) {
		return 	sessionFactory.getCurrentSession()
				.createQuery("from DHISServerReportsToReceive d where d.dhisServerUuid = :dhisServerUuid ")
				.setParameter("dhisServerUuid", dhisServerUuid)
				.list();
				
	}

}
