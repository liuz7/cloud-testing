//package com.vmware.vchs.performance.repository;
//
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.sql.DataSource;
//
///**
// * Created by georgeliu on 15/7/16.
// */
//@Configuration
//public class DataSourcePerformanceConfig {
//    @Bean(name = "performanceDS")
//    @Qualifier("performanceDS")
//    @ConfigurationProperties(prefix = "performance.datasource")
//    public DataSource performanceDataSource() {
//        return DataSourceBuilder.create().build();
//    }
//}
