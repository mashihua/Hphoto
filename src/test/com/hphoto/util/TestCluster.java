package com.hphoto.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.dfs.MiniDFSCluster;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.MiniHBaseCluster;

public class TestCluster extends TestCase{

	MiniDFSCluster cluster;
	FileSystem fs;
	String content = "we are com.flashget.util.TestCluster";
	
	Path path;
	public TestCluster() throws IOException{
		super();
		Configuration conf = new HBaseConfiguration();
		cluster = new MiniDFSCluster(conf,2,true,null);
		this.fs = FileSystem.get(conf);
	}
	
	public void testWriteAndRead() throws IOException{		
		path = new Path(("/bigtable/test/test.txt").replace(' ','+'));		
		FSDataOutputStream fout = fs.create(path);
		fout.write(content.getBytes());
		fout.close();
		FSDataInputStream in = fs.open(path);
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		byte[] b = new byte[1];
		while(in.read(b)!=-1){
			bo.write(b);
		}
		System.out.println(new String(bo.toByteArray()));
	}
	
	
	public void testRead() throws IOException, InterruptedException{
		Thread.sleep(20000);
		System.out.println("in read method:");
		System.out.println(fs.equals(path));
		FSDataInputStream in = fs.open(path);
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		byte[] b = new byte[1];
		while(in.read(b)!=-1){
			bo.write(b);
		}
		System.out.println(new String(b));
	}
}
