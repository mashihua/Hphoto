package com.hphoto.server;

/*
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.GifEncoder;
import ij.process.ImageProcessor;
import ij.process.MedianCut;
*/
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.media.jai.JAI;
import com.sun.imageio.plugins.bmp.BMPImageReader;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.jpeg.JPEGImageReader;
import com.sun.imageio.plugins.png.PNGImageReader;
import com.sun.media.jai.codec.MemoryCacheSeekableStream;


public class ImageManipulation {
	
	public static final String TYPE_GIF = "gif";   
	public static final String TYPE_JPEG = "jpeg";   
	public static final String TYPE_PNG = "png";   
	public static final String TYPE_BMP = "bmp";   
	public static final String TYPE_NOT_AVAILABLE = "na"; 
	
	public ImageManipulation(){
		
	}
	
	/*
	public static BufferedImage getImage(InputStream in) throws Exception{
		 try {
			 return JAI.create("stream", new MemoryCacheSeekableStream(
 					in
 			         )).getAsBufferedImage();       
	     } catch (Exception e) {
	         throw e;
	     } 
     
	}
	*/
	public static BufferedImage getImage(InputStream in) {
		 try {
			 MemoryCacheSeekableStream mc = new MemoryCacheSeekableStream(
						in
				         );
			 return JAI.create("stream", mc).getAsBufferedImage();       
	     } catch (Exception e) {
	         e.printStackTrace();
	     } 
	     return null;
	}
	
	/**  
	 * Get image type from byte[]  
	 *   
	 * @param textObj  
	 *            image byte[]  
	 * @return String image type  
	 */  
	
	public static String getImageType(byte[] textObj) {   
	   String type = TYPE_NOT_AVAILABLE;   
	   ByteArrayInputStream bais = null;   
	   MemoryCacheImageInputStream mcis = null;   
	   try {   
		    bais = new ByteArrayInputStream(textObj);
		    mcis = new MemoryCacheImageInputStream(bais);   
		    Iterator itr = ImageIO.getImageReaders(mcis);   
		    while (itr.hasNext()) {   
		     ImageReader reader = (ImageReader) itr.next();   
		     if (reader instanceof GIFImageReader) {   
		      type = TYPE_GIF;   
		     } else if (reader instanceof JPEGImageReader) {   
		      type = TYPE_JPEG;   
		     } else if (reader instanceof PNGImageReader) {   
		      type = TYPE_PNG;   
		     } else if (reader instanceof BMPImageReader) {   
		      type = TYPE_BMP;   
		     }   
		     reader.dispose();   
		    }   
	   } finally {   
		   if (bais != null) {   
		     try {   
		      bais.close();   
		     } catch (IOException ioe) {}   
		    } 
		    if (mcis != null) {   
			  try {   
			      mcis.close(); 
			  } catch (IOException ioe) {}   
		    }
	    } 	      
	   return type;   
	}  
	
}
