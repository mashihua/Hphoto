package com.hphoto.server;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HRegionServer;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.util.StringUtils;

import com.hphoto.FConstants;

public class UidServer implements UidInterface,Runnable,FConstants {
	
	

	static final Log LOG = LogFactory.getLog(UidServer.class);
	private AtomicLong atom;
	private Server server;
	private Configuration conf;
	private boolean stopRequest = false; 
	private long sleeptime;
	
	public UidServer(Configuration conf) throws IOException{
		this(conf,Long.MAX_VALUE>>6);
	}
	
	public UidServer(Configuration conf,long value) throws IOException{
		this.atom = new AtomicLong(value);
		this.conf = conf;
	    try {
	        // Server to handle client requests
	    	String address = conf.get(UID_SERVER_ADDRESS,"0.0.0.0");
	    	int port = conf.getInt(UID_SERVER_PORT,UID_SERVER_PORT_DEFAULT);
	    	sleeptime = conf.getInt("uidserver.sleeptime", 500);
	        this.server = RPC.getServer(this, address, 
	        		port, conf.getInt("uidserver.handler.count", 10),
	          false, conf);
	    }catch (IOException e) {
	    	throw e;
	    }
	}
		
	public void run(){
		try {
		      this.server.start();
		      LOG.info("UidServer started at: " + (conf.get("uidserver.address","0.0.0.0") + ":" + conf.getInt("uidserver.port",70000)).toString());
		    } catch(IOException e) {		  
		   }
		    while(!stopRequest) {
		    	try {
					Thread.sleep(sleeptime);
				} catch (InterruptedException e) {
				}
		    }
	}
		
	public String UUID() {
		return UUID.randomUUID().toString();
	}

	public long getUid() {
		return this.atom.incrementAndGet();
	}

	public long getProtocolVersion(final String protocol, @SuppressWarnings("unused") final long clientVersion) throws IOException {
		if (protocol.equals(UidInterface.class.getName())) {
		      return UidInterface.versionID;
		 }
		 throw new IOException("Unknown protocol to name node: " + protocol);
	}
	
	private static void printUsageAndExit() {
		    printUsageAndExit(null);
		  }
	private static void printUsageAndExit(final String message) {
		    if (message != null) {
		      System.err.println(message);
		    }
		    System.err.println("Usage: java " +
		        "com.flashget.server.UidServer [--bind=hostname:port] start");
		    System.exit(0);
	}
	
	
	public static void main(String[] args){
		if (args.length < 1) {
		      printUsageAndExit();
		    }
		    
		    Configuration conf = new HBaseConfiguration();
		    
		    // Process command-line args. TODO: Better cmd-line processing
		    // (but hopefully something not as painful as cli options).
		    final String addressArgKey = "--bind=";
		    
		    for (String cmd: args) {
		        if (cmd.startsWith(addressArgKey)) {
		          String[] address = cmd.substring(addressArgKey.length()).split(":");
		          if(address.length < 2){
		        	  break;
		          }
		          conf.set("uidserver.address",address[0]);
		          conf.set("uidserver.port",address[1]);
		          continue;
		        }
		        
		        if (cmd.equals("start")) {
		          try {
		            (new Thread(new HRegionServer(conf))).start();
		          } catch (Throwable t) {
		            LOG.error( "Can not start region server because "+
		                StringUtils.stringifyException(t) );
		            System.exit(-1);
		          }
		          break;
		        }
		        
		        if (cmd.equals("stop")) {
		          printUsageAndExit("There is no regionserver stop mechanism. To stop " +
		            "regionservers, shutdown the hbase master");
		        }
		        
		        // Print out usage if we get to here.
		        printUsageAndExit();
		      }
		    // Print out usage if we get to here.
	        printUsageAndExit();
	}

}
