package com.vmware.vchs;


import com.vmware.vchs.base.BaseTest;
import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.utils.CommonUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

/**
 * Created by georgeliu on 14/11/3.
 */
@Test(groups = {"full"}/*,dependsOnGroups = {"initPortal.*"}*/)
public class InstanceBaseTest extends BaseTest {

    protected DbaasInstance.DbaasInstanceBuilder builder;

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod(Method method) throws Exception {
        try {
            logger.info("Setup for instance base test method...");
            super.setUpMethod(method);
            builder = new DbaasInstance.DbaasInstanceBuilder(dbaasApi);
            dbaasApi.deleteRemainingInstances();
        } catch (Exception e) {
            logger.info(Utils.getStackTrace(e));
        }
    }



    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws Exception {
        try {
            logger.info("Tear down for instance base test class...");
            super.tearDownClass();
        } catch (Exception e) {
            logger.info(Utils.getStackTrace(e));
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownMethod(Method method) throws Exception {
        try {
            logger.info("Tear down for instance base test method...");
            super.tearDownMethod(method);
            dbaasApi.cleanInstance(CommonUtils.generateNamePrefix(this.getClass().getName(), method.getName()));
        } catch (Exception e) {
            logger.info(Utils.getStackTrace(e));
        }
    }


}
