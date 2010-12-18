package com.hphoto.bean;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

import org.apache.hadoop.io.WritableComparable;

import com.hphoto.FConstants;

public class Image implements WritableComparable{
	
	public static long version = 1;
	
	//who possess the image 
	private String owner;
	//the image id
	private String id;
	//category lable
	private String category;
	//store the file name
	private String fileName;
	//the image caption
	private String caption;
	//the image timestamp
	private Date timestamp;
	//allow download this image
	private boolean allowdownload;
	//allow print this image
	private boolean allowprint;
	//allow comments
	private boolean allowcomments;
	//how kbytes
	private int kbytes;		
	//the image description
	private String description;
	//the image src url
	private String imgsrc;
	//the image width
	private int width;
	//the image height	
	private int height;
	//the image type
	private String type;
	//sort id
	private int sort;
	//image sort type,default 0;
	//0			sort by timestamp
	//1			sort by bytes
	//2			sort by sortk	
	private int sorttype;
	
//	the exif
	private Exif exif;
	//the tag
	private Tags[] tags;
	//the comment
	private Comment[] comments;
	//the album
	private Album[] album;
	
	
	
	
	public Image(){
		//default
		this.allowdownload = true;
		this.allowcomments = true;
		this.allowprint = true;
	}

	
	//////////////////////////////////////////////////////////////////////////////
	// Comparable
	//////////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	*/
	public int compareTo(Object o) {
		Image that = (Image)o;
		switch(sorttype){
			case 0:return (this == that) ? 0 : (this.timestamp.getTime() > that.timestamp.getTime() ? -1 : 1 );
			case 1:return (this == that) ? 0 : (this.kbytes > that.kbytes ? -1 : 1 ); 
			case 2:return (this == that) ? 0 : (this.sort > that.sort ? -1 : 1 );
			default:return (this == that) ? 0 :(this.timestamp.getTime() > that.timestamp.getTime() ? -1 : 1 );
		}
	}
	
	@Override
	public String toString(){
		return "{id:"+this.id+
		",category:"+this.category+
		",filename:"+this.fileName+
		",caption:"+this.caption+		
		",owner:"+this.owner+
		",timestamp:"+this.timestamp+
		",kybte:"+this.kbytes+
		",imgurl:"+this.imgsrc+
		",type:" + this.type+
		",allowdownload:"+this.allowdownload+
		",allowprint:"+this.allowprint+
		",allowcomments+"+this.allowcomments+
		",width:"+this.width+
		",height:"+this.height+
		",sortType:"+this.sorttype+
		",sort:"+this.sort+
		",description:"+this.description+
		"}"; 
	}
	 
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Image)
			return compareTo(obj) == 0;
		return false;
	}
	
	@Override
	public int hashCode() {
		int result = this.id.hashCode();
		result ^= this.category.hashCode();
		result ^= this.caption.hashCode();
		result ^= this.fileName.hashCode();
		result ^= this.imgsrc.hashCode();
		return result;
	}
	
	
	public Exif getExif() {
		return exif;
	}

	public void setExif(Exif exif) {
		this.exif = exif;
	}
	
	public Album[] getAlbum() {
		return album;
	}

	public void setAlbum(Album[] album) {
		this.album = album;
	}
	
	
	public Comment[] getComments() {
		return comments;
	}

	public void setComments(Comment[] comments) {
		this.comments = comments;
	}
	
	
	public Tags[] getTags() {
		return tags;
	}

	public void setTags(Tags[] tags) {
		this.tags = tags;
	}

	
	public int getSorttype() {
		return sorttype;
	}

	public void setSorttype(int sorttype) {
		this.sorttype = sorttype;
	}

	public boolean isAllowcomments() {
		return allowcomments;
	}

	public void setAllowcomments(boolean allowcomments) {
		this.allowcomments = allowcomments;
	}

	public boolean isAllowdownload() {
		return allowdownload;
	}

	public void setAllowdownload(boolean allowdownload) {
		this.allowdownload = allowdownload;
	}

	public boolean isAllowprint() {
		return allowprint;
	}

	public void setAllowprint(boolean allowprint) {
		this.allowprint = allowprint;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImgsrc() {
		return imgsrc;
	}

	public void setImgsrc(String imgsrc) {
		this.imgsrc = imgsrc;
	}

	public int getKbytes() {
		return kbytes;
	}

	public void setKbytes(int kbytes) {
		this.kbytes = kbytes;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}


	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	//inherit javadoc	
	public void readFields(DataInput in) throws IOException {
		version = in.readLong();
		owner = in.readUTF();
		id = in.readUTF();
		String cap = in.readUTF();
		caption = cap.equals("") ? null : cap;
		category = in.readUTF();
		type = in.readUTF();
		fileName = in.readUTF();
		long time = in.readLong();
		timestamp = new Date(time);
		allowdownload = in.readBoolean();
		allowprint = in.readBoolean();
		allowcomments = in.readBoolean();
		kbytes = in.readInt();
		String des = in.readUTF();
		description = des.equals("")?null:des;
		imgsrc = in.readUTF();
		width = in.readInt();
		height = in.readInt();
		sort = in.readInt();
		sorttype = in.readInt();
	}
	
	
	//inherit javadoc
	public void write(DataOutput out) throws IOException {
		out.writeLong(version);
		out.writeUTF(owner);
		out.writeUTF(id);
		out.writeUTF(caption==null?"":caption);
		out.writeUTF(category);
		out.writeUTF(type==null?FConstants.UNKNOW_TYPE:type);
		out.writeUTF(fileName);
		out.writeLong(timestamp.getTime());
		out.writeBoolean(allowdownload);
		out.writeBoolean(allowprint);
		out.writeBoolean(allowcomments);
		out.writeInt(kbytes);
		out.writeUTF(description==null?"":description);
		out.writeUTF(imgsrc);
		out.writeInt(width);
		out.writeInt(height);
		out.writeInt(sort);
		out.writeInt(sorttype);
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	

}
