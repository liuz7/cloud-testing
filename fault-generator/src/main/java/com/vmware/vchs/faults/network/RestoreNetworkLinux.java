package com.vmware.vchs.faults.network;

import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.vmware.vchs.common.CommonUtilLinux;
import com.vmware.vchs.common.Node;

public class RestoreNetworkLinux implements Job {
	// Constant Logger
	private static final Logger log = Logger
			.getLogger(RestoreNetworkLinux.class);

	CommonUtilLinux utility = new CommonUtilLinux();
	private Node newNode;

	private String networkInterface;

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

		if (optionMap.get("NetworkInterface") != null)
			this.networkInterface = optionMap.get("NetworkInterface")
			.toString();
		else
			log.debug("Network interface is missing. Default to set on all network interfaces ");


		this.newNode = new Node(ipAddress, userName, password);

	}

	/**
	 * param - Returns the Result of the execution Checks for the no of
	 * instances of the process name provided and lower of the one provided Vs
	 * running is executed
	 *
	 * TODO: If processname is empty, handle it
	 */

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		log.debug("Entered execute of RestoreNetworkLinux");

		// Read and validate all parameters from JobDataMap
		JobDataMap jdMap = context.getJobDetail().getJobDataMap();
		this.init(jdMap);
		if (Thread.currentThread().interrupted())
			return;

		this.targetScriptFilePath = this.utility.prepareScriptForExecution();

		log.debug("Restore Network Interface !!" + this.newNode.getIpAddress()
				+ "," + this.newNode.getUserName() + ","
				+ this.newNode.getPassword() + "," + this.networkInterface);

		executeSshPassShellScript();
		this.utility.cleanUp(this.targetScriptFilePath);
	}

	public void executeSshPassShellScript() {

		ArrayList<String> listExecuteCmdArray = new ArrayList<String>();
		listExecuteCmdArray.add(targetScriptFilePath.toString());
		listExecuteCmdArray.add(newNode.getIpAddress());
		listExecuteCmdArray.add(newNode.getUserName());
		listExecuteCmdArray.add(newNode.getPassword());

		try {

			utility.restoreNetworkInterfaces(this.networkInterface,
					listExecuteCmdArray);

		} catch (Exception ex) {
			log.error(
					"Error in restoring the network interface for the host : ",
					ex);
		}
		listExecuteCmdArray.clear();
	}

}