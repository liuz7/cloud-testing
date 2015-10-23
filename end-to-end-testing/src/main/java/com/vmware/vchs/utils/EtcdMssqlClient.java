package com.vmware.vchs.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vmware.vchs.test.client.model.etcd.MSSqlNode;
import com.vmware.vchs.test.client.etcd.BasicEtcdClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by georgeliu on 15/4/9.
 */
public class EtcdMssqlClient extends BasicEtcdClient {

    public EtcdMssqlClient(String serverUrl) {
        super(serverUrl);
    }

    public String getNodeStatusByInstanceId(String instanceId) {
        String result = null;
        List<MSSqlNode> mssqlNodes = this.getMSSqlNodes();
        for (MSSqlNode mssqlNode : mssqlNodes) {
            if (Arrays.asList(mssqlNode.getInstances()).contains(instanceId)) {
                result = mssqlNode.getState();
                break;
            }
        }
        return result;
    }

    public List<String> getAllNodeId() {
        List<String> result = Lists.newArrayList();
        List<MSSqlNode> mssqlNodes = this.getMSSqlNodes();
        mssqlNodes.forEach(e -> result.add(e.getId()));
        return result;
    }

    public Map<String, String> getAllNodeStatus() {
        Map<String, String> result = Maps.newHashMap();
        List<MSSqlNode> mssqlNodes = this.getMSSqlNodes();
        mssqlNodes.forEach(e -> result.put(e.getId(), e.getState()));
        return result;
    }

    public String getNodeStatusByNodeId(String nodeId) {
        return getAllNodeStatus().get(nodeId);
    }

}
