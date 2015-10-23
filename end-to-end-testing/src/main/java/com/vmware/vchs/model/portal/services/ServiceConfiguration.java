package com.vmware.vchs.model.portal.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by georgeliu on 15/4/23.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceConfiguration {

    private String id;
    private String name;
    private String description;
    private boolean major;
    private boolean isFixed;
    private String value;
    private String valueCanonical;
    private String minValue;
    private String minValueCanonical;
    private String maxValue;
    private String maxValueCanonical;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isMajor() {
        return major;
    }

    public void setMajor(boolean major) {
        this.major = major;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setIsFixed(boolean isFixed) {
        this.isFixed = isFixed;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueCanonical() {
        return valueCanonical;
    }

    public void setValueCanonical(String valueCanonical) {
        this.valueCanonical = valueCanonical;
    }

    public String getMinValue() {
        return minValue;
    }

    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    public String getMinValueCanonical() {
        return minValueCanonical;
    }

    public void setMinValueCanonical(String minValueCanonical) {
        this.minValueCanonical = minValueCanonical;
    }

    public String getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    public String getMaxValueCanonical() {
        return maxValueCanonical;
    }

    public void setMaxValueCanonical(String maxValueCanonical) {
        this.maxValueCanonical = maxValueCanonical;
    }
}
