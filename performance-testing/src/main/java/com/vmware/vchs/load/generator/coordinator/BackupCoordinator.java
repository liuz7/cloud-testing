//package com.vmware.vchs.load.generator.coordinator;
//
//import com.vmware.vchs.load.generator.result.collector.Collectable;
//import com.vmware.vchs.load.generator.result.collector.ResultCollector;
//import com.vmware.vchs.load.generator.service.LoadService;
//import com.vmware.vchs.load.generator.service.TestResult;
//import com.vmware.vchs.performance.model.TestCase;
//import com.vmware.vchs.performance.repository.TestCaseRepository;
//import org.apache.thrift.TException;
//import org.apache.thrift.protocol.TBinaryProtocol;
//import org.apache.thrift.transport.TFramedTransport;
//import org.apache.thrift.transport.TSocket;
//import org.apache.thrift.transport.TTransport;
//import org.apache.thrift.transport.TTransportException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.orm.jpa.EntityScan;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@SpringBootApplication
//@EntityScan({"com.vmware.vchs.performance.repository","com.vmware.vchs.performance.model"})
//@EnableJpaRepositories(basePackages = {"com.vmware.vchs.performance.repository"})
//public class BackupCoordinator extends OptionHandler  {
//
//    @Autowired
//    TestCaseRepository testCaseRepository;
//    public static void main(String[] args) throws Exception {
//        SpringApplication app = new SpringApplication(BackupCoordinator.class);
//        app.setWebEnvironment(false);
//        BackupCoordinator coordinator=app.run().getBean(BackupCoordinator.class);
//        coordinator.process(args);
//        coordinator.run();
//
//    }
//    public void run(String... strings) throws Exception {
//        TestCase testCase = new TestCase("new", new Date());
//        testCaseRepository.save(testCase);
//        List<String> requestIds = new ArrayList<>();
//        Map<String,List<Double>> responseTimes=new HashMap<>();
//        double totalSuccess=0, totalFailure=0;
//        for (String ip : this.ips) {
//            TTransport transport = new TFramedTransport(new TSocket(ip, 29999));
//            TBinaryProtocol protocol = new TBinaryProtocol(transport);
//            LoadService.Client client = new LoadService.Client(protocol);
//            TestResult res = new TestResult();
//            try {
//                transport.open();
////                int index = client.createLoadServer(10, MyTimeUnit.SECOND);
////                client.addVUsers(index, this.user, 10000);
//                client.setupTest(this.tests, this.user, this.interval, this.duration);
//                client.startTest();
//                res = client.getResult();
//                totalSuccess+=res.getSuccess();
//                totalFailure+=res.getFailure();
//                requestIds.addAll(res.getRequests());
//
//                res.getResults().forEach((k, v) -> responseTimes.merge(k, v, (v1, v2) -> Stream.concat(v1.stream(), v2.stream()).collect(Collectors.toList())));
//                transport.close();
//            } catch (TTransportException e) {
//                e.printStackTrace();
//            } catch (TException e) {
//                e.printStackTrace();
//            }
//            System.out.println("Number of success is " + res.getSuccess());
//            System.out.println("Number of failure is " + res.getFailure());
//            System.out.println("Result time are " + res.getResults());
//            System.out.println("RequestIds are " + res.getRequests());
//        }
//        System.out.println("Total success is " + totalSuccess);
//        System.out.println("Total failure is " + totalFailure);
//        System.out.println("Total Result time are " + responseTimes);
//        System.out.println("Total RequestIds are " + requestIds);
//        Collectable collector = new ResultCollector(null);
//        collector.collect(null);
//
//    }
//}
