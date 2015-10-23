package com.vmware.vchs.common.utils;

import com.google.common.base.Throwables;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by liuda on 5/26/15.
 */
public class Utils {

    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static String getStackTrace(Throwable e) {
        String stackTrace = Throwables.getStackTraceAsString(e);
        e.printStackTrace();
        return stackTrace;
    }

    public static boolean checkHostAndPort(String host, String port) {
        try {
            int portNumber = Integer.parseInt(port);
            TelnetClient client = new TelnetClient();
            client.setDefaultTimeout(5000);
            client.connect(host, portNumber);
            client.disconnect();
            return true;
        } catch (Exception e) {
            logger.info(host + ":" + port + " not connected.");
        }
        return false;
    }
}
