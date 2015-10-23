package com.vmware.vchs.load.generator.service;

import com.vmware.vchs.common.bucket.Bucket;
import com.vmware.vchs.common.bucket.LeakyBucketBuilder;
import com.vmware.vchs.common.utils.DaemonThreadFactory;
import com.vmware.vchs.load.generator.common.Constants;
import com.vmware.vchs.load.testcase.TestCase;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ClassUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class LoadServiceImpl implements LoadService.Iface {

    protected static final Logger logger = LoggerFactory.getLogger(LoadServiceImpl.class);
    private int numUsers = 0;
    private ExecutorService es = Executors.newCachedThreadPool(new DaemonThreadFactory());
    private CompletionService<Result> cs = new ExecutorCompletionService<Result>(es);
    private ArrayList<Task> tasks = new ArrayList<>();
    private Bucket bucket;
    private CountDownLatch trigger = new CountDownLatch(1);
    private List<TestCase> tests = new ArrayList<>();
    public ClassLoader classLoader = this.getClass().getClassLoader();

    public void setTests(String originalTest) throws TException {
        try {
            for (String test : originalTest.split(Constants.TEST_DELIMITER)) {
                Class<?> className= Class.forName(test);
                TestCase cases=(TestCase)className.newInstance();
                tests.add(((TestCase) ClassUtils.forName(test, this.classLoader).newInstance()));
            }
        } catch (Exception e) {
            throw new TException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void setupTest(String originalTest, int user, int interval, int duration) throws TException {
        logger.info("get request for test: " + originalTest + ", user number is: " + user + ", interval is: " + interval + ", duration is :" + duration + "minutes");
        bucket = new LeakyBucketBuilder().withCapacity(user).withRefillStrategy(user, 1, TimeUnit.MILLISECONDS).build();
//                .withNumRequests(1000).withTimeUnit(TimeUnit.SECONDS).build();
        setTests(originalTest);
        this.numUsers = user;
        for (int i = 0; i < user; ++i) {
            tasks.add(new Task(duration, interval));
        }

        for (int i = 0; i < user; ++i) {
            cs.submit(tasks.get(i));
        }
    }


    @Override
    public void startTest() throws TException {
        logger.info("Start test");
        trigger.countDown();
        logger.info("Start success");
    }

    @Override
    public void stopTest() throws TException {
        logger.info("Stop test");
        for (int i = 0; i < tasks.size(); ++i) {
            tasks.get(i).stop();
        }
        logger.info("Stop success");

    }


    @Override
    public TestResult getResult() throws TException {
        logger.info("getResult");
        int success = 0;
        int failure = 0;
        Map<String, List<Double>> responseTimes = new HashMap<>();
        List<String> requestIds = new ArrayList<>();
        for (TestCase test : LoadServiceImpl.this.tests) {
            responseTimes.put(test.getClass().getSimpleName(), new ArrayList<Double>());
        }
        TestResult res = new TestResult();
        for (int i = 0; i < numUsers; ++i) {
            Result r = null;
            try {
                r = cs.take().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (r != null) {
                success += r.getSuccess();
                failure += r.getFailure();
                for(Map.Entry<String, List<Double>> entry:r.getResponseTimes().entrySet()){
                    responseTimes.get(entry.getKey()).addAll(entry.getValue());
                }
                requestIds.addAll(r.getRequestIds());
            } else {
                failure++;
            }
        }
        res.setSuccess(success);
        res.setFailure(failure);
        res.setResults(responseTimes);
        res.setRequests(requestIds);
        logger.info("result is success:" + res.getSuccess() + ", failure:" + res.getFailure());
        return res;
    }

    class Task implements Callable<Result> {

        private int duration;
        private int interval;
        private volatile boolean flagStop = false;

        public Task(int duration, int interval) {
            this.duration = 1 * 1000;
//            this.duration = duration * 60*1000;
            this.interval = interval * 1000;
        }

        public void stop() {
            flagStop = true;
        }

        @Override
        public Result call() {
            try {
                trigger.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }

            Result res = new Result();
            for (TestCase test : LoadServiceImpl.this.tests) {
                res.responseTimes.put(test.getClass().getSimpleName(), new ArrayList<Double>());
            }
            long endTime = System.currentTimeMillis() + this.duration;
            Duration duration;
            while (!flagStop && System.currentTimeMillis() < endTime) {
                bucket.consume();
                for (TestCase test : LoadServiceImpl.this.tests) {
                    try {
                        Instant begin = Instant.now();
                        com.vmware.vchs.load.testcase.TestResult r = test.runTest();
//                        Socket socket = new Socket("127.0.0.1", 19999);
                        if (r.getSuccess()) {
                            duration = Duration.between(begin, Instant.now());
                            res.responseTimes.get(test.getClass().getSimpleName()).add((double)duration.getSeconds());
                            res.getRequestIds().add(r.getRequestId());
                            res.incrementSuccess();
                        } else {
                            res.incrementFailure();
                        }
                        Thread.sleep(10);
//                        Thread.sleep(this.interval);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
            return res;
        }
    }

    class Result {

        private double numSuccess;
        private double numFailure;
        private Map<String, List<Double>> responseTimes = new HashMap<>();
        private List<String> requestIds = new ArrayList<>();

        Result() {
            numSuccess = 0;
            numFailure = 0;
        }

        public List<String> getRequestIds() {
            return requestIds;
        }

        public void setRequestIds(List<String> requestIds) {
            this.requestIds = requestIds;
        }

        void incrementSuccess() {
            addSuccess(1);
        }

        public Map<String, List<Double>> getResponseTimes() {
            return responseTimes;
        }

        public void setResponseTimes(Map<String, List<Double>> responseTimes) {
            this.responseTimes = responseTimes;
        }

        void addSuccess(int num) {
            numSuccess += num;
        }

        void incrementFailure() {
            addFailure(1);
        }

        void addFailure(int num) {
            numFailure += num;
        }

        double getSuccess() {
            return numSuccess;
        }

        double getFailure() {
            return numFailure;
        }

    }
}