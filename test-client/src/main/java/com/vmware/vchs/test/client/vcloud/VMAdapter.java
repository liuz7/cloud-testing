package com.vmware.vchs.test.client.vcloud;

import com.vmware.vcloud.sdk.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeoutException;

/**
 * Created by georgeliu on 15/10/12.
 */
public class VMAdapter {

    private String vmName;
    private String vAppName;
    private volatile VM vm;
    private VcloudClient client;
    private Vdc vdc;
    protected static final Logger logger = LoggerFactory.getLogger(VMAdapter.class);

    public VMAdapter(String vAppName, String vmName, VcloudClient client, Vdc vdc) {
        this.vmName = vmName;
        this.vAppName = vAppName;
        this.client = client;
        this.vdc = vdc;
    }

    public void reboot() throws VCloudException {
        VM vm = getVM();
        Task task = vm.reboot();
        waitForTaskToComplete(task);
    }

    private VM getVM() throws VCloudException {
        if (vdc.getVappRefsByName().containsKey(vAppName)) {
            Vapp vapp = Vapp.getVappByReference(client, vdc.getVappRefByName(vAppName));
            for (VM vm : vapp.getChildrenVms()) {
                if (vm.getResource().getName().equals(vmName)) {
                    logger.info("Reboot VM: " + vmName + " - " + vm.getReference().getHref());
                    this.vm = vm;
                }
            }
        }
        return this.vm;
    }

    private void waitForTaskToComplete(Task task) throws VCloudException {
        logger.info("Waiting for task");
        try {
            task.waitForTask(0);
        } catch (final TimeoutException e) {
            throw new VCloudException("Timed out");
        }
        logger.info("Task done");
    }
}
