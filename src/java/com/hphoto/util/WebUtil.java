package com.hphoto.util;

import java.util.Date;

import javax.servlet.http.*;

public class WebUtil {
	
	static {
		Date expires = new Date(new Date().getTime() + (30 * 365 * 24 * 60 * 60 * 1000L));
		};
	
	public  class CookieValue{
		String name;
		String value;
		String domain;
		String path;
		Date date;
		public CookieValue(String name, String value){
			this.name = name;
			this.value = value;
		}
		public void setDomain(String domain){
			this.domain = domain;			
		}
		public void setPath(String path){
			this.path = path;			
		}
		public void setExpires(Date date){
			this.date = date;			
		}
		public String toString(){
			return "name="+name+";value="+value+(domain!=null?";domian="+domain:"")+(path!=null?";path="+path:"")+(date!=null?";expires="+date:"");
		}
	}

	
	public static String getCookieValue(Cookie[] cookies,String name,String defaultValue){
		if(cookies == null){
			return defaultValue;
		}
		if(cookies.length == 0){
			return defaultValue;
		}
		for(int i=0; i<cookies.length; i++) {
		Cookie cookie = cookies[i];
		if (name.equals(cookie.getName()))
			return(cookie.getValue());
		}
		return(defaultValue);
	}
	
	public static void setCookie(HttpServletResponse response,String cookieName,String value){
		
		response.addCookie(new Cookie(cookieName,value));
	}

	public static void setCookie(HttpServletResponse response,CookieValue value){
		Cookie cookie = new Cookie(value.name,value.value);
		if(value.path != null)
			cookie.setPath(value.path);
		if(value.domain != null)
			cookie.setDomain(value.domain);
		if(value.date != null){
			Date now = new Date();
			if(now.before(value.date)){				
				cookie.setMaxAge((int)((value.date.getTime() - now.getTime())/1000L));
			}else{
				cookie.setMaxAge(-1);
			}
		}else{
			cookie.setMaxAge(-1);
		}
		response.addCookie(cookie);
	}


	
}
