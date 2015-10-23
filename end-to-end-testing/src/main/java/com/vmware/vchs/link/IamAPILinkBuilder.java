package com.vmware.vchs.link;


import com.vmware.vchs.test.client.rest.PathBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by fanz on 5/4/15.
 */

public class IamAPILinkBuilder extends LinkBuilder {

    private static final Logger logger = LoggerFactory.getLogger(IamAPILinkBuilder.class);

    private static String iamBaseURL;
    //private static String api_head_ext_ip;

    public static String IamAPILinkBuider() {
        //this.api_head_ext_ip = api_head_ext_ip;
        iamBaseURL = setIamBaseURL();
        return iamBaseURL;
    }


    public static String setIamBaseURL() {
        if (iamBaseURL == "") {
            iamBaseURL = PathBuilder.newPath().path(IamAPILink.APPSRV.value()).path(IamAPILink.API.value()).path(IamAPILink.VERSION.value()).build();
        }
        logger.info(iamBaseURL);
        return iamBaseURL;
    }

    /**
     * Get Role-Permission
     * a) ~/acm/roles/mssql   --> list roles/permission of global/instance
     * b) ~/acm/assignment/mssql --> list users with roles of global/instance  (GET) admin can
     * c) ~/acm/assignment/mssql --> Update users with roles (PUT, JSON) of global/instance
     * d) ~/users/mssql --> list all users global, search one user global
     */

    public static String getRolePath() {
        return PathBuilder.newPath().path(IamAPILink.APPSRV.value()).path(IamAPILink.API.value()).path(IamAPILink.VERSION.value()).path(IamAPILink.ACM.value()).path(IamAPILink.ROLES.value()).path(IamAPILink.SERVICE.value()).addSeparator().build();
        /**
         *  <apihead:port> /appsrv/api/v1/acm/roles/mssql
         */
    }

    public static String getAccessControlPath() {
        return PathBuilder.newPath().path(IamAPILink.APPSRV.value()).path(IamAPILink.API.value()).path(IamAPILink.VERSION.value()).path(IamAPILink.ACM.value()).path(IamAPILink.ASSIGNMENT.value()).path(IamAPILink.SERVICE.value()).addSeparator().build();
        /**
         *  <apihead:port> /appsrv/api/v1/acm/assignment/mssql
         */
    }

    public static String getUserPath() {
        return PathBuilder.newPath().path(IamAPILink.APPSRV.value()).path(IamAPILink.API.value()).path(IamAPILink.VERSION.value()).path(IamAPILink.USERS.value()).path(IamAPILink.SERVICE.value()).addSeparator().build();
        /**
         *  <apihead:port> /appsrv/api/v1/users/mssql
         */
    }


}
