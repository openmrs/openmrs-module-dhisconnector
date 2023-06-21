package org.openmrs.module.dhisconnector.api.model.lockexception;

import javax.annotation.Generated;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({ "id", })
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataSet {

	@JsonProperty("id")
	private String id;

	public DataSet() {
		super();
	}

	public DataSet(String id) {
		this.id = id;
	}

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("id")
	public void setId(String id) {
		this.id = id;
	}
}