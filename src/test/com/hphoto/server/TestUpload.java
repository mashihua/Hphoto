package com.hphoto.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;


public class TestUpload extends TestCase {
	
	public static final String FB_SERVER = "127.0.0.1/upload"; 
	public static final String SERVER_ADDR = "http://" + FB_SERVER;

	public static URL SERVER_URL = null;
	private File[]  _uploadFile = null;
	
	static {
	    try {
	      SERVER_URL = new URL(SERVER_ADDR);
	    }
	    catch (MalformedURLException e) {
	      System.err.println("MalformedURLException: " + e.getMessage());
	      System.exit(1);
	    }
	  }
		
	 
	 public String getContentType(File file){
		 return "application/octet-stream";
	 }
	 
	protected static final String CRLF = "\r\n";
	protected static final String PREF = "--";
	protected static final int UPLOAD_BUFFER_SIZE = 2048;
	
	
	 public InputStream postFileRequest(URL SERVER_URL,
	                                     Map<String, CharSequence> params,File[] uploadFile) {
		 
	    assert (null != uploadFile);
	    try {
	    
	      String boundary = Long.toString(System.currentTimeMillis(), 16);
	      URLConnection con = SERVER_URL.openConnection();
	      con.setDoInput(true);
	      con.setDoOutput(true);
	      con.setUseCaches(false);
	      
	      con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
	      
	      
	      long fileLength = 0;
	      String fileStart[] = new String[uploadFile.length];
	      String fileEnd = CRLF;
	      StringBuilder data = new StringBuilder();
	      
	      //build file start handle
	      for(int i = 0 ; i < uploadFile.length ; i++){
	    	  fileLength += uploadFile[i].length();
	    	  data.append(PREF + boundary + CRLF);
	    	  data.append("Content-disposition: form-data; name=\"file"+i+"\"; filename=\"" + uploadFile[i].getName() + "\"" + CRLF);
	    	  data.append("Content-Type: " + getContentType(uploadFile[i]) + CRLF);
	    	  data.append(CRLF);
	    	  //data.append("Content-Transfer-Encoding: binary" + CRLF); // not necessary
	    	  fileStart[i] = data.toString();
	    	  data.setLength(0);
	      }
	      
	      //builder form data field;	      	      
	      for (Map.Entry<String, CharSequence> entry: params.entrySet()) {
	    	  data.append(PREF + boundary + CRLF);
	    	  data.append("Content-disposition: form-data; name=\"" + entry.getKey() + "\"");
	    	  data.append(CRLF + CRLF);
	    	  data.append(entry.getValue().toString());
	    	  data.append(CRLF);
	      }
	      

	      int fileHadleLength = 0;
	      for(String s :fileStart){
	    	  fileHadleLength += s.getBytes().length;
	      }

	      //now together the data length
	      int total = (int) (data.toString().getBytes().length + fileHadleLength + fileLength + CRLF.getBytes().length * uploadFile.length);	      
	      //send content length
	      con.setRequestProperty("Content-Length", String.valueOf(total));
	      con.setRequestProperty("MIME-version", "1.0");
	      
	      DataOutputStream out = new DataOutputStream(con.getOutputStream());
	      //send form data field
	      out.writeBytes(data.toString());

	      int byteCounter = 0;
	      //send files
	      for(int i = 0 ; i < uploadFile.length ; i++){    	  
		      BufferedInputStream bufin = new BufferedInputStream(new FileInputStream(uploadFile[i]));
		      out.writeBytes(fileStart[i]);
	    	  byte b[] = new byte[UPLOAD_BUFFER_SIZE];		      
		      int t;
		      while (-1 != (t = bufin.read(b))) {
		        byteCounter += t;
		        out.write(b, 0, t);
		      }
		      bufin.close();
		      out.writeBytes(fileEnd);
	      }
	 
	      out.writeBytes(CRLF + PREF + boundary + PREF + CRLF);
	      out.flush();
	      out.close();	    	      
	      InputStream is = con.getInputStream();
	      return is;
	    }
	    catch (Exception e) {
	      System.out.println("exception: " + e.getMessage());
	      e.printStackTrace();
	      return null;
	    }
	 }  
	 
	 public InputStream postFileRequest1(File file,
             Map<String, CharSequence> params) throws IOException {
			assert (null != file);
			try {
			BufferedInputStream bufin = new BufferedInputStream(new FileInputStream(file));
			
			String boundary = Long.toString(System.currentTimeMillis(), 16);
			URLConnection con = SERVER_URL.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			con.setRequestProperty("MIME-version", "1.0");
			
			DataOutputStream out = new DataOutputStream(con.getOutputStream());
			
			for (Map.Entry<String, CharSequence> entry: params.entrySet()) {
			out.writeBytes(PREF + boundary + CRLF);
			out.writeBytes("Content-disposition: form-data; name=\"" + entry.getKey() + "\"");
			out.writeBytes(CRLF + CRLF);
			out.writeBytes(entry.getValue().toString());
			out.writeBytes(CRLF);
			}
			
			out.writeBytes(PREF + boundary + CRLF);
			out.writeBytes("Content-disposition: form-data; name=\"file0\"; filename=\"" + file.getName() + "\"" +
			CRLF);
			out.writeBytes("Content-Type: image/jpeg" + CRLF);
			// out.writeBytes("Content-Transfer-Encoding: binary" + CRLF); // not necessary
			
			// Write the file
			out.writeBytes(CRLF);
			byte b[] = new byte[UPLOAD_BUFFER_SIZE];
			int byteCounter = 0;
			int i;
			while (-1 != (i = bufin.read(b))) {
			byteCounter += i;
			out.write(b, 0, i);
			}
			out.writeBytes(CRLF + PREF + boundary + PREF + CRLF);
			
			out.flush();
			out.close();

			InputStream is = con.getInputStream();
			return is;
			}
			catch (Exception e) {
			System.out.println("exception: " + e.getMessage());
			e.printStackTrace();
			return null;
			}
	 }
	 
	 public void testUpload() throws IOException{
		 HashMap<String,CharSequence> map = new HashMap<String,CharSequence>();
		 map.put("u","joshma");
		 map.put("category", "HytlOe");
		 File[] file = (new File("./photo")).listFiles(new FileFilter(){
			public boolean accept(File pathname) {
				String f = pathname.getName().toLowerCase();
				return f.endsWith("jpg");
			}
			 
		 });
		 map.put("num", String.valueOf(file.length));
		 //InputStream in = postFileRequest1(file[1],map);

		 InputStream in =  postFileRequest(SERVER_URL,map,file);
		 
		 DataOutputStream out = new DataOutputStream(System.out);
		
		 if(in != null){
			 byte b[] = new byte[UPLOAD_BUFFER_SIZE];		      
		      int t;
		      while (-1 != (t = in.read(b))) {

		    	  out.write(b, 0, t);
		      }
		 }
		 
		 out.close();
	 }
	    
}
