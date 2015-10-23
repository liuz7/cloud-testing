package com.vmware.vchs.instance;

import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.common.utils.exception.RestException;
import com.vmware.vchs.constant.StatusCode;
import com.vmware.vchs.model.portal.common.ListResponse;
import com.vmware.vchs.model.portal.instance.ListInstanceItem;
import org.testng.annotations.Test;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by georgeliu on 14/11/4.
 */
public class ListInstancesTest extends InstanceTest {

    @Test(groups = {"sanity"}, priority = 1)
    public void testListDBInstancesAfterProvision() throws Exception {
        int size = dbaasApi.listDBInstances().getTotal();
        DbaasInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        ListResponse listResponse = dbaasApi.listDBInstances();
        assertThat(listResponse).isNotNull();
        List<ListInstanceItem> responseInstanceList = (List<ListInstanceItem>) listResponse.getData();
        assertThat(responseInstanceList).hasSize(size + 1);
        List<ListInstanceItem> createdResponseInstanceList = responseInstanceList.stream().filter(x -> x.getId().equals(instance.getInstanceid())).collect(Collectors.toList());
        assertThat(createdResponseInstanceList).hasSize(1);
        createdResponseInstanceList.stream().forEach(e -> assertThat(e.getStatus()).isIn(dbaasApi.getAvailableStatusList()));
    }

    @Test
    public void testListDBInstancesDuringProvision() throws Exception {
        int size = dbaasApi.listDBInstances().getTotal();
        DbaasInstance instance = builder.createInstance(getNamePrefix());
        assertThat(instance.getStatus()).isEqualToIgnoringCase(StatusCode.CREATING.value());
        ListResponse listResponse = dbaasApi.listDBInstances();
        assertThat(listResponse).isNotNull();
        List<ListInstanceItem> responseInstanceList = (List<ListInstanceItem>) listResponse.getData();
        assertThat(responseInstanceList).hasSize(size + 1);
        List<ListInstanceItem> createdResponseInstanceList = responseInstanceList.stream().filter(x -> x.getId().equals(instance.getInstanceid())).collect(Collectors.toList());
        assertThat(createdResponseInstanceList).hasSize(1);
        instance.waitAndGetAvailableInstance();
    }

    @Test(groups = {"sanity"}, priority = 1)
    public void testListDBInstancesAfterUnProvision() throws Exception {
        DbaasInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        instance.deleteWithRetry();
        ListResponse listResponse = dbaasApi.listDBInstances();
        assertThat(listResponse).isNotNull();
        List<ListInstanceItem> responseInstanceList = (List<ListInstanceItem>) listResponse.getData();
        // assertThat(responseInstanceList).hasSize(size - 1);
        List<ListInstanceItem> createdResponseInstanceList = responseInstanceList.stream().filter(x -> x.getId().equals(instance.getInstanceid())).collect(Collectors.toList());
        assertThat(createdResponseInstanceList).hasSize(0);
    }

    @Test
    public void testListDBInstancesDuringUnProvision() throws Exception {
        DbaasInstance instance = builder.createInstanceWithRetry(getNamePrefix());
        int size = dbaasApi.listDBInstances().getTotal();
        instance.delete();
        try {
            ListResponse listResponse = dbaasApi.listDBInstances();
            dbaasApi.getDBInstance(instance.getInstanceid());
            assertThat(listResponse).isNotNull();
            List<ListInstanceItem> responseInstanceList = (List<ListInstanceItem>) listResponse.getData();
            assertThat(responseInstanceList).hasSize(size);
            List<ListInstanceItem> createdResponseInstanceList = responseInstanceList.stream().filter(x -> x.getId().equals(instance.getInstanceid())).collect(Collectors.toList());
            assertThat(createdResponseInstanceList).hasSize(1);
        } catch (RestException e) {
            assertThat(e.getStatusCode()).isEqualTo(HttpURLConnection.HTTP_NOT_FOUND);
//            assertThat(((PortalError) e.getError()).getCode()).isEqualTo(PortalErrorMap.PortalStatus.RESOURCE_NOT_FOUND.getCode());
        }
    }

//    @Test
//    public void testListEmptyDBInstances() throws Exception {
//        ListResponse listResponse = dbaasApi.listDBInstances();
//        assertThat(listResponse).isNotNull();
//        List<ListInstanceItem> responseInstanceList = (List<ListInstanceItem>) listResponse.getData();
//        if(responseInstanceList.size()>0){
//            for(ListInstanceItem item:responseInstanceList){
//                dbaasApi.deleteForDBInstance(item.getId());
//                dbaasApi.waitInstanceDeleted(item.getId());
//            }
//            responseInstanceList = (List<ListInstanceItem>) listResponse.getData();
//        }
//        assertThat(responseInstanceList.size()).isZero();
//    }
}
