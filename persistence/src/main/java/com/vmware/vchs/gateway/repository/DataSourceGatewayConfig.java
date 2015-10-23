package com.vmware.vchs.gateway.repository;

import com.vmware.vchs.condition.DataSourceCondition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Created by georgeliu on 15/7/16.
 */
@Configuration
public class DataSourceGatewayConfig {
    @Bean(name = "gatewayDS")
    @Qualifier("gatewayDS")
    @ConfigurationProperties(prefix = "gateway.datasource")
    @Conditional(value=DataSourceCondition.class)
    public DataSource gatewayDataSource() {
        DataSource dataSource=DataSourceBuilder.create().build();
        if(dataSource instanceof  org.apache.tomcat.jdbc.pool.DataSource){
            ((org.apache.tomcat.jdbc.pool.DataSource) dataSource).setMaxAge(1);
        }
        return dataSource;
    }
}
