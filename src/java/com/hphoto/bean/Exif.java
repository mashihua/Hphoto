package com.hphoto.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.hadoop.io.Writable;

import com.drew.metadata.Directory;

public class Exif implements Writable{
	
	private Directory director = null;
	
	public Exif(){}
	
	
	public Exif(Directory director){		
		this.director = director;
	}
	
	//inherit javadoc
	public void readFields(DataInput in) throws IOException {		
		int length = in.readInt();
		byte [] bytes = new byte[length];
		in.readFully(bytes);
		ByteArrayInputStream bi = new  ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bi);
		try {
			director = (Directory) ois.readObject();
		} catch (ClassNotFoundException e) {
			throw new IOException(e.getMessage());
		}finally{
			bi.close();
			ois.close();
		}
	}
	
	//inherit javadoc
	public void write(DataOutput out) throws IOException {
		ByteArrayOutputStream bot = new ByteArrayOutputStream();
		ObjectOutputStream os = new ObjectOutputStream(bot);
		os.writeObject(director);
		os.flush();
		out.writeInt(bot.size());
		out.write(bot.toByteArray());
		os.close();		
	}
	
	public Directory getDirector() {
		return director;
	}
	
	public void setDirector(Directory director) {
		this.director = director;
	}

}
