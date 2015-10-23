package com.vmware.vchs.model.portal.iamRole;

import java.util.List;

/**
 * Created by fanz on 5/27/15.
 */
public class GetUserResponse {

    private List<UserItemResponse> userItemResponses;

    public List<UserItemResponse> getUserItemResponses() {
        return userItemResponses;
    }

    public void setUserItemResponses(List<UserItemResponse> userItemResponses) {
        this.userItemResponses = userItemResponses;
    }
}
