package com.hphoto.web;

public enum ApiField {
	USER("user"),
	ALBUM("album"),
	FEED("feed"),
	KIND("kind"),
	AUTHKEY("authKey"),
	LANGUAGE("lh"),
	API("api"),
	ALT("alt");
	  private String fieldName;
	
	  ApiField(String name) {
	    this.fieldName = name;
	  }
	
	  public String fieldName() {
	    return this.fieldName;
	  }
	
	  public String toString() {
	    return fieldName();
	  }
	  
	  /**
	   * Returns true if this field has a particular name.
	   */
	  public boolean isName(String name) {
	    return toString().equals(name);
	  }
}
