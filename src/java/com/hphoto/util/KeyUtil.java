package com.hphoto.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.Text;

public class KeyUtil {
	
	static final char[] charset = new char[52];
	static{
		for(int i = 65 ; i < 91 ;i++){
			charset[i-65] = (char)i;
		}
		for(int i = 97 ; i < 123 ;i++){
			charset[i-97 + 26] = (char)i;
		}
	}
	/** The SHA-1 algorithm. */
	private static MessageDigest sha;
	
	 static Pattern isEnglishPattern = Pattern.compile("\\w+");
		 
	public static Text getKey(String key){
		return getKey(key,7);
	}
	
	public static Text getKey(String key,int length){
		if(key == null || key.trim().equals(""))
			key = UUID.randomUUID().toString();
		key = key.replaceAll(" ", "");		
		StringBuilder sb = new StringBuilder();
		Matcher m = isEnglishPattern.matcher(key);
		if(m.matches()){
			sb.append(key);
		}else{
			byte s[] = null;
			if(sha==null){
				try {
					sha = MessageDigest.getInstance("SHA-1");
				} catch (NoSuchAlgorithmException e) {
				}
			}else{
				sha.reset();
			}			
			byte[] b = key.getBytes();		     
		    sha.update(b);
		    byte[] digestBytes = sha.digest();
		    int nbBytePerInt = digestBytes.length/length;
		    int offset = 0;
		    for(int i = 0; i < length; i++){
		        int val = 0;
		        for(int j = offset; j < offset + nbBytePerInt; j++) {
		          val |=
		            (digestBytes[offset] & 0xff) << ((nbBytePerInt - 1 - (j - offset)) * 8);
		        }
		        offset += nbBytePerInt;
		        sb.append(charset[Math.abs(val) % charset.length]);
		    }
		}
		return new Text(sb.toString());
	}
	
	public static Text getAuthKey(){
		
		return getAuthKey(UUID.randomUUID().toString());
	}
	
	public static Text getAuthKey(String key){
		return getAuthKey(key,9);		
	}
	
	public static Text getAuthKey(String key,int length){
		key = key.replaceAll(" ", "");		
		StringBuilder sb = new StringBuilder();
		byte s[] = null;
		if(sha==null){
			try {
				sha = MessageDigest.getInstance("SHA-1");
			} catch (NoSuchAlgorithmException e) {
			}
		}else{
			sha.reset();
		}			
		byte[] b = key.getBytes();		     
	    sha.update(b);
	    byte[] digestBytes = sha.digest();
	    int nbBytePerInt = digestBytes.length/length;
	    int offset = 0;
	    for(int i = 0; i < length; i++){
	        int val = 0;
	        for(int j = offset; j < offset + nbBytePerInt; j++) {
	          val |=
	            (digestBytes[offset] & 0xff) << ((nbBytePerInt - 1 - (j - offset)) * 8);
	        }
	        offset += nbBytePerInt;
	        sb.append(charset[Math.abs(val) % charset.length]);
	    }
	    return new Text(sb.toString());
	}
	/*
	public static String reverse(String input){
		return input;
	}
	*/
	
}
