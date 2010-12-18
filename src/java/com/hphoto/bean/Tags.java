package com.hphoto.bean;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class Tags implements WritableComparable{

	
	private String tag;
	private int sort;
	private int width;
	
	public Tags(){}
	
	//inherit javadoc
	public void readFields(DataInput in) throws IOException {
		tag = in.readUTF();
		sort = in.readInt();
		width = in.readInt();
	}
	
	//inherit javadoc
	public void write(DataOutput out) throws IOException {
		out.writeUTF(tag);
		out.writeInt(sort);
		out.writeInt(width);
	}
	
	//////////////////////////////////////////////////////////////////////////////
	// Comparable
	//////////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	* @see java.lang.Comparable#compareTo(java.lang.Object)
	*/
	public int compareTo(Object o) {
		Tags other = (Tags)o;
		return (this.tag.equals(other.tag)) ? 0 : this.sort > other.sort ? -1 : 1;
	}
	
	@Override	
	public String toString(){
		return "{"
		+"tag:"+tag
		+",width:"+width
		+",sort:"+ sort
		+"}";
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof Tags)
			return compareTo(o) == 0;
		return false;
	}
	
	@Override
	public int hashCode(){
		int result = tag.hashCode();
		result ^= sort;
		result ^= width;
		return result;
	}
	
	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
}
