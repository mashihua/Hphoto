
package com.hphoto.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.hphoto.bean.Category;
import com.hphoto.bean.UserProfile;
import com.hphoto.image.AbstractBufferedImageOp;
import com.hphoto.image.AdvanceScaleFilter;
import com.hphoto.image.ScaleFilter;
import com.hphoto.image.TwirlFilter;
import com.hphoto.server.ImageManipulation;


public class Test {
	


	static void printMem(){
		Runtime rt=Runtime.getRuntime( );
		System.out.println("Total Memory= "
				 	+ rt.totalMemory() //打印总内存大小
				 	+" Free Memory = "
				 	+ rt.freeMemory()
				 	+ "\t"  + (rt.totalMemory() / (1024 * 1024)) + ":"+(rt.freeMemory() /(1024 * 1024))
					); //打印空闲内存大小
	}
	
	static void printTrue(boolean value){
		/*
		for(int i = 7 ; i >= 0 ; i--){
			if(((1 << i)& (int)value) != 0)
				System.out.print("1");
			else
				System.out.print("0");
		}
		*/
	}
	
	static void printByte(byte value){
		for(int i = 16 ; i >= 0 ; i--){
			if(((1 << i)& value) != 0)
				System.out.print("1");
			else
				System.out.print("0");
		}
	}
	
	static void printInt(int value){
		for(int i = 31 ; i >= 0 ; i--){
			if(((1 << i)& value) != 0)
				System.out.print("1");
			else
				System.out.print("0");
		}
	}
	
	static void printLong(long value){
		for(int i = 63 ; i >= 0 ; i--){
			if(((1L << i)& value) != 0)
				System.out.print("1");
			else
				System.out.print("0");
		}
	}

	  /** Construct a half-sized version of this MD5.  Fits in a long **/
	  public static long halfDigest(byte[] digest) {
	    long value = 0;
	    for (int i = 0; i < 16; i++)
	      value |= ((digest[i] & 0xffL) << (16*(15-i)));
	    return value;
	  }
	  
	 private class Kuaiche implements Comparable{
		 private int i;
		 public Kuaiche(int i){
			 this.i =i;
		 }
		 
		 public String toString(){
			 return "\t"+Integer.toString(i);
		 }

		public int compareTo(Object o) {
			Kuaiche other = (Kuaiche)o;
			return (this == other) ? 0 : this.i > other.i ? -1 : 1;
		}
		 
	 }
	 
	public void doTest(){
		Kuaiche[] k;// = new Kuaiche[10];
		Random r = new Random();
		ArrayList list = new ArrayList();
		for(int i = 0;i < 10 ; i++){
			int l = Math.abs(r.nextInt() % 100);
			System.out.println(l);
			//k[i] = new Kuaiche(l);
			list.add(new Kuaiche(l));
		}
		k = (Kuaiche[]) list.toArray(new Kuaiche[0]);
		Arrays.sort(k);
		for(int i = 0;i < 10; i++ ){
			System.out.println(k[i]);
		}
	}
	
	
	Random generator = new Random();
	
	String[] font = {
			"Arial",
			"Courier"
	};
	
	Color[] color = {
			Color.black,
			Color.blue,
			Color.green,
			Color.orange,
			Color.red,
			Color.yellow
	};
	
	private Font[] getFont(){		
		return getFont(font,40);
	}
	
	private Font[] getFont(String[] font,int size){
		Font[] fonts = new Font[font.length];
		for(int i = 0 ; i < font.length ; i++ ){
			fonts[i] = new Font(font[i],Font.BOLD,size);
		}
		return fonts;
	}
	
	private void shear(Graphics g, int w1, int h1, Color color) {

		shearX(g, w1, h1, color);
		shearY(g, w1, h1, color);
	}

	public void shearX(Graphics g, int w1, int h1, Color color) {

		int period = generator.nextInt(10) + 5;

		boolean borderGap = true;
		int frames = 15;
		int phase = generator.nextInt(5) + 2;

		for (int i = 0; i < h1; i++) {
			double d =
				(double) (period >> 1)
					* Math.sin(
						(double) i / (double) period
							+ (6.2831853071795862D * (double) phase)
								/ (double) frames);
			g.copyArea(0, i, w1, 1, (int) d, 0);
			if (borderGap) {
				g.setColor(color);
				g.drawLine((int) d, i, 0, i);
				g.drawLine((int) d + w1, i, w1, i);
			}
		}

	}

	public void shearY(Graphics g, int w1, int h1, Color color) {

		int period = generator.nextInt(30) + 10; // 50;

		boolean borderGap = true;
		int frames = 15;
		int phase = 7;
		for (int i = 0; i < w1; i++) {
			double d =
				(double) (period >> 1)
					* Math.sin(
						(double) i / (double) period
							+ (6.2831853071795862D * (double) phase)
								/ (double) frames);
			g.copyArea(i, 0, 1, h1, 0, (int) d);
			if (borderGap) {
				g.setColor(color);
				g.drawLine(i, (int) d, i, 0);
				g.drawLine(i, (int) d + h1, i, h1);
			}

		}

	}

	
 	public String saveVerifyImage(int width,int height,OutputStream out) throws Exception{
 		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		
		BufferedImage image = new BufferedImage(width, height,
		BufferedImage.TYPE_BYTE_INDEXED);
		// 获取图形上下文
		Graphics2D g = (Graphics2D) image.getGraphics();
		// 设定背景色
		g.setColor(new Color(0xFFFFFF));
		g.fillRect(0, 0, width, height);		

		//g.drawRect(0,0,width-1,height-1);
		String word = "acpgh";//tempNumber.toString();
		float w = (float) (width * 0.6F);
		float h = (float) (height * 0.2F);
		int d = (int) (w / word.length());
		
		//g.setFont(new Font("Arial",Font.BOLD,(int)(height - (h * 2))));
		
		RenderingHints hints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
			
			hints.add(new RenderingHints(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY));
			
		g.setRenderingHints(hints);
			
			
		Font[] fonts = getFont();
		char[] wc = word.toCharArray();
		
		FontRenderContext frc = g.getFontRenderContext();
		int startPosX = 25;
		//字体颜色
		g.setColor(color[(int) (Math.random()*color.length)]);
		for (int i = 0;i<wc.length;i++) {
			char[] itchar = new char[]{wc[i]};
			//g2D.setColor(Color.black);
			int choiceFont = generator.nextInt(fonts.length) ;
			Font itFont = fonts[choiceFont];
			g.setFont(itFont);
			LineMetrics lmet = itFont.getLineMetrics(itchar,0,itchar.length,frc);
			GlyphVector gv = itFont.createGlyphVector(frc, itchar);
			double charWitdth = gv.getVisualBounds().getWidth();
			
			g.drawChars(itchar,0,itchar.length,(int)((((float) (width * 0.2F)) + d*i)) ,height/2 + 10);
			startPosX = startPosX+(int)charWitdth+2;
			//
	    	
		}// for next char array.
		/*
		for(int i = 0 ; i < word.length(); i++){
			String s = word.substring(i,i+1);
			g.drawString(s,(int)((((float) (width * 0.2F)) + d*i)),height/2 + 10);
		}
		*/
		/*
		WarpGrid source = new WarpGrid(6,10,width,height);
		WarpGrid dist = new WarpGrid(6,10,width,height);
		WarpFilter filter  = new WarpFilter(source,dist);	
		*/
		/*
		FieldWarpFilter filter = new FieldWarpFilter();		
		filter.setAmount(0.8f);
		filter.setPower(0.2f);
		filter.setStrength(10f);
		*/

		TwirlFilter filter = new TwirlFilter();	
		filter.setCentreX(0.5f);
		filter.setCentreY(0.5f);
		float angle = (float) Math.asin(Math.random() > 0.5 ?(float) Math.random():-(float) Math.random());
		//System.out.println(angle);
		filter.setAngle(angle);
		filter.setRadius(w / 2 + 5);
		
		ColorModel dstCM = image.getColorModel();		
		BufferedImage dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(width, height), dstCM.isAlphaPremultiplied(), null);
		image = filter.filter(image, dst);		
		
		ImageIO.write(image, "JPEG",out); 		
		return word;
	}

	static Category get(){
		
		return null;
	}
	
	  private static String parserText(final String text){
		  StringBuilder sb = new StringBuilder(text.length());
		  for(int i = 0 ; i < text.length() ;i++){		  
		  /*
		  & &amp;
		  < &it;
		  > &gt;
		  " &quot;
		  ' &apos;
		  */
			  char s = text.charAt(i);
			  switch(s){
				  case 38: 	sb.append("&amp;");
				  			break;
				  case 60:	sb.append("&it;");
				  			break;
				  case 62:	sb.append("&gt;");
				  			break;
				  case 34:	sb.append("&quot;");
				  			break;
				  case 39:	sb.append("&apos;");
				  			break;
				  default : sb.append(s);
				  
			  }
		  }
		  return sb.toString();
	  }
	  protected static String getLegalXml(final String text) {
	      if (text == null) {
	          return null;
	      }
	      StringBuffer buffer = null;
	      for (int i = 0; i < text.length(); i++) {
	        char c = text.charAt(i);
	        if (!isLegalXml(c)) {
				  if (buffer == null) {
			              // Start up a buffer.  Copy characters here from now on
			              // now we've found at least one bad character in original.
				      buffer = new StringBuffer(text.length());
			              buffer.append(text.substring(0, i));
			          }
	        	} else {
		           if (buffer != null) {
		             buffer.append(c);
		           }
	        }
	      }
	      return (buffer != null)? buffer.toString(): text;
	  }
	 
	  private static boolean isLegalXml(final char c) {
	    return c == 0x9 || c == 0xa || c == 0xd || (c >= 0x20 && c <= 0xd7ff)
	        || (c >= 0xe000 && c <= 0xfffd) || (c >= 0x10000 && c <= 0x10ffff);
	  }
	  
	  private static Element addNode(Document doc, Node parent,
		              String name, String text) {
		Element child = doc.createElement(name);
		child.appendChild(doc.createTextNode(getLegalXml(text)));
		parent.appendChild(child);
		return child;
	  	}
	  
	  private static Element addNode(Document doc, Node parent, String name) {
		    Element child = doc.createElement(name);
		    parent.appendChild(child);
		    return child;
		  }
	  
	 static long dirlength;//保存目录大小的变量
	 
	 static void sdl(String dirname)
	 {
		 File dir=new File(dirname);
		 String f[]=dir.list();
		 File f1;
		 for(int i=0;i<f.length;i++)
		 {
			 f1 = new File (dirname+"/"+f[i]);
			 if (!f1.isDirectory())
				 dirlength+=f1.length();
			 else
				 sdl(dirname+"/"+f[i]);//如果是目录,递归调用
		 }
	 }
	 
	 private static long getDU(String path,long l){
		 File file = new File(path);		 
		 String[] f =  file.list();
		 File f1;
		 for(String fe :f){
			 //if is file
			 f1 = new File (path+"/"+fe);
			 if(!f1.isDirectory())
				 l += fe.length();
			 else{ 
				 l += getDU(path+"/"+ fe,l);
			 	//System.out.println(fe.getPath() + "" + l);
			 }
		 }
		 return l;
	 }


			
	public static void Raise(int width,int height,int max){
		float rate = (float)width/(float)height;
		/*
			if(rate > 1){
				rate = (float)max / (float)height;
				width *= rate;
				height *= rate;
			}else{
				rate = (float)max /(float)width;
				width *= rate;
				height *=rate;
			}
		*/
		
		if(rate > 1){
			width = max;
			height = (int)(max/rate);
		}else{
			width = (int) (max *rate);
			height = max;
		}
		
				System.out.println(width + "," + height);
	}
			
			
	     
	public static void main(String[] arg) throws ParserConfigurationException, TransformerException{	
		
		
		//int i=2592,n=3872;
		
		//Raise(160,239,239);
		//File f = new File("./");
		//System.out.println(f.getPath());
		//System.out.println(getDU("./",0l));
		/*
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    Document doc = factory.newDocumentBuilder().newDocument();
		PrintStream out = System.out;
		Element rss = addNode(doc, doc, "rss");
		addNode(doc,rss,"descript","<a href=\"http://127.0.0.1/\"><img style=\"border:1px solid #5C7FB9\" src=\"\" alt=\"美女\"/></a></td>");
		DOMSource source = new DOMSource(doc);
	      TransformerFactory transFactory = TransformerFactory.newInstance();
	      Transformer transformer = transFactory.newTransformer();
	      transformer.setOutputProperty("indent", "yes");
	      StreamResult result = new StreamResult(out);
	      transformer.transform(source, result);
		//out.println(rss.toString());
		/*
		out.println(parserText("<a href=\"http://127.0.0.1/\"><img style=\"border:1px solid #5C7FB9\" src=\"\" alt=\"美女\"/></a></td>"));
		out.println(getLegalXml(parserText("<a href=\"http://127.0.0.1/\"><img style=\"border:1px solid #5C7FB9\" src=\"\" alt=\"美女\"/></a></td>")));
		/*
		System.out.println(Locale.getDefault().getLanguage().toString().toLowerCase());
		System.out.println(Locale.US.getLanguage().toString().toLowerCase());
		System.out.println(Locale.UK.getLanguage().toString().toLowerCase());
		
		
		/*
	    for( line = lines.readLine(); line != null; line = lines.readLine() ) {
	        if( line.equals("") )
	          continue;
	        if( line.startsWith( "Total # of bytes" ) ) {
	          this.capacity = Long.parseLong( line.substring( 
	                                line.lastIndexOf(' ') + 1, line.length() ));
	          continue;
	        } 
	        if( line.startsWith( "Total # of avail free bytes" ) ) {
	          this.available = Long.parseLong( line.substring( 
	                                line.lastIndexOf(' ') + 1, line.length() ));
	          continue;
	        }
	      }
	    */
		/*
		Calendar c=Calendar.getInstance();
		c.add(Calendar.YEAR,30);
		System.out.println(new Date());
		System.out.println(new Date(c.getTimeInMillis()));
		
		/*
		System.out.println(StringUtil.formatPercent(0.0001D,1));
		//System.out.println("/josh/josh/".indexOf("/josh"));
		String patternStr = "^/image/([^/]+)/([^/]+)/([s])?([0-9]+)-?(.?)/\\1$";
	    Pattern pattern = Pattern.compile(patternStr);
	    Matcher matcher = pattern.matcher("/image/flashget/AHHYILU/s48-c/flashget");
	    								 //image/flashget/AHHYILU/s48-c/flashget
	    //System.out.println(matcher.find());
	    //String s = matcher.replaceAll("/image?u=$1&amp;c=$5&amp;max=$4");
	    //System.out.println(s);
	    
	   // patternStr = "^/image/([^/]+)/([^/]+)/([s])?([0-9]+)-?(.?)/\\1$";
	   // pattern = Pattern.compile(patternStr);
	    //matcher = pattern.matcher("/image/flashget/AHHYILU/s48-c/flashget");
	    //matcher = pattern.matcher("/image/flashget/UntitledAlbum/s144/144115188075855872.jpg");
	    						///image/flashget/UntitledAlbum/s144/144115188075855872.jpg
	    System.out.println(matcher.find());
	    String s = matcher.replaceAll("/image?u=$1&corp=$5&max=$4");
	    System.out.println(s);
	    
	    //s = matcher.replaceAll("/hp/photo?user=$2&amp;category=$4&amp;id=$5&amp;$6");
	    //System.out.println(s);
	    
		/*
		 * [^/]?\\???([^/]*)
		String patternStr = "^/image/([^/]+)/([^/]+)/([s])?([0-9]+)-?(.?)/\1$";
	    Pattern pattern = Pattern.compile(patternStr);
	    Matcher matcher = pattern.matcher("/image/beijing.josh/category/s48-c/beijing.josh");
	    
	    String s = matcher.replaceAll("/image?u=$1&c=$5&max=$4");
	    
	  
		*/
		
		/*
		String[] p ={
				"^/image/([^/]+)/([^/]+)/([s])?([0-9]+)-?(.?)/([^\\.]+)\\.jpg$",
				"^/image/([^/]+)/([^/]+)/([s])?([0-9]+)-?(.?)/\\1",
				"^/image/([^/]+)/([^/]+)/([^\\.]+)\\.jpg\\?[^0-9]*([0-9]*)"
				
		};		
		String[] s0 = {
				"/image/beijing.josh/category/s600/photoid.jpg",
				"/image/beijing.josh/category/600-c/photoid.jpg",
				"/image/beijing.josh/category/s600-c/photoid.jpg"				
		};		
		String[] s1 ={
				"/image/beijing.josh/category/s48-c/beijing.josh"
		};		
		String[] s2 ={
				"/image/beijing.josh/category/photoid.jpg?max=600"
		};		
		//for(int i = 0 ; i < p.length ; i++){
			Pattern pattern = Pattern.compile(p[2]);
	    	
	    	for(int n = 0 ; n < s0.length ;n++){
	    		Matcher matcher = pattern.matcher(s0[n]);
	    		if(matcher.find()){
	    			System.out.println(s0[n]);
	    		}
	    		
	    	}
	    	Pattern email = Pattern.compile("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
	    	Matcher matcher = email.matcher("bei@josm.com");
	    	System.out.println(matcher.find());
	    	//}
	    	/*
	    	for(int n = 0 ; n < s1.length ;n++){
	    		Matcher matcher = pattern.matcher(s1[n]);
	    		if(matcher.find()){
	    			System.out.println(i + ":" + matcher.find());
	    			System.out.println(s1[n]);
	    		}
	    	}
	    	for(int n = 0 ; n < s2.length ;n++){
	    		Matcher matcher = pattern.matcher(s2[n]);
	    		if(matcher.find()){
	    			System.out.println(i + ":" + matcher.find());
	    			System.out.println(s2[n]);
	    		}
	    		
	    	}
	    	*/
	    // Set the input
	   // matcher.reset("/image/beijing.josh/category/s48-c/beijing.josh");
	    
	    // Get tagname and contents of tag
	   // boolean matchFound = matcher.find();   // true
	    //String tagname = matcher.group(1);     // tag
	    //String contents = matcher.group(2);    //  yy
	}

}
