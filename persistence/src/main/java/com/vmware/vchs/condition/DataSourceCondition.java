package com.vmware.vchs.condition;

import com.vmware.vchs.common.utils.Utils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by georgeliu on 15/8/24.
 */
public class DataSourceCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String host = context.getEnvironment().getProperty("mysql.host");
        String port = context.getEnvironment().getProperty("mysql.port");
        return Utils.checkHostAndPort(host, port);
    }
}
