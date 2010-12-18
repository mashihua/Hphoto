package com.hphoto.util;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;


public class I18nUtil {
	
	static Set<String> set = new HashSet(); 
	
	public I18nUtil(){
		
	}
	
	public I18nUtil(String entry){
		set.add(entry);
	}
	
	public static String getText(String id,String key){
		
		return getText(id,key,Locale.getDefault(),null);
	}
	
	public static String getText(String id,String key,Object[] args){
		
		return getText(id,key,Locale.getDefault(),args);
	}
	
	public static String getText(String id,String key,Locale locale){
		
		return getText(id,key,locale,null);
	}
	

	
	public static String getText(String id,String key,Locale locale,Object[] args){
		 String message = ""; 
		 try {
				ResourceBundle resourceBundle = ResourceBundle.getBundle(id,locale);
	            String text = resourceBundle.getString(key);
	            message =  MessageFormat.format(text,args);
	        } catch ( MissingResourceException e ) {
	        }
		 
		 return message;
	 }
	 
	
	public static Locale getLoacl(HttpServletRequest request){
		try {
			String[] s = request.getHeader("accept-language").split(";")[0].split(",");
			return new Locale(s[1]);
		}catch(Exception e){
			
		}		
		return Locale.getDefault();
		
	}
}
