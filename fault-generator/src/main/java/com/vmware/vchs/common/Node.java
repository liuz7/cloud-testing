package com.vmware.vchs.common;

/**
 * Created by sshankar 
 */
public class Node {
    /**
     * This is the ipaddress of the machine to connect
     * to.
     */
    private String ipAddress;
    /**
     * This is the hostname of the machine to connect
     * to. This is an optional parameter and by default
     * is ...
     */
    private String hostName;
    /**
     * This is the username of the machine to conect to.
     * If not provided will default to root
     */
    private String userName;
    /**
     * This is the password required for the machine to connect to
     * If not provided will default to ca$hc0w
     */
    private String password;
    /**
     * Constructor with IPAddress
     *
     * @param newIpAddress
     *
     */
    public Node(String newIpAddress) {
        this.ipAddress = newIpAddress;
        this.userName = "root";
        this.password = "ca$hcow";
    }
    /**
     * Constructor with IPAddress, username and password
     *
     * @param newIpAddress
     * @param userName
     * @param password
     *
     */
    public Node(String newIpAddress, String newUserName, String newPassword) {
        this.ipAddress = newIpAddress;
        this.userName = newUserName;
        this.password = newPassword;

    }
    /**
     * Constructor with optional arguments.
     *
     * @param newHostName
     * @param newUserName
     * @param newPassword
     */
    public Node(String newIpAddress,String newHostName,
                String newUserName,
                String newPassword) {
        this.ipAddress = newIpAddress;
        this.hostName = newHostName;
        this.userName = newUserName;
        this.password = newPassword;
    }
    /**
     * setter and getter methods for the node
     */
    public void setIpAddress(String newIpAddress){
        this.ipAddress = newIpAddress;
    }
    public String getIpAddress(){
        return this.ipAddress;
    }
    public void setHostName(String newHostName){
        this.hostName = newHostName;
    }
    public String getHostName(){
        return this.hostName;
    }
    public void setUserName(String newUserName){
        this.hostName = newUserName;
    }
    public String getUserName(){
        return this.userName;
    }
    public void setPassword(String newPassword){
        this.password = newPassword;
    }
    public String getPassword(){
        return this.password;
    }
}


