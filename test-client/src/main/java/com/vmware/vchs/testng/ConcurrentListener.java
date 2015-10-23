package com.vmware.vchs.testng;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;
import org.testng.annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by georgeliu on 14/11/14.
 */
public class ConcurrentListener extends BaseTestListener implements IAnnotationTransformer {
    @Override
    public void transform(ITestAnnotation annotation, Class arg1, Constructor arg2, Method testMethod) {
        if (testMethod != null) {
            Annotation[] methodAnnotations = testMethod.getAnnotations();
            for (Annotation methodAnnotation : methodAnnotations) {
                if (methodAnnotation instanceof Test) {
                    annotation.setInvocationCount(this.configuration.getConcurrent().getInvocationCount());
                    annotation.setThreadPoolSize(this.configuration.getConcurrent().getThreadPoolSize());
                }
            }
        }
    }
}
