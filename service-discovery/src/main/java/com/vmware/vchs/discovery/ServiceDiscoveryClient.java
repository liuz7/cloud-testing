package com.vmware.vchs.discovery;

import com.google.common.collect.Maps;
import com.vmware.vchs.common.utils.JsonInstanceSerializer;
import jetcd.EtcdClient;
import jetcd.EtcdClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by liuzhiwen on 14-7-8.
 */
public class ServiceDiscoveryClient {

    private String host;
    private EtcdClient client;
    private JsonInstanceSerializer jsonInstanceSerializer;
    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscoveryClient.class);

    public ServiceDiscoveryClient(String host) {
        this.host = host;
        client = EtcdClientFactory.newInstance(this.host);
        jsonInstanceSerializer = new JsonInstanceSerializer<>(InstanceDetail.class);
    }

    public InstanceDetail getInstance(String instanceId) throws Exception {
        String result = client.get(instanceId);
        InstanceDetail instanceDetail = (InstanceDetail) jsonInstanceSerializer.deserialize(result);
        logger.info("Instance:" + instanceId + " got." + instanceDetail);
        return instanceDetail;
    }

    public void addInstance(String instanceId, InstanceDetail instanceDetail) throws Exception {
        client.set(instanceId, jsonInstanceSerializer.serialize(instanceDetail));
        logger.info("Instance:" + instanceId + " added." + instanceDetail);
    }

    public void addInstanceWithTTL(String instanceId, InstanceDetail instanceDetail, int ttl) throws Exception {
        client.set(instanceId, jsonInstanceSerializer.serialize(instanceDetail), ttl);
        logger.info("Instance:" + instanceId + " with TTL=" + ttl + " added." + instanceDetail);
    }

    public void deleteInstance(String instanceId) throws Exception {
        client.delete(instanceId);
        logger.info("Instance:" + instanceId + " deleted.");
    }

    public Map<String, InstanceDetail> listInstances(String path) throws Exception {
        Map<String, InstanceDetail> result = Maps.newHashMap();
        Map<String, String> etcdResult = null;
        try {
            etcdResult = client.list(path);
        } catch (NullPointerException npe) {
            logger.info("No result found by " + path);
        }
        if (etcdResult != null) {
            for (Map.Entry<String, String> entry : etcdResult.entrySet()) {
                String key = entry.getKey();
                result.put(key, getInstance(key));
            }
        }
        return result;
    }
}
