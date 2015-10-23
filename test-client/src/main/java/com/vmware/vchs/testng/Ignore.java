package com.vmware.vchs.testng;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The Ignore annotation class.
 */
@Retention(RUNTIME)
public @interface Ignore {
    String[] reasons();

    Class<?> methodClass() default Ignore.class;
}