package com.vmware.vchs.misc;

import com.vmware.vchs.InstanceBaseTest;
import com.vmware.vchs.base.DbaasInstance;
import com.vmware.vchs.test.client.remote.WinRMClient;
import com.xebialabs.overthere.OverthereProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by georgeliu on 15/3/17.
 */
@Test(groups = {"kms"})
public class KMSTest extends InstanceBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(KMSTest.class);

    @Test
    public void testActivationAfterProvision() throws Exception {
        DbaasInstance instance =builder.createInstanceWithRetry(getNamePrefix());
        String host = instance.getInstanceResponse().getIpAddress();
        assertThat(isActivated(host)).isTrue();
    }

    private boolean isActivated(String host) throws Exception {
        boolean result = false;
        WinRMClient winRMClient = new WinRMClient(host);
        BufferedReader stdOutReader = null;
        BufferedReader stdErrorReader = null;
        OverthereProcess overthereProcess = null;
        try {
            overthereProcess = winRMClient.startProcess("cscript", "c:\\windows\\system32\\slmgr.vbs", "-dlv");
            String line;
            stdOutReader = new BufferedReader(new InputStreamReader(overthereProcess.getStdout()));
            while ((line = stdOutReader.readLine()) != null) {
                if (line.contains("Licensed")) {
                    result = true;
                    logger.info(line);
                    break;
                }
            }
            stdErrorReader = new BufferedReader(new InputStreamReader(overthereProcess.getStderr()));
            while ((line = stdErrorReader.readLine()) != null) {
                logger.info(line);
                result = false;
            }
            try {
                this.threadTask.runWithTimeout(new ProcessTask(overthereProcess), 10, TimeUnit.SECONDS);
            } catch (TimeoutException e) {
                throw e;
            }
            if (overthereProcess.exitValue() != 0) {
                result = false;
            }
        } finally {
            overthereProcess.destroy();
            winRMClient.getConnection().close();
            stdOutReader.close();
            stdErrorReader.close();
        }
        return result;
    }

    public class ProcessTask implements Callable<Integer> {

        private OverthereProcess overthereProcess;

        public ProcessTask(OverthereProcess overthereProcess) {
            this.overthereProcess = overthereProcess;
        }

        @Override
        public Integer call() throws Exception {
            int result;
            try {
                result = this.overthereProcess.waitFor();
            } catch (Exception e) {
                throw e;
            }
            return Integer.valueOf(result);
        }
    }
}
