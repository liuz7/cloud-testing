/*
 * ******************************************************
 * Copyright VMware, Inc. 2014.   All Rights Reserved.
 * ******************************************************
 */
package com.vmware.vchs.test.client.vcloud;

import com.vmware.vcloud.api.rest.schema.*;
import com.vmware.vcloud.sdk.*;
import com.vmware.vcloud.sdk.admin.EdgeGateway;
import com.vmware.vcloud.sdk.constants.FenceModeValuesType;
import com.vmware.vcloud.sdk.exception.MissingPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class EdgeGatewayAdapter {
    private final String edgeGatewayName;

    private volatile EdgeGateway edgeGateway;

    private final VcloudClient client;

    private final Vdc vdc;

    protected static final Logger logger = LoggerFactory.getLogger(EdgeGatewayAdapter.class);

    public EdgeGatewayAdapter(final String name, final VcloudClient client, final Vdc vdc) {
        super();
        this.edgeGatewayName = name;
        this.client = client;
        this.vdc = vdc;
    }

    public void initialize() throws VCloudException {
        if (edgeGateway != null) {
            return;
        }

        final List<ReferenceType> refs = vdc.getEdgeGatewayRefs().getReferences();
        for (final ReferenceType ref : refs) {
            final EdgeGateway egRef = EdgeGateway.getEdgeGatewayByReference(client, ref);
            if (edgeGatewayName == null) {
                edgeGateway = egRef;
                return;
            }
            if (edgeGatewayName.equalsIgnoreCase(egRef.getResource().getName())) {
                edgeGateway = egRef;
                return;
            }
        }
        throw new VCloudException("Edge gateway not found: " + edgeGatewayName);
    }

    public void showEdgeGatewayDetails() throws VCloudException {
        initialize();

        logger.info("Edge Gateway HREF: " + edgeGateway.getReference().getHref());
        logger.info("Edge Gateway ID: " + edgeGateway.getReference().getId());
        logger.info("Edge Gateway Name: " + edgeGateway.getReference().getName());
        logger.info("Edge Gateway Type: " + edgeGateway.getReference().getType());
        logger.info("-----------------");
        logger.info("Edge Gateway Name: " + edgeGateway.getResource().getName());
        logger.info("Edge Gateway Description: " + edgeGateway.getResource().getDescription());
        logger.info("Edge Gateway Status: " + edgeGateway.getResource().getStatus());

        final GatewayConfigurationType gwType = edgeGateway.getResource().getConfiguration();
        logger.info("Edge Gateway HA enabled: " + gwType.isHaEnabled());

        final List<GatewayInterfaceType> gwInterfaces = gwType.getGatewayInterfaces().getGatewayInterface();
        logger.info("Edge Gateway Interface count: " + gwInterfaces.size());

        for (final GatewayInterfaceType gwInterface : gwInterfaces) {
            logger.info("Interface display name: " + gwInterface.getDisplayName());
            logger.info("Interface type: " + gwInterface.getInterfaceType());
            logger.info("Interface  name: " + gwInterface.getName());

            final ReferenceType networkRef = gwInterface.getNetwork();
            logger.info("Interface ref type: " + networkRef);

            if (!"uplink".equalsIgnoreCase(gwInterface.getInterfaceType())) {
                final OrgVdcNetwork network = OrgVdcNetwork.getOrgVdcNetworkByReference(client, networkRef);
                logger.info("Org VDC Network: " + network);
            } else {
                final List<SubnetParticipationType> subnetParticipations = gwInterface.getSubnetParticipation();
                for (final SubnetParticipationType subnetParticipation : subnetParticipations) {

                    logger.info(subnetParticipation.getIpAddress());
                    logger.info(subnetParticipation.getGateway());
                    logger.info(subnetParticipation.getNetmask());

                    final IpRangesType ipRangesType = subnetParticipation.getIpRanges();
                    final List<IpRangeType> ranges = ipRangesType.getIpRange();

                    for (final IpRangeType range : ranges) {
                        logger.info("Start address:" +  range.getStartAddress());
                        logger.info("End address:" +  range.getEndAddress());
                    }

                    logger.info("Edge Gateway IP address: " + subnetParticipation.getIpAddress());
                }
            }
        }

        final NatServiceType natServiceType = getNATServiceType();
        final List<NatRuleType> natRules = natServiceType.getNatRule();

        for (final NatRuleType natRule : natRules) {
            logger.info(natRule.getRuleType());

            if (null != natRule.getGatewayNatRule()) {
                final GatewayNatRuleType gwNatRule = natRule.getGatewayNatRule();

                logger.info("Original IP: " + gwNatRule.getOriginalIp());
                logger.info("Original Port: " + gwNatRule.getOriginalPort());
                logger.info("Translated IP: " + gwNatRule.getTranslatedIp());
                logger.info("Translated Port: " + gwNatRule.getTranslatedPort());
                logger.info("Protocol: " + gwNatRule.getProtocol());
            }
        }

        final FirewallServiceType firewallServiceType = getFirewallServiceType();
        final List<FirewallRuleType> fwRules = firewallServiceType.getFirewallRule();
        for (final FirewallRuleType fwRule : fwRules) {
            logger.info("Firewall Rule Description: " + fwRule.getDescription());
            logger.info("Firewall Rule Destination IP: " + fwRule.getDestinationIp());
            logger.info("Firewall Rule Destination Port range: " + fwRule.getDestinationPortRange());
            logger.info("Firewall Rule Direction: " + fwRule.getDirection());
            logger.info("Firewall Rule ICMP subtype: " + fwRule.getIcmpSubType());
            logger.info("Firewall Rule Policy: " + fwRule.getPolicy());
            logger.info("Firewall Rule Source IP: " + fwRule.getSourceIp());
            logger.info("Firewall Rule Source Port range: " + fwRule.getSourcePortRange());
            logger.info("Firewall Rule Port: " + fwRule.getPort());
            logger.info("Firewall Rule Source Port: " + fwRule.getSourcePort());
            final FirewallRuleProtocols protocols = fwRule.getProtocols();
            if (protocols != null) {
                logger.info("Firewall Rule IsAny: " + protocols.isAny());
                logger.info("Firewall Rule IsICMP: " + protocols.isIcmp());
                logger.info("Firewall Rule isTCP: " + protocols.isTcp());
                logger.info("Firewall Rule isUDP: " + protocols.isUdp());
            }
        }
    }

    public void configure() throws VCloudException {
        initialize();

        logger.info("Edge Gateway HREF: " + edgeGateway.getReference().getHref());
        logger.info("Edge Gateway ID: " + edgeGateway.getReference().getId());
        logger.info("Edge Gateway Name: " + edgeGateway.getReference().getName());
        logger.info("Edge Gateway Type: " + edgeGateway.getReference().getType());
        logger.info("-----------------");
        logger.info("Edge Gateway Name: " + edgeGateway.getResource().getName());
        logger.info("Edge Gateway Description: " + edgeGateway.getResource().getDescription());
        logger.info("Edge Gateway Status: " + edgeGateway.getResource().getStatus());

        final GatewayConfigurationType gwType = edgeGateway.getResource().getConfiguration();
        logger.info("Edge Gateway HA enabled: " + gwType.isHaEnabled());

        final List<GatewayInterfaceType> gwInterfaces = gwType.getGatewayInterfaces().getGatewayInterface();
        logger.info("Edge Gateway Interface count: " + gwInterfaces.size());
        

        for (final GatewayInterfaceType gwInterface : gwInterfaces) {
            logger.info("Interface display name: " + gwInterface.getDisplayName());
            logger.info("Interface type: " + gwInterface.getInterfaceType());
            logger.info("Interface  name: " + gwInterface.getName());

            final ReferenceType networkRef = gwInterface.getNetwork();
            logger.info("Interface ref type: " + networkRef);

            if (!"uplink".equalsIgnoreCase(gwInterface.getInterfaceType())) {
                final OrgVdcNetwork network = OrgVdcNetwork.getOrgVdcNetworkByReference(client, networkRef);
                logger.info("Org VDC Network: " + network);
            } else {
                final List<SubnetParticipationType> subnetParticipations = gwInterface.getSubnetParticipation();
                for (final SubnetParticipationType subnetParticipation : subnetParticipations) {

                    logger.info(subnetParticipation.getIpAddress());
                    logger.info(subnetParticipation.getGateway());
                    logger.info(subnetParticipation.getNetmask());

                    logger.info("Edge Gateway IP address: " + subnetParticipation.getIpAddress());

                    final IpRangesType ipRangesType = subnetParticipation.getIpRanges();
                    final List<IpRangeType> ranges = ipRangesType.getIpRange();

                    for (final IpRangeType range : ranges) {
                        logger.info("Start address:" +  range.getStartAddress());
                        logger.info("End address:" +  range.getEndAddress());
                    }

                    if (gwInterface.getDisplayName().equals("DBaaS-Services")) {
                        final IpRangeType range = new IpRangeType();
                        range.setStartAddress("100.64.1.70");
                        range.setEndAddress("100.64.1.77");
                        ranges.add(range);

                        final GatewayFeaturesType gatewayFeaturesType = edgeGateway.getResource().getConfiguration()
                                .getEdgeGatewayServiceConfiguration();

                        final Task task = edgeGateway.configureServices(gatewayFeaturesType);

                        logger.info("Waiting for DNAT rule to be created");
                        try {
                            task.waitForTask(0);
                        } catch (final TimeoutException e) {
                            throw new VCloudException("Timed out");
                        }
                        logger.info("Added");
                    }
                }
            }

        }
    }

    public void showSNATRules() throws VCloudException {
        initialize();

        final NatServiceType natServiceType = getNATServiceType();
        final List<NatRuleType> natRules = natServiceType.getNatRule();

        for (final NatRuleType natRule : natRules) {
            if (natRule.getRuleType().equals("SNAT")) {
                logger.info(natRule.getRuleType());

                if (null != natRule.getGatewayNatRule()) {
                    final GatewayNatRuleType gwNatRule = natRule.getGatewayNatRule();

                    logger.info("Original IP: " + gwNatRule.getOriginalIp());
                    logger.info("Original Port: " + gwNatRule.getOriginalPort());
                    logger.info("Translated IP: " + gwNatRule.getTranslatedIp());
                    logger.info("Translated Port: " + gwNatRule.getTranslatedPort());
                    logger.info("Protocol: " + gwNatRule.getProtocol());
                }
            }
        }
    }

    public String getUplinkInterfaceName() {
        final GatewayInterfaceType gwInterface = getUplinkInterface();
        if (gwInterface != null) {
            return gwInterface.getName();
        }
        return null;

    }

    public GatewayInterfaceType getUplinkInterface() {
        final GatewayConfigurationType gwType = edgeGateway.getResource().getConfiguration();
        final List<GatewayInterfaceType> gwInterfaces = gwType.getGatewayInterfaces().getGatewayInterface();
        for (final GatewayInterfaceType gwInterface : gwInterfaces) {
            if ("uplink".equalsIgnoreCase(gwInterface.getInterfaceType())) {
                return gwInterface;
            }
        }
        return null;
    }

    public void addNatRoutedOrgVdcNetwork(final String name, final String description, final String netmaskIP,
            final String gatewayIP, final String startIPRange, final String endIPRange, final FenceModeValuesType networkType) throws VCloudException {

        initialize();
        final OrgVdcNetworkType orgVdcNetworkParams = new OrgVdcNetworkType();
        orgVdcNetworkParams.setName(name);
        orgVdcNetworkParams.setDescription(description);

        // Configure Internal IP Settings
        final NetworkConfigurationType netConfig = new NetworkConfigurationType();
        netConfig.setRetainNetInfoAcrossDeployments(true);

        final IpScopeType ipScope = new IpScopeType();
        ipScope.setNetmask(netmaskIP);
        ipScope.setGateway(gatewayIP);
        ipScope.setIsEnabled(true);
        ipScope.setIsInherited(true);

        // IP Ranges
        final IpRangesType ipRangesType = new IpRangesType();
        final IpRangeType ipRangeType = new IpRangeType();
        ipRangeType.setStartAddress(startIPRange);
        ipRangeType.setEndAddress(endIPRange);

        ipRangesType.getIpRange().add(ipRangeType);

        ipScope.setIpRanges(ipRangesType);
        ipScope.setIsEnabled(true);
        final IpScopesType ipScopes = new IpScopesType();
        ipScopes.getIpScope().add(ipScope);
        netConfig.setIpScopes(ipScopes);
        netConfig.setFenceMode(networkType.value());

        orgVdcNetworkParams.setEdgeGateway(edgeGateway.getReference());
        orgVdcNetworkParams.setConfiguration(netConfig);
        logger.info("Creating Org vDC Network: type: " + networkType.value());
        try {
            final OrgVdcNetwork orgVdcNet = vdc.createOrgVdcNetwork(orgVdcNetworkParams);
            if (orgVdcNet.getTasks().size() > 0) {
                orgVdcNet.getTasks().get(0).waitForTask(0);
            }
            logger.info("Org vDC Network : " + orgVdcNet.getResource().getName() + " created - "
                    + orgVdcNet.getResource().getHref());
        } catch (final TimeoutException | MissingPropertyException e) {
            logger.info("FAILED: creating org vdc network - " + e.getLocalizedMessage());
        }
    }

    public NatServiceType getNATServiceType() throws VCloudException {
        initialize();
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

    private FirewallServiceType getFirewallServiceType() throws VCloudException {
        initialize();
        final GatewayFeaturesType gatewayFeaturesType = edgeGateway.getResource().getConfiguration()
                .getEdgeGatewayServiceConfiguration();
        final List<JAXBElement<? extends NetworkServiceType>> networkServices = gatewayFeaturesType.getNetworkService();
        for (final JAXBElement<? extends NetworkServiceType> ns : networkServices) {
            final NetworkServiceType networkServiceType = ns.getValue();
            if (networkServiceType.getClass() == FirewallServiceType.class) {
                return (FirewallServiceType) networkServiceType;
            }
        }
        return null;
    }

    public void redeploy() throws VCloudException {
        logger.info("Redeploying Edge Gateway: " + edgeGatewayName);
        final Task task = edgeGateway.redeploy();

        logger.info("Waiting for Redeploy to be done");
        try {
            task.waitForTask(0);
        } catch (final TimeoutException e) {
            throw new VCloudException("Timed out");
        }
        logger.info("Redeployed Edge Gateway: " + edgeGatewayName);
    }

    public void addDNATRule(final String description, final String originalIP, final String translatedIP)
            throws VCloudException {
        addDNATRule(description, originalIP, 0, translatedIP, 0);
    }

    public GatewayInterfaceType getUplinkInterface(final String uplinkName) {
        final GatewayConfigurationType gwType = edgeGateway.getResource().getConfiguration();
        final List<GatewayInterfaceType> gwInterfaces = gwType.getGatewayInterfaces().getGatewayInterface();
        for (final GatewayInterfaceType gwInterface : gwInterfaces) {
            if ("uplink".equalsIgnoreCase(gwInterface.getInterfaceType()) &&
                    gwInterface.getName().equalsIgnoreCase(uplinkName)) {
                return gwInterface;
            }
        }
        return null;
    }

    public void addDNATRule(final String description, final String originalIP, final int originalPort,
                            final String translatedIP, final int translatedPort) throws VCloudException {

        initialize();
        final GatewayInterfaceType externalNetwork = getUplinkInterface();
        final NatServiceType natServiceType = getNATServiceType();

        final NatRuleType natRuleType = new NatRuleType();
        natRuleType.setDescription(description);
        natRuleType.setRuleType("DNAT");
        final GatewayNatRuleType gwNatRuleType = new GatewayNatRuleType();
        gwNatRuleType.setOriginalIp(originalIP);
        if (originalPort > 0) {
            gwNatRuleType.setOriginalPort(String.valueOf(originalPort));
        } else {
            gwNatRuleType.setOriginalPort("any");
        }
        gwNatRuleType.setProtocol("any");
        gwNatRuleType.setTranslatedIp(translatedIP);
        if (translatedPort > 0) {
            gwNatRuleType.setProtocol("TCP");
            gwNatRuleType.setTranslatedPort(String.valueOf(translatedPort));
        } else {
            gwNatRuleType.setTranslatedPort("any");
        }
        gwNatRuleType.setInterface(externalNetwork.getNetwork());
        natRuleType.setGatewayNatRule(gwNatRuleType);
        natRuleType.setIsEnabled(true);

        natServiceType.getNatRule().add(natRuleType);

        final GatewayFeaturesType gatewayFeaturesType = edgeGateway.getResource().getConfiguration()
                .getEdgeGatewayServiceConfiguration();

        final Task task = edgeGateway.configureServices(gatewayFeaturesType);

        logger.info("Waiting for DNAT rule to be created");
        try {
            task.waitForTask(0);
        } catch (final TimeoutException e) {
            throw new VCloudException("Timed out");
        }
        logger.info("Added DNAT rule: " + description);
    }

    public void addDNATRule(final String description, final String originalIP, final int originalPort,
            final String translatedIP, final int translatedPort, String uplinkName) throws VCloudException {

        initialize();
        final GatewayInterfaceType externalNetwork = getUplinkInterface();
        final NatServiceType natServiceType = getNATServiceType();

        final NatRuleType natRuleType = new NatRuleType();
        natRuleType.setDescription(description);
        natRuleType.setRuleType("DNAT");
        final GatewayNatRuleType gwNatRuleType = new GatewayNatRuleType();
        gwNatRuleType.setOriginalIp(originalIP);
        if (originalPort > 0) {
            gwNatRuleType.setOriginalPort(String.valueOf(originalPort));
        } else {
            gwNatRuleType.setOriginalPort("any");
        }
        gwNatRuleType.setProtocol("any");
        gwNatRuleType.setTranslatedIp(translatedIP);
        if (translatedPort > 0) {
            gwNatRuleType.setProtocol("TCP");
            gwNatRuleType.setTranslatedPort(String.valueOf(translatedPort));
        } else {
            gwNatRuleType.setTranslatedPort("any");
        }
        GatewayInterfaceType uplink = getUplinkInterface(uplinkName);
        gwNatRuleType.setInterface(uplink.getNetwork());
        natRuleType.setGatewayNatRule(gwNatRuleType);
        natRuleType.setIsEnabled(true);

        natServiceType.getNatRule().add(natRuleType);

        final GatewayFeaturesType gatewayFeaturesType = edgeGateway.getResource().getConfiguration()
                .getEdgeGatewayServiceConfiguration();

        final Task task = edgeGateway.configureServices(gatewayFeaturesType);

        logger.info("Waiting for DNAT rule to be created");
        try {
            task.waitForTask(0);
        } catch (final TimeoutException e) {
            throw new VCloudException("Timed out");
        }
        logger.info("Added DNAT rule: " + description);
    }

    public void removeDNATRule(final String originalIP, final String translatedIP) throws VCloudException {

        initialize();
        getUplinkInterface();
        final NatServiceType natServiceType = getNATServiceType();

        NatRuleType natRuleToRemove = null;
        final List<NatRuleType> natRules = natServiceType.getNatRule();
        for (final NatRuleType natRule : natRules) {
            final GatewayNatRuleType gwNatRuleType = natRule.getGatewayNatRule();
            if (gwNatRuleType.getOriginalIp().equalsIgnoreCase(originalIP) && gwNatRuleType.getTranslatedIp().equalsIgnoreCase(translatedIP)) {
                natRuleToRemove = natRule;
                break;
            }
        }

        boolean flag = false;
        if (natRuleToRemove != null) {
            flag = natServiceType.getNatRule().remove(natRuleToRemove);
        }
        logger.info("FLAG: " + flag);
        if (!flag)
            return;

        final GatewayFeaturesType gatewayFeaturesType = edgeGateway.getResource().getConfiguration()
                .getEdgeGatewayServiceConfiguration();

        final Task task = edgeGateway.configureServices(gatewayFeaturesType);

        logger.info("Waiting for DNAT rule to be removed");
        try {
            task.waitForTask(0);
        } catch (final TimeoutException e) {
            throw new VCloudException("Timed out");
        }
        logger.info("Remove DNAT rule: " + originalIP + " " + translatedIP);
    }

    public void addSNATRule(final String description, final String originalIP, final String translatedIP)
            throws VCloudException {
        addSNATRule(description, originalIP, translatedIP, "any");
    }

    public void addSNATRule(final String description, final String originalIP, final String translatedIP, final String translatedPort)
            throws VCloudException {

        initialize();
        final GatewayInterfaceType externalNetwork = getUplinkInterface();
        final NatServiceType natServiceType = getNATServiceType();

        final NatRuleType natRuleType = new NatRuleType();
        natRuleType.setDescription(description);
        natRuleType.setRuleType("SNAT");
        final GatewayNatRuleType gwNatRuleType = new GatewayNatRuleType();
        gwNatRuleType.setOriginalIp(originalIP);
        gwNatRuleType.setOriginalPort("any");
        gwNatRuleType.setProtocol("TCP");
        gwNatRuleType.setTranslatedIp(translatedIP);
        gwNatRuleType.setTranslatedPort(translatedPort);
        gwNatRuleType.setInterface(externalNetwork.getNetwork());

        natRuleType.setGatewayNatRule(gwNatRuleType);
        natRuleType.setIsEnabled(true);

        natServiceType.getNatRule().add(natRuleType);

        final GatewayFeaturesType gatewayFeaturesType = edgeGateway.getResource().getConfiguration()
                .getEdgeGatewayServiceConfiguration();

        final Task task = edgeGateway.configureServices(gatewayFeaturesType);

        logger.info("Waiting for SNAT rule to be created");
        try {
            task.waitForTask(0);
        } catch (final TimeoutException e) {
            throw new VCloudException("Timed out");
        }
        logger.info("Added SNAT rule: " + description);
    }

    public void addFirewallRule(final String description) throws VCloudException {
        addFirewallRule(description, "Any", "Any", "Any", "Any");
    }

    public void addFirewallRule(final String description, final String sourceIP, final String sourcePort, final String destinationIp, final String destinationPort) throws VCloudException {

        initialize();
        final FirewallServiceType firewallServiceType = getFirewallServiceType();

        firewallServiceType.setIsEnabled(true);

        final FirewallRuleType fwRule = new FirewallRuleType();
        fwRule.setDescription(description);
        fwRule.setPolicy("allow");

        fwRule.setSourceIp(sourceIP);

        if (sourcePort.equalsIgnoreCase("Any")) {
            fwRule.setSourcePortRange(sourcePort);
        } else {
            fwRule.setSourcePort(Integer.valueOf(sourcePort));
        }

        fwRule.setDestinationIp(destinationIp);

        if (destinationPort.equalsIgnoreCase("Any")) {
            fwRule.setDestinationPortRange("Any");
        } else {
            fwRule.setPort(Integer.valueOf(destinationPort));
        }

        final FirewallRuleProtocols protocols = new FirewallRuleProtocols();
        //protocols.setAny(true);
        protocols.setTcp(true);
        fwRule.setProtocols(protocols);
        firewallServiceType.getFirewallRule().add(fwRule);

        final GatewayFeaturesType gatewayFeaturesType = edgeGateway.getResource().getConfiguration()
                .getEdgeGatewayServiceConfiguration();

        final Task task = edgeGateway.configureServices(gatewayFeaturesType);

        logger.info("Waiting for Firewall rule to be created");
        try {
            task.waitForTask(0);
        } catch (final TimeoutException e) {
            throw new VCloudException("Timed out");
        }
        logger.info("Added Firewall rule: " + description);
    }
}
