package com.vmware.vchs.datapath;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by sjun on 8/12/15.
 */
public class DataGenerator {
    private static AtomicInteger index = new AtomicInteger(0);

    public static String getData(int size) {
        String prefix =  String.format("%015d-%015d-", index.incrementAndGet(), System.currentTimeMillis());
        if (prefix.length() > size) {
            return prefix;
        } else {
            return prefix + RandomStringUtils.random(size - prefix.length(), true, true);
        }
    }
}
