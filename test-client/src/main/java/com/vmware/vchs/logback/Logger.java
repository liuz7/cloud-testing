package com.vmware.vchs.logback;

import com.google.common.base.MoreObjects;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by liuda on 8/20/15.
 */
public class Logger {
    protected static final org.slf4j.Logger logger = LoggerFactory.getLogger(Logger.class);
    public static void logList(List<String> list){
        MoreObjects.ToStringHelper helper=MoreObjects.toStringHelper(Logger.class);
        for(String item:list){
            helper.addValue(item);
        }
        logger.info(helper.toString());
    }
}
