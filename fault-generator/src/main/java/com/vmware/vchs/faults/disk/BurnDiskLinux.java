package com.vmware.vchs.faults.disk;

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


/*
 * Class : BurnDisk - Fills the disk selected or default with a particular bytesize and block count
 */
public class BurnDiskLinux implements Job {

	//Constant Logger
	private static final Logger log = Logger.getLogger(BurnDiskLinux.class);

	CommonUtilLinux utility = new CommonUtilLinux();
	private Node newNode;

	private String diskName = "burn";


	private String byteSize = "1M";


	private String  blockCount = "65536";

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

		if (optionMap.get("DiskName") != null)
			this.diskName = optionMap.get("DiskName").toString();
		else {
			log.debug("Default disk will be used");

		}

		if (optionMap.get("ByteSize") != null)
			this.byteSize = optionMap.get("ByteSize").toString();
		else {
			log.debug("Default byte size will be used");

		}

		if (optionMap.get("BlockCount") != null)
			this.blockCount = optionMap.get("BlockCount").toString();
		else {
			log.debug("Default blockcount will be used");

		}

		this.newNode = new Node(ipAddress, userName, password);


	}


	/*
	 * Generates the ssh commands to be executed
	 * @param - returns the command to be executed
	 */
	private static String createSshCommand(String diskName,String byteSize, String blockCount) {
		log.info("Entered createSshCommand");

		String processCmd = "dd if=/dev/urandom of=" + diskName + " bs=" + byteSize + " count=" + blockCount + "&";
		return (processCmd);

	}

	/**
	 * Entry class for Burn disk
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

		log.debug("Burn Disk Linux !!" + this.newNode.getIpAddress() + ","
				+ this.newNode.getUserName() + "," + this.newNode.getPassword() + ","
				+ this.diskName );

		executeSshPassShellScript();
		this.utility.cleanUp (this.targetScriptFilePath);
	}

	/**
	 * Has the logic for executing the required command
	 *
	 */
	public void executeSshPassShellScript() {

		String executableScriptCommand = null;
		ArrayList<String> sshResultArray;
		ArrayList<String> listExecuteCmdArray = new ArrayList<String>();
		listExecuteCmdArray.add(targetScriptFilePath.toString());
		listExecuteCmdArray.add(newNode.getIpAddress());
		listExecuteCmdArray.add(newNode.getUserName());
		listExecuteCmdArray.add(newNode.getPassword());
		String actionCmd = createSshCommand(diskName, byteSize, blockCount );
		log.debug("actionCmd :" + actionCmd);
		listExecuteCmdArray.add(actionCmd);
		SshpassClientConnection sshpassConn = new SshpassClientConnection();
		sshResultArray = sshpassConn.executeProcess(listExecuteCmdArray);
		listExecuteCmdArray.remove(4);
		listExecuteCmdArray.clear();

	}



}
