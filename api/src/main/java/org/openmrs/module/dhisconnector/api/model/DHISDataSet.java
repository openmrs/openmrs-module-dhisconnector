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

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhisconnector.api.DHISConnectorService;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({ "id", "created", "name", "href", "lastUpdated", "shortName", "code", "displayDescription", "expiryDays",
        "version", "approveData", "description", "renderHorizontally", "externalAccess", "openFuturePeriods",
        "fieldCombinationRequired", "skipOffline", "dataSetType", "publicAccess", "validCompleteOnly", "skipAggregation",
        "noValueRequiresComment", "notifyCompletingUser", "timelyDays", "renderAsTabs", "dataElementDecoration",
        "periodType", "displayName", "displayShortName", "mobile", "dataEntryForm", "access", "categoryCombo", "sections",
        "dataElements", "dataSetElements", "organisationUnits", "indicators", "attributeValues",
        "compulsoryDataElementOperands", "userGroupAccesses" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class DHISDataSet {
	
	
	@JsonProperty("id")
	private String id;
	
	@JsonProperty("created")
	private String created;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("href")
	private String href;
	
	@JsonProperty("lastUpdated")
	private String lastUpdated;
	
	@JsonProperty("shortName")
	private String shortName;
	
	@JsonProperty("code")
	private String code;
	
	@JsonProperty("uuid")
	private String uuid;
	
	@JsonProperty("displayDescription")
	private String displayDescription;
	
	@JsonProperty("expiryDays")
	private Integer expiryDays;
	
	@JsonProperty("version")
	private Integer version;
	
	@JsonProperty("approveData")
	private Boolean approveData;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("renderHorizontally")
	private Boolean renderHorizontally;
	
	@JsonProperty("externalAccess")
	private Boolean externalAccess;
	
	@JsonProperty("openFuturePeriods")
	private Integer openFuturePeriods;
	
	@JsonProperty("fieldCombinationRequired")
	private Boolean fieldCombinationRequired;
	
	@JsonProperty("skipOffline")
	private Boolean skipOffline;
	
	@JsonProperty("dataSetType")
	private String dataSetType;
	
	@JsonProperty("publicAccess")
	private String publicAccess;
	
	@JsonProperty("validCompleteOnly")
	private Boolean validCompleteOnly;
	
	@JsonProperty("skipAggregation")
	private Boolean skipAggregation;
	
	@JsonProperty("noValueRequiresComment")
	private Boolean noValueRequiresComment;
	
	@JsonProperty("notifyCompletingUser")
	private Boolean notifyCompletingUser;
	
	@JsonProperty("timelyDays")
	private Integer timelyDays;
	
	@JsonProperty("renderAsTabs")
	private Boolean renderAsTabs;
	
	@JsonProperty("dataElementDecoration")
	private Boolean dataElementDecoration;
	
	@JsonProperty("periodType")
	private String periodType;
	
	@JsonProperty("displayName")
	private String displayName;
	
	@JsonProperty("displayShortName")
	private String displayShortName;
	
	@JsonProperty("mobile")
	private Boolean mobile;
	
	@JsonProperty("dataEntryForm")
	private DHISDataEntryForm dataEntryForm;
	
	@JsonProperty("access")
	private DHISAccess access;
	
	@JsonProperty("categoryCombo")
	private DHISCategoryCombo categoryCombo;
	
	@JsonProperty("sections")
	private List<Object> sections = new ArrayList<Object>();
	
	@JsonProperty("dataElements")
	private List<DHISDataElement> dataElements = new ArrayList<DHISDataElement>();
	
	@JsonProperty("dataSetElements")
	private List<DHISDataSetElement> dataSetElements = new ArrayList<DHISDataSetElement>();
	
	@JsonProperty("organisationUnits")
	private List<DHISOrganisationUnit> organisationUnits = new ArrayList<DHISOrganisationUnit>();
	
	@JsonProperty("indicators")
	private List<Object> indicators = new ArrayList<Object>();
	
	@JsonProperty("attributeValues")
	private List<Object> attributeValues = new ArrayList<Object>();
	
	@JsonProperty("compulsoryDataElementOperands")
	private List<Object> compulsoryDataElementOperands = new ArrayList<Object>();
	
	@JsonProperty("userGroupAccesses")
	private List<Object> userGroupAccesses = new ArrayList<Object>();
	
	/**
	 * @return The id
	 */
	@JsonProperty("id")
	public String getId() {
		return id;
	}
	
	/**
	 * @param id The id
	 */
	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return The created
	 */
	@JsonProperty("created")
	public String getCreated() {
		return created;
	}
	
	/**
	 * @param created The created
	 */
	@JsonProperty("created")
	public void setCreated(String created) {
		this.created = created;
	}
	
	/**
	 * @return The name
	 */
	@JsonProperty("name")
	public String getName() {
		return name;
	}
	
	/**
	 * @param name The name
	 */
	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return The href
	 */
	@JsonProperty("href")
	public String getHref() {
		return href;
	}
	
	/**
	 * @param href The href
	 */
	@JsonProperty("href")
	public void setHref(String href) {
		this.href = href;
	}
	
	/**
	 * @return The lastUpdated
	 */
	@JsonProperty("lastUpdated")
	public String getLastUpdated() {
		return lastUpdated;
	}
	
	/**
	 * @param lastUpdated The lastUpdated
	 */
	@JsonProperty("lastUpdated")
	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	/**
	 * @return The shortName
	 */
	@JsonProperty("shortName")
	public String getShortName() {
		return shortName;
	}
	
	/**
	 * @param shortName The shortName
	 */
	@JsonProperty("shortName")
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	
	/**
	 * @return The code
	 */
	@JsonProperty("code")
	public String getCode() {
		return code;
	}
	
	/**
	 * @param code The code
	 */
	@JsonProperty("code")
	public void setCode(String code) {
		this.code = code;
	}
	
	/**
	 * @return The displayDescription
	 */
	@JsonProperty("displayDescription")
	public String getDisplayDescription() {
		return displayDescription;
	}
	
	/**
	 * @param displayDescription The displayDescription
	 */
	@JsonProperty("displayDescription")
	public void setDisplayDescription(String displayDescription) {
		this.displayDescription = displayDescription;
	}
	
	/**
	 * @return The expiryDays
	 */
	@JsonProperty("expiryDays")
	public Integer getExpiryDays() {
		return expiryDays;
	}
	
	/**
	 * @param expiryDays The expiryDays
	 */
	@JsonProperty("expiryDays")
	public void setExpiryDays(Integer expiryDays) {
		this.expiryDays = expiryDays;
	}
	
	/**
	 * @return The version
	 */
	@JsonProperty("version")
	public Integer getVersion() {
		return version;
	}
	
	/**
	 * @param version The version
	 */
	@JsonProperty("version")
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	/**
	 * @return The approveData
	 */
	@JsonProperty("approveData")
	public Boolean getApproveData() {
		return approveData;
	}
	
	/**
	 * @param approveData The approveData
	 */
	@JsonProperty("approveData")
	public void setApproveData(Boolean approveData) {
		this.approveData = approveData;
	}
	
	/**
	 * @return The description
	 */
	@JsonProperty("description")
	public String getDescription() {
		return description;
	}
	
	/**
	 * @param description The description
	 */
	@JsonProperty("description")
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return The renderHorizontally
	 */
	@JsonProperty("renderHorizontally")
	public Boolean getRenderHorizontally() {
		return renderHorizontally;
	}
	
	/**
	 * @param renderHorizontally The renderHorizontally
	 */
	@JsonProperty("renderHorizontally")
	public void setRenderHorizontally(Boolean renderHorizontally) {
		this.renderHorizontally = renderHorizontally;
	}
	
	/**
	 * @return The externalAccess
	 */
	@JsonProperty("externalAccess")
	public Boolean getExternalAccess() {
		return externalAccess;
	}
	
	/**
	 * @param externalAccess The externalAccess
	 */
	@JsonProperty("externalAccess")
	public void setExternalAccess(Boolean externalAccess) {
		this.externalAccess = externalAccess;
	}
	
	/**
	 * @return The openFuturePeriods
	 */
	@JsonProperty("openFuturePeriods")
	public Integer getOpenFuturePeriods() {
		return openFuturePeriods;
	}
	
	/**
	 * @param openFuturePeriods The openFuturePeriods
	 */
	@JsonProperty("openFuturePeriods")
	public void setOpenFuturePeriods(Integer openFuturePeriods) {
		this.openFuturePeriods = openFuturePeriods;
	}
	
	/**
	 * @return The fieldCombinationRequired
	 */
	@JsonProperty("fieldCombinationRequired")
	public Boolean getFieldCombinationRequired() {
		return fieldCombinationRequired;
	}
	
	/**
	 * @param fieldCombinationRequired The fieldCombinationRequired
	 */
	@JsonProperty("fieldCombinationRequired")
	public void setFieldCombinationRequired(Boolean fieldCombinationRequired) {
		this.fieldCombinationRequired = fieldCombinationRequired;
	}
	
	/**
	 * @return The skipOffline
	 */
	@JsonProperty("skipOffline")
	public Boolean getSkipOffline() {
		return skipOffline;
	}
	
	/**
	 * @param skipOffline The skipOffline
	 */
	@JsonProperty("skipOffline")
	public void setSkipOffline(Boolean skipOffline) {
		this.skipOffline = skipOffline;
	}
	
	/**
	 * @return The dataSetType
	 */
	@JsonProperty("dataSetType")
	public String getDataSetType() {
		return dataSetType;
	}
	
	/**
	 * @param dataSetType The dataSetType
	 */
	@JsonProperty("dataSetType")
	public void setDataSetType(String dataSetType) {
		this.dataSetType = dataSetType;
	}
	
	/**
	 * @return The publicAccess
	 */
	@JsonProperty("publicAccess")
	public String getPublicAccess() {
		return publicAccess;
	}
	
	/**
	 * @param publicAccess The publicAccess
	 */
	@JsonProperty("publicAccess")
	public void setPublicAccess(String publicAccess) {
		this.publicAccess = publicAccess;
	}
	
	/**
	 * @return The validCompleteOnly
	 */
	@JsonProperty("validCompleteOnly")
	public Boolean getValidCompleteOnly() {
		return validCompleteOnly;
	}
	
	/**
	 * @param validCompleteOnly The validCompleteOnly
	 */
	@JsonProperty("validCompleteOnly")
	public void setValidCompleteOnly(Boolean validCompleteOnly) {
		this.validCompleteOnly = validCompleteOnly;
	}
	
	/**
	 * @return The skipAggregation
	 */
	@JsonProperty("skipAggregation")
	public Boolean getSkipAggregation() {
		return skipAggregation;
	}
	
	/**
	 * @param skipAggregation The skipAggregation
	 */
	@JsonProperty("skipAggregation")
	public void setSkipAggregation(Boolean skipAggregation) {
		this.skipAggregation = skipAggregation;
	}
	
	/**
	 * @return The noValueRequiresComment
	 */
	@JsonProperty("noValueRequiresComment")
	public Boolean getNoValueRequiresComment() {
		return noValueRequiresComment;
	}
	
	/**
	 * @param noValueRequiresComment The noValueRequiresComment
	 */
	@JsonProperty("noValueRequiresComment")
	public void setNoValueRequiresComment(Boolean noValueRequiresComment) {
		this.noValueRequiresComment = noValueRequiresComment;
	}
	
	/**
	 * @return The notifyCompletingUser
	 */
	@JsonProperty("notifyCompletingUser")
	public Boolean getNotifyCompletingUser() {
		return notifyCompletingUser;
	}
	
	/**
	 * @param notifyCompletingUser The notifyCompletingUser
	 */
	@JsonProperty("notifyCompletingUser")
	public void setNotifyCompletingUser(Boolean notifyCompletingUser) {
		this.notifyCompletingUser = notifyCompletingUser;
	}
	
	/**
	 * @return The timelyDays
	 */
	@JsonProperty("timelyDays")
	public Integer getTimelyDays() {
		return timelyDays;
	}
	
	/**
	 * @param timelyDays The timelyDays
	 */
	@JsonProperty("timelyDays")
	public void setTimelyDays(Integer timelyDays) {
		this.timelyDays = timelyDays;
	}
	
	/**
	 * @return The renderAsTabs
	 */
	@JsonProperty("renderAsTabs")
	public Boolean getRenderAsTabs() {
		return renderAsTabs;
	}
	
	/**
	 * @param renderAsTabs The renderAsTabs
	 */
	@JsonProperty("renderAsTabs")
	public void setRenderAsTabs(Boolean renderAsTabs) {
		this.renderAsTabs = renderAsTabs;
	}
	
	/**
	 * @return The dataElementDecoration
	 */
	@JsonProperty("dataElementDecoration")
	public Boolean getDataElementDecoration() {
		return dataElementDecoration;
	}
	
	/**
	 * @param dataElementDecoration The dataElementDecoration
	 */
	@JsonProperty("dataElementDecoration")
	public void setDataElementDecoration(Boolean dataElementDecoration) {
		this.dataElementDecoration = dataElementDecoration;
	}
	
	/**
	 * @return The periodType
	 */
	@JsonProperty("periodType")
	public String getPeriodType() {
		return periodType;
	}
	
	/**
	 * @param periodType The periodType
	 */
	@JsonProperty("periodType")
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}
	
	/**
	 * @return The displayName
	 */
	@JsonProperty("displayName")
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * @param displayName The displayName
	 */
	@JsonProperty("displayName")
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * @return The displayShortName
	 */
	@JsonProperty("displayShortName")
	public String getDisplayShortName() {
		return displayShortName;
	}
	
	/**
	 * @param displayShortName The displayShortName
	 */
	@JsonProperty("displayShortName")
	public void setDisplayShortName(String displayShortName) {
		this.displayShortName = displayShortName;
	}
	
	/**
	 * @return The mobile
	 */
	@JsonProperty("mobile")
	public Boolean getMobile() {
		return mobile;
	}
	
	/**
	 * @param mobile The mobile
	 */
	@JsonProperty("mobile")
	public void setMobile(Boolean mobile) {
		this.mobile = mobile;
	}
	
	/**
	 * @return The dataEntryForm
	 */
	@JsonProperty("dataEntryForm")
	public DHISDataEntryForm getDataEntryForm() {
		return dataEntryForm;
	}
	
	/**
	 * @param dataEntryForm The dataEntryForm
	 */
	@JsonProperty("dataEntryForm")
	public void setDataEntryForm(DHISDataEntryForm dataEntryForm) {
		this.dataEntryForm = dataEntryForm;
	}
	
	/**
	 * @return The access
	 */
	@JsonProperty("access")
	public DHISAccess getAccess() {
		return access;
	}
	
	/**
	 * @param access The access
	 */
	@JsonProperty("access")
	public void setAccess(DHISAccess access) {
		this.access = access;
	}
	
	/**
	 * @return The categoryCombo
	 */
	@JsonProperty("categoryCombo")
	public DHISCategoryCombo getCategoryCombo() {
		return categoryCombo;
	}
	
	/**
	 * @param categoryCombo The categoryCombo
	 */
	@JsonProperty("categoryCombo")
	public void setCategoryCombo(DHISCategoryCombo categoryCombo) {
		this.categoryCombo = categoryCombo;
	}
	
	/**
	 * @return The sections
	 */
	@JsonProperty("sections")
	public List<Object> getSections() {
		return sections;
	}
	
	/**
	 * @param sections The sections
	 */
	@JsonProperty("sections")
	public void setSections(List<Object> sections) {
		this.sections = sections;
	}
	
	/**
	 * @return The dataElements
	 */
	@JsonProperty("dataElements")
	public List<DHISDataElement> getDataElements() {
		return dataElements;
	}
	
	/**
	 * @param dataElements The dataElements
	 */
	@JsonProperty("dataElements")
	public void setDataElements(List<DHISDataElement> dataElements) {
		this.dataElements = dataElements;
	}
	
	/**
	 * @return The dataSetElements
	 */
	@JsonProperty("dataSetElements")
	public List<DHISDataSetElement> getDataSetElements() {
		return dataSetElements;
	}
	
	/**
	 * @param dataSetElements The dataSetElements
	 */
	@JsonProperty("dataSetElements")
	public void setDataSetElements(List<DHISDataSetElement> dataSetElements) {
		/*List<DHISDataSetElement> regeneratedDataSetElements = new ArrayList<DHISDataSetElement>();
		
		if (dataSetElements != null && dataSetElements.size() > 0
		        && (getDataElements() == null || getDataElements().size() <= 0)) {
			for (DHISDataSetElement d : dataSetElements) {
				DHISDataSetElement d1 = getDataSetElementFromDataSetElementId(d.getId());

				regeneratedDataSetElements.add(d1 != null ? d1 : d);
			}
			this.dataSetElements = regeneratedDataSetElements;
		} else*/
			this.dataSetElements = dataSetElements;
	}
	

	private DHISDataSetElement getDataSetElementFromDataSetElementId(String dataSetElementId) {
		ObjectMapper mapper = new ObjectMapper();
		String jsonResponse = Context.getService(DHISConnectorService.class)
					.getDataFromDHISEndpoint("/api/dataSetElements/" + dataSetElementId + ".json");
        DHISDataSetElement ret = null;
		
		try {
			ret = mapper.readValue(jsonResponse, DHISDataSetElement.class);
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return ret;
	}
	
	/**
	 * @return The organisationUnits
	 */
	@JsonProperty("organisationUnits")
	public List<DHISOrganisationUnit> getOrganisationUnits() {
		return organisationUnits;
	}
	
	/**
	 * @param organisationUnits The organisationUnits
	 */
	@JsonProperty("organisationUnits")
	public void setOrganisationUnits(List<DHISOrganisationUnit> organisationUnits) {
		this.organisationUnits = organisationUnits;
	}
	
	/**
	 * @return The indicators
	 */
	@JsonProperty("indicators")
	public List<Object> getIndicators() {
		return indicators;
	}
	
	/**
	 * @param indicators The indicators
	 */
	@JsonProperty("indicators")
	public void setIndicators(List<Object> indicators) {
		this.indicators = indicators;
	}
	
	/**
	 * @return The attributeValues
	 */
	@JsonProperty("attributeValues")
	public List<Object> getAttributeValues() {
		return attributeValues;
	}
	
	/**
	 * @param attributeValues The attributeValues
	 */
	@JsonProperty("attributeValues")
	public void setAttributeValues(List<Object> attributeValues) {
		this.attributeValues = attributeValues;
	}
	
	/**
	 * @return The compulsoryDataElementOperands
	 */
	@JsonProperty("compulsoryDataElementOperands")
	public List<Object> getCompulsoryDataElementOperands() {
		return compulsoryDataElementOperands;
	}
	
	/**
	 * @param compulsoryDataElementOperands The compulsoryDataElementOperands
	 */
	@JsonProperty("compulsoryDataElementOperands")
	public void setCompulsoryDataElementOperands(List<Object> compulsoryDataElementOperands) {
		this.compulsoryDataElementOperands = compulsoryDataElementOperands;
	}
	
	/**
	 * @return The userGroupAccesses
	 */
	@JsonProperty("userGroupAccesses")
	public List<Object> getUserGroupAccesses() {
		return userGroupAccesses;
	}
	
	/**
	 * @param userGroupAccesses The userGroupAccesses
	 */
	@JsonProperty("userGroupAccesses")
	public void setUserGroupAccesses(List<Object> userGroupAccesses) {
		this.userGroupAccesses = userGroupAccesses;
	}

	@JsonProperty("uuid")
	public String getUuid() {
		return uuid;
	}

	@JsonProperty("uuid")
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	
	
}
