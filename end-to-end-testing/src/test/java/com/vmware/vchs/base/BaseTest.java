package com.vmware.vchs.base;

import com.vmware.vchs.common.utils.DaemonThreadFactory;
import com.vmware.vchs.common.utils.RetryTask;
import com.vmware.vchs.common.utils.ThreadTask;
import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.launcher.TestHelper;
import com.vmware.vchs.test.client.rest.IdleConnectionMonitorThread;
import com.vmware.vchs.test.client.rest.metrics.HttpRequestMetrics;
import com.vmware.vchs.test.config.Configuration;
import com.vmware.vchs.testng.IgnoreListener;
import com.vmware.vchs.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by georgeliu on 14/11/3.
 */

@Listeners(IgnoreListener.class)
public class BaseTest implements E2ETest {

    protected static int numberOfRetries;
    protected static List<Integer> timeToWait;
    protected static HttpRequestMetrics httpRequestMetrics = new HttpRequestMetrics();
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected static IdleConnectionMonitorThread connMonitorThread;
    protected Instant start;
    public int diskSize;
    protected static Configuration configuration = TestHelper.getConfiguration();
    protected DbaasApi dbaasApi;
    protected static RetryTask retryTask;

    protected static ThreadTask threadTask;
    protected static ExecutorService executor;
    protected static CompletionService pool;
    protected String methodName;
    protected String getNamePrefix(){
        return CommonUtils.generateNamePrefix(this.getClass().getName(), methodName);
    }

    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() throws Exception {
        logger.info("Setup for base test suite...");
        connMonitorThread = IdleConnectionMonitorThread.getIdleConnectionMonitorThread();
        logger.info("Setup success for base test suite...");
        this.executor = Executors.newCachedThreadPool(new DaemonThreadFactory("BaseTest"));
        this.pool = new ExecutorCompletionService(this.executor);
        this.threadTask = new ThreadTask(this.executor);
//        this.numberOfRetries = configuration.getRetryTimes();
//        this.timeToWait = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(configuration.getRetryTimeToWait()).stream().mapToInt(Integer::parseInt).boxed().collect(Collectors.toList());
//        this.retryTask = new RetryTask(this.numberOfRetries, this.timeToWait);
    }

    @BeforeClass(alwaysRun = true)
    public void setUpClass() throws Exception {
        start = Instant.now();
        logger.info("Setup for base test class...");
        logger.info("Praxis Url:" + this.configuration.getPraxisServerConnection().getPraxisConnectUrl());
        logger.info("Praxis Username:" + this.configuration.getPraxisServerConnection().getDbadminUsername());
        logger.info("Praxis Password:" + this.configuration.getPraxisServerConnection().getDbadminPassword());
    }

    @BeforeMethod(alwaysRun = true)
    public void setUpMethod(Method method) throws Exception {
        try {
            logger.info("Setup for base test method...");
            logger.info("iam configuration is " + this.configuration.isAuthentication());
            logger.info("class name is " + this.getClass());
            logger.info("method name is " + method.getName());
            methodName=method.getName();
            dbaasApi = new DbaasApi(TestHelper.getConfiguration());
        } catch (Exception e) {
            logger.info(Utils.getStackTrace(e));
        }
    }

    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() throws Exception {
        logger.info("Tear down for base test suite...");
        connMonitorThread.shutdown();
        logger.info("connMonitorThread shutdown success");
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        for (Thread thread : threadSet) {
            if (!thread.isDaemon()) {
                logger.info(thread.toString());
                logger.info(Arrays.toString(thread.getStackTrace()));
            }
        }
        shutdownExecutorService(this.executor);
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() throws Exception {
        logger.info("Tear down for base test class...");
        Instant end = Instant.now();
        Duration between = Duration.between(start, end);
        logger.info("Test class duration: " + between.toMinutes() + " minutes.");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownMethod(Method method) throws Exception {
        logger.info("Tear down for base test method...");
    }

    protected void sleepBySeconds(long timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
            logger.info(Utils.getStackTrace(e));
        }
    }

    protected void shutdownExecutorService(ExecutorService executorService) throws InterruptedException {
        logger.info("start shutdown executorservice" + executorService.toString());
        if (executorService != null) {
            executorService.shutdownNow();
            logger.info("executorservice is isTerminated: " + executorService.isTerminated());
            logger.info("executorservice is shutdown: " + executorService.isShutdown());
            logger.info("shutdown executorservice" + executorService.toString() + " success");
        }
    }

    protected int getThreadSizeForInstance() {
        int threadSizeForInstance = Integer.parseInt(configuration.getThreadSizeForInstance());
        logger.info("Thread size for instance:" + threadSizeForInstance);
        return threadSizeForInstance;
    }

}

