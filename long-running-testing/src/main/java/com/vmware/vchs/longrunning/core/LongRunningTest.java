package com.vmware.vchs.longrunning.core;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by sjun on 8/26/15.
 */
public class LongRunningTest {
    public static final String LONG_RUNNING_DATA_DIR_PROPERTY = "longRunningDataDir";
    private static final String DEFAULT_LONG_RUNNING_DATA_DIR = "long_running_data";

    public static final String LONG_RUNNING_SLEEP_SECONDS_PROPERTY = "longRunningSleepSeconds";
    private static final String DEFAULT_LONG_RUNNING_SLEEP_SECONDS = "1";

    public static final String LONG_RUNNING_CONTINUE_PROPERTY = "longRunningContinue";
    private static final String DEFAULT_LONG_RUNNING_CONTINUE = "false";

    private String testFolder;
    private int sleepSeconds;
    private boolean continueRun;

    private LongRunningTest() {
        testFolder = System.getProperty(LONG_RUNNING_DATA_DIR_PROPERTY, DEFAULT_LONG_RUNNING_DATA_DIR);
        sleepSeconds = Integer.parseInt(System.getProperty(LONG_RUNNING_SLEEP_SECONDS_PROPERTY, DEFAULT_LONG_RUNNING_SLEEP_SECONDS));
        continueRun = Boolean.parseBoolean(System.getProperty(LONG_RUNNING_CONTINUE_PROPERTY, DEFAULT_LONG_RUNNING_CONTINUE));
    }

    private boolean deleteFile(File file) {
        if (file.isDirectory()) {
            for (File subfile: file.listFiles()) {
                boolean success = deleteFile(subfile);
                if (!success) {
                    return false;
                }
            }
        }

        return file.delete();
    }

    public void run() {
        File folder = new File(testFolder);
        if (!continueRun && folder.exists()) {
            if (!deleteFile(folder)) {
                System.err.println(String.format("Unable to clean %s.", folder.toString()));
                System.exit(1);
            }
        }
        folder.mkdirs();

        TestRunner runner = new TestRunner(new TestSuite(), testFolder);
        while(true) {
            if(runner.run()) {
                break;
            }

            try {
                TimeUnit.SECONDS.sleep(sleepSeconds);
            } catch (InterruptedException e) {
                break;
            }
        }
    }


    public static void main(String[] args) {
        new LongRunningTest().run();
    }
}
