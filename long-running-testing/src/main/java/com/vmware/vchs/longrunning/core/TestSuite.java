package com.vmware.vchs.longrunning.core;

import com.vmware.vchs.longrunning.base.TestCase;
import org.reflections.Reflections;

import java.util.Set;

/**
 * Created by sjun on 8/25/15.
 */
public class TestSuite {
    public Set<Class<?>> getTestClasses() {
        Reflections reflections = new Reflections();
        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(TestCase.class);
        return classes;
    }
}
