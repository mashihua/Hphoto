package com.hphoto.server;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hphoto.server.VerifyCodeImage;

public class Captcha extends HttpServlet implements Servlet{
	public final static String CAPCHA_SESSION_KEY = "CAPCHA_SESSION_KEY";
	private VerifyCodeImage vc;
	public void doGet(HttpServletRequest request,
            HttpServletResponse response
            ) throws IOException{
		if(vc == null)
			vc = new VerifyCodeImage();
		int width = 140;
		int height = 70;
		String word = vc.getWord(6);
		request.getSession().setAttribute(Captcha.CAPCHA_SESSION_KEY, word);
		 response.setHeader("Content-Disposition", "attachment; filename=\"" + 
                 "captcha.jpg" + "\"");
		 	response.setContentType("application/octet-stream");
		vc.setFontSize(36);
		vc.saveVerifyImage(width, height, response.getOutputStream());

	}
	
	public void doPost(HttpServletRequest request,
            HttpServletResponse response
            ){
		
	}

}
