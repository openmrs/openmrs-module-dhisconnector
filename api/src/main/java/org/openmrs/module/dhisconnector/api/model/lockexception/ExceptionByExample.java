package org.openmrs.module.dhisconnector.api.model.lockexception;

import java.util.List;

import javax.annotation.Generated;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({ "pager", "lockExceptions" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExceptionByExample {

	@JsonProperty("pager")
	private Pager pager;
	@JsonProperty("lockExceptions")
	private List<LockException> lockExceptions;

	public ExceptionByExample() {
		super();
	}

	public ExceptionByExample(Pager pager, List<LockException> lockExceptions) {
		this.pager = pager;
		this.lockExceptions = lockExceptions;
	}

	@JsonProperty("pager")
	public Pager getPager() {
		return pager;
	}

	@JsonProperty("pager")
	public void setPager(Pager pager) {
		this.pager = pager;
	}

	@JsonProperty("lockExceptions")
	public List<LockException> getLockExceptions() {
		return lockExceptions;
	}

	@JsonProperty("lockExceptions")
	public void setLockExceptions(List<LockException> lockExceptions) {
		this.lockExceptions = lockExceptions;
	}
}
