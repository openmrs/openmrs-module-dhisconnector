package org.openmrs.module.dhisconnector;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "dhisconnector_location_to_orgunit")
public class LocationToOrgUnitMapping extends BaseOpenmrsObject {

    @ManyToOne(optional = false)
    @JoinColumn(name = "creator")
    protected User creator;

    @Column(name = "date_created", nullable = false)
    private Date dateCreated;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "org_unit_uid", nullable = false)
    private String orgUnitUid;

    @JsonIgnore
    @ManyToOne(optional = false)
    @JoinColumn(name = "location")
    private Location location;
    
    @Column(name = "server_uuid", nullable = false)
    private String serverUuid;
    
    @Column(name = "org_unit_name", nullable = false)
    private String orgUnitName;

    public LocationToOrgUnitMapping() {
    }

    public LocationToOrgUnitMapping(Location location, String orgUnitId, String serverUuid, String orgUnitName) {
        setLocation(location);
        setOrgUnitUid(orgUnitId);
        setServerUuid(serverUuid);
        setOrgUnitName(orgUnitName);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrgUnitUid() {
        return orgUnitUid;
    }

    public void setOrgUnitUid(String orgUnitUid) {
        this.orgUnitUid = orgUnitUid;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

	public String getServerUuid() {
		return serverUuid;
	}

	public void setServerUuid(String serverUuid) {
		this.serverUuid = serverUuid;
	}

	public String getOrgUnitName() {
		return orgUnitName;
	}

	public void setOrgUnitName(String orgUnitName) {
		this.orgUnitName = orgUnitName;
	}
	
}
