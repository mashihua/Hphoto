package com.hphoto.bean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectory;
import com.hphoto.bean.Exif;

import junit.framework.TestCase;

public class TestExif extends TestCase{
	
	public void testExif() throws Exception{
		File withExif = new File("src/test/com/flashget/bean/withExifAndIptc.jpg");

		Metadata metadata = JpegMetadataReader.readMetadata(withExif);
		Directory directory = metadata.getDirectory(ExifDirectory.class);
	    Iterator tags = directory.getTagIterator();
	    while (tags.hasNext()) {
	            Tag tag = (Tag)tags.next();
	            System.out.println(tag.getTagName() +"\t"+ tag.getTagType() +"\t"+ tag.getDescription());
	    }
	        
	    assertEquals("0", directory.getString(ExifDirectory.TAG_ISO_EQUIVALENT));
	    
	    Exif exif = new Exif(directory);
	    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(bytes);
		exif.write(out);
		
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes.toByteArray()));
		exif.readFields(in);
		
		Directory anther = exif.getDirector();
		tags = anther.getTagIterator();
	    while (tags.hasNext()) {
	            Tag tag = (Tag)tags.next();
	            System.out.println(tag.getTagName() +"\t"+ tag.getTagType() +"\t"+ tag.getDescription());
	    }
	    assertEquals("0", anther.getString(ExifDirectory.TAG_ISO_EQUIVALENT));
	}
	
}
