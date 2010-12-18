package com.hphoto.server;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.HBaseAdmin;
import org.apache.hadoop.hbase.HBaseClusterTestCase;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HScannerInterface;
import org.apache.hadoop.hbase.HStoreKey;
import org.apache.hadoop.hbase.HTable;
import org.apache.hadoop.hbase.filter.PageRowFilter;
import org.apache.hadoop.hbase.filter.RegExpRowFilter;
import org.apache.hadoop.hbase.filter.RowFilterInterface;
import org.apache.hadoop.hbase.filter.RowFilterSet;
import org.apache.hadoop.hbase.filter.StopRowFilter;
import org.apache.hadoop.hbase.filter.WhileMatchRowFilter;
import org.apache.hadoop.hbase.util.Writables;
import org.apache.hadoop.io.Text;


import com.hphoto.bean.UserProfile;
import com.hphoto.server.InitTable;

public class TestHbase extends HBaseClusterTestCase{
	  private Log LOG = LogFactory.getLog(this.getClass().getName());
	  private static final Text USERS = new Text("user:");
	  private String[] value={"josh","flashget","flash"};
	  private String change = "change";
	  HTable table;
	  HBaseAdmin admin;
	  public void setUp() throws Exception {
	    super.setUp();	    	   
	    HBaseConfiguration conf = new HBaseConfiguration();	  
	    InitTable.init(new HBaseAdmin(conf));
	    table = new HTable(conf,new Text("user--info"));
	  }

	  
	  public void testSetBean() throws InvocationTargetException, NoSuchMethodException, Exception{
		  for(int i = 0 ; i < value.length; i++){
			  UserProfile up = new UserProfile();
			  up.setFirstname(value[i]);
			  up.setMail(value[i]+"@gmail.com");
			  up.setImgurl("/image/src/"+value[i]);
			  up.setPassword(value[i]);
			  up.setImageSetted(false);
			  up.setMailpublic(true);
			  up.setNicename(value[i]);
			  up.setLastname(value[1] + " ");
			  long id = table.startUpdate(new Text(value[i])); 
			  table.put(id, USERS, Writables.getBytes(up));
			  table.commit(id,System.currentTimeMillis());
		  }	
		  		 
		  getUesr();
		  scanner();
	  }
	  
	  public void getUesr() throws IOException{
		  UserProfile user = getUser(value[0]);
		  System.out.println("get user "+ value[0] +",user nicename is " + user.getNicename());
		  
		  //set the nicename is change,
		  user.setNicename(change);
		  long id = table.startUpdate(new Text(value[0])); 
		  table.put(id, USERS, Writables.getBytes(user));
		  table.commit(id,System.currentTimeMillis());	  
		  user = getUser(value[0]);
		  System.out.println("get user "+ change +",user nicename is " + user.getNicename());
		 
		  //set the nicename is flashget and put timestamp to ahead of all
		  //so that get user's nicename is change
		  user.setNicename(value[1]);
		  id = table.startUpdate(new Text(value[0])); 
		  table.put(id, USERS, Writables.getBytes(user));
		  table.commit(id,System.currentTimeMillis() - 9000000L);
		  user = getUser(value[0]);
		  System.out.println("set user "+ value[1] +",but get user is " + user.getNicename());
		  
		  //get all version form table that user is josh
		  UserProfile[] users = getUser(value[0],10);
		  System.out.println("get users at 10 numVersions,this length is "+users.length);
		  int i = 0;
		  for(UserProfile u:users){			  
			  System.out.println("get user at users item " + i + ",the user nicename is " +u.getNicename());
			  i++;
		  }
		 
		  //the default value is save 3 version in the table,
		  //add a new version to table,what happened?
		  //we get 4 version,because at this time HRegion doesn't call compactStores() method;
		  user.setNicename("version 4");
		  id = table.startUpdate(new Text(value[0])); 
		  table.put(id, USERS, Writables.getBytes(user));
		  table.commit(id,System.currentTimeMillis());
		  
		  users = getUser(value[0],10);
		  System.out.println("after add new version, we get users at 10 numVersions,this length is "+users.length);
		  i = 0;
		  for(UserProfile u:users){			  
			  System.out.println("get user at users item " + i + ",the user nicename is " +u.getNicename());
			  i++;
		  }
		  
		  /*
		  //affter delete the user,what happened?
		  id = table.startUpdate(new Text(value[0])); 
		  table.delete(id, USERS);
		  table.commit(id);
		  user = getUser(value[0]);
		  
		  if(user == null)
			  System.out.println("after delete the column,we get user is null");
		  else
			  System.out.println("after delete the column,we get user is " + user.getNicename());	
		  
		  users = getUser(value[0],10);
		  System.out.println("after delete the column, get users at 10 numVersions,this length is "+users.length);
		  i = 0;
		  for(UserProfile u:users){			  
			  System.out.println("get user at users item " + i + ",the user nicename is " +u.getNicename());
			  i++;
		  }		  		 		 
		  */
		  
		  //no user is name fls,so we get null;
		  user = getUser("fls");
		  System.out.println("no row is fls :" + (user == null));
		  user = null;
	  }
	  
	  public void scanner() throws IOException{
		  System.out.println();
		  System.out.println();
		  System.out.println();
		  System.out.println("Start PageRowFilter(1)");
		  //only return one row.
		  RowFilterInterface filter = new PageRowFilter(1);
		  HScannerInterface s = table.obtainScanner(new Text[]{USERS}, new Text("jo"), System.currentTimeMillis(),filter);
		  printScanner(s);
		  
		  
		  Set<RowFilterInterface> filters = new HashSet<RowFilterInterface>();
		  filters.add(new PageRowFilter(1));
		  //the StopRowFilter is useful for alphabetical filtering.
		  filters.add(new StopRowFilter(new Text(value[0])));
	      filter = new RowFilterSet(RowFilterSet.Operator.MUST_PASS_ALL, 
	    	      filters); 
	      System.out.println("Start RowFilterSet(),must no value to print");
	      s = table.obtainScanner(new Text[]{USERS}, new Text(value[0]), System.currentTimeMillis(),filter);
		  printScanner(s);
		  
		  //This type of functionality will be useful when filtering for a set of
		  //adjacent rows that match a certain regex (as opposed to every row that matches that regex).
	      filter = new WhileMatchRowFilter(new RegExpRowFilter(value[0]));
	      System.out.println("Start WhileMatchRowFilter(new RegExpRowFilter()),must print a user");
	      s = table.obtainScanner(new Text[]{USERS}, new Text(value[0]), System.currentTimeMillis(),filter);
		  printScanner(s);
		  
		  filter = new WhileMatchRowFilter(new RegExpRowFilter("jos"));
	      System.out.println("Start WhileMatchRowFilter(new RegExpRowFilter()),no value to print");
	      s = table.obtainScanner(new Text[]{USERS}, new Text(value[0]), System.currentTimeMillis(),filter);
		  printScanner(s);
		  
	  }
	  
	  public void printScanner(HScannerInterface s) throws IOException{
		  HStoreKey curKey = new HStoreKey();
	      TreeMap<Text, byte[]> curVals = new TreeMap<Text, byte[]>();
	      boolean next = s.next(curKey, curVals);
	      while(next) {  
	    	 System.out.println(curKey.getRow() + "\t" + curKey.getColumn() + "\t" + curKey.getTimestamp()); 
	    	 for(Iterator<Text> it = curVals.keySet().iterator(); it.hasNext(); ) {	
	    		 UserProfile user = (UserProfile)Writables.getWritable(curVals.get(it.next()),new UserProfile());
	    		 System.out.println("get user :"+user.getNicename());
	    	 }
	    	 curVals.clear();
	    	 next = s.next(curKey, curVals);
	      }
	  }
	  
	  
	  public UserProfile getUser(String row){
		  byte[] bytes;
		  try {
			  bytes = table.get(new Text(row), USERS);
		  } catch (IOException e1) {
			return null;
		  }
		  if(bytes == null){
			  return null;
		  }
		  try {
			return (UserProfile)Writables.getWritable(bytes,new UserProfile());
			} catch (IOException e) {
				return null;
		}
	  }
	  
	  public UserProfile[] getUser(String row,int num){
		  byte[][] bytes;
		  try {
			  bytes = table.get(new Text(row), USERS , num);
		  } catch (IOException e1) {
			return null;
		  }
		  UserProfile[] users = new UserProfile[bytes.length];
		  int i = 0;
		  for(byte[] b : bytes){
			UserProfile user;
			try {
				user = (UserProfile)Writables.getWritable(b,new UserProfile());
				users[i] = user;
			} catch (IOException e) {
				users[i] = null;
			}
			 i++;
		  }
		  return users;
	  }
	  
	  
}
