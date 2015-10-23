package com.vmware.vchs.billing.repository;

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
@EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactoryBilling", transactionManagerRef = "transactionManagerBilling", basePackages = {"com.vmware.vchs.billing"})
public class RepositoryBillingConfig {

    @Autowired(required = false)
    private JpaProperties jpaProperties;

    @Autowired(required = false)
    @Qualifier("billingDS")
    private DataSource billingDS;

    @Bean(name = "entityManagerBilling")
    @Conditional(value = DataSourceCondition.class)
    @Primary
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryBilling(builder).getObject().createEntityManager();
    }

    @Bean(name = "entityManagerFactoryBilling")
    @Primary
    @Conditional(value = DataSourceCondition.class)
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryBilling(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(billingDS)
                .properties(getVendorProperties(billingDS))
                .packages("com.vmware.vchs.billing")
                .persistenceUnit("BillingPersistenceUnit")
                .build();
    }

    private Map<String, String> getVendorProperties(DataSource dataSource) {
        return jpaProperties.getHibernateProperties(dataSource);
    }

    @Bean(name = "transactionManagerBilling")
    @Conditional(value = DataSourceCondition.class)
    @Primary
    PlatformTransactionManager transactionManagerBilling(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryBilling(builder).getObject());
    }

}
