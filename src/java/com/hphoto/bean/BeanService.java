
package com.hphoto.bean;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.hadoop.hbase.HTable;
import org.apache.hadoop.hbase.HScannerInterface;
import org.apache.hadoop.hbase.HStoreKey;
import org.apache.hadoop.io.Text;

import com.hphoto.util.Convertion;

public class BeanService  {	
	private static Log LOG = LogFactory.getLog(BeanService.class.getClass().getName());
	private HTable client;
	private Text table;
	
	public BeanService(HTable client) throws IOException{
		this(client,null);
	}

	public BeanService(HTable client,Text table)throws IOException{
		this.client  =  client;		
		this.table = table;
		//if(this.table!=null)
			//this.client.openTable(,this.table);
	}
	
	
	public Object getBean(Text row,Text column,Class type) throws InvocationTargetException, NoSuchMethodException, Exception{		
		Text[] cols = new Text[] {
				column
		};
		int i = 0;
		HScannerInterface s = client.obtainScanner(cols,row);
		HStoreKey curKey = new HStoreKey();
	    TreeMap<Text, byte[]> curVals = new TreeMap<Text, byte[]>();	    
	    Object bean = Class.forName(type.getName()).newInstance();	    
	    BeanMap map = new BeanMap(bean);
	    while(s.next(curKey, curVals)) {
	    	System.out.println("get key:\t" + curKey.getRow() + ":"+ curKey.getColumn());
	    	for(Iterator<Text> it = curVals.keySet().iterator(); it.hasNext(); ) {
	    		 Text col = it.next();
	             byte val[] = curVals.get(col);
	             if(col.toString().startsWith(column.toString())){
	            	 String lable = col.toString().substring(column.toString().length());	            	 
	            	 //if we find the user lable that in UserProfile bean;	            	 
	            	 if(map.containsKey(lable)&&map.getWriteMethod(lable)!=null){
	            		 //convert the byte value;
	            		 Object value = Convertion.encode(PropertyUtils.getPropertyType(bean,lable),val);
	            		 map.put(lable, value);
	            		 if(LOG.isDebugEnabled()){
	            			 //LOG.debug()
	            			 LOG.info(i + ":\t"+ lable + "\t" + value);
	            		 }
	            	 }
	             }
	    	}
	    	i++;
	    }
	    s.close();
		return bean;
	}
	
	public long setBean(Text row,Text lable,Object bean) throws InvocationTargetException, NoSuchMethodException, Exception{	    		
		long lockid = client.startUpdate(row);
		BeanMap map = new BeanMap(bean);
	    Iterator iter = map.keyIterator();
	    while(iter.hasNext()){
	    	String name = iter.next().toString();	    	 
	    	if(map.getReadMethod(name)!=null && map.getWriteMethod(name) != null && map.get(name) != null){   		
	    		byte[] value =  Convertion.decode(PropertyUtils.getPropertyType(bean,name),
	    				map.get(name));	    		
	    		//put value to hbase;
	    		if(value != null)
	    			client.put(lockid,new Text(lable+name),value);
    			if(LOG.isDebugEnabled()){
	    			LOG.debug(new Text(lable+name) + 
		    				":\t" + Convertion.encode(PropertyUtils.getPropertyType(bean,name)
		    				,value)
		    		);
    			}
	    	
	    	}
	    }
	    return lockid;
	}
	
	
	public void update(long lockid) throws IOException{
		update(lockid,null);
	}
	
	
	public void update(long writeid,Long time) throws IOException{
		client.commit(writeid,time==null?System.currentTimeMillis():time);
	}

	
	public void about(long lockid) throws IOException{
		this.client.abort(lockid);
	}
	
	
	public Text getTable() {
		return table;
	}

	
	public void setTable(Text table) throws IOException {
		this.table = table;
		//this.client.(this.table);
	}
	

}
