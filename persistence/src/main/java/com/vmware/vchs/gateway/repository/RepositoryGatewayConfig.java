package com.vmware.vchs.gateway.repository;

import com.vmware.vchs.condition.DataSourceCondition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

/**
 * Created by georgeliu on 15/7/16.
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactoryGateway", transactionManagerRef = "transactionManagerGateway", basePackages = {"com.vmware.vchs.gateway"})
public class RepositoryGatewayConfig {

    @Autowired(required = false)
    private JpaProperties jpaProperties;

    @Autowired(required = false)
    @Qualifier("gatewayDS")
    private DataSource gatewayDS;

    @Bean(name = "entityManagerGateway")
    @Conditional(value = DataSourceCondition.class)
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryGateway(builder).getObject().createEntityManager();
    }

    @Bean(name = "entityManagerFactoryGateway")
    @Primary
    @Conditional(value = DataSourceCondition.class)
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryGateway(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(gatewayDS)
                .properties(getVendorProperties(gatewayDS))
                .packages("com.vmware.vchs.gateway")
                .persistenceUnit("GatewayPersistenceUnit")
                .build();
    }

    private Map<String, String> getVendorProperties(DataSource dataSource) {
        return jpaProperties.getHibernateProperties(dataSource);
    }

    @Bean(name = "transactionManagerGateway")
    @Conditional(value = DataSourceCondition.class)
    PlatformTransactionManager transactionManagerGateway(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryGateway(builder).getObject());
    }

}
