package com.vmware.vchs.model.portal.networks;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by georgeliu on 15/5/28.
 */
public class UpdateConnectionRequest {

    @JsonProperty("data")
    private UpdateDataPath updateDataPath;

    public UpdateDataPath getUpdateDataPath() {
        return updateDataPath;
    }

    public void setUpdateDataPath(UpdateDataPath updateDataPath) {
        this.updateDataPath = updateDataPath;
    }
}
