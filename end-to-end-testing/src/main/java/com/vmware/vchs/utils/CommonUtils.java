package com.vmware.vchs.utils;

import com.vmware.vchs.constant.Constants;

/**
 * Created by liuda on 8/27/15.
 */
public class CommonUtils {
    public static String generateNamePrefix(String className, String methodName) {
        return className + Constants.DELIMITER + methodName;
    }
}
