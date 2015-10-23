package com.vmware.vchs.model.constant;

import com.google.common.base.MoreObjects;

/**
 * Created by liuda on 6/25/15.
 */
public class Meter {
    private String name;
    private String value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString(){
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("value", value)
                .toString();
    }

}
