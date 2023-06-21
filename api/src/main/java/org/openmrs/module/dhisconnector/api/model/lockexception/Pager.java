package org.openmrs.module.dhisconnector.api.model.lockexception;

import javax.annotation.Generated;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Generated("org.jsonschema2pojo")
@JsonPropertyOrder({ "page", "pageCount", "total", "pageSize" })
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pager {

	@JsonProperty("page")
	private Integer page;
	@JsonProperty("pageCount")
	private Integer pageCount;
	@JsonProperty("total")
	private Integer total;
	@JsonProperty("pageSize")
	private Integer pageSize;

	public Pager() {
		super();
	}

	public Pager(Integer page, Integer pageCount, Integer total, Integer pageSize) {
		this.page = page;
		this.pageCount = pageCount;
		this.total = total;
		this.pageSize = pageSize;
	}

	@JsonProperty("page")
	public Integer getPage() {
		return page;
	}

	@JsonProperty("page")
	public void setPage(Integer page) {
		this.page = page;
	}

	@JsonProperty("pageCount")
	public Integer getPageCount() {
		return pageCount;
	}

	@JsonProperty("pageCount")
	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	@JsonProperty("total")
	public Integer getTotal() {
		return total;
	}

	@JsonProperty("total")
	public void setTotal(Integer total) {
		this.total = total;
	}

	@JsonProperty("pageSize")
	public Integer getPageSize() {
		return pageSize;
	}

	@JsonProperty("pageSize")
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

}
