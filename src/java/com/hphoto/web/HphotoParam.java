package com.hphoto.web;

import java.util.HashMap;
import java.util.Map;



public enum HphotoParam implements CharSequence{
	  USER("user"),
	  ALBUM("album"),
	  IMAGE_ID("id"),
	  IMAGE_CORP("corp","c"),
	  IMAGE_QUALITY("high","h"),
	  IMAGE_MAX("max"),
	  SORT("sort"),
	  AUTH_KEY("authKey")
	  ;
	  
	  private static Map<String, HphotoParam> _lookupTable =
		    new HashMap<String, HphotoParam>(HphotoParam.values().length);
		  static {
		    for (HphotoParam param: HphotoParam.values()) {
		      _lookupTable.put(param.toString(), param);
		    }
	 }
	 
		  
	/**
	 * Retrieves the FacebookParam corresponding to the supplied String key.
	 * @param key a possible FacebookParam
	 * @return the matching FacebookParam or null if there's no match
	*/
	 public static HphotoParam get(String key) {
		    return _lookupTable.get(key);
	 }
	 
	  private String paramName;
	  private String seemVale;
	  HphotoParam(String name) {
		  this(name,null);
	  }
	  
	  HphotoParam(String name,String seemValue) {
		  this.paramName = name;
		  this.seemVale = seemValue;
	  }
	  

	  /* Implementing CharSequence */
	  public char charAt(int index) {
	    return this.paramName.charAt(index);
	  }

	  public int length() {
	    return this.paramName.length();
	  }

	  public CharSequence subSequence(int start, int end) {
	    return this.paramName.subSequence(start, end);
	  }
	  
	  public boolean equals(String value){
		  return this.seemVale != null && this.seemVale.equals(value);		  
	  }
	  
	  public String toString() {
	    return this.paramName;
	  }
}
