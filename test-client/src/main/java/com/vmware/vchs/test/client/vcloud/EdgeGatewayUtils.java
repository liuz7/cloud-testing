/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */
package com.vmware.vchs.test.client.vcloud;

import java.util.List;
import java.util.concurrent.TimeoutException;

import javax.xml.bind.JAXBElement;

import com.vmware.vcloud.api.rest.schema.GatewayConfigurationType;
import com.vmware.vcloud.api.rest.schema.GatewayFeaturesType;
import com.vmware.vcloud.api.rest.schema.GatewayInterfaceType;
import com.vmware.vcloud.api.rest.schema.GatewayNatRuleType;
import com.vmware.vcloud.api.rest.schema.IpRangeType;
import com.vmware.vcloud.api.rest.schema.IpRangesType;
import com.vmware.vcloud.api.rest.schema.IpScopeType;
import com.vmware.vcloud.api.rest.schema.IpScopesType;
import com.vmware.vcloud.api.rest.schema.NatRuleType;
import com.vmware.vcloud.api.rest.schema.NatServiceType;
import com.vmware.vcloud.api.rest.schema.NetworkConfigurationType;
import com.vmware.vcloud.api.rest.schema.NetworkServiceType;
import com.vmware.vcloud.api.rest.schema.OrgVdcNetworkType;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.OrgVdcNetwork;
import com.vmware.vcloud.sdk.Task;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;
import com.vmware.vcloud.sdk.Vdc;
import com.vmware.vcloud.sdk.admin.EdgeGateway;
import com.vmware.vcloud.sdk.constants.FenceModeValuesType;
import com.vmware.vcloud.sdk.exception.MissingPropertyException;

public class EdgeGatewayUtils {
    public static void getEdgeGatewayDetails(final VcloudClient client, final Vdc vdc) throws VCloudException {
        final List<ReferenceType> refs = vdc.getEdgeGatewayRefs().getReferences();
        for (final ReferenceType ref : refs) {
            final EdgeGateway egRef = EdgeGateway.getEdgeGatewayByReference(client, ref);
            System.out.println("Edge Gateway Name: " + egRef.getResource().getName());
            System.out.println("Edge Gateway Description: " + egRef.getResource().getDescription());
            System.out.println("Edge Gateway Status: " + egRef.getResource().getStatus());

            final GatewayConfigurationType gwType = egRef.getResource().getConfiguration();
            System.out.println("Edge Gateway HA enabled: " + gwType.isHaEnabled());

            final List<GatewayInterfaceType> gwInterfaces = gwType.getGatewayInterfaces().getGatewayInterface();
            System.out.println("Edge Gateway Interface count: " + gwInterfaces.size());
            System.out.println();



            for (final GatewayInterfaceType gwInterface : gwInterfaces) {
                System.out.println("Interface display name: " + gwInterface.getDisplayName());
                System.out.println("Interface type: " + gwInterface.getInterfaceType());
                System.out.println("Interface  name: " + gwInterface.getName());

                final ReferenceType networkRef = gwInterface.getNetwork();
                System.out.println("Interface ref type: " + networkRef);

                if (!"uplink".equalsIgnoreCase(gwInterface.getInterfaceType())) {
                    final OrgVdcNetwork network = OrgVdcNetwork.getOrgVdcNetworkByReference(client, networkRef);
                    System.out.println("Org VDC Network: " + network);
                } else {
                    //final ExternalNetwork externalNetwork = ExternalNetwork.getExternalNetworkByReference(client, networkRef);
                    //System.out.println("External Network: " + externalNetwork);
                }

                System.out.println();
            }
        }
    }

    public static EdgeGateway getEdgeGateway(final VcloudClient client, final Vdc vdc, final String edgeGatewayName)
            throws VCloudException {
        final List<ReferenceType> refs = vdc.getEdgeGatewayRefs().getReferences();
        for (final ReferenceType ref : refs) {
            final EdgeGateway egRef = EdgeGateway.getEdgeGatewayByReference(client, ref);
            if (edgeGatewayName.equalsIgnoreCase(egRef.getResource().getName())) {
                return egRef;
            }
        }
        return null;
    }

    public static String getUplinkInterfaceName(final EdgeGateway edgeGateway) {
        final GatewayConfigurationType gwType = edgeGateway.getResource().getConfiguration();
        final List<GatewayInterfaceType> gwInterfaces = gwType.getGatewayInterfaces().getGatewayInterface();
        for (final GatewayInterfaceType gwInterface : gwInterfaces) {
            if ("uplink".equalsIgnoreCase(gwInterface.getInterfaceType())) {
                return gwInterface.getName();
            }
        }
        return null;
    }

    public static GatewayInterfaceType getUplinkInterface(final EdgeGateway edgeGateway) {
        final GatewayConfigurationType gwType = edgeGateway.getResource().getConfiguration();
        final List<GatewayInterfaceType> gwInterfaces = gwType.getGatewayInterfaces().getGatewayInterface();
        for (final GatewayInterfaceType gwInterface : gwInterfaces) {
            if ("uplink".equalsIgnoreCase(gwInterface.getInterfaceType())) {
                return gwInterface;
            }
        }
        return null;
    }

    public static void addNatRoutedOrgVdcNetwork(final EdgeGateway edgeGateway, final Vdc vdc) throws VCloudException {

        final OrgVdcNetworkType orgVdcNetworkParams = new OrgVdcNetworkType();
        orgVdcNetworkParams.setName("Nat-Routed_Org_Vdc_Network");
        orgVdcNetworkParams.setDescription("Org vdc network of type Nat-Routed");

        // Configure Internal IP Settings
        final NetworkConfigurationType netConfig = new NetworkConfigurationType();
        netConfig.setRetainNetInfoAcrossDeployments(true);

        final IpScopeType ipScope = new IpScopeType();
        ipScope.setNetmask("255.255.255.0");
        ipScope.setGateway("192.168.1.101");
        ipScope.setIsEnabled(true);
        ipScope.setIsInherited(true);

        // IP Ranges
        final IpRangesType ipRangesType = new IpRangesType();
        final IpRangeType ipRangeType = new IpRangeType();
        ipRangeType.setStartAddress("192.168.1.2");
        ipRangeType.setEndAddress("192.168.1.100");

        ipRangesType.getIpRange().add(ipRangeType);

        ipScope.setIpRanges(ipRangesType);
        ipScope.setIsEnabled(true);
        final IpScopesType ipScopes = new IpScopesType();
        ipScopes.getIpScope().add(ipScope);
        netConfig.setIpScopes(ipScopes);
        netConfig.setFenceMode(FenceModeValuesType.NATROUTED.value());

        orgVdcNetworkParams.setEdgeGateway(edgeGateway.getReference());
        orgVdcNetworkParams.setConfiguration(netConfig);
        System.out.println("Creating Nat-Routed Org vDC Network");
        try {
            final OrgVdcNetwork orgVdcNet = vdc.createOrgVdcNetwork(orgVdcNetworkParams);
            if (orgVdcNet.getTasks().size() > 0) {
                orgVdcNet.getTasks().get(0).waitForTask(0);
            }
            System.out.println("    Nat-Routed Org vDC Network : " + orgVdcNet.getResource().getName() + " created - "
                    + orgVdcNet.getResource().getHref());
        } catch (final TimeoutException | MissingPropertyException e) {
            System.out.println("FAILED: creating org vdc network - " + e.getLocalizedMessage());
        }
    }

    private static NatServiceType getNATServiceType(final EdgeGateway edgeGateway) {
        final GatewayFeaturesType gatewayFeaturesType = edgeGateway.getResource().getConfiguration()
                .getEdgeGatewayServiceConfiguration();
        final List<JAXBElement<? extends NetworkServiceType>> networkServices = gatewayFeaturesType.getNetworkService();
        for (final JAXBElement<? extends NetworkServiceType> ns : networkServices) {
            final NetworkServiceType networkServiceType = ns.getValue();
            if (networkServiceType.getClass() == NatServiceType.class) {
                return (NatServiceType) networkServiceType;
            }
        }
        return null;
    }

    public static void addDNATRule(final VcloudClient client, final Vdc vdc, final String edgeGatewayName)
            throws VCloudException {
        final EdgeGateway edgeGateway = getEdgeGateway(client, vdc, edgeGatewayName);
        if (edgeGateway == null) {
            return;
        }

        final GatewayInterfaceType externalNetwork = getUplinkInterface(edgeGateway);
        final NatServiceType natServiceType = getNATServiceType(edgeGateway);

        final NatRuleType natRuleType = new NatRuleType();
        natRuleType.setDescription("Test rule");
        natRuleType.setRuleType("DNAT");
        final GatewayNatRuleType gwNatRuleType = new GatewayNatRuleType();
        gwNatRuleType.setOriginalIp("100.64.1.117");
        gwNatRuleType.setOriginalPort("any");
        gwNatRuleType.setProtocol("any");
        gwNatRuleType.setTranslatedIp("172.16.0.2");
        gwNatRuleType.setTranslatedPort("any");
        gwNatRuleType.setInterface(externalNetwork.getNetwork());

        natRuleType.setGatewayNatRule(gwNatRuleType);
        natRuleType.setIsEnabled(true);

        natServiceType.getNatRule().add(natRuleType);

        final GatewayFeaturesType gatewayFeaturesType = edgeGateway.getResource().getConfiguration()
                .getEdgeGatewayServiceConfiguration();

        final Task task = edgeGateway.configureServices(gatewayFeaturesType);

        System.out.println("Waiting for task");
        try {
            task.waitForTask(0);
        } catch (final TimeoutException e) {
            throw new VCloudException("Timed out");
        }
        System.out.println("Task done");

    }
}
