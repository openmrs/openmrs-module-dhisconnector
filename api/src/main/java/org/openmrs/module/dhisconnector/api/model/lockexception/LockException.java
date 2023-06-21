package org.openmrs.module.dhisconnector.api.model.lockexception;

import javax.annotation.Generated;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({ "name", "period", "organisationUnit", "dataSet" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class LockException {

	@JsonProperty("name")
	private String name;
	@JsonProperty("period")
	private Period period;
	@JsonProperty("organisationUnit")
	private OrganisationUnit organisationUnit;
	@JsonProperty("dataSet")
	private DataSet dataSet;

	public LockException() {
		super();
	}

	public LockException(String name, Period period, OrganisationUnit organisationUnit, DataSet dataSet) {

		this.name = name;
		this.period = period;
		this.organisationUnit = organisationUnit;
		this.dataSet = dataSet;
	}

	@JsonProperty("name")
	public String getName() {
		return name;
	}

	@JsonProperty("name")
	public void setName(String name) {
		this.name = name;
	}

	@JsonProperty("period")
	public Period getPeriod() {
		return period;
	}

	@JsonProperty("period")
	public void setPeriod(Period period) {
		this.period = period;
	}

	@JsonProperty("organisationUnit")
	public OrganisationUnit getOrganisationUnit() {
		return organisationUnit;
	}

	@JsonProperty("organisationUnit")
	public void setOrganisationUnit(OrganisationUnit organisationUnit) {
		this.organisationUnit = organisationUnit;
	}

	@JsonProperty("dataSet")
	public DataSet getDataSet() {
		return dataSet;
	}

	@JsonProperty("dataSet")
	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}
}