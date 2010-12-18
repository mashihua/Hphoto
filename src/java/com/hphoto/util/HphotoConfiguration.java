package com.hphoto.util;

import java.util.Enumeration;

import javax.servlet.ServletContext;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

public class HphotoConfiguration {
	 private final static String KEY = HphotoConfiguration.class.getName();
	 

	  public static Configuration create() {
	    Configuration conf = new HBaseConfiguration();
	    return conf;
	  }
	  

	  public static Configuration get(ServletContext application) {
	    Configuration conf = (Configuration) application.getAttribute(KEY);
	    if (conf == null) {
	      conf = create();
	      Enumeration e = application.getInitParameterNames();
	      while (e.hasMoreElements()) {
	        String name = (String) e.nextElement();
	        conf.set(name, application.getInitParameter(name));
	      }
	      application.setAttribute(KEY, conf);
	    }
	    return conf;
	  }
}
