package com.vmware.vchs.load.generator.result.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by georgeliu on 15/5/28.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetailInfluxDbResponse {

    private String name;
    private String[] columns;
    private String[][] points;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[][] getPoints() {
        return points;
    }

    public void setPoints(String[][] points) {
        this.points = points;
    }

    public String[] getColumns() {
        return columns;
    }

    public void setColumns(String[] columns) {
        this.columns = columns;
    }
//
//    public String[] getPoints() {
//        return points;
//    }
//
//    public void setPoints(String[] points) {
//        this.points = points;
//    }
}
