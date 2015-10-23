package com.vmware.vchs.test.client.vcloud;

import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.admin.AdminOrganization;
import com.vmware.vcloud.sdk.constants.Version;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;

import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.logging.Level;

public class LoginAdapter {

    private static final class FakeSSLSocketFactory {

        private FakeSSLSocketFactory() {
        }

        public static SSLSocketFactory getInstance() throws KeyManagementException, UnrecoverableKeyException,
                NoSuchAlgorithmException, KeyStoreException {
            return new SSLSocketFactory(new TrustStrategy() {
                @Override
                public boolean isTrusted(final X509Certificate[] chain, final String auth) throws CertificateException {
                    return true;
                }

            }, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        }
    }

    private final String orgUrl;
    private final String orgName;
    private final String userName;
    private final String password;

    private volatile VcloudClient client = null;

    public LoginAdapter(final String orgUrl, final String orgName, final String userName, final String password) {
        super();
        this.orgUrl = orgUrl;
        this.orgName = orgName;
        this.userName = userName;
        this.password = password;
    }

    public VcloudClient getVCloudClient() {
        return client;
    }

    public void login() throws VCloudException {
        boolean exceptionOccurred = false;
        if (client != null) {
            return;
        }

        VcloudClient.setLogLevel(Level.OFF);
        client = new VcloudClient(orgUrl, Version.V5_5);

        try {
            final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            client.registerScheme("https", 443, FakeSSLSocketFactory.getInstance());
            client.login(String.format("%s@%s", userName, orgName), password);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
                | KeyManagementException | UnrecoverableKeyException  e) {
            exceptionOccurred = true;
            throw new RuntimeException("Exception during login: ", e);
        } finally {
            if (exceptionOccurred) {
                client = null;
            }
        }
    }

    public void loginAsSystem() throws VCloudException {
        boolean exceptionOccurred = false;
        if (client != null) {
            return;
        }

        VcloudClient.setLogLevel(Level.OFF);
        client = new VcloudClient(orgUrl, Version.V5_5);

        try {
            final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            client.registerScheme("https", 443, FakeSSLSocketFactory.getInstance());
            client.login(String.format("%s@%s", userName, "system"), password);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
                | KeyManagementException | UnrecoverableKeyException  e) {
            exceptionOccurred = true;
            throw new RuntimeException("Exception during login: ", e);
        } finally {
            if (exceptionOccurred) {
                client = null;
            }
        }
    }

    public void loginUsingSSOToken(final String token) throws VCloudException {
        boolean exceptionOccurred = false;
        if (client != null) {
            return;
        }

        VcloudClient.setLogLevel(Level.OFF);
        client = new VcloudClient(orgUrl, Version.V5_5);

        try {
            final KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            client.registerScheme("https", 443, FakeSSLSocketFactory.getInstance());
            client.ssoLogin(token, orgName);
            //client.login(arg0, arg1);
        } catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException
                | KeyManagementException | UnrecoverableKeyException  e) {
            exceptionOccurred = true;
            throw new RuntimeException("Exception during login: ", e);
        } finally {
            if (exceptionOccurred) {
                client = null;
            }
        }
    }

    public void logout() throws VCloudException {
        try {
            if (client != null) {
                client.logout();
            }
        } finally {
            client = null;
        }
    }

    public Vdc getVdcRef(final String adminVdcName) throws VCloudException {
        final ReferenceType adminOrgRef = client.getVcloudAdmin().getAdminOrgRefByName(orgName);
        final AdminOrganization adminOrg = AdminOrganization.getAdminOrgByReference(client, adminOrgRef);
        final ReferenceType adminVdcRef = adminOrg.getAdminVdcRefByName(adminVdcName);
        return Vdc.getVdcByReference(client, adminVdcRef);
    }

    public Vdc getAnyVdcRef() throws VCloudException {
        final ReferenceType adminOrgRef = client.getVcloudAdmin().getAdminOrgRefByName(orgName);
        final AdminOrganization adminOrg = AdminOrganization.getAdminOrgByReference(client, adminOrgRef);

        ReferenceType adminVdcRef = null;
        final Collection<ReferenceType> refs = adminOrg.getAdminVdcRefs();
        if (refs != null && !refs.isEmpty()) {
            final ReferenceType[] refsArray = refs.toArray(new ReferenceType[refs.size()]);
            adminVdcRef = refsArray[0];
        }
        return Vdc.getVdcByReference(client, adminVdcRef);
    }
}
