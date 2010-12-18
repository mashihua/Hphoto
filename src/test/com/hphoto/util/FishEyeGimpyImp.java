/*
 * Created on Sep 14, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.hphoto.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * @author Administrator
 *
 * 
 */
public class FishEyeGimpyImp implements GimpyEngine {

	private Properties props = null;
	private int imgH;
	private int imgW;

	public BufferedImage getDistortedImage(BufferedImage image) {

		Graphics2D graph = (Graphics2D) image.getGraphics();
		imgH = image.getHeight();
		imgW = image.getWidth();

		//want lines put them in a variable so we migh configure these later
		int hstripes = imgH/7;//4;
		int vstripes = imgW/7;//8;

		// claculate space between lines
		int hspace = imgH / (hstripes + 1);
		int vspace = imgW / (vstripes + 1);

		//draw the horizontal stripes
		for (int i = hspace; i < imgH; i = i + hspace) {
			graph.setColor(Color.blue);
			graph.drawLine(0, i, imgW, i);

		}

		// draw the vertical stripes
		for (int i = vspace; i < imgW; i = i + vspace) {
			graph.setColor(Color.red);
			graph.drawLine(i, 0, i, imgH);

		}

		// create a pixel array of the original image.
		// we need this later to do the operations on..

		int pix[] = new int[imgH * imgW];
		int j = 0;

		for (int j1 = 0; j1 < imgW; j1++) {
			for (int k1 = 0; k1 < imgH; k1++) {
				pix[j] = image.getRGB(j1, k1);
				j++;
			}

		}

		double distance = ranInt(imgW / 4, imgW / 3);

		// put the distortion in the (dead) middle
		int wMid = image.getWidth() / 2;
		int hMid = image.getHeight() / 2;

		//again iterate over all pixels..
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				
				int relX = x - wMid;
				int relY = y - hMid;
				
				double d1 =    Math.sqrt(relX * relX + relY * relY);
				if (d1 < distance) {
					 
					int j2 =wMid	+ (int) (((fishEyeFormula(d1 / distance) * distance) / d1)* (double) (x - wMid));
					int k2 =hMid	+ (int) (((fishEyeFormula(d1 / distance) * distance) / d1)* (double) (y - hMid));
					image.setRGB(x, y, pix[j2 * imgH + k2]);
				}
			}

		}

		return image;
	}

	/* (non-Javadoc)
	 * @see nl.captcha.obscurity.GimpyEngine#setProperties(java.util.Properties)
	 */
	public void setProperties(Properties props) {
		this.props = props;

	}

	private int ranInt(int i, int j) {
		double d = Math.random();
		return (int) ((double) i + (double) ((j - i) + 1) * d);
	}

	private double fishEyeFormula(double s) {
		// 		implementation of:
		//		g(s) = - (3/4)s3 + (3/2)s2 + (1/4)s, with s from 0 to 1.
		if (s < 0.0D) 	return 0.0D;
		if (s > 1.0D) 	return s;
		else
			return -0.75D * s * s * s + 1.5D * s * s + 0.25D * s;
	}
	
	public static void main(String[] args) {
		
		FishEyeGimpyImp imp = new FishEyeGimpyImp();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfiguration gc = gd.getDefaultConfiguration();
		BufferedImage image = new BufferedImage(3000, 2000, BufferedImage.TYPE_INT_ARGB);
		image.getGraphics().setColor(Color.white);
		image.getGraphics().drawImage(image, 0, 0, null);
		
		image = imp.getDistortedImage(image);
		
		try {
		 
			FileOutputStream out = new FileOutputStream(new File("./test"+System.currentTimeMillis()+".jpg"));
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			JPEGEncodeParam param =  encoder.getDefaultJPEGEncodeParam(image);
			param.setQuality(1f,true);
			encoder.encode(image,param);

		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
