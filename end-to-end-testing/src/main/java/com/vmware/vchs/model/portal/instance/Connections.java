package com.vmware.vchs.model.portal.instance;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.vmware.vchs.test.config.Configuration;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * Created by georgeliu on 15/5/11.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Connections {

    private DataPath dataPath;

    public DataPath getDataPath() {
        return dataPath;
    }

    public void setDataPath(DataPath dataPath) {
        this.dataPath = dataPath;
    }
}
