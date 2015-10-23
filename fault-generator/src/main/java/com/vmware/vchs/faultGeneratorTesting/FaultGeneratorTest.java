package com.vmware.vchs.faultGeneratorTesting;

/**
 * Created by sshankar
 */
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

public class FaultGeneratorTest {
	public static Logger log = Logger.getLogger(FaultGeneratorTest.class);


	public static void main(String args[]) throws SchedulerException  {

		FaultGeneratorTest test = new FaultGeneratorTest();

		String presentWorkingDirectory = System.getProperty("user.home");
		System.out.println ("Present " + presentWorkingDirectory);


		Path sourceScriptFilePath = Paths
				.get(System.getProperty("user.dir") + "resources" +  "sshpassBaseScript.sh");
		System.out.println("ssh file is at" + sourceScriptFilePath );

		//URI uri1 = (test.getClass().getClassLoader().getResource("sshpassBaseScript.sh")).toURI();
		//System.out.println ("URL is " + uri1);
		//File f= new File (uri1);

		//System.out.println("File is "+ f);


		try {

			log.debug("Fault Generator started ....");

			// Getting the scheduler instance from the factory
			SchedulerFactory factory = new StdSchedulerFactory(
					"quartz.properties");

			Scheduler scheduler = factory.getScheduler();

			// Start scheduler
			scheduler.start();

			//Thread.sleep(1900000);

			//scheduler.shutdown();

			//} catch (InterruptedException e) {
			//e.printStackTrace();

		} catch (SchedulerException e) {
			e.printStackTrace();
		}

	}

}
