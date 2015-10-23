package com.vmware.vchs.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Created by sjun on 8/17/15.
 */
public class MDCBasedSkipFilter extends Filter<ILoggingEvent> {
    private String key;
    private String value;

    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }
        if(event.getMDCPropertyMap().get(key).equalsIgnoreCase(value)) {
            return FilterReply.DENY;
        }
        return FilterReply.NEUTRAL;
    }

    public void start() {
        if (this.key != null && this.value != null) {
            super.start();
        }
    }

}
