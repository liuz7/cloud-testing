package com.vmware.vchs.instance;

import com.vmware.vchs.base.BaseDataProvider;
import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.common.utils.exception.RestException;
import com.vmware.vchs.launcher.TestHelper;
import com.vmware.vchs.model.portal.common.AsyncResponse;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.model.portal.instance.UpdateInstanceRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Created by georgeliu on 14/11/4.
 */
public class UpdateInstanceWithoutPreCreateInstanceTest extends InstanceTest {

    private static final Logger logger = LoggerFactory.getLogger(UpdateInstanceWithoutPreCreateInstanceTest.class);

    @BeforeClass(alwaysRun = true)
    public void setUpClass() throws Exception {
        logger.info("Setup for update instance test class...");
        super.setUpClass();
    }


    @Test(groups = {"alpha"}, dataProvider = "can be updated", dataProviderClass = BaseDataProvider.class)
    public void testUpdateDuringProvisionInstance(UpdateInstanceRequest updateInstanceRequest) throws Exception {
        DbaasInstance instance = builder.createInstance(getNamePrefix());
        try {
            instance.updateInstanceWithRetry(updateInstanceRequest);
            fail("Update instance in creating status should return portal error.");
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
        } finally {
            // ensure the instance exit creating status, so it could be deleted correctly.
            instance.waitAndGetAvailableInstance();
        }
    }

    @Test(dataProvider = "can be updated", dataProviderClass = BaseDataProvider.class)
    public void testUpdateDuringSnapshot(UpdateInstanceRequest updateInstanceRequest) throws Exception {
        DbaasInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        AsyncResponse response = instance.createSnapshot();
        try {
            instance.updateInstance(updateInstanceRequest);
            fail("Update instance in snapshotting status should return portal error.");
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_BAD_REQUEST);
        } finally {
            // ensure the snapshot exit creating status, so it & it's source instance could be deleted correctly.
            dbaasApi.waitAndGetAvailableSnapshot(response.getId());
        }
    }

    @Test(groups = {"alpha"}, dataProvider = "can be updated concurrently", dataProviderClass = BaseDataProvider.class)
    public void testConcurrentUpdateToSingleInstance(List<UpdateInstanceRequest> updateInstanceRequestList) throws Exception {
        DbaasInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        for (UpdateInstanceRequest updateInstanceRequest : updateInstanceRequestList) {
            try {
                instance.updateInstance(updateInstanceRequest);
            }
            catch (Exception e){
                logger.info("Concurrently update failed");
            }
        }
        GetInstanceResponse instanceResponse = dbaasApi.waitAndGetAvailableInstance(instance.getInstanceid());
        dbaasApi.verifySuccessfulUpdatedInstanceWithConcurrentData(instanceResponse, updateInstanceRequestList);
        //TODO: verify the later request take effect
    }

    @Test(groups = {"alpha"}, dataProvider = "can be updated concurrently", dataProviderClass = BaseDataProvider.class)
    public void testConcurrentUpdateToMultipleInstances(List<UpdateInstanceRequest> updateInstanceRequestList) throws Exception {
        int size = Integer.parseInt(TestHelper.getConfiguration().getThreadSizeForInstance());
        List<DbaasInstance> instanceList = new ArrayList<>();
        List<GetInstanceResponse> instanceResponseList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            instanceList.add(builder.createInstanceWithRetry(getNamePrefix()));
        }
        for (int i = 0; i < size; i++) {
                             instanceList.get(i).updateInstance(updateInstanceRequestList.get(i));
        }
        for (int i = 0; i < size; i++) {
            instanceResponseList.add(dbaasApi.waitAndGetAvailableInstance(instanceList.get(i).getInstanceid()));
        }
        for (int i = 0; i < size; i++) {
            dbaasApi.verifyUpdatedInstanceWithData(instanceResponseList.get(i), updateInstanceRequestList.get(i), true);
        }
    }

}
