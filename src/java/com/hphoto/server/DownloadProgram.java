package com.hphoto.server;


import java.io.DataOutputStream;

import java.io.FileInputStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hphoto.util.LibraryUtil;



public class DownloadProgram extends HttpServlet{

	private static final long serialVersionUID = 1L;
	private static String path = System.getProperty("java.io.tmpdir");
	 private static String file = "hphoto.zip";
	 private String windowsCmd = "hphoto.bat";
	 private String linuxCmd = "hphoto";
	 private static String readme = "readme.txt";
	 private String jar = "hphoto.jar";
	 public void doGet(HttpServletRequest request,
	            HttpServletResponse response
	            ) throws IOException{
		   response.setHeader("Content-Disposition", "attachment; filename=\"" +  file + "\"");
		   response.setContentType("application/octet-stream");
		   OutputStream out = response.getOutputStream();
		   ZipOutputStream zipos=new ZipOutputStream(out);
		   zipos.putNextEntry(new ZipEntry(jar));
		   InputStream in = new FileInputStream(URLDecoder.decode(DownloadProgram.class.getProtectionDomain().getCodeSource().getLocation().getFile()));
		   write(in,zipos);
		   zipos.closeEntry();		   
		   zipos.putNextEntry(new ZipEntry(windowsCmd));
		   in = LibraryUtil.getJarFile(windowsCmd);
		   write(in,zipos);
		   zipos.closeEntry();
		   zipos.putNextEntry(new ZipEntry(linuxCmd));
		   in = LibraryUtil.getJarFile(linuxCmd);
		   write(in,zipos);
		   zipos.closeEntry();
		   zipos.putNextEntry(new ZipEntry(readme));
		   in = LibraryUtil.getJarFile(readme);
		   write(in,zipos);
		   zipos.closeEntry();
		   zipos.close();
		   in.close();		  
		   out.close();
		}
	 
	 private static void write(InputStream in,OutputStream out) throws IOException{		
		 if(in == null)
			 return;
         byte[] bytes = new byte[1024 * 10];
         for (int n = 0; n != -1; n = in.read(bytes)) {
             out.write(bytes, 0, n);
         } 
         out.flush();
         in.close();
	 }
	 public void doPost(HttpServletRequest request,
	            HttpServletResponse response
     ) throws IOException{
		 doGet(request,response);
	 }
	 
	 public static void main(String[] arg) throws IOException{
		   OutputStream out = new FileOutputStream(file);
		   ZipOutputStream zipos=new ZipOutputStream(out);
		   zipos.putNextEntry(new ZipEntry(readme));
		   //InputStream in = new FileInputStream(URLDecoder.decode(DownloadProgram.class.getProtectionDomain().getCodeSource().getLocation().getFile()));
		   InputStream in = new FileInputStream(readme); 
		   if(in!=null){
			   write(in,zipos);
		   }else{
			   System.out.println("not input file");
		   }
		   zipos.closeEntry();
		   zipos.close();
	 }
}
