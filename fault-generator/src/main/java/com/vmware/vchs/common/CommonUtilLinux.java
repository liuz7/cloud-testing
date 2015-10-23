package com.vmware.vchs.common;

import static java.nio.file.StandardCopyOption.COPY_ATTRIBUTES;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.vmware.vchs.driver.SshpassClientConnection;
/**
 * Created by sshankar
 */
public class CommonUtilLinux {


	// Constant Logger
	private static final Logger log = Logger.getLogger(CommonUtilLinux.class);

	//TODO: Make it configurable from config file
	Path sourceScriptFilePath = Paths
			.get(System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator+ "sshpassBaseScript.sh");

	//TODO : Do not use tmp in project directory (George's review)
	public Path targetDirectory = Paths.get(System.getProperty("user.dir") + File.separator + "tmp" + File.separator);


	/**
	 * Create a target file in the path from the source ssh file and make it executable
	 * @param className
	 * @return
	 */
	/*
        public Path prepareScript (String className){
        	Path targetDirectory = null;
            String targetFileExt = new SimpleDateFormat("yyyy-MM-ddhhmmss'.sh'").format(new Date());
            String targetScriptFileName = className + targetFileExt;
            Path targetScriptFilePath = null;
            try{
                Files.createDirectories(targetDirectory);
                targetScriptFilePath = Paths
                        .get(targetDirectory + File.separator + targetScriptFileName);
                createSshScriptFileFromBase(sourceScriptFilePath,
                        targetScriptFilePath);
                if (!(Files.isExecutable(targetScriptFilePath)))
                    createExecutableFile(targetScriptFilePath);
            }
            catch (IOException ex){
                log.debug(ex.getStackTrace());;
            }
            return targetScriptFilePath;
        }
	 */

	public Path prepareScriptForExecution (){
		String targetScriptFileName = sourceScriptFilePath.getFileName()
				.toString();
		Path targetScriptFilePath = Paths
				.get(targetDirectory + File.separator + targetScriptFileName);
		try{
			Files.createDirectories(targetDirectory);

			createSshScriptFileFromBase(sourceScriptFilePath,
					targetScriptFilePath);

			if (!(Files.isExecutable(targetScriptFilePath)))
				createExecutableFile(targetScriptFileName);
		} catch (IOException ex){
			log.error(ex.toString());

		}
		return targetScriptFilePath;
	}

	public void cleanUp(Path targetScriptFilePath) {
		try{
			Files.deleteIfExists(targetScriptFilePath);
		} catch (NoSuchFileException ex) {
			log.error("No such file :" +  targetScriptFilePath);

		} catch (IOException ex) {
			// File permission problems are caught here.
			log.error("Could not remove the file ");
		}

	}

	/*
	 * Get the network interfaces that are up using command line
	 *
	 * @param - input the ssh as handle
	 *
	 * @param - Returns the array list of network interfaces that are up
	 */
	public ArrayList<String> getNetworkInterfaces(
			ArrayList<String> listExecuteCmdArray) throws Exception {
		log.debug("getNetworkInterfaces");
		ArrayList<String> interfaceList = new ArrayList<String>();
		String actionCmd = "ifconfig -a | sed 's/[ \\t].*//;/^\\(lo\\|\\)$/d'";
		log.debug("actionCmd" + actionCmd);

		listExecuteCmdArray.add(actionCmd);
		log.debug("Executable Script Command : " + listExecuteCmdArray);
		SshpassClientConnection sshpassConn = new SshpassClientConnection();
		interfaceList = sshpassConn.executeProcess(listExecuteCmdArray);
		listExecuteCmdArray.remove(4);
		if (interfaceList.size() > 0) {
			for (int i = 0; i < interfaceList.size(); i++)
				log.debug("Index" + i + interfaceList.get(i));
		} else {
			log.debug("There are no network interfaces available on this host");

		}

		return interfaceList;
	}


	/**
	 * This function returns an ssh command to restore network set on the device
	 * @param networkInterface
	 * @return command string
	 * example: tc qdisc del dev <networkInterface>  root
	 */
	private static String restoreNetworkSshCommand(String networkInterface) {
		log.debug("Entered restoreCommand");
		String processCmd = "tc qdisc del dev " + networkInterface + " root";
		return (processCmd);
	}

	/**
	 *
	 * @param networkInterface
	 * @param executeCmdArray
	 * @throws Exception
	 */
	public void restoreNetworkInterfaces(String networkInterface,
			ArrayList<String> listExecuteCmdArray) throws Exception {
		log.debug("Entered restoreNetworkInterfaces ");

		SshpassClientConnection sshpassConn = new SshpassClientConnection();
		ArrayList<String> listSshResultArray = new ArrayList<String>();
		ArrayList<String> networkInterfaceList = new ArrayList<String>();

		if (networkInterface != null && networkInterface.length() != 0)
			networkInterfaceList.add(networkInterface);
		else

			networkInterfaceList = getNetworkInterfaces(listExecuteCmdArray);
		log.debug("Size of network interfaces :" + networkInterfaceList.size());
		for (int i = 0; i < networkInterfaceList.size(); i++) {
			networkInterface = networkInterfaceList.get(i);
			String executableScriptCommand = restoreNetworkSshCommand(networkInterface);
			log.debug("Executable Script command :" + executableScriptCommand);

			listExecuteCmdArray.add(executableScriptCommand);
			listSshResultArray = sshpassConn.executeProcess(listExecuteCmdArray);
			listExecuteCmdArray.remove(4);

		}

	}
	/**
	 * Verify if a file provided is a directory
	 * @param fileDirectoryName
	 * @return
	 */
	public boolean validateDirectoryFile(String fileDirectoryName) {
		if ((new File(fileDirectoryName)).isDirectory())
			return true;
		else
			return false;

	}

	/**
	 * copyScriptFiles copy the source file to destination with the sour file
	 * attributes and -replacing the target file if it exists
	 *
	 * @param source
	 * @param dest
	 * @throws IOException
	 */

	public void createSshScriptFileFromBase(Path scriptBaseFilePath,
			Path executeScriptFilePath) {
		try {
			if (!Files.exists(scriptBaseFilePath)) {
				log.debug("Source script file could not be found :"
						+ scriptBaseFilePath);
			}

			if (Files.isRegularFile(scriptBaseFilePath)
					&& Files.isReadable(scriptBaseFilePath)) {
				copyScriptFiles(scriptBaseFilePath, executeScriptFilePath);
			}
		} catch (NoSuchFileException ex) {
			log.error("Source file does not exist. Retry after fixing it"
					+ scriptBaseFilePath + ex.toString());
		} catch (IOException ex) {
			log.error(ex.toString());
		}

	}

	/**
	 * copyScriptFiles copy the source file to destination with the source file
	 * attributes and -replacing the target file if it exists
	 *
	 * @param source
	 * @param dest
	 * @throws IOException
	 */
	public void copyScriptFiles(Path source, Path dest) throws IOException {
		CopyOption[] fileOptions = new CopyOption[] { COPY_ATTRIBUTES,
				REPLACE_EXISTING };

		try {
			Files.copy(source, dest, fileOptions);
		} catch (IOException ex) {
			log.error("Files could not be copied " + ex.toString());
		}

	}

	/**
	 * Helper function to make the file executable
	 *
	 * @param targetScriptFilePath
	 */
	/*public void createExecutableFile(Path targetScriptFilePath) {
            ArrayList<String> listExecuteCmdArray = new ArrayList<String>();
            listExecuteCmdArray.add("`chmod a+x toMakeExecutableFileName`");
            SshpassClientConnection sshpassConn = new SshpassClientConnection();
            sshpassConn.executeProcess(listExecuteCmdArray);
            log.debug("createExecutableFile: " + targetScriptFilePath);

        }*/

	/**
	 * Helper function to make the file executable
	 *
	 * @param toMakeExecutableFileName
	 */
	public void createExecutableFile(String toMakeExecutableFileName) {
		ArrayList<String> executeCmdArray = new ArrayList<String>();
		executeCmdArray.add("`chmod a+x toMakeExecutableFileName`");
		SshpassClientConnection sshpassConn = new SshpassClientConnection();
		sshpassConn.executeProcess(executeCmdArray);
		log.debug("createExecutableFile: " + toMakeExecutableFileName);

	}

	/**
	 * Check if the service is running and return the PID array list for the
	 * process name
	 *
	 * @param - ssh handle to the connection
	 * @param - process name as input
	 * @param - Returns the array list of the process id
	 *
	 */
	public ArrayList<String> getProcessID(String processName,
			ArrayList<String> listExecuteCmdArray) {
		log.debug("Entered  get ProcessID");
		ArrayList<String> processIdList = new ArrayList<String>();

		String executableScriptCommand = createSshCommand(processName);
		listExecuteCmdArray.add(executableScriptCommand);
		log.debug("Executable Script Command : " + executableScriptCommand);
		SshpassClientConnection sshpassConn = new SshpassClientConnection();
		processIdList = sshpassConn.executeProcess(listExecuteCmdArray);
		listExecuteCmdArray.remove(4);
		if (processIdList.size() > 0) {
			for (int i = 0; i < processIdList.size(); i++)
				log.debug("Index" + i + processIdList.get(i));
		} else {

		}
		return processIdList;
	}

	/**
	 * Creates the ssh command to get the processid given the process name
	 *
	 * @param processName
	 * @return
	 */
	private static String createSshCommand(String processName) {
		log.debug("Entered createSshCommand");
		String processCmd = "ps aux |grep ";
		String removeGrep = "|grep -v grep";
		String cutPID = "| awk '{print $2}'";

		return (processCmd + processName + removeGrep + cutPID);

	}

}

