package com.hphoto.util;

import java.io.Writer;

import org.json.JSONException;
import org.json.JSONStringer;
import org.json.JSONWriter;

import com.hphoto.bean.Category;

public class JSONUtil {
	public JSONUtil(){}
	
	static public JSONWriter write(JSONWriter writer,Category category) throws JSONException{
			writer.object()
			.key("name").value(category.getName())
			.key("lname").value(category.getLablename())
			.key("count").value(category.getCount())
			.key("lastupload").value(category.getLastupload())
			.key("createdate").value(category.getCreatdate())
			.key("location").value(category.getLocation())
			.key("description").value(category.getDescription())
			.key("img").value(category.getImgurl())
			.endObject();
		return writer;
	} 
	
	static public JSONWriter write(Category[] categories,boolean showPublic) throws JSONException{
		JSONStringer js = new JSONStringer();
		for (Category category:categories){
			if(showPublic  && !category.isOpened()){
				continue;
			}
			js.array();
			//write(js,category);
			js.value(1);
			js.endArray();
		}
		return js;
	}
	
	static public JSONWriter write(){
		return null;
	}
}
