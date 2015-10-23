package com.vmware.vchs.model.portal.snapshot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vmware.vchs.model.portal.common.Data;

import java.util.List;

/**
 * Created by georgeliu on 15/1/20.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListSnapshotResponse {

    @JsonProperty("data")
    private List<Data> data;

    @JsonProperty("total")
    private int total;

    public List<Data> getData() {
        return data;
    }

    public int getTotal() {
        return total;
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    @JsonProperty("page")
    private int page;

    @JsonProperty("pageSize")
    private int pageSize;
}
