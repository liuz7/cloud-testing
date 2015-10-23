/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */
package com.vmware.vchs.test.client.vcloud;

import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.admin.EdgeGateway;
import com.vmware.vcloud.sdk.constants.FenceModeValuesType;

public class EdgeGatewayConfigDriver {

    public static void main(final String[] args) throws VCloudException {
        showEdgeGatewayDetailsPraxis0();
        //showEdgeGatewayDetailsDBaaSOrg();
        //showEdgeGatewayDetailsTestCDS();
        //configureTestCDS();
        //showEdgeGatewayDetailsNimbus();
    }

    public static void addRoutedOrgVdcNetworkToCustomerORG() throws VCloudException {
        final String orgUrl = "https://p2v3-vcd.vchs-arch.vmware.com";
        final String orgName = "Dbaas-Service-Network";
        final String adminVdcName = "Dbass Service Network";
        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "tej", "ca$hc0w");
        loginAdapter.login();

        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);

        final String edgeGatewayName = "Edge-Service1";
        final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(edgeGatewayName, loginAdapter.getVCloudClient(), vdc);
        edgeGatewayAdapter.showEdgeGatewayDetails();

        edgeGatewayAdapter.addNatRoutedOrgVdcNetwork("cust-org-nw", "Customer Routed ORG vDC network", "255.255.255.0", "10.10.10.101",
                "10.10.10.1", "10.10.10.100", FenceModeValuesType.NATROUTED);

        loginAdapter.logout();
    }

    public static void addIsolatedVdcNetwork() throws VCloudException {
        final String orgUrl = "https://p2v3-vcd.vchs-arch.vmware.com";
        final String orgName = "DBaaS-Services";
        final String adminVdcName = "DbaaS Service";
        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "admin", "VMware1234!");
        loginAdapter.login();

        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);

        final String edgeGatewayName = "Edge-Dbaas-Service";
        final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(edgeGatewayName, loginAdapter.getVCloudClient(), vdc);
        edgeGatewayAdapter.showEdgeGatewayDetails();

        edgeGatewayAdapter.addNatRoutedOrgVdcNetwork("db-nw3", "DbaaS Isolated vDC network", "255.255.255.0", "10.10.10.101",
                "10.10.10.1", "10.10.10.100", FenceModeValuesType.ISOLATED);

        loginAdapter.logout();
    }

    public static void addRoutedOrgVdcNetwork() throws VCloudException {
        final String orgUrl = "https://p2v3-vcd.vchs-arch.vmware.com";
        final String orgName = "DBaaS-Services";
        final String adminVdcName = "DbaaS Service";
        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "admin", "VMware1234!");
        loginAdapter.login();

        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);

        final String edgeGatewayName = "Edge-Dbaas-Service";
        final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(edgeGatewayName, loginAdapter.getVCloudClient(), vdc);
        edgeGatewayAdapter.showEdgeGatewayDetails();

        edgeGatewayAdapter.addNatRoutedOrgVdcNetwork("db-nw2", "DbaaS ORG vDC network2", "255.255.255.0", "192.168.1.101",
                "192.168.1.1", "192.168.1.100", FenceModeValuesType.NATROUTED);

        loginAdapter.logout();
    }

    public static void confgureDNATRules() throws VCloudException {
        final String orgUrl = "https://p2v3-vcd.vchs-arch.vmware.com";
        final String orgName = "DBaaS-Services";
        final String adminVdcName = "DbaaS Service";
        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "admin", "VMware1234!");
        loginAdapter.login();

        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);

        final String edgeGatewayName = "Edge-Dbaas-Service";
        final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(edgeGatewayName, loginAdapter.getVCloudClient(), vdc);

        //edgeGatewayAdapter.addDNATRule("Jetty NAT rule", "100.64.1.126", 5000, "192.168.1.2", 8746);
        //edgeGatewayAdapter.addDNATRule("SSHD NAT rule", "100.64.1.126", 5001, "192.168.1.2", 22);

        //edgeGatewayAdapter.addDNATRule("Jetty NAT rule", "100.64.1.126", 5002, "192.168.1.1", 8746);
        //edgeGatewayAdapter.addDNATRule("SSHD NAT rule", "100.64.1.126", 5003, "192.168.1.1", 22);

        edgeGatewayAdapter.addDNATRule("DNAT rule", "100.64.1.126", "192.168.1.1");
        edgeGatewayAdapter.addDNATRule("DNAT rule", "100.64.1.127", "192.168.1.2");

        edgeGatewayAdapter.showEdgeGatewayDetails();

        loginAdapter.logout();
    }

    public static void removeDNATRules() throws VCloudException {
        final String orgUrl = "https://p2v3-vcd.vchs-arch.vmware.com";
        final String orgName = "DBaaS-Services";
        final String adminVdcName = "DbaaS Service";
        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "admin", "VMware1234!");
        loginAdapter.login();

        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);

        final String edgeGatewayName = "Edge-Dbaas-Service";
        final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(edgeGatewayName, loginAdapter.getVCloudClient(), vdc);

        //edgeGatewayAdapter.addDNATRule("Jetty NAT rule", "100.64.1.126", 5000, "192.168.1.2", 8746);
        //edgeGatewayAdapter.addDNATRule("SSHD NAT rule", "100.64.1.126", 5001, "192.168.1.2", 22);

        //edgeGatewayAdapter.addDNATRule("Jetty NAT rule", "100.64.1.126", 5002, "192.168.1.1", 8746);
        //edgeGatewayAdapter.addDNATRule("SSHD NAT rule", "100.64.1.126", 5003, "192.168.1.1", 22);

        edgeGatewayAdapter.removeDNATRule("100.64.1.126", "192.168.1.3");

        edgeGatewayAdapter.showEdgeGatewayDetails();

        loginAdapter.logout();
    }

    public static void confgureSNATRules() throws VCloudException {
        final String orgUrl = "https://p2v3-vcd.vchs-arch.vmware.com";
        final String orgName = "Dbaas-Service-Network";
        final String adminVdcName = "Dbass Service Network";
        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "tej", "ca$hc0w");
        loginAdapter.login();

        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);

        final String edgeGatewayName = "Edge-Service1";
        final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(edgeGatewayName, loginAdapter.getVCloudClient(), vdc);

        //edgeGatewayAdapter.addSNATRule("SNAT rule", "192.168.1.2", "100.64.1.119");
        //edgeGatewayAdapter.addSNATRule("SNAT rule", "192.168.1.3", "100.64.1.119");
        //edgeGatewayAdapter.addSNATRule("SNAT rule", "0.0.0.0/0", "100.64.1.119");
        edgeGatewayAdapter.addSNATRule("SNAT rule", "192.168.1.3", "100.64.1.120", "5005");

        edgeGatewayAdapter.showEdgeGatewayDetails();
        loginAdapter.logout();
    }

    public static void showEdgeGatewayDetailsCustomerOrg() throws VCloudException {
        final String orgUrl = "https://p2v3-vcd.vchs-arch.vmware.com";
        final String orgName = "Dbaas-Service-Network";
        final String adminVdcName = "Dbass Service Network";
        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "tej", "ca$hc0w");
        loginAdapter.login();

        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);

        final String edgeGatewayName = "Edge-Service1";
        final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(edgeGatewayName, loginAdapter.getVCloudClient(), vdc);

        edgeGatewayAdapter.showEdgeGatewayDetails();
        loginAdapter.logout();
    }

    public static void showSNATRulesCustomerOrg() throws VCloudException {
        final String orgUrl = "https://p2v3-vcd.vchs-arch.vmware.com";
        final String orgName = "Dbaas-Service-Network";
        final String adminVdcName = "Dbass Service Network";
        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "tej", "ca$hc0w");
        loginAdapter.login();

        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);

        final String edgeGatewayName = "Edge-Service1";
        final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(edgeGatewayName, loginAdapter.getVCloudClient(), vdc);

        edgeGatewayAdapter.showSNATRules();
        loginAdapter.logout();
    }

    public static void showEdgeGatewayDetailsAkhlaqOrg(final String[] args) throws VCloudException {
        final String orgUrl = "https://p2v3-vcd.vchs-arch.vmware.com";
        final String orgName = "akhlaq";
        final String adminVdcName = "akhlaq";
        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "aali", "VMware1,1");
        loginAdapter.login();

        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);

        final String edgeGatewayName = "Edge1";
        final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(edgeGatewayName, loginAdapter.getVCloudClient(), vdc);
        edgeGatewayAdapter.showEdgeGatewayDetails();
        loginAdapter.logout();
    }

    public static void showEdgeGatewayDetailsDBaaSOrg() throws VCloudException {
        final String orgUrl = "https://p2v3-vcd.vchs-arch.vmware.com";
        final String orgName = "DBaaS-Services";
        final String adminVdcName = "DbaaS Service";
        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "admin", "VMware1234!");
        loginAdapter.login();

        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);
        System.out.println("VDC name: " + vdc.getResource().getName());

        final String edgeGatewayName = "Edge-Dbaas-Service";
        final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(edgeGatewayName, loginAdapter.getVCloudClient(), vdc);
        edgeGatewayAdapter.showEdgeGatewayDetails();
        loginAdapter.logout();
    }

    public static void showEdgeGatewayDetailsNimbus() throws VCloudException {
        final String orgUrl = "https://10.161.15.227";
        final String orgName = "vca";
        final String adminVdcName = "dbaasvdc";
        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "tdas", "ca$hc0w");
        loginAdapter.login();

        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);

        final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(null, loginAdapter.getVCloudClient(), vdc);
        edgeGatewayAdapter.showEdgeGatewayDetails();
        loginAdapter.logout();
    }


    public static void showEdgeGatewayDetailsPraxis0() throws VCloudException {
//        final String orgUrl = "https://10.145.206.46";
//        final String orgName = "09CBBBEC-334C-4ECC-8A53-9137B7BAE3B4";
//        final String adminVdcName = "DBaaSVDC";
//        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "tdasna", "ca$hc0w");
//        loginAdapter.login();
//
//        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);
//
//        final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(null, loginAdapter.getVCloudClient(), vdc);
//        edgeGatewayAdapter.showEdgeGatewayDetails();
//        loginAdapter.logout();
        final String orgUrl = "https://10.156.74.130";
        final String orgName = "dbaas";
        final String adminVdcName = "DBaaSVDC";
        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "dev_01", "password");
        loginAdapter.login();

        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);

        final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(null, loginAdapter.getVCloudClient(), vdc);
        edgeGatewayAdapter.showEdgeGatewayDetails();
        loginAdapter.logout();
    }

    public static void showEdgeGatewayDetailsTestCDS() throws VCloudException {
        final String orgUrl = "https://p5v8-vcd.vchs-int.vmware.com";
        final String orgName = "dbaas";
        final String adminVdcName = "testcds";
        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "tdas", "ca$hc0w");
        loginAdapter.login();

        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);

        final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(null, loginAdapter.getVCloudClient(), vdc);
        edgeGatewayAdapter.showEdgeGatewayDetails();
        loginAdapter.logout();
    }

    public static void configureTestCDS() throws VCloudException {
        final String orgUrl = "https://p5v8-vcd.vchs-int.vmware.com";
        final String orgName = "dbaas";
        final String adminVdcName = "testcds";
        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "acompeau", "Rabidvc1sw4n!");
        loginAdapter.loginAsSystem();

        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);

        final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(null, loginAdapter.getVCloudClient(), vdc);
        edgeGatewayAdapter.configure();
        edgeGatewayAdapter.showEdgeGatewayDetails();
        loginAdapter.logout();
    }

    public static void addFirewallRule() throws VCloudException {
        final String orgUrl = "https://p2v3-vcd.vchs-arch.vmware.com";
        final String orgName = "DBaaS-Services";
        final String adminVdcName = "DbaaS Service";
        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "admin", "VMware1234!");
        loginAdapter.login();

        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);

        final String edgeGatewayName = "Edge-Dbaas-Service";
        final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(edgeGatewayName, loginAdapter.getVCloudClient(), vdc);

        edgeGatewayAdapter.addFirewallRule("DBaaS Firewall rule", "100.64.1.120", "Any", "100.64.1.126", "8746");
        edgeGatewayAdapter.addFirewallRule("DBaaS Firewall rule", "100.64.1.119", "Any", "100.64.1.127", "8746");
        edgeGatewayAdapter.showEdgeGatewayDetails();

        loginAdapter.logout();
    }

    public static void main4(final String[] args) throws VCloudException {
        final String orgUrl = "https://p2v3-vcd.vchs-arch.vmware.com";
        final String orgName = "Dbaas-Service-Network";
        final String adminVdcName = "Dbass Service Network";
        final String edgeGatewayName = "Edge-Service1";

        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "tej", "ca$hc0w");
        loginAdapter.login();

        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);
        EdgeGatewayUtils.getEdgeGatewayDetails(loginAdapter.getVCloudClient(), vdc);

        final EdgeGateway edgeGateway = EdgeGatewayUtils.getEdgeGateway(loginAdapter.getVCloudClient(), vdc, edgeGatewayName);
        System.out.println("Uplink interface name: " + EdgeGatewayUtils.getUplinkInterfaceName(edgeGateway));

        //EdgeGatewayUtils.addNatRoutedOrgVdcNetwork(edgeGateway, vdc);


        loginAdapter.getVCloudClient().getVcloudAdmin().getExternalNetworkRefByName("XaaS-Network");

        EdgeGatewayUtils.addDNATRule(loginAdapter.getVCloudClient(), vdc, edgeGatewayName);

        loginAdapter.logout();
    }
}
