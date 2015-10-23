package com.vmware.vchs.iam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.MoreObjects;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class HttpClient {
    protected static final Logger logger = LoggerFactory.getLogger(HttpClient.class);

    public static class Response {
        private String token;
        private String serviceGroupId;

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("token", token)
                    .add("serviceGroupId", serviceGroupId)
                    .toString();
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getServiceGroupId() {
            return serviceGroupId;
        }

        public void setServiceGroupId(String serviceGroupId) {
            this.serviceGroupId = serviceGroupId;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getAccessToken("hhu@vmware.com", "Ca\\$hc0w", "iam.vchs.vmware.com"));
    }

    public static Response getAccessToken(String username, String password, String praixUrl) throws Exception {
        Response res = new Response();
        SSLSocketFactory sslsf = new SSLSocketFactory(new TrustStrategy() {

            public boolean isTrusted(
                    final X509Certificate[] chain, String authType) throws CertificateException {
                return true;
            }
        });
        if (!praixUrl.startsWith("https")) {
            praixUrl = "https://" + praixUrl;
        }
        HttpPost httppost = new HttpPost(praixUrl + "/api/iam/login");
        String authString = username + ":" + password;
        httppost.setHeader("Authorization", "Basic " + Base64.encodeBase64String(authString.getBytes()));
        httppost.setHeader("Accept", "application/json");
        DefaultHttpClient client = new DefaultHttpClient();
        client.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, sslsf));
        HttpResponse response = client.execute(httppost);
        String result = EntityUtils.toString(response.getEntity());
        logger.info("get access token result is :" + result);
        if (response.getStatusLine().getStatusCode() == 201 || response.getStatusLine().getStatusCode() == 200) {
            res.setToken(response.getHeaders("vchs-authorization")[0].getValue());
            ObjectMapper mapper = new ObjectMapper();
            ServiceGroupId serviceGroupId = mapper.readValue(result, ServiceGroupId.class);
            res.setServiceGroupId(serviceGroupId.getServiceGroupIds()[0]);
            logger.info(res.toString());
            return res;
        }
        return res;
    }
}
