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

public class KillProcessEsxVM implements Job {

	// Constant Logger
	private static final Logger log = Logger.getLogger(KillProcessEsxVM.class);
	CommonUtilLinux utility = new CommonUtilLinux();
	private Node newNode;

	private String vmName;

	private int noOfInstances;


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

		if (optionMap.get("VMName") != null)
			this.vmName = optionMap.get("VMName").toString();
		else {
			log.debug("VMName is missing. ");
			Thread.currentThread().interrupt();
			return;

		}

		if (optionMap.get("Instances") != null)
			this.noOfInstances = Integer.parseInt(optionMap.get("Instances")
					.toString());

		this.newNode = new Node(ipAddress, userName, password);


	}


	/*
	 * Check if the service is running and return the PID array list for the
	 * process name
	 *
	 * @param - ssh handle to the connection
	 *
	 * @param - process name as input
	 *
	 * @param - Returns the array list of the process id
	 */
	public ArrayList<String> getVMID(String processName,
			ArrayList<String> listExecuteCmdArray) throws Exception {
		log.info("Entered  get ProcessID");
		ArrayList<String> listProcessId = new ArrayList<String>();

		String executableScriptCommand = createSshCommand(processName);
		listExecuteCmdArray.add(executableScriptCommand);
		log.debug("Executable Script Command : " + executableScriptCommand);
		SshpassClientConnection sshpassConn = new SshpassClientConnection();
		listProcessId = sshpassConn.executeProcess(listExecuteCmdArray);
		listExecuteCmdArray.remove(4);
		if (listProcessId.size() > 0) {
			for (int i = 0; i < listProcessId.size(); i++)
				log.info("Index" + i + listProcessId.get(i));
		}
		return listProcessId;
	}

	/**
	 * Creates the ssh command to get the vm id for a given VM name
	 * vim-cmd vmsvc/getallvms | grep -F 'sshankar-centOS'|awk '{print $1}'
	 * @param vmName
	 * @return
	 */
	private static String createSshCommand(String vmName) {
		log.info("Entered createSshCommand");
		String processCmd = "vim-cmd vmsvc/getallvms |grep -i ";
		String cutPID = "| awk '{print $1}'";

		return (processCmd + vmName + cutPID);

	}

	/**
	 *
	 * @param processId
	 * @param executeCmdArray
	 * @return
	 * @throws Exception
	 */
	private static void shutdownVMIdCmd(int vmId,
			ArrayList<String> listExecuteCmdArray) throws Exception {
		log.debug("Entered shutdown VMs");
		ArrayList<String> listSshResult = new ArrayList<String>();
		String processCmd = "vim-cmd vmsvc/power.shutdown ";

		String actionCmd = processCmd + vmId;
		log.debug("actionCmd :" + actionCmd);
		listExecuteCmdArray.add(actionCmd);
		SshpassClientConnection sshpassConn = new SshpassClientConnection();
		listSshResult = sshpassConn.executeProcess(listExecuteCmdArray);
		listExecuteCmdArray.remove(4);
		for (int i=0; i < listSshResult.size() ; i++)
			log.debug("Result from trying to shutdown vm" + listSshResult.get(i));

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
		log.debug("Entered execute of KillProcessLinux");


		// Read and validate all parameters from JobDataMap
		JobDataMap jdMap = context.getJobDetail().getJobDataMap();
		this.init(jdMap);
		if (Thread.currentThread().interrupted())
			return;

		this.targetScriptFilePath = this.utility.prepareScriptForExecution();

		log.debug("Shutdown ESX VM !!" + this.newNode.getIpAddress() + ","
				+ this.newNode.getUserName() + "," + this.newNode.getPassword() + ","
				+ this.vmName + "," + this.noOfInstances);

		executeSshPassShellScript();
		this.utility.cleanUp (this.targetScriptFilePath);

	}

	public void executeSshPassShellScript() {

		String executableScriptCommand = null;


		ArrayList<String> listExecuteCmdArray = new ArrayList<String>();
		listExecuteCmdArray.add(targetScriptFilePath.toString());
		listExecuteCmdArray.add(newNode.getIpAddress());
		listExecuteCmdArray.add(newNode.getUserName());
		listExecuteCmdArray.add(newNode.getPassword());
		SshpassClientConnection sshpassConn = new SshpassClientConnection();


		try {

			log.debug("vmName" + vmName);
			ArrayList<String> listVmId = getVMID(vmName,
					listExecuteCmdArray);
			log.debug("There are " + listVmId.size() + "vms matching vmName");

			if (listVmId.size() > 0) {
				this.noOfInstances = listVmId.size() < this.noOfInstances ? listVmId
						.size() : this.noOfInstances;
						log.debug("No of instances to kill: " + noOfInstances);
						if (this.noOfInstances != 0) {
							for (int i=0; i< this.noOfInstances; i++){
								shutdownVMIdCmd(
										Integer.parseInt(listVmId.get(i)),
										listExecuteCmdArray);
							}
						}
						else {
							log.debug("There is something wrong if you reached here !!");
						}
			} else {
				log.info("There are no vms matching "+ vmName + "currently running on "
						+ newNode.getIpAddress() + ". Nothing to shutdown !!");
			}
		} catch (Exception ex) {
			log.error("Error in killing the running process", ex);
		}

	}


}
