package com.vmware.vchs.test.client.vcloud;

import com.google.common.collect.Lists;
import com.vmware.vcloud.api.rest.schema.ReferenceType;
import com.vmware.vcloud.sdk.*;
import com.vmware.vcloud.sdk.constants.Version;

import java.math.BigInteger;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by liuda on 15/4/3.
 */
public class VCloudClient {

    private final VcloudClient client;

    private enum Type {
        DISKSIZE(17);

        private int typeValue;

        Type(int typeValue) {
            this.typeValue = typeValue;
        }

        public int getTypeValue(){
            return this.typeValue;
        }
    }

    public VCloudClient(String baseUrl, Version version) throws SecurityException, NoSuchAlgorithmException, KeyManagementException, UnrecoverableKeyException, KeyStoreException{
        VcloudClient.setLogLevel(Level.FINEST);
        this.client = new VcloudClient(baseUrl, version);
        this.client.registerScheme("https", 443, FakeSSLSocketFactory.getInstance());
    }

    public void login(String username, String password) throws VCloudException {
        this.client.login(username, password);
    }

    private VM findVM(String orgName, String vAppName, String vmName)
            throws VCloudException {
        Organization org = Organization.getOrganizationByReference(client,
                client.getOrgRefsByName().get(orgName));

        for (ReferenceType vdcRef : org.getVdcRefs()) {
            Vdc vdc = Vdc.getVdcByReference(client, vdcRef);
            if (vdc.getVappRefsByName().containsKey(vAppName)) {
                Vapp vapp = Vapp.getVappByReference(client, vdc
                        .getVappRefByName(vAppName));
                for (VM vm : vapp.getChildrenVms()) {
                    if (vm.getResource().getName().equals(vmName)) {
                        System.out.println("VM Found: " + vmName + " - "
                                + vm.getReference().getHref());
                        return vm;
                    }
                }
            }
        }
        System.out.println("VM " + vmName + " not found");
        System.exit(0);
        return null;
    }

    public List<BigInteger> findDiskSize(String orgName, String vAppName, String vmName) throws VCloudException{
        VM vm = findVM(orgName, vAppName, vmName);
        List<VirtualDisk> disks = vm.getDisks();
        List<BigInteger> diskSizes = Lists.newArrayList();
        for(VirtualDisk disk : disks) {
            int resourceType = Integer.valueOf(disk.getItemResource().getResourceType().getValue());
            if(resourceType == Type.DISKSIZE.getTypeValue()) {
                diskSizes.add(disk.getHardDiskSize());
            }
        }
        return diskSizes;

    }
}
