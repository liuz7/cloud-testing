package com.vmware.vchs.test.client.cds;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jayway.jsonpath.JsonPath;
import com.vmware.vchs.common.utils.Utils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Created by georgeliu on 15/8/6.
 */
public class CDSClient {

    private static String baseUrl;

    public CDSClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Map<String, String> getCurrentReleaseVersions() {
        Map<String, String> result = Maps.newHashMap();
        URI deploymentsUrl = UriComponentsBuilder.fromHttpUrl(this.baseUrl)
                .path("api/deployments")
                .build()
                .toUri();
        String jsonString = getStringContentFromUrl(deploymentsUrl);
        if (jsonString == null) {
            return result;
        }
        List<String> currentDeployments = JsonPath.read(jsonString, "$.deployments[?(@.state=='OK')].name");
        for (String deploymentName : currentDeployments) {
            List<String> currentDeploymentVersion = JsonPath.read(jsonString, "$.deployments[?(@.name=='" + deploymentName + "')].release.version");
            if (currentDeploymentVersion.size() == 1) {
                result.put(deploymentName, currentDeploymentVersion.get(0));
            }
        }
        return result;
    }

    public List<Map<String, String>> getCurrentReleaseComponentVersions(String deploymentName) {
        List<Map<String, String>> result = Lists.newArrayList();
        List<String> currentComponentNames;
        URI currentReleaseUrl = UriComponentsBuilder.fromHttpUrl(this.baseUrl)
                .path("api/deployments/" + deploymentName + "/releases/current")
                .build()
                .toUri();
        String jsonString = getStringContentFromUrl(currentReleaseUrl);
        if (jsonString == null) {
            return result;
        }
        currentComponentNames = JsonPath.read(jsonString, "$.release.manifest.services[*].name");
        for (String componentName : currentComponentNames) {
            List<String> componentVersions = JsonPath.read(jsonString, "$.release.manifest.services[?(@.name=='" + componentName + "')].version");
            if (componentVersions.size() == 1) {
                Map<String, String> componentVersionMap = Maps.newHashMap();
                componentVersionMap.put(componentName, componentVersions.get(0));
                result.add(componentVersionMap);
            }
        }
        return result;
    }

    private String getStringContentFromUrl(URI url) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        String bodyAsString = null;
        try {
            CloseableHttpResponse response = client.execute(new HttpGet(url));
            bodyAsString = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            Utils.getStackTrace(e);
        }
        return bodyAsString;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(new CDSClient("http://10.156.75.239").getCurrentReleaseComponentVersions("dsp-20150724_24"));
    }
}
