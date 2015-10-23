package com.vmware.vchs.faults.system;

import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.vmware.vchs.common.CommonUtilLinux;
import com.vmware.vchs.common.Node;
import com.vmware.vchs.driver.SshpassClientConnection;

/**
 * This program is to burn the CPU specified for the core
 *
 * @author sshankar
 *
 */
public class BurnCPULinux implements Job {

	// Constant Logger
	private static final Logger log = Logger.getLogger(BurnCPULinux.class);

	CommonUtilLinux utility = new CommonUtilLinux();
	private Node newNode;
	private String processName;
	private int cpuNo;
	private int core;
	private int limitPercent;
	private Path targetScriptFilePath;
	private String executableName;

	/**
	 * Populate and validate all required parameters from JobDataMap
	 *
	 * @param optionMap
	 *
	 */
	public void init(JobDataMap optionMap) {
		String ipAddress = "";
		String userName = "";
		String password = "";

		if (optionMap.get("IPAddress") != null)
			ipAddress = optionMap.get("IPAddress").toString();
		else {
			log.debug("IP Address is missing. Cannot connect to host ");
			Thread.currentThread().interrupt();
			return;

		}
		if (optionMap.get("UserName") != null)
			userName = optionMap.get("UserName").toString();
		else {
			log.debug("Username is missing. Cannot connect to host ");
			Thread.currentThread().interrupt();
			return;

		}

		if (optionMap.get("Password") != null)
			password = optionMap.get("Password").toString();
		else {
			log.debug("Password is missing. Cannot connect to host ");
			Thread.currentThread().interrupt();
			return;

		}

		if (optionMap.get("ProcessName") != null)
			this.processName = optionMap.get("ProcessName").toString();

		if (optionMap.get("LimitPercent") != null)
			this.limitPercent = optionMap.getIntFromString("LimitPercent");

		if (optionMap.get("ExecutableName") != null)
			this.executableName = optionMap.get("ExecutableName").toString();

		this.newNode = new Node(ipAddress, userName, password);

	}

	/**
	 *
	 * This functions uses cat to load the CPU .If multiple instances are
	 * invoked, it splits the instances Alternatively can use dd if=/dev/zero
	 * of=/dev/null
	 *
	 * @param - returns the command to be executed
	 */
	private static String createSshCommandCat() {
		String processCmd = "cat /dev/zero > /dev/null &";
		return (processCmd);

	}

	/**
	 *
	 * This functions uses cpulimit to limit the cpu for specified processid
	 * invoked, including the children
	 *
	 * @param - returns the command to be executed
	 */
	private static String createSshCommandCPUlimit(String processId,
			int limitPercent) {
		String processCmd = "cpulimit -i --limit  " + limitPercent + "% "
				+ "--pid " + processId + " &";
		return (processCmd);

	}

	/**
	 *
	 * This functions uses cpulimit to limit the cpu for specified processid
	 * invoked, including the children
	 *
	 * @param - returns the command to be executed
	 */
	private static String createSshCommandCPUlimitExecutable(
			String executableName, int limitPercent) {
		String processCmd = "cpulimit -i --limit  " + limitPercent + "% "
				+ "--exe " + executableName + " &";
		return (processCmd);

	}

	/**
	 * Execute
	 */
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

		// Read and validate all parameters from JobDataMap
		JobDataMap jdMap = context.getJobDetail().getJobDataMap();
		this.init(jdMap);
		if (Thread.currentThread().interrupted())
			return;

		this.targetScriptFilePath = this.utility.prepareScriptForExecution();

		log.debug("Bur CPU Linux !!" + this.newNode.getIpAddress()
				+ this.newNode.getUserName() + this.newNode.getPassword()
				+ this.processName);

		executeSshPassShellScript();

		this.utility.cleanUp(this.targetScriptFilePath);
	}

	/**
	 * Has the logic for executing the required command
	 */
	public void executeSshPassShellScript() {

		String executableScriptCommand = null;

		ArrayList<String> listExecuteCmdArray = new ArrayList<String>();
		listExecuteCmdArray.add(targetScriptFilePath.toString());
		listExecuteCmdArray.add(newNode.getIpAddress());
		listExecuteCmdArray.add(newNode.getUserName());
		listExecuteCmdArray.add(newNode.getPassword());

		ArrayList<String> sshResultArray = new ArrayList<String>();

		// Create the ssh command to be executed for this job
		try {
			log.debug("ProcessName :" + this.processName);
			SshpassClientConnection sshpassConn = new SshpassClientConnection();

			if (this.processName != null) {

				if (limitPercent == 0)
					limitPercent = 50;
				ArrayList<String> processIdList = this.utility.getProcessID(
						this.processName, listExecuteCmdArray);

				// TODO: If multiple processes are running, do we need to set
				// limit on all?
				if (processIdList.size() > 0) {
					executableScriptCommand = createSshCommandCPUlimit(
							processIdList.get(0), this.limitPercent);
					listExecuteCmdArray.add(executableScriptCommand);
					log.info(executableScriptCommand);
					sshResultArray = sshpassConn
							.executeProcess(listExecuteCmdArray);
					listExecuteCmdArray.remove(4);
				} else {
					log.info("There are no processes running "
							+ this.processName
							+ " .So the cpu limit cannot be set.");

				}

			} else if (this.executableName != null) {
				if (limitPercent == 0)
					limitPercent = 50;
				log.debug(limitPercent);
				executableScriptCommand = createSshCommandCPUlimitExecutable(
						this.executableName, this.limitPercent);
				listExecuteCmdArray.add(executableScriptCommand);
				log.debug(executableScriptCommand);
				sshResultArray = sshpassConn
						.executeProcess(listExecuteCmdArray);
				listExecuteCmdArray.remove(4);
			} else {
				log.debug("CPU limit cannot be used, so using default command");

				executableScriptCommand = createSshCommandCat();
				listExecuteCmdArray.add(executableScriptCommand);
				log.debug(executableScriptCommand);
				sshResultArray = sshpassConn
						.executeProcess(listExecuteCmdArray);
				listExecuteCmdArray.remove(4);
			}

		} catch (Exception ex) {
			log.debug("Burn CPU could not be executed.");
		}
		listExecuteCmdArray.clear();
	}

}
