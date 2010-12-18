package com.hphoto.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.HBaseClusterTestCase;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.io.Text;


import com.hphoto.bean.BeanService;
import com.hphoto.bean.UserProfile;

public class UserServiceTest extends HBaseClusterTestCase{
	  private Log LOG = LogFactory.getLog(this.getClass().getName());
	  static String TABLE = "user_table";
	  
	  private UserProfile up;
	  private BeanService us;
	  
	  private String user = "babytree";
	  private String city = "beijing";
	  private static final Text USERS = new Text("user:");
	  
	  public void setUp() throws Exception {
	    super.setUp();	    	   
	    up = new UserProfile();
	 	Date d = new Date();
	 	/*
	    up.setBirthday(d);
	    up.setMailPublic(false);
	    up.setAge(25);
	    up.setCity("beijing");
	    up.setCountry("china");
	    up.setGender((byte)0x00);
	    up.setFirstName("josh");
	    up.setLastName("ma");
	    up.setState("beijing");
	    */
	    HBaseConfiguration conf = new HBaseConfiguration();	    
	    //HClient client = new HClient(conf);
	    //us = new BeanService(client,new Text(TABLE));
	  }

	  
	  public void testSetBean() throws InvocationTargetException, NoSuchMethodException, Exception{
		  
		  long id = us.setBean(new Text(TABLE),USERS, up);
		  us.update(id);
		  UserProfile o = (UserProfile) us.getBean(new Text(TABLE),USERS,UserProfile.class);
	  }
	  
}
