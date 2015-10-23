package com.vmware.vchs.load;

import com.vmware.vchs.load.generator.service.LoadService;
import com.vmware.vchs.load.generator.service.LoadServiceImpl;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;



public class LoadServiceServer {

    public static void main(String[] args) {
        try {
            TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(29999);
            LoadService.Processor<LoadService.Iface> processor = new LoadService.Processor<LoadService.Iface>(new LoadServiceImpl());
            TNonblockingServer.Args argss = new TNonblockingServer.Args(serverTransport);
            argss.processor(processor);
            argss.protocolFactory(new TBinaryProtocol.Factory(true, true));
            TServer server = new TNonblockingServer(argss);
            System.out.println("Start load service server on port 29999...");

            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }

    }
}
