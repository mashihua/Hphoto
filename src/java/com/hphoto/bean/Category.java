package com.hphoto.bean;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

import org.apache.hadoop.io.WritableComparable;

import com.hphoto.FConstants;

public class Category implements WritableComparable{
	
	public static long version = FConstants.VERSION;
	
	//authenticate key,if not opend
	private String authkey;
	//category name
	private String name;
	//fixed category name,useful for url
	private String lablename;
	//category description
	private String description;
	//where the category screened
	private String location;
	//category imgage
	private String imgurl;
	//the category is publiced
	private boolean opened;
	//category create date
	private Date creatdate;
	//category last upload;
	private Date lastupload;
	//category sort id
	private int sort;
	//the image sum
	private int count;
	//is setAlbumPhoto
	private boolean setAlbumPhoto;
	//this album used space
	private long usedSpace;
	//this ablum owner
	private String owner;
	
	//category sort type
	//0			sort by create date
	//1			sort by last upload
	//2			sort by sort id
	private int sorttype;

	
	public Category(){
		//default;
		this.sorttype = 0;
		this.usedSpace = 0L;
		this.setAlbumPhoto = false;
		this.lastupload = new Date();
		this.creatdate = new Date();
	}
	
	//inherit javadoc
	public void readFields(DataInput in) throws IOException {
		version = in.readLong();
		creatdate = new Date(in.readLong());
		lastupload = new Date(in.readLong());
		opened = in.readBoolean();
		setAlbumPhoto = in.readBoolean();
		sort = in.readInt();
		sorttype = in.readInt();
		name = in.readUTF();
		lablename = in.readUTF();
		description = in.readUTF();
		location = in.readUTF();
		String key = in.readUTF();
		authkey = key.equals("") ? null : key ;		
		imgurl = in.readUTF();
		count = in.readInt();
		usedSpace = in.readLong();
		owner = in.readUTF();
	}
	
	
	//inherit javadoc
	public void write(DataOutput out) throws IOException {
		out.writeLong(version);
		out.writeLong(creatdate.getTime());
		out.writeLong(lastupload.getTime());
		out.writeBoolean(opened);
		out.writeBoolean(setAlbumPhoto);
		out.writeInt(sort);
		out.writeInt(sorttype);
		out.writeUTF(name);
		out.writeUTF(lablename);
		out.writeUTF(description);
		out.writeUTF(location);
		out.writeUTF(authkey != null ?  authkey: "");
		out.writeUTF(imgurl);
		out.writeInt(count);
		out.writeLong(usedSpace);
		out.writeUTF(owner);
		
	}
	
	 //////////////////////////////////////////////////////////////////////////////
	 // Comparable
	 //////////////////////////////////////////////////////////////////////////////

	 /* (non-Javadoc)
	  * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	
	public int compareTo(Object o) {
		Category that = (Category)o;
		switch(sorttype){
			case 0:	return (this == that) ? 0 : this.creatdate.getTime() > that.creatdate.getTime() ? -1 : 1;
			case 1:	return (this == that) ? 0 : this.lastupload.getTime() > that.lastupload.getTime() ? -1 : 1;
			case 2: return (this == that) ? 0 : this.sort > that.sort ? -1 : 1;
			default: return (this == that) ? 0 : this.creatdate.getTime() > that.creatdate.getTime() ? -1 : 1;
		}
	}
	

	 @Override
	public String toString(){
		return "{name:"+name
		+",lableName:" + lablename 
		+",authkey:" + authkey
		+",imgurl:"+imgurl
		+",location:"+location
		+",creatdate:" + creatdate
		+",lastupload:" + lastupload
		+",opened:"+opened
		+",count:"+count
		+",sortType:"+sorttype
		+",sort:"+sort
		+",description:"+description+
		"}";		
	}
	 
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Category)
			return compareTo(obj) == 0;
		return false;
	}
	  
	@Override
	public int hashCode() {
	    int result = this.name.hashCode();
	     result ^= this.lablename.hashCode(); 
	     result ^= this.authkey.hashCode();
	     result ^= this.imgurl.hashCode();
	     result ^= this.creatdate.hashCode();
	     result ^= this.lastupload.hashCode();
	     result ^= this.description.hashCode();
	     result ^= Long.valueOf(this.sort).hashCode();
	    return result;
	} 
	
	public String getAuthkey() {
		return authkey;
	}

	public void setAuthkey(String authkey) {
		this.authkey = authkey;
	}

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isOpened() {
		return opened;
	}

	public void setOpened(boolean opened) {
		this.opened = opened;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getCreatdate() {
		return creatdate;
	}

	public void setCreatdate(Date creatdate) {
		this.creatdate = creatdate;
	}

	public Date getLastupload() {
		return lastupload;
	}

	public void setLastupload(Date lastupload) {
		this.lastupload = lastupload;
	}

	public String getLablename() {
		return lablename;
	}

	public void setLableName(String lablename) {
		this.lablename = lablename;
	}


	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public int getSortType() {
		return sorttype;
	}

	public void setSortType(int sortType) {
		this.sorttype = sortType;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isSetAlbumPhoto() {
		return setAlbumPhoto;
	}

	public void setSetAlbumPhoto(boolean setAlbumPhoto) {
		this.setAlbumPhoto = setAlbumPhoto;
	}

	public long getUsedSpace() {
		return usedSpace;
	}

	public void setUsedSpace(long usedSpace) {
		this.usedSpace = usedSpace;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	
}
