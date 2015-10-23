package com.vmware.vchs.model.portal.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by georgeliu on 15/4/23.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicePlan {

    private String id;
    private String name;
    private String description;
    private List<ServiceConfiguration> configurations;

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

    public List<ServiceConfiguration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(List<ServiceConfiguration> configurations) {
        this.configurations = configurations;
    }
}
