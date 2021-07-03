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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Location;
import org.openmrs.api.context.Context;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.dhisconnector.LocationToOrgUnitMapping;
import org.openmrs.module.dhisconnector.ReportToDataSetMapping;
import org.openmrs.module.dhisconnector.api.db.DHISConnectorDAO;
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
}
