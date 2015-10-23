package com.vmware.vchs.driver;

/**
 * Created by sshankar
 */
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import com.vmware.vchs.common.*;

import org.apache.log4j.Logger;

/**
 * Class to execute a sshPass command
 *
 * @author sshankar
 *
 */
public class SshpassClientConnection {
    public static Logger log = Logger.getLogger(SshpassClientConnection.class);

    public ArrayList<String> executeProcess(List<String> executeCmd) {
        ArrayList<String> resultList = new ArrayList<String>();

        try {
            log.debug("Executable command :" + executeCmd);
            ProcessBuilder sshProcessBuilder = new ProcessBuilder(executeCmd);

            Process process = sshProcessBuilder.start();
            InputStream input = process.getInputStream();

            InputStreamReader inputReader = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(inputReader);
            String sshOutput ;
            while ((sshOutput = br.readLine()) != null) {
                log.debug(sshOutput);
                resultList.add(sshOutput);
            }
            input = process.getErrorStream();
            inputReader = new InputStreamReader(input);
            br = new BufferedReader(inputReader);
            while ((sshOutput = br.readLine()) != null){
                log.debug(sshOutput);
                resultList.add (sshOutput);
            }
            int rc = process.waitFor();
            log.debug("Process exit value is :" + process.exitValue());
            return resultList;
	    } catch (IOException | InterruptedException ex) {
	        log.debug(ex.getMessage());
	    }
        return resultList;

    }

    /**
     * Disconnects the ssh object created arguments.
     *
     * TODO: Anything to close
     */
    public void disconnect() {

    }

}

