package com.vmware.vchs.test.client.remote;

import com.xebialabs.overthere.*;

import static com.xebialabs.overthere.ConnectionOptions.*;
import static com.xebialabs.overthere.OperatingSystemFamily.WINDOWS;
import static com.xebialabs.overthere.cifs.CifsConnectionBuilder.CONNECTION_TYPE;
import static com.xebialabs.overthere.cifs.CifsConnectionType.WINRM_INTERNAL;

/**
 * Created by georgeliu on 15/3/18.
 */
public class WinRMClient {

    private ConnectionOptions options;
    private OverthereConnection connection;
    private static final String USER_NAME = "Administrator";
    private static final String OS_PASSWORD = "password";

    public WinRMClient(String host) {
        this(host, USER_NAME, OS_PASSWORD);
    }

    public WinRMClient(String host, String username, String password) {
        this.options = new ConnectionOptions();
        options.set(ADDRESS, host);
        options.set(USERNAME, username);
        options.set(PASSWORD, password);
        options.set(OPERATING_SYSTEM, WINDOWS);
        options.set(CONNECTION_TYPE, WINRM_INTERNAL);
        this.connection = startConnection();
    }

    public OverthereConnection startConnection() {
        return startConnection(this.options);
    }

    public OverthereConnection startConnection(ConnectionOptions options) {
        OverthereConnection connection = Overthere.getConnection("cifs", options);
        return connection;
    }

    public ConnectionOptions getOptions() {
        return options;
    }

    public void setOptions(ConnectionOptions options) {
        this.options = options;
    }

    public OverthereConnection getConnection() {
        return connection;
    }

    public void setConnection(OverthereConnection connection) {
        this.connection = connection;
    }

    public OverthereProcess startProcess(String... args) throws Exception {
        OverthereProcess overthereProcess = this.connection.startProcess(CmdLine.build(args));
        return overthereProcess;
    }

    public int execute(String... args) throws Exception {
        return this.connection.execute(CmdLine.build(args));
    }
}
