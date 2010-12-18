package com.hphoto.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.RemoteExceptionHandler;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.ipc.Server;

import com.hphoto.server.UidInterface;
import com.hphoto.server.UidServer;

public class TestUidServer extends TestCase{
	
	private UidInterface uisServer;
	private  Configuration conf = new HBaseConfiguration();
	
	public TestUidServer() throws IOException{
		(new Thread(new UidServer(conf))).start();	
		 
		String address = conf.get("uidserver.address","0.0.0.0");
    	int port = conf.getInt("uidserver.port",40000);
    	InetSocketAddress scoket = new InetSocketAddress(address,port);
    		
    	try {
        	this.uisServer = (UidInterface) RPC.waitForProxy(
    				UidInterface.class, UidInterface.versionID,
    					scoket,
    		         conf);
        } catch (IOException e) {        		
        		if (e instanceof RemoteException) {
        	        e = RemoteExceptionHandler.decodeRemoteException((RemoteException) e);
        	      }
        	      throw e;
    	}
	}
	
	public void testGet(){
		System.out.println(uisServer.getUid());
	}
}
