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
package org.openmrs.module.dhisconnector.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
	"dhisServers",
    "dataSet",
    "completeData",
    "period",
    "orgUnit",
    "reportName",
    "dataValues"
})
public class DHISDataValueSet {
	
    @JsonProperty("dhisServers")
    private List<DHISServerConfigurationDTO> dhisServers = new ArrayList<DHISServerConfigurationDTO>();
    @JsonProperty("dataSet")
    private String dataSet;
    @JsonProperty("completeData")
    private String completeData;
    @JsonProperty("period")
    private String period;
    @JsonProperty("orgUnit")
    private String orgUnit;
    @JsonProperty("reportName")
    private String reportName;
    @JsonProperty("dataValues")
    private List<DHISDataValue> dataValues = new ArrayList<DHISDataValue>();
    /**
     * 
     * @return
     *     The dataSet
     */
    @JsonProperty("dataSet")
    public String getDataSet() {
        return dataSet;
    }

    /**
     * 
     * @param dataSet
     *     The dataSet
     */
    @JsonProperty("dataSet")
    public void setDataSet(String dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * 
     * @return
     *     The completeData
     */
    @JsonProperty("completeData")
    public String getCompleteData() {
        return completeData;
    }

    /**
     * 
     * @param completeData
     *     The completeData
     */
    @JsonProperty("completeData")
    public void setCompleteData(String completeData) {
        this.completeData = completeData;
    }

    /**
     * 
     * @return
     *     The period
     */
    @JsonProperty("period")
    public String getPeriod() {
        return period;
    }

    /**
     * 
     * @param period
     *     The period
     */
    @JsonProperty("period")
    public void setPeriod(String period) {
        this.period = period;
    }

    /**
     * 
     * @return
     *     The orgUnit
     */
    @JsonProperty("orgUnit")
    public String getOrgUnit() {
        return orgUnit;
    }

    /**
     * 
     * @param orgUnit
     *     The orgUnit
     */
    @JsonProperty("orgUnit")
    public void setOrgUnit(String orgUnit) {
        this.orgUnit = orgUnit;
    }
    
    /**
     * 
     * @return
     *     The reportName
     */
    public String getReportName() {
		return reportName;
	}

    /**
     * 
     * @param reportName
     *     The reportName
     */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

    /**
     * 
     * @return
     *     The dataValues
     */
    @JsonProperty("dataValues")
    public List<DHISDataValue> getDataValues() {
        return dataValues;
    }
    

	/**
     * 
     * @param dataValues
     *     The dataValues
     */
    @JsonProperty("dataValues")
    public void setDataValues(List<DHISDataValue> dataValues) {
        this.dataValues = dataValues;
    }

    public void addDataValue(DHISDataValue dataValue) {
        if(dataValues == null) {
            dataValues = new ArrayList<DHISDataValue>();
        }

        dataValues.add(dataValue);
    }

    @JsonProperty("dhisServers")
	public List<DHISServerConfigurationDTO> getDhisServers() {
		return dhisServers;
	}

	/**
     * 
     * @param dhisServers
     *     The dhisServers
     */
    @JsonProperty("dhisServers")
	public void setDhisServers(List<DHISServerConfigurationDTO> dhisServers) {
		this.dhisServers = dhisServers;
	}
    
    public void addDhisServer(DHISServerConfigurationDTO server) {
        if(dhisServers == null) {
        	dhisServers = new ArrayList<DHISServerConfigurationDTO>();
        }

        dhisServers.add(server);
    }

}
