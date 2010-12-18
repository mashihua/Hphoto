package com.hphoto.util;

import javax.servlet.http.*;

public class SessionListener implements HttpSessionListener
{
    private static int count=0;

    public void sessionCreated(HttpSessionEvent se)
    {	
    	se.getSession().getServletContext();
        count++;
    }

    public void sessionDestroyed(HttpSessionEvent se)
    {
       count--;
    }

    public static int getCount()
    {
       return(count);
     }
} 