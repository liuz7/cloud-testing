package com.vmware.vchs.test.client.remote;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class SshUtil {
    private String charset = "UTF-8";
    private String user;
    private String passwd;
    private String host;
    private JSch jsch;
    private Session session;
    protected static final Logger LOGGER = LoggerFactory.getLogger(SshUtil.class);


    public static void main(String[] arg) throws Exception {
        SshUtil co = new SshUtil("root", "password", "10.156.75.230");
        co.connect();
        System.out.println(co.execCmd("date +%s"));
    }


    public SshUtil(String user, String passwd, String host) {
        this.user = user;
        this.passwd = passwd;
        this.host = host;
    }

    public void connect() throws JSchException {
        jsch = new JSch();
        session = jsch.getSession(user, host, 22);
        session.setPassword(passwd);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
        LOGGER.info("session connect successfully");
    }

    public String execCmd(String command) {
        return execCmd(command, true);
    }

    public String execCmd(String command, boolean closeSession) {
        BufferedReader reader = null;
        Channel channel = null;
        String output = "";
        try {
            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err, true);

            channel.connect();
            InputStream in = channel.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in,
                    Charset.forName(charset)));
            String buf = null;
            while ((buf = reader.readLine()) != null) {
                output += buf;
            }
            LOGGER.info("session output is "+output);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSchException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            channel.disconnect();
            if (closeSession == true) {
                session.disconnect();
                LOGGER.info("session disconnect successfully");
            }
        }
        return output;
    }

    public void disConnect() throws JSchException {
        session.disconnect();
        LOGGER.info("session disconnect successfully");
    }


}
