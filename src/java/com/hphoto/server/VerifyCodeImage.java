package com.hphoto.server;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Random;

import javax.imageio.ImageIO;

import com.hphoto.image.Captcha;
import com.hphoto.image.RippleFilter;
import com.hphoto.image.TransformFilter;
import com.hphoto.image.TwirlFilter;


public class VerifyCodeImage {
	
	private Properties properties;
	private String[] font = {
			"Arial",
			"Courier"
	};
	
	
	private Color[] color = {
			Color.black,
			Color.blue,
			//Color.orange,
			new Color(179,21,17),
			new Color(10,131,31),
			new Color(140,89,47),
			new Color(94,95,41),
			new Color(18,73,118),
			new Color(81,23,115),
			new Color(90,90,90),
			new Color(113,37,129)
	};
	
	private static char[] captchars =
		new char[] {
			'a',
			'b',
			'c',
			'd',
			'e',
			'f',
			'g',
			'h',
			'i',
			'j',
			'k',
			'l',
			'm',
			'n',
			'0',
			'p',
			'q',
			'r',
			's',
			't',
			'u',
			'v',
			'w',
			'x',
			'y',
			'z'
			};
	private Random generator = new Random();
	
	public VerifyCodeImage(){
		
	}
	
	public VerifyCodeImage(Properties properties){
		this.properties = properties;
	}
	
	private String word;
	
	public String getWord(){
		return getWord(4);
	}
	
	
	public String getWord(int length){
		String result = ""; 
		for (int i = 0; i < length; i++) {
			result += captchars[generator.nextInt(captchars.length - 1) + 1];
		}
		this.word = result;
		return result;
	} 
	
	public void setWord(String word){
		this.word = word;
	}
	int fontSize = 18;
	private Font getFont(){
		return getFont(fontSize);
	}
	
	private Font getFont(int size){
		return new Font(font[generator.nextInt(font.length - 1) +1],Font.BOLD,size);
	}
	public void setFontSize(int size){
		fontSize = size;
	}
	
	private Color getColor(){		
		return color[(int) (Math.random()*color.length)];		
	}
	
	public String saveVerifyImage(int width,int height,OutputStream out) throws IOException{
		
	 		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	        
			
			
			BufferedImage image = new BufferedImage(width, height,
										BufferedImage.TYPE_BYTE_INDEXED);
			Graphics2D g = (Graphics2D) image.getGraphics();
			g.setColor(new Color(0xFFFFFF));
			g.fillRect(0, 0, width, height);
			String word;
			if(this.word == null)
				word = getWord(4);
			else
				word = this.word;
			//total width;
			float w = (float) (width * 0.9F);
			//toatl height;
			float h = (float) (height * 0.2F);
			//every word width
			int d = (int) (w / word.length());

			RenderingHints hints = new RenderingHints(
					RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
				
				hints.add(new RenderingHints(RenderingHints.KEY_RENDERING,
					RenderingHints.VALUE_RENDER_QUALITY));
				
			g.setRenderingHints(hints);
				
				
			Font font = getFont();
			char[] wc = word.toCharArray();
			
			FontRenderContext frc = g.getFontRenderContext();
			g.setColor(getColor());
			g.setFont(font);
			
			GlyphVector gv = font.createGlyphVector(frc, wc);
			double charWitdth = gv.getVisualBounds().getWidth();
			int startPosX = (int)(width - charWitdth) / 2;	
			g.drawChars(wc,0,wc.length, startPosX , height/2 + (int)(height*0.1));
			/*
			for (int i = 0;i<wc.length;i++) {
				char[] itchar = new char[]{wc[i]};
				GlyphVector gv = font.createGlyphVector(frc, itchar);
				double charWitdth = gv.getVisualBounds().getWidth();
				g.drawChars(itchar,0,itchar.length, startPosX , height/2 + 10 );
				startPosX +=  d;
				//g.drawChars(itchar,0,itchar.length,(int)((((float) (width * 0.1F)) + d*i)) ,height/2 + 10);				
			}// for next char array.
			*/
			Captcha ca = captcha[generator.nextInt(captcha.length)];
			ca.setRange(w);
			image = ca.getDistortedImage(image);
			ImageIO.write(image, "JPEG",out); 		
			return word;
		}
	Captcha[] captcha = {
			new Ripple(),
			new Twirl()
	};
	class Ripple implements  Captcha{
		public Ripple(){};
		private int fil[] = {
				RippleFilter.SINE//,
				//RippleFilter.NOISE
		};
		private int ripple = 0;

		
		public  BufferedImage getDistortedImage(BufferedImage image){
					
			RippleFilter wfilter = new RippleFilter();
			ripple = fil[0];//[generator.nextInt(fil.length - 1) +1];
			wfilter.setWaveType(ripple);
			wfilter.setXAmplitude(ripple);
			wfilter.setYAmplitude(generator.nextFloat()+1.0f);
			wfilter.setXWavelength(generator.nextInt(5)+8F);
			wfilter.setYWavelength(generator.nextInt(3)+2F);
			wfilter.setEdgeAction(TransformFilter.WRAP);
			ColorModel dstCM = image.getColorModel();		
			BufferedImage dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(image.getWidth(), image.getHeight()), dstCM.isAlphaPremultiplied(), null);
			return wfilter.filter(image, dst);
		}
		
		public void setRange(float width) {
						
		}
		
		private float getFloat(){
			float value;
			value = generator.nextInt(4)+3.6f;
			if(ripple == RippleFilter.NOISE){
				value = generator.nextInt(4)+1.6f;
			}			
			return value;
		}
	}
	
	class Twirl implements  Captcha{
		float w;
		public Twirl(){}
		public BufferedImage getDistortedImage(BufferedImage image) {
			TwirlFilter filter = new TwirlFilter();	
			filter.setCentreX(0.4f);
			filter.setCentreY(0.4f);
			float angle = getFloat();
			filter.setAngle(angle);
			filter.setRadius(w / 2 + 5);
			ColorModel dstCM = image.getColorModel();		
			BufferedImage dst = new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(image.getWidth(), image.getHeight()), dstCM.isAlphaPremultiplied(), null);
			return filter.filter(image, dst);	
		}
		public void setRange(float width) {
			this.w = width;			
		}	
		
		private float getFloat(){
			float angle;
			while (true){
				angle = (float)(generator.nextFloat());
				if ( angle > 0.2F && angle < 0.6){
					break;
				}
			}
			return  generator.nextInt() % 2 == 0 ? angle : -angle;
		}
	}
	
	public static void main(String[] arg) throws  Exception{		
		new VerifyCodeImage().saveVerifyImage(140, 70, new FileOutputStream(new File(System.currentTimeMillis()+ ".jpg")));
	}
}
