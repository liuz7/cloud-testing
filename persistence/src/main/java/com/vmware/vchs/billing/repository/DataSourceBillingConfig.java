package com.vmware.vchs.billing.repository;


import com.vmware.vchs.condition.DataSourceCondition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.*;

import javax.sql.DataSource;

/**
 * Created by georgeliu on 15/7/16.
 */
@Configuration
public class DataSourceBillingConfig {
    @Bean(name = "billingDS") @Qualifier("billingDS")
    @Primary
    @ConfigurationProperties(prefix = "billing.datasource")
    @Conditional(value=DataSourceCondition.class)
    public DataSource billingDataSource() {
        DataSource dataSource=DataSourceBuilder.create().build();
        if(dataSource instanceof  org.apache.tomcat.jdbc.pool.DataSource){
            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).setMaxAge(1);
        }
        return dataSource;
    }
}
