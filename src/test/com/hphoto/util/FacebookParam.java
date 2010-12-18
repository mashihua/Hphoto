package com.hphoto.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;



public enum FacebookParam
implements CharSequence {
	  SIGNATURE,
	  USER("user"),
	  SESSION_KEY("session_key"),
	  EXPIRES("expires"),
	  IN_CANVAS("in_canvas"),
	  IN_IFRAME("in_iframe"),
	  IN_PROFILE("profile"),
	  TIME("time"),
	  FRIENDS("friends"),
	  ADDED("added"),
	  PROFILE_UPDATE_TIME("profile_udpate_time"),
	  API_KEY("api_key"),
	  // PhotoUploads
	  PHOTOS_CREATE_ALBUM("facebook.photos.createAlbum",3),
	  PHOTOS_ADD_TAG("facebook.photos.addTag", 5)//,
	  //PHOTOS_UPLOAD("facebook.photos.upload", 3, true)
	  ;
/*
	     private static Map<String, FacebookParam> _lookupTable =
		    new HashMap<String, FacebookParam>(FacebookParam.values().length);
		  static {
		    for (FacebookParam param: FacebookParam.values()) {
		      _lookupTable.put(param.toString(), param);
		    }
		  }

		  /**
		   * Retrieves the FacebookParam corresponding to the supplied String key.
		   * @param key a possible FacebookParam
		   * @return the matching FacebookParam or null if there's no match
		   */
	  /*
		  public static FacebookParam get(String key) {
		    return isInNamespace(key) ? _lookupTable.get(key) : null;
		  }
		  */
		  /**
		   * Indicates whether a given key is in the FacebookParam namespace
		   * @param key
		   * @return boolean 
		   */


		  private String _paramName;
		  private String _signatureName;

		  private FacebookParam() {
		    this._paramName = "fb_sig";
		  }

		  private FacebookParam(String name) {
		    this._signatureName = name;
		    this._paramName = "fb_sig_" + name;
		  }

		  private FacebookParam(String name,int i) {
			    this._signatureName = name;
			    this._paramName = "fb_sig_" + name;
			}
		  
		  /* Implementing CharSequence */
		  public char charAt(int index) {
		    return this._paramName.charAt(index);
		  }

		  public int length() {
		    return this._paramName.length();
		  }

		  public CharSequence subSequence(int start, int end) {
		    return this._paramName.subSequence(start, end);
		  }

		  public String toString() {
		    return this._paramName;
		  }
		  
		  public String getSignatureName() {
		    return this._signatureName;
		  }
		  
		  public static String stripSignaturePrefix(String paramName) {
		    if (paramName != null && paramName.startsWith("fb_sig_")) {
		      return paramName.substring(7);
		    }
		    return paramName;
		  }

		  protected class Pair<N, V> {
			    public N first;
			    public V second;

			    public Pair(N name, V value) {
			      this.first = name;
			      this.second = value;
			    }
			  }
		  
		  public static void main(String[] args) throws Exception {
		    //System.out.println( isSignature("fb_sig") );
			  
		    System.out.println(PHOTOS_CREATE_ALBUM.toString());
		    
		    BrowserLauncher browserLauncher = new BrowserLauncher(null);
		    browserLauncher.openURLinBrowser("http://s.kuaiche.com");
		    
		    //assert false;
		  }
}
