package com.vmware.vchs.test.client.etcd;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;
import com.vmware.vchs.common.utils.JsonInstanceSerializer;
import com.vmware.vchs.test.client.model.etcd.MSSqlNode;
import jetcd.EtcdClient;
import jetcd.EtcdClientFactory;
import jetcd.EtcdException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by georgeliu on 15/4/8.
 */
public class BasicEtcdClient {
    public static final String PATH_NODES_MSSQL = "/dbaasnodes/mssql";
    public static final String PATH_LOCKS_MSSQL = "/dbaaslocks/mssql";

    private EtcdClient client;
    private JsonInstanceSerializer jsonInstanceSerializer;
    private static final Logger logger = LoggerFactory.getLogger(BasicEtcdClient.class);

    public BasicEtcdClient(String serverUrl) {
        logger.info("etcd url is " + serverUrl);
        this.client = EtcdClientFactory.newInstance(serverUrl);
        jsonInstanceSerializer = new JsonInstanceSerializer();
    }

    public String getRaw(String key) {
        try {
            return this.client.get(key);
        } catch (EtcdException e) {
            logger.info(e.toString());
        }
        return "";
    }

    public List<String> listRaw(String path) {
        List<String> result = new ArrayList<>();
        try {
            Iterator iterator;
            try {
                logger.info("etcd path is " + path);
                iterator = this.client.list(path).entrySet().iterator();
            } catch (NullPointerException e) {
                logger.info("No node in etcd.");
                return result;
            }
            while (iterator.hasNext()) {
                Map.Entry pairs = (Map.Entry) iterator.next();
                result.add((String) pairs.getValue());
            }
        } catch (Exception e) {
            logger.info(e.toString());
        }
        com.vmware.vchs.logback.Logger.logList(result);
        return result;
    }

    public <T> T get(String key, Class<T> payloadClass) {
        Object result = null;
        try {
            result = jsonInstanceSerializer.deserialize(this.client.get(key), payloadClass);
        } catch (EtcdException | IOException e) {
            logger.info(e.toString());
        }
        return (T) result;
    }

    public <T> void set(String key, T value) {
        try {
            this.client.set(key, jsonInstanceSerializer.serialize(value));
        } catch (EtcdException | JsonProcessingException e) {
            logger.info(e.toString());
        }
    }

    public <T> List<T> list(String path, Class<T> payloadClass) {
        List<T> result = Lists.newArrayList();
        try {
            Iterator iterator;
            try {
                iterator = this.client.list(path).entrySet().iterator();
            } catch (NullPointerException e) {
                logger.info("No node in etcd.");
                return result;
            }
            while (iterator.hasNext()) {
                Map.Entry pairs = (Map.Entry) iterator.next();
                try {
                    Object etcdNode = jsonInstanceSerializer.deserialize((String) pairs.getValue(), payloadClass);
                    result.add((T) etcdNode);
                } catch (IOException e) {
                    logger.info(e.toString());
                }
            }
        } catch (EtcdException e) {
            logger.info(e.toString());
        }
        return result;
    }

    public List<MSSqlNode> getMSSqlNodes() {
        return this.list(this.PATH_NODES_MSSQL, MSSqlNode.class);
    }
}
