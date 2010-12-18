package com.hphoto.server;


import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.MiniHBaseCluster;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.mapred.StatusHttpServer;
import org.apache.log4j.Logger;

import com.hphoto.FConstants;
import com.hphoto.util.LibraryUtil;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingExecutionException;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;


public class UserService implements HConstants{
	private static Logger LOG = Logger.getLogger(UserService.class.getClass().getName());
	
	    protected static MiniHBaseCluster cluster = null;
		private static StatusHttpServer infoServer;
		static private ProcessImage image;
		static private TableServer server; 
		private Configuration conf;
		
		public UserService(Configuration conf) {	
			System.setProperty("java.awt.headless", "true");
			this.conf = conf;
			
		
		}
		
		public void start() throws IOException{
			int nodes = conf.getInt("hphoto.server.nodes", 1);
			
			boolean miniCluster = conf.getBoolean("hphoto.server.mini", true);
			if(miniCluster){
				cluster = new MiniHBaseCluster(conf,nodes,miniCluster,!(new File("./dfs").exists()),false);
			}
			(new Thread(new UidServer(conf))).start();
			this.server = new TableServer(conf);				
			image = new ProcessImage(conf,this.server);
			this.infoServer = new StatusHttpServer("hphoto", "0.0.0.0", 3000, true);	
			this.infoServer.addServlet("upload", "/hp/addPhotos", FileUploadService.class);
			this.infoServer.addServlet("uploadPercent", "/hp/addPhotosPercent", UploadListener.class);
			this.infoServer.addServlet("shutdownServer", "/hp/shutdownServer", ShutDownService.class);
			this.infoServer.addServlet("apiServer", "/hp/data", ApiServlet.class);
			this.infoServer.addServlet("image", "/image", ImageService.class);
			this.infoServer.addServlet("download", "/s/hphoto.zip", DownloadProgram.class);
			this.infoServer.setAttribute("hphoto.conf", this.conf);
			this.infoServer.setAttribute("hphoto.tableServer", this.server);
			if(System.getProperty("hptoto.debug","false").equals("true")){
				checkDafaultImage();
			}else{
				extractJarFileToHDTS();
			}
			this.infoServer.start();
			final String port = this.infoServer.getPort() == 80 ? "" : ":" +Integer.toString(this.infoServer.getPort());
			final String p = this.infoServer.getPort() == 80 ? ":80" : ":" +Integer.toString(this.infoServer.getPort());
			System.out.println("web server start at port" + p);
			TimerTask tt = new TimerTask() {
			      @Override
			      public void run() {
			    	  if(conf.getBoolean("browser.enable", true)){
							BrowserLauncher browserLauncher;
							try {
								browserLauncher = new BrowserLauncher(null);
								browserLauncher.openURLinBrowser("http://localhost"+port);
							} catch (BrowserLaunchingInitializingException e) {
							} catch (UnsupportedOperatingSystemException e) {
							} catch (BrowserLaunchingExecutionException e) {
							}
						}
			      }
			   };
			  Timer t = new Timer("StartBrowser");
			  t.schedule(tt, 500);
			    			
		}
		
		public static void shutdown(){
			try {
				infoServer.stop();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(cluster != null)
				cluster.shutdown();
		}
		
		public static class ShutDownService extends HttpServlet{
			public void doGet(final HttpServletRequest request,
		            final HttpServletResponse response
		            ){				
				try {	
					response.getWriter().print("Shut down cluster server!");
					Thread.sleep(1000);
					shutdown();
					System.exit(0);
				} catch (final Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			public void doPost(final HttpServletRequest request,
		            final HttpServletResponse response
		            ) throws IOException{
					doGet(request,response);
			}
	 }
	
		public boolean checkDafaultImage() throws IOException{
			FileSystem fs;
			if(cluster != null)
				fs = cluster.getDFSCluster().getFileSystem();
			else
				fs = FileSystem.get(conf);
			try {
				boolean exists = fs.exists(new Path("/bigtable/hphoto/default/defaultuser.jpg"));
					if(!exists){
					FileUtil.copy(new File("./build/webapps/hphoto/s/images/defaultuser.jpg"),
							fs,
							new Path(FConstants.DEFAULT_USER_IMAGE),
							false,
							this.conf
					);
					FileUtil.copy(new File("./build/webapps/hphoto/s/images/UntitledAlbum.jpg"),
							fs,
							new Path(FConstants.DEFAULT_ALBUMIMAGE),
							false,
							this.conf
					);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
		
		public boolean extractJarFileToHDTS()throws IOException {
			FileSystem fs;
			if(cluster != null)
				fs = cluster.getDFSCluster().getFileSystem();
			else
				fs = FileSystem.get(conf);
			boolean exists = fs.exists(new Path("/bigtable/hphoto/default/defaultuser.jpg"));
			if(!exists){
				OutputStream out = fs.create(new Path(FConstants.DEFAULT_USER_IMAGE));
				InputStream in = LibraryUtil.getJarFile("webapps/hphoto/s/images/defaultuser.jpg");
				if(in != null){
					IOUtils.copyBytes(in, out, conf, true);
				}
				out = fs.create(new Path(FConstants.DEFAULT_ALBUMIMAGE));
				in = LibraryUtil.getJarFile("webapps/hphoto/s/images/UntitledAlbum.jpg");
				if(in != null){
					IOUtils.copyBytes(in, out, conf, true);
				}
				
			}
			
			return false;
		}
		public static class FileUploadService extends HttpServlet{
				public void doGet(HttpServletRequest request,
			            HttpServletResponse response
			            ) throws UnsupportedEncodingException{
					request.setCharacterEncoding("UTF-8");
				  Map<String,String[]> pmap = request.getParameterMap();
				  ServletContext context = getServletContext();
					boolean isMultipart = ServletFileUpload.isMultipartContent(request);
					if(isMultipart){
							try {
								image.processUpload(request, response);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}else{
						response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
					}
				}
				
				public void doPost(HttpServletRequest request,
			            HttpServletResponse response
			            ) throws UnsupportedEncodingException{
				  request.setCharacterEncoding("UTF-8");
				  Map<String,String[]> pmap = request.getParameterMap();
				  ServletContext context = getServletContext();
					boolean isMultipart = ServletFileUpload.isMultipartContent(request);
					if(isMultipart){
							try {
								image.processUpload(request, response);
							} catch (Exception e) {
								e.printStackTrace();
							}
					}		
				}
		 }
		
		public static class ImageService extends HttpServlet{
			public void doGet(HttpServletRequest request,
		            HttpServletResponse response
		            ){
			  Map<String,String[]> pmap = request.getParameterMap();
			  ServletContext context = getServletContext();
				image.processRequest(request, response);
				
			}
			
			public void doPost(HttpServletRequest request,
		            HttpServletResponse response
		            ){
			  Map<String,String[]> pmap = request.getParameterMap();
			  ServletContext context = getServletContext();
				image.processRequest(request, response);	
			}
	 }
		
		public static void main(String[] args) throws IOException{
			 doMain(args, UserService.class);
		}
		
		  //
		  // Main program and support routines
		  //
		  
		  private static void printUsageAndExit() {
		    printUsageAndExit(null);
		  }
		  
		  private static void printUsageAndExit(final String message) {
		    if (message != null) {
		      System.err.println(message);
		    }
		    System.err.println("Usage: java " +
		        "com.hphoto.server.UserService [--port=port] start");
		    System.exit(0);
		  }
		  private static UserService us;
		  /**
		   * Do class main.
		   * @param args
		   * @param regionServerClass HRegionServer to instantiate.
		   */
		  protected static void doMain(final String [] args,
		      final Class<? extends UserService> serverClass) {
		    if (args.length < 1) {
		      printUsageAndExit();
		    }
		    String port = "REGION_PORT";
		    Configuration conf = new HBaseConfiguration();
		    conf.set(port,"60020");
		    // Process command-line args. TODO: Better cmd-line processing
		    // (but hopefully something not as painful as cli options).
		    final String addressArgKey = "--port=";
		    final String browserArgKey = "--browser=";
		    for (String cmd: args) {
		      if (cmd.startsWith(addressArgKey)) {
		        conf.set(port, cmd.substring(addressArgKey.length()));
		        continue;
		      }
		      
		      if (cmd.startsWith(browserArgKey)) {
			        conf.setBoolean("browser.enable", cmd.substring(browserArgKey.length()).equals("false")?false:true);
			        continue;
			   }
		      
		      if (cmd.equals("start")) {
		    	  us = new UserService(conf);
		    	  try {
					us.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	  break;
		      }
		      
		      if (cmd.equals("stop")) {	
					us.shutdown();
		    	  break;
		      }
		      
		      // Print out usage if we get to here.
		      printUsageAndExit();
		    }
		  }
		  
		
}
