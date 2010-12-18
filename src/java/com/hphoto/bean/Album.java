package com.hphoto.bean;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class Album implements WritableComparable{
	
	private String name;
	private String caption;
	private String url;
	private int sort;
	
	public Album(){}
	
	 //////////////////////////////////////////////////////////////////////////////
	 // Writable
	 //////////////////////////////////////////////////////////////////////////////
	
	  
	 /* (non-Javadoc)
	  * @see org.apache.hadoop.io.Writable#readFields(java.io.DataInput)
	 */

	public void readFields(DataInput in) throws IOException {
		name = in.readUTF();
		caption = in.readUTF();
		url = in.readUTF();
		sort = in.readInt();
	}

	/* (non-Javadoc)
	 * @see org.apache.hadoop.io.Writable#write(java.io.DataOutput)
	 */

	
	public void write(DataOutput out) throws IOException {
		out.writeUTF(name);
		out.writeUTF(caption);
		out.writeUTF(url);
		out.writeInt(sort);		
	}

	//////////////////////////////////////////////////////////////////////////////
	// Comparable
	//////////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	
	public int compareTo(Object o) {
		Album that = (Album)o;		
		return (that.name == this.name)? 0 : this.sort == that.sort ? -1 : 1;
	}

	@Override
	public String toString(){
		return "{name:"+name+" caption:" + caption + "url:" + url + "sort:" +sort +"}";
		
	}
	 
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Album)
			return compareTo(obj) == 0;
		return false;
	}
	  
	@Override
	public int hashCode() {
	    int result = this.name.hashCode();
	    result ^= this.caption.hashCode(); 
	    result ^= this.url.hashCode();
	    result ^= Long.valueOf(this.sort).hashCode();
	    return result;
	} 
	 
	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
