package com.vmware.vchs;

//import com.vmware.vchs.performance.repository.TestCaseRepository;
//import com.vmware.vchs.TestCaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@SpringBootApplication
//@ActiveProfiles("mysql")
//@Configuration
//@EnableAutoConfiguration
//@ComponentScan
public class Application  {

//public class Application implements CommandLineRunner {
//    @Autowired
//    TestCaseRepository testCaseRepository;


    public static void main(String[] args){

        SpringApplication.run(Application.class, args);
////        Application a =new Application();
////        a.run();
//        Application a = SpringApplication.run(Application.class, args).getBean(Application.class);
//        a.run();
//
////        SpringApplication app = new SpringApplication(Application.class);
////        app.setWebEnvironment(false);
////        ConfigurableApplicationContext ctx = app.run(args);
//
//
    }
}
