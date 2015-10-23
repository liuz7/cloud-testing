package com.vmware.vchs.load;

import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.launcher.TestHelper;
import com.vmware.vchs.load.generator.coordinator.OptionHandler;
import com.vmware.vchs.load.generator.result.collector.ResultCollector;
import com.vmware.vchs.load.generator.service.LoadService;
import com.vmware.vchs.load.generator.service.TestResult;
import com.vmware.vchs.load.generator.util.TimeUtil;
import com.vmware.vchs.performance.model.TestRun;
import com.vmware.vchs.performance.repository.TestRunRepository;
import com.vmware.vchs.test.client.remote.SshUtil;
import com.vmware.vchs.test.config.CdsServer;
import com.vmware.vchs.test.config.Configuration;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@EnableConfigurationProperties
@org.springframework.context.annotation.Configuration
@EnableAutoConfiguration
//@SpringBootApplication
@EntityScan({"com.vmware.vchs.performance.repository", "com.vmware.vchs.performance.model"})
@ComponentScan({"com.vmware.vchs.test.config","com.vmware.vchs.load"})
@EnableJpaRepositories(basePackages = {"com.vmware.vchs.performance.repository"})
public class Coordinator extends OptionHandler {


    @Autowired
    TestRunRepository testCaseRepository;

    private static ApplicationContext context;

    private static Configuration configuration;

    public static void main(String[] args) throws Exception {
//        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
//        for(Thread thread:threadSet){
//            if(thread.isDaemon()){
////                logger.info(thread.toString());
//                System.out.println(thread.toString());
//                System.out.println(Arrays.toString(thread.getStackTrace()));
//            }
//        }
//        SpringApplication app = new SpringApplication(Coordinator.class);
//            app.setWebEnvironment(false);
//            context = app.run(args);

//        try {


//        SpringApplication app = new SpringApplication(Coordinator.class);
//        app.setWebEnvironment(false);
//        context = app.run(args);
//        configuration = app.run(args).getBean(Configuration.class);
        context=TestHelper.getContext();
        Coordinator coordinator = context.getBean(Coordinator.class);
        coordinator.process(args);
        coordinator.run(args);
        ((ConfigurableApplicationContext) context).close();
        ((ConfigurableApplicationContext) context).registerShutdownHook();

//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }
    }

    public void run(String... strings) throws Exception {
//        SpringApplication app = new SpringApplication(Coordinator.class);
//        app.setWebEnvironment(false);
//        context = app.run(strings);
//        this.configuration =  app.run(strings).getBean(Configuration.class);


        CdsServer cdsServer = this.configuration.getCdsServer();
        SshUtil ssh = new SshUtil(cdsServer.getUser(), cdsServer.getPassword(), cdsServer.getBaseUrl());
        ssh.connect();
        List<String> requestIds = new ArrayList<>();
        Map<String, List<Double>> responseTimes = new HashMap<>();
        double totalSuccess = 0, totalFailure = 0;

        String timeStamp = ssh.execCmd("date +%s", false);
        LocalDateTime start = TimeUtil.fromTimeStamp(timeStamp);
        for (String ip : this.ips) {
            TTransport transport = new TFramedTransport(new TSocket(ip, 29999));
            TBinaryProtocol protocol = new TBinaryProtocol(transport);
            LoadService.Client client = new LoadService.Client(protocol);
            TestResult res = new TestResult();
            try {
                transport.open();
//                int index = client.createLoadServer(10, MyTimeUnit.SECOND);
//                client.addVUsers(index, this.user, 10000);
                client.setupTest(this.tests, this.user, this.interval, this.duration);
                client.startTest();
                res = client.getResult();
                totalSuccess += res.getSuccess();
                totalFailure += res.getFailure();
                requestIds.addAll(res.getRequests());

                res.getResults().forEach((k, v) -> responseTimes.merge(k, v, (v1, v2) -> Stream.concat(v1.stream(), v2.stream()).collect(Collectors.toList())));
                transport.close();
            } catch (TTransportException e) {
                e.printStackTrace();
            } catch (TException e) {
                e.printStackTrace();
            }
            System.out.println("Number of success is " + res.getSuccess());
            System.out.println("Number of failure is " + res.getFailure());
            System.out.println("Result time are " + res.getResults());
            System.out.println("RequestIds are " + res.getRequests());
        }
        System.out.println("Total success is " + totalSuccess);
        System.out.println("Total failure is " + totalFailure);
        System.out.println("Total Result time are " + responseTimes);
        System.out.println("Total RequestIds are " + requestIds);
        TestRun testRun = new TestRun("performance test", start, TimeUtil.fromTimeStamp(ssh.execCmd("date +%s")));
        ResultCollector collector = context.getBean(ResultCollector.class);
        collector.setConfiguration(this.configuration);
        collector.setTestRun(testRun);
        collector.setRequestIds(new ArrayList<>());
        collector.collect();
        System.out.println("performance end success");
    }
}
