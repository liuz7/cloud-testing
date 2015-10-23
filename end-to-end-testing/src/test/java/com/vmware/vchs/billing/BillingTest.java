package com.vmware.vchs.billing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.vchs.InstanceBaseTest;
import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.billing.model.Event;
import com.vmware.vchs.common.utils.exception.RestException;
import com.vmware.vchs.constant.Constants;
import com.vmware.vchs.model.billing.MeterModel;
import com.vmware.vchs.model.constant.ContentModel;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.utils.BillingUtils;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;

/**
 * Created by david on 15/3/4.
 */
@Test(groups = {"billing"})
public class BillingTest extends InstanceBaseTest {
    ObjectMapper mapper = new ObjectMapper();


    @Test(groups = {"sanity"}, priority = 0)
    public void testBillingFromProvisionDB() throws Exception {
        MeterModel meterModel = new MeterModel();
        DbaasInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        List<Event> results = instance.getEvents();
        for (Event event : results) {
            logger.info("event is " + event.toString());
        }
        assertThat(results.size()).isGreaterThanOrEqualTo(1);
        for (Event event : results) {
            assertThat(event.getInstanceId()).isEqualToIgnoringCase(instance.getInstanceid());
            assertThat(event.getService_name()).isEqualToIgnoringCase(Constants.SERVICE_NAME);
//            assertThat(event.getStatus()).isEqualToIgnoringCase(Status.UNDELIVERED.toString());
            ContentModel content = mapper.readValue(event.getContent(), ContentModel.class);
            logger.info("content is " + content.toString());
            checkNotNull(content);
            checkNotNull(content.getMeters());
            meterModel = BillingUtils.getMeter(content.getMeters(), instance.getInstanceResponse(), configuration.getPlanName().toLowerCase(), meterModel);
        }
        assertThat(meterModel.getStorage()).isEqualTo(instance.getInstanceResponse().getDiskSize());
        assertThat(meterModel.getPlan()).isEqualTo(BillingUtils.PROVISION);
    }


    @Test
    public void testBillingFromUnProvisionDB() throws Exception {
        DbaasInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        instance.deleteEvents();
        dbaasApi.cleanInstance(this.getClass().getName());

        Event event = instance.getEvents().get(0);
        logger.info("event is " + event.toString());

        assertThat(event.getInstanceId()).isEqualToIgnoringCase(instance.getInstanceid());
        assertThat(event.getService_name()).isEqualToIgnoringCase(Constants.SERVICE_NAME);
//      assertThat(event.getStatus()).isEqualToIgnoringCase(Status.UNDELIVERED.toString());
        ContentModel content = mapper.readValue(event.getContent(), ContentModel.class);
        logger.info("content is " + content.toString());
        checkNotNull(content);
        checkNotNull(content.getMeters());
        MeterModel meterModel = BillingUtils.getMeter(content.getMeters(),
                instance.getInstanceResponse(),
                configuration.getPlanName().toLowerCase(),
                new MeterModel());

        assertThat(meterModel.getStorage()).isEqualTo(0);
        assertThat(meterModel.getPlan()).isEqualTo(BillingUtils.UNPROVISION);
    }

    @Test
    public void testNoBillingFromProvisionDBFail() throws Exception {
        int beforeCount = dbaasApi.getEventsCount();
        try {
            builder.setIsPlanEmpty(true).createInstance(getNamePrefix());
            failBecauseExceptionWasNotThrown(RestException.class);
        } catch (RestException e) {
            int afterCount = dbaasApi.getEventsCount();
            assertThat(beforeCount).isEqualTo(afterCount);
        }
    }

    @Test
    public void testNoBillingFromUnProvisionDBFailed() throws Exception {
        int beforeCount = dbaasApi.getEventsCount();
        dbaasApi.deleteForDBInstance("1111");
        int afterCount = dbaasApi.getEventsCount();
        assertThat(beforeCount).isEqualTo(afterCount);
    }

    @Test
    public void testBillingFromConcurrentProvisionDB() throws Exception {
        MeterModel meterModel = new MeterModel();
        int threadSize = getThreadSizeForInstance();
        List<DbaasInstance> instanceList = new ArrayList<>();
        for (int i = 0; i < threadSize; i++) {
            instanceList.add(builder.createInstance(getNamePrefix()));
        }
        for (int i = 0; i < threadSize; i++) {
            GetInstanceResponse response = instanceList.get(i).waitAndGetAvailableInstance();
            assertThat(response.getStatus()).isIn(dbaasApi.getAvailableStatusList());
        }
        assertThat(threadSize).isEqualTo(instanceList.size());

        for (DbaasInstance instance : instanceList) {
            List<Event> results = instance.getEvents();
            for (Event event : results) {
                logger.info("event is " + event.toString());
            }
            assertThat(results.size()).isGreaterThanOrEqualTo(1);
            for (Event event : results) {
                logger.info("event is " + event.toString());
                assertThat(event.getInstanceId()).isIn(instanceList.stream().map(sc -> sc.getInstanceid()).collect(Collectors.toList()));
                assertThat(event.getService_name()).isEqualToIgnoringCase(Constants.SERVICE_NAME);
//                assertThat(event.getStatus()).isEqualToIgnoringCase(Status.UNDELIVERED.toString());
                ContentModel content = mapper.readValue(event.getContent(), ContentModel.class);
                logger.info("content is " + content.toString());
                checkNotNull(content);
                checkNotNull(content.getMeters());
                meterModel = BillingUtils.getMeter(content.getMeters(), instance.getInstanceResponse(), configuration.getPlanName().toLowerCase(), meterModel);
            }
            assertThat(meterModel.getStorage()).isEqualTo(instance.getInstanceResponse().getDiskSize());
            assertThat(meterModel.getPlan()).isEqualTo(BillingUtils.PROVISION);
        }
    }


    @Test
    public void testBillingFromProvisionDBDuringUnProvision() throws Exception {
        DbaasInstance instanceToBeUnProvision = builder.createInstanceWithRetry(getNamePrefix());
        //instanceToBeUnProvision.deleteEvents();

        logger.info("delete and create instanceToBeUnProvision");
        instanceToBeUnProvision.delete();
        DbaasInstance instanceToBeProvision = null;
        try {
            instanceToBeProvision = builder.createInstance(getNamePrefix());
            GetInstanceResponse instanceCreated2 = instanceToBeProvision.waitAndGetAvailableInstance();
            assertThat(instanceCreated2.getStatus()).isIn(dbaasApi.getAvailableStatusList());
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
        }
        instanceToBeUnProvision.waitInstanceDeleted();

        {
            logger.info("Verify the deleted instanceToBeUnProvision event");
            Event event = instanceToBeUnProvision.getEvents(2).get(1);
            logger.info("event is " + event.toString());
            if (instanceToBeUnProvision == null) {
                throw new Exception("create instanceToBeUnProvision failed");
            }
            assertThat(event.getInstanceId()).isIn(instanceToBeUnProvision.getInstanceid(), instanceToBeProvision.getInstanceid());
            assertThat(event.getService_name()).isEqualToIgnoringCase(Constants.SERVICE_NAME);
//          assertThat(event.getStatus()).isEqualToIgnoringCase(Status.UNDELIVERED.toString());
            ContentModel content = mapper.readValue(event.getContent(), ContentModel.class);
            logger.info("content is " + content.toString());
            checkNotNull(content);
            checkNotNull(content.getMeters());
            MeterModel meterModel = BillingUtils.getMeter(content.getMeters(),
                    instanceToBeUnProvision.getInstanceResponse(),
                    configuration.getPlanName().toLowerCase(), new MeterModel());
            assertThat(meterModel.getStorage()).isEqualTo(0);
        }

        {
            logger.info("Verify the create instanceToBeProvision event");
            List<Event> results = instanceToBeProvision.getEvents();
            for (Event event : results) {
                logger.info("event is " + event.toString());
            }
            assertThat(results.size()).isGreaterThanOrEqualTo(1);

            MeterModel meterModel = new MeterModel();
            for (Event event : results) {
                if (instanceToBeProvision == null) {
                    throw new Exception("create instanceToBeUnProvision failed");
                }
                assertThat(event.getInstanceId()).isIn(instanceToBeUnProvision.getInstanceid(), instanceToBeProvision.getInstanceid());
                assertThat(event.getService_name()).isEqualToIgnoringCase(Constants.SERVICE_NAME);
//              assertThat(event.getStatus()).isEqualToIgnoringCase(Status.UNDELIVERED.toString());
                ContentModel content = mapper.readValue(event.getContent(), ContentModel.class);
                logger.info("content is " + content.toString());
                checkNotNull(content);
                checkNotNull(content.getMeters());
                meterModel = BillingUtils.getMeter(content.getMeters(),
                        instanceToBeProvision.getInstanceResponse(),
                        configuration.getPlanName().toLowerCase(),
                        meterModel);
            }
            assertThat(meterModel.getStorage()).isEqualTo(instanceToBeProvision.getInstanceResponse().getDiskSize());
        }
    }
}

