package com.hphoto.bean;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.Writable;

import com.hphoto.bean.Exif;
import com.hphoto.bean.Tags;


public class TestData implements Writable{
	
	private String[] name ;
	private Tags[] tags;
	private String caption;
	private Exif exif;
	public void readFields(DataInput in) throws IOException {
		caption = in.readUTF();
		int length = in.readInt();
		name = new String[length];
		for(int i = 0 ; i < name.length; i++){
			name[i] = in.readUTF();
		}
		exif = new Exif();
		exif.readFields(in);
		ArrayWritable aw = new ArrayWritable(Tags.class);
		//read tag
		//aw.setValueClass(Tags.class);
		aw.readFields(in);
		tags = (Tags[])aw.toArray();
	}
	public void write(DataOutput out) throws IOException {
		out.writeUTF(caption);
		out.writeInt(name.length);
		for(int i = 0 ; i < name.length; i++){
			out.writeUTF(name[i]);
		}
		exif.write(out);
		ArrayWritable aw = new  ArrayWritable(Tags.class);
		//write tags
		aw.set(tags);
		aw.write(out);
	}
	public String getCaption() {
		return caption;
	}
	public void setCaption(String caption) {
		this.caption = caption;
	}
	public String[] getName() {
		return name;
	}
	public void setName(String[] name) {
		this.name = name;
	}
	public Tags[] getTags() {
		return tags;
	}
	public void setTags(Tags[] tags) {
		this.tags = tags;
	}
	public Exif getExif() {
		return exif;
	}
	public void setExif(Exif exif) {
		this.exif = exif;
	}
}
