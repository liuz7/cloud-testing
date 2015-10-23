package com.vmware.vchs;

import com.vmware.vchs.base.DbaasApi;
import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.launcher.TestHelper;
import com.vmware.vchs.test.client.db.model.Employee;
import com.vmware.vchs.utils.CommonUtils;
import com.vmware.vchs.utils.RandomEmployee;
import org.testng.annotations.*;

import java.lang.reflect.Method;

import static com.vmware.vchs.test.client.db.SQLStatements.DB_TESTDB;

/**
 * Created by georgeliu on 14/12/15.
 */
public class SnapshotBaseTest extends InstanceBaseTest {

    protected DbaasInstance instance;
    protected Employee testEmployee1 = RandomEmployee.getRandomEmployee();
    protected boolean preCreateInstance=true;

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() throws Exception {
        logger.info("Setup for snapshot test suite...");
        super.setUpSuite();
    }

    @BeforeClass(alwaysRun = true)
    public void setUpClass() throws Exception {
        try {
            logger.info("Setup for snapshot test class...");
            super.setUpClass();
            builder = new DbaasInstance.DbaasInstanceBuilder(new DbaasApi(TestHelper.getConfiguration()));
            if(this.preCreateInstance) {
                instance = builder.createInstanceWithRetry(getNamePrefix());
                instance.updatePreferredStartTime();
                instance.insertData(DB_TESTDB, testEmployee1);
            }
        } catch (Exception e) {
            logger.info(Utils.getStackTrace(e));
        }
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod(Method method) throws Exception {
        logger.info("Setup for snapshot test method...");
        super.setUpMethod(method);
        if(this.preCreateInstance) {
            preCreateInstance();
        }

    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws Exception {
        super.tearDownClass();
        try {
            logger.info("Tear down for snapshot test class...");
            dbaasApi.cleanInstance(this.getClass().getName());
            dbaasApi.cleanSnapshots(this.getClass().getName());
        } catch (Exception e) {
            logger.info(Utils.getStackTrace(e));
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownMethod(Method method) throws Exception {
        super.tearDownMethod(method);
        try {
            logger.info("Tear down for snapshot test method...");
            dbaasApi.cleanSnapshots(CommonUtils.generateNamePrefix(this.getClass().getName(), method.getName()));
        } catch (Exception e) {
            logger.info(Utils.getStackTrace(e));
        }
    }
    protected void preCreateInstance() throws Exception {
        if (this.instance != null) {
            if (instance.checkAvailability()) {
                return;
            }
        }
        this.instance = builder.createInstanceWithRetry(getNamePrefix());
        this.instance.updatePreferredStartTime();
        if (this.instance == null) {
            throw new Exception("create instance for snapshot failed");
        }
        instance.insertData(DB_TESTDB, testEmployee1);
    }
}
