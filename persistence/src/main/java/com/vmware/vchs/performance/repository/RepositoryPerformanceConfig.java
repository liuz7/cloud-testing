//package com.vmware.vchs.performance.repository;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilder;
//import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//
//import javax.persistence.EntityManager;
//import javax.sql.DataSource;
//import java.util.Map;
//
///**
// * Created by georgeliu on 15/7/16.
// */
//@Configuration
//@EnableTransactionManagement
//@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactoryPerformance", transactionManagerRef = "transactionManagerPerformance", basePackages = {"com.vmware.vchs.performance"})
//public class RepositoryPerformanceConfig {
//    @Autowired
//    private JpaProperties jpaProperties;
//
//    @Autowired
//    @Qualifier("performanceDS")
//    private DataSource gatewayDS;
//
//    @Bean(name = "entityManagerPerformance")
//    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
//        return entityManagerFactoryGateway(builder).getObject().createEntityManager();
//    }
//
//    @Bean(name = "entityManagerFactoryPerformance")
//    @Primary
//    public LocalContainerEntityManagerFactoryBean entityManagerFactoryGateway(EntityManagerFactoryBuilder builder) {
//        return builder
//                .dataSource(gatewayDS)
//                .properties(getVendorProperties(gatewayDS))
//                .packages("com.vmware.vchs.performance")
//                .persistenceUnit("PerformancePersistenceUnit")
//                .build();
//    }
//
//    private Map<String, String> getVendorProperties(DataSource dataSource) {
//        return jpaProperties.getHibernateProperties(dataSource);
//    }
//
//    @Bean(name = "transactionManagerPerformance")
//    PlatformTransactionManager transactionManagerGateway(EntityManagerFactoryBuilder builder) {
//        return new JpaTransactionManager(entityManagerFactoryGateway(builder).getObject());
//    }
//
//}
