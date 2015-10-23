package com.vmware.vchs.datapath;

import com.vmware.vchs.base.DbaasApi;
import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.base.E2ETest;
import com.vmware.vchs.launcher.TestHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Created by sjun on 9/23/15.
 */
@Test(groups = {"datapath", "datapath-permission", "full"})
public class MssqlPermissionTest implements E2ETest {
    protected static final Logger logger = LoggerFactory.getLogger(MssqlPermissionTest.class);
    protected static final String TEST_DB = "test_permission_db";
    protected static final String TEST_TABLE = "test_table";

    protected String masterUser = null;
    protected DbaasInstance instance;
    protected MssqlConnection mssqlConnection;

    protected DbaasApi dbaasApi = new DbaasApi(TestHelper.getConfiguration());

    @BeforeMethod
    public void prepareMssqlConnection(Method method) throws Exception {
        if (instance == null) {
            instance = new DbaasInstance.DbaasInstanceBuilder(dbaasApi).createInstanceWithRetry(this.getClass().getName());
        }

        masterUser = instance.getInstanceResponse().getMasterUsername();
        mssqlConnection = instance.getMssqlConnection();
        mssqlConnection.createDatabase(TEST_DB);
        DataPathTestModel.createTable(mssqlConnection, TEST_DB, TEST_TABLE);
    }

    @AfterClass
    public void cleanInstance() throws Exception {
        if (instance != null) {
            instance.deleteWithRetry();
        }
    }

    @Test
    public void testUserPermissions() {
        final String testUser = "User_" + UUID.randomUUID().toString().replaceAll("-", "");
        final String testPassword = "testPassword";
        final String queryTestUserCount = String.format("SELECT count(*) FROM sys.server_principals where name='%s'", testUser);

        //create test user & verify created user exists
        String query = String.format("CREATE LOGIN %s WITH PASSWORD='%s'", testUser, testPassword);
        mssqlConnection.getTemplate().execute(query);
        int count = mssqlConnection.getTemplate().queryForObject(queryTestUserCount, Integer.class);
        assertThat(count).isEqualTo(1);

        //create user for testdb
        query = String.format("USE %s; CREATE USER %s FOR LOGIN %s", TEST_DB, testUser, testUser);
        mssqlConnection.getTemplate().execute(query);

        //verify test user could connect testdb
        MssqlConnection mssqlConnectionForTestUser = new MssqlConnection(instance.getInstanceResponse().getIpAddress(),
                String.valueOf(instance.getInstanceResponse().getConnections().getDataPath().getDestPort()),
                testUser, testPassword);
        mssqlConnectionForTestUser.useDatabase(TEST_DB);
        mssqlConnectionForTestUser.close();

        //verify role for master user and test user
        query = String.format("%s.sys.sp_helpuser", TEST_DB);
        List<Map<String, Object>> results = mssqlConnection.getTemplate().queryForList(query);
        Map<String, Object> masterUserData = results.stream().filter(m -> masterUser.equals(m.get("LoginName"))).findAny().get();
        assertThat(masterUserData).isNotNull();
        assertThat(masterUserData.get("RoleName")).isEqualTo("db_owner");

        Map<String, Object> testUserData = results.stream().filter(m -> testUser.equals(m.get("LoginName"))).findAny().get();
        assertThat(testUserData).isNotNull();
        assertThat(testUserData.get("RoleName")).isEqualTo("public");

        //delete test user & verify test user is deleted
        query = String.format("DROP LOGIN %s", testUser);
        mssqlConnection.getTemplate().execute(query);
        count = mssqlConnection.getTemplate().queryForObject(queryTestUserCount, Integer.class);
        assertThat(count).isEqualTo(0);
    }

    @Test
    public void testMasterUserNoBackupPermission() {
        //verify master use has no backup permission
        String query = String.format("BACKUP DATABASE %s TO DISK='C:\\test.bak'", TEST_DB);
        try {
            mssqlConnection.getTemplate().execute(query);
            fail("Master user should not have backup permisison to " + TEST_DB);
        } catch (DataAccessException exception) {
            logger.info("Verified master user has no backup permisison to {}.", TEST_DB);
        }
    }

    @Test
    public void testSAUserDisabled() {
        List<Map<String, Object>> principals = mssqlConnection.getTemplate().queryForList("SELECT * FROM sys.server_principals");
        Map<String, Object> saPrincipal = principals.stream().filter(m->m.get("name").equals("sa")).findAny().get();
        if (saPrincipal != null) {
            assertThat(saPrincipal.get("is_disabled")).isEqualTo(true);
        }
    }

    @Test
    public void testAccessVCHSDatabaseFailed() {
        List<String> databases = mssqlConnection.getTemplate().queryForList("SELECT name FROM master.sys.databases", String.class);
        for (String database: databases) {
            if (database.startsWith("vCHS_")) {
                try {
                    mssqlConnection.useDatabase(database);
                    fail("Master user should not have permisison to " + database);
                } catch (DataAccessException exception) {
                    logger.info("Verified master user has no permisison to {}.", database);
                }
            }
        }
    }
}
