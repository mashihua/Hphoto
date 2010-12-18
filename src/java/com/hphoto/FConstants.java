
package com.hphoto;

import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.io.Text;

public interface FConstants extends HConstants {
	
	public static final long VERSION = 0x01;
	
	//uesr table
	static final Text UESR_TABLE = new Text("user--info");
	//image category table
	static final Text IMAGE_TABLE = new Text("image--info");
	//
	static final Text USER_FAMILY = new Text("user:");
	
	static final Text[] USER_FAMILYS= new Text[]{USER_FAMILY}; 
	
	static final Text IMAGE_CATEGORY = new Text("category:");
	
	static final Text IMAGE_INFO = new Text("info:");
	
	static final Text IMAGE_TAGS = new Text("tags:");
	
	static final Text IMAGE_EXIF = new Text("exif:");
	
	static final Text IMAGE_COMMENT = new Text("comment:");
	
	static final Text IMAGE_ALBUM = new Text("album:");
	
	static final Text IMAGE_CONFIG = new Text("config:");
	
	static final String UID_SERVER_ADDRESS ="hphoto.uidserver.address";
	
	static final String UID_SERVER_PORT = "hphoto.uidserver.port";
	
	static final int UID_SERVER_PORT_DEFAULT = 40000;
    
	static final String UNKNOW_TYPE = "unknow";
	
	static final String DEFAULT_USER_IMAGE = "/bigtable/hphoto/default/defaultuser.jpg";
	
	static final String DEFAULT_ALBUMIMAGE = "/bigtable/hphoto/default/UntitledAlbum.jpg";
}
