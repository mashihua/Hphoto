
package com.hphoto.admin;

import java.io.IOException;

import org.apache.hadoop.hbase.HBaseAdmin;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.io.Text;


public class AdminTable {	
	private HBaseAdmin client;
	
	public AdminTable(HBaseAdmin client){
		this.client = client;
	}
	
	public void createTable(Text table,String[] column) throws IOException{
		if(!this.client.tableExists(table)){
			HTableDescriptor desc = new HTableDescriptor(table.toString());
			for(int i = 0 ; i < column.length ; i++)
				desc.addFamily(new HColumnDescriptor(column[i]));
			this.client.createTable(desc);
		}
	}
	
	public void deleteTable(Text table) throws IOException{
		this.client.deleteTable(table);		
	}
	
	
	public void addColumn(Text table,String column) throws IOException{
		this.client.addColumn(table,new HColumnDescriptor(column));
	}
	
	
	public void deleteColumn(Text table,String column) throws IOException{
		this.client.deleteColumn(table,new Text(column));
	}
	
	public void enableTable(Text table) throws IOException{
		this.client.enableTable(table);
	}
	
	
	public void disableTable(Text table) throws IOException{
		this.client.disableTable(table);
	}
}
