package com.vmware.vchs.launcher;

import com.vmware.vchs.Application;
import com.vmware.vchs.billing.repository.EventRepository;
import com.vmware.vchs.common.utils.Utils;
import com.vmware.vchs.gateway.repository.BackupRepository;
import com.vmware.vchs.gateway.repository.InstanceRepository;
import com.vmware.vchs.gateway.repository.SnapshotRepository;
import com.vmware.vchs.test.config.Configuration;
import com.vmware.vchs.testng.BaseTestListener;
import com.vmware.vchs.testng.NodeListener;
import com.vmware.vchs.testng.TestStatusListener;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.nio.file.Paths;

/**
 * Created by sjun on 7/14/15.
 */
public class TestHelper {
    private static Configuration configuration = null;
    private static SpringApplication application = null;
    private static ConfigurableApplicationContext context = null;
    private static EventRepository eventRepository = null;
    private static BackupRepository backupRepository = null;
    private static SnapshotRepository snapshotRepository = null;
    private static InstanceRepository instanceRepository = null;
    private static boolean initialized = false;

    private static final String[] ymlFiles = {"application.yml", "dataPath.yml", "env.yml", "auth.yml", "test.yml"};

    public static void initApplication(String... args) {
        if (initialized) return;
        initialized = true;

        String location = "";
        for (String ymlFile : ymlFiles) {
            location += "," + TestHelper.class.getClassLoader().getResource(Paths.get("config", ymlFile).toString());
        }
        for (String ymlFile : ymlFiles) {
            location += "," + Paths.get(System.getenv("HOME"), ".spring", ymlFile).toString();
        }
        for (String ymlFile : ymlFiles) {
            location += "," + ymlFile;
        }
        location = location.substring(1);
        System.setProperty("spring.config.location", location);

        application = new SpringApplication(Application.class);
        application.setWebEnvironment(false);
        context = application.run(args);
        configuration = context.getBean(Configuration.class);

        String host = TestHelper.getContext().getEnvironment().getProperty("mysql.host");
        String port = TestHelper.getContext().getEnvironment().getProperty("mysql.port");

        if (Utils.checkHostAndPort(host, port)) {
            eventRepository = context.getBean(EventRepository.class);
            backupRepository = context.getBean(BackupRepository.class);
            snapshotRepository = context.getBean(SnapshotRepository.class);
            instanceRepository = context.getBean(InstanceRepository.class);
        }

        TestStatusListener.setConfiguration(configuration);
        BaseTestListener.setConfiguration(configuration);
    }

    public static Configuration getConfiguration() {
        initApplication();
        return configuration;
    }

    public static EventRepository getEventRepository() {
        initApplication();
        return eventRepository;
    }

    public static BackupRepository getBackupRepository() {
        initApplication();
        return backupRepository;
    }

    public static SnapshotRepository getSnapshotRepository() {
        initApplication();
        return snapshotRepository;
    }

    public static InstanceRepository getInstanceRepository() {
        initApplication();
        return instanceRepository;
    }

    public static SpringApplication getApplication(){
        initApplication();
        return application;
    }

    public static ConfigurableApplicationContext getContext(){
        initApplication();
        return context;
    }

}
