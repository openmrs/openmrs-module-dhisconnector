package org.openmrs.module.dhisconnector.api.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.Location;
import org.openmrs.User;

import javax.annotation.Generated;
import javax.persistence.*;
import java.util.Date;

/**
 * Represents a general mapping between an OpenMRS location against a DHIS2 Organisation unit
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({
		"location",
		"serverUrl",
		"serverUuid",
		"orgUnitUid",
		"orgUnitName"
		
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class DHISLocationToOrgUnitMapping {

	@JsonProperty("location")
    private Location location;
    
	@JsonProperty("serverUrl")
    private String serverUrl;
    
	@JsonProperty("serverUuid")
	private String serverUuid;
	
	@JsonProperty("orgUnitUid")
    private String orgUnitUid;
	
	@JsonProperty("orgUnitName")
    private String orgUnitName;

    public DHISLocationToOrgUnitMapping() {
    }

    public DHISLocationToOrgUnitMapping(Location location, String orgUnitId, String serverUuid, String serverUrl, String orgUnitName) {
        setLocation(location);
        setOrgUnitUid(orgUnitId);
        setServerUuid(serverUuid);
        setServerUrl(serverUrl);
        setOrgUnitName(orgUnitName);
    }

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getServerUuid() {
		return serverUuid;
	}

	public void setServerUuid(String serverUuid) {
		this.serverUuid = serverUuid;
	}

	public String getOrgUnitUid() {
		return orgUnitUid;
	}

	public void setOrgUnitUid(String orgUnitUid) {
		this.orgUnitUid = orgUnitUid;
	}

	public String getOrgUnitName() {
		return orgUnitName;
	}

	public void setOrgUnitName(String orgUnitName) {
		this.orgUnitName = orgUnitName;
	}
}
