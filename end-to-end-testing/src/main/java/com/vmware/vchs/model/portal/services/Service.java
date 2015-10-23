package com.vmware.vchs.model.portal.services;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by georgeliu on 15/4/23.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Service {

    private String id;
    private String serviceCode;
    private String name;
    private String description;
    private String provider;
    private String url;
    private String icon;
    private String updatedAt;
    private String templateId;
    private String[] permissions;
    private List<ServicePlan> plans;
    private List<Version> version;
    private List<LicenseType> licenseType;
    private List<Edition> edition;

    public List<Edition> getEdition() {
        return edition;
    }

    public void setEdition(List<Edition> edition) {
        this.edition = edition;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
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

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String[] getPermissions() {
        return permissions;
    }

    public void setPermissions(String[] permissions) {
        this.permissions = permissions;
    }

    public List<ServicePlan> getPlans() {
        return plans;
    }

    public void setPlans(List<ServicePlan> plans) {
        this.plans = plans;
    }

    public List<Version> getVersion() {
        return version;
    }

    public void setVersion(List<Version> version) {
        this.version = version;
    }

    public List<LicenseType> getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(List<LicenseType> licenseType) {
        this.licenseType = licenseType;
    }
}
