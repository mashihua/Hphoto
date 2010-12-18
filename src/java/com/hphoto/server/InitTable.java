package com.hphoto.server;

import java.io.IOException;

import org.apache.hadoop.hbase.HBaseAdmin;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.log4j.Logger;

import com.hphoto.FConstants;

public class InitTable implements FConstants{
	private static final Logger LOG =
	    Logger.getLogger(InitTable.class.getName());
	
	static void init(HBaseAdmin admin) throws IOException{;
		HTableDescriptor desc;
		LOG.info(UESR_TABLE + " exist on table:" +admin.tableExists(UESR_TABLE));
		if(!admin.tableExists(UESR_TABLE)){
			desc = new HTableDescriptor(UESR_TABLE.toString());
			desc.addFamily(new HColumnDescriptor(
					USER_FAMILY,
					3,
					HColumnDescriptor.CompressionType.NONE,
					true,
					Integer.MAX_VALUE,
					null
					)
			);			
			admin.createTable(desc);
		}
		if(!admin.tableExists(IMAGE_TABLE)){
			desc = new HTableDescriptor(IMAGE_TABLE.toString());			
			desc.addFamily(
					new HColumnDescriptor(
							IMAGE_CATEGORY,
							3,
							HColumnDescriptor.CompressionType.NONE,
							true,
							Integer.MAX_VALUE,
							null
							)
			);
			desc.addFamily(new HColumnDescriptor(IMAGE_INFO.toString()));
			desc.addFamily(new HColumnDescriptor(IMAGE_TAGS.toString()));
			desc.addFamily(new HColumnDescriptor(IMAGE_EXIF.toString()));
			desc.addFamily(new HColumnDescriptor(IMAGE_COMMENT.toString()));
			desc.addFamily(new HColumnDescriptor(IMAGE_ALBUM.toString()));
			admin.createTable(desc);
		}
	}
}
