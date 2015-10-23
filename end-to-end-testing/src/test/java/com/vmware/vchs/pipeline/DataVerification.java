package com.vmware.vchs.pipeline;

import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.testng.annotations.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by fanz on 8/20/15.
 */
public class DataVerification extends PipelineBase {
    private static final Logger logger = LoggerFactory.getLogger(DataVerification.class);

    public String snapshotInstanceId;
    private GetInstanceResponse provisionInstanceResponse;

    public static String databaseUsername;
    public static SQLServerClient sqlServerClientDataVerification ;


    public static List<String> ipAddressList = new ArrayList<>();

    public final static String dataProvisionNamePrefix = "ProvisionDataForPipelineTest";

    @Test(groups={"pipeline", "verifyData"})
    public void verifyData() throws Exception {
        try {
//            listAllInstance();
//            ipAddressList = retrieveInstanceIP();

            getProvisionedInstance(dataProvisionNamePrefix);
            logger.info(" ProvisionData Restored Instance ID : " + verifyInstanceId);
            logger.info(" ProvisionData Restored instance IP address: " + verifyInstanceIPAddress);
            logger.info(" ProvisionData Restored instance Creator Email: " + verifyInstanceCreatorEmail);
            assertThat(verifyInstanceId).isNotNull();
            assertThat(verifyInstanceIPAddress).isNotNull();
            databaseUsername = getInstanceUser(verifyInstanceId);
            assertThat(databaseUsername).isNotNull();

//            List<ListInstanceItem> listInstanceItems = checkNotNull((List<ListInstanceItem>) listResponse.getData());
//            for (ListInstanceItem listInstanceItem : listInstanceItems) {
//                if (listInstanceItem.getId() != null && ipAddressList.contains(listInstanceItem.getIpAddress())) {
//                    verifyInstanceId = listInstanceItem.getId();
//                    verifyInstanceIPAddress = listInstanceItem.getIpAddress();
//                    break;
//                }
//            }
            if (verifyInstanceIPAddress != null ) {
                sqlServerClientDataVerification = new SQLServerClient(verifyInstanceIPAddress, databaseUsername, DBuserPassword, verifyInstancePort);
                if (sqlServerClientDataVerification.isConnected()) {
                    assertThat(verifyTable(sqlServerClientDataVerification));
                    assertThat(verifyTableData(sqlServerClientDataVerification, databaseName, tableName));
                }
            }

        } catch (DataAccessException e) {
            logger.info("Can not verify Data Test, test failed !!!!  ");
            e.printStackTrace();
        } catch (SQLException e) {
            logger.info("Can not operate SQL server. ");
            e.printStackTrace();
        }

    }

    public SQLServerClient getSQLServerConnection() {
        return this.sqlServerClientDataVerification;
    }
}
