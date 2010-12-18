package com.hphoto.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.List;

import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class ImageWriteable implements WritableComparable{
	
	private String name;
	private byte[] bytes;
	private String type;
	public ImageWriteable(){
		super();
	}
	
	public ImageWriteable(String name,String type,byte[] bytes){
		this.name = name;
		this.type = type;
		this.bytes = bytes;
	}
	
	public byte[] getBytes() {
		    if (this.bytes == null) {
		      throw new IllegalStateException("Uninitialiized. Null constructor " +
		        "called w/o accompaying readFields invocation");
		    }
		    return this.bytes;
	}
	
	public String getName(){		
		if (this.name == null) {
		      throw new IllegalStateException("Uninitialiized. Null constructor " +
		        "called w/o accompaying readFields invocation");
		 }
		 return this.name;
		
	}
	public String getType(){
		
		if (this.type == null) {
		      throw new IllegalStateException("Uninitialiized. Null constructor " +
		        "called w/o accompaying readFields invocation");
		 }
		 return this.type;
	}
	 /**
	  * Get the current size of the buffer.
	 */
	 public int getSize() {
	    if (this.bytes == null) {
	      throw new IllegalStateException("Uninitialiized. Null constructor " +
	        "called w/o accompaying readFields invocation");
	    }
	    return this.bytes.length;
	 }

	  
	public void readFields(DataInput in) throws IOException {
		this.name = in.readUTF();
		this.type = in.readUTF();
		this.bytes = new byte[in.readInt()];
	    in.readFully(this.bytes, 0, this.bytes.length);
		
	}

	public void write(DataOutput out) throws IOException {
		out.writeUTF(name);
		out.writeUTF(type);
		out.writeInt(this.bytes.length);
		out.write(this.bytes, 0, this.bytes.length);
	}
	
	  
	  // Below methods copied from BytesWritable
	  
	  public int hashCode() {
	    return WritableComparator.hashBytes(bytes, this.bytes.length);
	  }
	  
	  /**
	   * Define the sort order of the BytesWritable.
	   * @param right_obj The other bytes writable
	   * @return Positive if left is bigger than right, 0 if they are equal, and
	   *         negative if left is smaller than right.
	   */
	  public int compareTo(Object right_obj) {
	    return compareTo(((ImageWriteable)right_obj).getBytes());
	  }
	  
	  public int compareTo(final byte [] that) {
	    int diff = this.bytes.length - that.length;
	    return (diff != 0)?
	      diff:
	      WritableComparator.compareBytes(this.bytes, 0, this.bytes.length, that,
	        0, that.length);
	  }
	  
	  /**
	   * Are the two byte sequences equal?
	   */
	  public boolean equals(Object right_obj) {
	    if (right_obj instanceof ImageWriteable) {
	      return compareTo(right_obj) == 0;
	    }
	    return false;
	  }
	  
	  /**
	   * Generate the stream of bytes as hex pairs separated by ' '.
	   */
	  public String toString() { 
	    StringBuffer sb = new StringBuffer(3*this.bytes.length);
	    for (int idx = 0; idx < this.bytes.length; idx++) {
	      // if not the first, put a blank separator in
	      if (idx != 0) {
	        sb.append(' ');
	      }
	      String num = Integer.toHexString(bytes[idx]);
	      // if it is only one digit, add a leading 0.
	      if (num.length() < 2) {
	        sb.append('0');
	      }
	      sb.append(num);
	    }
	    return sb.toString();
	  }

	  /** A Comparator optimized for ImmutableBytesWritable.
	   */ 
	  public static class Comparator extends WritableComparator {
	    private BytesWritable.Comparator comparator =
	      new BytesWritable.Comparator();
	    
	    public Comparator() {
	      super(ImageWriteable.class);
	    }
	    
	    /**
	     * Compare the buffers in serialized form.
	     */
	    public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
	      return comparator.compare(b1, s1, l1, b2, s2, l2);
	    }
	  }
	  
	  static { // register this comparator
	    WritableComparator.define(ImageWriteable.class, new Comparator());
	  }
	  
	  /**
	   * @param array List of byte [].
	   * @return Array of byte [].
	   */
	  public static byte [][] toArray(final List<byte []> array) {
	    // List#toArray doesn't work on lists of byte [].
	    byte[][] results = new byte[array.size()][];
	    for (int i = 0; i < array.size(); i++) {
	      results[i] = array.get(i);
	    }
	    return results;
	  }
}
