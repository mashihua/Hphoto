package com.hphoto.bean;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;

import com.hphoto.FConstants;


public class UserProfile implements Writable{
	
	public static long version = FConstants.VERSION;
	
	private String lastname;
	private String firstname;
	private String nicename;
	private boolean mailpublic;
	private String mail;
	private String imgurl;
	private String password;
	private long avlidSpace;
	private long uesdeSpace;
	private boolean imageSetted;
	public UserProfile(){
		this.imageSetted = false;
		this.uesdeSpace = 0L;
		this.avlidSpace = 1024L * 1024L * 1024L;
	}
	public String getFirstname() {
		return firstname;
	}
	
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public boolean isMailpublic() {
		return mailpublic;
	}
	
	public String getNicename() {
		return nicename;
	}

	public void setNicename(String nicename) {
		this.nicename = nicename;
	}
	
	public void setMailpublic(boolean mailpublic) {
		this.mailpublic = mailpublic;
	}
	

	public String getImgurl() {
		return imgurl;
	}

	
	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getAvlidSpace() {
		return avlidSpace;
	}

	public void setAvlidSpace(long avlidSpace) {
		this.avlidSpace = avlidSpace;
	}

	public long getUesdeSpace() {
		return uesdeSpace;
	}

	public void setUesdeSpace(long uesdeSpace) {
		this.uesdeSpace = uesdeSpace;
	}
	public boolean isImageSetted() {
		return imageSetted;
	}
	public void setImageSetted(boolean imageSetted) {
		this.imageSetted = imageSetted;
	}
	
	@Override
	public String toString(){
		return "{firstname:"+ firstname+ ",lastname:"+lastname+",nicename:"+nicename+",mail:"+mail+",mailpublic:"+mailpublic+",imgurl:"+imgurl+"}";
	}
	
	@Override
	public int hashCode(){
		int result = firstname.hashCode();		
		return result;
	}
	
	@Override	
	public boolean equals(Object o){
		if(o instanceof UserProfile)
			return ((UserProfile) o).mail == this.mail;
		return false;
	}
	
	//inherit javadoc
	public void write(DataOutput out) throws IOException {
		out.writeLong(version);
		out.writeUTF(firstname);
		out.writeUTF(lastname);
		out.writeUTF(nicename);
		out.writeUTF(mail);
		out.writeUTF(imgurl);
		out.writeUTF(password);
		out.writeBoolean(mailpublic);
		out.writeLong(avlidSpace);
		out.writeLong(uesdeSpace);
		out.writeBoolean(imageSetted);
	}
	//inherit javadoc
	public void readFields(DataInput in) throws IOException {
		version = in.readLong();
		firstname = in.readUTF();
		lastname = in.readUTF();
		nicename = in.readUTF();
		mail = in.readUTF();
		imgurl = in.readUTF();
		password = in.readUTF();
		mailpublic = in.readBoolean();
		avlidSpace = in.readLong();
		uesdeSpace = in.readLong();
		imageSetted = in.readBoolean();
	}


	public int compareTo(Object o) {
		UserProfile other = (UserProfile) o;
		return this.mail.compareTo(other.mail) & this.nicename.compareTo(other.nicename);
	}



	
}
