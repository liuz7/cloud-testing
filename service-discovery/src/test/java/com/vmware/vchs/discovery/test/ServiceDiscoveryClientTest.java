package com.vmware.vchs.discovery.test;

/**
 * Created by liuzhiwen on 14-6-23.
 */

import com.google.common.collect.Lists;
import com.vmware.vchs.discovery.InstanceDetail;
import com.vmware.vchs.discovery.ServiceDiscoveryClient;
import com.vmware.vchs.discovery.ServiceDiscoveryClientFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public final class ServiceDiscoveryClientTest {
    private static final String HOST = "http://172.17.8.102:4001";
    private static final String PATH = "services/";
    private static ServiceDiscoveryClient serviceDiscoveryClient;
    private static List<InstanceDetail> testData = Lists.newArrayList();

    @BeforeMethod
    public void setUp() throws Exception {
        serviceDiscoveryClient = ServiceDiscoveryClientFactory.newInstance(HOST);
        testData.add(new InstanceDetail("localhost", 8080, 123));
        testData.add(new InstanceDetail("localhost", 8080, 234));
    }

    @Test
    public void testAddInstance() throws Exception {
        addInstance("node1", testData.get(0));
        Map<String, InstanceDetail> result = listInstances();
        Assert.assertEquals(1, result.size());
        addInstance("node2", testData.get(1));
        result = listInstances();
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.containsValue(testData.get(0)));
        Assert.assertTrue(result.containsValue(testData.get(1)));
    }

    @Test
    public void testDeleteInstance() throws Exception {
        addInstance("node1", testData.get(0));
        addInstance("node2", testData.get(1));
        Map<String, InstanceDetail> result = listInstances();
        Assert.assertEquals(2, result.size());
        deleteInstance("node2");
        result = listInstances();
        Assert.assertEquals(1, result.size());
        Assert.assertTrue(result.containsValue(testData.get(0)));
        Assert.assertFalse(result.containsValue(testData.get(1)));
    }

    @Test
    public void testListInstance() throws Exception {
        addInstance("node1", testData.get(0));
        addInstance("node2", testData.get(1));
        Map<String, InstanceDetail> result = listInstances();
        Assert.assertEquals(2, result.size());
        Assert.assertTrue(result.containsValue(testData.get(0)));
        Assert.assertTrue(result.containsValue(testData.get(1)));
    }

    @Test
    public void testAddInstanceWithTTL() throws Exception {
        addInstanceWithTTL("node1", testData.get(0), 3);
        Map<String, InstanceDetail> result = listInstances();
        Assert.assertEquals(1, result.size());
        Thread.sleep(5000);
        result = listInstances();
        Assert.assertEquals(0, result.size());
        addInstanceWithTTL("node1", testData.get(1), 3);
        Thread.sleep(2000);
        result = listInstances();
        Assert.assertEquals(1, result.size());
    }

    @AfterMethod
    public void tearDown() throws Exception {
        Map<String, InstanceDetail> result = listInstances();
        for (String key : result.keySet()) {
            serviceDiscoveryClient.deleteInstance(key);
        }
        result.clear();
        testData.clear();
    }

    private void addInstance(String instanceId, InstanceDetail instanceDetail) throws Exception {
        serviceDiscoveryClient.addInstance(PATH + instanceId, instanceDetail);
    }

    private void addInstanceWithTTL(String instanceId, InstanceDetail instanceDetail, int ttl) throws Exception {
        serviceDiscoveryClient.addInstanceWithTTL(PATH + instanceId, instanceDetail, ttl);
    }

    private void deleteInstance(String instanceId) throws Exception {
        serviceDiscoveryClient.deleteInstance(PATH + instanceId);
    }

    private Map<String, InstanceDetail> listInstances() throws Exception {
        return serviceDiscoveryClient.listInstances(PATH);
    }
}
