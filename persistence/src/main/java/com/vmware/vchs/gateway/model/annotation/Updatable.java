package com.vmware.vchs.gateway.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface Updatable {
    UpdateType value() default UpdateType.NOT_IMPLEMENTED; // alias to type.

    UpdateType type() default UpdateType.NOT_IMPLEMENTED;

    /**
     * system won't check its value against exist one if true.
     * update is forced to execute.
     */
    boolean force() default false;
}