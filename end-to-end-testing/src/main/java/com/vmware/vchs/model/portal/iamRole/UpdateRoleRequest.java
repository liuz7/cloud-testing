package com.vmware.vchs.model.portal.iamRole;

import com.fasterxml.jackson.annotation.JsonFilter;

import java.util.List;

/**
 * Created by fanz on 5/5/15.
 */
@JsonFilter("updateFilter")
public class UpdateRoleRequest {

    private List<UpdateRoleRequestItem> updateRoleRequestItemList;

    public List<UpdateRoleRequestItem> getUpdateRoleRequestItemList() {
        return updateRoleRequestItemList;
    }

    public void setUpdateRoleRequestItemList(List<UpdateRoleRequestItem> updateRoleRequestItemList) {
        this.updateRoleRequestItemList = updateRoleRequestItemList;
    }
}
