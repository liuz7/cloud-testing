package com.vmware.vchs.misc;

import com.google.common.collect.Lists;
import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.instance.InstanceTest;
import com.vmware.vchs.model.portal.instance.Connections;
import com.vmware.vchs.model.portal.instance.DataPath;
import com.vmware.vchs.model.portal.instance.GetInstanceResponse;
import com.vmware.vchs.model.portal.networks.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by georgeliu on 15/4/1.
 */
@Test(groups = {"snsonly"})
public class NetworkTest extends InstanceTest {

    private static final Logger logger = LoggerFactory.getLogger(NetworkTest.class);

    @Test
    public void testListVdcIps() throws Exception {
        ListVdcIpsResponse listVdcIpsResponse = dbaasApi.getVdcIps();
        assertThat(listVdcIpsResponse).isNotNull();
    }

    @Test
    public void testUpdateNetworkConnectionWithAddIps() throws Exception {
        DbaasInstance instance =builder.createInstanceWithRetry(getNamePrefix());
        ListVdcIpsResponse listVdcIpsResponse = dbaasApi.getVdcIps();
        assertThat(listVdcIpsResponse).isNotNull();
        List<String> ipList = getIPList(listVdcIpsResponse);
        if (ipList.size() != 0) {
            assertThat(ipList.size()).isPositive();
            UpdateConnectionRequest updateConnectionRequest = new UpdateConnectionRequest();
            updateConnectionRequest.getUpdateDataPath().setAddIPs(ipList);
            DataPath dataPath = instance.updateConnection(updateConnectionRequest);
            assertThat(dataPath.getAllowedIPs().containsAll(ipList)).isTrue();
            Connections connections = instance.getDataPath();
            assertThat(connections.getDataPath().getAllowedIPs().containsAll(ipList)).isTrue();
            assertThat(instance.isConnected()).isTrue();
        } else {
            logger.info("No region found.");
            throw new Exception("No region found.");
        }

    }

    @Test
    public void testUpdateNetworkConnectionWithRevokeIps() throws Exception {
        DbaasInstance instance =builder.createInstanceWithRetry(getNamePrefix());
        ListVdcIpsResponse listVdcIpsResponse = dbaasApi.getVdcIps();
        assertThat(listVdcIpsResponse).isNotNull();
        List<String> ipList = getIPList(listVdcIpsResponse);
        if (ipList.size() != 0) {
            assertThat(ipList.size()).isPositive();
            UpdateConnectionRequest updateConnectionRequest = new UpdateConnectionRequest();
            updateConnectionRequest.getUpdateDataPath().setRevokeIPs(ipList);
            DataPath dataPath = instance.updateConnection(updateConnectionRequest);
            assertThat(dataPath.getAllowedIPs().containsAll(ipList)).isFalse();
            Connections connections = instance.getDataPath();
            assertThat(connections.getDataPath().getAllowedIPs().containsAll(ipList)).isFalse();
            assertThat(instance.isConnected()).isTrue();
        } else {
            logger.info("No region found.");
            throw new Exception("No region found.");
        }
    }

    private List<String> getIPList(ListVdcIpsResponse listVdcIpsResponse) {
        List<Region> regionList = listVdcIpsResponse.getData().getRegions();
        List<String> ipList = Lists.newArrayList();
        if (regionList.size() != 0) {
            List<Org> orgList = regionList.get(0).getOrgs();
            if (orgList.size() != 0) {
                List<VDC> vdcList = orgList.get(0).getVdcs();
                if (vdcList.size() != 0) {
                    ipList = vdcList.get(0).getIps();
                }
            }
        }
        return ipList;
    }
}
