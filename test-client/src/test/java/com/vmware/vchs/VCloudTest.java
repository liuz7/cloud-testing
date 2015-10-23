package com.vmware.vchs;

import com.vmware.vchs.test.client.vcloud.LoginAdapter;
import com.vmware.vchs.test.client.vcloud.VMAdapter;
import com.vmware.vcloud.sdk.Vdc;

/**
 * Created by liuda on 15/4/23.
 */
public class VCloudTest {
    public static void main(String[] args) throws Exception {
        final String orgUrl = "https://10.156.74.130";
        final String orgName = "dbaas";
        final String adminVdcName = "dbaas_dev_51";
        final LoginAdapter loginAdapter = new LoginAdapter(orgUrl, orgName, "dev_51", "password");
        loginAdapter.login();

        final Vdc vdc = loginAdapter.getVdcRef(adminVdcName);

        //final EdgeGatewayAdapter edgeGatewayAdapter = new EdgeGatewayAdapter(null, loginAdapter.getVCloudClient(), vdc);
//        edgeGatewayAdapter.addDNATRule("DBaaS NAT rule", "10.156.74.21", 8085, "192.168.70.20", 8085, "dbaas_ext_net");
//        edgeGatewayAdapter.addFirewallRule("DBaaS Firewall rule", "external", "Any", "10.156.74.21", "8085");
        //edgeGatewayAdapter.showEdgeGatewayDetails();
        VMAdapter vmAdapter = new VMAdapter("cds-vapp-eb691b7dcaea497caba255a311e07381", "vm0", loginAdapter.getVCloudClient(), vdc);
        vmAdapter.reboot();
        loginAdapter.logout();
    }
}
