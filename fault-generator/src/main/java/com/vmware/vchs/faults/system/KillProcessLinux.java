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
 * KillProcessLinux kills the process , if running on a linux box
 * Needs : IPAddress, UserName, Password, ProcessName, Instances (optional)
 * @author sshankar
 *
 */
public class KillProcessLinux implements Job {

	// Constant Logger
	private static final Logger log = Logger.getLogger(KillProcessLinux.class);

	CommonUtilLinux utility = new CommonUtilLinux();
	private Node newNode;
	private String processName;
	private int noOfInstances;
	private boolean reboot;  //TODO:In case process keeps rebooting, may need to be handled
	private Path targetScriptFilePath;

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
		else {
			log.debug("ProcessName is missing. Not sure what to kill ");
			Thread.currentThread().interrupt();
			return;

		}

		if (optionMap.get("Instances") != null)
			this.noOfInstances = Integer.parseInt(optionMap.get("Instances")
					.toString());

		this.newNode = new Node(ipAddress, userName, password);


	}


	/**
	 *
	 * @param processId
	 * @param executeCmdArray
	 * @return
	 * @throws Exception
	 */
	private static boolean killProcessIdCmd(int processId,
			ArrayList<String> listExecuteCmdArray) throws Exception {
		ArrayList<String> listSshResultArray = new ArrayList<String>();
		String processCmd = "kill -9 ";

		String actionCmd = processCmd + processId;
		log.debug("actionCmd :" + actionCmd);
		listExecuteCmdArray.add(actionCmd);
		SshpassClientConnection sshpassConn = new SshpassClientConnection();
		listSshResultArray = sshpassConn.executeProcess(listExecuteCmdArray);
		listExecuteCmdArray.remove(4);
		return true;

	}

	/*
	 * Kills the process for every process name provided
	 *
	 * @param - ssh as input, process name as input
	 *
	 * @param - Returns result
	 */
	private static boolean killProcessNameCmd(String processName,
			boolean forceOption, ArrayList<String> executeCmdArray)
					throws Exception {
		ArrayList<String> listSshResultArray;
		String processCmd = "pkill -KILL -f ";

		String actionCmd = processCmd + processName;
		executeCmdArray.add(actionCmd);
		for (int i = 0; i < executeCmdArray.size(); i++) {
			log.debug("Result of executeCmd : " + executeCmdArray.get(i));
		}
		SshpassClientConnection sshpassConn = new SshpassClientConnection();
		listSshResultArray = sshpassConn.executeProcess(executeCmdArray);
		for (int i = 0; i < listSshResultArray.size(); i++) {
			log.debug("Result of killProcessNameCmd : " + listSshResultArray.get(i));
		}
		return true;
	}



	/**
	 * Entry class for KillProcess
	 *
	 *
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

		log.debug("Kill Linux !!" + this.newNode.getIpAddress() + ","
				+ this.newNode.getUserName() + "," + this.newNode.getPassword() + ","
				+ this.processName + "," + this.noOfInstances);

		executeSshPassShellScript();
		this.utility.cleanUp (this.targetScriptFilePath);
	}

	/**
	 * Has the logic for executing the required command
	 *
	 */
	public void executeSshPassShellScript() {


		String executableScriptCommand = null;
		ArrayList<String> listExecuteCmdArray = new ArrayList<String>();
		listExecuteCmdArray.add(targetScriptFilePath.toString());
		listExecuteCmdArray.add(newNode.getIpAddress());
		listExecuteCmdArray.add(newNode.getUserName());
		listExecuteCmdArray.add(newNode.getPassword());
		boolean result = true;
		try {

			log.debug("ProcessName" + processName);
			ArrayList<String> processIdList = this.utility.getProcessID(processName,
					listExecuteCmdArray);
			if (processIdList.size() > 0) {

				if (noOfInstances != 0) {

					noOfInstances = processIdList.size() < noOfInstances ? processIdList
							.size() : noOfInstances;
							log.debug("No of instances to kill: " + noOfInstances);
							for (int i = 0; i < noOfInstances; i++) {
								result = killProcessIdCmd(
										Integer.parseInt(processIdList.get(i)),
										listExecuteCmdArray);
							}

				} else {
					result = killProcessNameCmd(processName, true,
							listExecuteCmdArray);

				}
			} else {
				log.info("There are no " + processName
						+ " processes currently running on "
						+ newNode.getIpAddress() + ". Nothing to kill!!");
			}
		} catch (Exception ex) {
			log.error("Error in killing the running process", ex);
		}
		listExecuteCmdArray.clear();

	}


}
