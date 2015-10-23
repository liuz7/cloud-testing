/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */
package com.vmware.vchs.tmclient;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class ServiceConfig {
    private final Config config;

    public ServiceConfig(String filepath) {
        File file = new File(filepath);
        config = ConfigFactory.parseFile(file).withFallback(ConfigFactory.load());
    }

    public ServiceConfig() {
        config = ConfigFactory.load();
    }

    public String getString(final String path) {
        return getConfig().getString(path);
    }

    public int getInt(final String path) {
        return getConfig().getInt(path);
    }

    public String getMessageBusServer() {
        return config.getString("vchs.messagebus.host") + ":" + config.getInt("vchs.messagebus.port");
    }

    public String getMessageBusUsername() {
        return config.getString("vchs.messagebus.username");
    }

    public String getMessageBusPassword() {
        return config.getString("vchs.messagebus.password");
    }

    public String getRequestChannel() {
        return config.getString("vchs.messagebus.request");
    }

    public String getEventChannel() {
        return config.getString("vchs.messagebus.event");
    }

    public String getNotificationChannelTemplate() {
        return config.getString("vchs.messagebus.notificationTemplate");
    }

    public Config getMembershipConfig() {
        return config.getConfig("vchs.membership");
    }

    public Config getDDSConfig() {
        return config.getConfig("vchs.dds");
    }

    public Config getConfig() {
        return config;
    }

    public String getTMPub() {
        return config.getString("vchs.tm.pub");
    }

    public String getNode() {
        return config.getString("vchs.messagebus.node");
    }

    public String getTMPubURI() {
        return getNatsURI(getTMPub());
    }

    public String getNodePubURI(String channel) {
        return getNatsURI(channel);
    }

    public String getTMQuery() {
        return config.getString("vchs.tm.query");
    }

    public String getTMQueryURI() {
        return getNatsURI(getTMQuery());
    }

    public String getTMNodePubPrefix() {
        return config.getString("vchs.tm.nodepubprefix");
    }

    public String getTMNodePubURIPrefix() {
        return getNatsURI(getTMNodePubPrefix());
    }

    public String getTMGWAck() {
        return config.getString("vchs.tm.gwack");
    }

    public String getTMGWAckURI() {
        return getNatsURI(getTMGWAck());
    }

    public String getTMGWPub() {
        return config.getString("vchs.tm.gwpub");
    }

    public String getTMGWPubURI() {
        return getNatsURI(getTMGWPub());
    }

    public String getQueueFromTopic(String topic) {
        return "queue@" + topic;
    }

    public String getNatsURI(String channel) {
        return "nats://" + getMessageBusServer() + "/topic/" + channel;
    }

    public String getTMSerializationYype() {
        return config.getString("vchs.tm.serializationtype");
    }

    public Config getTaskTimeoutConfig() {
        return config.getConfig("vchs.tm.timeout");
    }

    public boolean inDebugMode() {
        return config.getBoolean("vchs.debug");
    }

    public boolean getCBEtcdCircuitBreakerEnable() {
        return config.getBoolean("vchs.cb.etcd.circuitBreakerEnable");
    }

    public int getCBEtcdNumberOfSlots() {
        return config.getInt("vchs.cb.etcd.numberOfSlots");
    }

    public long getCBEtcdInterval() {
        return config.getLong("vchs.cb.etcd.interval");
    }

    public long getCBEtcdOpenTimeout() {
        return config.getLong("vchs.cb.etcd.openTimeout");
    }

    public long getCBEtcdOpenThreshold() {
        return config.getLong("vchs.cb.etcd.openThreshold");
    }

    public int getCBEtcdErrorPercentage() {
        return config.getInt("vchs.cb.etcd.errorPercentage");
    }

    public boolean getCBEtcdCircuitForceOpen() {
        return config.getBoolean("vchs.cb.etcd.circuitForceOpen");
    }

    public boolean getCBEtcdCircuitForceClose() {
        return config.getBoolean("vchs.cb.etcd.circuitForceClose");
    }

    public String getIdemponentDir() {
        return config.getString("vchs.idempotent.dir");
    }

    public int getIdempontentTtl() {
        return config.getInt("vchs.idempotent.ttl");
    }

    public Config getHouseKeeperConfig() {
        return config.getConfig("vchs.housekeeper");
    }
    public Config getPurgeOrphanConfig() {
        return config.getConfig("vchs.housekeeper.purgeorphan");
    }

    public Config getCleanUpConfig() {
        return config.getConfig("vchs.housekeeper.cleanup");
    }

    public Config getEtcdConfig() {
        return config.getConfig("vchs.etcd");
    }

    public Config getSNSConfig() {
        return config.getConfig("vchs.sns");
    }

    public Config getIAMConfig() {
        return config.getConfig("vchs.iam");
    }
}
