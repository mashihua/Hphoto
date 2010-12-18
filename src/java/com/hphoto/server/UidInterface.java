package com.hphoto.server;

import org.apache.hadoop.ipc.VersionedProtocol;


public interface UidInterface extends VersionedProtocol{
	
	 public static final long versionID = 1L; // initial version
	 
	 public long getUid();
	 
	 public String UUID();
	 
	 
	 
}
