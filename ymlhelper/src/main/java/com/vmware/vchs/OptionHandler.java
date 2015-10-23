package com.vmware.vchs;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by liuda on 5/27/15.
 */
public class OptionHandler {
    public static enum Operation{
        GET,DELETE,PUT
    }
    protected Options options = new Options();
    protected CommandLine commandLine;
    private final static Logger logger = LoggerFactory.getLogger(YmlHelper.class);
    public List<Querable> queryList = new ArrayList<>();
    public String value;
    public String file="";
    public Operation operation;
    public void addOptions() {
        options.addOption("h", "help", false, "print help message");
        options.addOption("r", "read", true, "read value for given key");
        Option option = new Option("w", "write", true, "write value for given key, format like key=value");
        option.setArgs(2);
        options.addOption(option);
        option = new Option("a", "append", true, "append line for given key, format like key=line");
        option.setArgs(2);
        options.addOption(option);
        options.addOption("f", "file", true, "the location of the yml file to use");
        options.addOption("d", "delete", true, "delete key");
    }

    public CommandLine parseOptions(String[] args) {
        CommandLineParser parser = new PosixParser();
        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException e) {
            logger.info("Failed to parse arguments.");
            return null;
        }

        if (commandLine.hasOption("help")) {
            return null;
        }

        return commandLine;
    }

    public void parse(String[] args) throws Exception {
        addOptions();
        CommandLine commandLine = parseOptions(args);
        if (commandLine == null) {
            showHelp();
        } else {
            this.file = commandLine.getOptionValue("f");
            String queryString="";
            if(commandLine.hasOption("r")){
                queryString=commandLine.getOptionValue("r");
                this.operation= Operation.GET;
            }
            else if(commandLine.hasOption("w")){
                queryString=commandLine.getOptionValue("w");
                int index=queryString.lastIndexOf("||");
                this.value=queryString.substring(index+2);
                queryString=queryString.substring(0,index);
                this.operation= Operation.PUT;
            }
            else if(commandLine.hasOption("d")){
                queryString=commandLine.getOptionValue("d");
                this.operation= Operation.DELETE;
            }

            String[] queryStringList = queryString.split("/");
            for (int queryIndex = 0; queryIndex < queryStringList.length; queryIndex++) {
                Pattern keyValuePattern = Pattern.compile("\\[.*\\]");
                Matcher m = keyValuePattern.matcher(queryStringList[queryIndex]);
                if (m.find()) {
                    String matchString = m.group();
                    String key = queryStringList[queryIndex].substring(0, queryStringList[queryIndex].indexOf(matchString));
                    queryList.add(new KeyQuery(key));
                    if (matchString.indexOf("=") > 0||matchString.equalsIgnoreCase("[*]")) {
                        queryList.add(new AttributeQuery(matchString));
                    } else {
                        queryList.add(new IndexQuery(Integer.valueOf(matchString)));
                    }
                } else {
                    queryList.add(new KeyQuery(queryStringList[queryIndex]));
                }
            }
        }
        int a=0;
    }


    protected final void showHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Usage:", options);
    }
}
