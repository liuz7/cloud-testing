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
 * KillProcessWindows is used to generate all commands required for identifying
 * the process and killing it It also executes the kill. This is not reversible
 * Needs : IPAddress, UserName, Password, ProcessName, Instances (optional)
 * @author sshankar
 *
 */
public class KillProcessWindows implements Job {
	public static Logger log = Logger.getLogger(KillProcessWindows.class);
	CommonUtilLinux utility = new CommonUtilLinux();

	private Node newNode;
	private String processName;
	private int noOfInstances;
	private boolean reboot;  //Incase process keeps rebooting, may need to be handled
	private Path targetScriptFilePath;

	// force option
	boolean forceOption = true;

	// child processes
	boolean childProcesses = true;

	String serviceName = "";

	/**
	 * Gets the process id for the process given
	 * example : TASKLIST /nh /fo TABLE /FI 'IMAGENAME eq notepad.exe'|
	 */
	public String listProcessesIdCmd(String processName) {
		log.debug("Entered listProcessesIdCmd");
		String processCmd = "TASKLIST /fo TABLE /nh /fi \'imagename eq ";
		String cutPID = "\'| awk '{print $2}' ";
		String getCommandPid = processCmd + processName + cutPID;
		log.debug("Script is " + getCommandPid);
		return (getCommandPid);
	}

	/**
	 * killProcessNameCmd generates the command to kill with imagename
	 *
	 * @param processName
	 * @param forceOption
	 * @return
	 * example : TASKLIST /nh /fo TABLE /FI 'IMAGENAME eq notepad.exe'
	 */
	public String killProcessNameCmd(String processName, boolean forceOption) {
		log.debug("Entered createScriptProcessName");
		String processCmd = null;
		if (forceOption)
			processCmd = "TASKKILL /T /F /IM ";
		else
			processCmd = "TASKKILL /T /IM ";
		processCmd = processCmd + processName;
		log.debug("killProcessNameCmd:" + processCmd);

		return (processCmd);
	}

	/**
	 * killProcessCmd generates the kill command with pid values obtained for
	 * the process
	 *
	 * @param pidValue
	 * @param childProcess
	 * @param processIdList
	 * @return
	 */
	public String killProcessIdCmd(String pidValue, boolean childProcess,
			ArrayList<String> processIdList) {
		log.debug("Entered killProcessIdCmd");
		String processCmd = null;
		if (childProcess)
			processCmd = "TASKKILL /F /T ";
		else
			processCmd = "TASKKILL /F ";

		for (int i = 0; i < noOfInstances; i++) {
			processCmd = processCmd + " /PID " + processIdList.get(i);
		}
		log.debug("killProcessIdCmd: " + processCmd);

		return (processCmd);

	}

	public String listProcessServicesCmd(String processName, String serviceName) {
		String processCmd = "TASKLIST /fo TABLE /nh /fi \'imagename eq ";
		String serviceCmd = "\' /svc | grep ";
		String cutPID = "| awk '{print $2}' ";
		String getCommandPid = processCmd + processName + serviceCmd
				+ serviceName + cutPID;
		log.debug("Script is " + getCommandPid);

		return (processCmd);

	}


	/**
	 * Populate and validate all required parameters from JobDataMap
	 *
	 * @param optionMap
	 * Input parameters are IPAddress, UserName, Password, ProcessName, Instances
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

		if (optionMap.get("ServiceName") != null)
			this.serviceName = optionMap.get("ServiceName").toString();

		this.newNode = new Node(ipAddress, userName, password);

	}



	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		log.debug("Entered execute of KillProcessWindows");

		// Read and validate all parameters from JobDataMap
		JobDataMap jdMap = context.getJobDetail().getJobDataMap();
		this.init(jdMap);
		if (Thread.currentThread().interrupted())
			return;

		this.targetScriptFilePath = this.utility.prepareScriptForExecution();

		log.debug("Kill Windows !!" + this.newNode.getIpAddress() + ","
				+ this.newNode.getUserName() + "," +  this.newNode.getPassword() + ","
				+ this.processName + "," +  this.noOfInstances);

		executeSshPassShellScript();
		this.utility.cleanUp (this.targetScriptFilePath);

	}

	/**
	 * The class is the wrapper for actually executing the killing based on a
	 * processName 1. Creates a ssh script file from the base golden script file
	 * and makes it executable if not 2. Creates the required TASKLIST command
	 * based on values (Process name, service name, no of instances) and will
	 * return the process ids 3. Based on the process id returned, the processes
	 * are killed (child process option and force option)
	 *
	 * @param newNode
	 * @param sourceScriptFilePath
	 * @param targetScriptFilePath
	 */
	public void executeSshPassShellScript() {
		String executableScriptCommand = null;


		ArrayList<String> listExecuteCmdArray = new ArrayList<String>();
		listExecuteCmdArray.add(targetScriptFilePath.toString());
		listExecuteCmdArray.add(newNode.getIpAddress());
		listExecuteCmdArray.add(newNode.getUserName());
		listExecuteCmdArray.add(newNode.getPassword());
		ArrayList<String> sshResultArray = new ArrayList<String>();
		SshpassClientConnection sshpassConn = new SshpassClientConnection();

		try {

			log.debug("ProcessName" + processName);
			if (this.serviceName.isEmpty()) {
				executableScriptCommand = listProcessesIdCmd(processName);
			} else {
				executableScriptCommand = listProcessServicesCmd(processName,
						serviceName);

			}

			listExecuteCmdArray.add(executableScriptCommand);
			sshResultArray = sshpassConn.executeProcess(listExecuteCmdArray);
			listExecuteCmdArray.remove(4);
			sshResultArray.remove(0); // The first row returned from TASKLIST is
			// empty

			if (sshResultArray.size() > 0) {

				if (noOfInstances != 0) {

					noOfInstances = sshResultArray.size() < noOfInstances ? sshResultArray
							.size() : noOfInstances;
							executableScriptCommand = killProcessIdCmd(processName,
									true, sshResultArray);
							sshResultArray = sshpassConn
									.executeProcess(listExecuteCmdArray);
				}

				else {
					executableScriptCommand = killProcessNameCmd(processName,
							true);
					listExecuteCmdArray.add(executableScriptCommand);
					sshResultArray = sshpassConn
							.executeProcess(listExecuteCmdArray);
					listExecuteCmdArray.remove(4);
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