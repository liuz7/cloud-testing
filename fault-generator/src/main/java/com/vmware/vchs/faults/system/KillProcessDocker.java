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

public class KillProcessDocker implements Job {

	// Constant Logger
	private static final Logger log = Logger.getLogger(KillProcessDocker.class);

	CommonUtilLinux utility = new CommonUtilLinux();
	private Node newNode;
	private String processName;
	private int noOfInstances;
	private boolean reboot; // TODO:In case process keeps rebooting, may need to
	// be handled
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
	 * Creates the ssh command to get the processid given the process name
	 *
	 * @param processName
	 * @return the command string to be executed example: docker images | grep
	 *         'servicecontroller'|grep -v grep| awk '{print $3}'
	 */
	private static String getImageIdCommand(String processName) {
		log.info("Entered getImageIdCommand");
		String processCmd = "docker images | grep '";
		String removeGrep = "'|grep -v grep";
		String cutPID = "| awk '{print $3}'";

		return (processCmd + processName + removeGrep + cutPID);

	}

	/**
	 * Creates the ssh command to get the processid given the process name
	 *
	 * @param processName
	 * @return the command string to be executed example: sudo docker ps
	 *         --filter 'status=running'|awk '{print $1}'
	 */
	private static String getRunningContainerCommand(String processName) {
		log.info("Entered createSshCommand");
		String processCmd = "docker ps --filter 'status=running' | grep '"
				+ processName + "'|awk '{print $1}'";

		return processCmd;

	}

	/**
	 * Creates the ssh command to get the processid given the process name
	 *
	 * @param processName
	 * @return the command string to be executed example: sudo docker ps
	 *         --filter 'status=running'|awk '{print $1}'
	 */
	private static boolean isMatchingContainerId(String imageId,
			String containerId, String processName,
			ArrayList<String> executeCmdArray) {
		ArrayList<String> listResult = new ArrayList<String>();
		SshpassClientConnection sshpassConn = new SshpassClientConnection();
		log.info("Entered getMatchingContainerId");
		String executableScriptCommand = "docker inspect -f '{{.Config.Image}}' "
				+ containerId;

		executeCmdArray.add(executableScriptCommand);
		log.debug("Executable Script Command : " + executableScriptCommand);
		listResult = sshpassConn.executeProcess(executeCmdArray);
		executeCmdArray.remove(4);
		if (listResult.size() > 0) {
			for (int i = 0; i < listResult.size(); i++) {
				if ((listResult.get(i).equalsIgnoreCase(imageId))
						|| (listResult.get(i).equalsIgnoreCase(processName))) {
					log.debug("The imageId " + imageId
							+ " and the container id " + containerId + " match");
				}
				return true;
			}
		}

		return false;

	}

	/**
	 *
	 * Get the image id using docker images on the name of the process For each
	 * of the imageid, docker inspect to get container id Using the container
	 * id, stop the container Using container TODO:
	 */

	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		log.debug("Entered execute of Stop Docker Process");

		// Read and validate all parameters from JobDataMap
		JobDataMap jdMap = context.getJobDetail().getJobDataMap();
		this.init(jdMap);
		if (Thread.currentThread().interrupted())
			return;

		this.targetScriptFilePath = this.utility.prepareScriptForExecution();

		log.debug("Kill Docker Process !!" + this.newNode.getIpAddress() + ","
				+ this.newNode.getUserName() + "," + this.newNode.getPassword()
				+ "," + this.processName + "," + this.noOfInstances);

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

		ArrayList<String> listExecuteCmdArray = new ArrayList<String>();
		listExecuteCmdArray.add(targetScriptFilePath.toString());
		listExecuteCmdArray.add(newNode.getIpAddress());
		listExecuteCmdArray.add(newNode.getUserName());
		listExecuteCmdArray.add(newNode.getPassword());
		SshpassClientConnection sshpassConn = new SshpassClientConnection();

		try {

			log.debug("ProcessName" + processName);
			ArrayList<String> listImageId = new ArrayList<String>();
			ArrayList<String> listRunningProcesses = new ArrayList<String>();

			executableScriptCommand = getImageIdCommand(this.processName);
			listExecuteCmdArray.add(executableScriptCommand);
			log.debug("Executable Script Command : " + executableScriptCommand);
			listImageId = sshpassConn.executeProcess(listExecuteCmdArray);
			listExecuteCmdArray.remove(4);

			if (listImageId.size() > 0) {
				executableScriptCommand = getRunningContainerCommand(this.processName);
				listExecuteCmdArray.add(executableScriptCommand);
				log.debug("Executable Script Command : "
						+ executableScriptCommand);
				listRunningProcesses = sshpassConn
						.executeProcess(listExecuteCmdArray);
				listExecuteCmdArray.remove(4);
				if (listRunningProcesses.size() > 0) {

					this.noOfInstances = listRunningProcesses.size() < this.noOfInstances ? listRunningProcesses
							.size() : this.noOfInstances;
							log.debug("No of instances to kill: " + this.noOfInstances);
							if (noOfInstances != 0) {
								for (int i = 0; i < listImageId.size(); i++) {
									for (int j = 0; j < this.noOfInstances; j++) {
										if (isMatchingContainerId(listImageId.get(i),
												listRunningProcesses.get(j),
												this.processName, listExecuteCmdArray))

											listExecuteCmdArray.add("docker stop "
													+ listRunningProcesses.get(j));
										log.debug("Executable Script Command : "
												+ executableScriptCommand);
										ArrayList<String> sshResult = sshpassConn
												.executeProcess(listExecuteCmdArray);
										listExecuteCmdArray.remove(4);

									}

								}
							} else {
								log.info("There are no " + processName
										+ " processes currently running on "
										+ newNode.getIpAddress()
										+ ". Nothing to kill!!");
							}
				}
			}
		} catch (Exception ex) {
			log.error("There are no images on this host ", ex);
		}

	}

}
