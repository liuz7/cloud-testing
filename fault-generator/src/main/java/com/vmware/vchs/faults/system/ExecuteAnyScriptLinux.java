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
 * This class takes in any  script entered and executes on the target host
 * Needs : IPAddress, UserName, Password, Script
 * @author sshankar
 *
 */
public class ExecuteAnyScriptLinux implements Job {

	// Constant Logger
	private static final Logger log = Logger.getLogger(ExecuteAnyScriptLinux.class);
	CommonUtilLinux utility = new CommonUtilLinux();



	private Node newNode;
	private String executableScriptCommand;

	private Path targetScriptFilePath;



	/**
	 * Entry function from quartz scheduler
	 *
	 */

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		log.debug("Entered execute of ExecuteAnyScriptLinux");


		// Read and validate all parameters from JobDataMap
		JobDataMap jdMap = context.getJobDetail().getJobDataMap();
		this.init(jdMap);

		log.debug("Execute any script !!" + this.newNode.getIpAddress() + ","
				+ this.newNode.getUserName() + "," + this.newNode.getPassword()
				+ "," + this.executableScriptCommand);

		this.targetScriptFilePath = this.utility.prepareScriptForExecution();
		executeSshPassShellScript();
		this.utility.cleanUp (this.targetScriptFilePath);
	}

	public void executeSshPassShellScript() {

		ArrayList<String> resultList = new ArrayList<String>();
		ArrayList<String> listExecuteCmdArray = new ArrayList<String>();
		listExecuteCmdArray.add(targetScriptFilePath.toString());
		listExecuteCmdArray.add(newNode.getIpAddress());
		listExecuteCmdArray.add(newNode.getUserName());
		listExecuteCmdArray.add(newNode.getPassword());
		listExecuteCmdArray.add(this.executableScriptCommand);
		SshpassClientConnection sshpassConn = new SshpassClientConnection();

		resultList = sshpassConn.executeProcess(listExecuteCmdArray);
		listExecuteCmdArray.remove(4);
		if (resultList.size() > 0) {
			for (int i = 0; i < resultList.size(); i++)
				log.info("Index " + i + resultList.get(i));
		}

		listExecuteCmdArray.clear();
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

		if (optionMap.get("Script") != null)
			this.executableScriptCommand = optionMap.get("Script").toString();
		else {
			log.debug("Password is missing. Cannot connect to host ");
			Thread.currentThread().interrupt();
			return;
		}
		this.newNode = new Node(ipAddress, userName, password);

	}



}
