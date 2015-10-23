package com.vmware.vchs.test.client.rest;

import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class IdleConnectionMonitorThread extends Thread {

    private final List<PoolingHttpClientConnectionManager> syncConnMgrList;
    private final List<PoolingNHttpClientConnectionManager> asyncConnMgrList;
    private volatile boolean shutdown;
    private static IdleConnectionMonitorThread monitorThread = null;
    private IdleConnectionMonitorThread() {
        super();
        shutdown = false;
        syncConnMgrList = new ArrayList<>();
        asyncConnMgrList = new ArrayList<>();
        setDaemon(true);
    }

    public static IdleConnectionMonitorThread getIdleConnectionMonitorThread() {
        if(monitorThread != null)
            return monitorThread;
        else {
            //System.out.println("Start the idle monitor thread");
            monitorThread = new IdleConnectionMonitorThread ();
            monitorThread.start();
            return monitorThread;
        }
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    int size = syncConnMgrList.size();
                    for(int i=0; i<size; i++) {
                        //System.out.println("close expired or idle conn from sync conn manager");
                        syncConnMgrList.get(i).closeExpiredConnections();
                        syncConnMgrList.get(i).closeIdleConnections(15, TimeUnit.SECONDS);
                    }
                    size = asyncConnMgrList.size();
                    for(int i=0; i<size; i++) {
                        //System.out.println("close expired or idle conn from sync async conn manager");
                        asyncConnMgrList.get(i).closeExpiredConnections();
                        asyncConnMgrList.get(i).closeIdleConnections(15, TimeUnit.SECONDS);
                    }
                }
                try { Thread.sleep(4000); } catch(Exception e) {}
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void shutdown() {
        //System.out.println("shutdown idle monitor thread");
        shutdown = true;
        synchronized (this) {
            syncConnMgrList.clear();
            asyncConnMgrList.clear();
        }
    }

    public void addSyncConnMgr(PoolingHttpClientConnectionManager connMgr) {
        //System.out.println("add a connmgr into sync connmgr list");
        synchronized(this) { syncConnMgrList.add(connMgr); }
    }


    public void addAsyncConnMgr(PoolingNHttpClientConnectionManager connMgr) {
        //System.out.println("add a connmgr into async connmgr list");
        synchronized(this) { asyncConnMgrList.add(connMgr); }
    }
}