package com.hphoto.util;


import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class ErrorHandler {
	
	
	static public void setError404(HttpServletResponse response) throws IOException
    {
        response.setContentType("text/html;charset=gb2312");
        response.setStatus(404);
        response.getOutputStream().write("Error:404,File not Found!".getBytes());
     
    }

         
 
}
