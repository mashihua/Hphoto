package com.hphoto.server;


import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.*;


import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.io.MapFile;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.metrics.MetricsContext;
import org.apache.hadoop.metrics.MetricsRecord;
import org.apache.hadoop.metrics.MetricsUtil;
import org.apache.hadoop.metrics.Updater;
import org.apache.hadoop.metrics.jvm.JvmMetrics;

import org.apache.log4j.Logger;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.hphoto.bean.Exif;
import com.hphoto.bean.Category;
import com.hphoto.bean.Image;
import com.hphoto.bean.UserProfile;
import com.hphoto.image.AbstractBufferedImageOp;
import com.hphoto.image.AdvanceScaleFilter;
import com.hphoto.image.CropFilter;
import com.hphoto.image.ScaleFilter;
import com.hphoto.util.KeyUtil;

import com.hphoto.io.ImageWriteable;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.hphoto.web.HphotoParam;
import com.hphoto.FConstants;

public class ProcessImage {
	
	 public static final Logger LOG = Logger.getLogger(ProcessImage.class.getName());
	
	private static final ThreadLocal<ServletFileUpload> uploads = new ThreadLocal<ServletFileUpload>();	
	private long start = 0L;
	//private LinkedList
    private long timeout = 500L;
    private Configuration conf;
    private FileSystem fs;
    private TableServer server;
    private int BUFFER_SIZE = 4096; 
    private int defaultImameMax;
    private boolean high = true; 
    private Text original = new Text("original");
    private Cache cache = new LRUCache(2000);
    
    public final static String IMAGE_FILE_PATH = "/bigtable/hphoto/user/";
	
	private static class ImageNodeMetrics implements Updater {
	    private final MetricsRecord metricsRecord;
	    
	    private int numFilesCreated = 0;
	    private int numFilesOpened = 0;
	    private int byteWritten = 0;
	    private int byteRead= 0;
	    
	    
	    ImageNodeMetrics(Configuration conf) {
	      String sessionId = conf.get("session.id");
	      // Initiate Java VM metrics
	      JvmMetrics.init("ImageNode", sessionId);
	      // Create a record for imagenode metrics
	      MetricsContext metricsContext = MetricsUtil.getContext("image");
	      metricsRecord = MetricsUtil.createRecord(metricsContext, "ImageNode");
	      metricsRecord.setTag("sessionId", sessionId);
	      metricsContext.registerUpdater(this);
	    }
	      
	    /**
	     * Since this object is a registered updater, this method will be called
	     * periodically, e.g. every 5 seconds.
	     */
	    public void doUpdates(MetricsContext unused) {
	      synchronized (this) {
	        metricsRecord.incrMetric("files_created", numFilesCreated);
	        metricsRecord.incrMetric("files_opened", numFilesOpened);
	        metricsRecord.incrMetric("bytes_written", byteWritten);
	        metricsRecord.incrMetric("bytes_read", byteRead);
	              
	        numFilesCreated = 0;
	        numFilesOpened = 0;
	        byteWritten = 0;
	        byteRead = 0;
	      }
	      metricsRecord.update();
	    }
	      
	    synchronized void createFile() {
	      ++numFilesCreated;
	    }
	      
	    synchronized void openFile() {
	      ++numFilesOpened;
	    }
	      
	    synchronized void readBytes(int nbytes) {
	    	byteRead +=  nbytes;
	    }
	      
	    synchronized void wroteBytes(int nbytes) {
	      byteWritten += nbytes;
	    }
	  }
	    
	private ImageNodeMetrics myMetrics;
		
	
	class SquareCorp{
		int width,height,x,y;
		float rate;
		public SquareCorp(int width,int height){
			this.width = width;
			this.height = height;
			this.rate = (float)width/(float)height;
		}
		
		public BufferedImage action(int max,BufferedImage image){	
			if(rate > 1){
				rate = (float)max / (float)height;
				width *= rate;
				height *= rate;
				x = (width - max) / 2;
				y = 0;
			}else{
				rate = (float)max /(float)width;
				width *= rate;
				height *=rate;
				x = 0;
				y = (height - max) / 2;
			}
			BufferedImage dst = null;			
			Raise raise = new Raise(image.getWidth(),image.getHeight());
			image = raise.action(width>height?width:height, image);
			CropFilter filter = new CropFilter(x,y,max,max);
			dst = filter.filter(image, dst);
			return dst;
		}
		
		public int getWidth(){
			return width;
		}
		public int getHeight(){
			return height;
		}
		public int getX(){
			return x;
		}
		public int getY(){
			return y;
		}
		
	}
	
	class Raise{
		
		private int width,height;
		float rate;
		
		public Raise(int width,int height){
			this.width = width;
			this.height = height;
			this.rate = (float)width/(float)height;
		}
		
		public BufferedImage action(int max,BufferedImage image){			
			if(rate > 1){
				width = max;
				height = (int)(max/rate);
			}else{
				width = (int) (max *rate);
				height = max;
			}
			
			ColorModel dstCM = image.getColorModel();
			BufferedImage dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(width, height), dstCM.isAlphaPremultiplied(), null);
			AbstractBufferedImageOp filter;
			if(high){
				filter = new AdvanceScaleFilter(width,height);
			}else{
				filter = new ScaleFilter(width,height);
			}
			BufferedImage img = filter.filter(image, dst);
			return img;
		}
		
		public int getHeight(){
			return height;
		}
		public int getWidth(){
			return width;
		}
	}
	
	
	public ProcessImage(Configuration conf,TableServer server) throws IOException{
		this.conf = conf;
		defaultImameMax = conf.getInt("image.max",160);
		this.fs = FileSystem.get(conf);
		this.server = server;
		myMetrics = new ImageNodeMetrics(conf);
	}
	
	class ImageStream{
		
		private String filename;
		private String filetype;
		
		private OutputStream stream;
		
		public ImageStream(){
			stream = new ByteArrayOutputStream();
		}
		
		public OutputStream getStream(String owner,String category,String id){	
			byte[] bytes= new byte[BUFFER_SIZE];
			FSDataInputStream in = null;
			//for a singal image
			if(id!=null){
				try {
					Image image = server.getImage(owner,id);
					if(image!=null){
						in = fs.open(new Path(image.getImgsrc()));
						filename = image.getFileName();
						filetype = image.getType();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}			
			//for album image
			else if(category != null){
				try {
					Category  ic =  server.getCategory(owner, category);
					if(ic!=null)
						in = fs.open(new Path(ic.getImgurl()));	
					filename = ic.getName()+".jpg";
					filetype = "JPEG";
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}	
			//for user image
			else{			
				try {
					UserProfile[] up = server.getUser(owner, 1);
					if(up != null){
						in = fs.open(new Path(up[0].getImgurl()));
						filename = up[0].getNicename()+".jpg";
						filetype = "JPEG";
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
			try {
				if(in != null){
					int lenght = 0;
					//call metrics
				    myMetrics.openFile();
						while((lenght = in.read(bytes, 0, BUFFER_SIZE)) > 0){
							 stream.write(bytes,0,lenght);
							 myMetrics.readBytes(lenght);
						}			
				    stream.flush();
				    in.close(); 
				    				    

				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return stream;
		}
		
		public String getFileName(){
			return filename;
		}
		public String getFileType(){
			return filetype;
		}
		public OutputStream getStream(){
			return stream;
		}
	}
	
	private final static int FOR_USER_IMAGE = 0;
	
	private final static int FOR_ALBUM_IMAGE = 1;
		
	private final static int FOR_RAISE_IMAGE = 2;
	
	private final static int FOR_SINGAL_IMAGE = 3;
	
	MapFile.Reader reader = null;
	
	private static DateFormat expiresFormat  
	         = new SimpleDateFormat("E, dd MMM yyyy k:m:s 'GMT'", Locale.US);  
	
	private boolean inSize(String size){
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void processRequest(HttpServletRequest request,HttpServletResponse response){
		
		Map<String,CharSequence> map = new HashMap<String,CharSequence>();
		map.put(HphotoParam.USER.toString(),request.getParameter(HphotoParam.USER.toString()));
		map.put(HphotoParam.ALBUM.toString(),request.getParameter(HphotoParam.ALBUM.toString()));
		map.put(HphotoParam.IMAGE_ID.toString(),request.getParameter(HphotoParam.IMAGE_ID.toString()));
		map.put(HphotoParam.IMAGE_CORP.toString(),request.getParameter(HphotoParam.IMAGE_CORP.toString()));
		map.put(HphotoParam.IMAGE_MAX.toString(),request.getParameter(HphotoParam.IMAGE_MAX.toString()));
		map.put(HphotoParam.IMAGE_QUALITY.toString(),request.getParameter(HphotoParam.IMAGE_QUALITY.toString()));
		String path = null;
		int what = FOR_SINGAL_IMAGE;
		
		
		//if user,album,id						,for orinigal file
		//if user,album,id,corp,max,high		,for album image
		//if user,album,id,max,high				,for a raised image
		//if user,corp,max						,for a user image
		String id = (String) map.get(HphotoParam.IMAGE_ID.toString());
		String user = (String) map.get(HphotoParam.USER.toString());
		String maxStr = (String)map.get(HphotoParam.IMAGE_MAX.toString());
		String album =  (String) map.get(HphotoParam.ALBUM.toString());
		String corp = (String) map.get(HphotoParam.IMAGE_CORP.toString());
		
		assert(user!= null);
		if(!inSize(maxStr)){
			return;
		}

		if(map.get(HphotoParam.ALBUM.toString()) == null){	//for a user image
			path = user;
			what = this.FOR_USER_IMAGE;
		}else if(corp != null && corp.equals("c")){	//for album image
			assert(album != null);
			path =  user + "/" + album;
			what = this.FOR_ALBUM_IMAGE;
		}else if(maxStr != null ){//for a raised image
			assert(id != null);
			assert(album != null);
			path = user + "/" + album + "/" + id + maxStr;
			what = this.FOR_RAISE_IMAGE;
		}else{
			path =  user + "/" + album + "/" + id;
		}
		
		
		ByteArrayOutputStream bo = null;
		BufferedImage image = null;
		ImageWriteable imageWriter = (ImageWriteable) cache.get(path);	
				
		//if not incache
		if(imageWriter == null){
			//if is album
			if(what == this.FOR_ALBUM_IMAGE){
				imageWriter = getAlbumImage(user,album,null,maxStr);
				if(imageWriter != null){
					cache.put(path,imageWriter);
				}
			}else{
				ImageStream iStream = new ImageStream();
				bo = (ByteArrayOutputStream) iStream.getStream(user,album,id);
				if(bo != null && bo.size() > 0){					
				    	try {
				    		if(what != FOR_SINGAL_IMAGE){
								image = ImageManipulation.getImage(new ByteArrayInputStream(bo.toByteArray()));				
								int oWidth = image.getWidth();
								int oHeight = image.getHeight();
								int max = 166;
								try{
									max = Integer.parseInt(maxStr);
								}catch(NumberFormatException ne){}
								if(maxStr != null){
									Raise raise = new Raise(oWidth,oHeight);
									image = raise.action(max, image);
								}
								bo = new ByteArrayOutputStream();
					            JPEGEncodeParam jpegParam = new JPEGEncodeParam();
					            jpegParam.setQuality(1.0f);
					            ImageEncodeParam param = (ImageEncodeParam)jpegParam;
								ImageCodec.createImageEncoder(iStream.getFileType(), bo, param).encode(image);
				    		}
							imageWriter= new ImageWriteable(iStream.getFileName(),iStream.getFileType(),bo.toByteArray()); 
							cache.put(path,imageWriter);
							image = null;
			            } catch (Exception e) {
			            }
					finally{
						try {
							bo.close();
							bo = null;
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				     
		    	}else{
		    		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		    		return;
		    	}
			}
		}else if(what == this.FOR_ALBUM_IMAGE){ //if is album
			imageWriter = getAlbumImage(user,album,null,maxStr);
			if(imageWriter != null){
				cache.put(path,imageWriter);
			}
		}
		if(imageWriter != null){
			try{    
				response.setHeader("Content-Disposition", "attachment; filename=\"" +  new String(imageWriter.getName().getBytes("UTF-8"), "ISO8859-1" ) + "\"");
	    		response.setContentType("image/jpeg");
	    		response.setHeader("Content-Length", Integer.toString(imageWriter.getSize()));
	    		response.setHeader("Cache-Control", "public");   		
	    		Calendar c=Calendar.getInstance();
	    		c.add(Calendar.DAY_OF_MONTH,1);
	    		response.setHeader("Expires", expiresFormat.format(new Date(c.getTimeInMillis())));
	    		response.setHeader("Date", expiresFormat.format(new Date()));
	    		OutputStream os = response.getOutputStream();
	    		os.write(imageWriter.getBytes(), 0, imageWriter.getSize());
				os.flush();
				os.close();
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}finally{
				
			}
		}else{
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}
		
		
		try {
			if(bo!= null)
				bo.close();
		} catch (IOException e) {
		}		
	}
	
	private ImageWriteable getAlbumImage(String user,String album,String id,String maxStr){
		String path = user + "/" + album ;
		ImageWriteable image = (ImageWriteable) cache.get(path);		
		ImageStream iStream = new ImageStream();
		ByteArrayOutputStream  bo = null;
		FSDataInputStream in = null;
		Category cate = null;
		byte[] bytes= new byte[BUFFER_SIZE];
		try {
			cate = server.getCategory(user, album);
			if(cate == null){
				return null;
			}
			String file = cate.getImgurl();
			file = file.substring(file.lastIndexOf('/')+1);
			if(image!=null && file.equals(image.getName())){
				return image;
			}
			in = fs.open(new Path(cate.getImgurl()));
			if(in == null){
				LOG.info("Cound not album image:" + cate.getImgurl());
				return null;
			}
			int lenght = 0;
			//call metrics
		    myMetrics.openFile();
		    bo = new ByteArrayOutputStream();
			while((lenght = in.read(bytes, 0, BUFFER_SIZE)) > 0){
				 bo.write(bytes,0,lenght);
				 myMetrics.readBytes(lenght);
			}
			
		    bo.flush();
		    in.close(); 
			BufferedImage img = ImageManipulation.getImage(new ByteArrayInputStream(bo.toByteArray()));
			bo.close();
			bo=null;
			int oWidth = img.getWidth();
			int oHeight = img.getHeight();
			int max = 166;
			try{
				max = Integer.parseInt(maxStr);
			}catch(NumberFormatException ne){}
			SquareCorp scorp = new SquareCorp(oWidth,oHeight);
			img = scorp.action(max,img);
			JPEGEncodeParam jpegParam = new JPEGEncodeParam();
            jpegParam.setQuality(1.0f);
            ImageEncodeParam param = (ImageEncodeParam)jpegParam;
            bo = new ByteArrayOutputStream();
            ImageCodec.createImageEncoder("JPEG", bo, param).encode(img);
			image= new ImageWriteable(cate.getLablename()+".jpg","JPEG",bo.toByteArray()); 
			return image;
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(in != null)
				try {in.close();} catch (IOException e) {}
			if(bo != null)
				try {bo.close();} catch (IOException e) {}
		}
		return null;
	}
	
	public void processUpload(HttpServletRequest request,HttpServletResponse response) throws Exception{		
	    	ServletFileUpload upload = new ServletFileUpload();
	    	upload.setProgressListener(new UploadListener());
	    	//Parse the request
	    	FileItemIterator iter = upload.getItemIterator(request);	    	    
	    	String owner  = null;//= request.getParameter("uname");
			String category = null;//=  request.getParameter("category");
			Map<String,String> map = new HashMap<String,String> (10);
			
			upload.setHeaderEncoding("utf-8");
			
			
			//Process the uploaded items
			int uploadCunt = 0;
			//Image[] infos = null;
			//Exif[] exifs = null;
			String albumImage = null;
			long usedSpace = 0L;
			while (iter.hasNext()) {
				FileItemStream item = iter.next();
	    	    InputStream stream = item.openStream();

				 if(item.isFormField()){
					 String fieldName = item.getFieldName();
					 String value = Streams.asString(stream);
					 map.put(fieldName,value);
				 }else{						 
					 owner = map.get("uname");
					 category = map.get("category");
					 if(owner == null || category == null){
						 continue;
					 }
					 
					 byte[] bytes= new byte[BUFFER_SIZE];
					 int inLength = 0;
					 inLength = stream.read(bytes, 0, BUFFER_SIZE);
					 
					 String fileName = item.getName();
					  if (fileName != null) {
					    	fileName = FilenameUtils.getName(fileName);
					 }

				     //String fileName = new  String(item.getName().getBytes("ISO8859_1"),"UTF-8");
				     //get input stream;
				     ByteArrayOutputStream bo = new ByteArrayOutputStream();    
				     if(inLength < 1)
				    	 continue;
					 do{
					     bo.write(bytes,0,inLength);				    	 
					  } while((inLength = stream.read(bytes, 0, BUFFER_SIZE)) > 0);				     
					 bo.flush();
					 stream.close();
					 //if upload item is null,continue
					 int sizeInBytes = bo.size();
				     if(sizeInBytes < 10)
				    	 continue;
				     String type = ImageManipulation.getImageType(bo.toByteArray());
				     if(!type.equals(ImageManipulation.TYPE_JPEG)){
				    	 continue;
				     }
				     
				     uploadCunt++;//= Integer.getInteger(map.get("num"), 0);
					 //infos = new Image[uploadCunt];
					 //exifs = new Exif[uploadCunt];
					 
				     Image info = new Image();
				     Exif exif = null;
				     info.setFileName(fileName);
				     info.setCategory(category);
				     info.setOwner(owner);
				     info.setTimestamp(new Date(System.currentTimeMillis()));
				     info.setKbytes(sizeInBytes / 1024 );
				     usedSpace += (long)sizeInBytes;
				     String suffix = fileName.substring(fileName.lastIndexOf('.'));
				     String imagePath = (IMAGE_FILE_PATH  +owner+"/"+category+"/"+KeyUtil.getAuthKey(fileName,12)).replace(' ','+')+suffix;
				     //save file to dfs
				     Path path = new Path(imagePath);
				     albumImage = imagePath;
				     
				     info.setImgsrc(path.toString());
				     FSDataOutputStream fout = fs.create(path, true, BUFFER_SIZE);								     
				     fout.write(bo.toByteArray(),0,sizeInBytes);				    
				     fout.flush();
				     fout.close();
				     
				     //call metrics
				     myMetrics.createFile();
				     myMetrics.wroteBytes(sizeInBytes);
				     //get exif				  
				     if(type == ImageManipulation.TYPE_JPEG){
					     ByteArrayInputStream mbi = new ByteArrayInputStream(bo.toByteArray());					     				    
					     Metadata metadata = JpegMetadataReader.readMetadata(mbi);
					     Directory directory = metadata.getDirectory(ExifDirectory.class);
					     exif = new Exif(directory);				     
					     mbi.close();
				     }
				     
				     //get image width and height 
				     ByteArrayInputStream ibi = new ByteArrayInputStream(bo.toByteArray());
				     BufferedImage image = ImageManipulation.getImage(ibi);
				     info.setType(type);
				     info.setWidth(image.getWidth());
				     info.setHeight(image.getHeight());
				     ibi.close();				     
				     bo.close(); 				  				   				     
				     server.setImages(owner, new Image[]{info});			     
				     if(exif != null){
				    	 server.setExif(owner, info, exif);
				     }
				     
				 }
			}
			if(uploadCunt > 0){
				Category album = server.getCategory(owner, category);
				album.setCount(album.getCount() + uploadCunt );
				album.setLastupload(new Date());
				if(albumImage != null && !album.isSetAlbumPhoto()){
					album.setImgurl(albumImage);
				}
				if(usedSpace > 0L){
					album.setUsedSpace(album.getUsedSpace() + usedSpace);
				}
				server.setCategory(owner, new Category[]{album});
				if(usedSpace > 0L){
					UserProfile[] user = server.getUser(owner,1);
					if(user != null && user.length == 1){
						user[0].setUesdeSpace(user[0].getUesdeSpace() + usedSpace);
						server.setUser(user);
					}
					
				}
			}
			String redir = (String) map.get("redir");
			if( redir != null){
				response.sendRedirect(redir);
			}
			response.setStatus(response.SC_OK);
	    }
	
	/*	
	private ImageWriteable getUserImage(String user){
		ImageWriteable imageData = null;
		MapFile.Reader reader = null;
		InputStream in = null;
		ByteArrayOutputStream bo = null;		
		try {
			UserProfile[] profile = server.getUser(user, 1);
			if(profile != null && profile.length >0){
				boolean set = profile[0].isImageSetted();
				if(set){
					reader = new MapFile.Reader(fs, IMAGE_FILE_PATH + user, conf);
					imageData = (ImageWriteable) reader.get(new Text(user),new ImageWriteable());
				}else{
					 byte[] bytes= new byte[BUFFER_SIZE];
					 int inLength = 0;
					 in = fs.open(new Path(FConstants.DEFAULT_USER_IMAGE));
					 bo = new ByteArrayOutputStream();
					 do{
					     bo.write(bytes,0,inLength);				    	 
					  } while((inLength = in.read(bytes, 0, BUFFER_SIZE)) > 0);				     
					 bo.flush();
					 imageData = new ImageWriteable(user,"jpeg",bo.toByteArray());
				}
			}
		} catch (IOException e) {
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
			if(in != null){
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if(bo != null){
				try {
					bo.close();
				} catch (IOException e) {
				}
			}
		}
		return imageData;
	}
	
	
	private ImageWriteable getAlbumImage(String user,String album,String maxStr){
		ImageWriteable imageData = null;
		MapFile.Reader reader = null;
		InputStream in = null;
		ByteArrayOutputStream bo = null;
		Category category = null;
		try {
			category = server.getCategory(user,album);
		} catch (IOException e1) {
		}
		if(category != null && category.getCount() > 0){
			try {
				reader = new MapFile.Reader(fs, IMAGE_FILE_PATH + user + "/" + album, conf);				
				imageData = (ImageWriteable) reader.get(new Text(album),new ImageWriteable());
				String imgurl = category.getImgurl();
				String fileName = imgurl.substring(imgurl.lastIndexOf('/')+1);
				String fileType = imgurl.substring(imgurl.lastIndexOf('.')+1);
				if(imageData == null || fileName.equals(imageData.getName())){
					BufferedImage image;
					try {
						byte[] bytes= new byte[BUFFER_SIZE];
						int inLength = 0;
						in = fs.open(new Path(category.getImgurl()));				
						bo = new ByteArrayOutputStream();
						do{
							     bo.write(bytes,0,inLength);				    	 
						} while((inLength = in.read(bytes, 0, BUFFER_SIZE)) > 0);				     
						bo.flush();
						
						image = ImageManipulation.getImage(new ByteArrayInputStream(bo.toByteArray()));
						int oWidth = image.getWidth();
						int oHeight = image.getHeight();
						int max = 166;
						try{
							max = Integer.parseInt(maxStr);
						}catch(NumberFormatException ne){}
						SquareCorp scorp = new SquareCorp(oWidth,oHeight);
						image = scorp.action(max,image);
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						MapFile.Writer writer = null;
			            try {		            	
							JPEGEncodeParam jpegParam = new JPEGEncodeParam();
				            jpegParam.setQuality(1.0f);
				            ImageEncodeParam param = (ImageEncodeParam)jpegParam;
							ImageCodec.createImageEncoder("jpeg", out, param).encode(image);
							byte[] buffer = out.toByteArray();
							imageData = new ImageWriteable(fileName,fileType,buffer);
							//add to map file	

							writer = new MapFile.Writer(conf, fs, IMAGE_FILE_PATH + user + "/" + album, Text.class,
									 ImageWriteable.class, SequenceFile.CompressionType.BLOCK);
							writer.append(new Text(album),imageData);
							writer.close();

						     
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}						
				}
			} catch (IOException e) {
			}finally{
				if(reader != null){
					try {
						reader.close();
					} catch (IOException e) {
					}
				}
				if(in != null){
					try {
						in.close();
					} catch (IOException e) {
					}
				}
				if(bo != null){
					try {
						bo.close();
					} catch (IOException e) {
					}
				}
			}				
		}
		if(imageData == null){
			 byte[] bytes= new byte[BUFFER_SIZE];
			 int inLength = 0;
			try {
				in = fs.open(new Path(FConstants.DEFAULT_ALBUMIMAGE));				
				bo = new ByteArrayOutputStream();
				do{
				     bo.write(bytes,0,inLength);				    	 
				} while((inLength = in.read(bytes, 0, BUFFER_SIZE)) > 0);				     
				bo.flush();
				 imageData = new ImageWriteable(user,"JPEG",bo.toByteArray());
			} catch (IOException e) {
			}finally{
				if(in != null){
					try {
						in.close();
					} catch (IOException e) {
					}
				}
				if(bo != null){
					try {
						bo.close();
					} catch (IOException e) {
					}
				}
			}
		}
		
		return imageData;
	}
	
	private ImageWriteable getOriginalImage(MapFile.Reader reader,Text key){
		try {
			System.out.println("want to get :"+ key);
			return (ImageWriteable) reader.get(key,new ImageWriteable());
		} catch (IOException e) {
			return null;
		}
	}
	
	private ImageWriteable getImage(String user,String album,String id,String maxStr){
		ImageWriteable imageData = null;
		MapFile.Reader reader = null;
		Text  key = original;
		try {
			reader = new MapFile.Reader(fs, IMAGE_FILE_PATH + user + "/" + album, conf);
			if(maxStr == null){
				
			}else{
				key = new Text(maxStr + id);
			}	
			imageData = getOriginalImage(reader,key);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(imageData == null){
			try {
				reader = new MapFile.Reader(fs, IMAGE_FILE_PATH + user + "/" + album, conf);
				imageData = getOriginalImage(reader,original);
				BufferedImage image = ImageManipulation.getImage(new ByteArrayInputStream(imageData.getBytes()));
				int oWidth = image.getWidth();
				int oHeight = image.getHeight();
				int max = 166;
				try{
					max = Integer.parseInt(maxStr);
				}catch(NumberFormatException ne){}
				Raise raise = new Raise(oWidth,oHeight);
				image = raise.action(max, image);
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				MapFile.Writer writer = null;
	            try {		            	
					JPEGEncodeParam jpegParam = new JPEGEncodeParam();
		            jpegParam.setQuality(1.0f);
		            ImageEncodeParam param = (ImageEncodeParam)jpegParam;
					ImageCodec.createImageEncoder("jpeg", out, param).encode(image);
					byte[] buffer = out.toByteArray();
					imageData = new ImageWriteable(imageData.getName(),imageData.getType(),buffer);
					//add to map file
					writer = new MapFile.Writer(conf, fs, IMAGE_FILE_PATH + user + "/" + album, Text.class,
							 ImageWriteable.class, SequenceFile.CompressionType.BLOCK);
					writer.append(new Text(maxStr + id),imageData);
					writer.close();
				     
				} catch (IOException e1) {			
					e1.printStackTrace();
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return imageData;
	}
*/
}
