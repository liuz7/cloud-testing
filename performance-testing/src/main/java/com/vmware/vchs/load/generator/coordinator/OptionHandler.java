package com.vmware.vchs.load.generator.coordinator;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static com.vmware.vchs.load.generator.common.Constants.*;
/**
 * Created by liuda on 5/27/15.
 */
public class OptionHandler {
    protected Options options = new Options();
    protected CommandLine commandLine;

    public int user;
//    public int client;
    public int interval;
    public int duration;
    public String tests = null;
//    public List<BaseTest> tests = new ArrayList<>();
    public List<String> ips = new ArrayList<>();
    public ClassLoader classLoader = this.getClass().getClassLoader();

    public void addOptions() {
        options.addOption("h", "help", false, "print help message");
        options.addOption("a", "ip", false, "ip address list for clients, delimited by " + TEST_DELIMITER);
        options.addOption("u", "user", true, "how many users run for a single client");
//        options.addOption("c", "client", true, "how many clients will be launched");
//        options.addOption("r", "request", true, "how many requests will be sent in one minute");
        options.addOption("i", "interval", true, "how many seconds will it wait bewteen 2 requests");
        options.addOption("t", "test", true, "what tests will be run, delimited by " + TEST_DELIMITER);
        options.addOption("d", "duration", true, "how long test will run in minutes");
    }

    public CommandLine parseOptions(String[] args) {
        CommandLineParser parser = new PosixParser();
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            System.err.println("Failed to parse arguments.");
            return null;
        }

        if (commandLine.hasOption("help")) {
            return null;
        }

        return commandLine;
    }

    public void process(String[] args) throws InstantiationException, IllegalAccessException {
        addOptions();
        if (parseOptions(args) == null) {
            showHelp();
        } else {
            processOptions();
        }
    }

    private void processOptions() throws InstantiationException, IllegalAccessException {
        user = getFromOptionsForValidValue(commandLine.getOptionValue("u"), DEFAULT_USER_NUMBER);
//        client = getFromOptionsForValidValue(commandLine.getOptionValue("c"), DEFAULT_CLIENT_NUMBER);
//        request = getFromOptionsForValidValue(commandLine.getOptionValue("r"), DEFAULT_REQUEST_NUMBER);
        interval = getFromOptionsForValidValue(commandLine.getOptionValue("i"), DEFAULT_INTERVAL);
        duration = getFromOptionsForValidValue(commandLine.getOptionValue("d"), DEFAULT_DURATION);
        this.tests = StringUtils.isNotEmpty(commandLine.getOptionValue("t"))?commandLine.getOptionValue("t"): DEFAULT_TEST;
        getIpsFromOptions(commandLine.getOptionValue("a"));
    }

    public void getIpsFromOptions(String optionValue) throws IllegalAccessException, InstantiationException {
        if (StringUtils.isNotEmpty(optionValue)) {
            ips= Arrays.asList(optionValue.split(TEST_DELIMITER));
        } else {
            ips.add("localhost");
        }
    }

//    public void getTestsFromOptions(String optionValue) throws IllegalAccessException, InstantiationException {
//        if (StringUtils.isNotEmpty(optionValue)) {
//            for (String test : optionValue.split(this.TEST_DELIMITER)) {
//                tests.add(((BaseTest) ReflectionUtils.forName(test, this.classLoader).newInstance()));
//            }
//        } else {
//            tests.add(new ProvisionTest());
//        }
//    }

    public int getFromOptionsForValidValue(String optionValue, int defaultValue) {
        if (StringUtils.isNotEmpty(optionValue) && Integer.valueOf(optionValue) > 0) {
            return Integer.valueOf(optionValue);
        }
        return defaultValue;
    }


    protected final void showHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Usage:", options);
    }
}
