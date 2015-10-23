package com.vmware.vchs.testng;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.testng.*;
import org.testng.internal.ConstructorOrMethod;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

/**
 * The Ignore listener which skipped the test method if it has ignore annotations.
 */
public class IgnoreListener extends BaseTestListener implements IInvokedMethodListener {

    @Override
    public void beforeInvocation(IInvokedMethod invokedMethod, ITestResult result) {
        ITestNGMethod testNgMethod = result.getMethod();
        ConstructorOrMethod constructorOrMethod = testNgMethod.getConstructorOrMethod();
        Method method = constructorOrMethod.getMethod();
        List<String> reasons = checkReasons(method);
        List<String> excludeMethods = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(configuration.getTestExcludeMethod());
        if (excludeMethods.contains(method.getName())) {
            reasons.add("Test Excluded in Configuration");
        }
        if (!reasons.isEmpty()) {
            throw new SkipException(format("Skipped [%s] because of the %s reason(s).", constructorOrMethod.getName(), reasons));
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {

    }

    private List<String> checkReasons(Method method) {
        if (method != null && method.isAnnotationPresent(Ignore.class)) {
            Ignore annotation = method.getAnnotation(Ignore.class);
            String[] reasons = annotation.reasons();
            return Arrays.asList(reasons);
        } else {
            return Lists.newArrayList();
        }
    }
}
