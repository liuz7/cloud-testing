package com.vmware.vchs.launcher;

import com.vmware.vchs.base.E2ETest;
import org.reflections.Reflections;

import java.util.Set;

/**
 * Created by georgeliu on 15/3/4.
 */
public class TestFilter {

    public static Set<Class<? extends E2ETest>> getAllTestClass(String packageName) {
        Reflections reflections = new Reflections(packageName);
        Set<Class<? extends E2ETest>> subTypes = reflections.getSubTypesOf(E2ETest.class);
        return subTypes;
    }

    public static Class<? extends E2ETest> getTestClass(String packageName, String className) {
        Set<Class<? extends E2ETest>> classes = getAllTestClass(packageName);
        for (Class<? extends E2ETest> testClass : classes) {
            if (testClass.getSimpleName().equalsIgnoreCase(className) || testClass.getCanonicalName().equalsIgnoreCase(className)) {
                return testClass;
            }
        }
        return null;
    }

}
