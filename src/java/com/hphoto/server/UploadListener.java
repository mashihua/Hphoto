package com.hphoto.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.ProgressListener;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadListener extends HttpServlet implements ProgressListener {

	  private long megaBytes = -1;
	  private int items; 
	  private long contentLength;
	  private long bytesRead;
	   public void update(long pBytesRead, long pContentLength, int pItems) {
	       long mBytes = pBytesRead / 1000000;
	       if (megaBytes == mBytes) {
	           return;
	       }
	       megaBytes = mBytes;
	       this.items = pItems;
	       this.contentLength = pContentLength;
	       
	   }
	   
	   public void doGet(HttpServletRequest request,
	            HttpServletResponse response
	            ) throws IOException{
		   PrintWriter writer = response.getWriter();
		   writer.print("We are currently reading item " + items);
	       if (contentLength == -1) {
	    	   writer.print("So far, " + bytesRead + " bytes have been read.");
	       } else {
	    	   writer.print("So far, " + bytesRead + " of " + contentLength
	                              + " bytes have been read.");
	       }
		}
		
		public void doPost(HttpServletRequest request,
	            HttpServletResponse response
	            ) throws IOException{
			doGet(request,response);	
		}

}
