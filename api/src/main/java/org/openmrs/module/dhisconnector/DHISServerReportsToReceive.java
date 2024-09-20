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
package org.openmrs.module.dhisconnector;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Location;
import org.openmrs.User;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents the mapping of locations and the dhis servers to send the reports
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "dhisconnector_dhis_server_reports_to_receive")
public class DHISServerReportsToReceive extends BaseOpenmrsObject {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;
    
    @Column(name = "dhis_server_uuid", nullable = false)
	private String dhisServerUuid;

    @Column(name = "sesp_report_uuid", nullable = false)
	private String sespReportUuid;

    @ManyToOne(optional = false)
    @JoinColumn(name = "creator")
    protected User creator;

    @Column(name = "date_created", nullable = false)
    private Date dateCreated;

    
    public DHISServerReportsToReceive() {
	}
    
    public DHISServerReportsToReceive(String dhisServerUuid, String sespReportUuid) {
    	setDhisServerUuid(dhisServerUuid);
    	setSespReportUuid(sespReportUuid);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getDhisServerUuid() {
		return dhisServerUuid;
	}

	public void setDhisServerUuid(String dhisServerUuid) {
		this.dhisServerUuid = dhisServerUuid;
	}

	public String getSespReportUuid() {
		return sespReportUuid;
	}

	public void setSespReportUuid(String sespReportUuid) {
		this.sespReportUuid = sespReportUuid;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

}
