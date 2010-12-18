package com.hphoto.bean;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

import org.apache.hadoop.io.WritableComparable;

public class Comment implements WritableComparable{
	
	//who was comment
	private String owner;
	//the nice name to commenter
	private String commenter;
	//the use image url
	private String userimage;
	//the use link
	private String link;
	//the comment info
	private String comment;
	//timestamp;
	private Date timestamp;
	
	public Comment(){}
	
	//inherit javadoc
	public void readFields(DataInput in) throws IOException {
		timestamp = new Date(in.readLong());
		owner = in.readUTF();
		commenter = in.readUTF();
		userimage = in.readUTF();
		link = in.readUTF();
		comment = in.readUTF();
	}
	//inherit javadoc
	public void write(DataOutput out) throws IOException {
		out.writeLong(timestamp.getTime());
		out.writeUTF(owner);
		out.writeUTF(commenter);
		out.writeUTF(userimage);
		out.writeUTF(link);
		out.writeUTF(comment);
	}
	
	
	
	//////////////////////////////////////////////////////////////////////////////
	// Comparable
	//////////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		Comment that = (Comment)o;
		return (this.comment == that.comment && 
				this.owner == that.owner &&
				this.commenter == that.comment
				&& this.timestamp.getTime() == that.timestamp.getTime()) ? 0 : this.timestamp.getTime() > that.timestamp.getTime() ? -1 : 1;
	}
	
	
	@Override
	public String toString(){
		return "{owner:"+owner+",commenter:" + commenter + ",userimage:" + userimage + ",link:" +link +",timestamp:"+timestamp+",comment:"+comment+"}";
		
	}
	 
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Comment)
			return compareTo(obj) == 0;
		return false;
	}
	  
	@Override
	public int hashCode() {
	    int result = this.owner.hashCode();
	    result ^= this.commenter.hashCode(); 
	    result ^= this.comment.hashCode();
	    result ^= this.link.hashCode();
	    result ^= this.userimage.hashCode();
	    result ^= this.timestamp.hashCode();
	    return result;
	} 
	
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getCommenter() {
		return commenter;
	}

	public void setCommenter(String commenter) {
		this.commenter = commenter;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getUserimage() {
		return userimage;
	}

	public void setUserimage(String userimage) {
		this.userimage = userimage;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}



}
