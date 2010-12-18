package com.hphoto.util;

import java.io.*;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.jar.*;




public class LibraryUtil {
	
	 private static String libraryPath = System.getProperty("java.io.tmpdir");
	 
	 public LibraryUtil(){}
	 
	 public LibraryUtil(String path){		 
		 this.libraryPath = path;
	 }
	 
	 public void extractFile(String path) throws IOException{	
		 //get the curent jarFile and extract the embedded file that given
		 JarFile jarFile = new JarFile(URLDecoder.decode(LibraryUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile()));
		 Enumeration<JarEntry> entries = jarFile.entries();
		 while(entries.hasMoreElements()){			 
			 JarEntry entry = entries.nextElement();
			 //find the file math the given path
			 if(entry.getName().startsWith(path)){
				 //if is a directory,make dir
				 if(entry.isDirectory()){
					 new File(libraryPath,entry.getName()).mkdir();
					 continue;
				 }
				 //save the math file to the current directory
				 try {
					 write(new DataInputStream(jarFile.getInputStream(entry)),new DataOutputStream(
			    			  new BufferedOutputStream(
			    	               new FileOutputStream(new File(libraryPath,entry.getName()).getAbsolutePath())
			    	               )
			    			  )
					 );
				
				 }catch(IOException e){
				     e.printStackTrace();
			      }	
			 }

		 }
		 
	 }	 
	 
	 
	 public static InputStream getJarFile(String path) throws IOException{	
		 //get the curent jarFile and extract the embedded file that given
		 JarFile jarFile = new JarFile(URLDecoder.decode(LibraryUtil.class.getProtectionDomain().getCodeSource().getLocation().getFile()));
		 Enumeration<JarEntry> entries = jarFile.entries();
		 while(entries.hasMoreElements()){			 
			 JarEntry entry = entries.nextElement();
			 //find the file math the given path
			 if(entry.getName().startsWith(path)){
				 //if is a directory,make dir
				 if(entry.isDirectory()){
					 new File(libraryPath,entry.getName()).mkdir();
					 continue;
				 }
				 return jarFile.getInputStream(entry);
			 }

		 }
		 return null;
		 
	 }	
	 
	 private void write(DataInputStream in,DataOutputStream out) throws IOException{		 
	          byte[] bytes = new byte[1024 * 10];
	          for (int n = 0; n != -1; n = in.read(bytes)) {
	              out.write(bytes, 0, n);
	          }
	          out.close();
	          in.close();
	 }
	 
	 public void loadLibrary(final String name) throws IOException {
	      // store the DLL in the temporary directory for the System
		 
	      String suffix = ".so";
	      String os = System.getProperty("os.name").toLowerCase();
	      
	      if(os.indexOf("windows") != -1){
	    	  suffix = ".dll";
	      }
	      
	      File f = new File(libraryPath, name 
	              + suffix);
	      boolean exists = f.isFile(); // check if it already exists

	      
	      // extract the embedded library file from the jar and save
	      // it to the current directory
	      try{
	    	  extractFile(name + suffix);
	      } catch (IOException ioe) {
	          // We might get an IOException trying to overwrite an existing
	          // library file if there is another process using the DLL.
	          // If this happens, ignore errors.
	          if (!exists) {
	              throw ioe;
	          }
	      }
	      
	      // try to clean up the DLL after the JVM exits
	      f.deleteOnExit();

	      // now actually load the DLL
	      System.load(f.getAbsolutePath());
	  }
}
