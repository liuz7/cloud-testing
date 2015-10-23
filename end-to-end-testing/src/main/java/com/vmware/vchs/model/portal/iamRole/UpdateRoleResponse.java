package com.vmware.vchs.model.portal.iamRole;

import java.util.List;

/**
 * Created by fanz on 5/6/15.
 */
public class UpdateRoleResponse {

    private List<UpdateRoleResponseItem> updateRoleResponseItemList;

    public List<UpdateRoleResponseItem> getUpdateRoleResponseItemList() {
        return updateRoleResponseItemList;
    }

    public void setUpdateRoleResponseItemList(List<UpdateRoleResponseItem> updateRoleResponseItemList) {
        this.updateRoleResponseItemList = updateRoleResponseItemList;
    }
}
