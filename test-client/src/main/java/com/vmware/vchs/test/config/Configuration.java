package com.vmware.vchs.test.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by georgeliu on 15/6/18.
 */
//@Profile({"non-mysql","full"})
@Component
@ConfigurationProperties
public class Configuration {

    private Rest rest;
    private Nats nats;
    private Etcd etcd;
    private Jira jira;
    private Concurrent concurrent;
    private Mysql mysql;
    private CdsServer cdsServer;
    private List<Group> groups;
    private ElasticSearch elasticSearch;
    private InfluxDb influxDb;
    private boolean authentication;
    private PraxisServerConnection praxisServerConnection;
    private String planName;
    private String dbEngineVersion;
    private String allowedIP;
    private String customMssqlPort;
    private String diskSize;
    private String threadSizeForInstance;
    private int retryTimes;
    private String retryTimeToWait;
    private String testGroup;
    private String testExcludeGroup;
    private String testExcludeMethod;
    private String testClass;
    private String testMethod;
    private String edition;
    private String licenseType;
    private boolean exitOnFail;
    private String vdcNumber;
    private boolean sns;
    private String testFile;
    private DataPath dataPath;
    private int retryTimeout;
    private boolean parallel;
    private int testRounds;
    private boolean gateway_debug;
    private String deployEnv;
    private Vdc vdc;

    public String getDeployEnv() {
        return deployEnv;
    }

    public void setDeployEnv(String deployEnv) {
        this.deployEnv = deployEnv;
    }

    public boolean isGateway_debug() {
        return gateway_debug;
    }

    public void setGateway_debug(boolean gateway_debug) {
        this.gateway_debug = gateway_debug;
    }

    public int getTestRounds() {
        return testRounds;
    }

    public void setTestRounds(int testRounds) {
        this.testRounds = testRounds;
    }

    public boolean isParallel() {
        return parallel;
    }

    public void setParallel(boolean parallel) {
        this.parallel = parallel;
    }

    public boolean isSns() {
        return sns;
    }

    public void setSns(boolean sns) {
        this.sns = sns;
    }

    public String getVdcNumber() {
        return vdcNumber;
    }

    public void setVdcNumber(String vdcNumber) {
        this.vdcNumber = vdcNumber;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public Rest getRest() {
        return rest;
    }

    public void setRest(Rest rest) {
        this.rest = rest;
    }

    public Nats getNats() {
        return nats;
    }

    public void setNats(Nats nats) {
        this.nats = nats;
    }

    public Etcd getEtcd() {
        return etcd;
    }

    public void setEtcd(Etcd etcd) {
        this.etcd = etcd;
    }

    public Jira getJira() {
        return jira;
    }

    public void setJira(Jira jira) {
        this.jira = jira;
    }

    public Concurrent getConcurrent() {
        return concurrent;
    }

    public void setConcurrent(Concurrent concurrent) {
        this.concurrent = concurrent;
    }

    public Mysql getMysql() {
        return mysql;
    }

    public void setMysql(Mysql mysql) {
        this.mysql = mysql;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public ElasticSearch getElasticSearch() {
        return elasticSearch;
    }

    public void setElasticSearch(ElasticSearch elasticSearch) {
        this.elasticSearch = elasticSearch;
    }

    public InfluxDb getInfluxDb() {
        return influxDb;
    }

    public void setInfluxDb(InfluxDb influxDb) {
        this.influxDb = influxDb;
    }

    public String getEdition() {
        return edition;
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }

    public boolean isAuthentication() {
        return authentication;
    }

    public void setAuthentication(boolean authentication) {
        this.authentication = authentication;
    }

    public PraxisServerConnection getPraxisServerConnection() {
        return praxisServerConnection;
    }

    public void setPraxisServerConnection(PraxisServerConnection praxisServerConnection) {
        this.praxisServerConnection = praxisServerConnection;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getDbEngineVersion() {
        return dbEngineVersion;
    }

    public void setDbEngineVersion(String dbEngineVersion) {
        this.dbEngineVersion = dbEngineVersion;
    }

    public String getAllowedIP() {
        return allowedIP;
    }

    public void setAllowedIP(String allowedIP) {
        this.allowedIP = allowedIP;
    }

    public String getCustomMssqlPort() {
        return customMssqlPort;
    }

    public void setCustomMssqlPort(String customMssqlPort) {
        this.customMssqlPort = customMssqlPort;
    }

    public String getDiskSize() {
        return diskSize;
    }

    public void setDiskSize(String diskSize) {
        this.diskSize = diskSize;
    }

    public String getThreadSizeForInstance() {
        return threadSizeForInstance;
    }

    public void setThreadSizeForInstance(String threadSizeForInstance) {
        this.threadSizeForInstance = threadSizeForInstance;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public String getTestGroup() {
        return testGroup;
    }

    public void setTestGroup(String testGroup) {
        this.testGroup = testGroup;
    }

    public String getTestExcludeGroup() {
        return testExcludeGroup;
    }

    public void setTestExcludeGroup(String testExcludeGroup) {
        this.testExcludeGroup = testExcludeGroup;
    }

    public String getTestClass() {
        return testClass;
    }

    public void setTestClass(String testClass) {
        this.testClass = testClass;
    }

    public String getTestMethod() {
        return testMethod;
    }

    public void setTestMethod(String testMethod) {
        this.testMethod = testMethod;
    }

    public CdsServer getCdsServer() {
        return cdsServer;
    }

    public void setCdsServer(CdsServer cdsServer) {
        this.cdsServer = cdsServer;
    }

    public boolean isExitOnFail() {
        return exitOnFail;
    }

    public void setExitOnFail(boolean exitOnFail) {
        this.exitOnFail = exitOnFail;
    }

    public String getRetryTimeToWait() {
        return retryTimeToWait;
    }

    public void setRetryTimeToWait(String retryTimeToWait) {
        this.retryTimeToWait = retryTimeToWait;
    }

    public String getTestFile() {
        return testFile;
    }

    public void setTestFile(String testFile) {
        this.testFile = testFile;
    }

    public DataPath getDataPath() {
        return dataPath;
    }

    public void setDataPath(DataPath dataPath) {
        this.dataPath = dataPath;
    }

    public int getRetryTimeout() {
        return retryTimeout;
    }

    public void setRetryTimeout(int retryTimeout) {
        this.retryTimeout = retryTimeout;
    }

    public String getTestExcludeMethod() {
        return testExcludeMethod;
    }

    public void setTestExcludeMethod(String testExcludeMethod) {
        this.testExcludeMethod = testExcludeMethod;
    }

    public Vdc getVdc() {
        return vdc;
    }

    public void setVdc(Vdc vdc) {
        this.vdc = vdc;
    }
}
