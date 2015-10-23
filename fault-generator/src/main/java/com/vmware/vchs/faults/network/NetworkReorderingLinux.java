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
import com.vmware.vchs.driver.SshpassClientConnection;

public class NetworkReorderingLinux implements Job {

	// Constant Logger
	private static final Logger log = Logger
			.getLogger(NetworkReorderingLinux.class);

	CommonUtilLinux utility = new CommonUtilLinux();
	private Node newNode;

	private String networkInterface;

	private int lossTime;

	private int approximatePercent;

	private int reorderPercent;

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
			log.debug("Network interface is missing. Defaults to all network interfaces ");

		if (optionMap.get("LossTime") != null)
			this.lossTime = Integer.parseInt(optionMap.get("LossTime")
					.toString());
		else {
			log.debug("Loss Time is missing. Defaults to a set percent ");
			this.lossTime = 10;
		}
		if (optionMap.get("ReorderPercent") != null)
			this.reorderPercent = Integer.parseInt(optionMap.get(
					"ReorderPercent").toString());
		else {
			log.debug("Reorder Percent is missing. Defaults to a set percent ");
			this.reorderPercent = 25;
		}
		if (optionMap.get("ApproximatePercent") != null)
			this.approximatePercent = Integer.parseInt(optionMap.get(
					"ApproximatePercent").toString());
		else {
			log.debug("ApproximatePercent Percent is missing. Defaults to a set percent ");
			this.approximatePercent = 50;
		}

		this.newNode = new Node(ipAddress, userName, password);

	}

	/*
	 * Generates the ssh commands to be executed
	 *
	 * @param - returns the command to be executed example: tc qdisc add dev
	 * eth0 root netem delay 10ms reorder 25% 50%
	 */
	private static String createSshCommand(String networkInterface,
			int lossTime, int approximatePercent, int reorderPercent) {
		log.info("Entered createLatencySshCommand");
		String processCmd = "tc qdisc add dev " + networkInterface
				+ " root netem delay " + lossTime + "ms" + " reorder"
				+ " " + reorderPercent + "% " + approximatePercent + "%";
		return (processCmd);
	}

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		log.debug("Entered execute of NetworkCorruptionLinux");
		// Read and validate all parameters from JobDataMap
		JobDataMap jdMap = context.getJobDetail().getJobDataMap();
		this.init(jdMap);
		if (Thread.currentThread().interrupted())
			return;

		this.targetScriptFilePath = this.utility.prepareScriptForExecution();

		log.debug("Network Reordee !!" + this.newNode.getIpAddress() + ","
				+ this.newNode.getUserName() + "," + this.newNode.getPassword()
				+ "," + this.networkInterface + "," + this.lossTime + ","
				+ this.reorderPercent + "," + this.approximatePercent);

		executeSshPassShellScript();
		this.utility.cleanUp(this.targetScriptFilePath);
	}

	/**
	 *
	 * @param newNode
	 * @param sourceScriptFilePath
	 * @param targetScriptFilePath
	 */
	public void executeSshPassShellScript() {

		String executableScriptCommand = null;

		ArrayList<String> listSshResultArray = new ArrayList<String>();
		ArrayList<String> listExecuteCmdArray = new ArrayList<String>();
		listExecuteCmdArray.add(targetScriptFilePath.toString());
		listExecuteCmdArray.add(newNode.getIpAddress());
		listExecuteCmdArray.add(newNode.getUserName());
		listExecuteCmdArray.add(newNode.getPassword());
		SshpassClientConnection sshpassConn = new SshpassClientConnection();

		try {
			if (this.networkInterface == null) {
				ArrayList<String> networkInterfaceList = utility
						.getNetworkInterfaces(listExecuteCmdArray);
				if (networkInterfaceList.size() > 0) {

					for (int i = 0; i < networkInterfaceList.size(); i++) {
						log.debug("Network interface: "
								+ networkInterfaceList.get(i));
						this.networkInterface = networkInterfaceList.get(i);
						executableScriptCommand = createSshCommand(
								this.networkInterface, this.lossTime,
								reorderPercent, this.approximatePercent);

						log.debug("Executable Script command :"
								+ executableScriptCommand);
						listExecuteCmdArray.add(executableScriptCommand);
						listSshResultArray = sshpassConn
								.executeProcess(listExecuteCmdArray);
						listExecuteCmdArray.remove(4);
					}
				}
			}

			else {
				executableScriptCommand = createSshCommand(
						this.networkInterface, this.lossTime,
						this.reorderPercent, this.approximatePercent);
				log.debug("Executable Script command :"
						+ executableScriptCommand);

				listExecuteCmdArray.add(executableScriptCommand);
				listSshResultArray = sshpassConn
						.executeProcess(listExecuteCmdArray);
				listExecuteCmdArray.remove(4);
			}
			for (int i = 0; i < listSshResultArray.size(); i++)
				log.debug("Result of the ssh command :"
						+ listSshResultArray.get(i));
		} catch (Exception ex) {
			log.error(
					"Error in corrupting the network interface for the host : ",
					ex);
		}
		listExecuteCmdArray.clear();

	}
}
