package com.hphoto.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import com.hphoto.bean.Category;
import com.hphoto.bean.UserProfile;
import com.hphoto.util.JSONUtil;

import junit.framework.TestCase;



public class TestJsonOutput extends TestCase{
	
	Iterator it;
    JSONArray a; 
    JSONObject j;
    
    public static void testJso() throws JSONException{
    	JSONStringer js = new JSONStringer();
    	for (int i =0 ; i < 5;i++){
    		js.array().value(i).endArray();    		
    	}
    	
    }
    /*
	public void testJson() throws JSONException{
		
		 Category[] ups = new Category[3];
		 //String [] name = {"firstname","lastname","mail","imgurl","mailpublic","name"};
		 for(int i = 0 ; i < ups.length ; i++){
			 ups[i] = new Category();
			 ups[i].setCount(i);
			 ups[i].setCreatdate(new Date());
			 ups[i].setDescription("description " + i);
			 ups[i].setLableName("lable" + i);
			 ups[i].setLastupload(new Date());
			 ups[i].setName("name"+1);
			 ups[i].setOpened(i%2==0?true:false);
			 ups[i].setImgurl("/u/image?u=josh"+i);
			 
		 }
		//System.out.println(JSONUtil.write(new JSONStringer(),ups[1]).toString());
		 System.out.println(JSONUtil.write(ups,false).toString());
		
	}
	*/
}
