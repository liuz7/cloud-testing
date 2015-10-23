package com.vmware.vchs.testng;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vmware.vchs.test.config.Group;
import org.testng.IMethodInstance;
import org.testng.IMethodInterceptor;
import org.testng.ITestContext;

import java.util.List;
import java.util.Set;

/**
 * Created by georgeliu on 15/5/7.
 */
public class GroupListener extends BaseTestListener implements IMethodInterceptor {
    @Override
    public List intercept(List<IMethodInstance> methods, ITestContext context) {
        List<IMethodInstance> result = Lists.newArrayList();
        List<Group> configuredGroups = this.configuration.getGroups();
        for (IMethodInstance m : methods) {
            Set<String> groups = Sets.newHashSet();
            for (String group : m.getMethod().getGroups()) {
                groups.add(group);
            }
            for (Group group : configuredGroups) {
                if (groups.contains(group.getGroup())) {
                    if (!result.contains(m)) {
                        result.add(m);
                    }
                }
            }
        }
        result.forEach(e -> System.out.println(e.getMethod().getMethodName()));
        return result;
    }
}
